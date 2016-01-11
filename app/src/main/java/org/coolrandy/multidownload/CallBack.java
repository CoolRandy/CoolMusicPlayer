package org.coolrandy.multidownload;

/**
 * Created by admin on 2016/1/11.
 * 下载状态的回调
 */
public interface CallBack {

    void onStarted();

    /**
     *
     */
    void onConnecting();

    void onConnected();


}
