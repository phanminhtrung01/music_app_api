package com.example.music_app_api.component;

import com.example.music_app_api.component.enums.KeyCookie;
import com.example.music_app_api.config.ConfigHttpClient;
import com.example.music_app_api.config.ConfigProperties;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AppManager {

    private final BusinessService businessService;
    private final ConfigProperties configProperties;
    private final ConfigHttpClient configHttpClient;
    private final CacheManager cacheManagerVer;
    private final CacheManager cacheManagerPar;

    @Autowired
    public AppManager(
            BusinessService businessService,
            ConfigProperties configProperties,
            ConfigHttpClient configHttpClient,
            @Qualifier("cacheMngVer") CacheManager cacheManagerVer,
            @Qualifier("cacheMngPar") CacheManager cacheManagerPar) {
        this.businessService = businessService;
        this.configProperties = configProperties;
        this.configHttpClient = configHttpClient;
        this.cacheManagerVer = cacheManagerVer;
        this.cacheManagerPar = cacheManagerPar;
    }

    public @NotNull List<BasicNameValuePair> generatePar(
            @NotNull URI uriMultiSearch,
            Map<String, String> mapKey) {

        final LocalDateTime now = LocalDateTime.now();
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final String dataTime = Long.toString(zonedDateTime.toInstant().toEpochMilli());
        final String ctime = dataTime.substring(0, dataTime.length() - 3);
        final String pathApi = uriMultiSearch.getPath();
        final String version = businessService.getVerAsync();
        final Map<String, String> mapKeyBase =
                new TreeMap<>(Map.of("ctime", ctime, "version", version));
        mapKeyBase.putAll(mapKey);
        mapKey = businessService.getParameterAsync();
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
            @NotNull Map<String, String> mapPar,
            boolean hasProxy)
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
        final String response = configHttpClient
                .setHasCookies(true)
                .sendRequest(RequestMethod.GET, uriData, hasProxy)
                .getResponse();

        System.out.println(hasProxy);

        final JSONObject jsonDoc = new JSONObject(response);

        JSONObject jsonData;
        try {
            jsonData = jsonDoc.getJSONObject("data");
        } catch (Exception e) {

            jsonData = new JSONObject(Map.of("data", jsonDoc));
            throw new JSONException(jsonData.toString());
        }

        return jsonData;
    }

    public String getResponseRequest(URI uriData) throws IOException {
        return configHttpClient
                .sendRequest(RequestMethod.GET, uriData, false)
                .getResponse();
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

            final String response = configHttpClient
                    .setHasCookies(true)
                    .sendRequest(RequestMethod.GET, uriHome, false)
                    .getResponse();

            final JSONObject jsonResponse = new JSONObject(response);
            final JSONObject jsonData = jsonResponse.getJSONObject("data");
            final String linkData = jsonData.getString("link");
            final URI linkSong = new URIBuilder(HostApi.uriHostApiOld)
                    .appendPath(linkData)
                    .build();

            final String responsePageSong = configHttpClient
                    .sendRequest(RequestMethod.GET, linkSong, false)
                    .getResponse();

            //data-xml="/media/get-source?type=audio&
            // key=kHcmyZkdLNBdBDlTGTbHkHtkCshnkBbFc" data-id="ZZCEOICC"
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


    public Cookie[] loadDataCookies() {
        List<Cookie> cookies = new ArrayList<>();
        try {
            HttpClientContext clientContext = configHttpClient
                    .sendRequest(
                            RequestMethod.GET,
                            HostApi.uriHostApiNew,
                            false)
                    .getClientContext();

            cookies.addAll(clientContext.getCookieStore().getCookies());
            BasicClientCookie cookie = new BasicClientCookie(
                    KeyCookie.zmp3_sid.name(), configProperties.getCookieExp()
            );
            cookies.removeIf(cookie1 -> cookie1.getValue().isEmpty());
            Cookie cookieTemp = cookies
                    .stream()
                    .filter(cookie1 -> cookie1
                            .getName()
                            .equals("zmp3_app_version.1"))
                    .toList()
                    .stream()
                    .findFirst()
                    .orElse(new BasicClientCookie("", ""));

            cookie.setDomain(cookieTemp.getDomain());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setExpiryDate(cookieTemp.getExpiryInstant());

            cookies.add(cookie);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return cookies.toArray(new Cookie[3]);
    }

    @Cacheable(cacheNames = "cookies", cacheManager = "cacheMngCookie")
    public Cookie[] getCookiesCacheableMethod() {
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

}
