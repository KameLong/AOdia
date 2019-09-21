package com.kamelong.aodia.StationTimeTable;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.flexbox.FlexboxLayout;
import com.kamelong.aodia.AOdia;
import com.kamelong.aodia.AOdiaData.LineFile;
import com.kamelong.aodia.AOdiaData.Station;
import com.kamelong.aodia.AOdiaData.StationTime;
import com.kamelong.aodia.AOdiaData.Train;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class StationTimeTableFragment extends AOdiaFragmentCustom {
    static final int[] backColor = new int[]{Color.rgb(134, 179, 224), Color.rgb(241, 157, 181), Color.rgb(224, 224, 137), Color.rgb(181, 224, 137), Color.rgb(224, 137, 224)};
    LineFile lineFile;
    int diaIndex = 0;
    Station station;
    int direction;
    int trainIndex = 0;
    private int[] flexBoxIds;
    private boolean[] usedTrainType;
    /**
     * 行き先の短縮名称
     */
    private HashMap<String,String> subName;
    /**
     * 行き先の短縮名称の使用回数
     * 最多使用のものは無印となる
     */
    private HashMap<String,Integer> subNameCount;
    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            Bundle bundle = getArguments();
            lineFile = getAOdia().getLineFile(bundle.getInt(AOdia.FILE_INDEX, 0));
            diaIndex = bundle.getInt(AOdia.DIA_INDEX, 0);
            direction = bundle.getInt(AOdia.DIRECTION, 0);
            station = lineFile.getStation(bundle.getInt(AOdia.STATION_INDEX, 0));
        } catch (Exception e) {
            SDlog.log(e);
        }
        contentView = inflater.inflate(R.layout.station_timetable, container, false);
        if (lineFile == null) {
            getAOdia().killFragment(this);
            return contentView;
        }
        subName = new HashMap<>();
        subNameCount = new HashMap<>();
        usedTrainType = new boolean[lineFile.trainType.size()];
        for (int i = 0; i < lineFile.trainType.size(); i++) {
            usedTrainType[i] = false;
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (diaIndex < backColor.length) {
                findViewById(R.id.mainLinear).setBackgroundColor(backColor[diaIndex]);
            }
            String directS = "";
            if (direction == 0) {
                directS = "下り";
            } else {
                directS = "上り";
            }
            String title = "";
            title = title + station.name + "駅　時刻表(" + lineFile.diagram.get(diaIndex).name + ")\n" + directS + "　" + lineFile.station.get((1 - direction) * (lineFile.getStationNum() - 1)).name + "方面";
            TextView titleView = (TextView) findViewById(R.id.titleView);
            titleView.setText(title);
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StationInfoDialog dialog = new StationInfoDialog(getActivity(), lineFile, diaIndex, direction, lineFile.station.indexOf(station));
                    dialog.show();

                }
            });
            flexBoxIds = new int[]{R.id.hour3, R.id.hour4, R.id.hour5, R.id.hour6, R.id.hour7, R.id.hour8, R.id.hour9, R.id.hour10, R.id.hour11, R.id.hour12, R.id.hour13, R.id.hour14,
                    R.id.hour15, R.id.hour16, R.id.hour17, R.id.hour18, R.id.hour19, R.id.hour20, R.id.hour21, R.id.hour22, R.id.hour23, R.id.hour24, R.id.hour1, R.id.hour2};


            makeTimeTable();

        } catch (Exception e) {
            SDlog.log(e);
        }

    }

    private void makeTimeTable() {
        boolean existStartStation = false;
        boolean existEndStation = true;
        boolean existGoOther = false;

        ArrayList<Integer> trainList = makeTrainArray();
        addStationLegend();
        int stationIndex = lineFile.station.indexOf(station);
        for (int i = trainList.size() - 1; i >= 0; i--) {
            Train train = lineFile.getTrain(diaIndex, direction, trainList.get(i));
            int hour = train.getPredictionTime(stationIndex) / 3600;
            if (hour < 3 || hour > 26) {
                hour = hour + 21;
                hour = hour % 24;
                hour = hour + 3;
            }
            String min = "" + (train.getPredictionTime(stationIndex) / 60) % 60;
            int color = lineFile.trainType.get(train.type).textColor.getAndroidColor();
            if (train.getStopType(stationIndex) != StationTime.STOP_TYPE_STOP) {
                color = Color.rgb(150, 150, 150);
            }
            if (!train.timeExist(stationIndex)) {
                min = min + "?";
            }
            String destination = lineFile.station.get(train.getEndStation()).name;
            String addInfo = "";

            if (train.getStartStation() == stationIndex) {
                addInfo += "●";
                existStartStation = true;
            }
            if (train.getEndStation() == stationIndex) {
                addInfo += "▽";
                existEndStation = true;
            }
            if (train.getEndStation() + (1 - 2 * direction) >= 0 && train.getEndStation() + (1 - 2 * direction) < lineFile.getStationNum()) {
                if (train.getStopType(train.getEndStation() + (1 - 2 * direction)) == StationTime.STOP_TYPE_PASS || train.getStopType(train.getEndStation() + (1 - 2 * direction)) == StationTime.STOP_TYPE_NOVIA) {
                    addInfo += "||";
                    existGoOther = true;
                }
            }
            String endName=train.getOuterEndStationName();
            if(endName==null){
                endName=lineFile.getStation(train.getEndStation()).name;
            }
            StationTimeTableTrainView trainView = new StationTimeTableTrainView(getActivity(), "" + min, subName.get(endName), addInfo, color, lineFile.trainType.get(train.type).bold, lineFile, diaIndex, direction, trainList.get(i));
            ((FlexboxLayout) findViewById(flexBoxIds[hour - 3])).addView(trainView);

        }
        for (int i = 0; i < 24; i++) {
            if (((FlexboxLayout) findViewById(flexBoxIds[i])).getChildCount() == 0) {
                ((FlexboxLayout) findViewById(flexBoxIds[i])).addView(new StationTimeTableTrainView(getActivity(), "", "", "", Color.WHITE, false, null, 0, 0, 0));
            }
        }

        String str = "凡例：";
        if (existStartStation) {
            str += "●は当駅始発、";
        }
        if (existEndStation) {
            str += "▽は当駅終着、";
        }
        if (existGoOther) {
            str += "||は他線直通";
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(str + textView.getText());


        TextView textView2 = new TextView(getActivity());
        textView2.setText("種別：");
        textView2.setTextColor(Color.BLACK);
        ((FlexboxLayout) findViewById(R.id.example)).addView(textView2);


        for (int i = 0; i < usedTrainType.length; i++) {

            if (usedTrainType[i]) {
                StationTimeTableTrainView typeView = new StationTimeTableTrainView(getActivity(), lineFile.trainType.get(i).name, "  ", "", lineFile.trainType.get(i).textColor.getAndroidColor(), lineFile.trainType.get(i).bold, lineFile, diaIndex, direction, trainList.get(i));
                ((FlexboxLayout) findViewById(R.id.example)).addView(typeView);
            }
        }
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("STTpass", false)) {
            TextView typeView = new TextView(getActivity());
            typeView.setText("通過列車　");
            typeView.setTextColor(Color.rgb(150, 150, 150));
//            StationTimeTableTrainView exampleView=new StationTimeTableTrainView(getActivity(),"00:通過列車","","",Color.rgb(150,150,150),fileNum,diaNumber,direct,-1);
            ((FlexboxLayout) findViewById(R.id.example)).addView(typeView);
        }


    }


    private void addStationSubName(Train train){
        String endName=train.getOuterEndStationName();
        if(endName==null){
            endName=lineFile.getStation(train.getEndStation()).name;
        }
        if(subNameCount.containsKey(endName)){
            subNameCount.put(endName,subNameCount.get(endName)+1);
        }else{
            subNameCount.put(endName,1);
        }
    }

    private ArrayList<Integer> makeTrainArray() {
        int stationIndex = lineFile.station.indexOf(station);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ArrayList<Integer> trainList = new ArrayList<Integer>();
        for (int i = 0; i < lineFile.getTrainNum(diaIndex, direction); i++) {
            Train train=lineFile.getTrain(diaIndex, direction, i);
            if (train.getPredictionTime(stationIndex) > 0) {
                if (train.getStopType(stationIndex) != StationTime.STOP_TYPE_STOP && !spf.getBoolean("STTpass", true)) {
                    continue;
                }
                if (train.getEndStation() == stationIndex && !spf.getBoolean("endTrain", true)) {
                    continue;
                }

                addStationSubName(train);
                if (train.getStopType(stationIndex) == StationTime.STOP_TYPE_STOP) {
                    usedTrainType[train.type] = true;
                }
                int j = 0;
                for (j = trainList.size(); j > 0; j--) {
                    if (train.getPredictionTime(stationIndex) < lineFile.getTrain(diaIndex, direction, trainList.get(j - 1)).getPredictionTime(stationIndex)) {
                        break;
                    }
                }
                trainList.add(j, i);
            } else {
                SDlog.log("makeTrainArray");
            }
        }
        return trainList;
    }

    /**
     * 凡例部分に駅名一覧を追加します。
     */
    private void addStationLegend() {
        int stationIndex = lineFile.station.indexOf(station);
        String result = "\n行先：";


        List<Map.Entry<String,Integer>> entries =
                new ArrayList<Map.Entry<String,Integer>>(subNameCount.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {

            @Override
            public int compare(
                    Map.Entry<String,Integer> entry1, Map.Entry<String,Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });


        String directS = "";
        if (direction == 0) {
            directS = "下り";
        } else {
            directS = "上り";
        }

        String title = "";
        if (entries.size() > 0) {
            title = title + lineFile.station.get(stationIndex).name + "駅　時刻表(" + lineFile.diagram.get(diaIndex).name + ")\n" + directS + "　" + entries.get(0).getKey()+ "方面";
        } else {
            title = title + lineFile.station.get(stationIndex).name + "駅　時刻表(" + lineFile.diagram.get(diaIndex).name + ")\n" + directS + "　列車なし";
        }
        TextView titleView = (TextView) findViewById(R.id.titleView);
        titleView.setText(title);


        if (entries.size() > 0) {

            result = result + "無印:" + entries.get(0).getKey();
            subName.put(entries.get(0).getKey(),"");
            entries.remove(0);
            for (Map.Entry<String,Integer> entry:entries) {
                String stationName=entry.getKey();
                String name="";
                boolean frag=false;
                for(int i=0;i<stationName.length();i++){
                    name=entry.getKey().substring(i, i+1);
                    if(!subName.containsValue(name)){
                        subName.put(stationName,name);
                        frag=true;
                        break;
                    }
                }
                if(!frag) {
                    subName.put(stationName,stationName);
                }

            }
            for (Map.Entry<String,String> entry:subName.entrySet()) {
                if (!entry.getValue().equals("")) {
                    result = result + "　" + entry.getValue() + ":" +entry.getKey();
                }
            }
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(result);
    }

    public String fragmentName() {
        try {
            String directS = "";
            if (direction == 0) {
                directS = "下り";
            } else {
                directS = "上り";
            }
            return "駅時刻表　" + station.name + "　" + directS + lineFile.name;
        } catch (Exception e) {
            return "駅時刻表";
        }
    }

    public View findViewById(int id) {
        return contentView.findViewById(id);
    }

    @NonNull
    @Override
    public String getName() {
        return null;
    }

    @Override
    public LineFile getLineFile() {
        return null;
    }
}

