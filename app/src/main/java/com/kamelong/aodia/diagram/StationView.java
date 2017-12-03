package com.kamelong.aodia.diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaStation;
import com.kamelong.aodia.timeTable.KLView;

import java.util.ArrayList;

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
 * ダイヤグラム表示画面において、駅名を表示するView
 * scaleサイズに合わせて、駅間距離を調整する
 *
 * 駅間距離は最小所要時間に比例するようにする
 *
 */
public class StationView extends KLView {
    private AOdiaDiaFile diaFile;
    private AOdiaStation station;
    private DiagramSetting setting;
    private final int yshift=30;
    private int diaNum;
    private float scaleX =15;
    private float scaleY =42;
    private Paint paint = new Paint();

    private ArrayList<Integer>stationTime=new ArrayList<>();
    private StationView(Context context){
        super(context);
    }
    StationView(Context context, DiagramSetting s, AOdiaDiaFile dia, int num){
       this(context);
        setting=s;
        diaFile=dia;
        diaNum=num;
        stationTime=station.getStationTime();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        final float defaultLineSize=getResources().getDisplayMetrics().densityDpi / 160f;


        textPaint.setColor(Color.BLACK);
        textPaint.setTextSkewX(0);
        textPaint.setTypeface(Typeface.DEFAULT);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200,200,200));
        paint.setStrokeWidth(defaultLineSize);
        canvas.drawLine(getWidth()-2, yshift,getWidth()-2, stationTime.get(station.getStationNum()-1) * scaleY / 60+(int)textPaint.getTextSize()+yshift, paint);
        for(int i=0;i< station.getStationNum();i++){
            //主要駅なら太字にする
            if(station.bigStation(i)){
                paint.setStrokeWidth(defaultLineSize);
            }else{
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize()+yshift,1440* scaleX,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize()+yshift,paint);
            canvas.drawText(station.getStation(i).getName(),2,stationTime.get(i)* scaleY /60+(int)textPaint.getTextSize()*5/6+yshift,textPaint);
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
            return (int)(stationTime.get(station.getStationNum()-1)* scaleY /60+(int)textPaint.getTextSize()+4)+yshift*2;
    }
    public void setScale(float x,float y){
        scaleX =x;
        scaleY =y;
    }
}
