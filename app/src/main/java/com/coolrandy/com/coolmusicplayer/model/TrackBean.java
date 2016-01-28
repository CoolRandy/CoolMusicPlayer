package com.coolrandy.com.coolmusicplayer.model;

import java.io.Serializable;

/**
 * Created by admin on 2016/1/22.
 * 单曲
 */
public class TrackBean implements Serializable {
    //track id
    private int id;
    //track name
    private String name;
    //曲目播放总时长
    private int duration;
    //track url
    private String url;
    //具体的音乐流
    private String stream;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "id-->" + id + ", name-->" + name + ", duration-->" + duration
                 + ", url-->" + url + ", stream-->" + stream;
    }
}
