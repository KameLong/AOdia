package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.Gravity;

import com.kamelong.aodia.TimeTable.TimeTableDefaultView;


public class StationNameTextView extends AppCompatTextView {
    public int stationNumber;
    public static final int HEIGHT=30;
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

        setHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));

    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        ;
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1, TimeTableDefaultView.blackPaint);
    }
}
