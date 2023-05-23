package com.example.music_app_api.service.database_server.iml_service;


import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.controller.InfoController;
import com.example.music_app_api.entity.Charts;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.repo.SongRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.ChartsService;
import com.example.music_app_api.service.database_server.GenreService;
import com.example.music_app_api.service.database_server.PlaylistService;
import com.example.music_app_api.service.database_server.SongService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final GenreService genreService;
    private final PlaylistService playlistService;
    private final ChartsService chartsService;
    private final InfoController infoController;

    @Autowired
    public SongServiceImpl(
            SongRepository songRepository,
            UserRepository userRepository,
            GenreService genreService,
            PlaylistService playlistService,
            ChartsService chartsService,
            InfoController infoController) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.genreService = genreService;
        this.playlistService = playlistService;
        this.chartsService = chartsService;
        this.infoController = infoController;
    }

    @Override
    public List<Song> getAllSongs() {
        try {
            return songRepository.findAll();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song save(Song Song) {
        try {
            songRepository.save(Song);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return Song;
    }

    @Override
    public Song delete(String idSong) {
        try {
            Song song = getById(idSong);
            songRepository.delete(song);

            return song;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song getById(String idSong) {
        try {
            Optional<Song> songOptional = songRepository.findById(idSong);
            if (songOptional.isEmpty()) {
                throw new NotFoundException("Not fount song with ID: " + idSong);
            }

            return songOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public List<Song> getSongsByPlayList(String idPlaylist) {
        playlistService.getById(idPlaylist);
        return songRepository.getSongsByPlaylist(idPlaylist);
    }

    @Override
    public List<Song> getSongsByGenre(String idGenre) {
        genreService.getGenreById(idGenre);
        return songRepository.getSongsByGenre(idGenre);
    }

    @Override
    public List<Song> getSongsByIdUser(
            String idUser, TypeSong typeSong) {
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                switch (typeSong) {
                    case FAVORITE -> {
                        return songRepository.getFavoriteSongsByUser(idUser);
                    }
                    case LISTEN -> {
                        return songRepository.getListenSongsByUser(idUser);
                    }
                }
                return songRepository.getFavoriteSongsByUser(idUser);
            } else {
                throw new NotFoundException("Not fount user with ID: " + idUser);
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
    public Song addSongToChart(
            String idSong, String idChart) {
        try {
            Song song = getById(idSong);
            Charts charts = chartsService.getChartById(idChart);
            song.setChart(charts);
            return song;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song removeSongFromChart(
            String idSong, String idChart) {
        try {
            Song song = getById(idSong);
            chartsService.getChartById(idChart);
            song.setChart(null);
            return song;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song addSongToSongs(
            String idSong, String idUser, TypeSong typeSong) {
        try {
            Optional<Song> songOptional = songRepository.findById(idSong);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (songOptional.isEmpty()) {
                try {
                    InfoSong infoSong = (InfoSong) Objects.requireNonNull(infoController
                                    .getInfoSong(idSong)
                                    .getBody())
                            .getData();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Song song = objectMapper.convertValue(infoSong, Song.class);

                    songOptional = Optional.of(song);
                } catch (Exception e) {
                    throw new NotFoundException("Not fount song with ID: " + idSong);
                }
            }

            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + idUser);
            }

            Song song = songOptional.get();
            User user = userOptional.get();

            switch (typeSong) {
                case FAVORITE -> {
                    song.getUsersFavorite().add(user);
                    user.getFavoriteSongs().add(song);
                }
                case LISTEN -> {
                    song.getUsersListen().add(user);
                    user.getHistoryListen().add(song);
                }
            }

            songRepository.save(song);
            return song;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song removeSongFromSongs(
            String idSong, String idUser, TypeSong typeSong) {
        try {
            Optional<Song> songOptional = songRepository.findById(idSong);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (songOptional.isEmpty()) {
                throw new NotFoundException("Not fount song with ID: " + idSong);
            }

            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + idUser);
            }

            Song song = songOptional.get();
            User user = userOptional.get();

            switch (typeSong) {
                case FAVORITE -> user.getFavoriteSongs().remove(song);
                case LISTEN -> user.getHistoryListen().remove(song);
            }

            userRepository.save(user);
            return song;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
