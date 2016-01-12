package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.CallBack;
import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.architecture.DownloadResponse;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadStatusDelivery;

/**
 * Created by admin on 2016/1/12.
 * 下载响应的具体实现类：包含对网络连接以及下载状态的回调
 */
public class DownloadResponseImpl implements DownloadResponse {

    private DownloadStatus downloadStatus;
    private DownloadStatusDelivery downloadStatusDelivery;

    public DownloadResponseImpl(DownloadStatusDelivery downloadStatusDelivery, CallBack callBack) {
        this.downloadStatusDelivery = downloadStatusDelivery;
        downloadStatus = new DownloadStatus();
        downloadStatus.setCallBack(callBack);
    }

    @Override
    public void onConnectCanceled() {

        downloadStatus.setStatus(DownloadStatus.STATUS_CANCELED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onConnected(long time, long length, boolean acceptRanges) {

        downloadStatus.setTime(time);
//        downloadStatus.setLength(length);
        downloadStatus.setAcceptRanges(acceptRanges);
        downloadStatus.setStatus(DownloadStatus.STATUS_CONNECTED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onConnectFailed(DownloadException de) {

        downloadStatus.setDe(de);
        downloadStatus.setStatus(DownloadStatus.STATUS_FAILED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onConnecting() {

        downloadStatus.setStatus(DownloadStatus.STATUS_CONNECTING);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onDownloadCanceled() {

        downloadStatus.setStatus(DownloadStatus.STATUS_CANCELED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onDownloadCompleted() {

        downloadStatus.setStatus(DownloadStatus.STATUS_COMPLETED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onDownloadFailed(DownloadException de) {

        downloadStatus.setDe(de);
        downloadStatus.setStatus(DownloadStatus.STATUS_FAILED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onDownloadPaused() {
        downloadStatus.setStatus(DownloadStatus.STATUS_PAUSED);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onDownloadProgress(long finished, long length, int percent) {
        downloadStatus.setFinished(finished);
        downloadStatus.setLength(length);
        downloadStatus.setPercent(percent);
        downloadStatus.setStatus(DownloadStatus.STATUS_PROGRESS);
        downloadStatusDelivery.post(downloadStatus);
    }

    @Override
    public void onStarted() {

        //已经开始，设置状态
        downloadStatus.setStatus(DownloadStatus.STATUS_STARTED);
        downloadStatus.getCallBack().onStarted();
    }
}
