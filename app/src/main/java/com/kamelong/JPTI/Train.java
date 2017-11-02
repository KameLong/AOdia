package com.kamelong.JPTI;


import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.OuDia.OuDiaTrain;
import com.kamelong.aodia.SdLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kame on 2017/10/28.
 */

public class Train {
    protected JPTI jpti=null;
    protected Service service=null;
    protected Calendar calendar=null;
    protected int direct=0;
    protected String name="";
    protected String number="";
    protected String count="";
    protected ArrayList<Trip> tripList=new ArrayList<>();
    /**
     * 備考
     */
    protected String text="";


    public static final String DIRECT="direct";
    public static final String NAME="name";
    public static final String NUMBER="number";
    public static final String COUNT="count";
    public static final String TEXT="text";
    public static final String TRIP="trip";
    public static final String CALENDER="calendar_id";



    public Train(JPTI jpti, Service service, JSONObject json){
        this.jpti=jpti;
        this.service=service;
        direct=json.optInt(DIRECT,0);
        name=json.optString(NAME,"");
        number=json.optString(NUMBER,"");
        count=json.optString(COUNT,"");
        text=json.optString(TEXT,"");
        calendar=jpti.getCalendar(json.optInt(CALENDER,0));
        JSONArray tripArray=json.optJSONArray(TRIP);
        for(int i=0;i<tripArray.length();i++){
            tripList.add(jpti.getTrip(tripArray.optInt(i,0)));
        }
    }
    public Train(JPTI jpti, Service service, Calendar calendar, OuDiaFile oudiaFile, OuDiaTrain train, int blockID){
        this.jpti=jpti;
        this.service=service;
        this.calendar=calendar;
        this.direct=train.getDirect();
        name=train.getName();
        number=train.getNumber();
        text=train.getRemark();
        count=train.getCount();

        ArrayList<Integer> borderList=oudiaFile.getBorders();

        int startStation=0;
        int routeID=0;
        for(int border:borderList){
            if(border-startStation>0){
                int useStation=0;
                for(int i=startStation;i<border+1;i++){
                    if(train.getStopType(i)== com.kamelong.OuDia.OuDiaTrain.STOP_TYPE_STOP||train.getStopType(i)== com.kamelong.OuDia.OuDiaTrain.STOP_TYPE_PASS){
                        useStation++;
                    }
                }
                if(useStation>1){
                    Trip trip=jpti.addNewTrip(jpti.getRoute(routeID),calendar,oudiaFile,train,startStation,border,blockID);
                    tripList.add(trip);

                }
                startStation=border;
                if(oudiaFile.getStation(border).border()){
                    startStation++;
                }
                routeID++;

            }
        }
    }
    public void setCalendar(Calendar calendar){
        this.calendar=calendar;
    }
    public JSONObject makeJSONObject(){
        try{
            JSONObject json=new JSONObject();
            json.put(DIRECT,direct);
            if(name.length()>0){
                json.put(NAME,name);
            }
            if(number.length()>0){
                json.put(NUMBER,number);
            }
            if(count.length()>0){
                json.put(COUNT,count);
            }
            if(text.length()>0){
                json.put(TEXT,text);
            }
            if(calendar!=null){
                json.put(CALENDER,jpti.indexOf(calendar));
            }
            JSONArray tripArray=new JSONArray();
            for(Trip trip:tripList){
                tripArray.put(jpti.indexOf(trip));
            }
            json.put(TRIP,tripArray);
            return json;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public int getCalendarID(){
        return jpti.indexOf(calendar);
    }
    public int getDirect(){
        return direct;
    }
    public TrainType getTrainType(){
        try {
            return tripList.get(0).getTrainType();
        }catch(Exception e){
            SdLog.toast("エラー：列車種別取得失敗(Train-getTrainType)");
            return jpti.getTrainType(0);
        }
    }
    public String getNumber(){
        return number;
    }
    public String getName(){
        return name;
    }
    public String getCount(){
        return count;
    }
    public String getText(){
        return text;
    }
    /**
     * stationからTimeを取得する
     * @return
     */
    public Time searchTime(Station station){
        for(Trip trip:tripList){
            if(trip.searchTime(station)!=null){
                return trip.searchTime(station);
            }
        }
        return null;
    }
    public Trip searchTrip(Route route){
        for(Trip trip:tripList){
            if(trip.getRoute()==route){
                return trip;
            }
        }
        return null;
    }
    public ArrayList<Trip> getTrip(){
        return tripList;
    }


}
