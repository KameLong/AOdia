package com.kamelong.aodia.detabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kamelong.OuDia.Train;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

/**
 * @author KameLong
 * <p>
 * AOdia内で用いるデータベースを管理するクラス
 * Activityが閉じられても保持しなければならないデータはすべてこのデータベースに保存する
 * <p>
 * v1.0以降データベースはaodia.dbを使用する
 * <p>
 * データベースに保存するデータについてはonCreate内のコメントを参照
 */
public class AOdiaDetabase extends SQLiteOpenHelper {
    public static final String DETABASE_NAME = "aodia.db";
    public static final int DETABASE_VERSION = 4;
    public static final String ID = "_id";

    public static final String DIRECTORY_PATH = "directoryPath";
    public static final String FILE_NAME = "fileName";

    public static final String FILE_PATH = "filePath";
    public static final String DIA_NUM = "diaNum";
    public static final String DOWN_SCROLL_X = "downScrollX";
    public static final String DOWN_SCROLL_Y = "downScrollY";
    public static final String UP_SCROLL_X = "upScrollX";
    public static final String UP_SCROLL_Y = "upScrollY";
    public static final String DIA_SCROLL_X = "diaScrollX";
    public static final String DIA_SCROLL_Y = "diaScrollY";
    public static final String DIA_SCALE_X = "diaScaleX";
    public static final String DIA_SCALE_Y = "diaScaleY";


    public static final String STATION_ID = "stationID";
    public static final String STATION_NAME = "stationName";


    public static final String TABLE_LINEDATA = "lineData";
    public static final String TABLE_HISTORY = "history";

    public static final String TABLE_SAME_STATION = "sameStation";
    public static final String TABLE_STATION = "stationList";
    private ContentValues values;

    public AOdiaDetabase(Context context) {

        super(context, DETABASE_NAME, null, DETABASE_VERSION);
    }

    /**
     * このデータベースを初めて使用する時に実行される処理
     * テーブルの作成や初期データの投入を行う
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            /*line dataのテーブルには
            各ファイル各ダイヤの上り下り、ダイヤグラムに対するデータが入っている
            それぞれに対し、現在のスクロール位置
            ダイヤグラムに対しては拡大縮小率も保存する
            */
            db.execSQL(
                    "create table " + TABLE_LINEDATA + " ("
                            + ID + " integer primary key autoincrement not null, "
                            + FILE_PATH + " text not null, "
                            + DIA_NUM + " Integer not null, "
                            + DOWN_SCROLL_X + " Integer, "
                            + DOWN_SCROLL_Y + " Integer, "
                            + UP_SCROLL_X + " Integer, "
                            + UP_SCROLL_Y + " Integer, "
                            + DIA_SCROLL_X + " Integer, "
                            + DIA_SCROLL_Y + " Integer, "
                            + DIA_SCALE_X + " Float, "
                            + DIA_SCALE_Y + " Float )");

            /*
            オープンしたファイルの履歴を保存する
             */

            db.execSQL(
                    "create table " + TABLE_HISTORY + "("
                            + ID + " integer primary key autoincrement not null, "
                            + FILE_PATH + " text)");
            /*
            駅とファイルパスを対応させる
             */
            db.execSQL(
                    "create table " + TABLE_STATION + " ("
                            + ID + " integer primary key autoincrement not null, "
                            + STATION_NAME + " text, "
                            + DIRECTORY_PATH + " text, "
                            + FILE_NAME + " text)");


        } catch (Exception e) {
            SDlog.log("データベースの作成に失敗しました");
            SDlog.log(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if (i < 4) {
            try {
                db.execSQL(
                        "drop table " + TABLE_SAME_STATION);
            } catch (Exception e) {
            }
            try {
                db.execSQL(
                        "drop table " + TABLE_STATION);
            } catch (Exception e) {
            }
            db.execSQL(
                    "create table " + TABLE_STATION + " ("
                            + ID + " integer primary key autoincrement not null, "
                            + STATION_NAME + " text, "
                            + DIRECTORY_PATH + " text, "
                            + FILE_NAME + " text)");
        }
    }


    /**
     * line deta をアップデートする
     * これはダイヤグラムのデータを更新するためのメソッド
     */
    public void updateLineData(final String filePath, final int diaNum, final int scrollX, final int scrollY, final int scaleX, final int scaleY) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_LINEDATA, null, FILE_PATH + " = ? AND " + DIA_NUM + " = ?", new String[]{filePath, "" + diaNum}, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null || cursor.getCount() == 0) {
            addNewLineData(filePath, diaNum);

        }
        if (cursor != null) {
            cursor.close();
        }
        ContentValues cv = new ContentValues();
        cv.put(DIA_SCROLL_X, scrollX);
        cv.put(DIA_SCROLL_Y, scrollY);
        cv.put(DIA_SCALE_X, scaleX);
        cv.put(DIA_SCALE_Y, scaleY);
        getWritableDatabase().update(TABLE_LINEDATA, cv, FILE_PATH + " = ? AND " + DIA_NUM + " = ?", new String[]{filePath, "" + diaNum});
    }

    /**
     * line deta をアップデートする
     * これはダイヤグラムのデータを更新するためのメソッド
     */
    public void updateLineData(final String filePath, final int diaIndex, final int direction, final int scrollX, final int scrollY) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE_LINEDATA, null, FILE_PATH + " = ? AND " + DIA_NUM + " = ?", new String[]{filePath, "" + diaIndex}, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor == null || cursor.getCount() == 0) {
            addNewLineData(filePath, diaIndex);

        }
        if (cursor != null) {
            cursor.close();
        }
        ContentValues cv = new ContentValues();
        if (direction == Train.DOWN) {
            cv.put(DOWN_SCROLL_X, scrollX);
            cv.put(DOWN_SCROLL_Y, scrollY);
        } else {
            cv.put(UP_SCROLL_X, scrollX);
            cv.put(UP_SCROLL_Y, scrollY);
        }
        getWritableDatabase().update(TABLE_LINEDATA, cv, FILE_PATH + " = ? AND " + DIA_NUM + " = ?", new String[]{filePath, "" + diaIndex});
    }

    private void addNewLineData(final String filePath, final int diaNum) {
        getWritableDatabase().delete(TABLE_LINEDATA, FILE_PATH + "=? and " + DIA_NUM + "=?", new String[]{filePath, "" + diaNum});

        ContentValues cv = new ContentValues();
        cv.put(FILE_PATH, filePath);
        cv.put(DIA_NUM, diaNum);
        cv.put(UP_SCROLL_X, 0);
        cv.put(UP_SCROLL_Y, 0);
        cv.put(DOWN_SCROLL_X, 0);
        cv.put(DIA_SCALE_Y, 0);
        cv.put(DIA_SCROLL_X, 0);
        cv.put(DIA_SCROLL_Y, 0);
        cv.put(DIA_SCALE_X, 0.1);
        cv.put(DIA_SCALE_Y, 0.3);
        getWritableDatabase().insert(TABLE_LINEDATA, null, cv);

    }


    /**
     * 各路線ファイル、各画面のスクロール位置を読みだす
     *
     */
    public int[] getPositionData(String filePath, int diaNum, int key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_LINEDATA, null, FILE_PATH + " = ? AND " + DIA_NUM + " = ?", new String[]{filePath, "" + diaNum}, null, null, null);
            if (cursor.getCount() == 0) {
                throw new Exception("no data in lineData");
            }
            cursor.moveToFirst();
            int[] result;
            switch (key) {
                case 0:
                    result = new int[2];
                    result[0] = cursor.getInt(3);
                    result[1] = cursor.getInt(4);
                    cursor.close();
                    return result;
                case 1:
                    result = new int[2];
                    result[0] = cursor.getInt(5);
                    result[1] = cursor.getInt(6);
                    cursor.close();
                    return result;
                case 2:
                    result = new int[4];
                    result[0] = cursor.getInt(7);
                    result[1] = cursor.getInt(8);
                    result[2] = cursor.getInt(9);
                    result[3] = cursor.getInt(10);
                    cursor.close();
                    return result;
                default:
                    return null;
            }
        } catch (Exception e) {
            switch (key) {
                case 0:
                    return new int[]{0, 0};
                case 1:
                    return new int[]{0, 0};
                case 2:
                    return new int[]{0, 0, 0, 0};
                default:
                    return null;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * ファイル履歴を追加する。
     * ファイルを開いた際にファイルパスを使って呼び出す。
     * データベースに同名のファイルが記録されている場合はそれを削除したのち
     * 引数のファイルが追加される。
     *
     * @param filePath
     * @see #getHistory()
     */
    public void addHistory(String filePath) {
        try {

            getWritableDatabase().delete(TABLE_HISTORY, FILE_PATH + "=?", new String[]{filePath});
            ContentValues values = new ContentValues();
            values.put(FILE_PATH, filePath);
            getWritableDatabase().insert(TABLE_HISTORY, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ファイル履歴を取得する。
     *
     * @return 過去最大10件分の履歴をファイルパスの文字列にして返す。
     * @see #addHistory(String)
     */
    public String[] getHistory() {
        String[] result;
        Cursor c = getReadableDatabase().rawQuery("select * from " + TABLE_HISTORY + " ORDER BY " + ID + " DESC" + ";", null);
        if (c.getCount() > 10) {
            result = new String[10];
        } else {
            result = new String[c.getCount()];
        }
        c.moveToFirst();
        int i = 0;
        while (!c.isAfterLast() && i < 10) {
            result[i] = c.getString(1);
            i++;
            c.moveToNext();
        }
        c.close();
        return result;
    }


    /**
     * TABLE_STATIONに新しい駅データを付け加える。
     * 駅名とそのファイルパスをセットにして追加する。
     *
     * @param stationName
     * @param filePath
     */
    private void addStation(SQLiteDatabase db, ArrayList<String> stationName, String filePath) {
        try{
        String directory = filePath.substring(0, filePath.lastIndexOf("/"));
        String name = filePath.substring(filePath.lastIndexOf("/") + 1);
        db.delete(TABLE_STATION, DIRECTORY_PATH + "=? and " + FILE_NAME + "=?", new String[]{directory, name});
        for (int i = 0; i < stationName.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(DIRECTORY_PATH, directory);
            values.put(FILE_NAME, name);
            values.put(STATION_NAME, stationName.get(i));
            db.insert(TABLE_STATION, null, values);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * TABLE_STATIONに新しい駅データを付け加える。
     * 駅名とそのファイルパスをセットにして追加する。
     *
     * @param stationName
     * @param filePath
     */
    public void addStation(ArrayList<String>[] stationName, String[] filePath) {
        final SQLiteDatabase db= getWritableDatabase();

        try {
            db.beginTransaction();

            if (filePath.length > 0) {
                String directory = filePath[0].substring(0, filePath[0].lastIndexOf("/"));
                db.delete(TABLE_STATION, DIRECTORY_PATH + "=?", new String[]{directory});
            }
            for (int i = 0; i < stationName.length; i++) {
                addStation(db,stationName[i], filePath[i]);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

    }


    public ArrayList<String> searchFileFromStation(String stationName, String directoy) {
        ArrayList<String> result = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("select " + FILE_NAME + " from " + TABLE_STATION + " where " + FILE_NAME + " like ? and " + DIRECTORY_PATH + " like ?", new String[]{stationName, directoy});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String name = c.getString(0);
            if (!result.contains(name)) {
                result.add(name);
            }
            c.moveToNext();
        }
        c = getReadableDatabase().rawQuery("select " + FILE_NAME + " from " + TABLE_STATION + " where " + STATION_NAME + " like ? and " + DIRECTORY_PATH + " like ?", new String[]{stationName, directoy});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String name = c.getString(0);
            if (!result.contains(name)) {
                result.add(name);
            }
            c.moveToNext();
        }
        stationName = "%" + stationName + "%";
        c = getReadableDatabase().rawQuery("select " + FILE_NAME + " from " + TABLE_STATION + " where " + FILE_NAME + " like ? and " + DIRECTORY_PATH + " like ?", new String[]{stationName, directoy});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String name = c.getString(0);
            if (!result.contains(name)) {
                result.add(name);
            }
            c.moveToNext();
        }
        c = getReadableDatabase().rawQuery("select " + FILE_NAME + " from " + TABLE_STATION + " where " + STATION_NAME + " like ? and " + DIRECTORY_PATH + " like ?", new String[]{stationName, directoy});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String name = c.getString(0);
            if (!result.contains(name)) {
                result.add(name);
            }
            c.moveToNext();
        }
        c.close();
        return result;

    }

    public ArrayList<String> searchFileFromStation(String stationName, String directoy, boolean approximateMatch) {
        if (approximateMatch) {
            return searchFileFromStation(stationName, directoy);
        }
        ArrayList<String> result = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("select " + FILE_NAME + " from " + TABLE_STATION + " where " + STATION_NAME + " like ? and " + DIRECTORY_PATH + " like ?", new String[]{stationName, directoy});
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            String name = c.getString(0);
            if (!result.contains(name)) {
                result.add(name);
            }
            c.moveToNext();
        }
        return result;

    }
}
