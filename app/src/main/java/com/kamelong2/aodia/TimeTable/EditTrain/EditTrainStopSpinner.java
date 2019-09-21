package com.kamelong2.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.OuDia.Station;
import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaDefaultView;

import java.util.ArrayList;
import java.util.List;

public class EditTrainStopSpinner extends AppCompatSpinner {

    public int stationIndex;
    public Station station;
    public Train train;
    public OnTrainChangeListener listener;
    public static final int HEIGHT=30;
    public EditTrainStopSpinner(Context context, final int stationIndex, DiaFile diaFile, final Train train) {
        super(context);
        this.stationIndex=stationIndex;
        this.train=train;
        this.station=diaFile.station.get(stationIndex);
        List<String>stopList=new ArrayList<>();
        stopList.add(0,"デフォルト");
        for(int i=1;i<station.trackName.size();i++){
            stopList.add(station.trackName.get(i));
        }
        setBackground(null);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, stopList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(dataAdapter);
        setSelection(train.getStop(stationIndex));
        this.setMinimumHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                train.setStop(stationIndex,i);
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
        String text=station.trackshortName.get(this.getSelectedItemPosition());
        if(this.getSelectedItemPosition()==0){
            try {
                text = station.trackshortName.get(station.stopMain[train.direction]);
            }catch (Exception e){
                text = station.trackshortName.get(0);

            }
        }
        if(train.getStopType(stationIndex)==0||train.getStopType(stationIndex)==3){
            text="";
        }
        canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,17*density,paint);
    }
    public void setOnTrainChangeListener(OnTrainChangeListener listener){
        this.listener=listener;
    }
}