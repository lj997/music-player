package com.musicplayer.mapper;

import com.musicplayer.entity.MusicFolder;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MusicFolderMapper {
    int insert(MusicFolder musicFolder);
    int deleteById(Long id);
    List<MusicFolder> selectAll();
    MusicFolder selectById(Long id);
    MusicFolder selectByPath(String path);
}