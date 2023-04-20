package com.example.music_app_api.service.song_request;

import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.multi_search.MultiSearchSong;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

public interface SongRequestService {
    HotSearch searchHotSongs(String data) throws Exception;

    MultiSearch searchHMultiSongsZ(String data) throws Exception;

    StreamSourceSong getStreamSong(String idSong) throws Exception;

    StreamSourceSong getStreamSongN(BasicNameValuePair valuePair) throws Exception;

    List<MultiSearchSong> getChartsSong(int count) throws Exception;

    List<InfoSong> getRecommendSongs(String idSong) throws Exception;

    List<InfoAlbum> getAlbumsOfGenre(String idGenre) throws Exception;

    List<InfoSong> getSongsOfArtist(String idArtist) throws Exception;

    List<InfoSong> getSongsOfAlbum(String idAlbum) throws Exception;

    List<InfoSong> getSongNewRelease() throws Exception;
}
