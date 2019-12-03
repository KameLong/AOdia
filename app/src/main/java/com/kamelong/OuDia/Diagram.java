package com.kamelong.OuDia;

import com.kamelong.tool.SDlog;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * １つのダイヤを表します。
 * ダイヤは１つの路線に複数作る事ができますが、同一路線中のダイヤは全て同じ駅順を持ちます。
 */
public class Diagram implements Cloneable{
    /**
     * 親LineFile
     * このオブジェクトを生成する際には必ずlineFileを設定する必要があります。
     * 別のLineFileにこのオブジェクトをコピーする際には、lineFileを書き換えてください。
     */
    public LineFile lineFile;
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


    /**
     * 推奨コンストラクタ
     * @param diaFile 親ダイヤファイル
     */
    public Diagram(LineFile diaFile){
        this.lineFile =diaFile;
        name="新しいダイヤ";
        trains[0]=new ArrayList<>();
        trains[1]=new ArrayList<>();
    }
    /**
     * OuDia形式で、データを読み込みます。
     */
    protected void setValue(String title,String value){
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
     * OuDia2nd形式で出力します
     */
    void saveToFile(PrintWriter out) {
        out.println("Dia.");
        out.println("DiaName="+name);
        out.println("MainBackColorIndex="+mainBackColorIndex);
        out.println("SubBackColorIndex="+subBackColorIndex);
        out.println("BackPatternIndex="+timeTableBackPatternIndex);
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
    /**
     * OuDia形式で出力します
     */

    void saveToOuDiaFile(PrintWriter out){
        out.println("Dia.");
        out.println("DiaName="+name);
        out.println("Kudari.");
        for(Train t:trains[0]){
            t.saveToOuDiaFile(out);
        }
        out.println(".");
        out.println("Nobori.");
        for(Train t:trains[1]){
            t.saveToOuDiaFile(out);
        }
        out.println(".");
        out.println(".");
    }
    /**
     * Diagramをコピーします。
     * lineFile:コピー先のDiagramが所属するLineFile
     */

    public Diagram clone(LineFile lineFile){
        try {
            Diagram result = (Diagram) super.clone();
            result.trains=new ArrayList[2];
            result.trains[0] = new ArrayList<>();
            for (Train train : trains[0]) {
                result.trains[0].add(train.clone(lineFile));

            }
            result.trains[1] = new ArrayList<>();
            for (Train train : trains[1]) {
                result.trains[1].add(train.clone(lineFile));
            }
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Diagram(lineFile);
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */


    /**
     * 指定方向の列車数を返します
     */
    public int getTrainNum(int direction) {
        try {
            return trains[direction].size();
        }catch (IndexOutOfBoundsException e){
            return 0;
        }
    }


    /**
     * 列車を取得します
     * @param direction 方向(0,1)
     * @param index 列車index
     */
    public Train getTrain(int direction, int index) {
        try {
            return trains[direction].get(index);
        }catch(IndexOutOfBoundsException e){
            SDlog.log(new Exception("Diagram.getTrain("+direction+","+index+")"));
            return new Train(lineFile,0);
        }
    }

    /**
     * 指定列車のindexを返します
     */
    public int getTrainIndex(int direction, Train train) {
        return trains[direction].indexOf(train);
    }
    public int getTrainIndex( Train train) {
        return trains[train.direction].indexOf(train);
    }

    /**
     * 指定indexに列車を追加します。
     * index=-1の時は、末尾に追加されます
     * 列車を追加する際は、その列車が持つStationTimeの数とlineFileの駅数が一致する必要があります
     * 一致しない場合はfalseを返し、列車の追加を行いません。
     * 追加に成功するとtrueを返します。
     *
     */
    public boolean addTrain(int direction,int index,Train train){
        if(train.getStationNum()!=lineFile.getStationNum()){
            SDlog.log("列車の駅数とダイヤの駅数が合いません");
            return false;
        }

        if(index>=0&&index<getTrainNum(direction)){
            trains[direction].add(index,train);
            train.lineFile=lineFile;
            if(train.direction!=direction){
                Collections.reverse(train.stationTimes);
            }
            train.direction=direction;
        }else{
            trains[direction].add(train);
        }
        return true;
    }

    /**
     * 指定列車を削除します
     * 方向とindexを指定
     */
    public void deleteTrain(int direction,int index){
        if(index>=0&&index<getTrainNum(direction)) {
            trains[direction].remove(index);
        }
    }
    /**
     * 指定列車を削除します
     */
    public void deleteTrain(Train train){
        trains[train.direction].remove(train);
    }


    /**
     * ダイヤのソートを行う前の処理です。
     * 時刻が存在しない空列車を削除してからソートを行います。
     */
    private void beforeSort(int direction){
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i).isnull()){
                trains[direction].remove(i);
                i--;
            }
        }
    }
    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param direction     並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    public void sortTrain(int direction, int stationNumber) {
        beforeSort(direction);
        Train[] trainList = trains[direction].toArray(new Train[0]);
        TimeTableSorter sorter=new TimeTableSorter(lineFile,trainList,direction);
        trains[direction]=sorter.sort(stationNumber);
    }

    /**
     * 列車番号ソート
     */
    public void sortNumber(int direction){
        beforeSort(direction);
        Train[] trainList = trains[direction].toArray(new Train[0]);
        TimeTableSorter sorter=new TimeTableSorter(lineFile,trainList,direction);
        trains[direction]=sorter.sortNumber();

    }

    /**
     * 種別ソート
     */
    public void sortType(int direction){
        beforeSort(direction);
        Train[] trainList = trains[direction].toArray(new Train[0]);
        TimeTableSorter sorter=new TimeTableSorter(lineFile,trainList,direction);
        trains[direction]=sorter.sortType();

    }
    /**
     * 列車名ソート
     */
    public void sortName(int direction){
        beforeSort(direction);
        Train[] trainList = trains[direction].toArray(new Train[0]);
        TimeTableSorter sorter=new TimeTableSorter(lineFile,trainList,direction);
        trains[direction]=sorter.sortName();

    }
    /**
     * 備考ソート
     */
    public void sortRemark(int direction){
        beforeSort(direction);
        Train[] trainList = trains[direction].toArray(new Train[0]);
        TimeTableSorter sorter=new TimeTableSorter(lineFile,trainList,direction);
        trains[direction]=sorter.sortRemark();
    }
    /**
     * 列車番号が同一のものを一本化します。
     * ２本の列車を総当たりで調べていき、列車番号、列車種別、終着駅と相手の始発駅が同じになる組み合わせが見つかれば一本化します。
     */
    public void combineByTrainNumber(int direction){
        for(int i=0;i<trains[direction].size();i++){
            Train train1=trains[direction].get(i);
            if(train1.isnull()){
                continue;
            }
            int endStation=train1.getEndStation();
            for(int j=0;j<trains[direction].size();j++){
                Train train2=trains[direction].get(j);
                if(train1.number.equals(train2.number)&&train1.type==train2.type&&i!=j&&train2.getStartStation()==endStation){
                        train1.conbine(train2);
                        trains[direction].remove(train2);
                        if(j>i){
                            i=i-1;
                        }else{
                            i=i-2;
                        }
                        break;
                }
            }
        }
    }
}
