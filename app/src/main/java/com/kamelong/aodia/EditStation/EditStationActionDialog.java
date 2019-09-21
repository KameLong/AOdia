package com.kamelong.aodia.EditStation;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.kamelong.aodia.AOdiaData.LineFile;
import com.kamelong.aodia.AOdiaData.Station;
import com.kamelong.aodia.MainActivity;
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
 * 駅編集にて路線操作するダイアログです
 */
public class EditStationActionDialog extends Dialog {

    public EditStationActionDialog(@NonNull final Context context, final LineFile lineFile) {
        super(context);
        setContentView(R.layout.station_edit_action_dialog);
        final Spinner startSpiner=findViewById(R.id.startStation);
        final Spinner endSpiner=findViewById(R.id.endStation);
        ArrayList<String>stationList=new ArrayList<>();
        for(Station station:lineFile.station){
            stationList.add(station.name);
        }

        ArrayAdapter<String> startAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,stationList);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpiner.setAdapter(startAdapter);
        ArrayAdapter<String> endAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,stationList);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endSpiner.setAdapter(endAdapter);

        findViewById(R.id.makeSubFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //路線の切り出しを行います

                int startStation=startSpiner.getSelectedItemPosition();
                int endStation=endSpiner.getSelectedItemPosition();
                if(startStation>=endStation){
                    SDlog.toast("終着駅が始発駅より前方にあります。路線の切り出しができません");
                    return;
                }
                LineFile newLine=lineFile.clone();
                //同一路線を複製してから不要駅切り取り
                newLine.makeSubLine(startStation,endStation,((CheckBox)findViewById(R.id.userOuter)).isChecked());
                if(((CheckBox)findViewById(R.id.reverse1)).isChecked()){
                    newLine.reverse();
                }
                newLine.name="(切り出し)"+newLine.name;
                ((MainActivity)context).getAOdia().addLineFile(newLine);
                ((MainActivity)context).getAOdia().openTimeTable(newLine,0,0);
                dismiss();

            }
        });

        ArrayList<String>insertStation=new ArrayList<>();
        for(Station station:lineFile.station){
            insertStation.add(station.name);
        }
        ArrayList<String>lineList=new ArrayList<>();
        //組み入れ路線は現在開いているLineFileから選択されます
        for(LineFile line:((MainActivity)context).getAOdia().getLineFileList()){
            if(line.name.length()==0){
                lineList.add("路線名無し");
            }
            else {
                lineList.add(line.name);
            }
        }

        final Spinner lineSpiner=findViewById(R.id.lineSpinner);
        final Spinner insertSpiner=findViewById(R.id.insertStation);
        ArrayAdapter<String> lineAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,lineList);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineSpiner.setAdapter(lineAdapter);
        ArrayAdapter<String> insertAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,insertStation);
        insertAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        insertSpiner.setAdapter(insertAdapter);

        findViewById(R.id.insertButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //路線組み入れ
                int insertStation=insertSpiner.getSelectedItemPosition();
                LineFile insertFile=((MainActivity)context).getAOdia().getLineFileList().get(lineSpiner.getSelectedItemPosition()).clone();
                if(((CheckBox)findViewById(R.id.reverse2)).isChecked()){
                    insertFile.reverse();
                }

                if(insertFile==lineFile){
                    SDlog.toast("組み入れ元と組み入れ先は異なるファイルである必要があります");
                    return;
                }
                lineFile.addLineFile(insertStation,insertFile);
                ((MainActivity)context).getAOdia().openTimeTable(lineFile,0,0);

                dismiss();
            }
        });
    }
}
