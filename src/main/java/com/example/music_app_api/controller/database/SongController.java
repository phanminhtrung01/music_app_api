package com.example.music_app_api.controller.database;

import com.example.music_app_api.component.enums.TypeSong;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("DatabaseSongController")
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/pmdv/db/song/")
public class SongController {
    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {

        this.songService = songService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseObject> addSong(
            @RequestBody Song Song) {
        try {
            Song SongPar = songService.save(Song);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add song successful!",
                            SongPar));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ResponseObject> deleteSong(
            @RequestParam("idSong") String idSong) {
        try {
            Song song = songService.delete(idSong);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song successful!",
                            song));
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

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to chart successful!",
                            song));

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

    @DeleteMapping("delete/song_to_chart")
    public ResponseEntity<ResponseObject> removeSongFromChart(
            @RequestParam("idSong") String idSong,
            @RequestParam("idChart") String idChart) {
        try {
            Song song = songService.removeSongFromChart(idSong, idChart);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song to chart successful!",
                            song));

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

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by playlist successful!",
                                    songs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    songs)
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

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by genre successful!",
                                    songs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    songs)
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

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by genre successful!",
                                    songs)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty!",
                                    songs)
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

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to favorite song successful!",
                            song)
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

    @DeleteMapping("delete/song_from_favorite_song")
    public ResponseEntity<ResponseObject> removeSongFromFavoriteSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .removeSongFromSongs(idSong, idUser, TypeSong.FAVORITE);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song from favorite song successful!",
                            song)
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

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by genre successful!",
                                    songs)) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    songs));
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

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to favorite song successful!",
                            song)
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

    @DeleteMapping("delete/song_from_listen_song")
    public ResponseEntity<ResponseObject> removeSongFromListenSong(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {
        try {
            Song song = songService
                    .removeSongFromSongs(idSong, idUser, TypeSong.LISTEN);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song from favorite song successful!",
                            song)
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
