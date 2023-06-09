package com.example.music_app_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SongRequest {
    private String title;
    private String thumbnail;
    private int duration;
    private String sourceM4a;
    private String source128;
    private int source320;
    private int sourceLossless;
}
