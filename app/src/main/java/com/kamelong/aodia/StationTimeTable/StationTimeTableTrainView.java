package com.kamelong.aodia.StationTimeTable;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;


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
    LineFile lineFile;
    int diaIndex = 0;
    int direction = 0;
    int trainIndex = 0;

    public StationTimeTableTrainView(Context context, String time, String endStation, String add, int color, boolean bold, LineFile lineFile, int diaIndex, int direction, int trainIndex) {
        super(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.station_timetable_train, this);
        setOnClickListener(this);
        try {
            this.lineFile = lineFile;
            this.diaIndex = diaIndex;
            this.direction = direction;
            this.trainIndex = trainIndex;
            TextView timeView = findViewById(R.id.STTTViewTime);
            if (time.length() == 1) {
                time = "0" + time;
            }
            timeView.setText(time);
            TextView gotoView = findViewById(R.id.STTTViewGoto);
            gotoView.setText(endStation);
            TextView addView = findViewById(R.id.STTTViewAdd);
            addView.setText(add);

            if (bold) {
                timeView.setTextColor(Color.WHITE);
                findViewById(R.id.background).setBackgroundColor(color);
            } else {
                timeView.setTextColor(color);
            }
            gotoView.setTextColor(color);
            addView.setTextColor(color);
        } catch (Exception e) {
            SDlog.log(e);

        }
    }

    public void onClick(View view) {
        ((MainActivity) getContext()).getAOdia().openTimeTable(lineFile, diaIndex, direction, trainIndex);
    }
}
