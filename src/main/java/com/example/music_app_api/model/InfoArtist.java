package com.example.music_app_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InfoArtist {
    private String id;
    private String name;
    private String alias;
    @JsonProperty(value = "realname")
    private String realName;
    private String birthday;
    private Boolean spotlight;
    private String thumbnail;
    private String thumbnailM;
    private String sortBiography;
    private String biography;
    private String national;
    private String totalFollow;
    private String playlistId;
}
