package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Song {
    private Long id;
    private String title;
    private String artist;
    private String album;
    private String filePath;
    private String coverPath;
    private Long duration;
    private Integer fileSize;
    private String fileType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String artistGroup;
    private String albumGroup;
}