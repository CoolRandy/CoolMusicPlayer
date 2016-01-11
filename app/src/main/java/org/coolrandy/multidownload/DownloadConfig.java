package org.coolrandy.multidownload;

/**
 * Created by admin on 2016/1/11.
 * 下载的一些参数配置
 */
public class DownloadConfig {

    //默认最大线程数
    private static final int DEFAULT_MAX_THREAD_NUMBER = 100;
    //默认线程数
    private static final int DEFAULT_THREAD_NUMBER = 5;

    private int maxThreadNum;
    private int threadNum;

    public DownloadConfig() {
        this.maxThreadNum = DEFAULT_MAX_THREAD_NUMBER;
        this.threadNum = DEFAULT_THREAD_NUMBER;
    }

    public int getMaxThreadNum() {
        return maxThreadNum;
    }

    public void setMaxThreadNum(int maxThreadNum) {
        this.maxThreadNum = maxThreadNum;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
}
