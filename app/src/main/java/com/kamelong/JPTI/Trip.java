package com.kamelong.JPTI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 列車を記録するクラス
 */
public abstract class Trip {
    /**
     * 所属JPTIdata
     */
    protected JPTIdata jpti=null;
    /**
     * 所属Route
     */
    protected Route route=null;
    /**
     * 列車番号
     */
    protected String number=null;
    /**
     * 列車名、バス愛称、行先等の情報
     */
    protected  String name=null;
    /**
     * 0：Route順方向
     * 1：Route逆方向
     */
    protected int direction=0;
    /**
     * 種別id
     */
    protected TrainType traihType=null;
    /**
     * 系統id
     */
    protected int blockID=0;
    /**
     * 運行する日id
     */
    protected  Calendar calender=null;
    /**
     * 臨時運行日id
     */
    protected int extraCalendarID=-1;

    protected ArrayList<Time> timeList=new ArrayList<>();


    protected static final String NAME="trip_name";
    protected static final String NUMBER="trip_No";
    protected static final String CLASS="trip_class";
    protected static final String DIRECTION="trip_direction";
    protected static final String BLOCK="block_id";
    protected static final String CALENDER="calender_id";
    protected static final String EXTRA_CALENDER="extra_calendar";
    protected static final String TIME="time";

    public Trip(JPTIdata jpti,Route route){
        this.jpti=jpti;
        this.route=route;
    }
    public Trip(JPTIdata jptiData,Route route,JSONObject json){
        this(jptiData,route);
        try{
            try{
                this.traihType=route.classList.get(json.getInt(CLASS));
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                blockID=json.getInt(BLOCK);
            }catch(JSONException e){
                e.printStackTrace();
            }
            try{
                calender=jpti.calendarList.get(json.getInt(CALENDER));
            }catch(JSONException e){
                e.printStackTrace();
            }
            number=json.optString(NUMBER);
            name=json.optString(NAME);
            direction=json.optInt(DIRECTION);
            extraCalendarID=json.optInt(EXTRA_CALENDER);
            JSONArray timeArray=json.getJSONArray(TIME);
            for(int i=0;i<timeArray.length();i++){
                timeList.add(newTime(timeArray.getJSONObject(i)));
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            if(name!=null) {
                json.put(NAME, name);
            }
            if(number!=null) {
                json.put(NUMBER, number);
            }
            json.put(DIRECTION,direction);
            json.put(CLASS,traihType.index());
            json.put(BLOCK,blockID);
            json.put(CALENDER,calender.index());
            if(extraCalendarID>-1){
                json.put(EXTRA_CALENDER,extraCalendarID);
            }
            JSONArray timeArray=new JSONArray();
            for(Time time:timeList){
                timeArray.put(time.makeJSONObject());
            }
            json.put(TIME,timeArray);


        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * stationからTimeを取得する
     * @return
     */
    public Time searchTime(Station station){
        for(Time time:timeList){
            if(time.station==station){
                return time;
            }
        }
        return null;
    }

    /**
     * thisとtripの２つのTripを先発順を比較をする。
     * thisのほうが早い時刻の列車ならreturn -1;
     * thisのほうが遅い時刻の列車ならreturn 1;
     * 時刻が存在しないため比較不可能、途中で追い抜きがある場合　return 0;
     * @param trip
     * @return
     */
    public int compareTo(Trip trip){
        int result=0;
        for(Time time1:timeList){
            Time time2=trip.searchTime(time1.station);
            if(time2!=null){
                int timeInt1=time1.getTime();
                int timeInt2=time2.getTime();
                if(timeInt1!=-1&&timeInt2!=-1){
                    if(timeInt1>timeInt2){
                        if(result==-1){
                            return 0;
                        }
                        result= 1;
                    }
                    if(timeInt1<timeInt2){
                        if(result==1){
                            return 0;
                        }
                        result=-1;
                    }
                }
            }
        }
        return result;


    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(route.tripList.contains(this)) {
            return route.tripList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }
    public abstract Time newTime(JSONObject json);
}
