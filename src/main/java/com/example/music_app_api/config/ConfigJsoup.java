package com.example.music_app_api.config;

import com.example.music_app_api.component.AppManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ConfigJsoup {
    private final AppManager appManager;
    private final CacheManager cacheManager;

    @Autowired
    @Lazy
    public ConfigJsoup(
            AppManager appManager,
            @Qualifier("cacheMngCookie")
            CacheManager cacheManager) {
        this.appManager = appManager;
        this.cacheManager = cacheManager;
    }

    @Bean
    Proxy proxy() {
        return new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress("192.168.43.1", 10809)
        );
    }

    public Document jsoupConnectionNoCookies(String url) throws IOException {

        return Jsoup.connect(url).proxy(proxy())
                .ignoreHttpErrors(true)
                .ignoreContentType(true).get();
    }

    public Document jsoupConnectionCookies(String url)
            throws IOException {

        return Jsoup
                .connect(url).proxy(proxy())
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .cookies(appManager
                        .getCookiesCacheableMethod()).get();
    }


    public Connection.Response jsoupResponseCookies(String url)
            throws IOException {

        return Jsoup
                .connect(url).proxy(proxy())
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .cookies(appManager
                        .getCookiesCacheableMethod()).execute();
    }

    public Connection.Response jsoupResponseNoCookies(String url)
            throws IOException {

        return Jsoup
                .connect(url).proxy(proxy())
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .execute();
    }

    @Scheduled(fixedRate = 45, timeUnit = TimeUnit.MINUTES)
    protected void reloadCookies() {
        Cache cache = cacheManager.getCache("cookies");
        if (cache != null) {
            cache.clear();
            appManager.getCookiesCacheableMethod();
        }
    }
}
