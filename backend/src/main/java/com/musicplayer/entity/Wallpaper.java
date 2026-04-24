package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Wallpaper {
    private Long id;
    private String name;
    private String filePath;
    private Integer isCurrent;
    private LocalDateTime createTime;
}