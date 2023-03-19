package com.example.music_app_api.config;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

@Configuration
public class ConfigJsoup {

    @Bean
    Proxy proxy() {
        return new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress("192.168.43.1", 10809)
        );
    }

    public Document jsoupConnectionNoCookies(String url) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).ignoreHttpErrors(true)
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            document = Jsoup.connect(url).proxy(proxy())
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true).get();
        }

        return document;
    }

    public Document jsoupConnectionCookies(
            String url, Map<String,
            String> cookies) throws IOException {
        Document document;
        try {
            document = Jsoup
                    .connect(url).cookies(cookies)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .get();
        } catch (Exception e) {
            document = Jsoup
                    .connect(url).proxy(proxy())
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .cookies(cookies).get();
        }

        return document;
    }


    public Connection.Response jsoupResponseCookies(String url, Map<String, String> cookies)
            throws IOException {
        Connection.Response response;
        try {
            response = Jsoup
                    .connect(url).cookies(cookies)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .execute();
        } catch (Exception e) {
            response = Jsoup
                    .connect(url).proxy(proxy())
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .cookies(cookies).execute();
        }

        return response;
    }

    public Connection.Response jsoupResponseNoCookies(String url)
            throws IOException {
        Connection.Response response;
        try {
            response = Jsoup.connect(url)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .execute();
        } catch (Exception e) {
            response = Jsoup
                    .connect(url).proxy(proxy())
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .execute();
        }

        return response;
    }

}
