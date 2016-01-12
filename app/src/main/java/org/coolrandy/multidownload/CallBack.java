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

    /**
     *
     * @param total 文件的长度
     * @param isRangeSupport  是否支持从断点处恢复下载
     */
    void onConnected(long total, boolean isRangeSupport);

    /**
     *
     * @param finished 已下载的文件长度
     * @param total  全部的文件长度
     * @param progress 百分比进度
     */
    void onProgress(long finished, long total, int progress);

    void onCompleted();

    void onDownloadPaused();

    void onDownloadCanceled();

    void onFailed(DownloadException de);

}
