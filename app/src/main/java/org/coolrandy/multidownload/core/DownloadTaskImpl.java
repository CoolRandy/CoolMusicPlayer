package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.architecture.DownloadTask;

/**
 * Created by admin on 2016/1/11.
 * DownloadTask的具体实现
 */
public abstract class DownloadTaskImpl implements DownloadTask {

    @Override
    public boolean isDownloading() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public void run() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void pause() {

    }
}
