package com.example.music_app_api.model.hot_search;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HotSearchSong {
    @JsonAlias({"id"})
    private String idSong;
    private String title;
    private String artistsNames;
    @JsonAlias({"thumb", "thumbnail"})
    private String thumbnail;
    private Integer type;
}
