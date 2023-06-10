package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.*;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.repo.SongRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.*;
import com.example.music_app_api.service.song_request.InfoRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
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
    private final InfoRequestService infoRequestService;

    @Autowired
    public SongServiceImpl(
            SongRepository songRepository,
            ArtistService artistService,
            UserRepository userRepository,
            GenreService genreService,
            PlaylistService playlistService,
            PlaylistOnService playlistOnService,
            ChartsService chartsService,
            SourceSongService sourceSongService,
            InfoRequestService infoRequestService) {
        this.songRepository = songRepository;
        this.artistService = artistService;
        this.userRepository = userRepository;
        this.genreService = genreService;
        this.playlistService = playlistService;
        this.playlistOnService = playlistOnService;
        this.chartsService = chartsService;
        this.sourceSongService = sourceSongService;
        this.infoRequestService = infoRequestService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getSongsDB(int count) {
        Pageable pageable = PageRequest.of(0, count);
        List<Song> songs = songRepository.findAll(pageable).getContent();

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> getSongsByTitle(String title, int count) {
        try {
            Pageable pageable = PageRequest.of(count, 1);
            List<Song> songs = songRepository
                    .findByTitleContainingIgnoreCase(title, pageable);

            return songs.stream().map(this::getSong).toList();
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
    public Song save(@NotNull Song song) {
        Optional<Song> optionalSong = songRepository.findSongByTitleAndArtistsNames(
                song.getTitle(), song.getArtistsNames()
        );
        if (optionalSong.isPresent()) {
            throw new NotFoundException("There are already similar songs in the database");
        }

        if (isValidSong(song)) {
            Date today = new Date();
            long timeInMilliseconds = today.getTime();
            String numberStr = String.valueOf(timeInMilliseconds);
            String firstTenDigits = numberStr.substring(0, 10);
            song.setReleaseDate(Long.valueOf(firstTenDigits));

            songRepository.save(song);

            return song;
        }

        return new Song();
    }

    @Override
    @Transactional
    public Song save(Song song, SourceSong sourceSong) {
        try {
            Optional<Song> optionalSong = songRepository.findSongByTitleAndArtistsNames(
                    song.getTitle(), song.getArtistsNames()
            );
            if (optionalSong.isPresent()) {
                throw new NotFoundException("There are already similar songs in the database");
            }

            if (isValidSong(song)) {
                Date today = new Date();
                long timeInMilliseconds = today.getTime();
                String numberStr = String.valueOf(timeInMilliseconds);
                String firstTenDigits = numberStr.substring(0, 10);
                String sourceM4a = sourceSong.getSourceM4a();
                String source128 = sourceSong.getSource128();
                String source320 = sourceSong.getSource320();
                String sourceLossless = sourceSong.getSourceLossless();
                String source = source128;
                if (source == null) {
                    source = sourceM4a;
                }

                if (source == null) {
                    source = source320;
                }

                if (source == null) {
                    source = sourceLossless;
                }
                int duration = getDurationInSecondsWithMp3Spi(new File(source));

                song.setDuration(duration);
                song.setReleaseDate(Long.valueOf(firstTenDigits));
                song.setSourceSong(sourceSong);

                songRepository.save(song);
            }
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return getSong(song);
    }

    private int getDurationInSecondsWithMp3Spi(File file) {
        int duration;
        try {
            AudioFile audioFile = AudioFileIO.read(file);
            duration = audioFile.getAudioHeader().getTrackLength();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return duration;
    }

    private boolean isValidSong(@NotNull Song song) {
        String title = song.getTitle();
        String artistsNames = song.getArtistsNames();
        String thumbnail = song.getThumbnail();
        int duration = song.getDuration();

        if (title == null || title.isBlank()) {
            throw new RuntimeException("Invalid Title Song!");
        }

        if (artistsNames == null || artistsNames.isBlank()) {
            throw new RuntimeException("Invalid Name Song!");
        }

        if (thumbnail == null || thumbnail.isBlank()) {
            throw new RuntimeException("Invalid Thumbnail Song!");
        }

        if (duration <= 0) {
            throw new RuntimeException("Invalid Duration Song!");
        }

        return true;
    }

    @Override
    @Transactional
    public Song delete(String idSong) {
        try {
            Song song = getById(idSong);

            song.setChart(null);
            song.setSourceSong(null);
            song.setComments(null);
            song.setFile(null);

            song.getArtistsSing().clear();
            song.getGenres().clear();
            song.getPlaylistsOfSong().clear();
            song.getPlaylistsOnOfSong().clear();
            song.getUsersFavorite().clear();
            song.getUsersListen().clear();

            songRepository.saveAndFlush(song);

            songRepository.delete(song);

            return getSong(song);
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
    public Song getSong(@NotNull String idSong) {
        Song song;
        ObjectMapper objectMapper = new ObjectMapper();
        if (idSong.startsWith("S")) {
            song = getById(idSong);
        } else {

            Optional<InfoSong> infoSongOptional = infoRequestService.getInfoSong(idSong, true);
            if (infoSongOptional.isEmpty()) {
                throw new NotFoundException("Not fount song with ID: " + idSong);
            }
            InfoSong infoSong = infoSongOptional.get();
            Optional<Song> songDB = getByAllParameter(
                    infoSong.getTitle(),
                    infoSong.getArtistsNames(),
                    Integer.parseInt(infoSong.getDuration()));

            song = songDB.orElseGet(() -> objectMapper.convertValue(infoSong, Song.class));
            song.setEqualsCode(infoSong.getId());
        }
        return song;
    }

    @Override
    public Song getSong(@NotNull Song song) {
        Song songTemp;
        if (song.getEqualsCode() != null) {
            song.setIdSong(song.getEqualsCode());
        }
        songTemp = song;

        return songTemp;
    }

    private Optional<Song> getByAllParameter(
            String title, String artistsNames, int duration) {
        try {

            return songRepository
                    .findByTitleAndArtistsNamesAndDuration(title, artistsNames, duration);
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
    public List<Song> getSongsByPlayList(String idPlaylist) {
        playlistService.getById(idPlaylist);
        List<Song> songs = songRepository.getSongsByPlaylist(idPlaylist);

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Song> getSongsByPlayListOn(String idPlaylistOn) {
        playlistOnService.getPlaylistOnById(idPlaylistOn);
        List<Song> songs = songRepository.getSongsByPlaylistOn(idPlaylistOn);

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Song> getSongsOfChart(String idChart) {
        chartsService.getChartById(idChart);
        List<Song> songs = songRepository.getSongsByChart(idChart);

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Song> getSongsByGenre(String idGenre) {
        genreService.getGenreById(idGenre);
        List<Song> songs = songRepository.getSongsByGenre(idGenre);

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Song> getSongsByArtist(String idArtist, int count) {
        artistService.getArtist(idArtist);
        Pageable pageable = PageRequest.of(1, count);
        List<Song> songs = songRepository.getSongsByArtist(idArtist, pageable);

        return songs.stream().map(this::getSong).toList();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Song> getSongsByIdUser(
            String idUser, TypeSong typeSong) {
        try {
            Optional<User> user = userRepository.findById(idUser);
            if (user.isPresent()) {
                List<Song> songs;
                switch (typeSong) {
                    case FAVORITE -> {
                        songs = songRepository.getFavoriteSongsByUser(idUser);

                        return songs.stream().map(this::getSong).toList();
                    }
                    case LISTEN -> {
                        songs = songRepository.getListenSongsByUser(idUser);

                        return songs.stream().map(this::getSong).toList();
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
            Song song = getSong(idSong);
            Charts charts = chartsService.getChartById(idChart);
            song.setChart(charts);

            return getSong(song);
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
            Song song = getSong(idSong);
            chartsService.getChartById(idChart);
            song.setChart(null);

            return getSong(song);
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
    public Song addArtistsToSong(List<String> idArtists, String idSong) {
        try {
            Song song = getSong(idSong);
            idArtists.forEach(idArtist -> {
                Artist artist = artistService.getArtist(idArtist);
                artist.getSongs().add(song);
                song.getArtistsSing().add(artist);
            });

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
    public Song addSongToSingSong(String idSong, String idArtist) {
        try {
            Song song = getSong(idSong);
            Artist artist = artistService.getArtist(idArtist);

            song.getArtistsSing().add(artist);
            artist.getSongs().add(song);
            songRepository.save(song);

            return getSong(song);
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
            Song song = getSong(idSong);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + idUser);
            }

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

            return getSong(song);
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
            Song song = getSong(idSong);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + idUser);
            }

            User user = userOptional.get();

            switch (typeSong) {
                case FAVORITE -> user.getFavoriteSongs().remove(song);
                case LISTEN -> user.getHistoryListen().remove(song);
            }
            userRepository.save(user);

            return getSong(song);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
