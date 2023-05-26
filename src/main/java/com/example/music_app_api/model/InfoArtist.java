package com.example.music_app_api.model;

import com.example.music_app_api.model.source_song.InfoSong;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InfoArtist {
    @JsonAlias({"encodeId", "id"})
    private String id;
    private String name;
    private String alias;
    @JsonProperty(value = "realname")
    private String realName;
    private List<InfoSong> songs;
    private List<InfoAlbum> albums;
    private String birthday;
    private Boolean spotlight;
    private String thumbnail;
    private String thumbnailM;
    private String sortBiography;
    private String biography;
    private String national;
    private String playlistId;
}
