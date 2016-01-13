package org.coolrandy.multidownload;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.toolbox.DownloadRequest;

import org.coolrandy.multidownload.architecture.DownloadResponse;
import org.coolrandy.multidownload.architecture.DownloadStatus;
import org.coolrandy.multidownload.architecture.DownloadStatusDelivery;
import org.coolrandy.multidownload.architecture.Downloader;
import org.coolrandy.multidownload.core.DownloadResponseImpl;
import org.coolrandy.multidownload.core.DownloadStatusDeliveryImpl;

import java.util.HashMap;
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
        sDownloadManager = DownloadManager.getInstance();
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(new Handler(Looper.getMainLooper()));
    }

    //添加以下功能：下载，取消（全部），暂停（全部）

    /**
     * 下载
     * @param request 下载的请求
     * @param tag     url
     * @param callBack  回调
     */
    public void download(DownloadRequest request, String tag, CallBack callBack){

        String key = createKey(tag);
        if(check(key)){
            //map中不存在，为新的请求
            DownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);

        }
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
