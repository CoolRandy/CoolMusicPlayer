package org.coolrandy.multidownload.architecture;

/**
 * Created by admin on 2016/1/12.
 * 分发下载状态
 */
public interface DownloadStatusDelivery {

    void post(DownloadStatus status);
}
