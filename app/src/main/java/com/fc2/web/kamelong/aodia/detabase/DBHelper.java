package com.fc2.web.kamelong.aodia.detabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            それぞれに対し、現在のスクロール位置
            ダイヤグラムに対しては拡大縮小率も保存する
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
            /*
            table app dataのテーブルには
            最後に開いたファイル名、ダイヤ、上り時刻表か下り時刻表かダイヤグラムのどれを開いたかを記録する
            directは下り時刻表=0、上り時刻表=1、ダイヤグラム=2
             */
            db.execSQL(
                    "create table "+TABLE_APP_DATA+" ("
                            + ID+" Integer, "
                            + FILE_PATH+" text, "
                            + DIA_NUM+" Integer , "
                            + "direct Integer )");
            /*
            オープンしたファイルの履歴を保存する
             */

            db.execSQL(
                    "create table "+TABLE_HISTORY+"("
                            +ID+" integer primary key autoincrement not null, "
                            +FILE_PATH+ " text)");
            db.execSQL(
                    "create table "+TABLE_PREVIEW+" ("
                            +ID+" integer primary key autoincrement not null, "
                            +FILE_PATH+ " text)");
            /*
            Fragmentを表示するそれぞれのFrameについて、どのFragmentが表示されていたかを保存する
             */

            db.execSQL(
                    "create table "+TABLE_WINDOW+" ("
                            +ID+" integer , "
                            +WINDOW_DATA+ " text)");

            //v1.0.5追記、このテーブルは使用を中止しようと考えています
            db.execSQL(
                    "create table "+TABLE_WINDOW_TYPE+" ("
                            +ID+" integer primary key autoincrement not null, "
                            +WINDOW_TYPE+ " text)");
            /*
            同一駅データを保存する
            今後の　乗り継ぎ検索　に向けてのテーブル
            現在使用されていない
             */
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



    /**
     * 直近に開いたファイルを保存する
     * @param filePath　直近に開いたファイル
     * @param diaNum　開いたダイヤインデックス
     * @param direct　開いた方向(下り時刻表=0、上り時刻表=1、ダイヤグラム=2)
     *
     *
     * @see #getRecentFilePath()
     * @see #getRecentDiaNum()
     * @see #getRecentDirect()
     * これらはsetRecentFileで保存したデータを読み込むためのメソッド
     */
    public  void setRecentFile(final String filePath,final int diaNum,final int direct){
        try {
            String[] filePathSplit = filePath.split("/");
            if (filePathSplit.length > 3 && filePathSplit[filePathSplit.length - 1].equals("sample.oud")) {
                return;
            }
            getWritableDatabase().delete(TABLE_APP_DATA, null, null);
            ContentValues val = new ContentValues();
            val.put(FILE_PATH, filePath);
            val.put(DIA_NUM, diaNum);
            val.put("direct", direct);
            val.put(ID, 1);//IDを１に固定することで最新版のみデータが残るようになる
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    /**
     * 直近に開いたファイルを取得する
     * @return ファイルパス
     * @see #setRecentFile(String, int, int)
     */
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

    /**
     * 直近に開いたダイヤインデックスを取得する
     * @return ダイヤインデックス(int)
     * @see #setRecentFile(String, int, int)
     */
    public int getRecentDiaNum(){
        try {
            Cursor cursor = getReadableDatabase().rawQuery("select * from " + TABLE_APP_DATA + " where " + ID + " =?" + ";", new String[]{"1"});
            cursor.moveToFirst();
            return cursor.getInt(2);
        }catch(Exception e){
            SdLog.log(e);
            return 0;
        }
    }

    /**
     * 直近に開いた方向を取得する
     * @return (下り時刻表=0、上り時刻表=1、ダイヤグラム=2)
     * @see #setRecentFile(String, int, int)
     */
    public int getRecentDirect(){
        try {
            Cursor cursor = getReadableDatabase().query(TABLE_APP_DATA,
                    null,
                    null, null,
                    null, null, null);
            cursor.moveToFirst();
            return cursor.getInt(3);
        }catch(Exception e){
            SdLog.log(e);
            return 0;
        }
    }

    /**
     * line dataに新しいファイルを追加する。
     * もしすでに同じファイルパスが登録されている場合、
     * ダイヤ数が同じならそのままデータを再利用する
     * ダイヤ数が異なるならデータを削除し再生成する
     *
     * @param filePath
     * @param fileDiaNum
     */
    public void addNewFileToLineData(final String filePath,final int fileDiaNum){
        Cursor cursor=null;
        try {
            cursor = getReadableDatabase().query(TABLE_LINEDATA, null, FILE_PATH + "= ?", new String[]{filePath}, null, null, null);
            if(cursor.getCount()!=fileDiaNum){
                getWritableDatabase().delete(TABLE_LINEDATA,FILE_PATH+"= ?",new String[]{filePath});
                for(int i=0;i<fileDiaNum;i++){
                    ContentValues val = new ContentValues();
                    val.put(FILE_PATH, filePath );
                    val.put(DIA_NUM ,String.valueOf(i) );
                    val.put(DOWN_SCROLL_X, 0 );
                    val.put(DOWN_SCROLL_Y, 0 );
                    val.put(UP_SCROLL_X, 0 );
                    val.put(UP_SCROLL_Y, 0 );
                    val.put(DIA_SCROLL_X, 0 );
                    val.put(DIA_SCROLL_X, 0 );
                    val.put(DIA_SCALE_X, 0 );
                    val.put(DIA_SCALE_Y, 0 );
                    getWritableDatabase().insert(TABLE_LINEDATA, null, val );
                }
            }
        }
        catch(Exception e){
            SdLog.log(e);

        }
        finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    /**
     * line deta をアップデートする
     * これは路線時刻表のデータを更新するためのメソッド
     * あらかじめ該当するfilePath,diaNumについてのデータを作成しておく必要がある。
     * @see #addNewFileToLineData(String, int)
     * @param filePath
     * @param diaNum
     * @param direct
     * @param scrollX
     * @param scrollY
     * @see #getPositionData(SQLiteDatabase, String, int, int)  保存したデータを読み出す
     */
    public void updateLineData(final String filePath,final int diaNum,final int direct,final int scrollX,final int scrollY){
        ContentValues cv=new ContentValues();
        if(direct==0){
            cv.put(DOWN_SCROLL_X,scrollX);
            cv.put(DOWN_SCROLL_Y,scrollY);
        }else{
            cv.put(UP_SCROLL_X,scrollX);
            cv.put(UP_SCROLL_Y,scrollY);
        }
        getWritableDatabase().update(TABLE_LINEDATA,cv,FILE_PATH+" = ? AND "+DIA_NUM+" = ?",new String[]{filePath,""+diaNum});
    }
    /**
     * line deta をアップデートする
     * これはダイヤグラムのデータを更新するためのメソッド
     * あらかじめ該当するfilePath,diaNumについてのデータを作成しておく必要がある。
     * @see #addNewFileToLineData(String, int)
     * @param filePath
     * @param diaNum
     * @param scrollX
     * @param scrollY
     * @param scaleX
     * @param scaleY
     * @see #getPositionData(SQLiteDatabase, String, int, int)  保存したデータを読み出す
     */
    public void updateLineData(final String filePath,final int diaNum,final int scrollX,final int scrollY,final int scaleX,final int scaleY){
        ContentValues cv=new ContentValues();
        cv.put(DIA_SCROLL_X,scrollX);
        cv.put(DIA_SCROLL_Y,scrollY);
        cv.put(DIA_SCALE_X,scaleX);
        cv.put(DIA_SCALE_Y,scaleY);
        getWritableDatabase().update(TABLE_LINEDATA,cv,FILE_PATH+" = ? AND "+DIA_NUM+" = ?",new String[]{filePath,""+diaNum});
    }


    /**
     * 各路線ファイル、各画面のスクロール位置を読みだす
     *
     * @param db
     * @param filePath
     * @param diaNum
     * @param key 方向識別　下り路線時刻表=0　上り路線時刻表=1 ダイヤグラム=2
     * @return
     * @see #updateLineData(String, int, int, int, int)  データの保存方法
     */
    public int[] getPositionData(SQLiteDatabase db, String filePath, int diaNum, int key){
        Cursor cursor = null;
        try{
            cursor = db.query(TABLE_LINEDATA, null,FILE_PATH+" = ? AND "+DIA_NUM+" = ?",new String[]{filePath,""+diaNum}, null, null, null );
            if(cursor.getCount()==0){
                throw new Exception("no data in lineData");
            }
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
        catch(Exception e){
            switch(key){
                case 0:
                    return new int[]{0,0};
                case 1:
                    return new int[]{0,0};
                case 2:
                    return new int[]{0,0,0,0};
                default:
                    return null;
            }
        }
        finally{
            if( cursor != null ){
                cursor.close();
            }
        }
    }

    /**
     * ファイル履歴を追加する。
     * ファイルを開いた際にファイルパスを使って呼び出す。
     * データベースに同名のファイルが記録されている場合はそれを削除したのち
     * 引数のファイルが追加される。
     * @param filePath
     * @see #getHistory()
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
     * @see #addHistory(String)
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
     * 複数ファイル読み込みに対応するために用意された
     * アプリが終了されるときに呼び出される
     * @param filePaths
     * @see #getFilePaths()
     */
    public void addFilePaths(String[] filePaths){
        getWritableDatabase().delete(TABLE_PREVIEW,null,null);

        for(int i=0;i<filePaths.length;i++){
            ContentValues values=new ContentValues();
            values.put(DBHelper.FILE_PATH,filePaths[i]);
            getWritableDatabase().insert(TABLE_PREVIEW,null,values);
        }
    }
    /**
     *　前回開いていたファイルを読み込む
     * 　前回の復元の操作を行ったときに呼び出される
     *
     */
    public String[] getFilePaths(){
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
    public static final String LINE_TIME_TABLE="LineTimeTable";
    public static final String DIAGRAM="Diagram";
    public static final String STATION_TIME_TABLE="StationTimeTable";
    public static final String STATION_TIME_INDEX="StationTimeIndex";
    public static final String COMMENT="comment";
    public static final String HELP="help";
    public static final int WINDOW_NUM=5;

    /**
     * 開いていた画面の記録（v1.0以降の多い画面用)
     * @param windows サイズ５の文字列配列。
     *                それぞれの文字列は識別子をハイフンで繋がれたもの
     *                識別子については別記する
     * @see #readWindows()
     */
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

    /**
     * @see #readWindows()  readWindowsで保存したFragment識別子を読み込む
     * @return
     */
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
