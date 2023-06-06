package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.PlaylistOnline;

import java.util.List;

public interface PlaylistOnService {
    void getPlaylistOnById(String idPlaylistOn);

    List<PlaylistOnline> getPlaylistOns(int count);
}
