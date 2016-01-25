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
     * 专辑详情api  track  对于每个track本身并没有提供封面图，只有所在专辑的封面图
     */
    public static String ALBUM_INFO_URL = BASE_API + "id+name+duration+url+stream/track/jsonpretty/?album_id=";




}
