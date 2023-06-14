package com.example.music_app_api.mix_in;

import com.example.music_app_api.dto.SongDto;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public abstract class IgnoreSongMixIn {
    @JsonIgnore
    private SongDto song;
}