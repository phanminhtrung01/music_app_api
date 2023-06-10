package com.example.music_app_api.repo;


import com.example.music_app_api.entity.Lyric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LyricRepo extends JpaRepository<Lyric, String> {
    @Query(
            value = """
                    SELECT l FROM Lyric l JOIN l.song s WHERE s.idSong = ?1
                    """
    )
    Optional<Lyric> findBySong(String idSong);
}
