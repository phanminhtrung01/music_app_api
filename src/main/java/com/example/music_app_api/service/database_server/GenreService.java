package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getAll();

    Genre save(Genre genre);

    Genre delete(String idGenre);

    Genre getGenreById(String genreId);
}
