package org.coolrandy.multidownload.core;

import android.os.Handler;

import org.coolrandy.multidownload.CallBack;
import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadStatusDelivery;

import java.util.concurrent.Executor;

/**
 * Created by admin on 2016/1/12.
 * 下载状态犯法具体实现
 */
public class DownloadStatusDeliveryImpl implements DownloadStatusDelivery {

    private Executor mDownloadStausPoster;

    public DownloadStatusDeliveryImpl(final Handler handler) {

        mDownloadStausPoster = new Executor() {
            @Override
            public void execute(Runnable command) {

                handler.post(command);
            }
        };
    }

    @Override
    public void post(DownloadStatus status) {

        mDownloadStausPoster.execute(new DownloadStatusDeliveryRunnable(status));
    }

    private static class DownloadStatusDeliveryRunnable implements Runnable{

        private final DownloadStatus downloadStatus;
        private final CallBack callBack;

        public DownloadStatusDeliveryRunnable(DownloadStatus downloadStatus) {
            this.downloadStatus = downloadStatus;
            this.callBack = this.downloadStatus.getCallBack();
        }

        @Override
        public void run() {

            switch (downloadStatus.getStatus()){

                case DownloadStatus.STATUS_CONNECTING:
                    callBack.onConnecting();
                    break;
                case DownloadStatus.STATUS_CONNECTED:
                    callBack.onConnected(downloadStatus.getLength(), downloadStatus.isAcceptRanges());
                    break;
                case DownloadStatus.STATUS_STARTED:
                    callBack.onStarted();
                    break;
                case DownloadStatus.STATUS_PROGRESS:
                    callBack.onProgress(downloadStatus.getFinished(), downloadStatus.getLength(), downloadStatus.getPercent());
                    break;
                case DownloadStatus.STATUS_CANCELED:
                    callBack.onDownloadCanceled();
                    break;
                case DownloadStatus.STATUS_PAUSED:
                    callBack.onDownloadPaused();
                    break;
                case DownloadStatus.STATUS_COMPLETED:
                    callBack.onCompleted();
                    break;
                case DownloadStatus.STATUS_FAILED:
                    callBack.onFailed(downloadStatus.getDe());
                    break;

            }
        }
    }


}
