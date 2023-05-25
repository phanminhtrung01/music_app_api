package com.example.music_app_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class InfoGenre {
    private String id;
    private String name;
    private String title;
    private String alias;
    private InfoGenre parent;
    private List<InfoGenre> children;
}
