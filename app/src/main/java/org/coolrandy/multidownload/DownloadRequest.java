package org.coolrandy.multidownload;

import java.io.File;

/**
 * Created by admin on 2016/1/13.
 * 下载请求
 * 在application中进行一些配置之后，就可以发起请求
 */
public class DownloadRequest {

    private String uri;
    private File folder;
    private CharSequence title;
    private CharSequence description;
    private boolean scannable;

    public DownloadRequest() {
    }

    public DownloadRequest(CharSequence description, File folder, boolean scannable, CharSequence title, String uri) {
        this.description = description;
        this.folder = folder;
        this.scannable = scannable;
        this.title = title;
        this.uri = uri;
    }

    public CharSequence getDescription() {
        return description;
    }

    public File getFolder() {
        return folder;
    }

    public boolean isScannable() {
        return scannable;
    }

    public CharSequence getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public static class Builder{

        private String uri;
        private File folder;
        private CharSequence title;
        private CharSequence description;
        private boolean scannable;

        public Builder setDescription(CharSequence description) {
            this.description = description;
            return this;
        }

        public Builder setFolder(File folder) {
            this.folder = folder;
            return this;
        }

        public Builder setScannable(boolean scannable) {
            this.scannable = scannable;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public DownloadRequest build(){

            DownloadRequest request = new DownloadRequest(description, folder, scannable, title, uri);
            return request;
        }
    }
}
