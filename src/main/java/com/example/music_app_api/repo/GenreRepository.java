package com.example.music_app_api.repo;


import com.example.music_app_api.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    @Query(
            value = """
                    SELECT g FROM Genre g
                    JOIN g.songs s
                    WHERE s.idSong = ?1
                    """
    )
    List<Genre> getGenresBySong(String idSong);
}
