package org.coolrandy.multidownload.architecture;

/**
 * Created by admin on 2016/1/11.
 */
public interface Downloader {

    public interface OnDownloaderDestroyedListener{

        void onDestroyed(String key, Downloader downloader);
    }

    boolean isRunning();
    void start();
    void pause();
    void cancel();
    void onDestory();
}
