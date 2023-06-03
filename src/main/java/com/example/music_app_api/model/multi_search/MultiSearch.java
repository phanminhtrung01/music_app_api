package com.example.music_app_api.model.multi_search;

import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.source_song.InfoSong;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MultiSearch {
    private List<InfoSong> songs;
    private List<InfoArtist> artists;
}
