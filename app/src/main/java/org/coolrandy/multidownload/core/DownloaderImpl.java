package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.DownloadConfig;
import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.DownloadInfo;
import org.coolrandy.multidownload.DownloadRequest;
import org.coolrandy.multidownload.architecture.ConnectTask;
import org.coolrandy.multidownload.architecture.DownloadResponse;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadTask;
import org.coolrandy.multidownload.architecture.Downloader;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by admin on 2016/1/12.
 * Download类的具体实现，即执行具体下载任务
 */
public class DownloaderImpl implements Downloader, ConnectTask.OnConnectListener, DownloadTask.OnDownloadListener {

    //记录当前的状态
    private int mStatus;
    //响应
    private DownloadResponse response;
    //请求
    private DownloadRequest request;
    //并发执行
    private Executor executor;
    //数据库管理实例

    //tag
    private String tag;
    //下载配置
    private DownloadConfig config;

    private OnDownloaderDestroyedListener destoryedListener;

    //连接任务
    private ConnectTask connectTask;

    //设置获取下载信息
    private DownloadInfo downloadInfo;

    //存放downloaderTask的列表
    private List<DownloadTask> downloadTasks;



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
