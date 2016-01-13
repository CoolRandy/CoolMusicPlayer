package org.coolrandy.multidownload.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by admin on 2016/1/13.
 * 关闭IO操作
 */
public class IOCloseUtils {

    public static final void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            synchronized (IOCloseUtils.class) {
                closeable.close();
            }
        }
    }
}
