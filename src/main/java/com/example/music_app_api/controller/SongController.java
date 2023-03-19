package com.example.music_app_api.controller;

import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.multi_search.MultiSearchSong;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import com.example.music_app_api.service.song_request.SongRequestService;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/pmdv/ma/")
public class SongController {

    private final SongRequestService songRequestSer;

    @Autowired
    public SongController(
            SongRequestService songRequestSer) {
        this.songRequestSer = songRequestSer;
    }

    @GetMapping("search/hot/song")
    public ResponseEntity<ResponseObject> searchHotSong(
            @RequestParam(required = false, name = "query") String query) {

        try {
            HotSearch data = songRequestSer.searchHotSongs(query);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            data
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

    @GetMapping("search/multi/song")
    public ResponseEntity<ResponseObject> searchMultiSong(
            @RequestParam(required = false, name = "query") String query) {

        try {
            //TODO: -----------Use CompletableFuture-----------
            MultiSearch multiSearch = songRequestSer.searchHMultiSongsZ(query);
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
                            ""
                    ));
        }
    }

    @GetMapping("get/streaming/song")
    public ResponseEntity<ResponseObject> getStreamingSong(
            @RequestParam(required = false, name = "id") String id) {

        StreamSourceSong streamSourceSong;
        final int thread = Runtime.getRuntime().availableProcessors();
        final ExecutorService executorService = Executors.newFixedThreadPool(thread);
        final CompletableFuture<StreamSourceSong> streamSourceSongCompletableFutureN =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return songRequestSer
                                .getStreamSongN(new BasicNameValuePair(
                                        "id",
                                        id));
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
                }, executorService);

        try {
            streamSourceSong =
                    streamSourceSongCompletableFuture.get();
            streamSourceSongCompletableFutureN.cancel(true);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            streamSourceSong
                    ));
        } catch (Exception e) {
            log.error(e.getMessage());

            try {
                streamSourceSong =
                        streamSourceSongCompletableFutureN.get();
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Success",
                                streamSourceSong
                        ));
            } catch (InterruptedException | ExecutionException ex) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(new ResponseObject(
                                HttpStatus.BAD_REQUEST.value(),
                                "Failure",
                                ""
                        ));
            }

        }
    }

    @GetMapping("get/charts/song")
    public ResponseEntity<ResponseObject> getChartsSong(
            @RequestParam(name = "count") int n) {

        try {
            List<MultiSearchSong> songs = songRequestSer.getChartsSong(n);
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
                            "Success",
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
            @RequestParam(name = "id") String idArtist) {

        try {
            List<InfoSong> songs = songRequestSer
                    .getSongsOfArtist(idArtist);

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

}
