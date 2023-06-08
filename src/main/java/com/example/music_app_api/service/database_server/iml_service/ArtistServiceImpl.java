package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.repo.ArtistRepository;
import com.example.music_app_api.service.database_server.ArtistService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.UserService;
import com.example.music_app_api.service.song_request.InfoRequestService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final UserService userService;
    private final SongService songService;
    private final InfoRequestService infoRequestService;

    @Autowired
    @Lazy
    public ArtistServiceImpl(
            ArtistRepository artistRepository,
            UserService userService,
            SongService songService,
            InfoRequestService infoRequestService) {
        this.artistRepository = artistRepository;
        this.userService = userService;
        this.songService = songService;
        this.infoRequestService = infoRequestService;
    }

    @Override
    public List<Artist> getAllArtist() {
        try {
            return artistRepository.findAll();
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
    public List<Artist> getArtistsByNameOrRealName(
            String name, String realName, int count) {
        Pageable topTen = PageRequest.of(1, count);
        return artistRepository.getArtistByNameOrRealName(name, realName, topTen);
    }

    @Override
    public Artist getArtistById(String idArtist) {
        try {
            Optional<Artist> artistOptional = artistRepository.findById(idArtist);
            if (artistOptional.isEmpty()) {
                throw new NotFoundException("Not fount artist with ID: " + idArtist);
            }

            return artistOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public Artist getArtist(@NotNull String idArtist) {
        Artist artist;
        if (idArtist.startsWith("A")) {
            artist = getArtistById(idArtist);
        } else {

            Optional<InfoArtist> infoArtistOptional = infoRequestService
                    .getInfoArtist(idArtist, true);
            if (infoArtistOptional.isEmpty()) {
                throw new NotFoundException("Not fount artist with ID: " + idArtist);
            }
            InfoArtist infoArtist = infoArtistOptional.get();
            Optional<Artist> artistOptional = artistRepository
                    .findByNameAndBirthday(infoArtist.getName(), infoArtist.getBirthday());

            artist = artistOptional.orElseGet(() -> new Artist(
                    infoArtist.getName(),
                    infoArtist.getBirthday(),
                    infoArtist.getThumbnail(),
                    infoArtist.getSortBiography()
            ));

        }
        return artist;
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Artist> getArtistByIdSong(String idSong) {
        try {
            songService.getSong(idSong);

            return artistRepository.getArtistsBySong(idSong);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Artist save(Artist artist) {
        try {
            artistRepository.save(artist);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return artist;
    }

    @Override
    public Artist delete(String idArtist) {
        try {
            Artist artist = getArtistById(idArtist);
            artistRepository.delete(artist);

            return artist;
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
    public Artist addArtistToFavoriteArtist(
            String idArtist, String idUser) {
        try {
            Artist artist = getArtist(idArtist);
            User user = userService.getUserById(idUser);

            artist.getUsers().add(user);
            user.getFavoriteArtists().add(artist);
            userService.save(user);

            return artist;
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
    public Artist removeArtistFromFavoriteArtist(
            String idArtist, String idUser) {
        try {
            Artist artist = getArtist(idArtist);
            User user = userService.getUserById(idUser);

            user.getFavoriteArtists().remove(artist);
            userService.save(user);

            return artist;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
