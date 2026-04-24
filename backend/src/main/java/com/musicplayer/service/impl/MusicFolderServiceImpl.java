package com.musicplayer.service.impl;

import com.musicplayer.entity.MusicFolder;
import com.musicplayer.entity.Song;
import com.musicplayer.mapper.MusicFolderMapper;
import com.musicplayer.mapper.SongMapper;
import com.musicplayer.service.MusicFolderService;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class MusicFolderServiceImpl implements MusicFolderService {

    @Autowired
    private MusicFolderMapper musicFolderMapper;

    @Autowired
    private SongMapper songMapper;

    @Value("${app.cover-path}")
    private String coverPath;

    private static final Pattern SUPPORTED_FORMATS = Pattern.compile("\\.(mp3|flac|wav|aac|ogg|m4a)$", Pattern.CASE_INSENSITIVE);

    @Override
    public List<MusicFolder> findAll() {
        return musicFolderMapper.selectAll();
    }

    @Override
    @Transactional
    public MusicFolder add(String folderPath) {
        MusicFolder existing = musicFolderMapper.selectByPath(folderPath);
        if (existing != null) {
            return existing;
        }
        
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("文件夹不存在或不是有效目录: " + folderPath);
        }
        
        MusicFolder musicFolder = new MusicFolder();
        musicFolder.setFolderPath(folderPath);
        musicFolderMapper.insert(musicFolder);
        return musicFolder;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MusicFolder folder = musicFolderMapper.selectById(id);
        if (folder != null) {
            musicFolderMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void scanAll() {
        List<MusicFolder> folders = musicFolderMapper.selectAll();
        for (MusicFolder folder : folders) {
            scanFolderInternal(folder.getFolderPath());
        }
    }

    @Override
    @Transactional
    public void scanFolder(Long folderId) {
        MusicFolder folder = musicFolderMapper.selectById(folderId);
        if (folder == null) {
            throw new RuntimeException("文件夹不存在");
        }
        scanFolderInternal(folder.getFolderPath());
    }

    private void scanFolderInternal(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        List<File> audioFiles = findAudioFiles(folder);
        
        for (File file : audioFiles) {
            try {
                processAudioFile(file);
            } catch (Exception e) {
                // 记录错误但继续处理其他文件
                System.err.println("处理文件失败: " + file.getAbsolutePath() + " - " + e.getMessage());
            }
        }
    }

    private List<File> findAudioFiles(File folder) {
        List<File> audioFiles = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    audioFiles.addAll(findAudioFiles(file));
                } else if (SUPPORTED_FORMATS.matcher(file.getName()).find()) {
                    audioFiles.add(file);
                }
            }
        }
        return audioFiles;
    }

    private void processAudioFile(File file) throws Exception {
        String filePath = file.getAbsolutePath();
        
        Song existingSong = songMapper.selectByFilePath(filePath);
        
        AudioFile audioFile = AudioFileIO.read(file);
        Tag tag = audioFile.getTag();
        org.jaudiotagger.audio.AudioHeader header = audioFile.getAudioHeader();

        String title = tag != null && tag.getFirst(FieldKey.TITLE) != null && !tag.getFirst(FieldKey.TITLE).isEmpty()
                ? tag.getFirst(FieldKey.TITLE)
                : file.getName().replaceFirst("[.][^.]+$", "");
        
        String artist = tag != null ? tag.getFirst(FieldKey.ARTIST) : null;
        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : null;
        long duration = header != null ? (long) header.getTrackLength() * 1000 : 0;
        int fileSize = (int) file.length();
        String fileType = getFileExtension(file.getName());
        
        String coverPath = null;
        if (tag != null) {
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null && artwork.getBinaryData() != null) {
                coverPath = saveCoverImage(artwork.getBinaryData(), title);
            }
        }

        if (existingSong != null) {
            existingSong.setTitle(title);
            existingSong.setArtist(artist != null && !artist.isEmpty() ? artist : "未知艺术家");
            existingSong.setAlbum(album != null && !album.isEmpty() ? album : "未知专辑");
            existingSong.setDuration(duration);
            existingSong.setFileSize(fileSize);
            existingSong.setFileType(fileType);
            existingSong.setArtistGroup(artist != null && !artist.isEmpty() ? artist : "未知艺术家");
            existingSong.setAlbumGroup(album != null && !album.isEmpty() ? album : "未知专辑");
            if (coverPath != null) {
                existingSong.setCoverPath(coverPath);
            }
            songMapper.update(existingSong);
        } else {
            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist != null && !artist.isEmpty() ? artist : "未知艺术家");
            song.setAlbum(album != null && !album.isEmpty() ? album : "未知专辑");
            song.setFilePath(filePath);
            song.setCoverPath(coverPath);
            song.setDuration(duration);
            song.setFileSize(fileSize);
            song.setFileType(fileType);
            song.setArtistGroup(artist != null && !artist.isEmpty() ? artist : "未知艺术家");
            song.setAlbumGroup(album != null && !album.isEmpty() ? album : "未知专辑");
            songMapper.insert(song);
        }
    }

    private String saveCoverImage(byte[] imageData, String title) {
        try {
            Path coverDir = Paths.get(coverPath);
            if (!Files.exists(coverDir)) {
                Files.createDirectories(coverDir);
            }

            String filename = UUID.randomUUID() + ".jpg";
            Path coverFilePath = coverDir.resolve(filename);
            
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image != null) {
                ImageIO.write(image, "jpg", coverFilePath.toFile());
                return "/covers/" + filename;
            }
        } catch (IOException e) {
            System.err.println("保存封面图片失败: " + e.getMessage());
        }
        return null;
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1).toUpperCase();
        }
        return "UNKNOWN";
    }
}