package com.kamelong.JPTI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * JTPI形式の駅情報を格納するクラス
 */
public abstract class Station {

    protected JPTIdata jpti;
    /**
     駅・停留所・港・空港名
     */
    protected String name="";

    /**
     * 副駅名
     */
    protected String subName=null;

    /**
     1：駅
     2：バス停
     3：旅客船桟橋
     4：空港
     */
    protected int type=0;

    /**
     * 駅の説明
     */
    public String description=null;

    /**
     緯度
     */
    protected String lat=null;

    /**
     経度
     */
    protected String lon=null;

    /**
     * 停車場のURL
     * ※駅構内図など？
     */
    protected String url=null;

    /**
     * 車いすでの乗車が可能か？
     * ※stopにあるべき？
     */
    protected String wheelcharBoarding=null;

    /**
     * 駅に存在する停留所リスト
     */
    protected ArrayList<Stop> stops=new ArrayList<>();

    protected static final String NAME="station_name";
    protected static final String SUBNAME="station_subname";
    protected static final String TYPE="station_type";
    protected static final String DESCRIPTION="station_description";
    protected static final String LAT="station_lat";
    protected static final String LON="station_lon";
    protected static final String URL="station_url";
    protected static final String WHEELCHAIR="wheelchair_boarding";
    protected static final String STOP="stop";

    /**
     * デフォルトコンストラクタ
     * 特にベースに何もない状態から駅を作成する場合はこれ
     */
    public Station(JPTIdata jpti){
        this.jpti=jpti;

    }

    public Station(Station oldStation){
        jpti=oldStation.jpti;
        name=oldStation.name;
        subName=oldStation.subName;
        type=oldStation.type;
        description=oldStation.description;
        lat=oldStation.lat;
        lon=oldStation.lon;
        url=oldStation.url;
        wheelcharBoarding=oldStation.wheelcharBoarding;
        stops=oldStation.stops;
    }
    public Station(JPTIdata jpti,JSONObject json){
        this(jpti);
        try{
            try {
                name =json.getString(NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            subName=json.optString(SUBNAME);
            type=json.optInt(TYPE);
            description=json.optString(description);
            lat=json.optString(LAT);
            lon=json.optString(LON);
            url=json.optString(URL);
            wheelcharBoarding=json.optString(WHEELCHAIR);
            JSONArray stopArray=json.getJSONArray(STOP);
            for(int i=0;i<stopArray.length();i++){
                stops.add(newStop(stopArray.getJSONObject(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try {
            json.put(NAME, name);
            if(subName!=null){
                json.put(SUBNAME,subName);
            }
            if(type>0){
                json.put(TYPE,type);
            }
            if(description!=null) {
                json.put(DESCRIPTION, description);
            }
            if(lat!=null) {
                json.put(LAT,lat);
            }
            if(lon!=null) {
                json.put(LON,lon);
            }
            if(url!=null) {
                json.put(URL,url);
            }
            if(wheelcharBoarding!=null) {
                json.put(WHEELCHAIR,wheelcharBoarding);
            }
            json.put(STOP,makeStopsListJSON());
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;

    }
    public JSONArray makeStopsListJSON(){
        JSONArray array=new JSONArray();
        for(Stop stop:stops){
            array.put(stop.makeJSONObject());
        }
        return array;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(jpti.stationList.contains(this)) {
            return jpti.stationList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }
    protected abstract Stop newStop(JSONObject json);

}
