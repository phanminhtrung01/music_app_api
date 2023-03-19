package com.example.music_app_api;

import com.example.music_app_api.config.ConfigHttpClient;
import com.example.music_app_api.config.ConfigJsoup;
import com.example.music_app_api.config.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class MusicAppApiApplicationTests {

    ConfigHttpClient client;
    ConfigJsoup configJsoup;
    ConfigProperties configProperties;

    @Autowired
    MusicAppApiApplicationTests(
            ConfigHttpClient client,
            ConfigJsoup configJsoup,
            ConfigProperties configProperties) {
        this.client = client;
        this.configJsoup = configJsoup;
        this.configProperties = configProperties;
    }

}
