package org.coolrandy.multidownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2016/1/13.
 * 封装系统SQLiteOpenhelper
 */
public class DBOpenhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    private static final int DB_VERSION = 1;

    public DBOpenhelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

       createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        dropTable(db);
        createTable(db);
    }

    void createTable(SQLiteDatabase db){
        ThreadInfoDao.createTable(db);
    }

    void dropTable(SQLiteDatabase db){
        ThreadInfoDao.dropTable(db);
    }
}
