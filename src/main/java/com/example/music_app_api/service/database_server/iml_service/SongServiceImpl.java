package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.*;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.SongRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    private final SongRepository songRepository;
    private final ArtistService artistService;
    private final UserRepository userRepository;
    private final GenreService genreService;
    private final PlaylistService playlistService;
    private final PlaylistOnService playlistOnService;
    private final ChartsService chartsService;
    private final SourceSongService sourceSongService;

    @Autowired
    public SongServiceImpl(
            SongRepository songRepository,
            ArtistService artistService,
            UserRepository userRepository,
            GenreService genreService,
            PlaylistService playlistService,
            PlaylistOnService playlistOnService,
            ChartsService chartsService,
            SourceSongService sourceSongService) {
        this.songRepository = songRepository;
        this.artistService = artistService;
        this.userRepository = userRepository;
        this.genreService = genreService;
        this.playlistService = playlistService;
        this.playlistOnService = playlistOnService;
        this.chartsService = chartsService;
        this.sourceSongService = sourceSongService;
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
    public List<Song> getSongsDB(int count) {
        List<Song> songs = getAllSongs();
        int min = Math.min(count, songs.size());
        return songs.subList(0, min);
    }

    @Override
    public List<Song> getSongsByTitle(String title, int count) {
        try {
            Pageable pageable = PageRequest.of(count, 1);
            return songRepository.findByTitleContainingIgnoreCase(title, pageable);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public SourceSong getSourceSong(String idSong) {
        try {
            getById(idSong);
            return sourceSongService.getSourceSongByIdSong(idSong);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Song save(Song song) {
        try {
            Optional<Song> optionalSong = songRepository.findSongByTitleAndArtistsNames(
                    song.getTitle(), song.getArtistsNames()
            );
            if (optionalSong.isPresent()) {
                throw new NotFoundException("There are already similar songs in the database");
            }
            songRepository.save(song);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return song;
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
    public List<Song> getSongsByPlayListOn(String idPlaylistOn) {
        playlistOnService.getPlaylistOnById(idPlaylistOn);

        return songRepository.getSongsByPlaylistOn(idPlaylistOn);
    }

    @Override
    public List<Song> getSongsOfChart(String idChart) {
        return songRepository.getSongsByChart(idChart);
    }

    @Override
    public List<Song> getSongsByGenre(String idGenre) {
        genreService.getGenreById(idGenre);
        return songRepository.getSongsByGenre(idGenre);
    }

    @Override
    public List<Song> getSongsByArtist(String idArtist, int count) {
        artistService.getArtist(idArtist);
        Pageable pageable = PageRequest.of(1, count);
        return songRepository.getSongsByArtist(idArtist, pageable);
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
    @Transactional
    public Song addSongToSongs(
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
    @Transactional
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

    @Override
    @Transactional
    public List<Artist> getArtistByIdSong(String idSong) {
        return artistService.getArtistByIdSong(idSong);
    }

    @Override
    @Transactional
    public List<Genre> getGenresByIdSong(String idSong) {
        return genreService.getGenresByIdSong(idSong);
    }
}
