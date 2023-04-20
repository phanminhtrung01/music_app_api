package com.example.music_app_api.component;

import com.example.music_app_api.config.ConfigJsoup;
import com.example.music_app_api.main_api.HostApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BusinessService {
    private static ConfigJsoup configJsoup;

    @Autowired
    public BusinessService(ConfigJsoup configJsoup) {
        BusinessService.configJsoup = configJsoup;
    }

    public static Map<String, String> loadDataParameter() {
        String apiKey = "";
        String hmacKey = "";
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
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Map.of("apiKey", apiKey, "hmacKey", hmacKey);
    }

    @Cacheable(cacheNames = "parameter", cacheManager = "cacheMngPar")
    public Map<String, String> getParameterAsync() {
        log.info("Parameter: GetMain");
        return loadDataParameter();
    }

    public static String loadDataVersion() {
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

    @Cacheable(cacheNames = "version", cacheManager = "cacheMngVer")
    public String getVerAsync() {
        log.info("Version: GetMain");
        return loadDataVersion();
    }
}
