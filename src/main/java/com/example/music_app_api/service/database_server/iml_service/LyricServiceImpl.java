package com.example.music_app_api.service.database_server.iml_service;


import com.example.music_app_api.entity.Lyric;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.LyricRepo;
import com.example.music_app_api.service.database_server.LyricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LyricServiceImpl implements LyricService {

    private final LyricRepo lyricRepo;

    @Autowired
    public LyricServiceImpl(LyricRepo lyricRepo) {
        this.lyricRepo = lyricRepo;
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
}
