package com.example.music_app_api.model;

import com.example.music_app_api.model.source_song.InfoSong;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InfoAlbum {
    @JsonProperty(value = "encodeId")
    private String id;
    private String title;
    private String artistsNames;
    private String thumbnail;
    private String thumbnailM;
    private String sortDescription;
    private String releaseDate;
    @JsonProperty(value = "genreIds")
    private List<String> idGenres;
    private List<InfoArtist> artists;
    private List<InfoSong> songs;
    private List<InfoSong> sectionsSong;
}
