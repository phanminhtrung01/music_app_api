package com.example.music_app_api.main_api;

public class SearchSong {
    public static final String hotSearch = "ac-suggestions";
    public static final String multiSearch = "search/multi";
    public static final String multiSearchN = "tim-kiem/bai-hat";
    public static final String getSource = "xhr/media/get-source";
    public static final String getChart = "xhr/chart-realtime";
    public static final String streamSource = "song/get/streaming";
    public static final String recommendSong = "recommend/get/songs";
    //    public static final String recommendKeyWord = "app/get/recommend-keyword";
    //?id[type=artist&page=1&count=0&sort=listen&sectionId=aSongs]
    public static final String getSongsOfArtist = "song/get/list";
    //?id=6B88W0AB
    public static final String infoPagePlaylist = "page/get/playlist";
    //?page=1&count=30
    public static final String getSongNewRelease = "page/get/home";
    //q=huong%20giang&type=song&page=1&count=18
    public static final String search = "search";
}
