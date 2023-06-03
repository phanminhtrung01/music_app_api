package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.SourceSong;

public interface SourceSongService {
    SourceSong getSourceSongByIdSong(String idSong);
}
