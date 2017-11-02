package com.kamelong.aodia.diadata;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Service;
import com.kamelong.JPTI.Train;
import com.kamelong.JPTI.Trip;
import com.kamelong.OuDia.OuDiaFile;

import java.util.ArrayList;

/**
 * 時刻表内に存在する列車の情報を格納する
 */

public class AOdiaTimeTable {
    private AOdiaDiaFile diaFile;
    private Service service=null;
    private JPTI jpti=null;

    private int diaNum=0;
    private int direct=0;
    ArrayList<AOdiaTrain>trainList=new ArrayList<>();

    public AOdiaTimeTable(AOdiaDiaFile diaFile,int diaNum,int direct){
        this.diaFile=diaFile;
        this.diaNum=diaNum;
        this.direct=direct;
        this.service=diaFile.getService();
        this.jpti=diaFile.getJPTI();
        ArrayList<Train>servicetrain=service.getTrainList();
        for(Train train:servicetrain){
            if(train.getCalendarID()==diaNum&&train.getDirect()==direct){
                trainList.add(new AOdiaTrain(diaFile,train));
            }
        }
    }
    public int getTrainNum(){
        return trainList.size();
    }
    public AOdiaTrain getTrain(int index){
        return trainList.get(index);
    }

    public void sortTrain(int stationIndex){
        ArrayList<AOdiaTrain>newList=new ArrayList<>();
        trainLoop:
        for(int a=0;a<trainList.size();a++){
            int baseTime=trainList.get(a).getPredictionTime(diaFile.getStation().getStation(stationIndex));
            if(baseTime>=0){
                for(int i=0;i<newList.size();i++){
                    if (baseTime < newList.get(i).getPredictionTime(stationIndex)) {
                        newList.add(i, trainList.get(a));
                        trainList.remove(a);
                        a--;
                        continue trainLoop;
                    }


                }
                newList.add(trainList.get(a));
                trainList.remove(a);
                a--;
            }
        }
        baseStation:
        for(int index=stationIndex+1;index<diaFile.getStation().getStationNum();index++){
            trainLoop:
            for(int a=0;a<trainList.size();a++){
                int baseTime=trainList.get(a).getPredictionTime(diaFile.getStation().getStation(index));
                if(baseTime>=0){
                    for(int i=0;i<newList.size();i++){
                        if(direct==0){
                        if(baseTime>newList.get(newList.size()-i-1).getPredictionTime(diaFile.getStation().getStation(index))){
                            if(newList.get(newList.size()-i-1).getPredictionTime(diaFile.getStation().getStation(index))<0){
                                continue;
                            }
                            newList.add(newList.size()-i,trainList.get(a));
                            trainList.remove(a);
                            a--;
                            continue trainLoop;
                        }}else{
                            if(newList.get(i).getPredictionTime(diaFile.getStation().getStation(index))<0){
                                continue;
                            }
                            if(baseTime<newList.get(i).getPredictionTime(diaFile.getStation().getStation(index))){
                                newList.add(i,trainList.get(a));
                                trainList.remove(a);
                                a--;
                                continue trainLoop;
                            }
                        }
                    }
                    if(direct==0){
                        newList.add(0,trainList.get(a));
                        trainList.remove(a);
                        a--;

                    }else {
                        newList.add(trainList.get(a));
                        trainList.remove(a);
                        a--;
                    }


                }
            }
        }
        baseStation:
        for(int index=stationIndex-1;index>=0;index--){
            trainLoop:
            for(int a=0;a<trainList.size();a++){
                int baseTime=trainList.get(a).getPredictionTime(diaFile.getStation().getStation(index));
                if(baseTime>=0){
                    for(int i=0;i<newList.size();i++){
                        if(direct==0){
                            if(newList.get(i).getPredictionTime(diaFile.getStation().getStation(index))<0){
                                continue;
                            }

                            if(baseTime<newList.get(i).getPredictionTime(diaFile.getStation().getStation(index))){
                                newList.add(i,trainList.get(a));
                                trainList.remove(a);
                                a--;
                                continue trainLoop;
                            }

                        }else{
                            if(newList.get(newList.size()-i-1).getPredictionTime(diaFile.getStation().getStation(index))<0){
                                continue;
                            }
                            if(baseTime>newList.get(newList.size()-i-1).getPredictionTime(diaFile.getStation().getStation(index))){
                                newList.add(newList.size()-i,trainList.get(a));
                                trainList.remove(a);
                                a--;
                                continue trainLoop;
                            }
                        }
                    }
                    if(direct==0){
                        newList.add(trainList.get(a));
                        trainList.remove(a);
                        a--;
                    }else {
                        newList.add(0,trainList.get(a));
                        trainList.remove(a);
                        a--;

                    }


                }
            }
        }

        for(AOdiaTrain train:trainList){
            newList.add(train);
        }
        trainList=newList;
    }
    public AOdiaTrain getTrainByTrip(Trip trip){
        for(AOdiaTrain train:trainList){
            if(train.containTrip(trip)){
                return train;
            }
        }
        return null;
    }
}
