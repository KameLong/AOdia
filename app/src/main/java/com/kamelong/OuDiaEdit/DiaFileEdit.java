package com.kamelong.OuDiaEdit;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.TrainType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DiaFileEdit extends DiaFile {
    /**
     * 駅間の所要時間
     */
    private ArrayList<Integer>stationTime=new ArrayList<>();

    /**
     * 新規に空路線を生成する
     */
    public DiaFileEdit(){
        version="OuDiaSecond.1.06";
        name="新しい路線";
        trainType.add(new TrainType());
        diagram.add(new Diagram(this));
    }


    /**
     * ファイルからダイヤを開く
     * @param file　入力ファイル
     * @throws Exception ファイルが読み込めなかった時に返す
     */
    public DiaFileEdit(File file)throws Exception{
        super(file);
        //最小所要時間を計算する
        reCalcStationTime();
    }
    /**
     *    ２つのダイヤファイルを結合して新しいダイヤファイルを作る
     *    todo
     */
    public DiaFileEdit(DiaFile[] diaList,int includeStationIndex) {
        boolean stationConnect = false;
        ArrayList<Integer>[] stationIndexList = new ArrayList[2];
        stationIndexList[0] = new ArrayList<>();
        stationIndexList[1] = new ArrayList<>();
        for (int i = 0; i < diaList[0].getStationNum() && i < includeStationIndex; i++) {
            station.add(new Station(diaList[0].station.get(i)));
        }
        if (diaList[0].getStationNum() > includeStationIndex) {
            if (diaList[0].station.get(includeStationIndex).name.equals(diaList[1].station.get(0).name)) {
                stationConnect = true;
            } else {
                stationConnect = false;
                station.add(new Station(diaList[0].station.get(includeStationIndex)));
            }
        }
        for (int i = 0; i < diaList[1].getStationNum(); i++) {
            station.add(new Station(diaList[1].station.get(i)));
        }
        for (int i = includeStationIndex + 1; i < diaList[0].getStationNum(); i++) {
            station.add(new Station(diaList[0].station.get(i)));
        }
    }



    public void reCalcStationTime(){
        stationTime=new ArrayList<>();
        stationTime.add(0);
        for(int i=0;i<getStationNum()-1;i++){
            stationTime.add(stationTime.get(stationTime.size()-1)+getMinReqiredTime(i,i+1));
        }
    }
    /**
     * 最小所要時間のリストを返します。
     * 最小所要時間は別スレッドで計算されている場合がありますので、
     * 計算が終了するまで、スレッドを待機させます。
     * @return
     */
    public ArrayList<Integer> getStationTime(){
        while(stationTime.size()<getStationNum()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationTime;
    }

    /**
     * 最小所要時間を計算する。
     * この関数は処理の完了までにかなりの時間がかかると予想されます。
     */
    protected void calcMinReqiredTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                stationTime.add(0);
                for(int i=0;i<getStationNum()-1;i++){
                    stationTime.add(stationTime.get(stationTime.size()-1)+getMinReqiredTime(i,i+1));
                }
            }
        }).start();
    }

        /**
         *  駅間最小所要時間を返す。
         *  startStatioin endStationの両方に止まる列車のうち、
         *  所要時間（着時刻-発時刻)の最も短いものを秒単位で返す。
         *  ただし、駅間所要時間が90秒より短いときは90秒を返す。
         *
         *  startStation endStationは便宜上区別しているが、順不同である。
         * @param startStation
         * @param endStation
         * @return time(second)
         */
        public int getMinReqiredTime(int startStation,int endStation){
            int result=360000;
            for(int i=0;i<getDiaNum();i++){
                if(diagram.get(i).name.equals("基準運転時分")){
                    result=360000;
                    for(int train=0;train<getTrainSize(i,0);train++){
                        int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                        if(value>0&&(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1)){
                            value+=120;
                        }
                        if(value>0&&result>value){
                            result=value;
                        }
                    }
                    for(int train=0;train<getTrainSize(i,1);train++){
                        int value=this.getTrain(i,1,train).getRequiredTime(startStation,endStation);
                        if(value>0&&(getTrain(i,1,train).getStop(startStation)!=1||getTrain(i,1,train).getStop(endStation)!=1)){
                            value+=120;
                        }

                        if(value>0&&result>value){
                            result=value;
                        }
                    }
                    if(result==360000){
                        result=120;
                    }
                    return result;
                }
            }
            for(int i=0;i<getDiaNum();i++){

                for(int train=0;train<getTrainSize(i,0);train++){
                    int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1)){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
                for(int train=0;train<getTrainSize(i,1);train++){
                    int value=getTrain(i,1,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrain(i,1,train).getStop(startStation)!=1||getTrain(i,1,train).getStop(endStation)!=1)){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
            }
            if(result==360000){
                result=120;
            }
            if(result<90){
                result=90;
            }

            return result;
        }

    public void copyDiagram(int diagramIndex,String diaName){
        diagram.add((Diagram) diagram.get(diagramIndex).clone());
        diagram.get(diagram.size()-1).name=diaName;
    }
    public void addNewDiagram(){
        diagram.add(new Diagram(this));
    }
    public void deleteDiagram(int diagramIndex){
        diagram.remove(diagramIndex);
    }

}
