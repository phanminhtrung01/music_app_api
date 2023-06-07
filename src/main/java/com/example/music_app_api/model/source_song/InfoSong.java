package com.example.music_app_api.model.source_song;

import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InfoSong {
    @JsonAlias({"encodeId", "id", "idSong"})
    private String id;
    private String title;
    private String artistsNames;
    private String thumbnail;
    private String thumbnailM;
    private String duration;
    private Long releaseDate;
    @JsonAlias({"genreIds", ""})
    private List<String> idGenres;
    @JsonAlias({"artists", "artistsSing"})
    private List<InfoArtist> artists;
    private InfoAlbum album;

    @JsonIgnore
    public Boolean isEmpty() {
        return id == null || id.isEmpty();
    }
}
