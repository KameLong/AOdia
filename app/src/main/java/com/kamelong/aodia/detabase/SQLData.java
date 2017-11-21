package com.kamelong.aodia.detabase;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by kame on 2017/11/20.
 */

public class SQLData extends SQLiteOpenHelper {

    public SQLData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SQLData(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    public SQLData(Context context,File file) {
        super(context, "test", null, 1);
        long time=System.currentTimeMillis();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
        System.out.println(System.currentTimeMillis()-time);

        SQLiteCursor cursor=null;
        try {
            cursor = (SQLiteCursor) db.query("time2", new String[]{"ari_time","dep_time"}, "trip_id" + " = ?", new String[]{"0"}, null, null, null);
            int rowcount = cursor.getCount();
            cursor.moveToFirst();

            for (int i = 0; i < rowcount ; i++) {
                System.out.println(cursor.getString(0)+"\t"+cursor.getString(1));
                cursor.moveToNext();
            }
            System.out.println(System.currentTimeMillis()-time);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(cursor!=null){
                cursor.close();
            }
        }

        }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
