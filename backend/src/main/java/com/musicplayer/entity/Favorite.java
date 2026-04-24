package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long songId;
    private LocalDateTime createTime;
}