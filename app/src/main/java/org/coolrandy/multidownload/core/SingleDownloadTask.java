package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.Constants;
import org.coolrandy.multidownload.DownloadInfo;
import org.coolrandy.multidownload.db.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by admin on 2016/1/13.
 */
public class SingleDownloadTask extends DownloadTaskImpl {

    public SingleDownloadTask(DownloadInfo downloadInfo, OnDownloadListener onDownloadListener, ThreadInfo threadInfo) {
        super(downloadInfo, onDownloadListener, threadInfo);
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        //不支持
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        //不许重写
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        return null;
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(0);//从起始开始
        return raf;
    }
}
