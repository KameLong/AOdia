package com.fc2.web.kamelong.aodia.detabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

import com.fc2.web.kamelong.aodia.SdLog;


/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */

/**
 * @author KameLong
 *
 * AOdia内で用いるデータベースを管理するクラス
 * Activityが閉じられても保持しなければならないデータはすべてこのデータベースに保存する
 *
 * v1.0以降データベースはaodia.dbを使用する
 *
 * データベースに保存するデータについてはonCreate内のコメントを参照
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DETABASE_NAME="aodia.db";
    public static final int DETABASE_VERSION=1;
    public static final String ID="_id";

    public static final String FILE_PATH="filePath";
    public static final String DIA_NUM="diaNum";
    public static final String DOWN_SCROLL_X="downScrollX";
    public static final String DOWN_SCROLL_Y="downScrollY";
    public static final String UP_SCROLL_X="upScrollX";
    public static final String UP_SCROLL_Y="upScrollY";
    public static final String DIA_SCROLL_X="diaScrollX";
    public static final String DIA_SCROLL_Y="diaScrollY";
    public static final String DIA_SCALE_X="diaScaleX";
    public static final String DIA_SCALE_Y="diaScaleY";

    public static final String WINDOW_DATA="windowData";
    public static final String WINDOW_TYPE="windowType";
    public static final String STATION_ID="stationID";
    public static final String STATION_NAME="stationName";


    public static final String TABLE_LINEDATA="lineData";
    public static final String TABLE_APP_DATA="appData";
    public static final String TABLE_HISTORY="history";
    public static final String TABLE_PREVIEW="preview";
    public static final String TABLE_WINDOW="window";
    public static final String TABLE_WINDOW_TYPE="windowType";

    public static final String TABLE_SAME_STATION="sameStation";
    public DBHelper(Context context){

        super(context,DETABASE_NAME,null,DETABASE_VERSION);
    }
    /**
     * このデータベースを初めて使用する時に実行される処理
     * テーブルの作成や初期データの投入を行う
     */
    @Override
    public void onCreate( SQLiteDatabase db ) {
        try {
            /*line dataのテーブルには
            各ファイル各ダイヤの上り下り、ダイヤグラムに対するデータが入っている

            */
            db.execSQL(
                    "create table "+TABLE_LINEDATA+" ("
                            +ID+ " integer primary key autoincrement not null, "
                            +FILE_PATH+ " text not null, "
                            +DIA_NUM+" Integer not null, "
                            +DOWN_SCROLL_X+ " Integer, "
                            +DOWN_SCROLL_Y+ " Integer, "
                            +UP_SCROLL_X+ " Integer, "
                            +UP_SCROLL_Y+ " Integer, "
                            +DIA_SCROLL_X+ " Integer, "
                            +DIA_SCROLL_Y+ " Integer, "
                            +DIA_SCALE_X+ " Integer, "
                            +DIA_SCALE_Y+ " Integer )");
            // 必要なら、ここで他のテーブルを作成したり、初期データを挿入したりする
            db.execSQL(
                    "create table "+TABLE_APP_DATA+" ("
                            + ID+" Integer, "
                            + FILE_PATH+" text, "
                            + DIA_NUM+" Integer , "
                            + "direct Integer )");
            db.execSQL(
                    "create table "+TABLE_HISTORY+"("
                            +ID+" integer primary key autoincrement not null, "
                            +FILE_PATH+ " text)");
            db.execSQL(
                    "create table "+TABLE_PREVIEW+" ("
                            +ID+" integer primary key autoincrement not null, "
                            +FILE_PATH+ " text)");
            db.execSQL(
                    "create table "+TABLE_WINDOW+" ("
                            +ID+" integer , "
                            +WINDOW_DATA+ " text)");
            db.execSQL(
                    "create table "+TABLE_WINDOW_TYPE+" ("
                            +ID+" integer primary key autoincrement not null, "
                            +WINDOW_TYPE+ " text)");
            db.execSQL(
                    "create table "+TABLE_SAME_STATION+" ("
                            +ID+" integer primary key autoincrement not null, "
                            +STATION_ID+" integer, "
                            +STATION_NAME+" text, "
                            +FILE_PATH+ " text)");

        }catch(Exception e){
            SdLog.log("データベースの作成に失敗しました");
            SdLog.log(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public  void setRecentFile(String filePath,int diaNum,int direct){
        String[] filePathSplit=filePath.split("/");
        if(filePathSplit.length>3&&filePathSplit[filePathSplit.length-1].equals("sample.oud")){
            return;
        }
        getWritableDatabase().delete(TABLE_APP_DATA,null,null);
        ContentValues val = new ContentValues();
        val.put(FILE_PATH,filePath);
        val.put(DIA_NUM,diaNum);
        val.put("direct",direct);
        val.put(ID,1);
        System.out.println(getWritableDatabase().insert(TABLE_APP_DATA,null,val));
        System.out.println(getRecentFilePath());

    }
    public String getRecentFilePath(){
        try {
            Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_APP_DATA + " where " + ID + " =?" + ";", new String[]{"1"});
            cursor.moveToFirst();
            return cursor.getString(1);
        }catch(Exception e){
            e.printStackTrace();
            return"";
        }
    }
    public int getRecentDiaNum(){
        Cursor cursor = getReadableDatabase().rawQuery("select * from "+TABLE_APP_DATA+" where "+ID+" =?"+";",new String[]{"1"});
        cursor.moveToFirst();
        return cursor.getInt(2);
    }
    public int getRecentDirect(){
        Cursor  cursor = getReadableDatabase().query(TABLE_APP_DATA,
                null,
                null, null,
                null, null, null);
        cursor.moveToFirst();
        return cursor.getInt(3);
    }
    /** エントリ追加 */
    private void addNew( SQLiteDatabase db, String filepath,int diaNum ){

        // 挿入するデータはContentValuesに格納
        for(int i=0;i<diaNum;i++){
            ContentValues val = new ContentValues();
            val.put(FILE_PATH, filepath );
            val.put(DIA_NUM ,String.valueOf(i) );
            val.put(DOWN_SCROLL_X, 0 );
            val.put(DOWN_SCROLL_Y, 0 );
            val.put(UP_SCROLL_X, 0 );
            val.put(UP_SCROLL_Y, 0 );
            val.put(DIA_SCROLL_X, 0 );
            val.put(DIA_SCROLL_X, 0 );
            val.put(DIA_SCALE_X, 0 );
            val.put(DIA_SCALE_Y, 0 );
            db.insert(TABLE_LINEDATA, null, val );
        }
    }
    /** 年齢が一致するデータを検索 */
    private Cursor searchByFilePath( SQLiteDatabase db, String  filePath,int diaNum ){
        // Cursorを確実にcloseするために、try{}～finally{}にする
        Cursor cursor = null;
        try{
            // name_book_tableからnameとageのセットを検索する
            // ageが指定の値であるものを検索
            cursor = db.query(TABLE_LINEDATA,
                    null,
                    "filePath = ? AND diaNum = ?", new String[]{ filePath ,""+diaNum},
                    null, null, null );

            // 検索結果をcursorから読み込んで返す
            return cursor;
        }
        finally{
            // Cursorを忘れずにcloseする
            if( cursor != null ){
//                cursor.close();
            }
        }
    }
    public void addNewFile( SQLiteDatabase db,String filePath,int diaNum){
        if(searchByFilePath(db,filePath,0).getCount()==0){
            addNew(db,filePath,diaNum);
        }
    }
     public void update(SQLiteDatabase db,String filePath,int diaNum,ContentValues cv){

       db.update(TABLE_LINEDATA,cv,"filePath = \""+filePath+"\" AND diaNum = "+diaNum,null);
    }

    /**
     * 各路線ファイル、各画面のスクロール位置を保存し、読みだす
     * @param db
     * @param filePath
     * @param diaNum
     * @param key
     * @return
     */
    public int[] getPositionData(SQLiteDatabase db, String filePath, int diaNum, int key){
        Cursor cursor=searchByFilePath(db,filePath,diaNum);
        cursor.moveToFirst();
        int[] result;
        switch(key){
            case 0:
                result=new int[2];
                result[0]=cursor.getInt(3);
                result[1]=cursor.getInt(4);
                cursor.close();
                return result;
            case 1:
                result=new int[2];
                result[0]=cursor.getInt(5);
                result[1]=cursor.getInt(6);
                cursor.close();
                return result;
            case 2:
                result=new int[4];
                result[0]=cursor.getInt(7);
                result[1]=cursor.getInt(8);
                result[2]=cursor.getInt(9);
                result[3]=cursor.getInt(10);
                cursor.close();
                return result;
            default:
                return null;
        }
    }

    /**
     * ファイル履歴を追加する。
     * ファイルを開いた際にファイルパスを使って呼び出す。
     * データベースに開いたファイルが追加される。
     * @param filePath
     */
    public void addHistory(String filePath){
        try {
            getWritableDatabase().delete(TABLE_HISTORY, FILE_PATH + "=?", new String[]{filePath});
            ContentValues values = new ContentValues();
            values.put(DBHelper.FILE_PATH, filePath);
            getWritableDatabase().insert(TABLE_HISTORY, null, values);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * ファイル履歴を取得する。
     *
     * @return 過去最大10件分の履歴をファイルパスの文字列にして返す。
     */
    public String[] getHistory(){
        String[] result;
        Cursor c = getReadableDatabase().rawQuery("select * from "+TABLE_HISTORY+" ORDER BY "+ID+" DESC"+";",null);
        if(c.getCount()>10){
            result=new String[10];
        }else{
            result=new String[c.getCount()];
        }
        c.moveToFirst();
        int i=0;
        while(!c.isAfterLast()&&i<10){
            result[i]=c.getString(1);
            i++;
            c.moveToNext();
        }
        c.close();
        return result;
    }

    /**
     * 今開いているファイルをすべて記録する
     *
     * @param filePaths
     */
    public void memoryFilePaths(String[] filePaths){
        getWritableDatabase().delete(TABLE_PREVIEW,null,null);

        for(int i=0;i<filePaths.length;i++){
            ContentValues values=new ContentValues();
            values.put(DBHelper.FILE_PATH,filePaths[i]);
            getWritableDatabase().insert(TABLE_PREVIEW,null,values);
        }

        String[] data=readFilePaths();
        for(int i=0;i<data.length;i++){
            System.out.println(data[i]);
        }


    }
    /**
     * 直前に開いていたファイルのリストを返す。
     */
    public String[] readFilePaths(){
        Cursor c = getReadableDatabase().rawQuery("select * from "+TABLE_PREVIEW+";",null);
        String[] result=new String[c.getCount()];
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            result[i]=c.getString(1);
            c.moveToNext();
        }
        c.close();
        return result;
    }
    /**
     * 開いていた画面の記録（v1.0以降の多い画面用)
     * @param windows サイズ５の文字列配列。
     *                それぞれの文字列は識別子をハイフンで繋がれたもの
     *
     */
    public static final String LINE_TIME_TABLE="LineTimeTable";
    public static final String DIAGRAM="Diagram";
    public static final String STATION_TIME_TABLE="StationTimeTable";
    public static final String STATION_TIME_INDEX="StationTimeIndex";
    public static final String COMMENT="comment";
    public static final String HELP="help";
    public static final int WINDOW_NUM=5;

    public void saveWindows(String[] windows){
        try {
            getWritableDatabase().delete(TABLE_WINDOW, null, null);
            for (int i = 0; i < WINDOW_NUM; i++) {
                ContentValues cont = new ContentValues();
                cont.put(ID, i);
                cont.put(WINDOW_DATA, windows[i]);
                getWritableDatabase().insert(TABLE_WINDOW, null, cont);
                System.out.println(windows[i]);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public String[] readWindows(){
        try {
            String[] result = new String[WINDOW_NUM];
            Cursor c = getReadableDatabase().rawQuery("select * from " + TABLE_WINDOW + " order by " + ID + ";", null);
            c.moveToFirst();
            for (int i = 0; i < WINDOW_NUM; i++) {
                result[i] = c.getString(1);
                c.moveToNext();
            }
            return result;
        }catch(Exception e){
            e.printStackTrace();
            return new String[]{"","","","",""};
        }
    }



}
