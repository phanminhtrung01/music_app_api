package com.example.music_app_api.service.database_server;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.Song;

import java.util.List;

public interface SongService {
    List<Song> getAllSongs();

    List<Song> getSongs(int count);

    Song save(Song Song);

    Song delete(String idSong);

    Song getById(String idSong);

    List<Song> getSongsByGenre(String idGenre);

    List<Song> getSongsByPlayList(String idPlaylist);

    List<Song> getSongsByIdUser(String idUser, TypeSong typeSong);

    Song addSongToChart(String idSong, String idChart);

    Song addSongToSongs(String idSong, String idUser, TypeSong typeSong);

    Song removeSongFromSongs(String idSong, String idUser, TypeSong typeSong);

    Song removeSongFromChart(String idSong, String idChart);
}
