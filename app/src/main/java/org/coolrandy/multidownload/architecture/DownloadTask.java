package org.coolrandy.multidownload.architecture;

import org.coolrandy.multidownload.DownloadException;

/**
 * Created by admin on 2016/1/11.  https://github.com/Aspsine/MultiThreadDownload/blob/master/library/src/main/java/com/aspsine/multithreaddownload/architecture/DownloadTask.java
 * 下载任务接口  继承自Runnable  实现子线程  也就是request回调
 * 包含多个回调：包含下载的状态  下载正在连接、下载已完成、下载暂停、下载取消、下载失败以及下载进度
 */
public interface DownloadTask extends Runnable {

    interface OnDownloadListener{

        void onDownloadConnecting();
        void OnDownloadFinished();
        void OnDownlaodPaused();
        void OnDownlaodCanceled();
        void OnDownloadProgress(long finished, long length);
        void OnDownlaodFailed(DownloadException de);
    }

    void cancel();
    void pause();

    boolean isDownloading();
    boolean isComplete();
    boolean isPaused();
    boolean isFinished();
    boolean isFailed();

    @Override
    void run();
}
