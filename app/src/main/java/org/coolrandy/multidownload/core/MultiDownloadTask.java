package org.coolrandy.multidownload.core;

import org.coolrandy.multidownload.DownloadInfo;
import org.coolrandy.multidownload.db.DatabaseManager;
import org.coolrandy.multidownload.db.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/1/13.
 */
public class MultiDownloadTask extends DownloadTaskImpl{

    private DatabaseManager databaseManager;

    public MultiDownloadTask(DownloadInfo downloadInfo, OnDownloadListener onDownloadListener, ThreadInfo threadInfo, DatabaseManager databaseManager) {
        super(downloadInfo, onDownloadListener, threadInfo);
        this.databaseManager = databaseManager;
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_PARTIAL;
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(offset);//从偏移处开始
        return raf;
    }

    /**
     * 重点是添加参数Range
     * @param info
     * @return
     */
    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        Map<String, String> headers = new HashMap<>();
        long start = info.getStart() + info.getFinished();
        long end = info.getEnd();
        headers.put("Range", "bytes=" + start + "-" + end);
        return headers;
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {

        //首先判断是否存在
        if(!databaseManager.exists(info.getTag(), info.getId())){
            databaseManager.insert(info);
        }
    }

    @Override
    protected void updateDB(ThreadInfo info) {

        databaseManager.update(info.getTag(), info.getId(), info.getFinished());
    }
}
