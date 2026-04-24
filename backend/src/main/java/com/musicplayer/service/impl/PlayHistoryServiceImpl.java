package com.musicplayer.service.impl;

import com.musicplayer.entity.PlayHistory;
import com.musicplayer.entity.Song;
import com.musicplayer.mapper.PlayHistoryMapper;
import com.musicplayer.service.PlayHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PlayHistoryServiceImpl implements PlayHistoryService {

    @Autowired
    private PlayHistoryMapper playHistoryMapper;

    private static final int MAX_HISTORY = 30;

    @Override
    @Transactional
    public void record(Long songId) {
        PlayHistory playHistory = new PlayHistory();
        playHistory.setSongId(songId);
        playHistoryMapper.insert(playHistory);
        
        int count = playHistoryMapper.count();
        if (count > MAX_HISTORY) {
            playHistoryMapper.deleteOldest(MAX_HISTORY);
        }
    }

    @Override
    public List<Song> getRecent(int limit) {
        return playHistoryMapper.selectRecent(Math.min(limit, MAX_HISTORY));
    }
}