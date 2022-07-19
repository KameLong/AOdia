package com.kamelong.aodia.StationTimeTable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Train;
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
public class StationInfoDialog extends Dialog {
    LineFile lineFile;
    int diaIndex;
    int stationIndex;
    int direction;
    AOdia aodia;

    public StationInfoDialog(Context context, LineFile lineFile, int diaIndex, int direction, int stationIndex) {
        super(context);
        aodia = ((MainActivity) context).getAOdia();
        this.lineFile = lineFile;
        this.stationIndex = stationIndex;
        this.direction = direction;
        this.diaIndex = diaIndex;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.station_info_dialog);
        init();
    }

    private void init() {
        try {
            TextView stationNameView = findViewById(R.id.stationNameView);
            stationNameView.setText(lineFile.station.get(stationIndex).name + "駅");
            Button beforeStationButton = findViewById(R.id.beforeStationButton);
            if (stationIndex - (1 - 2 * direction) >= 0 && stationIndex - (1 - 2 * direction) < lineFile.getStationNum()) {
                beforeStationButton.setText("⇦" + lineFile.station.get(stationIndex - (1 - 2 * direction)).name + "駅");
                beforeStationButton.setVisibility(View.VISIBLE);
            } else {
                beforeStationButton.setVisibility(View.INVISIBLE);
            }
            beforeStationButton.setOnClickListener(view -> {
                stationIndex = stationIndex - (1 - 2 * direction);
                StationInfoDialog.this.init();
            });
            Button afterStationButton = findViewById(R.id.afterStationButton);
            if (stationIndex + (1 - 2 * direction) >= 0 && stationIndex + (1 - 2 * direction) < lineFile.getStationNum()) {
                afterStationButton.setText(lineFile.station.get(stationIndex + (1 - 2 * direction)).name + "駅⇨");
                afterStationButton.setVisibility(View.VISIBLE);
            } else {
                afterStationButton.setVisibility(View.INVISIBLE);
            }
            afterStationButton.setOnClickListener(view -> {
                stationIndex = stationIndex + (1 - 2 * direction);
                StationInfoDialog.this.init();
            });
            Button downTimetable = findViewById(R.id.downTimeTableButton);
            downTimetable.setOnClickListener(view -> {
                aodia.openStationTimeTable(lineFile, diaIndex, Train.DOWN, stationIndex);
                StationInfoDialog.this.dismiss();
            });
            Button upTimetable = findViewById(R.id.upTimeTableButton);
            upTimetable.setOnClickListener(view -> {
                aodia.openStationTimeTable(lineFile, diaIndex, Train.UP, stationIndex);
                StationInfoDialog.this.dismiss();
            });
        } catch (Exception e) {
            SDlog.log(e);
        }

    }

    public void setOnSortListener(final OnSortButtonClickListener listener) {
        Button sortButton = findViewById(R.id.sortButton);
        sortButton.setVisibility(View.VISIBLE);

        sortButton.setOnClickListener(view -> {
            listener.onSortCicked(stationIndex);
            StationInfoDialog.this.dismiss();
        });

    }

}


