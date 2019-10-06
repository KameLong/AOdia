package com.kamelong.OuDia;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 時刻表並び替え作業を行う作業クラスです。
 * 時刻表並び替えが終了したらこのオブジェクトを破棄してください。
 */
public class TimeTableSorter {
    LineFile lineFile;
    Train[] trainList;
    int direction;
    private ArrayList<Integer> sortBefore;
    private ArrayList<Integer> sortAfter;
    boolean[] sorted;

    /**
     * ソートする時刻表を入力します。
     */
    public TimeTableSorter(LineFile lineFile,Train[] trains,int direction){
        this.lineFile=lineFile;
        this.trainList =trains;
        this.direction=direction;
        this.sortBefore =new ArrayList<>();
        this.sortAfter =new ArrayList<>();
        for(int i=0;i<trains.length;i++){
            sortBefore.add(i);
        }
        sorted=new boolean[lineFile.getStationNum()];
        for(int i=0;i<sorted.length;i++){
            sorted[i]=false;
        }

    }

    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     */
    public ArrayList<Train>sortNumber(){
        ArrayList<TrainNumberSorter>sorter=new ArrayList<>();
        for(Train train:trainList){
            sorter.add(new TrainNumberSorter(train));
        }
        Collections.sort(sorter);
        ArrayList<Train>result=new ArrayList<>();
        for(TrainNumberSorter sort:sorter){
            result.add(sort.train);
        }
        return result;
    }
    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    public ArrayList<Train>sortType(){
        ArrayList<TrainTypeSorter>sorter=new ArrayList<>();
        for(Train train:trainList){
            sorter.add(new TrainTypeSorter(train));
        }
        Collections.sort(sorter);
        ArrayList<Train>result=new ArrayList<>();
        for(TrainTypeSorter sort:sorter){
            result.add(sort.train);
        }
        return result;
    }
    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    public ArrayList<Train>sortName(){
        ArrayList<TrainNameSorter>sorter=new ArrayList<>();
        for(Train train:trainList){
            sorter.add(new TrainNameSorter(train));
        }
        Collections.sort(sorter);
        ArrayList<Train>result=new ArrayList<>();
        for(TrainNameSorter sort:sorter){
            result.add(sort.train);
        }
        return result;
    }
    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    public ArrayList<Train>sortRemark(){
        ArrayList<TrainRemarkSorter>sorter=new ArrayList<>();
        for(Train train:trainList){
            sorter.add(new TrainRemarkSorter(train));
        }
        Collections.sort(sorter);
        ArrayList<Train>result=new ArrayList<>();
        for(TrainRemarkSorter sort:sorter){
            result.add(sort.train);
        }
        return result;
    }

    /**
     * 列車時刻でソートします。
     * @param stationIndex ソート基準時刻
     */
    public ArrayList<Train> sort(int stationIndex){
        for (int i = 0; i < sortBefore.size(); i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationIndex) > 0 && !trainList[sortBefore.get(i)].checkDoubleDay()) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime = trainList[sortBefore.get(i)].getPredictionTime(stationIndex);
                int j;
                for (j = sortAfter.size(); j > 0; j--) {
                    if (trainList[sortAfter.get(j - 1)].getPredictionTime(stationIndex) < baseTime) {
                        break;
                    }
                }
                sortAfter.add(j, sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }
        sorted[stationIndex]=true;
        if(direction==Train.DOWN){
            sortDown(stationIndex);
        }else{
            sortUp(stationIndex);
        }
        sortAfter.addAll(sortBefore);
        ArrayList<Train>result=new ArrayList<>();
        for(int i:sortAfter){
            result.add(trainList[i]);
        }
        return result;
    }

    /**
     * 路線内を上方向に探索していきます。
     */
    public void sortUp(int stationIndex){

        boolean skip=true;//ソート済みの路線から外れ、別の路線に入る場合skipfragがtrueになる。//ソート済み領域に戻ればskip=false
        search:
        for(;stationIndex>=0;stationIndex--){
            if(stationIndex==56){
                System.out.println(55);
            }
            //上方向に探索
            if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0){
                //この駅が分岐駅設定されている場合
                if(lineFile.getStation(stationIndex).brunchCoreStationIndex>stationIndex){
                    //下から分岐する場合はskipfragが入る
                    skip=true;
                }
            }
            if(sorted[stationIndex]){
                //skipを戻す
                //上から分岐する場合はこの駅でソートを試み、無理ならskipfragが入る
                skip= lineFile.getStation(stationIndex).brunchCoreStationIndex >= 0 && lineFile.getStation(stationIndex).brunchCoreStationIndex < stationIndex;
            }else{
                if(skip){
                    //skipされている場合でもbrachでの同一駅がソート済みならその駅を用いてソートする。
                    if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0&&sorted[lineFile.getStation(stationIndex).brunchCoreStationIndex]){
                        skip=false;
                        break;
                    }
                    for(int j=0;j<lineFile.getStationNum();j++){
                        if(sorted[j]&&lineFile.getStation(j).brunchCoreStationIndex==stationIndex){
                            skip=false;
                            break search;
                        }
                    }

                }else {
                    //skipされていないので普通のソートする
                    skip=false;
                    break;
                }
            }
        }
        if(stationIndex<0){
            stationIndex=0;

            //ループが終わったのに、既にソート済みの駅が最後に残った場合や最後スキップされていた場合
            boolean frag1=false;
            for(int i=0;i<lineFile.getStationNum();i++){
                if(!sorted[i]){
                    sortDown(0);
                }
            }
            return;
        }
        System.out.println("sortUp:\t"+stationIndex+"\t("+lineFile.getStation(stationIndex).name+")");

        ArrayList<Integer>stations=new ArrayList<>();
        stations.add(stationIndex);
        if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0){
            stations.add(lineFile.getStation(stationIndex).brunchCoreStationIndex);
        }
        for(int i=0;i<lineFile.getStationNum();i++){
            if(lineFile.getStation(i).brunchCoreStationIndex==stationIndex){
                stations.add(i);
            }
        }
        if(direction==Train.DOWN){
            addTrainInSort1(stations);
        }else{
            addTrainInSort2(stations);
        }
        sorted[stationIndex]=true;
        //上方向に探索
        if(stationIndex!=0){
            sortUp(stationIndex);
        }else{
            sortDown(0);
        }
    }
    /**
     * 路線内を下方向に探索していきます。
     */
    public void sortDown(int stationIndex){

        boolean skip=true;//ソート済みの路線から外れ、別の路線に入る場合skipfragがtrueになる。//ソート済み領域に戻ればskip=false
        search:
        for(;stationIndex<lineFile.getStationNum();stationIndex++){
            //上方向に探索
            if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0){
                //この駅が分岐駅設定されている場合
                if(lineFile.getStation(stationIndex).brunchCoreStationIndex<stationIndex){
                    //上から分岐する場合はskipfragが入る
                    skip=true;
                }
            }
            if(sorted[stationIndex]){
                //skipを戻す
                //上から分岐する場合はこの駅でソートを試み、無理ならskipfragが入る
                skip= lineFile.getStation(stationIndex).brunchCoreStationIndex > stationIndex;
            }else{
                if(skip){
                    //skipされている場合でもbrachでの同一駅がソート済みならその駅を用いてソートする。
                    if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0&&sorted[lineFile.getStation(stationIndex).brunchCoreStationIndex]){
                        skip=false;
                        break;
                    }
                    for(int j=0;j<lineFile.getStationNum();j++){
                        if(sorted[j]&&lineFile.getStation(j).brunchCoreStationIndex==stationIndex){
                            skip=false;
                            break search;
                        }
                    }

                }else {
                    //skipされていないので普通のソートする
                    skip=false;
                    break;
                }
            }
        }
        if(stationIndex==lineFile.getStationNum()){
            stationIndex--;

            //ループが終わったのに、既にソート済みの駅が最後に残った場合や最後スキップされていた場合
            boolean frag1=false;
            for(int i=0;i<lineFile.getStationNum();i++){
                if(!sorted[i]){
                    sortUp(lineFile.getStationNum()-1);
                }
            }
            return;
        }
        System.out.println("sortDown:\t"+stationIndex+"\t("+lineFile.getStation(stationIndex).name+")");
        ArrayList<Integer>stations=new ArrayList<>();
        stations.add(stationIndex);
        if(lineFile.getStation(stationIndex).brunchCoreStationIndex>=0){
            stations.add(lineFile.getStation(stationIndex).brunchCoreStationIndex);
        }
        for(int i=0;i<lineFile.getStationNum();i++){
            if(lineFile.getStation(i).brunchCoreStationIndex==stationIndex){
                stations.add(i);
            }
        }
        if(direction==Train.DOWN){
            addTrainInSort2(stations);
        }else{
            addTrainInSort1(stations);
        }
        sorted[stationIndex]=true;
        //上方向に探索
        if(stationIndex!=lineFile.getStationNum()-1){
            sortDown(stationIndex);
        }else{
            sortUp(lineFile.getStationNum()-1);
        }
    }    /**
     * 列車をsortAfterに時刻前方から挿入する
     * station[0]に停車する列車がソート対象
     * station[1以上]は同一駅
     */
    private void addTrainInSort1(ArrayList<Integer> station) {
        for (int i = sortBefore.size(); i > 0; i--) {
            int baseTime = trainList[sortBefore.get(i - 1)].getTime(station.get(0),Train.ARRIVE,true);
            if (baseTime < 0 || trainList[sortBefore.get(i - 1)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = 0; j < sortAfter.size(); j++) {

                int sortTime=-1;
                for(int s:station){
                    sortTime=Math.max(sortTime,trainList[sortAfter.get(j)].getPredictionTime(s,Train.DEPART));
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
                sortBefore.remove(i - 1);
            }
        }
    }

    private void addTrainInSort2(ArrayList<Integer> station) {
        for (int i = 0; i < sortBefore.size(); i++) {
            int baseTime = trainList[sortBefore.get(i)].getDepTime(station.get(0));
            if (baseTime < 0 || trainList[sortBefore.get(i)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = sortAfter.size()-1; j >= 0; j--) {

                int sortTime=-1;
                for(int s:station){
                        sortTime=Math.max(sortTime,trainList[sortAfter.get(j)].getPredictionTime(s,Train.ARRIVE));
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
                sortAfter.add(j+1, sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }

        }

    }
    class TrainNumberSorter implements Comparable<TrainNumberSorter>{
        Train train;
        String name="";
        int number=0;
        public TrainNumberSorter(Train train){
            this.train=train;
            String value=train.number;
            for(int i=0;i<value.length();i++){
                if(!isNumber(value.toCharArray()[i])){
                    if(i!=0) {
                        number = Integer.parseInt(value.substring(0, i));
                    }
                    name=value.substring(i);
                    return;
                }
            }
            if(value.length()!=0){
                number=Integer.parseInt(value);

            }
        }

        boolean isNumber(char c){
            return c >= '0' && c <= '9';
        }

        @Override
        public int compareTo(TrainNumberSorter o) {
            if(this.name.equals(o.name)){
                return this.number-o.number;
            }
            return this.name.compareTo(o.name);

        }
    }
    class TrainTypeSorter implements Comparable<TrainTypeSorter>{
        Train train;
        public TrainTypeSorter(Train train){
            this.train=train;
        }
        @Override
        public int compareTo(TrainTypeSorter o) {
                return this.train.type-o.train.type;
        }
    }
    class TrainNameSorter implements Comparable<TrainNameSorter>{
        Train train;
        public TrainNameSorter(Train train){
            this.train=train;
        }
        @Override
        public int compareTo(TrainNameSorter o) {
            if(this.train.name.equals(o.train.name)){
                return this.train.count.compareTo(o.train.count);
            }
            return this.train.name.compareTo(o.train.name);
        }
    }
    class TrainRemarkSorter implements Comparable<TrainRemarkSorter>{
        Train train;
        public TrainRemarkSorter(Train train){
            this.train=train;
        }
        @Override
        public int compareTo(TrainRemarkSorter o) {
            return this.train.remark.compareTo(o.train.remark);
        }
    }

}
