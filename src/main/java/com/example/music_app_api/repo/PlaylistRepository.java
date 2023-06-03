package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    @Query(
            value = "select * from playlist where " +
                    "exists(select id_playlist, id_user from playlist_user " +
                    "where id_user = ?1 && id_playlist = playlist.id_playlist)",
            nativeQuery = true)
    List<Playlist> findByUser(String idUser);
}
