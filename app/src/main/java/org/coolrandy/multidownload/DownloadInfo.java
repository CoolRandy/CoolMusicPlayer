package org.coolrandy.multidownload;

import java.io.File;

/**
 * Created by admin on 2016/1/13.
 * 下载信息类：包含下载文件名称、文件存放目录、url、下载进度、文件总长度、已下载文件长度、是否支持续传
 */
public class DownloadInfo {

    private String name;
    private File dir;
    private String url;
    private int progress;
    private long length;
    private long finished;
    private boolean isRangesSupport;

    public DownloadInfo() {
    }

    public DownloadInfo(File dir, String name, String url) {
        this.dir = dir;
        this.name = name;
        this.url = url;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public boolean isRangesSupport() {
        return isRangesSupport;
    }

    public void setIsRangesSupport(boolean isRangesSupport) {
        this.isRangesSupport = isRangesSupport;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
