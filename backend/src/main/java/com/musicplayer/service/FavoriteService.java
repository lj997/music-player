package com.musicplayer.service;

import com.musicplayer.entity.Song;
import java.util.List;

public interface FavoriteService {
    void add(Long songId);
    void remove(Long songId);
    boolean isFavorite(Long songId);
    List<Song> findAll();
}