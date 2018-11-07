package com.kamelong.aodia.stationInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.timeTable.TimeTableFragment;

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
    private AOdiaDiaFile diaFile;
    private TimeTableFragment fragment;
    private int fileNum;
    private int station;
    private int direct;
    private int diaNum;
    private AOdiaActivity activity;

    public StationInfoDialog(Context context, TimeTableFragment f, AOdiaDiaFile dia, int fileN, int diaN, int d, int s){
        super(context);
        diaFile=dia;
        fragment=f;
        station=s;
        direct=d;
        diaNum=diaN;
        fileNum=fileN;
        activity=(AOdiaActivity)context;
    }
    public StationInfoDialog(Context context, AOdiaDiaFile dia, int fileN, int diaN, int d, int s){
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
            stationNameView.setText(diaFile.getStation().getName(station) + "駅");
            Button beforeStationButton = (Button) findViewById(R.id.beforeStationButton);
            if (station - (1 - 2 * direct) >= 0 && station - (1 - 2 * direct) < diaFile.getStation().getStationNum()) {
                beforeStationButton.setText("⇦" + diaFile.getStation().getName(station - (1 - 2 * direct)) + "駅");
                beforeStationButton.setVisibility(View.VISIBLE);
            } else {
                beforeStationButton.setVisibility(View.INVISIBLE);
            }
            beforeStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station - (1 - 2 * direct);
                    StationInfoDialog.this.init();
                }
            });
            Button afterStationButton = (Button) findViewById(R.id.afterStationButton);
            if (station + (1 - 2 * direct) >= 0 && station + (1 - 2 * direct) < diaFile.getStation().getStationNum()) {
                afterStationButton.setText(diaFile.getStation().getName(station + (1 - 2 * direct)) + "駅⇨");
                afterStationButton.setVisibility(View.VISIBLE);
            } else {
                afterStationButton.setVisibility(View.INVISIBLE);
            }
            afterStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station + (1 - 2 * direct);
                    StationInfoDialog.this.init();
                }
            });
            Button sortButton = (Button) findViewById(R.id.sortButton);
            if(fragment!=null) {
                sortButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.sortTrain(station);
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
                    activity.openStationTimeTable(fileNum, diaNum, 0, station);
                    StationInfoDialog.this.dismiss();
                }
            });
            Button upTimetable = (Button) findViewById(R.id.upTimeTableButton);
            upTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum, diaNum, 1, station);
                    StationInfoDialog.this.dismiss();
                }
            });
        }catch(Exception e){
            SdLog.log(e);
        }

    }

}


