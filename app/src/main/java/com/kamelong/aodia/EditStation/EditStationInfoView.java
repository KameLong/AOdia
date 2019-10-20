package com.kamelong.aodia.EditStation;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.OuterTerminal;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.StationTrack;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * １つの駅に対する編集画面
 * 全ての駅の編集項目を一度に初期化すると処理時間が長すぎるため、EditStationViewから分割した
 */
public class EditStationInfoView extends LinearLayout {
    Station station = null;

    public EditStationInfoView(MainActivity context, final Station station, final LineFile lineFile) {
        super(context);
        this.station = station;
        try {
            LayoutInflater.from(context).inflate(R.layout.edit_station, this);
        } catch (Exception e) {
            SDlog.log(e);
        }
        try {
            CheckBox arriveToggleDown = findViewById(R.id.ariDown);
            arriveToggleDown.setChecked(station.showArrivalCustom[Train.DOWN]);
            arriveToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showArrivalCustom[Train.DOWN] = b;
                }
            });
            CheckBox stopToggleDown = findViewById(R.id.stopDown);
            stopToggleDown.setChecked((station.showtrack[Train.DOWN]));
            stopToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showtrack[Train.DOWN] = b;
                }
            });
            CheckBox departToggleDown = findViewById(R.id.depDown);
            departToggleDown.setChecked(station.showDepartureCustom[Train.DOWN]);
            departToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showDepartureCustom[Train.DOWN] = b;
                }
            });
            CheckBox arriveToggleUp = findViewById(R.id.ariUp);
            arriveToggleUp.setChecked(station.showArrivalCustom[Train.UP]);
            arriveToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showArrivalCustom[Train.UP] = b;
                }
            });
            CheckBox stopToggleUp = findViewById(R.id.stopUp);
            stopToggleUp.setChecked((station.showtrack[Train.UP]));
            stopToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showtrack[Train.UP] = b;
                }
            });
            CheckBox departToggleUp = findViewById(R.id.depUp);
            departToggleUp.setChecked(station.showDepartureCustom[Train.UP]);
            departToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.showDepartureCustom[Train.UP] = b;
                }
            });
            RadioGroup bigRadio = findViewById(R.id.bigRadio);
            if (station.bigStation) {
                bigRadio.check(R.id.bigStation);
            } else {
                bigRadio.check(R.id.normalStation);
            }
            bigRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    station.bigStation = (id == R.id.bigStation);
                }
            });
            initStop();
            initOuterStation();
            Spinner branchSpinner = findViewById(R.id.branchSpinner);
            List<String> stationList = new ArrayList<>();
            stationList.add("分岐無し");

            for (int i = 0; i < lineFile.getStationNum(); i++) {
                stationList.add(i + "：" + lineFile.getStation(i).name);
            }
            ArrayAdapter<String> stationDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stationList);
            stationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            branchSpinner.setAdapter(stationDataAdapter);
            if (station.brunchCoreStationIndex < 0) {
                branchSpinner.setSelection(0);
            } else {
                branchSpinner.setSelection(station.brunchCoreStationIndex + 1);

            }
            branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    station.brunchCoreStationIndex = i - 1;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        } catch (Exception e) {
            SDlog.log(e);
        }
    }

    public void initStop() {
        final int height = (int) (50 * getResources().getDisplayMetrics().density);
        LinearLayout stopNameLinear = findViewById(R.id.stopNameLinear);
        LinearLayout stopShortNameLinear = findViewById(R.id.stopShortNameLinear);
        final RadioGroup downRadio = findViewById(R.id.downMain);
        final RadioGroup upRadio = findViewById(R.id.upMain);
        LinearLayout deleteLinear = findViewById(R.id.delete);

        stopNameLinear.removeAllViews();
        stopShortNameLinear.removeAllViews();
        upRadio.removeAllViews();
        downRadio.removeAllViews();
        deleteLinear.removeAllViews();
        for (int i = 0; i < station.getTrackNum(); i++) {
            final int index=i;
            final EditText editText1 = new EditText(getContext());
            editText1.setHeight(height);

            editText1.setText(station.getTrackName(i));
            stopNameLinear.addView(editText1);
            editText1.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    station.setTrackName(index, editText1.getEditableText().toString());

                }
            });
            final EditText editText2 = new EditText(getContext());
            editText2.setText(station.getTrackShortName(i));
            editText2.setHeight(height);
            stopShortNameLinear.addView(editText2);
            editText2.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    station.setTrackShortName(index, editText2.getEditableText().toString());

                }
            });
            final RadioButton downButton = new RadioButton(getContext());
            downButton.setHeight(height);
            downRadio.addView(downButton);
            final RadioButton upButton = new RadioButton(getContext());
            upButton.setHeight(height);
            upRadio.addView(upButton);
            final Button delete=new Button(getContext());
            delete.setBackgroundResource(android.R.drawable.ic_menu_delete);
            delete.setWidth(height);
            delete.setHeight(height);
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!station.deleteTrack(index)){
                        SDlog.toast("主本線は削除できません");
                    }
                    initStop();

                }
            });
            deleteLinear.addView(delete);



        }
        downRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index=downRadio.indexOfChild(findViewById(checkedId));
                if(index>=0&&station.stopMain[Train.DOWN]!=index){
                    station.stopMain[Train.DOWN]=index;
                    initStop();
                }
            }
        });
        if(station.stopMain[Train.DOWN]>=0&&station.stopMain[Train.DOWN]<station.getTrackNum()){
            downRadio.check(downRadio.getChildAt(station.stopMain[Train.DOWN]).getId());
        }
        upRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index=upRadio.indexOfChild(findViewById(checkedId));
                if(index>=0&&station.stopMain[Train.UP]!=index){
                    station.stopMain[Train.UP]=index;
                    initStop();
                }

            }
        });
        if(station.stopMain[Train.UP]>=0&&station.stopMain[Train.UP]<station.getTrackNum()){
            upRadio.check(upRadio.getChildAt(station.stopMain[Train.UP]).getId());
        }
        Button newStopButton=findViewById(R.id.addTrack);
        newStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int stopNum=station.getTrackNum()+1;
                StationTrack track=new StationTrack(stopNum+"番線",stopNum+"");
                station.addTrack(track);
                initStop();
            }
        });


    }
    public void initOuterStation() {
        final int height = (int) (50 * getResources().getDisplayMetrics().density);
        LinearLayout stationNameLinear = findViewById(R.id.outerStationNameLinear);
        LinearLayout stationShortNameLinear = findViewById(R.id.outerStationShortNameLinear);
        LinearLayout deleteLinear = findViewById(R.id.deleteOuterStation);

        stationNameLinear.removeAllViews();
        stationShortNameLinear.removeAllViews();
        deleteLinear.removeAllViews();
        for (int i = 0; i < station.outerTerminals.size(); i++) {
            final OuterTerminal outerTerminal=station.outerTerminals.get(i);
            final EditText editText1 = new EditText(getContext());
            editText1.setHeight(height);

            editText1.setText(station.outerTerminals.get(i).outerTerminalName);
            stationNameLinear.addView(editText1);
            editText1.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    outerTerminal.outerTerminalName= editText1.getEditableText().toString();

                }
            });
            final EditText editText2 = new EditText(getContext());
            editText2.setText(outerTerminal.outerTerminalTimeTableName);
            editText2.setHeight(height);
            stationShortNameLinear.addView(editText2);
            editText2.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    outerTerminal.outerTerminalTimeTableName= editText2.getEditableText().toString();
                }
            });
            final Button delete=new Button(getContext());
            delete.setBackgroundResource(android.R.drawable.ic_menu_delete);
            delete.setWidth(height);
            delete.setHeight(height);
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!station.deleteOuterTerminal(outerTerminal)){
                        SDlog.toast("この駅は使用されているため削除できません");
                    }
                    initOuterStation();

                }
            });
            deleteLinear.addView(delete);



        }
        Button newStopButton=findViewById(R.id.addOuter);
        newStopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                OuterTerminal terminal=new OuterTerminal("駅名未設定");
                station.addOuterTerminal(terminal);
                initOuterStation();
            }
        });


    }


}
