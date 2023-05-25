package com.example.music_app_api.model.source_song;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SourceSong {
    @JsonProperty("name")
    private String displayName;
    private String title;
    @JsonProperty("isoffical")
    private boolean isOfficial;
    @JsonProperty("artists_names")
    private String artistsNames;
    private String performer;
    private String lyric;
    private String thumbnail;
    private String source128;
}
