package com.kamelong.aodia.detabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kamelong.aodia.SDlog;

import java.util.ArrayList;
import java.util.Arrays;

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
public class AOdiaDetabase extends SQLiteOpenHelper {
    public static final String DETABASE_NAME="aodia.db";
    public static final int DETABASE_VERSION=3;
    public static final String ID="_id";

    public static final String DIRECTORY_PATH="directoryPath";
    public static final String FILE_PATH="filePath";
    public static final String FILE_NAME="fileName";
    public static final String DIA_NUM="diaNum";
    public static final String DOWN_SCROLL_X="downScrollX";
    public static final String DOWN_SCROLL_Y="downScrollY";
    public static final String UP_SCROLL_X="upScrollX";
    public static final String UP_SCROLL_Y="upScrollY";
    public static final String DIA_SCROLL_X="diaScrollX";
    public static final String DIA_SCROLL_Y="diaScrollY";
    public static final String DIA_SCALE_X="diaScaleX";
    public static final String DIA_SCALE_Y="diaScaleY";

    public static final String STATION_ID="stationID";
    public static final String STATION_NAME="stationName";


    public static final String TABLE_LINEDATA="lineData";
    public static final String TABLE_HISTORY="history";

    public static final String TABLE_SAME_STATION="sameStation";
    public static final String TABLE_STATION="stationList";
    public AOdiaDetabase(Context context){

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

            /*
            オープンしたファイルの履歴を保存する
             */

            db.execSQL(
                    "create table "+TABLE_HISTORY+"("
                            +ID+" integer primary key autoincrement not null, "
                            +FILE_PATH+ " text)");
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
            /*
            駅とファイルパスを対応させる
             */
            db.execSQL(
                    "create table " + TABLE_STATION + " ("
                            + ID + " integer primary key autoincrement not null, "
                            + STATION_NAME + " text, "
                            + DIRECTORY_PATH + " text, "
                            +FILE_NAME+" text)");



        }catch(Exception e){
            SDlog.log("データベースの作成に失敗しました");
            SDlog.log(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if(i<3){
            try {
                db.execSQL(
                        "drop table " + TABLE_STATION);
            }catch (Exception e){}
            db.execSQL(
                    "create table " + TABLE_STATION + " ("
                            + ID + " integer primary key autoincrement not null, "
                            + STATION_NAME + " text, "
                            + DIRECTORY_PATH + " text, "
                            +FILE_NAME+" text)");
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
            SDlog.log(e);

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
     * scrollXは該当列車index
     * @see #addNewFileToLineData(String, int)
     * @see #getPositionData( String, int, int)  保存したデータを読み出す
     */
    public void updateLineData(final String filePath,final int diaNum,final int direct,final int trainIndex){
        ContentValues cv=new ContentValues();
        if(direct==0){
            cv.put(DOWN_SCROLL_X,trainIndex);
        }else{
            cv.put(UP_SCROLL_X,trainIndex);
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
     * @see #getPositionData(String, int, int)  保存したデータを読み出す
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
     * @param filePath
     * @param diaNum
     * @param key 方向識別　下り路線時刻表=0　上り路線時刻表=1 ダイヤグラム=2
     * @return
     * @see #updateLineData(String, int, int, int)  データの保存方法
     */
    public int[] getPositionData(String filePath, int diaNum, int key){
        SQLiteDatabase db=getReadableDatabase();
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
                    result=new int[1];
                    result[0]=cursor.getInt(3);
                    cursor.close();
                    return result;
                case 1:
                    result=new int[1];
                    result[0]=cursor.getInt(5);
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
            values.put(this.FILE_PATH, filePath);
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
     * TABLE_STATIONに新しい駅データを付け加える。
     * 駅名とそのファイルパスをセットにして追加する。
     * @param stationName
     * @param filePath
     */
    public void addStation(ArrayList<String> stationName, String filePath){
            String directory=filePath.substring(0,filePath.lastIndexOf("/"));
            String name=filePath.substring(filePath.lastIndexOf("/")+1);
            getWritableDatabase().delete(TABLE_STATION, DIRECTORY_PATH + "=? and "+FILE_NAME+"=?", new String[]{directory,name});
            for(int i=0;i<stationName.size();i++) {
                ContentValues values = new ContentValues();
                values.put(DIRECTORY_PATH, directory);
                values.put(FILE_NAME, name);
                values.put(STATION_NAME, stationName.get(i));
                getWritableDatabase().insert(TABLE_STATION, null, values);
            }
    }
    /**
     * TABLE_STATIONに新しい駅データを付け加える。
     * 駅名とそのファイルパスをセットにして追加する。
     * @param stationName
     * @param filePath
     */
    public void addStation(ArrayList<String>[] stationName, String[] filePath){
        if(filePath.length>0){
            String directory=filePath[0].substring(0,filePath[0].lastIndexOf("/"));
            getWritableDatabase().delete(TABLE_STATION, DIRECTORY_PATH + "=?" , new String[]{directory});
        }
        try {
            for (int i = 0; i < stationName.length; i++) {
                addStation(stationName[i], filePath[i]);
            }
            getWritableDatabase().setTransactionSuccessful();
        }
        catch(Exception e){
         e.printStackTrace();
    }finally {
        getWritableDatabase().endTransaction();
        getWritableDatabase().close();
    }

        /*
                try {
            String directory=filePath.substring(0,filePath.lastIndexOf("/"));
            String name=filePath.substring(filePath.lastIndexOf("/")+1);
            getWritableDatabase().delete(TABLE_STATION, DIRECTORY_PATH + "=? and "+FILE_NAME+"=?", new String[]{directory,name});
            for(int i=0;i<stationName.size();i++) {
                ContentValues values = new ContentValues();
                values.put(DIRECTORY_PATH, directory);
                values.put(FILE_NAME, name);
                values.put(STATION_NAME, stationName.get(i));
                getWritableDatabase().insert(TABLE_STATION, null, values);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
         */
    }

    public void addStation(String[] stationName, String filePath){
        addStation(new ArrayList<String>(Arrays.asList(stationName)),filePath);
    }
    public ArrayList<String>searchFileFromStation(String stationName,String directoy){
        ArrayList<String>result=new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("select "+FILE_NAME+" from " + TABLE_STATION + " where " + FILE_NAME + " like ? and "+DIRECTORY_PATH+" like ?", new String[]{stationName,directoy});
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            String name=c.getString(0);
            if(!result.contains(name)){
                result.add(name);
            }
            c.moveToNext();
        }
        c = getReadableDatabase().rawQuery("select "+FILE_NAME+" from " + TABLE_STATION + " where " + STATION_NAME + " like ? and "+DIRECTORY_PATH+" like ?", new String[]{stationName,directoy});
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            String name=c.getString(0);
            if(!result.contains(name)){
                result.add(name);
            }
            c.moveToNext();
        }
        stationName="%"+stationName+"%";
        c = getReadableDatabase().rawQuery("select "+FILE_NAME+" from " + TABLE_STATION + " where " + FILE_NAME + " like ? and "+DIRECTORY_PATH+" like ?", new String[]{stationName,directoy});
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            String name=c.getString(0);
            if(!result.contains(name)){
                result.add(name);
            }
            c.moveToNext();
        }
        c = getReadableDatabase().rawQuery("select "+FILE_NAME+" from " + TABLE_STATION + " where " + STATION_NAME + " like ? and "+DIRECTORY_PATH+" like ?", new String[]{stationName,directoy});
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            String name=c.getString(0);
            if(!result.contains(name)){
                result.add(name);
            }
            c.moveToNext();
        }
        c.close();
        return result;

    }

    public ArrayList<String>searchFileFromStation(String stationName,String directoy,boolean approximateMatch){
        if(approximateMatch){
           return  searchFileFromStation(stationName,directoy);
        }
        ArrayList<String>result=new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("select "+FILE_NAME+" from " + TABLE_STATION + " where " + STATION_NAME + " like ? and "+DIRECTORY_PATH+" like ?", new String[]{stationName,directoy});
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            String name=c.getString(0);
            if(!result.contains(name)){
                result.add(name);
            }
            c.moveToNext();
        }
        return result;

    }
}
