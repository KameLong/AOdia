package com.kamelong.aodia.StationTimeTable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.AOdiaFragmentCustom;
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
public class StationInfoIndexFragment extends AOdiaFragmentCustom {
    public static final String FRAGMENT_NAME="StationInfoIndexFragment";

    LineFile lineFile;
    View contentView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {

            Bundle bundle = getArguments();
            int filmIndex = bundle.getInt(AOdia.FILE_INDEX);
            lineFile = getAOdia().getLineFile(filmIndex);
        } catch (Exception e) {
            SDlog.log(e);
        }
        contentView = inflater.inflate(R.layout.station_timetable_index_fragment, container, false);
        if (lineFile == null) {
            getAOdia().killFragment(this);
            return contentView;
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (lineFile == null) {
            getAOdia().killFragment(this);
            return;
        }
        try {
            LinearLayout stationListLayout = contentView.findViewById(R.id.stationList);
            for (int station = 0; station < lineFile.getStationNum(); station++) {
                StationTimetableIndexStation stationView = new StationTimetableIndexStation(getMainActivity(), lineFile, station);
                stationListLayout.addView(stationView);
            }
        } catch (Exception e) {
            SDlog.log(e);
        }
    }

    @NonNull
    @Override
    public String getName() {
        try {
            return "駅時刻表" + "\n" + lineFile.name;
        } catch (Exception e) {
            return "駅時刻表";
        }
    }

    @Override
    public String getHash() {
        return FRAGMENT_NAME;
    }

    @Override
    public LineFile getLineFile() {
        return lineFile;
    }
}