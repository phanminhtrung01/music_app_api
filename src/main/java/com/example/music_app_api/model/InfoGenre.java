package com.example.music_app_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class InfoGenre {
    private String id;
    private String name;
    private String title;
    private String alias;
    private String idParent;
    private List<String> idChildren;
}
