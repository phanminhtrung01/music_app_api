package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Genre;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.GenreRepository;
import com.example.music_app_api.service.database_server.GenreService;
import com.example.music_app_api.service.database_server.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final SongService songService;

    @Autowired
    @Lazy
    public GenreServiceImpl(
            GenreRepository genreRepository,
            SongService songService) {
        this.genreRepository = genreRepository;
        this.songService = songService;
    }


    @Override
    public List<Genre> getAll() {
        try {
            return genreRepository.findAll();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public List<Genre> getGenresByIdSong(String idSong) {
        try {
            Song song = songService.getSong(idSong);

            return genreRepository.getGenresBySong(song.getIdSong());
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Genre save(Genre genre) {
        try {
            genreRepository.save(genre);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return genre;
    }

    @Override
    public Genre delete(String idGenre) {
        try {
            Genre genre = getGenreById(idGenre);
            genreRepository.delete(genre);

            return genre;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Genre getGenreById(String genreId) {
        try {
            Optional<Genre> genreOptional = genreRepository.findById(genreId);
            if (genreOptional.isEmpty()) {
                throw new NotFoundException("Not fount genre with ID: " + genreId);
            }

            return genreOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
