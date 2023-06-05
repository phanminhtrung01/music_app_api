package com.example.music_app_api.controller;

import com.example.music_app_api.entity.Song;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.song_request.SongRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController("RequestSongController")
@RequestMapping("/pmdv/src/")
public class SongController {

    private final SongRequestService songRequestSer;
    private final SongService songService;
    final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public SongController(
            SongRequestService songRequestSer,
            SongService songService) {
        this.songRequestSer = songRequestSer;
        this.songService = songService;
    }

    @GetMapping("search/hot/song")
    public ResponseEntity<ResponseObject> searchHotSong(
            @RequestParam(name = "query") String query) {

        HotSearch hotSearch;
        try {
            hotSearch = songRequestSer.searchHotSongs(query);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            hotSearch
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("search/multi")
    public ResponseEntity<ResponseObject> searchMulti(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "count", required = false) Integer count) {

        if (count == null) {
            count = 10;
        }

        try {
            MultiSearch multiSearch = songRequestSer
                    .searchMulti(query, count);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            multiSearch
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("search/multi/song")
    public ResponseEntity<ResponseObject> searchMultiSong(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "count", required = false) Integer count) {

        if (count == null) {
            count = 10;
        }
        try {
            MultiSearch multiSearch = songRequestSer.searchMultiSongs(query, count);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            multiSearch
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("search/multi/artist")
    public ResponseEntity<ResponseObject> searchMultiArtist(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "count", required = false) Integer count) {

        if (count == null) {
            count = 10;
        }

        try {
            MultiSearch multiSearch = songRequestSer.searchMultiArtists(query, count);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            multiSearch
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("get/streaming/song")
    public ResponseEntity<ResponseObject> getStreamingSong(
            @RequestParam(name = "id") String id) {

        StreamSourceSong streamSourceSong;
        final int thread = Runtime.getRuntime().availableProcessors();
        final ExecutorService executorService = Executors.newFixedThreadPool(thread);
        final CompletableFuture<StreamSourceSong> streamSourceSongCompletableFutureN =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return songRequestSer
                                .getStreamSongN(new BasicNameValuePair("id", id));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executorService);
        final CompletableFuture<StreamSourceSong> streamSourceSongCompletableFuture =
                CompletableFuture.supplyAsync(() -> {
                            try {
                                return songRequestSer.getStreamSong(id);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }, executorService)
                        .exceptionally(throwable -> streamSourceSongCompletableFutureN.join());

        try {
            streamSourceSong = streamSourceSongCompletableFuture.join();
            streamSourceSongCompletableFuture.cancel(true);
            streamSourceSongCompletableFutureN.cancel(true);
            executorService.shutdownNow();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            streamSourceSong
                    ));

        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Failure",
                            e.getMessage()
                    ));

        }
    }

    @GetMapping("get/charts/song")
    public ResponseEntity<ResponseObject> getChartsSong(
            @RequestParam(name = "count") int n) {

        try {
            List<InfoSong> songs = songRequestSer.getChartsSong(n);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            songs
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            ""
                    ));
        }
    }

    @GetMapping("get/recommend/song")
    public ResponseEntity<ResponseObject> getRecommendSong(
            @RequestParam(name = "id") String idSong) {

        try {
            List<InfoSong> songs = songRequestSer
                    .getRecommendSongs(idSong);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            songs
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("get/song/new-release")
    public ResponseEntity<ResponseObject> getSongNewRelease() {
        try {
            List<InfoSong> songs = songRequestSer.getSongNewRelease();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get songs new release successful!",
                            songs
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            ""
                    ));
        }
    }

    @GetMapping("get/albums/genre")
    public ResponseEntity<ResponseObject> getAlbumsOfGenre(
            @RequestParam(name = "id") String idGenre) {

        try {
            List<InfoAlbum> albums = songRequestSer
                    .getAlbumsOfGenre(idGenre);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get albums of genre successful!",
                            albums
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            ""
                    ));
        }
    }

    @GetMapping("get/songs/artist")
    public ResponseEntity<ResponseObject> getSongsOfArtist(
            @RequestParam(name = "id") String idArtist,
            @RequestParam(name = "count", required = false) Integer count) {

        try {
            if (count == null) {
                count = 10;
            }
            List<InfoSong> songs = songRequestSer
                    .getSongsOfArtist(idArtist, count);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get songs of artist successful!",
                            songs
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            ""
                    ));
        }
    }

    @GetMapping("get/songs/album")
    public ResponseEntity<ResponseObject> getSongsOfAlbum(
            @RequestParam(name = "id") String idAlbum) {

        try {
            List<InfoSong> songs = songRequestSer
                    .getSongsOfAlbum(idAlbum);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get songs of album successful!",
                            songs
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Failure",
                            ""
                    ));
        }
    }

    @GetMapping("get/songs/db")
    public ResponseEntity<ResponseObject> getSongS(
            @RequestParam(value = "count", required = false) Integer n) {
        try {

            if (n == null) {
                n = 10;
            }

            List<Song> songs = songService.getSongsDB(n);
            List<InfoSong> infoSongs = mapper.convertValue(songs, new TypeReference<>() {
            });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs database successful!",
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

    @GetMapping("get/songs_by_playlist_on")
    public ResponseEntity<ResponseObject> getSongsByPlaylistOnline(
            @RequestParam(value = "idPlaylist") String id) {
        try {

            List<Song> songs = songService.getSongsByPlayListOn(id);
            List<InfoSong> infoSongs = mapper.convertValue(songs, new TypeReference<>() {
            });

            return songs.size() > 0 ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get songs by playlist online successful!",
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

}
