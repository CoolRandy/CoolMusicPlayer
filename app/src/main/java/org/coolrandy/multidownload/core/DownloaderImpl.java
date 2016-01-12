package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.architecture.ConnectTask;
import org.coolrandy.multidownload.architecture.DownloadResponse;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadTask;
import org.coolrandy.multidownload.architecture.Downloader;

/**
 * Created by admin on 2016/1/12.
 * Download类的具体实现，即执行具体下载任务
 */
public class DownloaderImpl implements Downloader, ConnectTask.OnConnectListener, DownloadTask.OnDownloadListener {

    //记录当前的状态
    private int mStatus;
    //响应
    private DownloadResponse response;

    //正在运行:网络正在连接、已经连接、开始下载，下载进度
    @Override
    public boolean isRunning() {

        return mStatus == DownloadStatus.STATUS_CONNECTING
                || mStatus == DownloadStatus.STATUS_CONNECTED
                || mStatus == DownloadStatus.STATUS_STARTED
                || mStatus == DownloadStatus.STATUS_PROGRESS;
    }

    /**
     * 开始下载
     */
    @Override
    public void start() {
        //设置下载状态
        mStatus = DownloadStatus.STATUS_STARTED;
        response.onStarted();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void onDestory() {

    }

    @Override
    public void onConnectCanceled() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected(long time, long length, boolean isAcceptRanges) {

    }

    @Override
    public void onConnectFailed(DownloadException de) {

    }

    @Override
    public void onDownloadConnecting() {

    }


    @Override
    public void OnDownloadProgress(long finished, long length) {

    }

    @Override
    public void OnDownloadCanceled() {

    }

    @Override
    public void OnDownloadFailed(DownloadException de) {

    }

    @Override
    public void OnDownloadPaused() {

    }

    @Override
    public void OnDownloadFinished() {

    }
}
