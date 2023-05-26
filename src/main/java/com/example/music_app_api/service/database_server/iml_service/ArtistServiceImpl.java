package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.ArtistRepository;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArtistServiceImpl(
            ArtistRepository artistRepository,
            UserRepository userRepository) {
        this.artistRepository = artistRepository;
        this.userRepository = userRepository;
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
    public Artist getArtist(String idArtist) {
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
            Artist artist = getArtist(idArtist);
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
    public Artist addArtistToFavoriteArtist(
            String idArtist, String idUser) {
        try {
            Artist artist = getArtist(idArtist);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + idUser);
            }

            User user = userOptional.get();
            artist.getUsers().add(user);
            user.getFavoriteArtists().add(artist);
            userRepository.save(user);

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
    public Artist removeArtistFromFavoriteArtist(
            String idArtist, String idUser) {
        try {
            File file = new File("URI");
            Artist artist = getArtist(idArtist);
            Optional<User> userOptional = userRepository.findById(idUser);

            if (userOptional.isEmpty()) {
                throw new RuntimeException("Not fount user with ID: " + idUser);
            }

            User user = userOptional.get();
            user.getFavoriteArtists().remove(artist);
            userRepository.save(user);

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