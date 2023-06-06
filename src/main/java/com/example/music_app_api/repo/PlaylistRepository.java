package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    @Query(
            value = """
                    SELECT f FROM Playlist f
                    JOIN f.user u
                    WHERE u.idUser = ?1
                    """)
    List<Playlist> findByUser(String idUser);
}
