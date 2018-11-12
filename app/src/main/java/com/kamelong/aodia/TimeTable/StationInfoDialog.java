package com.kamelong.aodia.TimeTable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Station;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SDlog;
import com.kamelong.aodia.TimeTable.EditTrain.OnTrainChangeListener;

/**
 * Created by kame on 2017/01/28.
 */
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
public class StationInfoDialog extends Dialog{
    public DiaFile diaFile;
    public TimeTableFragment fragment;
    public int fileNum;
    public int stationIndex;
    public int direct;
    public int diaNum;
    public AOdiaActivity activity;
    public OnTrainChangeListener trainChangeListener;
    public Station station;


    public StationInfoDialog(Context context, TimeTableFragment f, DiaFile dia, int fileN, int diaN, int d, int s){
        super(context);
        diaFile=dia;
        fragment=f;
        stationIndex =s;
        direct=d;
        diaNum=diaN;
        fileNum=fileN;
        activity=(AOdiaActivity)context;
    }
    public StationInfoDialog(Context context, DiaFile dia, int fileN, int diaN, int d, int s){
        super(context);
        diaFile=dia;
        fragment=null;
        stationIndex =s;
        direct=d;
        diaNum=diaN;
        fileNum=fileN;
        activity=(AOdiaActivity)context;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.station_info_dialog);
        init();
    }
    @Override
    public void dismiss(){
        super.dismiss();
        if(trainChangeListener!=null){
            trainChangeListener.trainReset();
        }
    }
    private void init(){
        try {
            station=diaFile.station.get(stationIndex);
            TextView stationNameView = (TextView) findViewById(R.id.stationNameView);
            String text1=diaFile.station.get(stationIndex).name;
            if(text1.length()>10){
                text1=text1.substring(0,10);
            }
            stationNameView.setText(text1);
            Button beforeStationButton = (Button) findViewById(R.id.beforeStationButton);
            String text3=diaFile.station.get(stationIndex).name;


            if (stationIndex - (1 - 2 * direct) >= 0 && stationIndex - (1 - 2 * direct) < diaFile.getStationNum()) {
                String text2=diaFile.station.get(stationIndex - (1 - 2 * direct)).name;
                if(text2.length()>6){
                    text2=text2.substring(0,6);
                }

                beforeStationButton.setText("⇦" + text2);
                beforeStationButton.setVisibility(View.VISIBLE);
            } else {
                beforeStationButton.setVisibility(View.INVISIBLE);
            }
            beforeStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stationIndex = stationIndex - (1 - 2 * direct);
                    StationInfoDialog.this.init();
                }
            });
            Button afterStationButton = (Button) findViewById(R.id.afterStationButton);
            if (stationIndex + (1 - 2 * direct) >= 0 && stationIndex + (1 - 2 * direct) < diaFile.getStationNum()) {
                String text2=diaFile.station.get(stationIndex + (1 - 2 * direct)).name;
                if(text2.length()>6){
                    text2=text2.substring(0,6);
                }

                afterStationButton.setText(text2 + "⇨");
                afterStationButton.setVisibility(View.VISIBLE);
            } else {
                afterStationButton.setVisibility(View.INVISIBLE);
            }
            afterStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stationIndex = stationIndex + (1 - 2 * direct);
                    StationInfoDialog.this.init();
                }
            });
            Button sortButton = (Button) findViewById(R.id.sortButton);
            if(fragment!=null) {
                sortButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.sortTrain(stationIndex);
                        StationInfoDialog.this.dismiss();
                    }
                });
            }else{
                sortButton.setVisibility(View.INVISIBLE);
            }
            Button downTimetable = (Button) findViewById(R.id.downTimeTableButton);
            downTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum, diaNum, 0, diaFile.station.indexOf(station));
                    StationInfoDialog.this.dismiss();
                }
            });
            Button upTimetable = (Button) findViewById(R.id.upTimeTableButton);
            upTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum, diaNum, 1,diaFile.station.indexOf(station));
                    StationInfoDialog.this.dismiss();
                }
            });

            ToggleButton arriveToggle=findViewById(R.id.arriveToggle);
            arriveToggle.setChecked((station.getTimeTableStyle(direct)&0b010)!=0);
            arriveToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowArival(direct,b);
                }
            });
            ToggleButton stopToggle=findViewById(R.id.stopToggle);
            stopToggle.setChecked((station.getTimeTableStyle(direct)&0b100)!=0);
            stopToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowStop(direct,b);
                }
            });
            ToggleButton departToggle=findViewById(R.id.departToggle);
            departToggle.setChecked((station.getTimeTableStyle(direct)&0b001)!=0);
            departToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowDepart(direct,b);
                }
            });

        }catch(Exception e){
            SDlog.log(e);
        }

    }
    public void setOnTrainChangeListener(OnTrainChangeListener listener){
        trainChangeListener=listener;
    }

}


