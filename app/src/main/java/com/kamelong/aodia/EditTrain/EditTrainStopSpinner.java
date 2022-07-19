package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.TimeTable.TimeTableDefaultView;

import java.util.ArrayList;
import java.util.List;

/**
 * 到着番線を選ぶSpinner
 */
public class EditTrainStopSpinner extends AppCompatSpinner {

    public int stationIndex;
    public Station station;
    public Train train;
    public OnTrainChangeListener listener;
    public static final int HEIGHT=30;
    public EditTrainStopSpinner(Context context, final int stationIndex, LineFile lineFile, final Train train) {
        super(context);
        this.stationIndex=stationIndex;
        this.train=train;
        this.station=lineFile.getStation(stationIndex);
        List<String>stopList=new ArrayList<>();
        for(int i=0;i<station.getTrackNum();i++){
            stopList.add(station.getTrackName(i));
        }
        setBackground(null);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stopList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.setAdapter(dataAdapter);
        setSelection(train.getStopTrack(stationIndex));
        this.setMinimumHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (train.getStopTrack(stationIndex) != i) {
                        train.setStopTrack(stationIndex, i);
                        if (listener != null) {
                            listener.trainChanged(train);
                        }

                    }
                }catch(Exception e){
                    e.printStackTrace();
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
        String text=station.getTrackShortName(this.getSelectedItemPosition());
        if(train.getStopType(stationIndex)==0||train.getStopType(stationIndex)==3){
            text="";
        }
        canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,17*density,paint);
    }
    public void setOnTrainChangeListener(OnTrainChangeListener listener){
        this.listener=listener;
    }
}