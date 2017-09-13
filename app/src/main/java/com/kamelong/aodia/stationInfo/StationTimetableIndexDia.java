package com.kamelong.aodia.stationInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;

/**
 * Created by kame on 2017/02/03.
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
public class StationTimetableIndexDia extends LinearLayout {
    MainActivity activity;
    public StationTimetableIndexDia(Context context, AOdiaDiaFile diaFile, final int fileNum, final int stationNum, final int diaNum){
        super(context);
        activity=(MainActivity)context;
        try{
            View layout = LayoutInflater.from(context).inflate(R.layout.station_timetable_index_onedia, this);
            ((TextView)findViewById(R.id.diaName)).setText(diaFile.getDiaName(diaNum));
            ((Button)findViewById(R.id.downButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("indexDia",diaNum+","+stationNum);
                    activity.openStationTimeTable(fileNum,diaNum,0,stationNum);
                }
            });
            ((Button)findViewById(R.id.upButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openStationTimeTable(fileNum,diaNum,1,stationNum);
                }
            });

        }catch (Exception e){
            SdLog.log(e);
        }

    }

}
