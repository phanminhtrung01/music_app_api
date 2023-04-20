package com.example.music_app_api.service.song_request.impl_service;

import com.example.music_app_api.model.source_song.InfoSong;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImlSongRequestService {
    void getIdAlbumIdArtist(
            JSONObject jsonSong,
            InfoSong infoSong) {

        try {
            final JSONObject jsonAlbum = jsonSong.getJSONObject("album");
            infoSong.setIdAlbum(jsonAlbum.getString("encodeId"));
        } catch (Exception ignore) {
        }

        try {
            final List<String> artists = new ArrayList<>();
            final JSONArray jsonArtists = jsonSong.getJSONArray("artists");
            jsonArtists
                    .forEach(artist -> artists
                            .add(((JSONObject) artist).getString("id")));
            infoSong.setIdArtists(artists);
        } catch (Exception ignore) {
        }

    }
}
