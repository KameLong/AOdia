package com.kamelong.OuDia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DiaFile {


    public ArrayList<Diagram>diagram=new ArrayList<>();
    public ArrayList<Station>station=new ArrayList<>();
    public ArrayList<TrainType>trainType=new ArrayList<>();

    public String name="";
    public String version="";
    public String comment="";

    private ArrayList<Integer>stationTime=new ArrayList<>();
    public int diagramStartTime=3600*3;


    //AOdia専用オプション
    public String filePath="";
    public boolean menuOpen=true;
    public DiaFile(File file)throws Exception{
        filePath=file.getPath();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        version=br.readLine().split("=",-1)[1];
        if(version.startsWith("OuDia.")){
            loadShiftJis(file);
        }else{
            loadDiaFile(br);
        }
        reCalcStationTime();
        System.out.println("読み込み終了");

    }
    private void loadShiftJis(File file)throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
        String nouse=br.readLine();
        loadDiaFile(br);
    }
    private void loadDiaFile(BufferedReader br){
        try {
            br.readLine();//Rosen.
            name=br.readLine().split("=",-1)[1];
            String line=br.readLine();
            while(!line.equals(".")){
                if(line.equals("Eki.")){
                    station.add(new Station(br));
                }
                if(line.equals("Ressyasyubetsu.")){
                    trainType.add(new TrainType(br));
                }
                if(line.equals("Dia.")){
                    diagram.add(new Diagram(this,br));
                }
                line=br.readLine();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getDiaNum(){
        return diagram.size();
    }
    public int getStationNum(){
        return station.size();
    }

    public void reCalcStationTime(){
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
    public Train getTrain(int diagramIndex,int direction,int trainIndex){
        return diagram.get(diagramIndex).trains[direction].get(trainIndex);
    }
    public int getTrainSize(int diagramIndex,int direction){
        return diagram.get(diagramIndex).trains[direction].size();
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
                for(int train=0;train<getTrainSize(i,0);train++){
                    int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                    if(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1){
                        value+=120;
                    }
                    if(value>0&&result>value){
                        result=value;
                    }
                }
                for(int train=0;train<getTrainSize(i,1);train++){
                    int value=this.getTrain(i,1,train).getRequiredTime(startStation,endStation);
                    if(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
                if(result==360000){
                    result=120;
                }
                return result;            }
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

}
