package com.kamelong.aodia.StationTimeTable;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;


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
public class StationTimetableIndexStation extends LinearLayout {
    public StationTimetableIndexStation(final MainActivity activity, LineFile lineFile, int stationIndex) {
        super(activity);
        if(lineFile==null){
            return;
        }
        try {
            LayoutInflater.from(activity).inflate(R.layout.station_timetable_index_onestation, this);
            ((TextView) findViewById(R.id.stationName)).setText(lineFile.station.get(stationIndex).name);
            LinearLayout diaList = findViewById(R.id.stationTimetableList);
            LinearLayout connectList = findViewById(R.id.connectLineList);
            for (int i = 0; i < lineFile.getDiagramNum(); i++) {
                StationTimetableIndexDia stationTimetableIndexDia = new StationTimetableIndexDia(activity, lineFile, stationIndex, i);
                diaList.addView(stationTimetableIndexDia);
            }

        } catch (Exception e) {
            SDlog.log(e);
        }
    }

}
