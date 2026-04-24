package com.musicplayer.service;

import com.musicplayer.entity.MusicFolder;
import java.util.List;

public interface MusicFolderService {
    List<MusicFolder> findAll();
    MusicFolder add(String folderPath);
    void delete(Long id);
    void scanAll();
    void scanFolder(Long folderId);
}