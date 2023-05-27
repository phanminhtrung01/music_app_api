package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.enums.SearchField;
import com.example.music_app_api.component.enums.TypeParameter;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import com.example.music_app_api.main_api.SearchSong;
import com.example.music_app_api.model.Banner;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.SourceSong;
import com.example.music_app_api.service.song_request.InfoRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class ImlInfoRequest implements InfoRequestService {

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
                        Map.of(), false, true);

        ObjectMapper mapper = new ObjectMapper();
        final InfoSong infoSong = mapper
                .readValue(jsonData.toString(), InfoSong.class);

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
                        Map.of(), false, true);

        ObjectMapper mapper = new ObjectMapper();
        final InfoAlbum infoAlbum = mapper
                .readValue(jsonData.toString(), InfoAlbum.class);

        try {
            final JSONArray jsonArtists = jsonData.getJSONArray("artists");
            final List<InfoArtist> artists = mapper
                    .readValue(jsonArtists.toString(), new TypeReference<>() {
                    });

            infoAlbum.setArtists(artists);
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
                        Map.of(), false, true);

        String aliasName = jsonData.getString("alias");

        jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoArtist,
                        Map.of(),
                        Map.of("alias", aliasName),
                        false, true);

        final ObjectMapper mapper = new ObjectMapper();
        final InfoArtist infoArtist = mapper
                .readValue(jsonData.toString(), InfoArtist.class);
        final JSONArray itemSections = jsonData.getJSONArray("sections");

        itemSections.forEach(itemSection -> {
            JSONObject jsonObject = (JSONObject) itemSection;
            if (jsonObject.getString("sectionType")
                    .equals(SearchField.song.name())) {
                final JSONArray itemSong = jsonObject.getJSONArray("items");
                try {
                    List<InfoSong> infoSongs = mapper.readValue(itemSong.toString(),
                            new TypeReference<>() {
                            });

                    infoArtist.setSongs(infoSongs);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            List<InfoAlbum> infoAlbums = new ArrayList<>();
            if (jsonObject.getString("sectionType")
                    .equals(SearchField.playlist.name())) {
                final String sectionType = jsonObject.getString("sectionType");
                final JSONArray itemAlbum = jsonObject.getJSONArray("items");
                if (sectionType.equals(SearchField.playlist.name())) {
                    try {
                        List<InfoAlbum> infoAlbumsT = mapper.readValue(itemAlbum.toString(),
                                new TypeReference<>() {
                                });

                        infoAlbums.addAll(infoAlbumsT);

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (infoArtist.getAlbums() == null) {
                infoArtist.setAlbums(infoAlbums);
            }
            infoArtist.getAlbums().addAll(infoAlbums);
        });

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
                        Map.of(), false, true);

        final ObjectMapper mapper = new ObjectMapper();

        final InfoGenre infoGenre = mapper
                .readValue(jsonData.toString(), InfoGenre.class);

        try {
            JSONObject jsonParent = jsonData.getJSONObject("parent");
            JSONArray jsonChildren = jsonData.getJSONArray("childs");
            InfoGenre parent = mapper.readValue(jsonParent.toString(), InfoGenre.class);
            List<InfoGenre> children = mapper
                    .readValue(jsonChildren.toString(), new TypeReference<>() {
                    });

            infoGenre.setParent(parent);
            infoGenre.setChildren(children);
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
                        Map.of(), false, true);

        final ObjectMapper mapper = new ObjectMapper();

        final SourceLyric sourceLyric = mapper
                .readValue(jsonData.toString(), SourceLyric.class);

        log.info(sourceLyric.toString());

        return sourceLyric;
    }

    @Override
    public List<Banner> getBanner() throws Exception {
        List<Banner> banners = new ArrayList<>();

        JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        SearchSong.getSongNewRelease,
                        Map.of(TypeParameter.page.name(), String.valueOf(1),
                                TypeParameter.count.name(), String.valueOf(30)),
                        Map.of(),
                        false, true);

        final ObjectMapper mapper = new ObjectMapper();
        final JSONArray jsonItems = jsonData.getJSONArray("items");
        for (Object jsonItem : jsonItems) {
            JSONObject jsonContain = (JSONObject) jsonItem;
            String type = jsonContain.getString("sectionType");
            if (type.equals("banner")) {
                JSONArray jsonAllBanner = jsonContain.getJSONArray("items");
                banners = mapper
                        .readValue(
                                jsonAllBanner.toString(),
                                new TypeReference<>() {
                                });
                break;
            }
        }

        return banners.stream()
                .filter(banner -> banner.getType() == 1)
                .toList();
    }

    @Override
    public List<InfoArtist> getArtistHot() throws Exception {
        List<InfoArtist> infoArtists = new ArrayList<>();
        JSONObject jsonData = appManager
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        SearchSong.getSongNewRelease,
                        Map.of(TypeParameter.page.name(), String.valueOf(1),
                                TypeParameter.count.name(), String.valueOf(30)),
                        Map.of(),
                        false, true);

        final ObjectMapper mapper = new ObjectMapper();
        final JSONArray jsonItems = jsonData.getJSONArray("items");
        for (Object jsonItem : jsonItems) {
            JSONObject jsonContain = (JSONObject) jsonItem;

            String sectionId;
            try {
                sectionId = jsonContain.getString("sectionId");
            } catch (Exception ignore) {
                sectionId = "";
            }
            if (sectionId.equals("hArtistTheme")) {
                JSONArray jsonArrayArtist = jsonContain.getJSONArray("items");

                jsonArrayArtist.forEach(jsonArtist -> {
                    JSONObject jsonObject = (JSONObject) jsonArtist;
                    JSONArray jsonArtists = jsonObject.getJSONArray("artists");
                    try {
                        List<InfoArtist> temp = mapper.readValue(jsonArtists.toString(), new TypeReference<>() {
                        });

                        infoArtists.addAll(temp);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
                break;
            }


        }

        return infoArtists;
    }
}
