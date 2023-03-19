package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.BusinessService;
import com.example.music_app_api.component.enums.SearchField;
import com.example.music_app_api.component.enums.TypeParameter;
import com.example.music_app_api.component.enums.TypeSuggestion;
import com.example.music_app_api.config.ConfigJsoup;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import com.example.music_app_api.main_api.SearchSong;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.hot_search.HotSearchKeyword;
import com.example.music_app_api.model.hot_search.HotSearchSong;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.multi_search.MultiSearchSong;
import com.example.music_app_api.model.multi_search.TrackSong;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.SourceSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import com.example.music_app_api.service.song_request.SongRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImlSongRequestService implements SongRequestService {

    ConfigJsoup configJsoup;
    ImlInfoRequest infoRequest;

    @Autowired
    ImlSongRequestService(
            ConfigJsoup configJsoup,
            ImlInfoRequest infoRequest) {
        this.configJsoup = configJsoup;
        this.infoRequest = infoRequest;
    }

    @Override
    public HotSearch searchHotSongs(String data) throws Exception {
        final HotSearch hotSearch = new HotSearch();
        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("query", data));
        nameValuePairs.add(new BasicNameValuePair("language", "vi"));
        final URI uriHotSearch;

        uriHotSearch = new URIBuilder(HostApi.uriHostApiV1)
                .appendPath(SearchSong.hotSearch)
                .addParameters(nameValuePairs)
                .build();

        final Document document = configJsoup
                .jsoupConnectionNoCookies(uriHotSearch.toString());

        final JSONObject jsonResponse = new JSONObject(document.body().text());
        final JSONObject jsonData = jsonResponse.getJSONObject("data");
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

        final JSONObject jsonObjectSuggestions = jsonItems.getJSONObject(1);
        final JSONArray jsonArraySuggestions = jsonObjectSuggestions
                .getJSONArray("suggestions");

        final List<HotSearchSong> songs = mapper.readValue(
                jsonArraySuggestions.toString(),
                new TypeReference<>() {
                }
        );

        final int limitedSearchSong = 2;
        final int sizeLoopSongs = Math.min(limitedSearchSong, songs.size());
        final List<HotSearchSong> hotSearchSongs = new ArrayList<>();
        for (int i = 0; i < sizeLoopSongs; i++) {
            final StringBuilder artistBuilder = new StringBuilder();
            final JSONObject jsonArtist = jsonArraySuggestions.getJSONObject(i);
            final int typeSuggestion = jsonArtist.getInt("type");
            if (typeSuggestion == TypeSuggestion.map.get("song")) {
                final JSONArray jsonArtists = jsonArtist.getJSONArray("artists");
                for (int j = 0; j < jsonArtists.length(); j++) {
                    final String artist = jsonArtists.getJSONObject(j).get("name").toString();
                    artistBuilder.append(artist);
                    if (j != jsonArtists.length() - 1) {
                        artistBuilder.append(", ");
                    }
                }
                final HotSearchSong hotSearchSong = songs.get(i);
                hotSearchSong.setArtist(artistBuilder.toString());
                hotSearchSongs.add(hotSearchSong);
            }
        }

        hotSearch.setSongs(hotSearchSongs);

        log.info(hotSearch.toString());

        return hotSearch;
    }

    @Override
    public MultiSearch searchHMultiSongsZ(String data) throws Exception {
        final MultiSearch multiSearch = new MultiSearch();

        URI uriMultiSearch;
        uriMultiSearch = new URIBuilder(HostApi.uriHostApiV2)
                .appendPath(SearchSong.multiSearch)
                .build();

        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        final List<BasicNameValuePair> valuePairs = BusinessService
                .generatePar(uriMultiSearch, Map.of());

        nameValuePairs.add(new BasicNameValuePair("q", data));
        nameValuePairs.addAll(valuePairs);

        uriMultiSearch = new URIBuilder(uriMultiSearch)
                .addParameters(nameValuePairs)
                .build();

        Document document = configJsoup
                .jsoupConnectionCookies(
                        uriMultiSearch.toString(),
                        BusinessService.cookiesDefault);

        final JSONObject jsonResponse =
                new JSONObject(document.body().text());
        final MultiSearchSong songTop;
        final JSONObject jsonData = jsonResponse.getJSONObject("data");

        final ObjectMapper mapper = new ObjectMapper();

        final JSONObject jsonTopSong = jsonData.getJSONObject("top");
        final String objectType = jsonTopSong.getString("objectType");
        if (Objects.equals(objectType, "song")) {
            songTop = mapper
                    .readValue(jsonTopSong.toString(), MultiSearchSong.class);

            multiSearch.setTopSong(songTop);
        }

        final JSONArray jsonSongs = jsonData.getJSONArray("songs");

        final List<MultiSearchSong> songs = mapper
                .readValue(jsonSongs.toString(), new TypeReference<>() {
                });

        multiSearch.setSongs(songs);

        log.info(multiSearch.toString());
        return multiSearch;
    }

    @Override
    public StreamSourceSong getStreamSong(String idSong) throws Exception {

        URI uri = new URIBuilder(HostApi.uriHostApiV2)
                .appendPath(SearchSong.streamSource)
                .build();

        final List<BasicNameValuePair> valuePairs = BusinessService
                .generatePar(uri, Map.of("id", idSong));
        final List<NameValuePair> nameValuePairs = new ArrayList<>(valuePairs);

        uri = new URIBuilder(uri)
                .addParameters(nameValuePairs)
                .build();

        final Document document = configJsoup
                .jsoupConnectionCookies(
                        uri.toString(),
                        BusinessService.cookiesDefault
                );

        final JSONObject responseJson = new JSONObject(document.body().text());
        final JSONObject dataJson = responseJson.getJSONObject("data");

        final ObjectMapper mapper = new ObjectMapper();
        final StreamSourceSong sourceSong = mapper
                .readValue(dataJson.toString(), StreamSourceSong.class);

        log.info(sourceSong.toString());

        return sourceSong;
    }

    @Override
    public StreamSourceSong getStreamSongN(
            @NotNull BasicNameValuePair valuePair)
            throws Exception {
        URI uriMultiSearch = new URIBuilder(HostApi.uriHostApiV2)
                .appendPath(GetInfo.infoSong)
                .build();

        String data;
        Document document;
        if (valuePair.getName().equals(TypeParameter.id.name())) {
            final List<BasicNameValuePair> valuePairs = BusinessService
                    .generatePar(uriMultiSearch, Map.of("id", valuePair.getValue()));

            final List<NameValuePair> nameValuePairs = new ArrayList<>(valuePairs);

            uriMultiSearch = new URIBuilder(uriMultiSearch)
                    .addParameters(nameValuePairs)
                    .build();

            document = configJsoup.jsoupConnectionCookies(
                    uriMultiSearch.toString(),
                    BusinessService.cookiesDefault
            );

            final JSONObject jsonResponse = new JSONObject(document.body().text());
            final JSONObject jsonData = jsonResponse.getJSONObject("data");
            data = jsonData.getString("alias");

        } else {
            SourceSong song = infoRequest.getInfoSourceSong(valuePair);
            data = song.getTitle() + " " + song.getArtistsNames();
        }

        final BasicNameValuePair nameValuePair =
                new BasicNameValuePair("q", data);

        uriMultiSearch = new URIBuilder(HostApi.uriHostN)
                .appendPath(SearchSong.multiSearchN)
                .setParameters(nameValuePair)
                .build();

        //https://www.nhaccuatui.com/bai-hat/
        // em-dong-y-i-do-duc-phuc-ft-911.oO4E4ILYta9n.html
        document = configJsoup
                .jsoupConnectionNoCookies(uriMultiSearch.toString());
        final Element element = document.selectFirst(
                "body > div:nth-child(9) " +
                        "> div > div > div.box-left " +
                        "> div.sn_search_returns_frame " +
                        "> ul > li:nth-child(2) > a");

        assert element != null;
        final String linkSong = element.attr("href");

        document = configJsoup.jsoupConnectionNoCookies(linkSong);
        //player.peConfig.xmlURL =
        // "https://www.nhaccuatui.com/flash/xml?
        // html5=true&key1=93f3963ebd88a5a7923007f645a2257f"
        final String regex = "player.peConfig.xmlURL = \"(.*?)\"";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(document.toString());

        String linkSource = "";
        if (matcher.find(46000)) {
            linkSource = matcher.group(1);
        }

        final JAXBContext jaxbContext = JAXBContext.newInstance(TrackSong.class);

        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        final TrackSong trackSong = (TrackSong) unmarshaller.unmarshal(new URL(linkSource));

        final String source128 = trackSong.getSourceSong().getUri128().trim();
        final String source320 = trackSong.getSourceSong().getUri320().trim();

        trackSong.getSourceSong().setUri128(source128);
        trackSong.getSourceSong().setUri320(source320);

        log.info(trackSong.getSourceSong().toString());

        return trackSong.getSourceSong();
    }

    @Override
    public List<MultiSearchSong> getChartsSong(int count)
            throws Exception {
        List<MultiSearchSong> songs;
        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        final BasicNameValuePair valuePair1 =
                new BasicNameValuePair("chart", SearchField.song.name());
        final BasicNameValuePair valuePair2 =
                new BasicNameValuePair("count", String.valueOf(count));
        nameValuePairs.add(valuePair1);
        nameValuePairs.add(valuePair2);
        final URI uriChart = new URIBuilder(HostApi.uriHostApiOld)
                .appendPath(SearchSong.getChart)
                .setParameters(nameValuePairs)
                .build();

        final Document document = configJsoup
                .jsoupConnectionNoCookies(uriChart.toString());

        final JSONObject responseJson = new JSONObject(document.body().text());
        final JSONObject dataJson = responseJson.getJSONObject("data");
        final JSONArray songJson = dataJson.getJSONArray("song");

        final ObjectMapper mapper = new ObjectMapper();
        songs = mapper.readValue(songJson.toString(), new TypeReference<>() {
        });
        log.info(songs.toString());

        return songs;
    }

    @Override
    public List<InfoSong> getRecommendSongs(String idSong)
            throws Exception {

        final JSONObject jsonData = BusinessService
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        SearchSong.recommendSong,
                        Map.of("id", idSong),
                        Map.of("historyIds", idSong,
                                "start", String.valueOf(0),
                                "count", String.valueOf(10)));

        final ObjectMapper mapper = new ObjectMapper();
        final JSONArray jsonItems = jsonData.getJSONArray("items");
        final List<InfoSong> infoSongs = mapper
                .readValue(jsonItems.toString(), new TypeReference<>() {
                });

        AtomicInteger i = new AtomicInteger();

        jsonItems.forEach(jsonItem -> {

            try {
                final JSONObject jsonAlbum = ((JSONObject) jsonItem)
                        .getJSONObject("album");
                infoSongs.get(i.get())
                        .setIdAlbum(jsonAlbum
                                .getString("encodeId"));
            } catch (Exception ignore) {
            }

            try {

                final JSONArray jsonArtists = ((JSONObject) jsonItem)
                        .getJSONArray("artists");
                final List<String> artists = new ArrayList<>();

                jsonArtists
                        .forEach(artist -> artists
                                .add(((JSONObject) artist)
                                        .getString("id")));

                infoSongs.get(i.get())
                        .setIdArtists(artists);
            } catch (Exception ignore) {
            }
            i.incrementAndGet();
        });

        log.info(infoSongs.toString());

        return infoSongs;
    }

    @Override
    public List<InfoAlbum> getAlbumsOfGenre(String idGenre)
            throws Exception {
        JSONObject jsonData = BusinessService
                .getDataRequest(
                        HostApi.uriHostApiV2,
                        SearchSong.getAlbumsOfGenre,
                        Map.of(TypeParameter.id.name(), String.valueOf(idGenre),
                                TypeParameter.type.name(), "genre",
                                TypeParameter.page.name(), String.valueOf(1),
                                TypeParameter.count.name(), String.valueOf(10)),
                        Map.of("sort", "listen"));

        final ObjectMapper mapper = new ObjectMapper();
        final JSONArray jsonItems = jsonData.getJSONArray("items");
        final List<InfoAlbum> infoAlbums = mapper
                .readValue(jsonItems.toString(), new TypeReference<>() {
                });

        AtomicInteger i = new AtomicInteger();
        jsonItems.forEach(item -> {
            try {
                final JSONArray jsonArtists = ((JSONObject) item)
                        .getJSONArray("artists");
                final List<String> artists = new ArrayList<>();
                jsonArtists
                        .forEach(artist -> artists
                                .add(((JSONObject) artist)
                                        .getString("id")));

                infoAlbums.get(i.get()).setIdArtists(artists);
            } catch (Exception ignore) {
            }
            i.incrementAndGet();
        });

        return null;
    }

    @Override
    public List<InfoSong> getSongsOfArtist(String idArtist)
            throws Exception {
        return null;
    }

    @Override
    public List<InfoSong> getSongsOfAlbum(String idAlbum)
            throws Exception {
        return null;
    }


}
