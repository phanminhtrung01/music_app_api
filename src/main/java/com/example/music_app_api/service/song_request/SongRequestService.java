package com.example.music_app_api.service.song_request;

import com.example.music_app_api.model.InfoAlbum;
import com.example.music_app_api.model.hot_search.HotSearch;
import com.example.music_app_api.model.multi_search.MultiSearch;
import com.example.music_app_api.model.source_song.InfoSong;
import com.example.music_app_api.model.source_song.StreamSourceSong;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.List;

public interface SongRequestService {
    HotSearch searchHotSongs(String data);

    MultiSearch searchMulti(String data, int count);

    MultiSearch searchMultiSongs(String data, int count);

    MultiSearch searchMultiArtists(String data, int count);

    StreamSourceSong getStreamSong(String idSong);

    StreamSourceSong getStreamSongN(BasicNameValuePair valuePair);

    List<InfoSong> getRecommendSongs(String idSong);

    List<InfoAlbum> getAlbumsOfGenre(String idGenre);

    List<InfoSong> getSongsOfArtist(String idArtist, int count);

    List<InfoSong> getSongsOfAlbum(String idAlbum) throws Exception;

    List<InfoSong> getSongNewRelease();


}
