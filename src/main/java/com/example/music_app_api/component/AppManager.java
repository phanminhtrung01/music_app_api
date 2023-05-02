package com.example.music_app_api.component;

import com.example.music_app_api.config.ConfigHttpClient;
import com.example.music_app_api.main_api.GetInfo;
import com.example.music_app_api.main_api.HostApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class AppManager {
    private final ConfigHttpClient configHttpClient;

    @Autowired
    public AppManager(ConfigHttpClient configHttpClient) {

        this.configHttpClient = configHttpClient;
    }

    public @NotNull List<BasicNameValuePair> generatePar(
            @NotNull URI uriMultiSearch,
            Map<String, String> mapKey) {

        final LocalDateTime now = LocalDateTime.now();
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final String dataTime = Long.toString(zonedDateTime.toInstant().toEpochMilli());
        final String ctime = dataTime.substring(0, dataTime.length() - 3);
        final String pathApi = uriMultiSearch.getPath();
        final Map<String, String> mapKeyBase = new HashMap<>(BusinessService.keyParameter);
        final LinkedHashMap<String, String> mapMainPar = new LinkedHashMap<>(mapKey);
        mapMainPar.put("ctime", ctime);
        mapMainPar.put("version", mapKeyBase.get("version"));
        final String apiKey = mapKeyBase.get("apiKey");
        final String hmacKey = mapKeyBase.get("hmacKey");
        final String sigKey;
        try {
            sigKey = GenerateSigKey.getHmac512(hmacKey, pathApi, mapMainPar);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        final List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>(
                mapMainPar.keySet().stream().map((key) ->
                        new BasicNameValuePair(key, mapMainPar.get(key))).toList());

        basicNameValuePairs.add(new BasicNameValuePair("sig", sigKey));
        basicNameValuePairs.add(new BasicNameValuePair("apiKey", apiKey));

        return basicNameValuePairs;
    }

    public JSONObject getDataRequest(
            URI uriHost,
            @NotNull String path,
            Map<String, String> mapKey,
            @NotNull Map<String, String> mapPar,
            boolean hasProxy, boolean hasSecurity)
            throws Exception {

        final URIBuilder uriBuild =
                new URIBuilder(uriHost).appendPath(path);
        final List<NameValuePair> valuePairs = new ArrayList<>();

        if (hasSecurity) {
            valuePairs.addAll(generatePar(uriBuild.build(), mapKey));
        }

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

}
