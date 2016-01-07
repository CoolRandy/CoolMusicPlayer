package com.coolrandy.com.coolmusicplayer.model;

import java.io.Serializable;

/**
 * Created by admin on 2016/1/7.
 */
public class AlbumTrack implements Serializable {

    private long id;
    private String name;
    private String url;
    private String image;
    private double rating;
    private String artist_name;

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {

        return "id: " + id + ", name: " + name + ", url: " + url
                + ", image: " + image + ", rating: " + rating
                + ", artist_name: " + artist_name;
    }
}
