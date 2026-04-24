package com.musicplayer.mapper;

import com.musicplayer.entity.Wallpaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface WallpaperMapper {
    int insert(Wallpaper wallpaper);
    int deleteById(Long id);
    Wallpaper selectById(Long id);
    Wallpaper selectCurrent();
    List<Wallpaper> selectAll();
    int updateCurrent(@Param("id") Long id);
    int resetCurrent();
    int count();
}