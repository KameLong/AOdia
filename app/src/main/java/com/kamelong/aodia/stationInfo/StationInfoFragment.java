package com.kamelong.aodia.stationInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

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
public class StationInfoFragment extends AOdiaFragment {
    private int fileNum=0;
    private int diaNumber=0;
    private int direct=0;
    private int station=0;
    private int[] flexBoxIds;
    private boolean[] usedTrainType;
    /**
     * 行き先の短縮名称
     */
    private String[] subName;
    /**
     * 行き先の短縮名称の使用回数
     * 最多使用のものは無印となる
     */
    private int[] subNameCount;
    private View contentView;

    private static final int[] backColor=new int[]{Color.rgb(134,179,224),Color.rgb(241,157,181),Color.rgb(224,224,137),Color.rgb(181,224,137),Color.rgb(224,137,224)};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            Bundle bundle = getArguments();
            diaNumber = bundle.getInt("diaN");
            direct = bundle.getInt("direct");
            station=bundle.getInt("station");
            fileNum=bundle.getInt("fileNum");
        } catch (Exception e) {
            SdLog.log(e);
        }
        contentView = inflater.inflate(R.layout.station_timetable, container, false);
        try {
            setDiaFile(((AOdiaActivity) getAodiaActivity()).getDiaFiles().get(fileNum));
        }catch(Exception e){
            SdLog.log(e);
            Toast.makeText(getAodiaActivity(),"Error-StationInfoFragment-onCreateView-E1",Toast.LENGTH_SHORT).show();
        }
        if(getDiaFile() ==null){
            onDestroy();
            return contentView;
        }
        return contentView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        try {
            if(diaNumber<backColor.length){
                findViewById(R.id.mainLinear).setBackgroundColor(backColor[diaNumber]);
            }
            String directS="";
            if(direct==0){
                directS="下り";
            }else{
                directS="上り";
            }
            String title="";
            TextView titleView=(TextView)findViewById(R.id.titleView);
            titleView.setText(title);
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StationInfoDialog dialog = new StationInfoDialog(getAodiaActivity(), getDiaFile(),fileNum,diaNumber,direct,station);
                    dialog.show();

                }
            });
            flexBoxIds = new int[]{R.id.hour3, R.id.hour4, R.id.hour5, R.id.hour6, R.id.hour7, R.id.hour8, R.id.hour9, R.id.hour10, R.id.hour11, R.id.hour12, R.id.hour13, R.id.hour14,
                    R.id.hour15, R.id.hour16, R.id.hour17, R.id.hour18, R.id.hour19, R.id.hour20, R.id.hour21, R.id.hour22, R.id.hour23, R.id.hour24, R.id.hour1, R.id.hour2};



        }catch(Exception e){
            SdLog.log(e);
        }

    }

    /**
     * 駅名の略称を作成する。
     * 基本は最初の一文字、それまでに使われている文字があれば２文字目、３文字目…となる。
     * @param station
     */
    private void addStationSubName(int station){
        if(station==this.station)return;
        subNameCount[station]++;
        if(subName[station].length()!=0){
            return;
        }
        return;
        /*
        String name=diaFile.getStationName(station);
        for(int i=0;i<name.length();i++){
            boolean sameFrag=false;//同じものがあるかどうか
            for(int j=0;j<subName.length;j++){
                if(subName[j].equals(name.substring(i,i+1))){
                    sameFrag=true;
                }
            }
            if(!sameFrag){
                subName[station]=name.substring(i,i+1);
                return;
            }
        }
        subName[station]=name.substring(0,1);*/
    }

    /**
     * 凡例部分に駅名一覧を追加します。
     */
    @Override
    public String fragmentName(){
        try {
            String directS = "";
            if (direct == 0) {
                directS = "下り";
            } else {
                directS = "上り";
            }
            return "駅時刻表　";
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public String fragmentHash(){
        return "StationInfo-"+ getDiaFile().getFilePath()+"-"+diaNumber+"-"+direct;
    }
    protected View findViewById(int id){
        return contentView.findViewById(id);
    }
}

