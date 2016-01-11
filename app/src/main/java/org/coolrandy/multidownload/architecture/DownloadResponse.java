package org.coolrandy.multidownload.architecture;

import org.coolrandy.multidownload.DownloadException;

/**
 * Created by admin on 2016/1/11.
 * 下载响应接口  回调
 * 包含各种连接状态以及下载状态
 */
public interface DownloadResponse {

    //connect
    void onStarted();

    void onConnecting();

    void onConnected(long time, long length, boolean acceptRanges);

    void onConnectFailed(DownloadException de);

    void onConnectCanceled();

    //download
    void onDownloadProgress(long finished, long length, int percent);

    void onDownloadCompleted();

    void onDownloadPaused();

    void onDownloadCanceled();

    void onDownloadFailed(DownloadException de);
}
