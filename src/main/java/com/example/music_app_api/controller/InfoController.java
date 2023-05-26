package com.example.music_app_api.controller;

import com.example.music_app_api.model.*;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.SourceSong;
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
            final InfoSong infoSong = infoRequestService.getInfoSong(idSong);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            infoSong
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

    @GetMapping("album")
    public ResponseEntity<ResponseObject> getInfoAlbum(
            @RequestParam(name = "id") String idAlbum) {

        try {
            final InfoAlbum infoAlbum = infoRequestService
                    .getInfoAlbum(idAlbum);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            infoAlbum
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

    @GetMapping("artist")
    public ResponseEntity<ResponseObject> getInfoArtist(
            @RequestParam(name = "id") String idArtist) {

        try {
            final InfoArtist infoArtist = infoRequestService
                    .getInfoArtist(idArtist);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            infoArtist
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

    @GetMapping("genre")
    public ResponseEntity<ResponseObject> getInfoGenre(
            @RequestParam(name = "id") String idGenre) {

        try {
            final InfoGenre infoGenre = infoRequestService
                    .getInfoGenre(idGenre);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            infoGenre
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

    @GetMapping("source/song")
    public ResponseEntity<ResponseObject> getSourceSong(
            @RequestParam(name = "id") String id) {

        try {
            BasicNameValuePair valuePair = new BasicNameValuePair("id", id);
            SourceSong song = infoRequestService.getInfoSourceSong(valuePair);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            song
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

    @GetMapping("source/lyric")
    public ResponseEntity<ResponseObject> getSourceLyric(
            @RequestParam(name = "id") String idSong) {

        try {
            SourceLyric sourceLyric = infoRequestService
                    .getSourceLyric(idSong);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Success",
                            sourceLyric
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

    @GetMapping("/banner")
    public ResponseEntity<ResponseObject> getBanner() {
        try {
            List<Banner> banners = infoRequestService.getBanner();

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
