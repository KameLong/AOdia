package com.fc2.web.kamelong.aodia.detabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kame on 2017/03/06.
 */

public class AppDataSource {
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    public static final String[] historyColums={DBHelper.ID,DBHelper.FILE_PATH};

    public AppDataSource(Context context){
        dbHelper=new DBHelper(context);
    }
    public void open()throws SQLException{
        database=dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }
    public void addHistory(String filePath){
        ContentValues values=new ContentValues();
        values.put(DBHelper.FILE_PATH,filePath);
        long insertId=database.insert(DBHelper.TABLE_HISTORY,null,values);
    }

}
