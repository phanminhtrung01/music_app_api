package com.example.music_app_api.controller.database;

import com.example.music_app_api.dto.PlaylistDto;
import com.example.music_app_api.entity.Playlist;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pmdv/db/playlist/")
@CrossOrigin(value = "*", maxAge = 3600)
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(
            PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("get/playlist_by_user")
    public ResponseEntity<ResponseObject> getPlayListByUser(
            @RequestParam("idUser") String idUser) {
        try {
            List<PlaylistDto> playlists = playlistService.getPlayListByUser(idUser);

            return playlists.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get playlists by user successful!",
                                    playlists)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty",
                                    playlists)
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

    @PostMapping("add/user_to_playlist")
    public ResponseEntity<ResponseObject> addUserToPlaylist(
            @RequestParam("idUser") String idUser,
            @RequestBody Playlist playlist) {
        try {
            Playlist playlistPar = playlistService
                    .addUserToPlaylist(idUser, playlist);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add user to playlist successful!",
                            playlistPar)
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

    @DeleteMapping("delete/user_from_playlist")
    public ResponseEntity<ResponseObject> removeUserFromPlaylist(
            @RequestParam("idUser") String idUser,
            @RequestParam("idPlaylist") String idPlaylist) {
        try {
            Playlist playlist = playlistService
                    .removeUserFromPlaylist(idUser, idPlaylist);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove user from playlist successful!",
                            playlist)
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

    @PostMapping("add/song_to_playlist")
    public ResponseEntity<ResponseObject> addSongToPlaylist(
            @RequestParam("idSong") String idSong,
            @RequestParam("idPlaylist") String idPlaylist) {
        try {
            Playlist playlist = playlistService
                    .addSongToPlaylist(idSong, idPlaylist);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query add song to playlist successful!",
                            playlist)
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

    @DeleteMapping("delete/song_from_playlist")
    public ResponseEntity<ResponseObject> removeSongFromPlaylist(
            @RequestParam("idSong") String idSong,
            @RequestParam("idPlaylist") String idPlaylist) {
        try {
            Playlist playlist = playlistService
                    .removeSongFromPlaylist(idSong, idPlaylist);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove song from playlist successful!",
                            playlist)
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
