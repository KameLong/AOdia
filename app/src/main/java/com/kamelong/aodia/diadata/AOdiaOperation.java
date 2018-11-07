package com.kamelong.aodia.diadata;

import com.kamelong.JPTI.Operation;
import com.kamelong.JPTI.Trip;
import com.kamelong.aodia.SdLog;

import java.util.ArrayList;

/**
 * Created by kame on 2017/10/30.
 */

public class AOdiaOperation {
    private AOdiaDiaFile diaFile;
    private int diaNum=0;
    public ArrayList<AOdiaTrain> trains=new ArrayList<>();
    public String name="";
    public int number=-1;
    public AOdiaOperation(AOdiaDiaFile diaFile,int diaNum){
        this.diaFile=diaFile;
        this.diaNum=diaNum;
    }
    public AOdiaOperation(AOdiaDiaFile diaFile,Operation ope){
        this.diaFile=diaFile;
        diaNum=ope.getCalenderID();
        name=ope.getName();
        number=ope.getNumber();
        AOdiaTrain train=null;
        for(Trip trip:ope.getTrip()){
            if(train!=null&&train.isUsed()&&train.containTrip(trip)){
                //no action
            }else{
                if(diaFile.getTimeTable(diaNum,0).getTrainByTrip(trip)!=null){
                   train=diaFile.getTimeTable(diaNum,0).getTrainByTrip(trip);
                    trains.add(train);
                    train.addOperation(this);
                    continue;
                }
                if(diaFile.getTimeTable(diaNum,1).getTrainByTrip(trip)!=null){
                    train=diaFile.getTimeTable(diaNum,1).getTrainByTrip(trip);
                    trains.add(train);
                    train.addOperation(this);
                    continue;
                }
                if(train==null||train.isused){
                    train=new AOdiaTrain();
                    train.addTrip(trip);
                    trains.add(train);
                    train.addOperation(this);
                }else{
                    train.addTrip(trip);
                }
            }
        }

    }
    public int getTrainNum(){
        return trains.size();
    }
    public AOdiaTrain getTrain(int index){
        return trains.get(index);
    }
    public void setName (String value){
        name=value;
    }
    public String getName(){
        return name;
    }
    public void setNumber(int value){
        number=value;
    }
    public int getNumber(){
        return number;
    }
    public void addTrain(AOdiaTrain train) {
        if(train.getOperation()==null) {
            trains.add(train);
            train.addOperation(this);
        }else{
            SdLog.toast("この列車にはすでに運用が登録されています");
        }
    }
    public void addTrain(int index,AOdiaTrain train){
        if(train.getOperation()==null) {
            trains.add(index,train);
            train.addOperation(this);
        }else{
            SdLog.toast("この列車にはすでに運用が登録されています");
        }

    }
    public void removeTrain(AOdiaTrain train){
        trains.remove(train);
        train.removeOperation();
    }
    public ArrayList<AOdiaTrain>getTrain(){
        ArrayList<AOdiaTrain> result=new ArrayList<>();
        for(AOdiaTrain train:trains){
            if(train.isused){
                result.add(train);
            }
        }
        return result;
    }
    public AOdiaTrain getNext(AOdiaTrain train){
        int index=trains.indexOf(train);
        if(index+1<trains.size()){
            return trains.get(index+1);
        }
        return null;
    }
    public Operation getJPTIoperation(){
        Operation result=new Operation(diaFile.getJPTI());
        result.setName(name);
        result.setNumber(number);
        result.setCalenderID(diaNum);
        for(AOdiaTrain train:trains){
            for(Trip trip:train.getTrips()){
                result.addTrip(trip);
            }
        }
        return result;
    }
    public void removeAllTrain(){
        while(trains.size()!=0){
            removeTrain(trains.get(0));
        }
    }


}
