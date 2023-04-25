package com.example.music_app_api.config;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.BusinessService;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.cookie.Cookie;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.Map;

@Slf4j
@Configuration
public class ConfigCache {

    private final AppManager appManager;
    private final BusinessService businessService;

    @Autowired
    @Lazy
    public ConfigCache(
            AppManager appManager,
            BusinessService businessService) {
        this.appManager = appManager;
        this.businessService = businessService;
    }

    @Bean
    public CacheLoader<Object, Object> cacheLoaderVersion() {
        return new CacheLoader<>() {
            @Override
            public @Nullable Object load(Object o) {
                return getVersionToLoadNewValue();
            }
        };
    }

    @Bean
    public CacheLoader<Object, Object> cacheLoaderCookies() {
        return new CacheLoader<>() {
            @Override
            public @Nullable Object load(Object o) {
                return getCookiesToLoadNewValue();
            }

            @Override
            public @Nullable Object reload(Object key, Object oldValue) throws Exception {
                return CacheLoader.super.reload(key, oldValue);
            }
        };
    }

    @Bean
    public CacheLoader<Object, Object> cacheLoaderParameter() {
        return new CacheLoader<>() {
            @Override
            public @Nullable Object load(Object o) {
                return getParameterToLoadNewValue();
            }
        };
    }

    @Bean
    public Caffeine<Object, Object> caffeineVersion() {
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumWeight(10)
                .weigher((key, value) -> value.toString().length());

    }

    @Bean
    public Caffeine<Object, Object> caffeineCookies() {
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumWeight(3)
                .weigher((key, value) -> ((Object[]) value).length);

    }

    @Bean
    public Caffeine<Object, Object> caffeineParameter() {
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumWeight(2)
                .weigher((key, value) -> ((Map<?, ?>) value).size());

    }

    @Bean(name = "cacheMngVer")
    CaffeineCacheManager cacheManagerVersion() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager("version");
        cacheManager.setCacheLoader(cacheLoaderVersion());
        cacheManager.setCaffeine(caffeineVersion());

        return cacheManager;
    }

    @Bean(name = "cacheMngCookie")
    @Primary
    CaffeineCacheManager cacheManagerCookies() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager("cookies");
        cacheManager.setCacheLoader(cacheLoaderCookies());
        cacheManager.setCaffeine(caffeineCookies());

        return cacheManager;
    }

    @Bean(name = "cacheMngPar")
    CaffeineCacheManager cacheManagerParameter() {
        CaffeineCacheManager cacheManager =
                new CaffeineCacheManager("parameter");
        cacheManager.setCacheLoader(cacheLoaderParameter());
        cacheManager.setCaffeine(caffeineParameter());

        return cacheManager;
    }

    private String getVersionToLoadNewValue() {
        log.info("Version: Loader");
        return businessService.loadDataVersion();
    }

    public Map<String, String> getParameterToLoadNewValue() {
        log.info("Parameter: Loader");
        return businessService.loadDataParameter();
    }

    public Cookie[] getCookiesToLoadNewValue() {
        log.info("Cookies: Loader");
        return appManager.loadDataCookies();
    }
}
