package com.example.music_app_api.model.source_lyric;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class WordLyricSong {
    private int startTime;
    private int endTime;
    @JsonProperty(value = "data")
    private String word;
}
