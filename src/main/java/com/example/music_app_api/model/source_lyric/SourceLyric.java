package com.example.music_app_api.model.source_lyric;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SourceLyric {
    private String file;
    private List<WordsLyricSong> sentences;
    private List<String> defaultIBGUrls;
}
