package com.example.music_app_api.config;

import com.example.music_app_api.component.AppManager;
import com.example.music_app_api.component.BusinessService;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

@Slf4j
@Configuration
public class ConfigCache {

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
                .weigher((key, value) -> ((Map<?, ?>) value).size());

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
        return BusinessService.loadDataVersion();
    }

    public Map<String, String> getParameterToLoadNewValue() {
        log.info("Parameter: Loader");
        return BusinessService.loadDataParameter();
    }

    public Map<String, String> getCookiesToLoadNewValue() {
        log.info("Cookies: Loader");
        return AppManager.loadDataCookies();
    }
}
