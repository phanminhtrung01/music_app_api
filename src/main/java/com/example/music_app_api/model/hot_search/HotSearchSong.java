package com.example.music_app_api.model.hot_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HotSearchSong {
    private String id;
    private String title;
    private String artist;
    @JsonProperty("thumb")
    private String thumbnail;
    private String downloadTypes;
}
