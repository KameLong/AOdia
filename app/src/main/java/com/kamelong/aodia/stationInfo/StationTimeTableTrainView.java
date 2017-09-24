package com.kamelong.aodia.stationInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

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
public class StationTimeTableTrainView extends LinearLayout implements View.OnClickListener {
    int fileNum=0;
    int diaNum=0;
    int direct=0;
    int trainNum=0;
    public StationTimeTableTrainView(Context context) {
        super(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.station_timetable_train, this);
        setOnClickListener(this);

    }
    public StationTimeTableTrainView(Context context,String time,String endStation,String add,int color,int fileN,int diaN,int d,int trainN){
        this(context);
        try {
            fileNum = fileN;
            diaNum = diaN;
            direct = d;
            trainNum = trainN;
            TextView timeView = (TextView) findViewById(R.id.STTTViewTime);
            timeView.setText(time);
            TextView gotoView = (TextView) findViewById(R.id.STTTViewGoto);
            gotoView.setText(endStation);
            TextView addView = (TextView) findViewById(R.id.STTTViewAdd);
            addView.setText(add);

            timeView.setTextColor(color);
            gotoView.setTextColor(color);
            addView.setTextColor(color);
        }catch(Exception e){
            SdLog.log(e);

        }
    }
    public void onClick(View view) {
        Log.d("StationTimeTableView","onClick");
        if(fileNum<0){
            return;
        }
        ((AOdiaActivity)getContext()).openLineTimeTable(fileNum,diaNum,direct,trainNum);
    }
}
