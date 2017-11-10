package com.kamelong.JPTI;



import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.OuDia.OuDiaTrain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 列車を記録するクラス
 */
public class TripOld extends Trip{
    /**
     * 所属JPTIdata
     */
    private JPTI jpti=null;
    /**
     * 所属Route
     */
    private Route route=null;
    /**
     * 列車番号
     */
    private String number="";
    /**
     * 列車名、バス愛称、行先等の情報
     */
    private String name="";
    /**
     * 0：Route順方向
     * 1：Route逆方向
     */
    private int direction=0;
    /**
     * 種別
     */
    private TrainType traihType=null;
    /**
     * 所属列車
     */
    protected Train train=null;
    /**
     * 運行する日id
     */
    private Calendar calender=null;
    /**
     * 臨時運行日id
     */
    private int extraCalendarID=-1;
    /**
     *
     */
    private int blockID=-1;

    /**
     * 駅時刻
     */
    private Map<Station,Time> timeList=new HashMap<>();


    private static final String ROUTE="route_id";
    private static final String NAME="trip_name";
    private static final String NUMBER="trip_No";
    private static final String CLASS="trip_class";
    private static final String DIRECTION="trip_direction";
    private static final String BLOCK="block_id";
    private static final String CALENDER="calender_id";
    private static final String EXTRA_CALENDER="extra_calendar";
    private static final String TIME="time";

    public TripOld(JPTI jpti, Route route){
        this.jpti=jpti;
        this.route=route;
    }
    public TripOld(JPTI jpti, JSONObject json){
        this.jpti=jpti;
        try{
            route=jpti.getRoute(json.optInt(ROUTE,0));
            try{
                this.traihType=jpti.getTrainType(json.getInt(CLASS));
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
            number=json.optString(NUMBER,"");
            name=json.optString(NAME,"");
            direction=json.optInt(DIRECTION,0);
            extraCalendarID=json.optInt(EXTRA_CALENDER,-1);
            JSONArray timeArray=json.getJSONArray(TIME);
            for(int i=0;i<timeArray.length();i++){
                Time time=newTime(timeArray.getJSONObject(i));
                timeList.put(time.getStation(),time);
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public TripOld(JPTI jpti, Route route, Calendar calendar, OuDiaFile oudia, OuDiaTrain train, int startStation, int endStation, int blockID){
        this(jpti,route);
        this.calender=calendar;
        this.traihType=jpti.getTrainType(train.getType());
        this.blockID=blockID;
        for(int i=startStation;i<endStation+1;i++){
            if(train.getStopType(i)== OuDiaTrain.STOP_TYPE_PASS||train.getStopType(i)== OuDiaTrain.STOP_TYPE_STOP){
                Time time=new Time(jpti,this,jpti.getStop(jpti.getStopIDByName(jpti.getStation(jpti.getStationIDByName(oudia.getStation(i).getName())),"FromOuDia")),train,i);
                timeList.put(time.getStation(),time);
            }
        }
    }

    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            if(name.length()>0) {
                json.put(NAME, name);
            }
            if(number.length()>0) {
                json.put(NUMBER, number);
            }
            json.put(DIRECTION,direction);
            json.put(CLASS,jpti.indexOf(traihType));
            json.put(BLOCK,blockID);
            json.put(CALENDER,calender.index());
            json.put(ROUTE,jpti.indexOf(route));
            if(extraCalendarID>-1){
                json.put(EXTRA_CALENDER,extraCalendarID);
            }
            JSONArray timeArray=new JSONArray();
            for(Time time:timeList.values()){
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
        return timeList.get(station);
    }
    public Route getRoute(){
        return route;
    }

    /**
     * thisとtripの２つのTripを先発順を比較をする。
     * thisのほうが早い時刻の列車ならreturn -1;
     * thisのほうが遅い時刻の列車ならreturn 1;
     * 時刻が存在しないため比較不可能、途中で追い抜きがある場合　return 0;
     * @param trip
     * @return
     */
    public int compareTo(TripOld trip){
        int result=0;
        for(Time time1:timeList.values()){
            Time time2=trip.searchTime(time1.getStation());
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
    private Time newTime(JSONObject json){
        return new Time(jpti,this,json);
    };
    public TrainType getTrainType(){
        if(traihType==null){
            return jpti.getTrainType(0);
        }
        return traihType;
    }
    public void setName(String value){
        name=value;
    }
    public String getName(){
        return name;
    }
    public void addTime(Time time){
        timeList.put(time.getStation(),time);
    }
    public Calendar getCalendar(){
        return calender;
    }
    public void setCalendar(int i){
        calender=jpti.getCalendar(i);
    }
    public int getDirect(){
        return direction;
    }
    public void setDirect(int value){
        direction=value;
    }
}
