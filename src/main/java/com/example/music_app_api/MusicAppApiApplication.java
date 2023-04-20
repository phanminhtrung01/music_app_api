package com.example.music_app_api;

import com.example.music_app_api.config.ConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableConfigurationProperties(ConfigProperties.class)
public class MusicAppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicAppApiApplication.class, args);
    }
}
