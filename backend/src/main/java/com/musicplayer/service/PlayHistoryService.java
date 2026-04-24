package com.musicplayer.service;

import com.musicplayer.entity.Song;
import java.util.List;

public interface PlayHistoryService {
    void record(Long songId);
    List<Song> getRecent(int limit);
}