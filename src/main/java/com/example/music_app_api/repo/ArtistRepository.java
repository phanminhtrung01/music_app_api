package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

    @Query(
            value = """
                    SELECT a FROM Artist a
                    WHERE a.name = ?1
                    AND a.birthday = ?2
                    """
    )
    Optional<Artist> findByNameAndBirthday(String name, String birthday);

    @Query(
            value = """
                    SELECT a FROM Artist a
                    JOIN a.songs s
                    WHERE s.idSong = ?1
                    """
    )
    List<Artist> getArtistsBySong(String idSong);

    List<Artist> getArtistByNameOrRealName(String name, String realName, Pageable top);
}
