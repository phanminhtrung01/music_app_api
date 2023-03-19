package com.example.music_app_api.model.source_song;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InfoSong {
    @JsonAlias({"encodeId", "id"})
    private String id;
    private String title;
    private String artistsNames;
    private String thumbnail;
    private String thumbnailM;
    private String duration;
    private int releaseDate;
    @JsonProperty(value = "genreIds")
    private List<String> idGenres;
    private List<String> idArtists;
    private String idAlbum;
}
