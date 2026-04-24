package com.musicplayer.controller;

import com.musicplayer.entity.Song;
import com.musicplayer.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@CrossOrigin(origins = "*")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(songService.search(keyword));
        }
        return ResponseEntity.ok(songService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        Song song = songService.findById(id);
        if (song == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(song);
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamSong(@PathVariable Long id) {
        Song song = songService.findById(id);
        if (song == null) {
            return ResponseEntity.notFound().build();
        }
        
        File file = new File(song.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        FileSystemResource resource = new FileSystemResource(file);
        String contentType = getContentType(song.getFileType());
        String filename = URLEncoder.encode(song.getTitle() + "." + song.getFileType().toLowerCase(), StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/artists")
    public ResponseEntity<List<String>> getAllArtists() {
        return ResponseEntity.ok(songService.findAllArtists());
    }

    @GetMapping("/albums")
    public ResponseEntity<List<String>> getAllAlbums() {
        return ResponseEntity.ok(songService.findAllAlbums());
    }

    @GetMapping("/artist/{artist}")
    public ResponseEntity<List<Song>> getSongsByArtist(@PathVariable String artist) {
        return ResponseEntity.ok(songService.findByArtist(artist));
    }

    @GetMapping("/album/{album}")
    public ResponseEntity<List<Song>> getSongsByAlbum(@PathVariable String album) {
        return ResponseEntity.ok(songService.findByAlbum(album));
    }

    @GetMapping("/group/artist")
    public ResponseEntity<Map<String, List<Song>>> groupByArtist() {
        return ResponseEntity.ok(songService.groupByArtist());
    }

    @GetMapping("/group/album")
    public ResponseEntity<Map<String, List<Song>>> groupByAlbum() {
        return ResponseEntity.ok(songService.groupByAlbum());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCount() {
        Map<String, Integer> result = new HashMap<>();
        result.put("count", songService.count());
        return ResponseEntity.ok(result);
    }

    private String getContentType(String fileType) {
        if (fileType == null) {
            return "application/octet-stream";
        }
        return switch (fileType.toUpperCase()) {
            case "MP3" -> "audio/mpeg";
            case "FLAC" -> "audio/flac";
            case "WAV" -> "audio/wav";
            case "AAC" -> "audio/aac";
            case "OGG" -> "audio/ogg";
            case "M4A" -> "audio/mp4";
            default -> "application/octet-stream";
        };
    }
}