package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Playlist {
    private Long id;
    private String name;
    private String coverPath;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer songCount;
}