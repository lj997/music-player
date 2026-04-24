package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MusicFolder {
    private Long id;
    private String folderPath;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}