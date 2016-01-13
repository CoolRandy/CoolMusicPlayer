package org.coolrandy.multidownload;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.coolrandy.multidownload.architecture.DownloadResponse;
import org.coolrandy.multidownload.architecture.DownloadStatusDelivery;
import org.coolrandy.multidownload.architecture.Downloader;
import org.coolrandy.multidownload.core.DownloadResponseImpl;
import org.coolrandy.multidownload.core.DownloadStatusDeliveryImpl;
import org.coolrandy.multidownload.core.DownloaderImpl;
import org.coolrandy.multidownload.db.DatabaseManager;
import org.coolrandy.multidownload.db.ThreadInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2016/1/11.
 * 下载管理器
 * 主要用于对下载状态的管理，如暂停，取消  对下载线程数的管理
 */
public class DownloadManager implements Downloader.OnDownloaderDestroyedListener{

    private static final String TAG = DownloadManager.class.getSimpleName();

    private Map<String, Downloader> mDownloadMap;
    private DownloadConfig mConfig;
    //并发服务
    private ExecutorService mExecutorService;
    private DownloadStatusDelivery mDelivery;

    //线程管理数据库
    private DatabaseManager databaseManager;

    //单例
    private static DownloadManager sDownloadManager;
    //获取单例
    public static DownloadManager getInstance(){

        if(null == sDownloadManager){
            synchronized (DownloadManager.class){
                sDownloadManager = new DownloadManager();
            }
        }

        return sDownloadManager;
    }

    public DownloadManager() {

        mDownloadMap = new HashMap<>();
    }

    //初始化
    public void init(Context context){

        init(context, new DownloadConfig());
    }

    //参数中对config加了非空注解，这样方法内部就不需要进行非空判断了
    public void init(Context context, @NonNull DownloadConfig config){

        if(config.getThreadNum() > config.getMaxThreadNum()){
            throw new IllegalArgumentException("thread num must < max thread num");
        }

        mConfig = config;
//      sDownloadManager = DownloadManager.getInstance();
        databaseManager = DatabaseManager.getInstance(context);
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(new Handler(Looper.getMainLooper()));
    }

    //添加以下功能：下载，取消（全部），暂停（全部）

    /**
     * 下载  获取到请求request实例之后就可以执行下载
     * @param request 下载的请求
     * @param tag     url
     * @param callBack  回调：可以new一个回调实例，重写相应的方法
     */
    public void download(DownloadRequest request, String tag, CallBack callBack){

        String key = createKey(tag);
        if(check(key)){
            //map中不存在，为新的请求
            DownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);
            Downloader downloader = new DownloaderImpl(response, request, tag, databaseManager, mExecutorService,
                    mConfig, this);
            mDownloadMap.put(key, downloader);
            downloader.start();
        }
    }

    /**
     * 根据tag暂停下载线程
     * @param tag
     */
    public void pause(String tag){

        String key = createKey(tag);
        if(mDownloadMap.containsKey(key)){
            Downloader downloader = mDownloadMap.get(key);
            if(downloader != null){
                if(downloader.isRunning()) {
                    downloader.pause();
                }
            }
            mDownloadMap.remove(key);
        }

    }

    /**
     * 根据tag取消下载线程
     * @param tag
     */
    public void cancel(String tag){

        String key = createKey(tag);
        if(mDownloadMap.containsKey(key)){
            Downloader downloader = mDownloadMap.get(key);
            if(downloader != null){

                downloader.cancel();
            }
            mDownloadMap.remove(key);
        }
    }

    /**
     * 暂停所有的下载
     */
    public void pauseAll(){

        if(!mDownloadMap.isEmpty()){
            for (Downloader downloader: mDownloadMap.values()){

                if(downloader != null){
                    if(downloader.isRunning()) {
                        downloader.pause();
                    }
                }
            }
        }
    }

    /**
     * 取消所有的下载
     */
    public void cancelAll(){

        if(!mDownloadMap.isEmpty()){
            for (Downloader downloader: mDownloadMap.values()){

                if(downloader != null){
                    downloader.cancel();
                }
            }
        }
    }

    /**
     * 获取下载的进度
     * @param tag
     * @return
     */
    public DownloadInfo getDownloadProgress(String tag) {
        String key = createKey(tag);
        List<ThreadInfo> threadInfos = databaseManager.getThreadInfos(key);
        DownloadInfo downloadInfo = null;
        if (!threadInfos.isEmpty()) {
            int finished = 0;
            int progress = 0;
            int total = 0;
            for (ThreadInfo info : threadInfos) {
                finished += info.getFinished();
                total += (info.getEnd() - info.getStart());
            }
            progress = (int) ((long) finished * 100 / total);
            downloadInfo = new DownloadInfo();
            downloadInfo.setFinished(finished);
            downloadInfo.setLength(total);
            downloadInfo.setProgress(progress);
        }
        return downloadInfo;
    }


    /**
     * 注：这里比较好的编程实践就是取url字符串的hash码作为key，这样可以保证唯一性
     * @param tag
     * @return
     */
    private static String createKey(String tag){

        if(null == tag){
            throw new NullPointerException("tag cannot be null!");
        }
        return String.valueOf(tag.hashCode());
    }

    /**
     * 检查下载的map中是否已包含该key
     * @param key
     * @return
     */
    private boolean check(String key){

        if(mDownloadMap.containsKey(key)){

            Downloader downloader = mDownloadMap.get(key);
            if(downloader != null){
                if(downloader.isRunning()){
                    Log.i("TAG", "Task has been started!");
                    return false;
                }else {

                    throw new IllegalStateException("Downloader instance with same tag has not been destroyed!");
                }
            }
        }
        return true;
    }

    @Override
    public void onDestroyed(String key, Downloader downloader) {

        if(mDownloadMap.containsKey(key)){
            mDownloadMap.remove(key);
        }
    }
}
