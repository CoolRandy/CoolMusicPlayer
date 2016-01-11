package org.coolrandy.multidownload;

/**
 * Created by admin on 2016/1/11.
 * 对Exception做一次封装
 * 主要包含错误码和错误信息  比较简单，就是一些重载的构造方法和getter  setter
 */
public class DownloadException extends Exception {

    private String errorMessage;
    private int errorCode;

    public DownloadException() {
    }

    public DownloadException(String detailMessage) {
        super(detailMessage);
        this.errorMessage = detailMessage;
    }

    public DownloadException(String detailMessage, int errorCode) {
        super(detailMessage);
        this.errorCode = errorCode;
    }

    public DownloadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.errorMessage = detailMessage;
    }

    public DownloadException(String detailMessage, Throwable throwable, int errorCode) {
        super(detailMessage, throwable);
        this.errorCode = errorCode;
    }

    public DownloadException(Throwable throwable) {
        super(throwable);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
