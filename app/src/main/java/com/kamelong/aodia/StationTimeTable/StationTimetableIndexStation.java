package com.kamelong.aodia.StationTimeTable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.SimpleOudia;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.io.File;
import java.util.ArrayList;


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
    public StationTimetableIndexStation(final Context context, DiaFile diaFile, int fileNum, int stationNum){
        super(context);
        try{
            LayoutInflater.from(context).inflate(R.layout.station_timetable_index_onestation, this);
            ((TextView)findViewById(R.id.stationName)).setText(diaFile.station.get(stationNum).name);
            LinearLayout diaList=findViewById(R.id.stationTimetableList);
            LinearLayout connectList=findViewById(R.id.connectLineList);
            for(int i=0;i<diaFile.getDiaNum();i++){
                StationTimetableIndexDia stationTimetableIndexDia=new StationTimetableIndexDia(context,diaFile,fileNum,stationNum,i);
                diaList.addView(stationTimetableIndexDia);
            }
            final String directory=diaFile.filePath.substring(0,diaFile.filePath.lastIndexOf('/'));
            final ArrayList<String> pathList = ((AOdiaActivity)context).database.searchFileFromStation(diaFile.station.get(stationNum).name, directory,false);

            for(int i=0;i<pathList.size();i++){
                Button button=new Button(context);
                if(!diaFile.filePath.equals(directory+"/"+pathList.get(i))) {
                    SimpleOudia file = new SimpleOudia(new File(directory+"/"+pathList.get(i)));
                    button.setText(file.name);
                    button.setMaxLines(3);
                    button.setId(i);
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((AOdiaActivity)context).openFile(new File(directory+"/"+pathList.get(view.getId())));                        }
                    });
                    connectList.addView(button);
                }
            }


        }catch (Exception e){
            SDlog.log(e);
        }
    }

}
