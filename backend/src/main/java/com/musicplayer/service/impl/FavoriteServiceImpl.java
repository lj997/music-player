package com.musicplayer.service.impl;

import com.musicplayer.entity.Favorite;
import com.musicplayer.entity.Song;
import com.musicplayer.mapper.FavoriteMapper;
import com.musicplayer.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public void add(Long songId) {
        if (favoriteMapper.exists(songId)) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setSongId(songId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void remove(Long songId) {
        favoriteMapper.deleteBySongId(songId);
    }

    @Override
    public boolean isFavorite(Long songId) {
        return favoriteMapper.exists(songId);
    }

    @Override
    public List<Song> findAll() {
        return favoriteMapper.selectAll();
    }
}