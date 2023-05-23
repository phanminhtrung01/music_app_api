package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, String> {
    @Query(
            value = """
                    select * from song where
                    exists(select id_song, id_genre from genre_song
                    where Song.id_song = genre_song.id_song && id_genre = ?1)
                    """,
            nativeQuery = true)
    List<Song> getSongsByGenre(String idGenre);

    @Query(
            value = """
                    select * from song where exists(select id_playlist from playlist
                    where exists(select id_song from playlist_song
                    where playlist_song.id_song = song.id_song && id_playlist = ?1))""",
            nativeQuery = true)
    List<Song> getSongsByPlaylist(String idPlaylist);

    @Query(
            value = """
                    select * from song where
                    exists(select id_song from favorite_song
                    where id_user = ?1 && song.id_song = favorite_song.id_song)
                    """,
            nativeQuery = true)
    List<Song> getFavoriteSongsByUser(String idUser);

    @Query(
            value = """
                    select * from song where
                    exists(select id_song from listen_song
                    where id_user = ?1 && song.id_song = listen_song.id_song)
                    """,
            nativeQuery = true)
    List<Song> getListenSongsByUser(String idUser);

}
