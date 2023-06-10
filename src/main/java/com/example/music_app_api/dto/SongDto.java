package com.example.music_app_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SongDto implements Serializable {
    private String idSong;
    private String title;
    private String artistsNames;
    private String thumbnail;
    private String thumbnailM;
    private int duration;
    private Long releaseDate;
}