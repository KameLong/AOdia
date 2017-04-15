package com.fc2.web.kamelong.aodia.diagram;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.timeTable.KLView;

import java.util.ArrayList;

/**
 * Created by kame on 2016/12/01.
 */
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
public class StationView extends KLView {
    DiaFile diaFile;
    DiagramSetting setting;
    int diaNum;
    public  float scaleX =15;
    public  float scaleY =42;
    private ArrayList<Integer>stationTime=new ArrayList<Integer>();
    StationView(Context context){
        super(context);
    }
    StationView(Context context, DiagramSetting s,DiaFile dia, int num){
       this(context);
        setting=s;
        diaFile=dia;
        diaNum=num;
        stationTime=dia.getStationTime();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        textPaint.setColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200,200,200));
        paint.setStrokeWidth(4);
        canvas.drawLine(getWidth()-2, 0,getWidth()-2, stationTime.get(diaFile.getStationNum()-1) * scaleY / 60+(int)textPaint.getTextSize(), paint);
        for(int i=0;i< diaFile.getStationNum();i++){
            if(diaFile.getStation(i).getBigStation()){
                paint.setStrokeWidth(4);
            }else{
                paint.setStrokeWidth(2);
            }
            canvas.drawLine(0,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize(),1440* scaleX,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize(),paint);
            canvas.drawText(diaFile.getStationName(i),2,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize()*5/6,textPaint);
        }
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.getSize(heightMeasureSpec)>getYsize()){
            this.setMeasuredDimension(getXsize(),MeasureSpec.getSize(heightMeasureSpec));
        }else{
            this.setMeasuredDimension(getXsize(),getYsize());
        }
    }
    protected int getXsize(){
            return (int)(textPaint.getTextSize()*5)+2;
    }
    protected int getYsize(){
            return (int)(stationTime.get(diaFile.getStationNum()-1)* scaleY /60+(int)textPaint.getTextSize()+4);
    }
    public void setScale(float x,float y){
        scaleX =x;
        scaleY =y;
    }
}
