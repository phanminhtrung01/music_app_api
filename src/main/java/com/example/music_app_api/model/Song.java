package com.example.music_app_api.model;

import lombok.Data;

@Data
public class Song {
    private String title;
    private String artist;
    private String data;
    private String displayNameExt;
}
