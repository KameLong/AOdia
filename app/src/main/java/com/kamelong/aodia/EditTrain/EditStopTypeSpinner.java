package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.kamelong.OuDia.Train;
import com.kamelong.aodia.R;
import com.kamelong.aodia.TimeTable.TimeTableDefaultView;

import java.util.Arrays;
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

        List<String>stopList= Arrays.asList(getResources().getStringArray(R.array.StopTypeSpinner));
        setBackground(null);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stopList);
        dataAdapter.setDropDownViewResource( android.R.layout.simple_spinner_item);
        this.setAdapter(dataAdapter);
        this.setSelection(train.getStopType(stationIndex));
        this.setMinimumHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(train.getStopType(stationIndex)!=i){
                    train.setStopType(stationIndex,i);
                    if(listener!=null){
                        listener.trainChanged(train);
                    }

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
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1, TimeTableDefaultView.blackPaint);
        canvas.drawText(this.getSelectedItem().toString().split("：")[0],5*density,getHeight()-7*density,paint);
    }
    public void setOnTrainChangeListener(OnTrainChangeListener listener){
        this.listener=listener;
    }
}