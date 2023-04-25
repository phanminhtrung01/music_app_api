package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.enums.TypeParameter;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import com.example.music_app_api.main_api.SearchSong;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.SourceSong;
import com.example.music_app_api.service.song_request.InfoRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ImlInfoRequest
        extends ImlSongRequestService
        implements InfoRequestService {

    private final AppManager appManager;

    @Autowired
    ImlInfoRequest(
            AppManager appManager) {
        this.appManager = appManager;
    }

    @Override
    public InfoSong getInfoSong(String idSong) throws Exception {
        final JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoSong,
                        Map.of("id", idSong),
                        Map.of(), false);

        ObjectMapper mapper = new ObjectMapper();
        final InfoSong infoSong = mapper
                .readValue(jsonData.toString(), InfoSong.class);

        getIdAlbumIdArtist(jsonData, infoSong);

        log.info(infoSong.toString());
        return infoSong;
    }

    @Override
    public SourceSong getInfoSourceSong(
            @NotNull BasicNameValuePair basicNameValuePair)
            throws Exception {
        String keySong = basicNameValuePair.getValue();
        URI uriGetSource = new URIBuilder(HostApi.uriHostApiOld)
                .appendPath(SearchSong.getSource)
                .build();

        if (basicNameValuePair.getName()
                .equals(TypeParameter.id.name())) {
            keySong = appManager
                    .getKeySong(basicNameValuePair.getValue());
        }
        final BasicNameValuePair nameValuePair =
                new BasicNameValuePair("key", keySong);
        uriGetSource = new URIBuilder(uriGetSource)
                .setParameters(nameValuePair)
                .build();

        final String response = appManager.getResponseRequest(uriGetSource);

        final ObjectMapper mapper = new ObjectMapper();
        final JSONObject jsonResponse = new JSONObject(response);
        final JSONObject jsonData = jsonResponse.getJSONObject("data");
        final JSONObject jsonSource = jsonData.getJSONObject("source");
        final SourceSong song = mapper
                .readValue(jsonData.toString(), SourceSong.class);
        try {
            URI uriSourceSong = new URIBuilder()
                    .setScheme("https")
                    .appendPath(jsonSource.getString("128"))
                    .build();
            final String sourceSong = URLDecoder
                    .decode(uriSourceSong.toString(), StandardCharsets.UTF_8);
            song.setSource128(sourceSong);
        } catch (Exception ignore) {
        }

        log.info(song.toString());

        return song;
    }

    @Override
    public InfoAlbum getInfoAlbum(String idAlbum)
            throws Exception {

        JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoPlaylist,
                        Map.of("id", idAlbum),
                        Map.of(), false);

        ObjectMapper mapper = new ObjectMapper();
        final InfoAlbum infoAlbum = mapper
                .readValue(jsonData.toString(), InfoAlbum.class);

        try {
            final JSONArray jsonArtists = jsonData.getJSONArray("artists");
            final List<String> artists = new ArrayList<>();
            jsonArtists.forEach(artist -> artists
                    .add(((JSONObject) artist).getString("id")));
            infoAlbum.setIdArtists(artists);
        } catch (Exception ignore) {
        }

        log.info(infoAlbum.toString());

        return infoAlbum;
    }

    @Override
    public InfoArtist getInfoArtist(String idArtist) throws Exception {

        JSONObject jsonData;
        jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.artist,
                        Map.of("id", idArtist),
                        Map.of(), false);

        String aliasName = jsonData.getString("alias");

        jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoArtist,
                        Map.of(),
                        Map.of("alias", aliasName),
                        false);

        final ObjectMapper mapper = new ObjectMapper();

        final InfoArtist infoArtist = mapper
                .readValue(jsonData.toString(), InfoArtist.class);
        log.info(infoArtist.toString());

        return infoArtist;

    }

    @Override
    public InfoGenre getInfoGenre(String idGenre) throws Exception {
        final JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoGenre,
                        Map.of("id", idGenre),
                        Map.of(), false);

        final ObjectMapper mapper = new ObjectMapper();

        final InfoGenre infoGenre = mapper
                .readValue(jsonData.toString(), InfoGenre.class);

        try {
            JSONObject jsonParent = jsonData.getJSONObject("parent");
            JSONArray jsonChildren = jsonData.getJSONArray("childs");

            infoGenre.setIdParent(jsonParent.getString("id"));

            List<String> idChildren = new ArrayList<>();
            jsonChildren
                    .forEach(child -> idChildren.add(((JSONObject) child)
                            .getString("id")));
            infoGenre.setIdChildren(idChildren);
        } catch (Exception ignore) {
        }

        log.info(infoGenre.toString());

        return infoGenre;
    }

    @Override
    public SourceLyric getSourceLyric(String idSong) throws Exception {
        final JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoLyric,
                        Map.of("id", idSong),
                        Map.of(), false);

        final ObjectMapper mapper = new ObjectMapper();

        final SourceLyric sourceLyric = mapper
                .readValue(jsonData.toString(), SourceLyric.class);

        log.info(sourceLyric.toString());

        return sourceLyric;
    }


}
