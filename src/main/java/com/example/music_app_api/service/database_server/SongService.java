package com.example.music_app_api.service.database_server;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.Genre;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.SourceSong;

import java.util.List;

public interface SongService {
    List<Song> getAllSongs();

    List<Song> getSongsByTitle(String title, int count);

    SourceSong getSourceSong(String idSong);

    List<Song> getSongsDB(int count);

    Song save(Song Song);

    Song delete(String idSong);

    Song getById(String idSong);

    List<Song> getSongsByGenre(String idGenre);

    List<Song> getSongsByPlayList(String idPlaylist);

    List<Song> getSongsByPlayListOn(String idPlaylistOn);

    List<Song> getSongsByIdUser(String idUser, TypeSong typeSong);

    Song addSongToChart(String idSong, String idChart);

    Song addSongToSongs(String idSong, String idUser, TypeSong typeSong);

    Song removeSongFromSongs(String idSong, String idUser, TypeSong typeSong);

    Song removeSongFromChart(String idSong, String idChart);

    List<Artist> getArtistByIdSong(String idSong);

    List<Genre> getGenresByIdSong(String idSong);
}
