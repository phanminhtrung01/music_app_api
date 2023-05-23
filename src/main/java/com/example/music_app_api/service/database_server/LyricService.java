package com.example.music_app_api.service.database_server;


import com.example.music_app_api.entity.Lyric;

import java.util.Optional;

public interface LyricService {
    Lyric save(Lyric lyric);

    Optional<Lyric> getById(String id);

    Optional<Lyric> delete(String id);

}
