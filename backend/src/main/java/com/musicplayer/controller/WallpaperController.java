package com.musicplayer.controller;

import com.musicplayer.entity.Wallpaper;
import com.musicplayer.service.WallpaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallpapers")
@CrossOrigin(origins = "*")
public class WallpaperController {

    @Autowired
    private WallpaperService wallpaperService;

    @GetMapping
    public ResponseEntity<List<Wallpaper>> getAllWallpapers() {
        return ResponseEntity.ok(wallpaperService.findAll());
    }

    @GetMapping("/current")
    public ResponseEntity<Wallpaper> getCurrentWallpaper() {
        Wallpaper current = wallpaperService.getCurrent();
        return ResponseEntity.ok(current);
    }

    @PostMapping
    public ResponseEntity<Wallpaper> uploadWallpaper(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
        Wallpaper wallpaper = wallpaperService.upload(name, file);
        return ResponseEntity.ok(wallpaper);
    }

    @PutMapping("/{id}/current")
    public ResponseEntity<Map<String, Object>> setCurrentWallpaper(@PathVariable Long id) {
        wallpaperService.setCurrent(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteWallpaper(@PathVariable Long id) {
        wallpaperService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}