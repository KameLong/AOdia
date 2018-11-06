package com.kamelong.aodia.EditStation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kamelong.OuDia.Station;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

import java.util.ArrayList;
import java.util.List;

public class EditStationInfoView extends LinearLayout {
    Station station=null;
    ArrayList<Station>editStation;
    public EditStationInfoView(Context context,final Station station,ArrayList<Station> editStation){
        super(context);
        this.station=station;
        this.editStation=editStation;
        try{
            LayoutInflater.from(context).inflate(R.layout.edit_station, this);
        }catch (Exception e){
            SdLog.log(e);
        }
        try{

            ToggleButton arriveToggleDown=findViewById(R.id.arriveToggleDown);
            arriveToggleDown.setChecked((station.getTimeTableStyle(0)&0b010)!=0);
            arriveToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowArival(0,b);
                }
            });
            ToggleButton stopToggleDown=findViewById(R.id.stopToggleDown);
            stopToggleDown.setChecked((station.getTimeTableStyle(0)&0b100)!=0);
            stopToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowStop(0,b);
                }
            });
            ToggleButton departToggleDown=findViewById(R.id.departToggleDown);
            departToggleDown.setChecked((station.getTimeTableStyle(0)&0b001)!=0);
            departToggleDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowDepart(0,b);
                }
            });
            ToggleButton arriveToggleUp=findViewById(R.id.arriveToggleUp);
            arriveToggleUp.setChecked((station.getTimeTableStyle(1)&0b010)!=0);
            arriveToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowArival(1,b);
                }
            });
            ToggleButton stopToggleUp=findViewById(R.id.stopToggleUp);
            stopToggleUp.setChecked((station.getTimeTableStyle(1)&0b100)!=0);
            stopToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowStop(1,b);
                }
            });
            ToggleButton departToggleUp=findViewById(R.id.departToggleUp);
            departToggleUp.setChecked((station.getTimeTableStyle(1)&0b001)!=0);
            departToggleUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    station.setShowDepart(1,b);
                }
            });
            RadioGroup bigRadio=findViewById(R.id.bigRadio);
            if(station.bigStation){
                bigRadio.check(R.id.bigStation);
            }else{
                bigRadio.check(R.id.normalStation);
            }
            bigRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    station.bigStation=(id==R.id.bigStation);
                }
            });
            initStop();
            Spinner branchSpinner=findViewById(R.id.branchSpinner);
            List<String> stationList=new ArrayList<>();
            stationList.add("分岐無し");

            for(int i=0;i<editStation.size();i++){
                stationList.add(i+"："+editStation.get(i).name);
            }
            ArrayAdapter<String> stationDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stationList);
            stationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            branchSpinner.setAdapter(stationDataAdapter);
            if(station.brunchStationIndex<0){
                branchSpinner.setSelection(0);
            }else{
                branchSpinner.setSelection(station.brunchStationIndex+1);

            }
            branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    station.brunchStationIndex=i-1;

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }catch (Exception e){
            SdLog.log(e);
        }
    }
    public void initStop(){
        LinearLayout stopNameLinear=findViewById(R.id.stopNameLinear);
        LinearLayout stopShortNameLinear=findViewById(R.id.stopShortNameLinear);
        stopNameLinear.removeAllViews();
        stopShortNameLinear.removeAllViews();
        for(int i=1;i<station.trackName.size();i++){
            final EditText editText1=new EditText(getContext());
            editText1.setId(i);
            editText1.setText(station.trackName.get(i));
            stopNameLinear.addView(editText1);
            editText1.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(editText1.getEditableText().toString().length()==0){
                        editText1.setText(station.trackName.get(editText1.getId()));
                        return;
                    }
                    station.trackName.set(editText1.getId(),editText1.getEditableText().toString());

                }
            });
            final EditText editText2=new EditText(getContext());
            editText2.setText(station.trackshortName.get(i));
            stopShortNameLinear.addView(editText2);
            editText2.setId(i);
            editText2.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(editText2.getEditableText().toString().length()==0){
                        editText2.setText(station.trackshortName.get(editText2.getId()));
                        return;
                    }
                    station.trackshortName.set(editText2.getId(),editText2.getEditableText().toString());

                }
            });

        }
        Button newStopButton=findViewById(R.id.addNewStop);
        newStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stopNum=station.trackName.size();
                station.trackName.add(stopNum+"番線");
                station.trackshortName.add(stopNum+"");
                initStop();
            }
        });
        Spinner downMainStop=findViewById(R.id.downMainSpinner);
        Spinner upMainStop=findViewById(R.id.upMainSpinner);
        List<String> stopList=new ArrayList<>();
        for(int i=1;i<station.trackName.size();i++){
            stopList.add(station.trackName.get(i));
        }
        ArrayAdapter<String> downDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stopList);
        downDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        downMainStop.setAdapter(downDataAdapter);
        downMainStop.setSelection(station.stopMain[0]-1);
        downMainStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(station.stopMain[0]!=i+1) {
                    station.stopMain[0] = i + 1;
                    initStop();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ArrayAdapter<String> upDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stopList);
        upDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upMainStop.setAdapter(upDataAdapter);
        upMainStop.setSelection(station.stopMain[1]-1);
        upMainStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(station.stopMain[1]!=i+1) {
                    station.stopMain[1] = i + 1;
                    initStop();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


    }



}
