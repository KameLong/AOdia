package com.kamelong.OuDia;

import com.kamelong.tool.Color;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Diagram implements Cloneable{
    public static final int TIMETABLE_BACKCOLOR_NUM = 4;
    /**
     ダイヤの名称です。
     （例） "平日ダイヤ" など
     CentDedRosen に包含される CentDedDia では、
     この属性は一意でなくてはなりません。
     */
    public String name="";
    /**
     時刻表画面における基本背景色のIndexです
     単色時、種別色時の空行、縦縞・横縞・市松模様時
     および、基準運転時分機能有効時に用います。
     範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    public int mainBackColorIndex=0;
    /**
     時刻表画面における補助背景色のIndexです
     縦縞・横縞・市松模様時に用います。
     範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    public int subBackColorIndex=0;
    /**
     時刻表画面における背景色パターンのIndexです
     0:単色
     1:種別色
     2:縦縞
     3:横縞
     4:市松模様
     */
    public int timeTableBackPatternIndex=0;
    /**
     * ダイヤにふくまれる列車
     * [0]下り時刻表
     * [1]上り時刻表
     */
    public ArrayList<Train>[] trains=new ArrayList[2];
    public DiaFile diaFile;

    public Diagram(){

    }
    public Diagram(DiaFile diaFile){
        this.diaFile=diaFile;
        name="新しいダイヤ";
        trains[0]=new ArrayList<>();
        trains[0].add(new Train(diaFile,0));
        trains[1]=new ArrayList<>();
        trains[1].add(new Train(diaFile,1));
    }
    public void setValue(String title,String value){
        switch (title){
            case "DiaName":
                name=value;
                break;
            case "MainBackColorIndex":
                mainBackColorIndex=Integer.parseInt(value);
                break;
            case "SubBackColorIndex":
                subBackColorIndex=Integer.parseInt(value);
                break;
            case "BackPatternIndex":
                timeTableBackPatternIndex=Integer.parseInt(value);
                break;
        }
    }

    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param direction 並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    public void sortTrain(int direction,int stationNumber){
        long startTime=System.currentTimeMillis();
        long startCount=Train.count2;
        Train[] trainList=trains[direction].toArray(new Train[0]);

        //ソートする前の順番を格納したクラス
        ArrayList<Integer> sortBefore=new ArrayList<>();
        //ソートした後の順番を格納したクラス
        ArrayList<Integer> sortAfter=new ArrayList<>();

        for(int i=0;i<trainList.length;i++){
            sortBefore.add(i);
        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber)>0&&!trainList[sortBefore.get(i)].checkDoubleDay()) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }
        //この時点で基準駅に予測時間を設定できるものはソートされている
        if(direction==0) {
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より後方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    searchStation:
                    for(int i=station;i>0;i--){
                        //境界線がある駅の次の駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i-1,station});
                            for(int j=i;j<station;j++){
                                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{j});
                            }
                            station=i;
                            continue baseStation;
                        }
                    }
                    for(int i=station;i<diaFile.getStationNum();i++){
                        //境界線がある駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station-1).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1,i});
                            for(int j=i;j<station;j++){
                                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{j});
                            }
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1});
            }
//            基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station < diaFile.getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i-1});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i,station});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station});

            }
        }else{
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より前方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i-1,station});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station});
            }


            //基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station <diaFile. getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()) {
                    for (int i = station; i > 0; i--) {
                        if (diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)) {
                            addTrainInSort1(sortBefore, sortAfter, trainList, new int[]{station, i - 1});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i,station});
                            continue baseStation;
                        }
                    }

                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station});
            }

        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber)>0) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)>0&&trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }

        sortAfter.addAll(sortBefore);
        ArrayList<Train> trainAfter=new ArrayList<>();
        for(int i=0;i<sortAfter.size();i++){
            trainAfter.add(trainList[sortAfter.get(i)]);
        }
        trains[direction]=trainAfter;
        long endTime=System.currentTimeMillis();
        long endCount=Train.count2;

        System.out.println("sortTime:"+(endTime-startTime));
        System.out.println("count:"+(endCount-startCount));

    }

    private void addTrainInSort1(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int station[]){
        for (int i = sortBefore.size(); i >0; i--) {
            int baseTime = trains[sortBefore.get(i-1)].getArrivalTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i-1)].checkDoubleDay()) {
                continue;
            }
            int j =0;
            boolean frag = false;

            for (j = 0; j < sortAfter.size(); j++) {

                int sortTime;
                if(station.length==2) {
                    sortTime = Math.max(trains[sortAfter.get(j)].getPredictionTime(station[0]), trains[sortAfter.get(j)].getPredictionTime(station[1]));
                }else{
                    sortTime =trains[sortAfter.get(j)].getPredictionTime(station[0]);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime >= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i - 1));
                sortBefore.remove(i-1);
            }
        }
    }
    private void addTrainInSort2(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int[] station){
        for (int i = 0; i < sortBefore.size(); i++) {
            int baseTime = trains[sortBefore.get(i)].getDepartureTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = sortAfter.size(); j > 0; j--) {
                int sortTime;
                if(station.length==2){
                    if(trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE)>0&&trains[sortAfter.get(j - 1)].getPredictionTime(station[1],Train.ARRIVE)>0) {
                        sortTime = Math.min(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE));
                    }else{
                        sortTime = Math.max(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE));

                    }
                }else{
                    sortTime = trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime <= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }

        }

    }

    public void splitTrain(Train train,int station){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                Train newTrain=new Train(train);
                train.endTrain(station);
                newTrain.startTrain(station);
                trains[direction].add(i+1,newTrain);
                return;
            }
        }
    }
    public void combineTrain(Train train,int station){
        int direction=train.direction;
        int i=0;
        for(i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                break;
            }
        }
        for(;i<trains[direction].size();i++){
            if(trains[direction].get(i).type==train.type){
                if(trains[direction].get(i).startStation()==station){
                    train.combine(trains[direction].get(i),station);
                    trains[direction].remove(i);
                    return;
                }
            }
        }
    }
    public void copyTrain(Train train){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                trains[direction].add(i+1,train.clone());
                return;
            }
        }
    }
    public void insertTrain(Train train){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                trains[direction].add(i,new Train(diaFile,direction));
                return;
            }
        }
    }
    public void deleteTrain(Train train){
        int direction=train.direction;
        trains[direction].remove(train);
        if(trains[direction].size()==0){
            trains[direction].add(new Train(diaFile,direction));
        }
    }

    public Train nextOperation(Train train){
        int endStation=train.endStation();
        if(endStation<0)return null;
        int endTime=train.getADTime(endStation);
        if(endTime<0)return null;
        Train bestTrain=null;
        int bestTime=10000000;
        int stop=train.getStop(endStation);
        if(stop==0){
            stop=diaFile.station.get(endStation).stopMain[train.direction];
        }
        for(Train t:trains[0]){
            int stop2=t.getStop(endStation);
            if(stop2==0){
                stop2=diaFile.station.get(endStation).stopMain[0];
            }
            if(stop==stop2){
                if(t.getDATime(endStation)>endTime&&t.getDATime(endStation)<bestTime){
                    bestTrain=t;
                    bestTime=t.getDATime(endStation);
                }
            }
        }
        for(Train t:trains[1]){
            int stop2=t.getStop(endStation);
            if(stop2==0){
                stop2=diaFile.station.get(endStation).stopMain[1];
            }
            if(stop==stop2){
                if(t.getDATime(endStation)>endTime&&t.getDATime(endStation)<bestTime){
                    bestTrain=t;
                    bestTime=t.getDATime(endStation);
                }
            }
        }
        if(bestTrain==null){
            return null;
        }
        if(bestTrain.startStation()==endStation&&!bestTrain.leaveYard){
            return bestTrain;
        }
        return null;

    }
    public Train beforeOperation(Train train){
        int startStation=train.startStation();
        if(startStation<0)return null;
        int startTime=train.getDATime(startStation);
        if(startTime<0)return null;
        Train bestTrain=null;
        int bestTime=0;
        int stop=train.getStop(startStation);
        if(stop==0){
            stop=diaFile.station.get(startStation).stopMain[train.direction];
        }
        for(Train t:trains[0]){
            int stop2=t.getStop(startStation);
            if(stop2==0){
                stop2=diaFile.station.get(startStation).stopMain[0];
            }

            if(stop==stop2){
                if(t.getADTime(startStation)<startTime&&t.getADTime(startStation)>bestTime){
                    bestTrain=t;
                    bestTime=t.getADTime(startStation);
                }
            }
        }
        for(Train t:trains[1]){
            int stop2=t.getStop(startStation);
            if(stop2==0){
                stop2=diaFile.station.get(startStation).stopMain[1];
            }

            if(stop==stop2){
                if(t.getADTime(startStation)<startTime&&t.getADTime(startStation)>bestTime){
                    bestTrain=t;
                    bestTime=t.getADTime(startStation);

                }
            }
        }
        if(bestTrain==null){
            return null;
        }
        if(bestTrain.endStation()==startStation&&!bestTrain.goYard){
            return bestTrain;
        }
        return null;

    }

    public void reNewOperation(){
        for(Train t:trains[0]) {
            if(!t.leaveYard){
                t.operationName="";
            }
        }
        for(Train t:trains[1]) {
            if(!t.leaveYard){
                t.operationName="";
            }
        }
        for(Train t:trains[0]) {
            if(t.leaveYard){
                Train train=t;
                String operationName=train.operationName;
                while(train!=null){
                    train.operationName=operationName;
                    train=nextOperation(train);
                }
            }
        }
        for(Train t:trains[1]) {
            if(t.leaveYard){
                Train train=t;
                String operationName=train.operationName;
                while(train!=null){
                    train.operationName=operationName;
                    train=nextOperation(train);
                }
            }
        }

    }
    public void saveToFile(PrintWriter out) throws Exception {
        out.println("Dia.");
        out.println("DiaName="+name);
        out.println("MainBackColorIndex"+mainBackColorIndex);
        out.println("SubBackColorIndex"+subBackColorIndex);
        out.println("BackPatternIndex"+timeTableBackPatternIndex);
        out.println("Kudari.");
        for(Train t:trains[0]){
            t.saveToFile(out);
        }
        out.println(".");
        out.println("Nobori.");
        for(Train t:trains[1]){
            t.saveToFile(out);
        }
        out.println(".");
        out.println(".");
    }
    public void saveToOuDiaFile(PrintWriter out) throws Exception {
        out.println("Dia.");
        out.println("DiaName="+name);
        out.write("Kudari.");
        for(Train t:trains[0]){
            t.saveToFile(out);
        }
        out.write(".");
        out.write("Nobori.");
        for(Train t:trains[1]){
            t.saveToFile(out);
        }
        out.write(".");
        out.write(".");
    }
    @Override
    public Diagram clone() throws CloneNotSupportedException {
        Diagram result=(Diagram) super.clone();
        result.trains[0]=new ArrayList<>();
        for(Train train :trains[0]){
            result.trains[0].add((Train)train.clone());
        }
        result.trains[1]=new ArrayList<>();
        for(Train train :trains[1]){
            result.trains[1].add((Train)train.clone());
        }
        return result;
    }

}
