package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.SourceSongRepository;
import com.example.music_app_api.service.database_server.SourceSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SourceSongServiceIml implements SourceSongService {
    private final SourceSongRepository sourceSongRepository;

    @Autowired
    public SourceSongServiceIml(
            SourceSongRepository sourceSongRepository) {
        this.sourceSongRepository = sourceSongRepository;
    }

    @Override
    public SourceSong getSourceSongByIdSong(String idSong) {
        try {
            Optional<SourceSong> songOptional = sourceSongRepository.findById(idSong);
            if (songOptional.isEmpty()) {
                throw new NotFoundException("Not fount song with ID: " + idSong);
            }

            return sourceSongRepository
                    .findSourceSongBySong(idSong)
                    .orElse(new SourceSong());
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
