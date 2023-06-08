package com.example.music_app_api.service.database_server;


import com.example.music_app_api.dto.PlaylistDto;
import com.example.music_app_api.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    List<PlaylistDto> getPlayListByUser(String idUser);

    PlaylistDto save(Playlist playlist);

    PlaylistDto addUserToPlaylist(String idUser, Playlist playlist);

    PlaylistDto removeUserFromPlaylist(String idUser, String idPlaylist);

    PlaylistDto addSongToPlaylist(String idSong, String idPlaylist);

    PlaylistDto removeSongFromPlaylist(String idSong, String idPlaylist);

    Boolean addSongsToPlaylist(List<String> idSongs, String idPlaylist);

    PlaylistDto removeSongsFromPlaylist(List<String> idSongs, String idPlaylist);

    PlaylistDto removeAllSongsFromPlaylist(String idPlaylist);

    PlaylistDto delete(String id);

    Playlist getById(String playlistId);
}
