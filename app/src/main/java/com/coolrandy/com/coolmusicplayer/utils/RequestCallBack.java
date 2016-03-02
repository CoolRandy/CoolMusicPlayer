package com.coolrandy.com.coolmusicplayer.utils;

import com.coolrandy.com.coolmusicplayer.model.AlbumBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randy on 2015/9/13.
 */
public interface RequestCallBack {

    public void onFail(Exception e);

//    public <T> void onSuccess(List<Class<T>> list);
    public void onSuccess(String response);
    public void onSuccess(Object response);
//    public <T> void onSuccess(ArrayList<T> objects);
}
