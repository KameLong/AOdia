package com.kamelong2.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaDefaultView;

import java.util.ArrayList;
import java.util.List;


public class EditStopTypeSpinner extends AppCompatSpinner {

    public int stationIndex;
    public Train train;
    public OnTrainChangeListener listener;
    protected static final int HEIGHT=30;
    public EditStopTypeSpinner(Context context, final int stationIndex,final Train train) {
        super(context);
        this.stationIndex=stationIndex;
        this.train=train;


        List<String>stopList=new ArrayList<>();
        stopList.add("    ：運行無し");
        stopList.add("○：停車");
        stopList.add("レ：通過");
        stopList.add(" || ：経由無し");
        setBackground(null);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stopList);
        dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_item);
        this.setAdapter(dataAdapter);
        this.setSelection(train.getStopType(stationIndex));
        this.setMinimumHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));
        setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                train.setStopType(stationIndex,i);
                if(listener!=null){
                    listener.trainChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    public void onDraw(Canvas canvas){
        float density=getContext().getResources().getDisplayMetrics().density;
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(15*density);
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1, AOdiaDefaultView.blackPaint);
        canvas.drawText(this.getSelectedItem().toString().split("：")[0],5*density,getHeight()-7*density,paint);
    }

    public void setOnTrainChangeListener(OnTrainChangeListener listener){
        this.listener=listener;
    }

}