package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.*;
import com.kamelong.JPTI.Trip;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrain;
import org.json.JSONObject;


/**
 * Created by kame on 2017/09/04.
 */
public class Time extends com.kamelong.JPTI.Time {
    public Time(JPTIdata jpti, com.kamelong.JPTI.Trip trip) {
        super(jpti, trip);
    }

    public Time(JPTIdata jpti, Trip trip, JSONObject json) {
        super(jpti, trip, json);
    }
    public Time(JPTI jpti, Trip trip, OuDiaTrain train, OuDiaFile oudia, int stationIndex){
        this(jpti,trip);
        for(int i=0;i<jpti.stationList.size();i++){
         if(jpti.getStation(i).getName().equals(oudia.getStation(stationIndex).getName())){
                station=this.jpti.stationList.get(i);
                break;
            }
        }
        stop=getStation().getStop(getStation().findOuDiaStation());
        if(train.getStopType(stationIndex)==1){
            pickupType=0;
            dropoffType=0;
        }else{
            pickupType=1;
            dropoffType=1;
        }
        if(train.arriveExist(stationIndex)){
            arrivalTime=train.getArriveTime(stationIndex);
        }
        if(train.departExist(stationIndex)){
            departureTime=train.getArriveTime(stationIndex);
        }
    }
    public Station getStation(){
        return (Station)station;
    }
    public int getArrivelTime(){
        return arrivalTime;
    }
    public int getDepartureTime(){
        return departureTime;
    }
    public boolean isStop(){
        return pickupType==0||dropoffType==0;
    }


}
