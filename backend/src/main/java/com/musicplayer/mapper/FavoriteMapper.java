package com.musicplayer.mapper;

import com.musicplayer.entity.Favorite;
import com.musicplayer.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FavoriteMapper {
    int insert(Favorite favorite);
    int deleteBySongId(Long songId);
    List<Song> selectAll();
    boolean exists(Long songId);
}