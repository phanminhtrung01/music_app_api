package com.example.music_app_api.model.hot_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HotSearchKeyword {
    private String keyword;
}
