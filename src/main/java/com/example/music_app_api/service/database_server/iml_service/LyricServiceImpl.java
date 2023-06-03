package com.example.music_app_api.service.database_server.iml_service;


import com.example.music_app_api.entity.Lyric;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.LyricRepo;
import com.example.music_app_api.service.database_server.LyricService;
import com.example.music_app_api.service.database_server.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LyricServiceImpl implements LyricService {

    private final LyricRepo lyricRepo;
    private final SongService songService;

    @Autowired
    public LyricServiceImpl(
            LyricRepo lyricRepo,
            SongService songService) {
        this.lyricRepo = lyricRepo;
        this.songService = songService;
    }

    @Override
    public Lyric save(Lyric lyric) {
        try {
            lyricRepo.save(lyric);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return lyric;
    }

    @Override
    public Optional<Lyric> getById(String id) {
        try {
            return lyricRepo.findById(id);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Optional<Lyric> delete(String id) {
        try {
            Optional<Lyric> lyric = getById(id);
            lyric.ifPresent(lyricRepo::delete);

            return lyric;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Lyric getLyricByIdSong(String idSong) {
        songService.getById(idSong);
        Optional<Lyric> lyric = lyricRepo.findBySong(idSong);

        if (lyric.isPresent()) {
            return lyric.get();
        } else {
            throw new RuntimeException("Not found lyric of song with ID: " + idSong);
        }
    }
}
