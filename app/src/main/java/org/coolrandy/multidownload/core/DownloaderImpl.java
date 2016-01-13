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
import org.coolrandy.multidownload.db.DatabaseManager;
import org.coolrandy.multidownload.db.ThreadInfo;

import java.util.LinkedList;
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
    private DatabaseManager databaseManager;
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

    public DownloaderImpl(DownloadResponse response, DownloadRequest request, String tag, DatabaseManager databaseManager, Executor executor, DownloadConfig config, OnDownloaderDestroyedListener destoryedListener) {
        this.response = response;
        this.request = request;
        this.tag = tag;
        this.databaseManager = databaseManager;
        this.executor = executor;
        this.config = config;
        this.destoryedListener = destoryedListener;

        init();
    }

    public void init(){

        downloadInfo = new DownloadInfo(request.getFolder(), request.getTitle().toString(), request.getUri());
        downloadTasks = new LinkedList<>();
    }

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
        //开始连接
        connect();
    }

    private void connect(){
        //此处调用连接任务
        connectTask = new ConnectTaskImpl(this, request.getUri());
        executor.execute(connectTask);
    }

    @Override
    public void cancel() {

        //首先判断连接任务是否为空
        if(connectTask != null){
            connectTask.cancel();//如果连接任务不为空，表示还有连接任务在执行，取消掉
        }
        for (DownloadTask task: downloadTasks){
            task.cancel();
        }
    }

    @Override
    public void pause() {
        //首先判断连接任务是否为空
        if(connectTask != null){
            connectTask.cancel();//如果连接任务不为空，表示还有连接任务在执行，取消掉
        }
        //遍历暂停所有的下载任务
        for (DownloadTask task: downloadTasks){
            task.pause();
        }
    }

    @Override
    public void onDestory() {
        destoryedListener.onDestroyed(tag, this);
    }

    @Override
    public void onConnectCanceled() {

        mStatus = DownloadStatus.STATUS_CANCELED;
        response.onConnectCanceled();
        onDestory();
    }

    @Override
    public void onConnecting() {

        mStatus = DownloadStatus.STATUS_CONNECTING;
        response.onConnecting();
    }

    @Override
    public void onConnected(long time, long length, boolean isAcceptRanges) {
        //连接上了
        mStatus = DownloadStatus.STATUS_CONNECTED;
        response.onConnected(time,length, isAcceptRanges);

        //设置下载状态
        downloadInfo.setIsRangesSupport(isAcceptRanges);
        downloadInfo.setLength(length);
        //开始执行下载
        download(length, isAcceptRanges);
    }

    private void download(long length, boolean isAcceptRange){

        //init
        initDownloadTasks(length, isAcceptRange);
        for (DownloadTask task: downloadTasks){
            executor.execute(task);
        }
    }

    private void initDownloadTasks(long length, boolean isAcceptRange){
        downloadTasks.clear();
        if(isAcceptRange){

            List<ThreadInfo> threadInfos = getMultiThreadInfo(length);
            int finished = 0;
            for (ThreadInfo threadInfo: threadInfos){
                finished += threadInfo.getFinished();
            }

            downloadInfo.setFinished(finished);
            for (ThreadInfo threadInfo: threadInfos){
                downloadTasks.add(new MultiDownloadTask(downloadInfo, this, threadInfo, databaseManager));
            }
        }else {

            ThreadInfo threadInfo = getSingleThreadInfo();
            downloadTasks.add(new SingleDownloadTask(downloadInfo, this, threadInfo));
        }
    }

    @Override
    public void onConnectFailed(DownloadException de) {

        mStatus = DownloadStatus.STATUS_FAILED;
        response.onDownloadFailed(de);
        onDestory();
    }

    @Override
    public void onDownloadConnecting() {

    }


    @Override
    public void OnDownloadProgress(long finished, long length) {

        mStatus = DownloadStatus.STATUS_PROGRESS;
        //计算百分比
        final int percent = (int) (finished * 100 / length);
        response.onDownloadProgress(finished, length, percent);
    }

    @Override
    public void OnDownloadCanceled() {
        if(isAllCanceled()) {
            //将任务从数据库中删除
            deleteFromDB();
            mStatus = DownloadStatus.STATUS_CANCELED;
            response.onDownloadCanceled();
            onDestory();
        }

    }

    @Override
    public void OnDownloadFailed(DownloadException de) {
        if(isAllFailed()){
            mStatus = DownloadStatus.STATUS_FAILED;
            response.onDownloadFailed(de);
            onDestory();
        }
    }

    @Override
    public void OnDownloadPaused() {

        if(isAllPaused()){
            mStatus = DownloadStatus.STATUS_PAUSED;
            response.onDownloadPaused();
            onDestory();
        }
    }

    @Override
    public void OnDownloadFinished() {

        if (isAllCompleted()){
            deleteFromDB();
            mStatus = DownloadStatus.STATUS_COMPLETED;
            response.onDownloadCompleted();
            onDestory();
        }
    }

    /**
     * 单线程的直接下载即可
     * @return
     */
    private ThreadInfo getSingleThreadInfo(){

        ThreadInfo threadInfo = new ThreadInfo(0, tag, request.getUri(), 0);
        return threadInfo;
    }

    /**
     * 多线程下载
     * 原理：首先从数据库中获取ThreadInfo的列表，若为空，则获取配置的线程数，然后遍历这几个线程，
     * 将下载任务的大小平分给这几个线程，分别执行一段任务,遍历后面将线程加入到列表中
     * @param length
     * @return
     */
    private List<ThreadInfo> getMultiThreadInfo(long length){

        final List<ThreadInfo> threadInfos = databaseManager.getThreadInfos(tag);
        if(threadInfos.isEmpty()){
            final int threadNum = config.getThreadNum();
            for (int i = 0; i < threadNum; i++){

                final long average = length / threadNum;
                final long start = i * average;
                final long end;

                if(i == threadNum - 1){
                    end = length;
                }else {
                    end = start + average - 1;
                }

                ThreadInfo threadInfo = new ThreadInfo(i, tag, request.getUri(), start, end, 0);
                threadInfos.add(threadInfo);
            }
        }

        return threadInfos;
    }

    //判断所有的下载线程（任务）都被取消,只要有一个没被取消，就返回false
    private boolean isAllCanceled(){

        boolean isCanceled = true;
        for (DownloadTask task: downloadTasks){
            if(task.isDownloading()){
                isCanceled = false;
                break;
            }
        }

        return isCanceled;
    }

    private boolean isAllCompleted(){

        boolean isCompleted = true;
        for (DownloadTask task: downloadTasks){
            if(!task.isComplete()){
                isCompleted = false;
            }
        }

        return isCompleted;
    }

    private boolean isAllPaused(){

        boolean isPaused = true;
        for (DownloadTask task: downloadTasks){
            if(task.isDownloading()){
                isPaused = false;
                break;
            }
        }

        return isPaused;
    }

    private boolean isAllFailed(){

        boolean isFailed = true;
        for (DownloadTask task: downloadTasks){
            if(task.isDownloading()){
                isFailed = false;
            }
        }

        return isFailed;
    }

    private void deleteFromDB(){
        databaseManager.delete(tag);
    }
}
