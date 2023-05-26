package com.example.music_app_api.service.song_request;

import com.example.music_app_api.model.Banner;
import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.InfoArtist;
import com.example.music_app_api.model.InfoGenre;
import com.example.music_app_api.model.source_lyric.SourceLyric;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.SourceSong;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

public interface InfoRequestService {

    InfoSong getInfoSong(String idSong) throws Exception;

    SourceSong getInfoSourceSong(BasicNameValuePair nameValuePair) throws Exception;

    InfoAlbum getInfoAlbum(String idAlbum) throws Exception;

    InfoArtist getInfoArtist(String idArtist) throws Exception;

    InfoGenre getInfoGenre(String idGenre) throws Exception;

    SourceLyric getSourceLyric(String idSong) throws Exception;

    List<Banner> getBanner() throws Exception;

    List<InfoArtist> getArtistHot() throws Exception;

}
