package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.enums.SearchField;
import com.example.music_app_api.component.enums.TypeParameter;
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
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ImlSongRequest implements SongRequestService {

    private final ImlInfoRequest infoRequest;
    private final AppManager appManager;

    @Autowired
    ImlSongRequest(
            ImlInfoRequest infoRequest,
            AppManager appManager) {
        this.infoRequest = infoRequest;
        this.appManager = appManager;
    }

    @Override
    public HotSearch searchHotSongs(String data) throws Exception {
        final HotSearch hotSearch = new HotSearch();
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

        final JSONObject jsonObjectSuggestions = jsonItems.getJSONObject(1);
        final JSONArray jsonArraySuggestions = jsonObjectSuggestions
                .getJSONArray("suggestions");

        List<HotSearchSong> songs = mapper.readValue(
                jsonArraySuggestions.toString(),
                new TypeReference<>() {
                }
        );

        final int limitedSearchSong = 2;
        final int sizeLoopSongs = Math.min(limitedSearchSong, songs.size());
        songs = songs.stream()
                .filter(song -> song.getType() == 1)
                .toList()
                .subList(0, sizeLoopSongs);

        hotSearch.setSongs(songs);

        log.info(hotSearch.toString());

        return hotSearch;
    }

    @Override
    public MultiSearch searchHMultiSongsZ(String data) throws Exception {
        final MultiSearch multiSearch = new MultiSearch();
        final MultiSearchSong songTop;
        final JSONObject jsonData = appManager.getDataRequest(
                HostApi.uriHostApiV2,
                SearchSong.multiSearch,
                Map.of(),
                Map.of("q", data),
                false, true);

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
    public StreamSourceSong getStreamSong(String idSong)
            throws Exception {
        final JSONObject dataJson = appManager.getDataRequest(
                HostApi.uriHostApiV2,
                SearchSong.streamSource,
                Map.of("id", idSong),
                Map.of(), false, true);

        final ObjectMapper mapper = new ObjectMapper();
        final StreamSourceSong sourceSong = mapper
                .readValue(dataJson.toString(), StreamSourceSong.class);

        log.info(sourceSong.toString());

        return sourceSong;
    }

    @Override
    public StreamSourceSong getStreamSongN(
            @NotNull BasicNameValuePair valuePair) {

        String data;
        TrackSong trackSong;
        try {
            if (valuePair.getName().equals(TypeParameter.id.name())) {
                final JSONObject jsonData = appManager.getDataRequest(
                        HostApi.uriHostApiV2,
                        GetInfo.infoSong,
                        Map.of(TypeParameter.id.name(), valuePair.getValue()),
                        Map.of(), false, true);
                data = jsonData.getString("alias");
            } else {
                SourceSong song = infoRequest.getInfoSourceSong(valuePair);
                data = song.getTitle() + " " + song.getArtistsNames();
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

        final String response = appManager.getResponseRequest(uriChart);

        final JSONObject responseJson = new JSONObject(response);
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
        final List<InfoSong> infoSongs = mapper
                .readValue(jsonItems.toString(), new TypeReference<>() {
                });

        log.info(infoSongs.toString());
        return infoSongs;
    }

    @Override
    public List<InfoAlbum> getAlbumsOfGenre(String idGenre)
            throws Exception {
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

        return mapper
                .readValue(jsonItems.toString(), new TypeReference<>() {
                });
    }

    @Override
    public List<InfoSong> getSongsOfArtist(String idArtist, int count)
            throws Exception {
        //type=artist&page=1&count=0&sort=listen&sectionId=aSongs
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

        return mapper
                .readValue(itemSong.toString(), new TypeReference<>() {
                });
    }

    @Override
    public List<InfoSong> getSongsOfAlbum(String idAlbum)
            throws Exception {
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
        List<InfoSong> songs = new ArrayList<>(songMain);
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

        return songs;
    }

    @Override
    public List<InfoSong> getSongNewRelease() throws Exception {

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

        List<InfoSong> infoSongs = mapper
                .readValue(jsonAllSong.toString(), new TypeReference<>() {
                });

        log.info(infoSongs.toString());
        return infoSongs;
    }

}
