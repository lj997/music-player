package com.musicplayer.service.impl;

import com.musicplayer.entity.Playlist;
import com.musicplayer.entity.PlaylistSong;
import com.musicplayer.entity.Song;
import com.musicplayer.mapper.PlaylistMapper;
import com.musicplayer.mapper.PlaylistSongMapper;
import com.musicplayer.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private PlaylistSongMapper playlistSongMapper;

    @Override
    public List<Playlist> findAll() {
        return playlistMapper.selectAll();
    }

    @Override
    public Playlist findById(Long id) {
        return playlistMapper.selectById(id);
    }

    @Override
    public Playlist create(String name, String description) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setSongCount(0);
        playlistMapper.insert(playlist);
        return playlist;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        playlistSongMapper.deleteByPlaylistId(id);
        playlistMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void addSong(Long playlistId, Long songId) {
        if (playlistSongMapper.exists(playlistId, songId)) {
            return;
        }
        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylistId(playlistId);
        playlistSong.setSongId(songId);
        playlistSong.setSortOrder(playlistSongMapper.countByPlaylistId(playlistId));
        playlistSongMapper.insert(playlistSong);
        updateSongCount(playlistId);
    }

    @Override
    @Transactional
    public void removeSong(Long playlistId, Long songId) {
        playlistSongMapper.delete(playlistId, songId);
        updateSongCount(playlistId);
    }

    @Override
    public List<Song> getSongs(Long playlistId) {
        return playlistSongMapper.selectSongsByPlaylistId(playlistId);
    }

    private void updateSongCount(Long playlistId) {
        int count = playlistSongMapper.countByPlaylistId(playlistId);
        playlistMapper.updateSongCount(playlistId, count);
    }
}