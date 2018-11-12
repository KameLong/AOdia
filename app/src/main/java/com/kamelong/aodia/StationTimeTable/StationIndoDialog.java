package com.kamelong.aodia.StationTimeTable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SDlog;
import com.kamelong.aodia.TimeTable.TimeTableFragment;

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
public class StationIndoDialog extends Dialog{
    DiaFile diaFile;
    TimeTableFragment fragment;
    int fileNum;
    int station;
    int direct;
    int diaNum;
    AOdiaActivity activity;

    public StationIndoDialog(Context context, TimeTableFragment f,DiaFile dia,int fileN,int diaN,int d, int s){
        super(context);
        diaFile=dia;
        fragment=f;
        station=s;
        direct=d;
        diaNum=diaN;
        fileNum=fileN;
        activity=(AOdiaActivity)context;
    }
    public StationIndoDialog(Context context,DiaFile dia,int fileN,int diaN,int d, int s){
        super(context);
        diaFile=dia;
        fragment=null;
        station=s;
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
    private void init(){
        try {
            TextView stationNameView = (TextView) findViewById(R.id.stationNameView);
            stationNameView.setText(diaFile.station.get(station).name + "駅");
            Button beforeStationButton = (Button) findViewById(R.id.beforeStationButton);
            if (station - (1 - 2 * direct) >= 0 && station - (1 - 2 * direct) < diaFile.getStationNum()) {
                beforeStationButton.setText("⇦" + diaFile.station.get(station - (1 - 2 * direct)).name + "駅");
                beforeStationButton.setVisibility(View.VISIBLE);
            } else {
                beforeStationButton.setVisibility(View.INVISIBLE);
            }
            beforeStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station - (1 - 2 * direct);
                    StationIndoDialog.this.init();
                }
            });
            Button afterStationButton = (Button) findViewById(R.id.afterStationButton);
            if (station + (1 - 2 * direct) >= 0 && station + (1 - 2 * direct) < diaFile.getStationNum()) {
                afterStationButton.setText(diaFile.station.get(station + (1 - 2 * direct)).name + "駅⇨");
                afterStationButton.setVisibility(View.VISIBLE);
            } else {
                afterStationButton.setVisibility(View.INVISIBLE);
            }
            afterStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station + (1 - 2 * direct);
                    StationIndoDialog.this.init();
                }
            });
            Button sortButton = (Button) findViewById(R.id.sortButton);
            if(fragment!=null) {
                sortButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.sortTrain(station);
                        StationIndoDialog.this.dismiss();
                    }
                });
            }else{
                sortButton.setVisibility(View.GONE);
            }
            Button downTimetable = (Button) findViewById(R.id.downTimeTableButton);
            downTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum, diaNum, 0, station);
                    StationIndoDialog.this.dismiss();
                }
            });
            Button upTimetable = (Button) findViewById(R.id.upTimeTableButton);
            upTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum, diaNum, 1, station);
                    StationIndoDialog.this.dismiss();
                }
            });
            LinearLayout showStop=findViewById(R.id.showStop);
            showStop.setVisibility(View.GONE);
        }catch(Exception e){
            SDlog.log(e);
        }

    }

}


