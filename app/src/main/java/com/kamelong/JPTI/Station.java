package com.kamelong.JPTI;



import com.kamelong.OuDia.OuDiaStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * JTPI形式の駅情報を格納するクラス
 */
public class Station {

    private JPTI jpti;
    /**
     駅・停留所・港・空港名
     */
    String name="";

    /**
     * 副駅名
     */
    private String subName=null;

    /**
     1：駅
     2：バス停
     3：旅客船桟橋
     4：空港
     */
    private int type=0;

    /**
     * 駅の説明
     */
    private String description=null;

    /**
     緯度
     */
    private String lat=null;

    /**
     経度
     */
    private String lon=null;

    /**
     * 停車場のURL
     * ※駅構内図など？
     */
    private String url=null;

    /**
     * 車いすでの乗車が可能か？
     * ※stopにあるべき？
     */
    private String wheelcharBoarding=null;

    /**
     * 駅に存在する停留所リスト
     */
    ArrayList<Stop> stops=new ArrayList<>();

    private static final String NAME="station_name";
    private static final String SUBNAME="station_subname";
    private static final String TYPE="station_type";
    private static final String DESCRIPTION="station_description";
    private static final String LAT="station_lat";
    private static final String LON="station_lon";
    private static final String URL="station_url";
    private static final String WHEELCHAIR="wheelchair_boarding";
    private static final String STOP="stop";

    /**
     * デフォルトコンストラクタ
     * 特にベースに何もない状態から駅を作成する場合はこれ
     */
    public Station(JPTI jpti){
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
    public Station(JPTI jpti, JSONObject json){
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
                stops.add(jpti.getStop(stopArray.optInt(i,0)));
                jpti.getStop(stopArray.optInt(i,0)).setStation(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * OuDia形式の駅から作成する場合
     */
    public Station(JPTI jpti, OuDiaStation oudiaStation){
        this(jpti);
        name=oudiaStation.getName();
        int oudiaIndex=jpti.getStopIDByName(this,"FromOuDia");
        if(oudiaIndex==-1) {
            Stop stop=jpti.addNewStop(this);
            stop.setName("FromOuDia");
            stops.add(stop);
        }else{
            stops.add(jpti.getStop(oudiaIndex));

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
    private JSONArray makeStopsListJSON(){
        JSONArray array=new JSONArray();
        for(Stop stop:stops){
            array.put(jpti.indexOf(stop));
        }
        return array;
    }
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }
    public void addStop(Stop stop){
        if(!stops.contains(stop)){
            stops.add(stop);
        }
    }

}
