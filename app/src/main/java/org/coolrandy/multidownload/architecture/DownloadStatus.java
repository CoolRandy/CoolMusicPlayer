package org.coolrandy.multidownload.architecture;

import org.coolrandy.multidownload.CallBack;
import org.coolrandy.multidownload.DownloadException;

/**
 * Created by admin on 2016/1/11.
 * 下载状态类
 */
public class DownloadStatus {

    public static final int STATUS_STARTED = 101;
    public static final int STATUS_CONNECTING = 102;
    public static final int STATUS_CONNECTED = 103;
    public static final int STATUS_PROGRESS = 104;
    public static final int STATUS_COMPLETED = 105;
    public static final int STATUS_PAUSED = 106;
    public static final int STATUS_CANCELED = 107;
    public static final int STATUS_FAILED = 108;

    //状态
    private int status;
    //时间
    private long time;
    //总进度
    private long length;
    //已完成进度
    private long finished;
    //百分比
    private int percent;
    //
    private boolean acceptRanges;
    //exception
    private DownloadException de;

    //callback
    private CallBack callBack;

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public boolean isAcceptRanges() {
        return acceptRanges;
    }

    public void setAcceptRanges(boolean acceptRanges) {
        this.acceptRanges = acceptRanges;
    }

    public DownloadException getDe() {
        return de;
    }

    public void setDe(DownloadException de) {
        this.de = de;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
