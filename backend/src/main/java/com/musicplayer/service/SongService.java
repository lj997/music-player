package com.musicplayer.service;

import com.musicplayer.entity.Song;
import java.util.List;
import java.util.Map;

public interface SongService {
    Song findById(Long id);
    List<Song> findAll();
    List<Song> search(String keyword);
    List<Song> findByArtist(String artist);
    List<Song> findByAlbum(String album);
    List<String> findAllArtists();
    List<String> findAllAlbums();
    Map<String, List<Song>> groupByArtist();
    Map<String, List<Song>> groupByAlbum();
    int count();
}