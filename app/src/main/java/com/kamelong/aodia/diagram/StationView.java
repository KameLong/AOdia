package com.kamelong.aodia.Diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaDefaultView;

import java.util.ArrayList;

public class StationView extends AOdiaDefaultView {
    DiaFile diaFile;
    DiagramSetting setting;
    private ArrayList<Integer> stationTime=new ArrayList<Integer>();
    StationView(Context context){
        super(context);
    }

    StationView(Context context, DiagramSetting s,DiaFile dia){
        this(context);
        setting=s;
        diaFile=dia;
        stationTime=dia.getStationTime();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        final float defaultLineSize=getResources().getDisplayMetrics().densityDpi / 160f;


        textPaint.setColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200,200,200));
        paint.setStrokeWidth(defaultLineSize);
        canvas.drawLine(getWidth()-2, 0,getWidth()-2, stationTime.get(diaFile.getStationNum()-1) * setting.scaleY+(int)textPaint.getTextSize(), paint);
        for(int i=0;i< diaFile.getStationNum();i++){
            //主要駅なら太字にする
            if(diaFile.station.get(i).bigStation){
                paint.setStrokeWidth(defaultLineSize);
            }else{
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,stationTime.get(i)* setting.scaleY+(int)textPaint.getTextSize(),getWidth(),stationTime.get(i)* setting.scaleY+(int)textPaint.getTextSize(),paint);
            canvas.drawText(diaFile.station.get(i).name,2,stationTime.get(i)* setting.scaleY+(int)textPaint.getTextSize()*5/6,textPaint);
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
        if(stationTime.size()!=diaFile.getStationNum()){
            Toast.makeText(getContext(), "原因不明のエラーです。駅数が変更された可能性があります。StationView.java", Toast.LENGTH_SHORT).show();
            return 1000;
        }
        return (int)(stationTime.get(diaFile.getStationNum()-1)* setting.scaleY+(int)textPaint.getTextSize()+4);
    }

}
