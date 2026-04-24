package com.musicplayer.controller;

import com.musicplayer.entity.MusicFolder;
import com.musicplayer.service.MusicFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/folders")
@CrossOrigin(origins = "*")
public class MusicFolderController {

    @Autowired
    private MusicFolderService musicFolderService;

    @GetMapping
    public ResponseEntity<List<MusicFolder>> getAllFolders() {
        return ResponseEntity.ok(musicFolderService.findAll());
    }

    @PostMapping
    public ResponseEntity<MusicFolder> addFolder(@RequestBody Map<String, String> request) {
        String folderPath = request.get("folderPath");
        if (folderPath == null || folderPath.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        MusicFolder folder = musicFolderService.add(folderPath.trim());
        return ResponseEntity.ok(folder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        musicFolderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanAll() {
        musicFolderService.scanAll();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "扫描完成");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/scan/{id}")
    public ResponseEntity<Map<String, Object>> scanFolder(@PathVariable Long id) {
        musicFolderService.scanFolder(id);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "扫描完成");
        return ResponseEntity.ok(result);
    }
}