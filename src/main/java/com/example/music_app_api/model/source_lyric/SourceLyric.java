package com.example.music_app_api.model.source_lyric;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SourceLyric {
    private String file;
    private List<WordsLyricSong> sentences;
    private List<String> defaultIBGUrls;
}
