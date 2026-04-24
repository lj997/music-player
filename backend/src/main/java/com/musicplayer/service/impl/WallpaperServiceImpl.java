package com.musicplayer.service.impl;

import com.musicplayer.entity.Wallpaper;
import com.musicplayer.mapper.WallpaperMapper;
import com.musicplayer.service.WallpaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class WallpaperServiceImpl implements WallpaperService {

    @Autowired
    private WallpaperMapper wallpaperMapper;

    @Value("${app.wallpaper-path}")
    private String wallpaperPath;

    @Override
    public List<Wallpaper> findAll() {
        return wallpaperMapper.selectAll();
    }

    @Override
    public Wallpaper getCurrent() {
        return wallpaperMapper.selectCurrent();
    }

    @Override
    @Transactional
    public Wallpaper upload(String name, MultipartFile file) {
        try {
            Path uploadPath = Paths.get(wallpaperPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String filename = UUID.randomUUID() + extension;
            
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setName(name != null && !name.isEmpty() ? name : filename);
            wallpaper.setFilePath("/wallpapers/" + filename);
            wallpaper.setIsCurrent(wallpaperMapper.count() == 0 ? 1 : 0);
            
            wallpaperMapper.insert(wallpaper);
            return wallpaper;
        } catch (IOException e) {
            throw new RuntimeException("上传壁纸失败", e);
        }
    }

    @Override
    @Transactional
    public void setCurrent(Long id) {
        wallpaperMapper.resetCurrent();
        wallpaperMapper.updateCurrent(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Wallpaper wallpaper = wallpaperMapper.selectById(id);
        if (wallpaper != null) {
            String filePath = wallpaper.getFilePath();
            if (filePath != null && filePath.startsWith("/wallpapers/")) {
                String filename = filePath.substring("/wallpapers/".length());
                try {
                    Files.deleteIfExists(Paths.get(wallpaperPath, filename));
                } catch (IOException e) {
                    // 忽略删除文件的错误
                }
            }
            wallpaperMapper.deleteById(id);
        }
    }
}