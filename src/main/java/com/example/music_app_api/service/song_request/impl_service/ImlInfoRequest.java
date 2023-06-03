package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.enums.SearchField;
import com.example.music_app_api.component.enums.TypeParameter;
import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.Genre;
import com.example.music_app_api.entity.Lyric;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import com.example.music_app_api.main_api.SearchSong;
import com.example.music_app_api.model.Banner;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.InfoSourceSong;
import com.example.music_app_api.service.database_server.ArtistService;
import com.example.music_app_api.service.database_server.GenreService;
import com.example.music_app_api.service.database_server.LyricService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.song_request.InfoRequestService;
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
import java.util.Optional;

@Slf4j
@Service
public class ImlInfoRequest implements InfoRequestService {

    private final AppManager appManager;
    private final SongService songService;
    private final ArtistService artistService;
    private final GenreService genreService;
    private final LyricService lyricService;

    @Autowired
    ImlInfoRequest(
            AppManager appManager,
            SongService songService,
            ArtistService artistService,
            GenreService genreService,
            LyricService lyricService) {
        this.appManager = appManager;
        this.songService = songService;
        this.artistService = artistService;
        this.genreService = genreService;
        this.lyricService = lyricService;
    }

    @Override
    public Optional<InfoSong> getInfoSong(@NotNull String idSong) {
        InfoSong infoSong;
        ObjectMapper mapper = new ObjectMapper();
        if (idSong.startsWith("S")) {
            Song song = songService.getById(idSong);
            List<Artist> artists = songService.getArtistByIdSong(idSong);
            List<Genre> genres = songService.getGenresByIdSong(idSong);

            infoSong = mapper.convertValue(song, InfoSong.class);
            List<InfoArtist> infoArtists1 = mapper
                    .convertValue(artists, new TypeReference<>() {
                    });

            List<InfoGenre> infoGenres1 = mapper
                    .convertValue(genres, new TypeReference<>() {
                    });

            infoSong.setArtists(infoArtists1);
            infoSong.setIdGenres(infoGenres1
                    .stream()
                    .map(InfoGenre::getId)
                    .toList());

        } else {
            try {
                final JSONObject jsonData = appManager
                        .getDataRequest(
                                HostApi.uriHostApiV2,
                                GetInfo.infoSong,
                                Map.of("id", idSong),
                                Map.of(), false, true);

                infoSong = mapper
                        .readValue(jsonData.toString(), InfoSong.class);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (infoSong.isEmpty()) {
            return Optional.empty();
        } else {
            log.info(infoSong.toString());
            return Optional.of(infoSong);
        }
    }

    @Override
    public Optional<InfoSourceSong> getInfoSourceSong(
            @NotNull BasicNameValuePair basicNameValuePair) {

        InfoSourceSong song;
        try {
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
            song = mapper
                    .readValue(jsonData.toString(), InfoSourceSong.class);
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

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (song.isEmpty()) {
            return Optional.empty();
        } else {
            log.info(song.toString());
            return Optional.of(song);
        }
    }

    @Override
    public Optional<InfoAlbum> getInfoAlbum(String idAlbum) {
        InfoAlbum infoAlbum;

        try {
            JSONObject jsonData = appManager
                    .getDataRequest(
                            HostApi.uriHostApiV2,
                            GetInfo.infoPlaylist,
                            Map.of("id", idAlbum),
                            Map.of(), false, true);

            ObjectMapper mapper = new ObjectMapper();
            infoAlbum = mapper
                    .readValue(jsonData.toString(), InfoAlbum.class);

            try {
                final JSONArray jsonArtists = jsonData.getJSONArray("artists");
                final List<InfoArtist> artists = mapper
                        .readValue(jsonArtists.toString(), new TypeReference<>() {
                        });

                infoAlbum.setArtists(artists);
            } catch (Exception ignore) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (infoAlbum.isEmpty()) {
            return Optional.empty();
        } else {
            log.info(infoAlbum.toString());
            return Optional.of(infoAlbum);
        }
    }

    @Override
    public Optional<InfoArtist> getInfoArtist(@NotNull String idArtist) {

        final ObjectMapper mapper = new ObjectMapper();
        if (idArtist.startsWith("A")) {
            Artist artist = artistService.getArtist(idArtist);
            InfoArtist infoArtist = mapper.convertValue(artist, InfoArtist.class);

            log.info(infoArtist.toString());
            return Optional.of(infoArtist);
        } else {
            try {
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

                final InfoArtist infoArtist = mapper
                        .readValue(jsonData.toString(), InfoArtist.class);
                final JSONArray itemSections = jsonData.getJSONArray("sections");

                itemSections.forEach(itemSection -> {
                    JSONObject jsonObject = (JSONObject) itemSection;
                    if (jsonObject.getString("sectionType")
                            .equals(SearchField.song.name())) {
                        final JSONArray itemSong = jsonObject.getJSONArray("items");
                        List<InfoSong> infoSongs = null;
                        try {
                            infoSongs = mapper.readValue(itemSong.toString(),
                                    new TypeReference<>() {
                                    });
                        } catch (Exception ignore) {

                        }

                        infoArtist.setSongs(infoSongs);
                    }

                    List<InfoAlbum> infoAlbums = new ArrayList<>();
                    if (jsonObject.getString("sectionType")
                            .equals(SearchField.playlist.name())) {
                        final String sectionType = jsonObject.getString("sectionType");
                        final JSONArray itemAlbum = jsonObject.getJSONArray("items");
                        if (sectionType.equals(SearchField.playlist.name())) {
                            List<InfoAlbum> infoAlbumsT;
                            try {
                                infoAlbumsT = mapper.readValue(itemAlbum.toString(),
                                        new TypeReference<>() {
                                        });
                                infoAlbums.addAll(infoAlbumsT);
                            } catch (Exception ignore) {
                            }

                        }
                    }
                    if (infoArtist.getAlbums() == null) {
                        infoArtist.setAlbums(infoAlbums);
                    }
                    infoArtist.getAlbums().addAll(infoAlbums);
                });

                log.info(infoArtist.toString());
                return Optional.of(infoArtist);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public Optional<InfoGenre> getInfoGenre(@NotNull String idGenre) {

        final ObjectMapper mapper = new ObjectMapper();
        if (idGenre.startsWith("G")) {
            Genre genre = genreService.getGenreById(idGenre);
            InfoGenre infoGenre = mapper.convertValue(genre, InfoGenre.class);

            log.info(infoGenre.toString());

            return Optional.of(infoGenre);
        } else {
            try {
                final JSONObject jsonData = appManager
                        .getDataRequest(
                                HostApi.uriHostApiV2,
                                GetInfo.infoGenre,
                                Map.of("id", idGenre),
                                Map.of(), false, true);

                final InfoGenre infoGenre = mapper
                        .readValue(jsonData.toString(), InfoGenre.class);

                JSONObject jsonParent = jsonData.getJSONObject("parent");
                JSONArray jsonChildren = jsonData.getJSONArray("childs");
                InfoGenre parent = mapper.readValue(jsonParent.toString(), InfoGenre.class);
                List<InfoGenre> children = mapper
                        .readValue(jsonChildren.toString(), new TypeReference<>() {
                        });

                infoGenre.setParent(parent);
                infoGenre.setChildren(children);

                log.info(infoGenre.toString());

                return Optional.of(infoGenre);
            } catch (Exception ignore) {
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<SourceLyric> getSourceLyric(@NotNull String idSong) {

        final ObjectMapper mapper = new ObjectMapper();
        if (idSong.startsWith("S")) {
            Lyric lyric = lyricService.getLyricByIdSong(idSong);
            SourceLyric sourceLyric = mapper.convertValue(lyric, SourceLyric.class);

            log.info(sourceLyric.toString());

            return Optional.of(sourceLyric);
        } else {
            try {
                final JSONObject jsonData = appManager
                        .getDataRequest(
                                HostApi.uriHostApiV2,
                                GetInfo.infoLyric,
                                Map.of("id", idSong),
                                Map.of(), false, true);

                final JSONObject jsonData2 = appManager
                        .getDataRequest(
                                HostApi.uriHostApiPhone,
                                GetInfo.lyric,
                                Map.of(),
                                Map.of("media_id", idSong),
                                false, false);

                JSONArray jsonArray = jsonData2.getJSONArray("data");
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                final SourceLyric sourceLyric = mapper
                        .readValue(jsonData.toString(), SourceLyric.class);

                final SourceLyric sourceLyric2 = mapper
                        .readValue(jsonObject.toString(), SourceLyric.class);

                sourceLyric.setContent(sourceLyric2.getContent());

                log.info(sourceLyric.toString());

                return Optional.of(sourceLyric);
            } catch (Exception ignore) {
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Banner> getBanner() {
        List<Banner> banners = new ArrayList<>();

        try {
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
        } catch (Exception ignore) {
        }

        return banners.stream()
                .filter(banner -> banner.getType() == 1)
                .toList();
    }

    @Override
    public List<InfoArtist> getArtistHot() {
        List<InfoArtist> infoArtists = new ArrayList<>();
        try {
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
                        List<InfoArtist> temp;
                        try {
                            temp = mapper.readValue(jsonArtists.toString(), new TypeReference<>() {
                            });
                            infoArtists.addAll(temp);
                        } catch (Exception ignore) {
                        }
                    });
                    break;
                }
            }
        } catch (Exception ignore) {
        }

        return infoArtists;
    }
}
