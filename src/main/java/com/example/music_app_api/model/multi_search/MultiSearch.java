package com.example.music_app_api.model.multi_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MultiSearch {
    private List<MultiSearchSong> songs;
    @JsonProperty("top")
    private MultiSearchSong topSong;
}
