package com.kamelong.JPTI;



import com.eclipsesource.json.JsonObject;
import com.kamelong.OuDia.OuDiaTrain;

import org.json.JSONException;
import org.json.JSONObject;

public class Time{
    //所属データ
    private JPTI jpti=null;
    private Trip trip=null;
    private Stop stop=null;
    //基本データ
    public int pickupType=1;
    public int dropoffType=1;
    private int arrivalTime=-1;
    public String getSarrivalTime(){
        return timeInt2String(arrivalTime);
    }
    private int departureTime=-1;
    public String getSdepartureTime(){
        return timeInt2String(departureTime);
    }

    private static final String STOP_ID="stop_id";
    private static final String PICKUP="pickup_type";
    private static final String DROPOFF="dropoff_type";
    protected static final String ARRIVAL_DAYS="arrival_days";
    private static final String ARRIVAL_TIME="arrivel_time";
    protected static final String DEPARTURE_DAYS="depature_days";
    private static final String DEPARTURE_TIME ="departure_time";
/*
    private static final String STOP_ID="s";
    private static final String PICKUP="p";
    private static final String DROPOFF="o";
    protected static final String ARRIVAL_DAYS="arrival_days";
    private static final String ARRIVAL_TIME="a";
    protected static final String DEPARTURE_DAYS="depature_days";
    private static final String DEPARTURE_TIME ="d";
    */
protected  Time(){}
    public Time(JPTI jpti, Trip trip){
        this.trip=trip;
        this.jpti=jpti;
    }

    public Time(JPTI jpti, Trip trip, Stop stop){
        this.trip=trip;
        this.jpti=jpti;
        this.stop=stop;
    }
    public Time(JPTI jpti, Trip trip, JsonObject json){
        this.trip=trip;
        this.jpti=jpti;
        try {
                stop= jpti.stopList.get(json.getInt(STOP_ID,0));
            pickupType = json.getInt(PICKUP,1);
            dropoffType = json.getInt(DROPOFF,1);
            arrivalTime = timeString2Int(json.getString(ARRIVAL_TIME,null));
            departureTime = timeString2Int(json.getString(DEPARTURE_TIME,null));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Time(JPTI jpti, Trip trip, Stop stop, OuDiaTrain train, int station){
        this(jpti, trip, stop);
        if(train.getStopType(station)== com.kamelong.OuDia.OuDiaTrain.STOP_TYPE_STOP){
            pickupType=0;
            dropoffType=0;
        }else{
            pickupType=1;
            dropoffType=1;
        }
        if(train.arriveExist(station)){
            arrivalTime=train.getArriveTime(station);
        }
        if(train.departExist(station)){
            departureTime=train.getDepartureTime(station);
        }


    }

    public JsonObject makeJSONObject(){
        JsonObject json=new JsonObject();
        try {
            json.add(STOP_ID,jpti.indexOf(stop));
            json.add(PICKUP,pickupType);
            json.add(DROPOFF,dropoffType);
            if(arrivalTime!=-1){
                json.add(ARRIVAL_TIME,timeInt2String(arrivalTime));
            }
            if(departureTime!=-1){
                json.add(DEPARTURE_TIME,timeInt2String(departureTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;

    }
    private static int timeString2Int(String time){
        if(time==null||time.length()==0){
            return -1;
        }
        int hh=Integer.parseInt(time.split(":",-1)[0]);
        int mm=Integer.parseInt(time.split(":",-1)[1]);
        int ss=0;
        if(time.split(":",-1).length>2){
        ss=Integer.parseInt(time.split(":",-1)[2]);
        }
        hh=hh%24;
        if(hh<3){
            hh+=24;
        }
        return hh*3600+mm*60+ss;
    }
    private static String timeInt2String(int time){
        int ss=time%60;
        time=time/60;
        int mm=time%60;
        time=time/60;
        int hh=time%24;
        return String.format("%02d",hh)+":"+String.format("%02d",mm)+":"+String.format("%02d",ss);

    }

    /**
     * とりあえず列車時刻が存在するのならそれを返す
     * もし列車時刻が存在しなかればreturn -1;
     * @return
     */
    public int getTime(){
        if(departureTime!=-1){
            return departureTime;
        }
        return arrivalTime;
    }
    public Station getStation(){
        return stop.getStation();
    }
    public boolean isStop(){
        return pickupType==0||dropoffType==0;
    }
    public int getArrivalTime(){
        return arrivalTime;
    }
    public int getDepartureTime(){
        return departureTime;
    }
    public int getADTime(){
        if(arrivalTime<0){
            return departureTime;
        }
        return arrivalTime;
    }
    public int getDATime(){
        if(departureTime<0){
            return arrivalTime;
        }
        return departureTime;
    }
    public void setArrivalTime(String value){
        arrivalTime=timeString2Int(value);
    }
    public void setDepartureTime(String value){
        departureTime=timeString2Int(value);
    }
    public void setStop(boolean frag){
        if(frag){
            dropoffType=0;
            pickupType=0;
        }else{
            dropoffType=1;
            pickupType=1;
        }
    }


}
