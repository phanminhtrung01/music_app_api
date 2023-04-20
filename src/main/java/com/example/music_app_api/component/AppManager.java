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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AppManager implements CommandLineRunner {

    private final BusinessService businessService;
    private static ConfigJsoup configJsoup;
    private static ConfigProperties configProperties;
    private final CacheManager cacheManagerVer;
    private final CacheManager cacheManagerPar;

    public AppManager(
            BusinessService businessService,
            ConfigJsoup configJsoup,
            ConfigProperties configProperties,
            @Qualifier("cacheMngVer") CacheManager cacheManagerVer,
            @Qualifier("cacheMngPar") CacheManager cacheManagerPar) {
        this.businessService = businessService;
        AppManager.configJsoup = configJsoup;
        AppManager.configProperties = configProperties;
        this.cacheManagerVer = cacheManagerVer;
        this.cacheManagerPar = cacheManagerPar;
    }

    public @NotNull List<BasicNameValuePair> generatePar(
            @NotNull URI uriMultiSearch,
            Map<String, String> mapKey)
            throws ExecutionException, InterruptedException {

        final int nThread = Runtime.getRuntime().availableProcessors();
        final ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        final CompletableFuture<Map<String, String>> completableFuturePar;
        final CompletableFuture<String> completableFutureVer;

        completableFuturePar = CompletableFuture
                .supplyAsync(businessService::getParameterAsync, executorService);
        completableFutureVer = CompletableFuture
                .supplyAsync(businessService::getVerAsync, executorService);

        final LocalDateTime now = LocalDateTime.now();
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final String dataTime = Long.toString(zonedDateTime.toInstant().toEpochMilli());
        final String ctime = dataTime.substring(0, dataTime.length() - 3);
        final String pathApi = uriMultiSearch.getPath();
        final String version = completableFutureVer.get();
        final Map<String, String> mapKeyBase =
                new TreeMap<>(Map.of("ctime", ctime, "version", version));
        mapKeyBase.putAll(mapKey);
        mapKey = completableFuturePar.get();
        final String hmacKey = mapKey.get("hmacKey");
        final String apiKey = mapKey.get("apiKey");
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

    public JSONObject getDataRequest(
            URI uriHost,
            @NotNull String path,
            Map<String, String> mapKey,
            @NotNull Map<String, String> mapPar)
            throws Exception {

        final URIBuilder uriBuild =
                new URIBuilder(uriHost).appendPath(path);
        final List<NameValuePair> valuePairs = new ArrayList<>(
                generatePar(uriBuild.build(), mapKey));
        valuePairs.addAll(mapPar
                .keySet()
                .stream()
                .map((par -> new BasicNameValuePair(par, mapPar.get(par))))
                .toList());
        final URI uriData = uriBuild.addParameters(valuePairs).build();
        final Document document = configJsoup
                .jsoupConnectionCookies(uriData.toString());
        final JSONObject jsonDoc = new JSONObject(document.body().text());

        JSONObject jsonData;
        try {
            jsonData = jsonDoc.getJSONObject("data");
        } catch (Exception e) {

            jsonData = new JSONObject(Map.of("data", jsonDoc));
            throw new JSONException(jsonData.toString());
        }

        return jsonData;
    }

    public @NotNull String getKeySong(String idSong) {
        URI uriHome;
        String keySong = "";
        try {
            uriHome = new URIBuilder(HostApi.uriHostApiV2)
                    .appendPath(GetInfo.infoSong)
                    .build();

            final List<BasicNameValuePair> valuePairs =
                    generatePar(uriHome, Map.of("id", idSong));

            final List<NameValuePair> nameValuePairs = new ArrayList<>(valuePairs);

            uriHome = new URIBuilder(uriHome)
                    .addParameters(nameValuePairs)
                    .build();

            final Document document = configJsoup
                    .jsoupConnectionCookies(uriHome.toString());

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


    public static Map<String, String> loadDataCookies() {
        Map<String, String> cookiesDefault = new HashMap<>();
        try {
            Connection.Response response = configJsoup.
                    jsoupResponseNoCookies(HostApi.uriHostApiNew.toString());

            cookiesDefault = response.cookies();
            cookiesDefault
                    .put(KeyCookie.zmp3_sid.name(), configProperties.getCookieExp());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return cookiesDefault;
    }

    @Cacheable(cacheNames = "cookies", cacheManager = "cacheMngCookie")
    public Map<String, String> getCookiesCacheableMethod() {
        log.info("Cookies: GetMain");
        return loadDataCookies();
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    protected void reloadVersion() {
        Cache cache = cacheManagerVer.getCache("version");
        if (cache != null) {
            cache.clear();
            businessService.getVerAsync();
        }
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS)
    protected void reloadParameter() {
        Cache cache = cacheManagerPar.getCache("parameter");
        if (cache != null) {
            cache.clear();
            businessService.getParameterAsync();
        }
    }

    @Override
    public void run(String... args) {
    }
}
