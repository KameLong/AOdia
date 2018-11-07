package com.kamelong.JPTI;



import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
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
public class Trip {
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
     * 64bitLongにて記述
     * 4bit存在Frag(0:存在しない:1:存在する
     */
    public Map<Station,Time> timeList=new HashMap<>();


    private static final String ROUTE="route_id";
    private static final String NAME="trip_name";
    private static final String NUMBER="trip_No";
    private static final String CLASS="trip_class";
    private static final String DIRECTION="trip_direction";
    private static final String BLOCK="block_id";
    private static final String CALENDER="calender_id";
    private static final String EXTRA_CALENDER="extra_calendar";
    private static final String TIME="time";
    public Trip(){}
    public Trip(JPTI jpti, Route route){
        this.jpti=jpti;
        this.route=route;
    }
    public Trip(JPTI jpti, JsonObject json){
        this.jpti=jpti;
        try{
            route=jpti.getRoute(json.getInt(ROUTE,0));
                this.traihType=jpti.getTrainType(json.getInt(CLASS,0));
                blockID=json.getInt(BLOCK,0);
                calender=jpti.calendarList.get(json.getInt(CALENDER,0));
            number=json.getString(NUMBER,"");
            name=json.getString(NAME,"");
            direction=json.getInt(DIRECTION,0);
            extraCalendarID=json.getInt(EXTRA_CALENDER,-1);
            JsonArray timeArray=json.get(TIME).asArray();
            for(int i=0;i<timeArray.size();i++){
                Time time=newTime(timeArray.get(i).asObject());
                timeList.put(time.getStation(),time);
            }




        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Trip(JPTI jpti, Route route, Calendar calendar, OuDiaFile oudia, OuDiaTrain train, int startStation, int endStation, int blockID){
        this(jpti,route);
        this.calender=calendar;
        this.traihType=jpti.getTrainType(train.getType());
        this.blockID=blockID;
        for(int i=startStation;i<endStation+1;i++){
            if(train.getStopType(i)== com.kamelong.OuDia.OuDiaTrain.STOP_TYPE_PASS||train.getStopType(i)== com.kamelong.OuDia.OuDiaTrain.STOP_TYPE_STOP){
                Time time=new Time(jpti,this,jpti.getStop(jpti.getStopIDByName(jpti.getStation(jpti.getStationIDByName(oudia.getStation(i).getName())),"FromOuDia")),train,i);
                timeList.put(time.getStation(),time);
            }
        }
    }

    public JsonObject makeJSONObject(){
        JsonObject json=new JsonObject();
        try{
            if(name.length()>0) {
                json.add(NAME, name);
            }
            if(number.length()>0) {
                json.add(NUMBER, number);
            }
            json.add(DIRECTION,direction);
            json.add(CLASS,jpti.indexOf(traihType));
            json.add(BLOCK,blockID);
            json.add(CALENDER,calender.index());
            json.add(ROUTE,jpti.indexOf(route));
            if(extraCalendarID>-1){
                json.add(EXTRA_CALENDER,extraCalendarID);
            }
            JsonArray timeArray=new JsonArray();
            for(Time time:timeList.values()){
                timeArray.add(time.makeJSONObject());
            }
            json.add(TIME,timeArray);


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
    public int compareTo(Trip trip){
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
    private Time newTime(JsonObject json){
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
