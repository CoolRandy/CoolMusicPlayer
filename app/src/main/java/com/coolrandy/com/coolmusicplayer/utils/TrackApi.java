package com.coolrandy.com.coolmusicplayer.utils;

/**
 * Created by admin on 2016/1/22.
 * 音乐api
 */
public class TrackApi {

    /**
     * 基地址
     */
    public static String BASE_API = "http://api.jamendo.com/get2/";
    public static final int TRACK_PER_PAGE = 10;

    /**
     * 专辑详情api  track
     */
    public static String ALBUM_INFO_URL = "http://api.jamendo.com/get2/id+name+duration+url+stream/track/jsonpretty/?album_id=";
}
