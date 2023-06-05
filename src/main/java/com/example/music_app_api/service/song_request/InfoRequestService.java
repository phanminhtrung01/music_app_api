package com.example.music_app_api.service.song_request;

import com.example.music_app_api.entity.PlaylistOnline;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.InfoSourceSong;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;
import java.util.Optional;

public interface InfoRequestService {

    Optional<InfoSong> getInfoSong(String idSong);

    Optional<InfoSourceSong> getInfoSourceSong(
            BasicNameValuePair nameValuePair);

    Optional<InfoAlbum> getInfoAlbum(String idAlbum);

    Optional<InfoArtist> getInfoArtist(String idArtist);

    Optional<InfoGenre> getInfoGenre(String idGenre);

    Optional<SourceLyric> getSourceLyric(String idSong);

    List<PlaylistOnline> getBanner(int count);

    List<InfoArtist> getArtistHot();

}
