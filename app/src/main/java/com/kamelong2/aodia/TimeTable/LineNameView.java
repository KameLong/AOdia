package com.kamelong2.aodia.TimeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.aodia.AOdiaDefaultView;

/**
 * 時刻表画面の左上に路線名とダイヤ名を表示する部分がある。
 * そのためのView
 * @author kamelong
 */
public class LineNameView extends AOdiaDefaultView {
    DiaFile diaFile;
    int diaNum;

    public LineNameView(Context context) {
        super(context);
    }

    /**
     * デフォルトコンストラクタ
     * @param context　このViewを表示するactivity
     * @param diaFile　このViewに表示するダイヤファイル
     * @param num　このViewに表示するダイヤのインデックス
     */
    public LineNameView(Context context,DiaFile diaFile,int num){
        this(context);
        this.diaFile =diaFile;
        diaNum=num;
    }

    /**
     * onDraw.
     * 路線名とダイヤ名を表示してから、
     * 設定画面の列車名の表示のフラグを確認し、"列車名"の表示を行う
     * @param canvas
     */
    public void onDraw(Canvas canvas){
        int startLine=(int)textPaint.getTextSize();
        textPaint.setColor(Color.BLACK);
        canvas.drawText("運用番号", 5,startLine, textPaint);
        startLine=startLine+(int)textPaint.getTextSize();
        canvas.drawText("列車番号", 5,startLine, textPaint);
        startLine=startLine+(int)textPaint.getTextSize();
        canvas.drawText("列車種別", 5,startLine, textPaint);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("trainName",true)){
            canvas.drawLine(0,textPaint.getTextSize()*3.3f,getWidth(),textPaint.getTextSize()*3.3f,blackPaint);
            int startX=(int)((getWidth()-textPaint.getTextSize())/2);
            canvas.drawText("列",startX,textPaint.getTextSize()*4.4f,textPaint);
            canvas.drawText("車",startX,textPaint.getTextSize()*5.7f,textPaint);
            canvas.drawText("名",startX,textPaint.getTextSize()*7.0f,textPaint);
        }
    }

    /**
     * 列車名の表示を行う際は縦サイズを大きくする
     * @return　縦サイズ
     */
    protected int getYsize(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("trainName",true)){
            return (int)(textPaint.getTextSize()*11.2f);
        }else{
            return (int)(textPaint.getTextSize()*3.2f);
        }
    }

    /**
     * 駅名欄と同じく横は５文字分のサイズ
     * @return　横サイズ
     */
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*5);
    }
}
