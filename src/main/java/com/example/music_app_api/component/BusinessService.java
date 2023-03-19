package com.example.music_app_api.component;

import com.example.music_app_api.component.enums.KeyCookie;
import com.example.music_app_api.config.ConfigJsoup;
import com.example.music_app_api.config.ConfigProperties;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BusinessService implements CommandLineRunner {
    private static ConfigJsoup configJsoup;
    private static ConfigProperties configProperties;
    private static String apiKey;
    private static String hmacKey;
    public static Map<String, String> cookiesDefault = new HashMap<>();

    @Autowired
    BusinessService(
            ConfigJsoup configJsoup,
            ConfigProperties configProperties) {
        BusinessService.configProperties = configProperties;
        BusinessService.configJsoup = configJsoup;
    }

    public static void refreshParameter() {

        try {

            URI uriHotSearch = new URIBuilder(HostApi.uriHostApiNew)
                    .build();

            Connection.Response response = configJsoup
                    .jsoupResponseNoCookies(uriHotSearch.toString());

            //https://zjs.zmdcdn.me/zmp3-desktop/releases/v1.8.27/static/js/main.min.js
            String regex = "([a-z]+:)+.*v(\\d+(.\\d{1,2})+(.\\d{1,2})).*\\.js";
            //regex = "https://zjs.zmdcdn.me/zmp3-desktop/releases/(.*?)/static/js/main.min.js";
            Pattern pattern = Pattern.compile(regex);

            String matcherResponse = response.body();
            Matcher matcher = pattern.matcher(matcherResponse);
            URI uriJsMain = new URI("");
            int start = 0;
            if (matcher.find(8000)) {
                uriJsMain = new URI(matcher.group());
                start = matcher.end();
            }

            //var o="X5BM3w8N7MKozC0B85o4KMlzLZKhV00y",s=n(32),u=n(2),
            // c=n(125),l=n(612),f=n.n(l),d=n(613),h=n.n(d),
            // p=["ctime","id","type","page","count","version"];
            response = configJsoup
                    .jsoupResponseNoCookies(uriJsMain.toString());

            matcherResponse = response.body();

            regex = "(var [a-z]=\")+([A-Za-z\\d]{32}).*\\[\"ctime\"" +
                    ",\"id\",\"type\",\"page\",\"count\",\"version\"];";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(matcherResponse);
            if (matcher.find(start)) {
                apiKey = matcher.group(2);
                start = matcher.end();
            }

            //return h()(e+r,"acOrvUS15XRW2o9JksiK1KgQ6Vbds8ZW")}
            regex = "(return [a-z]\\(\\))\\(([a-z]\\+[a-z])+.*([A-Za-z\\d]{32})\"\\)}";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(matcherResponse);
            if (matcher.find(start)) {
                hmacKey = matcher.group(3);
            }

            log.info("Success!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static String getVerAsync() {
        final URI uriHotSearch;
        String version = "";
        try {
            uriHotSearch = new URIBuilder(HostApi.uriHostApiNew)
                    .build();

            final Document document = configJsoup
                    .jsoupConnectionNoCookies(uriHotSearch.toString());
            //https://zjs.zmdcdn.me/zmp3-desktop/releases/v1.8.38/static/css/main.min.css
            final String regex = "([a-z]+:)+.*v(\\d+(.\\d{1,2})+(.\\d{1,2})).*\\.css";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(document.toString());
            if (matcher.find(4500)) {
                version = matcher.group(2);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return version;
    }

    public static void getDefaultCookies() {
        try {
            Connection.Response response = configJsoup
                    .jsoupResponseNoCookies(HostApi.uriHostApiNew.toString());

            cookiesDefault = response.cookies();
            cookiesDefault
                    .put(KeyCookie.zmp3_sid.name(), configProperties.getCookieExp());
            log.info("Success!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static @NotNull String getKeySong(String idSong) {

        URI uriHome;
        String keySong = "";
        try {
            uriHome = new URIBuilder(HostApi.uriHostApiV2)
                    .appendPath(GetInfo.infoSong)
                    .build();

            final List<BasicNameValuePair> valuePairs = BusinessService
                    .generatePar(uriHome, Map.of("id", idSong));

            final List<NameValuePair> nameValuePairs = new ArrayList<>(valuePairs);

            uriHome = new URIBuilder(uriHome)
                    .addParameters(nameValuePairs)
                    .build();

            final Document document = configJsoup
                    .jsoupConnectionCookies(uriHome.toString(), cookiesDefault);

            final JSONObject jsonResponse = new JSONObject(document.body().text());
            final JSONObject jsonData = jsonResponse.getJSONObject("data");
            final String linkData = jsonData.getString("link");
            final URI linkSong = new URIBuilder(HostApi.uriHostApiOld)
                    .appendPath(linkData)
                    .build();

            final String responsePageSong = Jsoup
                    .connect(linkSong.toString())
                    .get()
                    .toString();

            //data-xml="/media/get-source?type=audio&key=kHcmyZkdLNBdBDlTGTbHkHtkCshnkBbFc" data-id="ZZCEOICC"
            final String regex = "data-xml=.*key=(.*?)\" data-id=\"(.*?)\"";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(responsePageSong);

            if (matcher.find(36500)) {
                if (matcher.group(2).equals(idSong)) {
                    keySong = matcher.group(1);
                }
            }

            log.info(keySong);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return keySong;
    }

    public static @NotNull List<BasicNameValuePair> generatePar(
            @NotNull URI uriMultiSearch,
            Map<String, String> mapKey) {

        final String version = getVerAsync();
        final LocalDateTime now = LocalDateTime.now();
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final String dataTime = Long.toString(zonedDateTime.toInstant().toEpochMilli());
        final String ctime = dataTime.substring(0, dataTime.length() - 3);
        final String pathApi = uriMultiSearch.getPath();
        final Map<String, String> mapKeyBase =
                new TreeMap<>(Map.of("ctime", ctime, "version", version));
        mapKeyBase.putAll(mapKey);
        final String hmacKey = BusinessService.hmacKey;
        final String apiKey = BusinessService.apiKey;
        final String sigKey;
        try {
            sigKey = GenerateSigKey.getHmac512(hmacKey, pathApi, mapKeyBase);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        final List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>(
                mapKeyBase.keySet().stream().map((key) ->
                        new BasicNameValuePair(key, mapKeyBase.get(key))).toList());
        basicNameValuePairs.add(new BasicNameValuePair("sig", sigKey));
        basicNameValuePairs.add(new BasicNameValuePair("apiKey", apiKey));

        return basicNameValuePairs;
    }

    public static JSONObject getDataRequest(
            URI uriHost,
            @NotNull String path,
            Map<String, String> mapKey,
            @NotNull Map<String, String> mapPar)
            throws Exception {

        final URIBuilder uriBuild = new URIBuilder(uriHost)
                .appendPath(path);

        final List<NameValuePair> valuePairs =
                new ArrayList<>(BusinessService
                        .generatePar(uriBuild.build(), mapKey));
        valuePairs.addAll(mapPar
                .keySet()
                .stream()
                .map((par ->
                        new BasicNameValuePair(par, mapPar.get(par))))
                .toList());
        final URI uriData = uriBuild
                .addParameters(valuePairs).build();
        final Document document = configJsoup
                .jsoupConnectionCookies(
                        uriData.toString(),
                        BusinessService.cookiesDefault);
        final JSONObject jsonDoc = new JSONObject(document.body().text());

        return jsonDoc.getJSONObject("data");
    }


    @Override
    public void run(String... args) {
        int numberThread = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberThread);
        CompletableFuture.runAsync(BusinessService::refreshParameter, executorService);
        CompletableFuture.runAsync(BusinessService::getDefaultCookies, executorService);
    }
}
