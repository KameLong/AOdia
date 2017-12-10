package com.kamelong.aodia.stationInfo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.timeTable.TimeTableFragmentOld;

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
    private TimeTableFragmentOld fragment;
    private int fileNum;
    private int station;
    private int direct;
    private int diaNum;
    private AOdiaActivity activity;

    public StationInfoDialog(Context context, TimeTableFragmentOld f, AOdiaDiaFile dia, int fileN, int diaN, int d, int s){
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
    }

}


