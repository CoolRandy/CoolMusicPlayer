package org.coolrandy.multidownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 2016/1/13.
 */
public abstract class AbstractDao<T> {

    private DBOpenhelper dbOpenhelper;

    public AbstractDao(Context context) {
        this.dbOpenhelper = new DBOpenhelper(context);
    }

    protected SQLiteDatabase getWritableDatabase(){

        return dbOpenhelper.getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase(){

        return dbOpenhelper.getReadableDatabase();
    }

    public void close(){
        dbOpenhelper.close();
    }
}
