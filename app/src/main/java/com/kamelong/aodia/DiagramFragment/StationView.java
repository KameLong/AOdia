package com.kamelong.aodia.DiagramFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.kamelong.OuDia.LineFile;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

public class StationView extends DiagramDefaultView {
    LineFile lineFile;
    private ArrayList<Integer> stationTime;

    StationView(Context context, DiagramOptions option, LineFile lineFile){
        super(context,option);
        this.lineFile =lineFile;
        stationTime=lineFile.getStationTime();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(lineFile.getStationNum()==0)return;
        final float defaultLineSize=getResources().getDisplayMetrics().densityDpi / 160f;


        textPaint.setColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200,200,200));
        paint.setStrokeWidth(defaultLineSize);
        canvas.drawLine(getWidth()-2, 0,getWidth()-2, stationTime.get(lineFile.getStationNum()-1) * options.scaleY+(int)textPaint.getTextSize(), paint);
        for(int i = 0; i< lineFile.getStationNum(); i++){
            //主要駅なら太字にする
            if(lineFile.station.get(i).bigStation){
                paint.setStrokeWidth(defaultLineSize);
            }else{
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,stationTime.get(i)* options.scaleY+(int)textPaint.getTextSize(),getWidth(),stationTime.get(i)* options.scaleY+(int)textPaint.getTextSize(),paint);
            canvas.drawText(lineFile.station.get(i).name,2,stationTime.get(i)* options.scaleY+(int)textPaint.getTextSize()*5/6,textPaint);
        }
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(View.MeasureSpec.getSize(heightMeasureSpec)>getYsize()){
            this.setMeasuredDimension(getXsize(), View.MeasureSpec.getSize(heightMeasureSpec));
        }else{
            this.setMeasuredDimension(getXsize(),getYsize());
        }
    }
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*stationWidth)+2;
    }
    protected int getYsize(){
        if(lineFile.getStationNum()==0)return 1000;
        return (int)(stationTime.get(lineFile.getStationNum()-1)* options.scaleY+(int)textPaint.getTextSize()+4);
    }

}
