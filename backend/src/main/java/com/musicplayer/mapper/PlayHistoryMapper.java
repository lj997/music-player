package com.musicplayer.mapper;

import com.musicplayer.entity.PlayHistory;
import com.musicplayer.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PlayHistoryMapper {
    int insert(PlayHistory playHistory);
    int deleteOldest(@Param("limit") int limit);
    List<Song> selectRecent(@Param("limit") int limit);
    int count();
}