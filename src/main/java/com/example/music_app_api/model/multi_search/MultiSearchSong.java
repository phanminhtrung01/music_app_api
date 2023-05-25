package com.example.music_app_api.model.multi_search;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MultiSearchSong {
    @JsonAlias({"encodeId", "id"})
    private String id;
    private String title;
    @JsonAlias({"artists_names", "artistsNames"})
    private String artistsNames;
    @JsonAlias({"thumbnailM", "thumbnail"})
    private String thumbnail;
}
