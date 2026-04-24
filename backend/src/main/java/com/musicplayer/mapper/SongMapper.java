package com.musicplayer.mapper;

import com.musicplayer.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SongMapper {
    int insert(Song song);
    int update(Song song);
    int deleteById(Long id);
    Song selectById(Long id);
    Song selectByFilePath(String filePath);
    List<Song> selectAll();
    List<Song> search(@Param("keyword") String keyword);
    List<Song> selectByArtist(@Param("artist") String artist);
    List<Song> selectByAlbum(@Param("album") String album);
    List<String> selectAllArtists();
    List<String> selectAllAlbums();
    int count();
    int deleteByFilePath(String filePath);
}