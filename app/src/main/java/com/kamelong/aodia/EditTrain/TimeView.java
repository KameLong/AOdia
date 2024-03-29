package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.kamelong.aodia.TimeTable.TimeTableDefaultView;

//時刻を表示するView
public class TimeView extends LabelView {
    protected static final int HEIGHT=30;
    public TimeView(Context context,int time) {
        super(context);
        this.setTextSize(15);
        this.setTime(time);

        setPadding(10, 5, 10, 5);

        this.setHeight((int)(HEIGHT));
    }
    public void setTime(int time){
        this.setText(timeInt2String(time));
    }
    protected String timeInt2String(int time){
        if(time<0)return"";
        int ss=time%60;
        time=time/60;
        int mm=time%60;
        time=time/60;
        int hh=time%24;
        return String.format("%02d", hh) + " " + String.format("%02d", mm) + " " + String.format("%02d", ss);
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1, TimeTableDefaultView.blackPaint);
    }
    public void setCheck(boolean check){
        if(check){
            this.setBackgroundColor(Color.YELLOW);
        }else{
            this.setBackgroundColor(Color.WHITE);
        }
    }

}
