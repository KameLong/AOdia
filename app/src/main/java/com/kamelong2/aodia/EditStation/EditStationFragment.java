package com.kamelong2.aodia.EditStation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kamelong2.OuDia.Diagram;
import com.kamelong2.OuDia.Station;
import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong2.aodia.SDlog;

import java.util.ArrayList;

public class EditStationFragment extends AOdiaFragment implements StationEditInterface{
    int fileIndex = 0;
    public ArrayList<Station> editStationList;
    public ArrayList<Integer>editStationIndex;
    public ArrayList<EditStationView> editStationViews=new ArrayList<>();
    boolean frag=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            fileIndex = bundle.getInt("fileIndex", 0);
        } catch (Exception e) {
            SDlog.log(e);
        }
        try {
            fragmentContainer = inflater.inflate(R.layout.old_edit_station_fragment, container, false);
            try {
                diaFile = getAOdiaActivity().diaFiles.get(fileIndex);
                if (diaFile == null) {
                    Toast.makeText(getContext(), "ダイヤファイルが見つかりませんでした。", Toast.LENGTH_LONG).show();
                    getAOdiaActivity().killFragment(getAOdiaActivity().fragmentIndex);
                    return fragmentContainer;
                }
            } catch (Exception e) {
                SDlog.log(e);
            }

            editStationList = new ArrayList<>();
            editStationIndex = new ArrayList<>();
            for (int i = 0; i < diaFile.getStationNum(); i++) {
                editStationList.add(new Station(diaFile.station.get(i)));
                editStationIndex.add(i);

            }
            return fragmentContainer;
        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast("駅編集の際にエラーが発生しました");
            getFragmentManager().beginTransaction().remove(this).commit();
            return fragmentContainer;
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        if(diaFile==null){
            return;
        }
        final LinearLayout stationList=(LinearLayout) findViewById(R.id.stationList);
        for(int i=0;i<diaFile.getStationNum();i++){
            EditStationView editStationView=new EditStationView(getContext(),editStationList.get(i),i,editStationList);
            editStationViews.add(editStationView);
            editStationView.setStationEditInterface(this);
            stationList.addView(editStationView,i+1);
        }
        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewStation(0);
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.requestFocus();
                submitEditStation();
            }
        });
        Toast.makeText(getContext(),"駅編集を反映させる為には、「駅編集完了」ボタンを押してください",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void addNewStation(int stationIndex) {
        final LinearLayout stationList=(LinearLayout) findViewById(R.id.stationList);

        editStationList.add(stationIndex,new Station(diaFile));
        EditStationView editStationView=new EditStationView(getContext(),editStationList.get(stationIndex),stationIndex,editStationList);
        editStationView.setStationEditInterface(this);
        editStationViews.add(stationIndex,editStationView);
        editStationIndex.add(stationIndex,-1);
        stationList.addView(editStationView,stationIndex+1);
        for(int i=0;i<editStationList.size();i++){
            editStationViews.get(i).renewStationIndex(i);
            if(editStationList.get(i).brunchStationIndex>=stationIndex){
                editStationList.get(i).brunchStationIndex++;
            }
            editStationViews.get(i).closeInfo();
        }



    }

    @Override
    public void removeStation(int stationIndex) {
        final LinearLayout stationList=(LinearLayout) findViewById(R.id.stationList);

        editStationList.remove(stationIndex);
        editStationViews.remove(stationIndex);
        editStationIndex.remove(stationIndex);
        stationList.removeViewAt(stationIndex+1);
        for(int i=0;i<editStationList.size();i++){
            editStationViews.get(i).renewStationIndex(i);
            editStationViews.get(i).closeInfo();
            if(editStationList.get(i).brunchStationIndex==stationIndex){
                editStationList.get(i).brunchStationIndex=-1;
            }
            if(editStationList.get(i).brunchStationIndex>stationIndex){
                editStationList.get(i).brunchStationIndex--;
            }

        }
    }
    @Override
    public void renewStationName(int stationIndex){

    }
    public void submitEditStation(){
        for(int i=0;i<editStationList.size();i++){
            if(editStationList.get(i).name.length()==0){
                Toast.makeText(getContext(),"駅index="+i+"　の駅に駅名が入力されていません",Toast.LENGTH_LONG).show();
                return;
            }
            for(int j=1;j<editStationList.get(i).trackName.size();j++){
                if(editStationList.get(i).trackName.get(j).length()==0){
                    Toast.makeText(getContext(),editStationList.get(i).name+"駅の番線名が入力されていません",Toast.LENGTH_LONG).show();
                    return;
                }
                if(editStationList.get(i).trackshortName.get(j).length()==0){
                    Toast.makeText(getContext(),editStationList.get(i).name+"駅の番線略称が入力されていません",Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        diaFile.station=editStationList;
        for(Diagram diagram:diaFile.diagram){
            for(Train train:diagram.trains[0]){
                train.editStationSubmit(editStationIndex);
            }
            for(Train train:diagram.trains[1]){
                train.editStationSubmit(editStationIndex);
            }
            diagram.reNewOperation();

        }
        diaFile.reCalcStationTime();
        getAOdiaActivity().killFragment(getAOdiaActivity().fragmentIndex);
    }



    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public String fragmentName(){
        return "駅編集";
    }


}
