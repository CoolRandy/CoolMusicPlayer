package org.coolrandy.multidownload.db;

import android.content.Context;
import android.provider.ContactsContract;

import java.util.List;

/**
 * Created by admin on 2016/1/13.
 * 数据库管理类
 */
public class DatabaseManager {

    //单例
    private static DatabaseManager sDatabaseManager;
    private final ThreadInfoDao threadInfoDao;

    public static DatabaseManager getInstance(Context context){

        if(null == sDatabaseManager){
            sDatabaseManager = new DatabaseManager(context);
        }

        return sDatabaseManager;
    }

    public DatabaseManager(Context context) {

        threadInfoDao = new ThreadInfoDao(context);
    }

    //插入，删除，更新
    public synchronized void insert(ThreadInfo threadInfo){

        threadInfoDao.insert(threadInfo);
    }

    public synchronized void delete(String tag){

        threadInfoDao.delete(tag);
    }

    public synchronized void update(String tag, int threadId, long finished){
        threadInfoDao.update(tag, threadId, finished);
    }

    public List<ThreadInfo> getThreadInfos(String tag){
       return threadInfoDao.getThreadInfos(tag);
    }

    public boolean exists(String tag, int threadId){
        return threadInfoDao.exists(tag, threadId);
    }
}
