package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Playlist;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.PlaylistRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.PlaylistService;
import com.example.music_app_api.service.database_server.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongService songService;

    @Autowired
    @Lazy
    public PlaylistServiceImpl(
            PlaylistRepository playlistRepository,
            UserRepository userRepository,
            SongService songService) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songService = songService;
    }

    @Override
    public Playlist save(Playlist playlist) {
        try {
            playlistRepository.save(playlist);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return playlist;
    }

    @Override
    public Playlist delete(String id) {
        try {
            Playlist playlist = getById(id);
            playlistRepository.delete(playlist);

            return playlist;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Playlist addUserToPlaylist(
            String idUser, String idPlaylist) {
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                Playlist playlist = getById(idPlaylist);
                user.get().getPlaylistsOfUser().add(playlist);
                playlist.getUsers().add(user.get());
                userRepository.save(user.get());
                playlistRepository.save(playlist);
                return playlist;
            } else {
                throw new RuntimeException("Not fount user with ID: " + idUser);
            }
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Playlist removeUserFromPlaylist(
            String idUser, String idPlaylist) {
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                Playlist playlist = getById(idPlaylist);
                if (playlist.getUsers().isEmpty()) {
                    throw new NotFoundException("List user of playlist empty");
                }

                if (!playlist.getUsers().contains(user.get())) {
                    throw new NotFoundException("Not found user from playlist");
                }

                playlist.getUsers().remove(user.get());
                playlistRepository.save(playlist);

                return playlist;
            } else {
                throw new RuntimeException("Not fount user with ID: " + idUser);
            }
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Playlist addSongToPlaylist(
            String idSong, String idPlaylist) {
        try {
            Song song = songService.getById(idSong);
            Playlist playlist = getById(idPlaylist);

            song.getPlaylistsOfSong().add(playlist);
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);

            return playlist;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Playlist removeSongFromPlaylist(
            String idSong, String idPlaylist) {
        try {
            Song song = songService.getById(idSong);
            Playlist playlist = getById(idPlaylist);

            if (playlist.getSongs().isEmpty()) {
                throw new NotFoundException("List song of playlist empty");
            }
            if (!playlist.getSongs().contains(song)) {
                throw new NotFoundException("Not found song in playlist");
            }
            playlist.getSongs().remove(song);
            playlistRepository.save(playlist);

            return playlist;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Playlist addSongsToPlaylist(
            List<String> idSongs, String idPlaylist) {
        return null;
    }

    @Override
    public Playlist removeSongsFromPlaylist(
            List<String> idSongs, String idPlaylist) {
        return null;
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
    public List<Playlist> getPlayListByUser(String idUser) {
        Optional<User> user = userRepository.findById(idUser);
        if (user.isPresent()) {
            return playlistRepository.findByUser(idUser);
        } else {
            throw new RuntimeException("Not fount user with ID: " + idUser);
        }
    }
}
