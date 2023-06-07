package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, String> {

    @Query("""
            SELECT s FROM Song s
            WHERE LOWER(s.title)
            LIKE LOWER(CONCAT('%', ?1, '%'))
            """
    )
    List<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.genres g
                    WHERE g.idGenre = ?1
                    """)
    List<Song> getSongsByGenre(String idGenre);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN FETCH s.artistsSing a
                    WHERE a.idArtist = ?1
                    """)
    List<Song> getSongsByArtist(String idArtist, Pageable pageable);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.playlistsOfSong p
                    WHERE p.idPlaylist = ?1
                    """)
    List<Song> getSongsByPlaylist(String idPlaylist);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.playlistsOnOfSong po
                    WHERE po.encodeId = ?1
                    """)
    List<Song> getSongsByPlaylistOn(String idPlaylist);


    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.chart ch
                    WHERE ch.idChart = ?1
                    """)
    List<Song> getSongsByChart(String idChart);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.usersFavorite u
                    WHERE u.idUser = ?1
                    """)
    List<Song> getFavoriteSongsByUser(String idUser);

    @Query(
            value = """
                    SELECT s FROM Song s
                    JOIN s.usersListen u
                    WHERE u.idUser = ?1
                    """)
    List<Song> getListenSongsByUser(String idUser);

    @Query(
            value = """
                    select * from song
                    where title = ?1 && artists_name =?2
                    """,
            nativeQuery = true
    )
    Optional<Song> findSongByTitleAndArtistsNames(String title, String artistNames);

}
