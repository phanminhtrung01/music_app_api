package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.enums.SearchField;
import com.example.music_app_api.component.enums.TypeParameter;
import com.example.music_app_api.entity.Artist;
import com.example.music_app_api.entity.Genre;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import com.example.music_app_api.main_api.SearchSong;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.hot_search.HotSearchKeyword;
import com.example.music_app_api.model.hot_search.HotSearchSong;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.multi_search.TrackSong;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.InfoSourceSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import com.example.music_app_api.service.database_server.ArtistService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.song_request.SongRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImlSongRequest implements SongRequestService {

    private final ImlInfoRequest infoRequest;
    private final AppManager appManager;
    private final SongService songService;
    private final ArtistService artistService;


    @Autowired
    ImlSongRequest(
            ImlInfoRequest infoRequest,
            AppManager appManager,
            SongService songService,
            ArtistService artistService) {
        this.infoRequest = infoRequest;
        this.appManager = appManager;
        this.songService = songService;
        this.artistService = artistService;
    }

    @Override
    public HotSearch searchHotSongs(String data) {
        final HotSearch hotSearch = new HotSearch();
        try {
            final JSONObject jsonData = appManager.getDataRequest(
                    HostApi.uriHostApiV1,
                    SearchSong.hotSearch,
                    Map.of(), Map.of("query", data,
                            "language", "vi"),
                    false, false);
            final JSONArray jsonItems = jsonData.getJSONArray("items");
            final JSONObject jsonObjectKeywords = jsonItems.getJSONObject(0);

            final ObjectMapper mapper = new ObjectMapper();

            final int limitedSearchWord = 3;
            List<HotSearchKeyword> keywords = new ArrayList<>(
                    mapper.readValue(
                            jsonObjectKeywords.get("keywords").toString(),
                            new TypeReference<>() {
                            }
                    )
            );

            keywords = keywords
                    .subList(0, Math.min(keywords.size(), limitedSearchWord));

            hotSearch.setKeywords(keywords);

            CompletableFuture<List<HotSearchSong>> completableFutureOn = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return getHotSearchSongsOn(jsonItems);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

            CompletableFuture<List<HotSearchSong>> completableFutureOff = CompletableFuture
                    .supplyAsync(() -> getHotSearchSongsOff(data));

            List<HotSearchSong> songsOff = completableFutureOff.join();
            List<HotSearchSong> songsOn = completableFutureOn.join();
            List<HotSearchSong> songs = new ArrayList<>();
            songs.addAll(songsOff);
            songs.addAll(songsOn);
            hotSearch.setSongs(songs);

            log.info(hotSearch.toString());
        } catch (Exception ignore) {
        }

        return hotSearch;
    }

    @NotNull
    private List<HotSearchSong> getHotSearchSongsOn(
            @NotNull JSONArray jsonItems)
            throws JsonProcessingException {
        List<HotSearchSong> songs;
        final ObjectMapper mapper = new ObjectMapper();
        if (jsonItems.length() > 1) {
            final JSONObject jsonObjectSuggestions = jsonItems.getJSONObject(1);
            final JSONArray jsonArraySuggestions = jsonObjectSuggestions
                    .getJSONArray("suggestions");

            songs = mapper.readValue(
                    jsonArraySuggestions.toString(),
                    new TypeReference<>() {
                    }
            );

            for (int i = 0; i < jsonArraySuggestions.length(); i++) {
                JSONObject jsonSuggestion = (JSONObject) jsonArraySuggestions.get(i);
                JSONArray artistsJson = jsonSuggestion.getJSONArray("artists");
                StringBuilder artistName = new StringBuilder();
                for (int j = 0; j < artistsJson.length(); j++) {
                    JSONObject artistJson = (JSONObject) artistsJson.get(j);
                    artistName.append(artistJson.getString("name"));
                }
                songs.get(i).setArtistsNames(artistName.toString());
            }

            final int limitedSearchSong = 2;
            final int sizeLoopSongs = Math.min(limitedSearchSong, songs.size());
            songs = songs.stream()
                    .filter(song -> song.getType() == 1)
                    .toList()
                    .subList(0, sizeLoopSongs);
        } else {
            songs = new ArrayList<>();
        }
        return songs;
    }

    @NotNull
    private List<HotSearchSong> getHotSearchSongsOff(String query) {
        List<HotSearchSong> hotSearchSongs;
        final ObjectMapper mapper = new ObjectMapper();
        List<Song> songs = songService.getSongsByTitle(query, 5);
        hotSearchSongs = mapper.convertValue(songs, new TypeReference<>() {
        });
        int minSelection = Math.min(hotSearchSongs.size(), 3);
        return hotSearchSongs.subList(0, minSelection);
    }

    @Override
    public MultiSearch searchMulti(String data, int count) {
        final MultiSearch multiSearch = new MultiSearch();

        CompletableFuture<List<InfoSong>> futureSong = CompletableFuture
                .supplyAsync(() -> searchMultiSongs(data, count).getSongs());
        CompletableFuture<List<InfoArtist>> futureArtist = CompletableFuture
                .supplyAsync(() -> searchMultiArtists(data, count).getArtists());

        final List<InfoSong> songs = futureSong.join();
        final List<InfoArtist> artists = futureArtist.join();

        multiSearch.setSongs(songs);
        multiSearch.setArtists(artists);

        log.info(multiSearch.toString());
        return multiSearch;
    }

    @Override
    public MultiSearch searchMultiSongs(String data, int count) {
        final MultiSearch multiSearch = new MultiSearch();

        CompletableFuture<List<InfoSong>> futureOn = CompletableFuture
                .supplyAsync(() -> getSongsOn(data, count));
        CompletableFuture<List<InfoSong>> futureOff = CompletableFuture
                .supplyAsync(() -> getSongsOff(data, count));
        final List<InfoSong> songsOn = futureOn.join();
        final List<InfoSong> songsOff = futureOff.join();
        final List<InfoSong> songs = new ArrayList<>();
        songs.addAll(songsOff);
        songs.addAll(songsOn);

        multiSearch.setSongs(songs);

        log.info(multiSearch.toString());
        return multiSearch;
    }

    @Contract(pure = true)
    private @NotNull List<InfoSong> getSongsOff(String data, int count) {
        List<InfoSong> infoSongs;
        final ObjectMapper mapper = new ObjectMapper();
        List<Song> songs = songService.getSongsByTitle(data, count);
        List<List<InfoArtist>> infoArtists = new ArrayList<>();
        List<List<InfoGenre>> infoGenres = new ArrayList<>();
        songs = songs.stream().peek(song -> {
            List<Artist> artists = songService.getArtistByIdSong(song.getIdSong());
            List<Genre> genres = songService.getGenresByIdSong(song.getIdSong());

            List<InfoArtist> infoArtists1 = mapper
                    .convertValue(artists, new TypeReference<>() {
                    });

            List<InfoGenre> infoGenres1 = mapper
                    .convertValue(genres, new TypeReference<>() {
                    });
            infoArtists.add(infoArtists1);
            infoGenres.add(infoGenres1);
        }).toList();
        infoSongs = mapper.convertValue(songs, new TypeReference<>() {
        });

        for (int i = 0; i < infoSongs.size(); i++) {
            infoSongs.get(i).setArtists(infoArtists.get(i));
            infoSongs.get(i).setIdGenres(
                    infoGenres
                            .get(i).stream()
                            .map(InfoGenre::getId)
                            .toList());
        }

        int minSelection = Math.min(infoSongs.size(), 5);
        return infoSongs.subList(0, minSelection);
    }

    private List<InfoSong> getSongsOn(String data, int count) {
        List<InfoSong> infoSongs = new ArrayList<>();

        try {
            final JSONObject jsonData = appManager.getDataRequest(
                    HostApi.uriHostApiV2,
                    SearchSong.search,
                    Map.of("type", "song",
                            "page", String.valueOf(count / 18 + 1),
                            "count", String.valueOf(18)),
                    Map.of("q", data),
                    false, true);

            final ObjectMapper mapper = new ObjectMapper();


            try {
                final JSONArray jsonSongs = jsonData.getJSONArray("items");
                infoSongs = mapper
                        .readValue(jsonSongs.toString(), new TypeReference<>() {
                        });
            } catch (Exception ignore) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return infoSongs;
    }

    @Override
    public MultiSearch searchMultiArtists(String data, int count) {
        final MultiSearch multiSearch = new MultiSearch();

        CompletableFuture<List<InfoArtist>> futureOn = CompletableFuture
                .supplyAsync(() -> getArtistsOn(data, count));
        CompletableFuture<List<InfoArtist>> futureOff = CompletableFuture
                .supplyAsync(() -> getArtistsOff(data, count));
        final List<InfoArtist> artistsOn = futureOn.join();
        final List<InfoArtist> artistsOff = futureOff.join();
        final List<InfoArtist> artists = new ArrayList<>();
        artists.addAll(artistsOn);
        artists.addAll(artistsOff);

        multiSearch.setArtists(artists);

        log.info(multiSearch.toString());
        return multiSearch;
    }

    @Contract(pure = true)
    private @NotNull List<InfoArtist> getArtistsOff(String data, int count) {
        final ObjectMapper mapper = new ObjectMapper();
        List<Artist> artists = artistService
                .getArtistsByNameOrRealName(data, data, count);

        return mapper.convertValue(artists, new TypeReference<>() {
        });
    }

    private List<InfoArtist> getArtistsOn(String data, int count) {
        List<InfoArtist> infoArtists = new ArrayList<>();

        try {
            final JSONObject jsonData = appManager.getDataRequest(
                    HostApi.uriHostApiV2,
                    SearchSong.search,
                    Map.of("type", "artist",
                            "page", String.valueOf(count / 18 + 1),
                            "count", String.valueOf(18)),
                    Map.of("q", data),
                    false, true);

            final ObjectMapper mapper = new ObjectMapper();


            try {
                final JSONArray jsonSongs = jsonData.getJSONArray("items");
                infoArtists = mapper
                        .readValue(jsonSongs.toString(), new TypeReference<>() {
                        });
            } catch (Exception ignore) {
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return infoArtists;
    }

    @Override
    public StreamSourceSong getStreamSong(@NotNull String idSong) {
        StreamSourceSong streamSourceSong;
        if (idSong.startsWith("Z")) {
            streamSourceSong = getSourceSongOn(idSong);
        } else {
            streamSourceSong = getSourceSongOff(idSong);
        }
        log.info(streamSourceSong.toString());
        return streamSourceSong;
    }

    private StreamSourceSong getSourceSongOff(String idSong) {
        StreamSourceSong streamSourceSong;
        final ObjectMapper mapper = new ObjectMapper();
        SourceSong sourceSong = songService.getSourceSong(idSong);
        streamSourceSong = mapper.convertValue(sourceSong, StreamSourceSong.class);
        return streamSourceSong;
    }

    private StreamSourceSong getSourceSongOn(String idSong) {
        try {
            final JSONObject dataJson = appManager.getDataRequest(
                    HostApi.uriHostApiV2,
                    SearchSong.streamSource,
                    Map.of("id", idSong),
                    Map.of(), false, true);

            final ObjectMapper mapper = new ObjectMapper();
            if (dataJson.isEmpty()) {
                throw new RuntimeException(dataJson.toString());
            }
            return mapper
                    .readValue(dataJson.toString(), StreamSourceSong.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public StreamSourceSong getStreamSongN(
            @NotNull BasicNameValuePair valuePair) {

        String data;
        TrackSong trackSong = new TrackSong();
        try {
            if (valuePair.getName().equals(TypeParameter.id.name())) {
                final JSONObject jsonData = appManager.getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoSong,
                        Map.of(TypeParameter.id.name(), valuePair.getValue()),
                        Map.of(), false, true);
                data = jsonData.getString("alias");
            } else {
                Optional<InfoSourceSong> song = infoRequest.getInfoSourceSong(valuePair);
                if (song.isPresent()) {
                    data = song.get().getTitle() + " " +
                            song.get().getArtistsNames();
                } else {
                    throw new RuntimeException("");
                }

            }

            final BasicNameValuePair nameValuePair =
                    new BasicNameValuePair("q", data);

            final URI uriMultiSearch = new URIBuilder(HostApi.uriHostN)
                    .appendPath(SearchSong.multiSearchN)
                    .setParameters(nameValuePair)
                    .build();

            //https://www.nhaccuatui.com/bai-hat/
            // em-dong-y-i-do-duc-phuc-ft-911.oO4E4ILYta9n.html
            Document document = Jsoup
                    .connect(uriMultiSearch.toString())
                    .get();
            final Element element = document.selectFirst(
                    "body > div:nth-child(9) " +
                            "> div > div > div.box-left " +
                            "> div.sn_search_returns_frame " +
                            "> ul > li:nth-child(2) > a");

            assert element != null;
            final String linkSong = element.attr("href");

            final String response = appManager
                    .getResponseRequest(new URI(linkSong));
            //player.peConfig.xmlURL =
            // "https://www.nhaccuatui.com/flash/xml?
            // html5=true&key1=93f3963ebd88a5a7923007f645a2257f"
            final String regex = "player.peConfig.xmlURL = \"(.*?)\"";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(response);

            String linkSource = "";
            if (matcher.find()) {
                linkSource = matcher.group(1);
            }

            final JAXBContext jaxbContext = JAXBContext.newInstance(TrackSong.class);

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            trackSong = (TrackSong) unmarshaller.unmarshal(new URL(linkSource));

            final String source128 = trackSong.getSourceSong().getUri128().trim();
            final String source320 = trackSong.getSourceSong().getUri320().trim();

            trackSong.getSourceSong().setUri128(source128);
            trackSong.getSourceSong().setUri320(source320);

            log.info(trackSong.getSourceSong().toString());
        } catch (Exception ignore) {
        }

        return trackSong.getSourceSong();
    }

    @Override
    public List<InfoSong> getRecommendSongs(String idSong) {
        List<InfoSong> infoSongs = new ArrayList<>();
        try {
            final JSONObject jsonData = appManager
                    .getDataRequest(
                            HostApi.uriHostApiV2,
                            SearchSong.recommendSong,
                            Map.of("id", idSong),
                            Map.of("historyIds", idSong,
                                    "start", String.valueOf(0),
                                    "count", String.valueOf(10)),
                            false, true);

            final ObjectMapper mapper = new ObjectMapper();
            final JSONArray jsonItems = jsonData.getJSONArray("items");
            infoSongs = mapper
                    .readValue(jsonItems.toString(), new TypeReference<>() {
                    });

            log.info(infoSongs.toString());
        } catch (Exception ignore) {
        }
        return infoSongs;
    }

    @Override
    public List<InfoAlbum> getAlbumsOfGenre(String idGenre) {
        List<InfoAlbum> albums = new ArrayList<>();
        try {
            JSONObject jsonData = appManager
                    .getDataRequest(
                            HostApi.uriHostApiV2,
                            GetInfo.getAlbumsOfGenre,
                            Map.of(TypeParameter.id.name(), String.valueOf(idGenre),
                                    TypeParameter.type.name(), "genre",
                                    TypeParameter.page.name(), String.valueOf(1),
                                    TypeParameter.count.name(), String.valueOf(10)),
                            Map.of("sort", "listen"),
                            false, true);

            final ObjectMapper mapper = new ObjectMapper();
            final JSONArray jsonItems = jsonData.getJSONArray("items");
            albums = mapper
                    .readValue(jsonItems.toString(), new TypeReference<>() {
                    });
        } catch (Exception ignore) {
        }
        return albums;
    }

    @Override
    public List<InfoSong> getSongsOfArtist(String idArtist, int count) {
        List<InfoSong> infoSongs = new ArrayList<>();
        try {
            JSONObject jsonData = appManager
                    .getDataRequest(
                            HostApi.uriHostApiV2,
                            SearchSong.getSongsOfArtist,
                            Map.of(TypeParameter.id.name(), String.valueOf(idArtist),
                                    TypeParameter.type.name(), SearchField.artist.name(),
                                    TypeParameter.page.name(), String.valueOf(1),
                                    TypeParameter.count.name(), String.valueOf(count)),
                            Map.of(TypeParameter.sort.name(), "listen",
                                    TypeParameter.sectionId.name(), "aSongs"),
                            false, true);

            ObjectMapper mapper = new ObjectMapper();
            JSONArray itemSong = jsonData.getJSONArray("items");
            infoSongs = mapper
                    .readValue(itemSong.toString(), new TypeReference<>() {
                    });
        } catch (Exception ignore) {
        }

        return infoSongs;
    }

    @Override
    public List<InfoSong> getSongsOfAlbum(String idAlbum) {
        List<InfoSong> songs = new ArrayList<>();
        try {
            JSONObject jsonData = appManager
                    .getDataRequest(
                            HostApi.uriHostApiV2,
                            SearchSong.infoPagePlaylist,
                            Map.of(TypeParameter.id.name(), String.valueOf(idAlbum)),
                            Map.of(),
                            false, true);

            ObjectMapper mapper = new ObjectMapper();
            JSONArray sections = jsonData.getJSONArray("sections");
            JSONObject jsonSongs = jsonData.getJSONObject("song");
            JSONArray songItems = jsonSongs.getJSONArray("items");
            List<InfoSong> songMain = mapper.readValue(
                    songItems.toString(), new TypeReference<>() {
                    });
            songs.addAll(songMain);
            sections.forEach(section -> {
                JSONObject jsonSection = (JSONObject) section;
                String sectionType = jsonSection.getString("sectionType");

                if (sectionType.equals(SearchField.song.name())) {
                    JSONArray jsonArray = jsonSection.getJSONArray("items");

                    try {
                        List<InfoSong> infoSongs = mapper
                                .readValue(jsonArray.toString(), new TypeReference<>() {
                                });
                        songs.addAll(infoSongs);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception ignore) {
        }

        return songs;
    }

    @Override
    public List<InfoSong> getSongNewRelease() {
        List<InfoSong> infoSongs = new ArrayList<>();
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
            JSONArray jsonAllSong = new JSONArray();
            for (Object jsonItem : jsonItems) {
                JSONObject jsonContain = (JSONObject) jsonItem;
                String type = jsonContain.getString("sectionType");
                if (type.equals("new-release")) {
                    final JSONObject jsonSelect = jsonContain.getJSONObject("items");
                    jsonAllSong = jsonSelect.getJSONArray("vPop");
                    break;
                }
            }

            infoSongs = mapper
                    .readValue(jsonAllSong.toString(), new TypeReference<>() {
                    });

            log.info(infoSongs.toString());
        } catch (Exception ignore) {
        }

        return infoSongs;
    }

}
