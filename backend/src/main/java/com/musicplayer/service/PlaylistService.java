package com.musicplayer.service;

import com.musicplayer.entity.Playlist;
import com.musicplayer.entity.Song;
import java.util.List;

public interface PlaylistService {
    List<Playlist> findAll();
    Playlist findById(Long id);
    Playlist create(String name, String description);
    void delete(Long id);
    void addSong(Long playlistId, Long songId);
    void removeSong(Long playlistId, Long songId);
    List<Song> getSongs(Long playlistId);
}