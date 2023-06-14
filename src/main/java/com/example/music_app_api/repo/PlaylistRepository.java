package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    @Query(
            value = """
                    SELECT f FROM Playlist f
                    JOIN f.user u
                    WHERE u.idUser = ?1
                    """)
    @Transactional(readOnly = true)
    List<Playlist> findByUser(String idUser);

    @Modifying
    @Query(
            value = """
                    DELETE FROM music_api.playlist_song
                    WHERE music_api.playlist_song.id_playlist = ?1
                    """,
            nativeQuery = true
    )
    void deleteAllSongsFromPlaylist(String playlistId);
}
