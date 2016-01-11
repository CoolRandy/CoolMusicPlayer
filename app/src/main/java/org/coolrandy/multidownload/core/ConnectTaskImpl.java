package org.coolrandy.multidownload.core;

import android.os.*;
import android.os.Process;
import android.provider.SyncStateContract;
import android.text.TextUtils;

import org.apache.http.protocol.HTTP;
import org.coolrandy.multidownload.Constants;
import org.coolrandy.multidownload.DownloadException;
import org.coolrandy.multidownload.architecture.ConnectTask;
import org.coolrandy.multidownload.architecture.DownloadStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by admin on 2016/1/11.
 * 连接任务的具体实现
 */
public class ConnectTaskImpl implements ConnectTask{

    private final String mUri;
    private final OnConnectListener mOnConnectListener;

    private volatile int mStatus;
    private volatile long mStartTime;

    public ConnectTaskImpl(OnConnectListener mOnConnectListener, String mUri) {
        this.mOnConnectListener = mOnConnectListener;
        this.mUri = mUri;
    }

    @Override
    public boolean isConnecting() {
        return mStatus == DownloadStatus.STATUS_CONNECTING;
    }

    @Override
    public boolean isConnected() {
        return mStatus == DownloadStatus.STATUS_CONNECTED;
    }

    @Override
    public boolean isCanceled() {
        return mStatus == DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public boolean isFailed() {
        return mStatus == DownloadStatus.STATUS_FAILED;
    }

    @Override
    public void cancel() {
        mStatus = DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public void run() {//一旦运行，其状态即为正在连接状态

        //设置优先级为后台
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mStatus = DownloadStatus.STATUS_CONNECTING;
        //回调正在连接
        mOnConnectListener.onConnecting();

        try{
            executeConnect();
        }catch (DownloadException de){
            handleDownloadException(de);
        }
    }

    /**
     * 执行连接
     * @throws DownloadException
     */
    private void executeConnect() throws DownloadException{

        mStartTime = System.currentTimeMillis();
        HttpURLConnection conn = null;

        final URL url;
        try {
            url = new URL(mUri);
        }catch (MalformedURLException e){
            throw new DownloadException("Bad url.", e, DownloadStatus.STATUS_FAILED);
        }

        //下面采用HttpURLConnection来连接数据
        try{
            //打开链接
            conn = (HttpURLConnection)url.openConnection();
            //配置一些参数
            conn.setConnectTimeout(Constants.HTTP.CONNECT_TIME_OUT);
            conn.setReadTimeout(Constants.HTTP.READ_TIME_OUT);
            conn.setRequestMethod(Constants.HTTP.GET);
            conn.setRequestProperty("Range", "bytes=" + 0 + "-");
            final int responseCode = conn.getResponseCode();
            //根据响应码，解析响应
            if(responseCode == HttpURLConnection.HTTP_OK){
                parseResponse(conn, false);
            }else if (responseCode == HttpURLConnection.HTTP_PARTIAL){
                //这里server值处理了部分请求，所以设置AcceptRange为true，以便后面实现续传
                parseResponse(conn, true);
            }else {
                throw new DownloadException("UnSupported response code:" + responseCode, DownloadStatus.STATUS_FAILED);
            }

        }catch (ProtocolException e){

            throw new DownloadException("Protocol error.", e, DownloadStatus.STATUS_FAILED);
        }catch (IOException e){

            throw new DownloadException("IO error", e, DownloadStatus.STATUS_FAILED);

        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }

    }

    /**
     * 解析响应
     * @param httpURLConnection
     * @param isAcceptRages
     * @throws DownloadException
     */
    private void parseResponse(HttpURLConnection httpURLConnection, boolean isAcceptRages) throws DownloadException{

        final long length;
        String contentLength = httpURLConnection.getHeaderField("Content-Length");
        if(TextUtils.isEmpty(contentLength) || contentLength.equals("0") || contentLength.equals("-1")){
            length = httpURLConnection.getContentLength();
        }else {
            length = Long.parseLong(contentLength);
        }

        if (length <= 0){
            throw new DownloadException("length <= 0", DownloadStatus.STATUS_FAILED);
        }
        //检查是否被取消
        checkCanceled();

        //既没有失败也没有被取消，则链接成功
        mStatus = DownloadStatus.STATUS_CONNECTED;
        final long timeDelta = System.currentTimeMillis() - mStartTime;
        mOnConnectListener.onConnected(timeDelta, length, isAcceptRages);
    }

    private void checkCanceled() throws DownloadException{

        if(isCanceled()){
            throw new DownloadException("Download Paused!", DownloadStatus.STATUS_PAUSED);
        }
    }

    /**
     * 处理异常
     * @param de
     */
    private void handleDownloadException(DownloadException de){

        switch (de.getErrorCode()) {
            case DownloadStatus.STATUS_FAILED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_FAILED;
                    mOnConnectListener.onConnectFailed(de);
                }
                break;
            case DownloadStatus.STATUS_CANCELED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_CANCELED;
                    mOnConnectListener.onConnectCanceled();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }
}
