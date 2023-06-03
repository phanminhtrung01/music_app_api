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
                    SELECT * FROM genre
                    WHERE EXISTS(SELECT * FROM genre_song
                    WHERE id_song = ?1 AND genre.id_genre=genre_song.id_genre)
                    """, nativeQuery = true
    )
    List<Genre> getGenresBySong(String idSong);
}
