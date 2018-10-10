package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaDefaultView;

public class TrainNameView extends AOdiaDefaultView {
    private DiaFile diaFile;
    private Train train;
    private int direct;
    private boolean secondFrag=false;
    private boolean showTrainName=false;
    TrainNameView(Context context){
        super(context);
    }
    TrainNameView(Context context,DiaFile diaFile,Train t){
        this(context);
        this.diaFile=diaFile;
        train=t;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        showTrainName=spf.getBoolean("trainName",showTrainName);
    }
    protected int getYsize(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("trainName",false)){
            return (int)(textPaint.getTextSize()*11.2f);
        }else{
            return (int)(textPaint.getTextSize()*3.2f);
        }
    }
    public void onDraw(Canvas canvas){
        int startLine=textSize;
        textPaint.setColor(diaFile.trainType.get(train.type).textColor.getAndroidColor());
        canvas.drawText(train.operationName, 5,startLine, textPaint);
        startLine=startLine+textSize;
        canvas.drawText(train.number, 5,startLine, textPaint);
        startLine=startLine+textSize;
        canvas.drawText(diaFile.trainType.get(train.type).shortName, 5,startLine, textPaint);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),blackPaint);

    }


    protected int getXsize(){
        return (int)(textPaint.getTextSize()*2.5);
    }


}
