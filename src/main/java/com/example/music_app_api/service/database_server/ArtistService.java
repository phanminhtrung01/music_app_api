package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> getAllArtist();

    List<Artist> getArtistsByNameOrRealName(String name, String realName, int count);

    Artist getArtistById(String idArtist);

    Artist getArtist(String idArtist);

    Artist getArtist(Artist artist);

    List<Artist> getArtistByIdSong(String idSong);

    Artist save(Artist artist);

    Artist addArtistToFavoriteArtist(String idArtist, String idUser);

    Artist removeArtistFromFavoriteArtist(String idArtist, String idUser);

    Artist delete(String idArtist);

}
