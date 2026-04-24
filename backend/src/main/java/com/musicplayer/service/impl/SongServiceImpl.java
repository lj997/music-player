package com.musicplayer.service.impl;

import com.musicplayer.entity.Song;
import com.musicplayer.mapper.SongMapper;
import com.musicplayer.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongMapper songMapper;

    @Override
    public Song findById(Long id) {
        return songMapper.selectById(id);
    }

    @Override
    public List<Song> findAll() {
        return songMapper.selectAll();
    }

    @Override
    public List<Song> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return songMapper.search(keyword.trim());
    }

    @Override
    public List<Song> findByArtist(String artist) {
        return songMapper.selectByArtist(artist);
    }

    @Override
    public List<Song> findByAlbum(String album) {
        return songMapper.selectByAlbum(album);
    }

    @Override
    public List<String> findAllArtists() {
        return songMapper.selectAllArtists();
    }

    @Override
    public List<String> findAllAlbums() {
        return songMapper.selectAllAlbums();
    }

    @Override
    public Map<String, List<Song>> groupByArtist() {
        List<Song> songs = songMapper.selectAll();
        return songs.stream()
                .filter(s -> s.getArtist() != null && !s.getArtist().isEmpty())
                .collect(Collectors.groupingBy(Song::getArtist));
    }

    @Override
    public Map<String, List<Song>> groupByAlbum() {
        List<Song> songs = songMapper.selectAll();
        return songs.stream()
                .filter(s -> s.getAlbum() != null && !s.getAlbum().isEmpty())
                .collect(Collectors.groupingBy(Song::getAlbum));
    }

    @Override
    public int count() {
        return songMapper.count();
    }
}