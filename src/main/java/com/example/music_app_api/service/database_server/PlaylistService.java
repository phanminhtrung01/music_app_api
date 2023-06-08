package com.example.music_app_api.service.database_server;


import com.example.music_app_api.dto.PlaylistDto;
import com.example.music_app_api.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    List<PlaylistDto> getPlayListByUser(String idUser);

    Playlist save(Playlist playlist);

    Playlist addUserToPlaylist(String idUser, Playlist playlist);

    Playlist removeUserFromPlaylist(String idUser, String idPlaylist);

    Playlist addSongToPlaylist(String idSong, String idPlaylist);

    Playlist removeSongFromPlaylist(String idSong, String idPlaylist);

    Boolean addSongsToPlaylist(List<String> idSongs, String idPlaylist);

    Playlist removeSongsFromPlaylist(List<String> idSongs, String idPlaylist);

    Playlist removeAllSongsFromPlaylist(String idPlaylist);

    Playlist delete(String id);

    Playlist getById(String playlistId);
}
