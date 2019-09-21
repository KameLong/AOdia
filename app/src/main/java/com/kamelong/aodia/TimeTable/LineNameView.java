package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;

public class LineNameView extends TimeTableDefaultView{

    /**
     * デフォルトコンストラクタ
     * @param context　このViewを表示するactivity
     */
    public LineNameView(Context context,TimeTableOptions options) {
        super(context,options);
    }

    /**
     * onDraw.
     * 路線名とダイヤ名を表示してから、
     * 設定画面の列車名の表示のフラグを確認し、"列車名"の表示を行う
     * @param canvas
     */
    public void onDraw(Canvas canvas){
        int startLine=0;
        if(options.trainEdit){
            int bitmapSize=(int)(textSize*2.5);
            if(bitmapSize>getXsize()){
                bitmapSize=getXsize();
            }

            startLine+=bitmapSize;
            startLine+=textSize;
            startLine+=textSize;
            startLine+=normalSpace;
            canvas.drawLine(0,startLine,getWidth(),startLine,blackPaint);
            startLine+=smallSpace;

        }

        textPaint.setColor(Color.BLACK);
        if(options.showOperation) {
            startLine+=textSize;
            canvas.drawText(activity.getString(R.string.operationNumber), 5, startLine, textPaint);
        }
        startLine += textSize;
        canvas.drawText(activity.getString(R.string.trainNumber), 5,startLine, textPaint);
        startLine+=textSize;
        canvas.drawText(activity.getString(R.string.trainType), 5,startLine, textPaint);
        if(options.showTrainName){
            startLine+=smallSpace;
            canvas.drawLine(0,startLine,getWidth(),startLine,blackPaint);
            int startX=(int)((getWidth()-textPaint.getTextSize())/2);
            startLine+=normalSpace;
            startLine+=textSize;
            canvas.drawText("列",startX,startLine,textPaint);
            startLine+=textSize;
            canvas.drawText("車",startX,startLine,textPaint);
            startLine+=textSize;
            canvas.drawText("名",startX,startLine,textPaint);
            startLine+=textSize*5;

        }

        startLine+=normalSpace;
        canvas.drawLine(0,startLine,getWidth(),startLine,blackPaint);

    }


    @Override
    int getXsize() {
        return textSize*5;
    }

    @Override
    int getYsize() {
        int startLine=0;
        if(options.trainEdit){
            int bitmapSize=(int)(textSize*2.5);
            if(bitmapSize>getXsize()){
                bitmapSize=getXsize();
            }

            startLine+=bitmapSize;
            startLine+=textSize;
            startLine+=textSize;
            startLine+=normalSpace;
            startLine+=smallSpace;

        }

        if(options.showOperation) {
            startLine+=textSize;
        }
        startLine+=2*textSize;
        if(options.showTrainName){
            startLine+=smallSpace;
            startLine+=normalSpace;
            startLine+=textSize*8;
        }
        startLine+=smallSpace;


        return startLine;
    }
}