package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.dto.PlaylistDto;
import com.example.music_app_api.entity.Playlist;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.PlaylistRepository;
import com.example.music_app_api.service.database_server.PlaylistService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserService userService;
    private final SongService songService;

    @Autowired
    @Lazy
    public PlaylistServiceImpl(
            PlaylistRepository playlistRepository,
            UserService userService,
            SongService songService) {
        this.playlistRepository = playlistRepository;
        this.userService = userService;
        this.songService = songService;
    }

    @Override
    public PlaylistDto save(Playlist playlist) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            playlistRepository.save(playlist);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return mapper.convertValue(playlist, PlaylistDto.class);
    }

    @Override
    public PlaylistDto delete(String id) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            Playlist playlist = getById(id);
            playlistRepository.delete(playlist);

            return mapper.convertValue(playlist, PlaylistDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public PlaylistDto addUserToPlaylist(
            String idUser, Playlist playlist) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = userService.getUserById(idUser);

            playlist.setUser(user);
            playlistRepository.save(playlist);

            return mapper.convertValue(playlist, PlaylistDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public PlaylistDto removeUserFromPlaylist(
            String idUser, String idPlaylist) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            userService.getUserById(idUser);

            Playlist playlist = getById(idPlaylist);
            playlist.setSongs(null);
            playlist.setUser(null);
            playlistRepository.deleteById(playlist.getIdPlaylist());

            return mapper.convertValue(playlist, PlaylistDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public PlaylistDto addSongToPlaylist(
            String idSong, String idPlaylist) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Song song = songService.getSong(idSong);
            Playlist playlist = getById(idPlaylist);

            song.getPlaylistsOfSong().add(playlist);
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);

            return mapper.convertValue(playlist, PlaylistDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public PlaylistDto removeSongFromPlaylist(
            String idSong, String idPlaylist) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Song song = songService.getSong(idSong);
            Playlist playlist = getById(idPlaylist);

            if (playlist.getSongs().isEmpty()) {
                throw new NotFoundException("List song of playlist empty");
            }
            if (!playlist.getSongs().contains(song)) {
                throw new NotFoundException("Not found song in playlist");
            }
            playlist.getSongs().remove(song);
            playlistRepository.save(playlist);

            return mapper.convertValue(playlist, PlaylistDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Boolean addSongsToPlaylist(
            List<String> idSongs, String idPlaylist) {
        return null;
    }

    @Override
    @Transactional
    public PlaylistDto removeSongsFromPlaylist(
            @NotNull List<String> idSongs, String idPlaylist) {
        ObjectMapper mapper = new ObjectMapper();

        Playlist playlist = getById(idPlaylist);
        idSongs.forEach(idSong -> removeSongFromPlaylist(idSong, idPlaylist));

        return mapper.convertValue(playlist, PlaylistDto.class);
    }

    @Override
    public PlaylistDto removeAllSongsFromPlaylist(String idPlaylist) {
        ObjectMapper mapper = new ObjectMapper();
        Playlist playlist = getById(idPlaylist);

        if (playlist.getSongs().isEmpty()) {
            throw new NotFoundException("List song of playlist empty");
        }

        playlist.getSongs().clear();
        playlistRepository.save(playlist);

        return mapper.convertValue(playlist, PlaylistDto.class);
    }

    @Override
    public Playlist getById(String playlistId) {
        try {
            Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
            if (playlistOptional.isEmpty()) {
                throw new NotFoundException("Not fount playlist with ID: " + playlistId);
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
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<PlaylistDto> getPlayListByUser(String idUser) {
        userService.getUserById(idUser);
        ObjectMapper mapper = new ObjectMapper();

        List<Playlist> playlists = playlistRepository.findByUser(idUser);
        return mapper.convertValue(playlists, new TypeReference<>() {
        });
    }
}
