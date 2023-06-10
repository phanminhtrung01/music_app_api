package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.SourceSongRepository;
import com.example.music_app_api.service.database_server.SourceSongService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public SourceSong add(@NotNull SourceSong sourceSong) {
        try {
            String sourceM4a = sourceSong.getSourceM4a();
            String source128 = sourceSong.getSource128();
            String source320 = sourceSong.getSource320();
            String sourceLossless = sourceSong.getSourceLossless();

            if (sourceM4a != null && !sourceM4a.isEmpty()
                    || source128 != null && !source128.isEmpty()
                    || source320 != null && !source320.isEmpty()
                    || sourceLossless != null && !sourceLossless.isEmpty()) {

                sourceSongRepository.save(sourceSong);
            } else {
                throw new RuntimeException("Invalid. At least one source is required!");
            }

        } catch (RuntimeException e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return sourceSong;
    }
}
