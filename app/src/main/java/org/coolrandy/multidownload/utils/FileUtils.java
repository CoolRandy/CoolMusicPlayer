package org.coolrandy.multidownload.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Created by admin on 2016/1/13.
 * 文件管理
 */
public class FileUtils {

    private static final String DOWNLOAD_DIR = "download";

    private static final File getDefaultDownloadDir(Context context){

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return new File(context.getExternalCacheDir(), DOWNLOAD_DIR);
        }

        return new File(context.getCacheDir(), DOWNLOAD_DIR);
    }

    public static boolean isSDMounted(){

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取前缀
     * @param fileName
     * @return
     */
    public static final String getPrefix(@NonNull String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * 获取后缀
     * @param fileName
     * @return
     */
    public static final String getSuffix(@NonNull String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
