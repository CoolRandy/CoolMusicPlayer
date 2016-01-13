package org.coolrandy.multidownload.utils;

import java.util.List;

/**
 * Created by admin on 2016/1/13.
 */
public class ListUtils {
    /**
     * 判断列表是否为空
     * @param list
     * @return
     */
    public static final boolean isEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }
}
