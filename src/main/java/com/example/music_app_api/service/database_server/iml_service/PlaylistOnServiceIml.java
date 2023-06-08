package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.PlaylistOnline;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.PlaylistOnRepository;
import com.example.music_app_api.service.database_server.PlaylistOnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistOnServiceIml implements PlaylistOnService {
    final PlaylistOnRepository playlistOnRepository;

    public PlaylistOnServiceIml(
            PlaylistOnRepository playlistOnRepository) {
        this.playlistOnRepository = playlistOnRepository;
    }

    @Override
    public PlaylistOnline getPlaylistOnById(String idPlaylistOn) {
        try {
            Optional<PlaylistOnline> playlistOptional = playlistOnRepository
                    .findById(idPlaylistOn);
            if (playlistOptional.isEmpty()) {
                throw new NotFoundException("Not fount playlist online with ID: " + idPlaylistOn);
            }

            return playlistOptional.get();

        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistOnline> getPlaylistOns(int count) {
        List<PlaylistOnline> playlistsOnline = playlistOnRepository.findAll();
        int min = Math.min(count, playlistsOnline.size());
        return playlistsOnline.subList(0, min);
    }
}
