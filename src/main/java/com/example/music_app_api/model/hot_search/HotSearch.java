package com.example.music_app_api.model.hot_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HotSearch {
    private List<HotSearchKeyword> keywords;
    @JsonProperty(value = "suggestions")
    private List<HotSearchSong> songs;
}
