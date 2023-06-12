package com.example.music_app_api.service.database_server;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;

import java.util.List;

public interface SongService {

    List<Song> getSongsByTitle(String title, int count);

    StreamSourceSong getSourceSong(String idSong);

    List<Song> getSongsDB(int count);

    Song save(Song song);

    Song save(Song song, SourceSong sourceSong);

    Song delete(String idSong);

    Song getById(String idSong, boolean isIn);

    Song getSong(Song song);

    List<Song> getSongsByGenre(String idGenre);

    List<Song> getSongsByArtist(String idArtist, int count);

    List<Song> getSongsOfChart(String idChart);


    List<Song> getSongsByPlayList(String idPlaylist);

    List<Song> getSongsByPlayListOn(String idPlaylistOn);

    List<Song> getSongsByIdUser(String idUser, TypeSong typeSong);

    Song addSongToChart(String idSong, String idChart);

    Song addArtistsToSong(List<String> idArtists, String idSong);

    Song addSongToSongs(String idSong, String idUser, TypeSong typeSong);

    Song getSong(String idSong);

    Song removeSongFromSongs(String idSong, String idUser, TypeSong typeSong);

    Song removeSongFromChart(String idSong, String idChart);
}
