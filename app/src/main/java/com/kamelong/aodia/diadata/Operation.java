package com.kamelong.aodia.diadata;

import com.kamelong.JPTIOuDia.OuDia.OuDiaTrain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * 運用を記述するクラス
 */

public class Operation extends com.kamelong.JPTIOuDia.JPTI.Operation {
    protected ArrayList<AOdiaTrain>trainList=new ArrayList<>();
    public Operation(){
        super();
    }
    public Operation(com.kamelong.JPTIOuDia.JPTI.Operation ope,Map<Integer,AOdiaTrain> trainMap){
        super();
        operationName=ope.getName();
        operationNumber=ope.getNumber();
        for(int i=0;i<ope.getTripNum();i++){
            trainList.add(trainMap.get(ope.getBlockID(i)));
            trainMap.get(ope.getBlockID(i)).setOperation(this);
        }
        System.out.println();
    }

    /**
     * 運用に列車を追加する
     */
    public void addTrain(AOdiaTrain train){
        trainList.add(train);
        train.setOperation(this);
    }
    /**
     * 運用に列車を追加する
     */
    public void addTrain(int i,AOdiaTrain train){
        trainList.add(i,train);
        train.setOperation(this);

    }
    public void removeTrain(AOdiaTrain train){
        trainList.remove(train);
        train.setOperation(null);

    }


    /**
     * trainのリストサイズ
     */
    public int getTrainNum(){
        return trainList.size();
    }
    /**
     * train取得
     */
    public AOdiaTrain getTrain(int index){
        return trainList.get(index);
    }
    public ArrayList<AOdiaTrain> getTrain(){
        return trainList;
    }
    /**
     * 運用名変更
     */
    public void setName(String name){
        this.operationName=name;
    }
    /**
     * 運用名
     */
    public String getName(){
        return operationName;
    }
    /**
     * 運用番号変更
     */
    public void setNumber(int number){
        this.operationNumber=number;
    }

    /**
     * 運用番号
     */
    public int getNumber(){
        return operationNumber;
    }

    public void move2JPTI(int diaNum,Map<OuDiaTrain,Integer> map){
        this.routeID=new ArrayList<>();
        this.calenderID=diaNum;
        for(int i=0;i<trainList.size();i++){
            this.routeID.add(0);
            this.tripID.add(map.get(trainList.get(i)));
        }
    }
    public AOdiaTrain getNext(AOdiaTrain train){
        for(int i=0;i<trainList.size()-1;i++){
            if(trainList.get(i)==train){
                return trainList.get(i+1);
            }
        }
        return null;
    }

}
