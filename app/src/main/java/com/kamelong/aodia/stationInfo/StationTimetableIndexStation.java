package com.kamelong.aodia.stationInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class StationTimetableIndexStation extends LinearLayout {
    public StationTimetableIndexStation(Context context, AOdiaDiaFile diaFile, int fileNum, int stationNum){
        super(context);
        try{
            LayoutInflater.from(context).inflate(R.layout.station_timetable_index_onestation, this);
            ((TextView)findViewById(R.id.stationName)).setText(diaFile.getStation(stationNum).getName());
            LinearLayout diaList=(LinearLayout)findViewById(R.id.diaList);
            for(int i=0;i<diaFile.getDiaNum();i++){
                StationTimetableIndexDia stationTimetableIndexDia=new StationTimetableIndexDia(context,diaFile,fileNum,stationNum,i);
                diaList.addView(stationTimetableIndexDia);
            }
        }catch (Exception e){
            SdLog.log(e);
        }
    }

}
