package com.kamelong.aodia.diadataOld;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Service;
import com.kamelong.JPTI.Train;
import com.kamelong.JPTI.Trip;
import com.kamelong.aodia.diadata.AOdiaDiaFile;

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
