package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.Song;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        List<Artist> artists = artistRepository.getArtistByNameOrRealName(name, realName, topTen);
        artists = artists.stream().map(this::getArtist).toList();
        return artists;
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
                    infoArtist.getSortBiography(),
                    infoArtist.getId()
            ));

        }
        return artist;
    }

    @Override
    public Artist getArtist(@NotNull Artist artist) {
        Artist artistTemp;
        if (artist.getEqualsCode() != null) {
            artist.setIdArtist(artist.getEqualsCode());
        }
        artistTemp = artist;

        return artistTemp;
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Artist> getArtistByIdSong(String idSong) {
        try {
            Song song = songService.getSong(idSong);
            List<Artist> artists = artistRepository.getArtistsBySong(song.getIdSong());
            artists = artists.stream().map(this::getArtist).toList();

            return artists;
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
            Optional<Artist> optionalArtist = artistRepository.findByNameAndRealNameAndBirthday(
                    artist.getName(), artist.getRealName(), artist.getBirthday()
            );
            if (optionalArtist.isPresent()) {
                throw new Exception("There are already similar artist in the database");
            }

            isValidArtist(artist, true);
            if (artist.getThumbnailM() == null) {
                artist.setThumbnailM(artist.getThumbnail());
            }

            if (artist.getBiography() == null) {
                artist.setBiography(artist.getSortBiography());
            }

            if (artist.getTotalFollow() == null) {
                artist.setTotalFollow(0);
            }

            artistRepository.save(artist);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }

        return getArtist(artist);
    }

    private void isValidArtist(@NotNull Artist artist, boolean constraint) {

        String newName = artist.getName();
        String newRealName = artist.getRealName();
        String newBirthday = artist.getBirthday();
        String newThumbnail = artist.getThumbnail();
        String newSortBiography = artist.getSortBiography();

        if (newName == null || newName.isBlank()) {
            throw new RuntimeException("Invalid Name Artist!");
        }
        if (newRealName == null || newRealName.isBlank()) {
            throw new RuntimeException("Invalid RealName Artist!");
        }
        if (newBirthday == null || newBirthday.isBlank()) {
            throw new RuntimeException("Invalid Birthday Artist!");
        }
        if (newThumbnail == null || newThumbnail.isBlank()) {
            throw new RuntimeException("Invalid Thumbnail Artist!");
        }
        if (newSortBiography == null || newSortBiography.isBlank()) {
            throw new RuntimeException("Invalid Thumbnail Artist!");
        }

        if (constraint) {

            if (!isValidDate(newBirthday)) {
                throw new RuntimeException("Invalid Birthday User!");
            }
        }

    }

    private boolean isValidDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public Artist delete(String idArtist) {
        try {
            Artist artist = getArtistById(idArtist);
            artistRepository.delete(artist);

            return getArtist(artist);
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

            return getArtist(artist);
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

            return getArtist(artist);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Artist addArtistsToSingSong(String idArtist, String idUser) {
        return null;
    }
}
