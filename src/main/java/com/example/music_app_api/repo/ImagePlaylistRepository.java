package com.example.music_app_api.repo;

import com.example.music_app_api.entity.ImagePlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagePlaylistRepository extends JpaRepository<ImagePlaylist, String> {

    @Query(
            value = "SELECT * FROM Playlist ORDER BY RAND() LIMIT 1",
            nativeQuery = true
    )
    ImagePlaylist findRandomImagePlaylist();
}
