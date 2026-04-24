package com.musicplayer.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlayHistory {
    private Long id;
    private Long songId;
    private LocalDateTime playTime;
}