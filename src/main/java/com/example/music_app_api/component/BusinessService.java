package com.example.music_app_api.component;

import com.example.music_app_api.component.enums.KeyCookie;
import com.example.music_app_api.config.ConfigHttpClient;
import com.example.music_app_api.config.ConfigProperties;
import com.example.music_app_api.main_api.HostApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BusinessService implements CommandLineRunner {
    private final ConfigHttpClient configHttpClient;
    private final ConfigProperties configProperties;
    public static Map<String, String> keyParameter;
    public static Map<String, Cookie[]> cookiesBase;

    @Autowired
    public BusinessService(
            ConfigHttpClient configHttpClient,
            ConfigProperties configProperties) {

        this.configHttpClient = configHttpClient;
        this.configProperties = configProperties;
    }

    public void loadDataParameter() {
        String apiKey = "";
        String hmacKey = "";
        String version = "";
        try {

            URI uriBase = new URIBuilder(HostApi.uriHostApiNew)
                    .build();

            String response = configHttpClient
                    .sendRequest(RequestMethod.GET, uriBase, false)
                    .getResponse();

            //https://zjs.zmdcdn.me/zmp3-desktop/releases/v1.8.38/static/css/main.min.css
            String regex = "([a-z]+:)+.*v(\\d+(.\\d{1,2})+(.\\d{1,2})).*\\.css";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find(4500)) {
                version = matcher.group(2);
            }

            //https://zjs.zmdcdn.me/zmp3-desktop/releases/v1.8.27/static/js/main.min.js
            regex = "([a-z]+:)+.*v(\\d+(.\\d{1,2})+(.\\d{1,2})).*\\.js";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(response);
            URI uriJsMain = new URI("");
            int start = 0;
            if (matcher.find(8000)) {
                uriJsMain = new URI(matcher.group());
                start = matcher.end();
            }

            //var o="X5BM3w8N7MKozC0B85o4KMlzLZKhV00y",s=n(32),u=n(2),
            // c=n(125),l=n(612),f=n.n(l),d=n(613),h=n.n(d),
            // p=["ctime","id","type","page","count","version"];
            response = configHttpClient
                    .sendRequest(RequestMethod.GET, uriJsMain, false)
                    .getResponse();

            regex = "(var [a-z]=\")+([A-Za-z\\d]{32}).*\\[\"ctime\"" +
                    ",\"id\",\"type\",\"page\",\"count\",\"version\"];";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(response);
            if (matcher.find(start)) {
                apiKey = matcher.group(2);
                start = matcher.end();
            }

            //return h()(e+r,"acOrvUS15XRW2o9JksiK1KgQ6Vbds8ZW")}
            regex = "(return [a-z]\\(\\))\\(([a-z]\\+[a-z])+.*([A-Za-z\\d]{32})\"\\)}";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(response);
            if (matcher.find(start)) {
                hmacKey = matcher.group(3);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        keyParameter = Map.of(
                "version", version,
                "apiKey", apiKey,
                "hmacKey", hmacKey);

        log.info("Success!");
    }

    public void loadDataCookies() {
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

        cookiesBase = Map.of("base", cookies.toArray(new Cookie[3]));

        log.info("Success!");
    }

    @Override
    public void run(String... args) {
        loadDataParameter();
        loadDataCookies();
    }
}
