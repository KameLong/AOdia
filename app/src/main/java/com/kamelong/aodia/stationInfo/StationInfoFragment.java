package com.kamelong.aodia.stationInfo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.aodia.KLFragment;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaTrain;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;

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
public class StationInfoFragment extends KLFragment {
    private int fileNum=0;
    public int diaNumber=0;
    public int direct=0;
    public int station=0;
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

    static final int[] backColor=new int[]{Color.rgb(134,179,224),Color.rgb(241,157,181),Color.rgb(224,224,137),Color.rgb(181,224,137),Color.rgb(224,137,224)};
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
            diaFile = ((MainActivity) getActivity()).diaFiles.get(fileNum);
        }catch(Exception e){
            SdLog.log(e);
            Toast.makeText(getActivity(),"なぜこの場所でエラーが起こるのか不明です。対策したいのですが、理由不明のため対策ができません。情報募集中です！",Toast.LENGTH_LONG);
        }
        if(diaFile==null){
            onDestroy();
            return contentView;
        }
        subName=new String[diaFile.getStationNum()];
        subNameCount=new int[diaFile.getStationNum()];
        usedTrainType=new boolean[diaFile.getTypeNum()];
        for(int i=0;i<subName.length;i++){
            subName[i]="";
            subNameCount[i]=0;
        }
        for(int i=0;i<diaFile.getTypeNum();i++){
            usedTrainType[i]=false;
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
            title=title+diaFile.getStation(station).getName()+"駅　時刻表("+diaFile.getDiaName(diaNumber)+")\n"+directS+"　"+diaFile.getStation((1-direct)*(diaFile.getStationNum()-1)).getName()+"方面";
            TextView titleView=(TextView)findViewById(R.id.titleView);
            titleView.setText(title);
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StationIndoDialog dialog = new StationIndoDialog(getActivity(),diaFile,fileNum,diaNumber,direct,station);
                    dialog.show();

                }
            });
            flexBoxIds = new int[]{R.id.hour3, R.id.hour4, R.id.hour5, R.id.hour6, R.id.hour7, R.id.hour8, R.id.hour9, R.id.hour10, R.id.hour11, R.id.hour12, R.id.hour13, R.id.hour14,
                    R.id.hour15, R.id.hour16, R.id.hour17, R.id.hour18, R.id.hour19, R.id.hour20, R.id.hour21, R.id.hour22, R.id.hour23, R.id.hour24, R.id.hour1, R.id.hour2};


            makeTimeTable();

        }catch(Exception e){
            SdLog.log(e);
        }

    }
    private void makeTimeTable(){
        boolean existStartStation=false;
        boolean existEndStation=true;
        boolean existGoOther=false;

        ArrayList<Integer> trainList=makeTrainArray();
        for(int i=0;i<24;i++){
            ((FlexboxLayout)findViewById(flexBoxIds[i])).addView(new StationTimeTableTrainView(getActivity(),"","","", Color.WHITE,-1,0,0,0));
        }
        addStationLegend();
        for(int i=trainList.size()-1;i>=0;i--){
            AOdiaTrain train=diaFile.getTrain(diaNumber, direct, trainList.get(i));
            int hour=train.getPredictionTime(station)/3600;
            if(hour<3||hour>26){
                hour=hour+21;
                hour=hour%24;
                hour=hour+3;
            }
            String min=""+(train.getPredictionTime(station)/60)%60;
            int color=diaFile.getTrainType(train.getType()).getAOdiaTextColor();
            if(train.getStopType(station)!= AOdiaTrain.STOP_TYPE_STOP){
               color=Color.rgb(150,150,150);
            }
            if(!train.timeExist(station)){
                min=min+"?";
            }
            String destination=diaFile.getStation(train.getEndStation(direct)).getName();
            String addInfo="";

            if(train.getStartStation(direct)==station){
                addInfo+="●";
                existStartStation=true;
            }
            if(train.getEndStation(direct)==station){
                addInfo+="▽";
                existEndStation=true;
            }
            if(train.getEndStation(direct)+(1-2*direct)>=0&&train.getEndStation(direct)+(1-2*direct)<diaFile.getStationNum()){
                if(train.getStopType(train.getEndStation(direct)+(1-2*direct))== AOdiaTrain.STOP_TYPE_PASS||train.getStopType(train.getEndStation(direct)+(1-2*direct))== AOdiaTrain.STOP_TYPE_NOVIA){
                    addInfo+="||";
                    existGoOther=true;
                }
            }

            StationTimeTableTrainView trainView=new StationTimeTableTrainView(getActivity(),""+min,subName[train.getEndStation(direct)],addInfo,color,fileNum,diaNumber,direct,trainList.get(i));
            ((FlexboxLayout)findViewById(flexBoxIds[hour-3])).addView(trainView);

        }
        String str="凡例：";
        if(existStartStation){
            str+="●は当駅始発、";
        }
        if(existEndStation){
            str+="▽は当駅終着、";
        }
        if(existGoOther){
            str+="||は他線直通";
        }
        TextView textView=(TextView)findViewById(R.id.textView);
        textView.setText(str+textView.getText());

        TextView textView2=new TextView(getActivity());
        textView2.setText("種別：");
        textView2.setTextColor(Color.BLACK);
        ((FlexboxLayout)findViewById(R.id.example)).addView(textView2);


        for(int i=0;i<usedTrainType.length;i++){

            if(usedTrainType[i]){
                TextView typeView=new TextView(getActivity());
                typeView.setText(diaFile.getTrainType(i).getName()+"　");
                typeView.setTextColor(diaFile.getTrainType(i).getAOdiaTextColor());
//                StationTimeTableTrainView exampleView=new StationTimeTableTrainView(getActivity(),"00:"+diaFile.getTrainType(i).getName(),"","",diaFile.getTrainType(i).getTextColor(),fileNum,diaNumber,direct,-1);
//                ((FlexboxLayout)findViewById(R.id.example)).addView(exampleView);
                ((FlexboxLayout)findViewById(R.id.example)).addView(typeView);
            }
        }
        if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("STTpass",false)){
            TextView typeView=new TextView(getActivity());
            typeView.setText("通過列車　");
            typeView.setTextColor(Color.rgb(150,150,150));
//            StationTimeTableTrainView exampleView=new StationTimeTableTrainView(getActivity(),"00:通過列車","","",Color.rgb(150,150,150),fileNum,diaNumber,direct,-1);
            ((FlexboxLayout)findViewById(R.id.example)).addView(typeView);
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
    private ArrayList<Integer> makeTrainArray(){
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ArrayList<Integer> trainList = new ArrayList<Integer>();
        for (int i = 0; i < diaFile.getTrainNum(diaNumber, direct); i++) {
            if (diaFile.getTrain(diaNumber, direct, i).getPredictionTime(station)>0){
                if(diaFile.getTrain(diaNumber, direct, i).getStopType(station)!= AOdiaTrain.STOP_TYPE_STOP&&!spf.getBoolean("STTpass",false)){
                    continue;
                }
                if(diaFile.getTrain(diaNumber, direct, i).getEndStation(direct)==station&&!spf.getBoolean("endTrain",false)){
                    continue;
                }
                addStationSubName(diaFile.getTrain(diaNumber, direct, i).getEndStation(direct));
                if(diaFile.getTrain(diaNumber, direct, i).getStopType(station)== AOdiaTrain.STOP_TYPE_STOP){
                    usedTrainType[diaFile.getTrain(diaNumber, direct, i).getType()]=true;
                }
                int j = 0;
                for (j = trainList.size(); j > 0; j--) {
                    if (diaFile.getTrain(diaNumber, direct, i).getPredictionTime(station) < diaFile.getTrain(diaNumber, direct, trainList.get(j - 1)).getPredictionTime(station)) {
                        break;
                    }
                }
                trainList.add(j,i);
            }
        }
        return trainList;
    }

    /**
     * 凡例部分に駅名一覧を追加します。
     */
    private void addStationLegend(){
        String result="\n行先：";
        int max=0;
        int maxIndex=-1;
        for(int i=0;i<subNameCount.length;i++){
            if(subNameCount[i]>max){
                maxIndex=i;
                max=subNameCount[i];
            }
        }

            String directS="";
            if(direct==0){
                directS="下り";
            }else{
                directS="上り";
            }

            String title="";
        if(maxIndex>=0) {
            title = title + diaFile.getStation(station).getName() + "駅　時刻表(" + diaFile.getDiaName(diaNumber) + ")\n" + directS + "　" + diaFile.getStation(maxIndex).getName() + "方面";
        }else{
            title = title + diaFile.getStation(station).getName() + "駅　時刻表(" + diaFile.getDiaName(diaNumber) + ")\n" + directS + "　列車なし";
        }
        TextView titleView = (TextView) findViewById(R.id.titleView);
        titleView.setText(title);


            if(maxIndex>=0){

            result=result+"無印:"+diaFile.getStation(maxIndex).getName();
            subName[maxIndex]="";
            subNameCount[maxIndex]=0;
            for(int station=0;station<diaFile.getStationNum();station++){
                if(subNameCount[station]==0)continue;
                subName[station]=diaFile.getStation(station).getName().substring(0,1);
                for(int i=0;i<diaFile.getStation(station).getName().length();i++){
                    boolean sameFrag=false;//同じものがあるかどうか
                    for(int j=0;j<station;j++){
                        if(subName[j].equals(diaFile.getStation(station).getName().substring(i,i+1))){
                            sameFrag=true;
                        }
                    }
                    if(!sameFrag){
                        subName[station]=diaFile.getStation(station).getName().substring(i,i+1);
                        break;
                    }
                }

            }
            for(int i=0;i<subName.length;i++){
                if(!subName[i].equals("")){
                    result=result+"　"+subName[i]+":"+diaFile.getStation(i).getName();
                }
            }}
        TextView textView=(TextView)findViewById(R.id.textView);
        textView.setText(result);
    }
    public String fragmentName(){
        String directS="";
        if(direct==0){
            directS="下り";
        }else{
            directS="上り";
        }
        return "駅時刻表　"+diaFile.getStation(station).getName()+"　"+directS+diaFile.getLineName();
    }
    public View findViewById(int id){
        return contentView.findViewById(id);
    }
}

