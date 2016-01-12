package org.coolrandy.multidownload.architecture;

import org.coolrandy.multidownload.DownloadException;

/**
 * Created by admin on 2016/1/11.
 * 连接任务接口
 */
public interface ConnectTask extends Runnable {

    public interface OnConnectListener{

        void onConnecting();
        void onConnected(long time, long length, boolean isAcceptRanges);
        void onConnectCanceled();
        void onConnectFailed(DownloadException de);
    }

    void cancel();
    boolean isConnecting();
    boolean isConnected();
    boolean isCanceled();
    boolean isFailed();

    @Override
    void run();
}
