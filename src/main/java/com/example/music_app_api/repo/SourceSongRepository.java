package com.example.music_app_api.repo;

import com.example.music_app_api.entity.SourceSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceSongRepository extends JpaRepository<SourceSong, String> {

    @Query(
            value = """
                    SELECT * FROM source_song
                    WHERE EXISTS(SELECT * FROM song where id_song=?1
                    AND source_song.id_source=song.id_source)
                    """,
            nativeQuery = true)
    Optional<SourceSong> findSourceSongBySong(String idSong);
}
