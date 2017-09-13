package com.kamelong.JPTI;

import org.json.JSONException;
import org.json.JSONObject;
public abstract class Time {
    protected JPTIdata jpti;
    protected Trip trip;


    protected Station station=null;
    protected Stop stop=null;
    protected int pickupType=1;
    protected int dropoffType=1;
    protected int arrival_days=-1;
    protected int arrivalTime=-1;
    protected int departure_days=-1;
    protected int departureTime=-1;

    protected static final String STATION_ID="station_id";
    protected static final String STOP_ID="stop_id";
    protected static final String PICKUP="pickup_type";
    protected static final String DROPOFF="dropoff_type";
    protected static final String ARRIVAL_DAYS="arrival_days";
    protected static final String ARRIVAL_TIME="arrivel_time";
    protected static final String DEPARTURE_DAYS="depature_days";
    protected static final String DEPARTURE_TIME ="departure_time";


    public Time(JPTIdata jpti,Trip trip){
        this.trip=trip;
        this.jpti=jpti;
    }
    public Time(JPTIdata jpti,Trip trip,JSONObject json){
        this(jpti,trip);
        try {
            try {
                station = jpti.stationList.get(json.getInt(STATION_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                stop= station.stops.get(json.getInt(STOP_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pickupType = json.optInt(PICKUP);
            dropoffType = json.optInt(DROPOFF);
            arrival_days = json.optInt(ARRIVAL_DAYS);
            arrivalTime = timeString2Int(json.optString(ARRIVAL_TIME));
            departure_days = json.optInt(DEPARTURE_DAYS);
            departureTime = timeString2Int(json.optString(DEPARTURE_TIME));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try {
            json.put(STATION_ID, station.index());
            json.put(STOP_ID,stop.index());
            json.put(PICKUP,pickupType);
            json.put(DROPOFF,dropoffType);
            if(arrival_days>0){
                json.put(ARRIVAL_DAYS,arrival_days);
            }
            if(arrivalTime!=-1){
                json.put(ARRIVAL_TIME,timeInt2String(arrivalTime));
            }
            if(departure_days>0){
                json.put(DEPARTURE_DAYS,departure_days);
            }
            if(departureTime!=-1){
                json.put(DEPARTURE_TIME,timeInt2String(departureTime));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;

    }
    static int timeString2Int(String time){
        if(time==null||time.length()==0){
            return -1;
        }
        int hh=Integer.parseInt(time.split(":",-1)[0]);
        int mm=Integer.parseInt(time.split(":",-1)[1]);
        int ss=0;
        if(time.split(":",-1).length>2){
        ss=Integer.parseInt(time.split(":",-1)[2]);
        }
        return hh*3600+mm*60+ss;
    }
    static String timeInt2String(int time){
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


}
