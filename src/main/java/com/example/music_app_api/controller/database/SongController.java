package com.example.music_app_api.controller.database;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.dto.SongDto;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.model.SongRequest;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.SourceSongService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("DatabaseSongController")
@RequestMapping("/pmdv/db/song/")
@CrossOrigin(value = "*", maxAge = 3600)
public class SongController {
    private final SongService songService;
    private final SourceSongService sourceSongService;
    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public SongController(
            SongService songService,
            SourceSongService sourceSongService) {

        this.songService = songService;
        this.sourceSongService = sourceSongService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseObject> addSong(
            @RequestBody Song song) {
        try {
            Song songPar = songService.save(song);

            if (song.getIdSong() == null
                    || song.getIdSong().isBlank()) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new ResponseObject(
                                HttpStatus.CREATED.value(),
                                "Query add song failure!",
                                null));
            }

            InfoSong infoSong = mapper
                    .convertValue(songPar, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add song successful!",
                            infoSong));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("add/song")
    public ResponseEntity<ResponseObject> addSong(
            @RequestBody SongRequest songRequest) {

        ObjectMapper objectMapper = new ObjectMapper();
        SourceSong sourceSong = objectMapper
                .convertValue(songRequest, SourceSong.class);
        Song song = objectMapper
                .convertValue(songRequest, Song.class);

        try {
            sourceSongService.add(sourceSong);
        } catch (Exception e) {
            if (e.getMessage().contains("Invalid")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(
                                HttpStatus.CONFLICT.value(),
                                e.getMessage(),
                                null));
            }

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));

        }

        try {
            songService.save(song, sourceSong);

            Song songResult = songService.getSong(song.getIdSong());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success!",
                            songResult));

        } catch (Exception e) {
            if (e.getMessage().contains("already")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(
                                HttpStatus.FORBIDDEN.value(),
                                e.getMessage(),
                                null));
            } else if (e.getMessage().contains("Invalid")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(
                                HttpStatus.CONFLICT.value(),
                                e.getMessage(),
                                null));
            } else if (e.getMessage().contains("Not fount")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(
                                HttpStatus.NOT_FOUND.value(),
                                e.getMessage(),
                                null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseObject(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                e.getMessage(),
                                null));
            }
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ResponseObject> deleteSong(
            @RequestParam("idSong") String idSong) {
        try {
            Song song = songService.delete(idSong);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song successful!",
                            infoSong));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("add/song_to_chart")
    public ResponseEntity<ResponseObject> addSongToChart(
            @RequestParam("idSong") String idSong,
            @RequestParam("idChart") String idChart) {
        try {
            Song song = songService.addSongToChart(idSong, idChart);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to chart successful!",
                            infoSong));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("add/artists/song")
    public ResponseEntity<ResponseObject> addArtistsToSong(
            @RequestBody List<String> idArtists,
            @RequestParam("idSong") String idSong) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Song song = songService.addArtistsToSong(idArtists, idSong);
            SongDto songDto = objectMapper.convertValue(song, SongDto.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success!",
                            songDto));

        } catch (Exception e) {
            if (e.getMessage().contains("Not fount")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(
                                HttpStatus.NOT_FOUND.value(),
                                e.getMessage(),
                                null));
            } else if (e.getMessage().contains("constraint")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(
                                HttpStatus.CONFLICT.value(),
                                "Artist was added to the song!",
                                null));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseObject(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                e.getMessage(),
                                null));
            }
        }
    }

    @DeleteMapping("delete/song_to_chart")
    public ResponseEntity<ResponseObject> removeSongFromChart(
            @RequestParam("idSong") String idSong,
            @RequestParam("idChart") String idChart) {
        try {
            Song song = songService.removeSongFromChart(idSong, idChart);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song to chart successful!",
                            infoSong));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get/songs_by_playlist")
    public ResponseEntity<ResponseObject> getSongsByPlayList(
            @RequestParam("idPlaylist") String idPlaylist) {
        try {
            List<Song> songs = songService.getSongsByPlayList(idPlaylist);
            List<InfoSong> infoSongs = mapper
                    .convertValue(songs, new TypeReference<>() {
                    });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by playlist successful!",
                                    infoSongs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    infoSongs)
                            );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get/songs_by_genre")
    public ResponseEntity<ResponseObject> getSongsByGenre(
            @RequestParam("idGenre") String idGenre) {
        try {
            List<Song> songs = songService.getSongsByGenre(idGenre);
            List<InfoSong> infoSongs = mapper
                    .convertValue(songs, new TypeReference<>() {
                    });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by genre successful!",
                                    infoSongs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    infoSongs)
                            );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get/favorite_song_by_user")
    public ResponseEntity<ResponseObject> getFavoriteSongByUser(
            @RequestParam("idUser") String idUser) {
        try {
            List<Song> songs = songService.getSongsByIdUser(idUser, TypeSong.FAVORITE);
            List<InfoSong> infoSongs = mapper
                    .convertValue(songs, new TypeReference<>() {
                    });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by favorite song successful!",
                                    infoSongs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty!",
                                    infoSongs)
                            );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("add/song_to_favorite_song")
    public ResponseEntity<ResponseObject> addSongToFavoriteSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .addSongToSongs(idSong, idUser, TypeSong.FAVORITE);
            InfoSong infoSong = mapper.convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to favorite song successful!",
                            infoSong)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return runtimeException.getMessage().contains("constraint")
                    ? ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ResponseObject(
                            HttpStatus.CONFLICT.value(),
                            "The song already in favorites song",
                            null))
                    : ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete/song_from_favorite_song")
    public ResponseEntity<ResponseObject> removeSongFromFavoriteSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .removeSongFromSongs(idSong, idUser, TypeSong.FAVORITE);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song from favorite song successful!",
                            infoSong)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get/listen_song_by_user")
    public ResponseEntity<ResponseObject> getListenSongByUser(
            @RequestParam("idUser") String idUser) {
        try {
            List<Song> songs = songService.getSongsByIdUser(idUser, TypeSong.LISTEN);
            List<InfoSong> infoSongs = mapper
                    .convertValue(songs, new TypeReference<>() {
                    });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by genre successful!",
                                    infoSongs)) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    infoSongs));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("add/song_to_listen_song")
    public ResponseEntity<ResponseObject> addSongToListenSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .addSongToSongs(idSong, idUser, TypeSong.LISTEN);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to favorite song successful!",
                            infoSong)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return runtimeException.getMessage().contains("constraint")
                    ? ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ResponseObject(
                            HttpStatus.CONFLICT.value(),
                            "The song is already on the playlist",
                            null))
                    : ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete/song_from_listen_song")
    public ResponseEntity<ResponseObject> removeSongFromListenSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .removeSongFromSongs(idSong, idUser, TypeSong.LISTEN);
            InfoSong infoSong = mapper
                    .convertValue(song, InfoSong.class);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song from favorite song successful!",
                            infoSong)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

}
