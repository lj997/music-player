package com.musicplayer.controller;

import com.musicplayer.entity.Song;
import com.musicplayer.service.FavoriteService;
import com.musicplayer.service.PlayHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HistoryFavoriteController {

    @Autowired
    private PlayHistoryService playHistoryService;

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/history/{songId}")
    public ResponseEntity<Map<String, Object>> recordHistory(@PathVariable Long songId) {
        playHistoryService.record(songId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Song>> getHistory(@RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(playHistoryService.getRecent(limit));
    }

    @PostMapping("/favorites/{songId}")
    public ResponseEntity<Map<String, Object>> addFavorite(@PathVariable Long songId) {
        favoriteService.add(songId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isFavorite", true);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/favorites/{songId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(@PathVariable Long songId) {
        favoriteService.remove(songId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isFavorite", false);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Song>> getFavorites() {
        return ResponseEntity.ok(favoriteService.findAll());
    }

    @GetMapping("/favorites/{songId}")
    public ResponseEntity<Map<String, Object>> isFavorite(@PathVariable Long songId) {
        boolean isFavorite = favoriteService.isFavorite(songId);
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorite", isFavorite);
        return ResponseEntity.ok(result);
    }
}