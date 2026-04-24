package com.musicplayer.mapper;

import com.musicplayer.entity.PlaylistSong;
import com.musicplayer.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlaylistSongMapper {
    int insert(PlaylistSong playlistSong);
    int delete(@Param("playlistId") Long playlistId, @Param("songId") Long songId);
    int deleteByPlaylistId(Long playlistId);
    List<Song> selectSongsByPlaylistId(Long playlistId);
    int countByPlaylistId(Long playlistId);
    boolean exists(@Param("playlistId") Long playlistId, @Param("songId") Long songId);
}