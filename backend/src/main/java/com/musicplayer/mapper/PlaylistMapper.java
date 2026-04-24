package com.musicplayer.mapper;

import com.musicplayer.entity.Playlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlaylistMapper {
    int insert(Playlist playlist);
    int update(Playlist playlist);
    int deleteById(Long id);
    Playlist selectById(Long id);
    List<Playlist> selectAll();
    int updateSongCount(@Param("playlistId") Long playlistId, @Param("count") int count);
}