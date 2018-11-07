package com.kamelong.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.widget.TextView;

import com.kamelong.aodia.AOdiaDefaultView;

public class StationNameTextView extends AppCompatTextView{
    public int stationNumber;
    public StationNameTextView(Context context,String text,int station){
        super(context);
        stationNumber=station;
        setTextColor(Color.BLACK);
        setTextSize(15);
        if(text.length()>5){
            setText(text.substring(0,5));
        }else{
            setText(text);
        }


        setGravity(Gravity.CENTER);

        setHeight((int)(24*context.getResources().getDisplayMetrics().density));

    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        ;
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1, AOdiaDefaultView.blackPaint);
    }
}
