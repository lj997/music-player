package com.musicplayer.service;

import com.musicplayer.entity.Wallpaper;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface WallpaperService {
    List<Wallpaper> findAll();
    Wallpaper getCurrent();
    Wallpaper upload(String name, MultipartFile file);
    void setCurrent(Long id);
    void delete(Long id);
}