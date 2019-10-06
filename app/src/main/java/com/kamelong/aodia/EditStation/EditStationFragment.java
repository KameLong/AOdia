package com.kamelong.aodia.EditStation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Station;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 駅編集を行うFragmentです
 */
public class EditStationFragment extends AOdiaFragmentCustom {
    public ArrayList<EditStationView> editStationViews = new ArrayList<>();
    int fileIndex = 0;
    boolean frag = true;
    private View fragmentContainer;
    private LineFile lineFile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            Bundle bundle = getArguments();
            fileIndex = bundle.getInt(AOdia.FILE_INDEX, 0);
        } catch (Exception e) {
            SDlog.log(e);
        }
        try {
            fragmentContainer = inflater.inflate(R.layout.edit_station_fragment, container, false);
            try {
                lineFile = getAOdia().getLineFile(fileIndex);
                if (lineFile == null) {
                    Toast.makeText(getContext(), "ダイヤファイルが見つかりませんでした。", Toast.LENGTH_LONG).show();
                    getAOdia().killFragment(this);
                    return fragmentContainer;
                }
            } catch (Exception e) {
                SDlog.log(e);
            }

            return fragmentContainer;
        } catch (Exception e) {
            SDlog.log(e);
            SDlog.toast("駅編集の際にエラーが発生しました");
            getFragmentManager().beginTransaction().remove(this).commit();
            return fragmentContainer;
        }
    }


    @Override public void onStart(){
        super.onStart();
        if (lineFile == null) {
            return;
        }
        initStation();

    }
    /**
     * 駅リスト初期化
     */
    public void initStation(){
        final LinearLayout stationList = fragmentContainer.findViewById(R.id.stationList);
        stationList.removeAllViews();
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            EditStationView editStationView = new EditStationView(getMainActivity(), lineFile.getStation(i), lineFile);
            editStationViews.add(editStationView);
            stationList.addView(editStationView);
        }

        final FloatingActionButton copy=fragmentContainer.findViewById(R.id.copyButton);
        final FloatingActionButton paste=fragmentContainer.findViewById(R.id.pasteButton);
        final FloatingActionButton delete=fragmentContainer.findViewById(R.id.deleteButton);
        final FloatingActionButton add=fragmentContainer.findViewById(R.id.addButton);
        final FloatingActionButton edit=fragmentContainer.findViewById(R.id.actionButton);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditStationActionDialog dialog=new EditStationActionDialog(getContext(),lineFile);
                dialog.show();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Station>copyStation=new ArrayList<>();
                for(int i=0;i<lineFile.getStationNum();i++){
                    if(((EditStationView)stationList.getChildAt(i)).checked){
                        copyStation.add(lineFile.getStation(i));
                    }
                }
                getAOdia().copyStation=copyStation;
                SDlog.toast("駅をコピーしました");
                initStation();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Station>copyStation=getAOdia().copyStation;
                int i=0;
                for(i=0;i<lineFile.getStationNum();i++){
                    if(((EditStationView)stationList.getChildAt(i)).checked){
                        break;
                    }
                }
                if(copyStation.size()==0){
                    SDlog.toast("駅がコピーされていません");
                    return;
                }
                for(Station s:copyStation){
                    Station newStation=s.clone(lineFile);
                    lineFile.addStation(i,newStation,false);
                    i++;
                }
                SDlog.toast("駅を貼り付けました");
                initStation();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                for(i=0;i<lineFile.getStationNum();i++){
                    if(((EditStationView)stationList.getChildAt(i)).checked){
                        break;
                    }
                }
                Station station=new Station(lineFile);
                lineFile.addStation(i,station,false);
                initStation();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Station>deleteStation=new ArrayList<>();
                for(int i=0;i<lineFile.getStationNum();i++){
                    if(((EditStationView)stationList.getChildAt(i)).checked){
                        deleteStation.add(lineFile.getStation(i));
                    }
                }
                for(Station s:deleteStation){
                    if(lineFile.deleteStation(lineFile.station.indexOf(s))<0){
                           SDlog.toast(s.name+"は分岐元設定されているため削除できません");
                           return;
                    }
                }
                SDlog.toast("駅を削除しました");
                initStation();

            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @NonNull
    @Override
    public String getName() {
        try {
            String line = lineFile.name;
            if (line.length() > 10) {
                line = line.substring(0, 10);
            }
            return line + "\n" + "駅編集";
        } catch (Exception e) {
            return "駅編集";
        }
    }

    @Override
    public LineFile getLineFile() {
        return lineFile;
    }
}
