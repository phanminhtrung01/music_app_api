package com.example.music_app_api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pmdv")
public class ConfigProperties {
    final String cookieExp;
}
