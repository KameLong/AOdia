package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrain;
import org.json.JSONObject;

public class Trip extends com.kamelong.JPTI.Trip {
    public Trip(JPTIdata jpti, com.kamelong.JPTI.Route route) {
        super(jpti, route);
    }

    public Trip(JPTIdata jptiData, Route route, JSONObject json) {
        super(jptiData, route, json);
    }

    @Override
    public Time newTime(JSONObject json) {
        return new Time(jpti,this,json);
    }

    public Trip(OuDiaTrain train, int startStation, int endStation, int block, int direct, Calendar calendar, OuDiaFile oudia, JPTI jptiData,Route route){
        super(jptiData,route);
        jpti=jptiData;
        if(train.getName().length()>0){
            name=train.getName();
        }
        if(train.getNumber().length()>0){
            number=train.getNumber();
        }
        direction=direct;
        blockID=block;
        this.calender=calendar;
        this.traihType=route.getTrainType(train.getType());

        if(direct==0){
            for(int i=startStation;i<endStation+1;i++){
                if(train.getStopType(i)==1||train.getStopType(i)==2){
                    timeList.add(new Time(jptiData,this,train,oudia,i));
                }
            }
        }else{
            for(int i=endStation;i>startStation-1;i--){
                if(train.getStopType(i)==1||train.getStopType(i)==2){
                    timeList.add(new Time(jptiData,this,train,oudia,i));
                }
            }
        }
    }
    public int getBlockID(){
        return blockID;
    }
    public Calendar getCalender(){
        return (Calendar) calender;
    }
    public Time getTime(Station station){
        for(int i=0;i<timeList.size();i++){
            if(((Time)timeList.get(i)).getStation()==station){
                return (Time)timeList.get(i);
            }
        }
        return null;
    }
    public int getDirect(){
        return direction;
    }
    public TrainType getType(){
        return (TrainType) traihType;
    }
    public String getName(){
        return name;
    }
    public String getNumber(){
        return number;
    }
}
