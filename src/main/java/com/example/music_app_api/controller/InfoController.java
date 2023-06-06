package com.example.music_app_api.controller;

import com.example.music_app_api.entity.PlaylistOnline;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.InfoSourceSong;
import com.example.music_app_api.service.song_request.InfoRequestService;
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
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/pmdv/src/get/info/")
public class InfoController {

    private final InfoRequestService infoRequestService;

    @Autowired
    public InfoController(
            InfoRequestService infoRequestService) {
        this.infoRequestService = infoRequestService;
    }

    @GetMapping("song")
    public ResponseEntity<ResponseObject> getInfoSong(
            @RequestParam(name = "id") String idSong) {

        try {
            final Optional<InfoSong> infoSong = infoRequestService
                    .getInfoSong(idSong);

            return infoSong.map(song -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            song
                    ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount song with ID: " + idSong,
                            null
                    )));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount song with ID: " + idSong,
                            null
                    ));
        }


    }

    @GetMapping("album")
    public ResponseEntity<ResponseObject> getInfoAlbum(
            @RequestParam(name = "id") String idAlbum) {

        try {
            final Optional<InfoAlbum> infoAlbum = infoRequestService
                    .getInfoAlbum(idAlbum);

            return infoAlbum.map(album -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            album
                    ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount album with ID: " + idAlbum,
                            null
                    )));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount album with ID: " + idAlbum,
                            null
                    ));
        }

    }

    @GetMapping("artist")
    public ResponseEntity<ResponseObject> getInfoArtist(
            @RequestParam(name = "id") String idArtist) {

        try {
            final Optional<InfoArtist> infoArtist = infoRequestService
                    .getInfoArtist(idArtist);
            return infoArtist.map(artist -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            artist
                    ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount artist with ID: " + idArtist,
                            null
                    )));
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount artist with ID: " + idArtist,
                            null
                    ));
        }
    }

    @GetMapping("genre")
    public ResponseEntity<ResponseObject> getInfoGenre(
            @RequestParam(name = "id") String idGenre) {

        try {
            final Optional<InfoGenre> infoGenre = infoRequestService
                    .getInfoGenre(idGenre);
            return infoGenre.map(genre -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            genre
                    ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount genre with ID: " + idGenre,
                            null
                    )));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            null
                    ));
        }
    }

    @GetMapping("source/song")
    public ResponseEntity<ResponseObject> getSourceSong(
            @RequestParam(name = "id") String id) {

        BasicNameValuePair valuePair = new BasicNameValuePair("id", id);
        Optional<InfoSourceSong> song = infoRequestService.getInfoSourceSong(valuePair);
        return song.map(infoSourceSong -> ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject(
                        HttpStatus.OK.value(),
                        "Success",
                        infoSourceSong
                ))).orElseGet(() -> ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new ResponseObject(
                        HttpStatus.BAD_REQUEST.value(),
                        "Failure",
                        null
                )));
    }

    @GetMapping("source/lyric")
    public ResponseEntity<ResponseObject> getSourceLyric(
            @RequestParam(name = "id") String idSong) {

        try {
            Optional<SourceLyric> sourceLyric = infoRequestService
                    .getSourceLyric(idSong);
            return sourceLyric.map(lyric -> ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            lyric
                    ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount lyric of song with ID: " + idSong,
                            null
                    )));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject(
                            HttpStatus.BAD_REQUEST.value(),
                            "Not fount lyric of song with ID: " + idSong,
                            null
                    ));
        }
    }

    @GetMapping("/banner")
    public ResponseEntity<ResponseObject> getBanner(
            @RequestParam(name = "count", required = false) Integer count) {
        try {
            if (count == null) {
                count = 10;
            }
            List<PlaylistOnline> banners = infoRequestService.getBanner(count);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            banners
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

    @GetMapping("/artist_hot")
    public ResponseEntity<ResponseObject> getArtistHot() {
        try {
            List<InfoArtist> infoArtists = infoRequestService.getArtistHot();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            infoArtists
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
