package org.coolrandy.multidownload.core;

import android.os.*;
import android.os.Process;
import android.text.TextUtils;

import org.coolrandy.multidownload.Constants;
import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.DownloadInfo;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadTask;
import org.coolrandy.multidownload.db.ThreadInfo;
import org.coolrandy.multidownload.utils.IOCloseUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by admin on 2016/1/11.
 * DownloadTask的具体实现
 */
public abstract class DownloadTaskImpl implements DownloadTask {

    private String tag;
    private final DownloadInfo downloadInfo;
    private final OnDownloadListener onDownloadListener;
    private final ThreadInfo threadInfo;

    private volatile int mStatus;
    private volatile int mCommend = 0;

    public DownloadTaskImpl(DownloadInfo downloadInfo, OnDownloadListener onDownloadListener, ThreadInfo threadInfo) {
        this.downloadInfo = downloadInfo;
        this.onDownloadListener = onDownloadListener;
        this.threadInfo = threadInfo;

        this.tag = getTag();
        if(TextUtils.isEmpty(tag)){
            tag = this.getClass().getSimpleName();
        }
    }

    @Override
    public boolean isDownloading() {
        return mStatus == DownloadStatus.STATUS_PROGRESS;
    }

    @Override
    public boolean isComplete() {
        return mStatus == DownloadStatus.STATUS_COMPLETED;
    }

    @Override
    public boolean isFailed() {
        return mStatus == DownloadStatus.STATUS_FAILED;
    }

    @Override
    public boolean isFinished() {
        return mStatus == DownloadStatus.STATUS_COMPLETED;
    }

    @Override
    public boolean isPaused() {
        return mStatus == DownloadStatus.STATUS_PAUSED;
    }

    @Override
    public void run() {

        //设置为后台线程
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        //将线程信息存入到数据库中
        insertIntoDB(threadInfo);
        try {
            mStatus = DownloadStatus.STATUS_PROGRESS;
            executeDownload();
            //下载完毕
            synchronized (onDownloadListener){
                mStatus = DownloadStatus.STATUS_COMPLETED;
                onDownloadListener.OnDownloadFinished();
            }
        }catch (DownloadException de){
            handleDownloadException(de);
        }

    }

    /**
     * 执行下载  这里前面的逻辑和执行连接很相似，后面需要对下载的数据进行存储到本地文件中
     */
    private void executeDownload() throws DownloadException{

        final URL url;
        try{
            url = new URL(threadInfo.getUri());
        }catch (MalformedURLException e){
            throw new DownloadException("Bad url.", e, DownloadStatus.STATUS_FAILED);
        }

        HttpURLConnection connection = null;
        try{
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(Constants.HTTP.CONNECT_TIME_OUT);
            connection.setReadTimeout(Constants.HTTP.READ_TIME_OUT);
            connection.setRequestMethod(Constants.HTTP.GET);
            //设置请求头
            setHttpHeaders(getHttpHeaders(threadInfo), connection);
            //获取响应码
            final int responseCode = connection.getResponseCode();
            if(getResponseCode() == responseCode){
                //存储数据到本地文件
                transforDataToLocal(connection);
            }else {
                throw new DownloadException("UnSupported response code:" + responseCode, DownloadStatus.STATUS_FAILED);
            }

        }catch (ProtocolException e){
            throw new DownloadException("Protocol error", e, DownloadStatus.STATUS_FAILED);
        } catch (IOException e){
            throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);
        }finally {
            if(connection != null){
                connection.disconnect();
            }
        }
    }

    /**
     * 传递数据到本地
     * @param connection
     */
    private void transforDataToLocal(HttpURLConnection connection) throws DownloadException{

        InputStream inputStream = null;
        RandomAccessFile raf = null;

        try {
            try {
                inputStream = connection.getInputStream();
            }catch (IOException e){
                throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);
            }

            //获取下载的偏移量，也就是已下载的位置
            final long offset = threadInfo.getStart() + threadInfo.getFinished();
            try{
                raf = getFile(downloadInfo.getDir(), downloadInfo.getName(), offset);
            }catch (IOException e){
                throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);
            }
            transforDataToLocal(inputStream, raf);
        }finally {
            try {
                IOCloseUtils.close(inputStream);
                IOCloseUtils.close(raf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void transforDataToLocal(InputStream inputStream, RandomAccessFile raf) throws DownloadException{

        final byte[] buffer = new byte[1024 * 16];
        while (true){

            checkPausedOrCanceled();
            int len = -1;
            try {
                len = inputStream.read(buffer);
            }catch (IOException e){
                throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);
            }

            if(-1 == len){
                return;
            }

            try {
                raf.write(buffer, 0, len);
                threadInfo.setFinished(threadInfo.getFinished() + len);
                synchronized (onDownloadListener){
                    downloadInfo.setFinished(downloadInfo.getFinished() + len);
                    onDownloadListener.OnDownloadProgress(downloadInfo.getFinished(), downloadInfo.getLength());
                }
            }catch (IOException e){
                throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);
            }
        }
    }

    private void checkPausedOrCanceled() throws DownloadException {

        if (mCommend == DownloadStatus.STATUS_CANCELED) {
            // cancel
            throw new DownloadException("Download canceled!", DownloadStatus.STATUS_CANCELED);
        } else if (mCommend == DownloadStatus.STATUS_PAUSED) {
            // pause
            updateDB(threadInfo);
            throw new DownloadException("Download paused!", DownloadStatus.STATUS_PAUSED);
        }
    }

    /**
     * 设置请求头内容
     * @param headers
     * @param connection
     */
    private void setHttpHeaders(Map<String, String> headers, URLConnection connection){

        if(headers != null){
            for (String key: headers.keySet()){
                connection.setRequestProperty(key, headers.get(key));
            }
        }
    }

    /**
     * 处理下载异常
     * @param de
     */
    private void handleDownloadException(DownloadException de){

        switch (de.getErrorCode()) {
            case DownloadStatus.STATUS_FAILED:
                synchronized (onDownloadListener) {
                    mStatus = DownloadStatus.STATUS_FAILED;
                    onDownloadListener.OnDownloadFailed(de);
                }
                break;
            case DownloadStatus.STATUS_PAUSED:
                synchronized (onDownloadListener) {
                    mStatus = DownloadStatus.STATUS_PAUSED;
                    onDownloadListener.OnDownloadPaused();
                }
                break;
            case DownloadStatus.STATUS_CANCELED:
                synchronized (onDownloadListener) {
                    mStatus = DownloadStatus.STATUS_CANCELED;
                    onDownloadListener.OnDownloadCanceled();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown state");
        }

    }

    @Override
    public void cancel() {

        mCommend = DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public void pause() {
        mCommend = DownloadStatus.STATUS_PAUSED;
    }

    /**
     * 将线程信息类插入到数据库中
     * @param info
     */
    protected abstract void insertIntoDB(ThreadInfo info);

    /**
     * 实现类根据传入的ThreadInfo对象获取相应的请求头
     * @param info
     * @return
     */
    protected abstract Map<String, String> getHttpHeaders(ThreadInfo info);

    protected abstract String getTag();

    /**
     * 获取响应码
     * @return
     */
    protected abstract int getResponseCode();

    /**
     * 这里采用随机访问文件的方式读取数据，这样就可以很好的实现断点续传的功能
     * @param dir
     * @param name
     * @param offset  偏移量
     * @return
     * @throws IOException
     */
    protected abstract RandomAccessFile getFile(File dir, String name, long offset) throws IOException;

    /**
     * 更新db
     * @param info
     */
    protected abstract void updateDB(ThreadInfo info);

}
