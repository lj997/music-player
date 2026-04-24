package com.musicplayer.controller;

import com.musicplayer.entity.Playlist;
import com.musicplayer.entity.Song;
import com.musicplayer.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.findById(id);
        if (playlist == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(playlist);
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String description = request.get("description");
        
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Playlist playlist = playlistService.create(name.trim(), description);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Song>> getPlaylistSongs(@PathVariable Long id) {
        return ResponseEntity.ok(playlistService.getSongs(id));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> addSongToPlaylist(
            @PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.addSong(playlistId, songId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Map<String, Object>> removeSongFromPlaylist(
            @PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSong(playlistId, songId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}