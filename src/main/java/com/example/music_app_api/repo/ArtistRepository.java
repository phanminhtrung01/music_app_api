package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

    @Query(
            value = """
                    SELECT * FROM artist
                    where EXISTS(SELECT * FROM sing_song WHERE id_song=?1
                    AND artist.id_artist = sing_song.id_artist)
                    """, nativeQuery = true
    )
    List<Artist> getArtistsBySong(String idSong);

    List<Artist> getArtistByNameOrRealName(String name, String realName, Pageable top);
}
