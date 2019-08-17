package com.kamelong.aodia.StationTimeTable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;


/**
 * Created by kame on 2017/02/02.
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
public class StationInfoIndexFragment extends AOdiaFragment {
    int fileNum = 0;
    View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {

            Bundle bundle = getArguments();
            fileNum=bundle.getInt("fileNum");
        }catch(Exception e){
            SDlog.log(e);
        }
        contentView=inflater.inflate(R.layout.station_timetable_index_fragment, container, false);
        diaFile=getAOdiaActivity().diaFiles.get(fileNum);
        if(diaFile==null){
            onDestroy();
            return contentView;
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            LinearLayout stationListLayout = (LinearLayout)findViewById(R.id.stationList);
            for (int station = 0; station < diaFile.getStationNum(); station++) {
                StationTimetableIndexStation stationView = new StationTimetableIndexStation(getAOdiaActivity(),diaFile,fileNum,station);
                stationListLayout.addView(stationView);


            }
        }catch(Exception e){
            SDlog.log(e);
        }
    }
    @Override
    public String fragmentName(){
        return "駅時刻表一覧　"+diaFile.name;
    }
    public View findViewById(int id){
        return contentView.findViewById(id);
    }
}