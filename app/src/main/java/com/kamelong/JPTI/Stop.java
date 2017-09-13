package com.kamelong.JPTI;

import org.json.JSONObject;

/**
 * 停留所情報を格納するクラス
 */
public abstract class Stop {
    protected JPTIdata jpti;
    protected Station station;
    /**
     例：1番線
     例：5番乗り場
     */
    protected String name="";
    /**
     * 停留所番号
     例：「1」番線
     例：「5」番乗り場
     */
    protected int number=-1;
    /**
     例：大和・海老名方面
     例：110系統 杉田平和町行etc...
     */
    protected String description=null;
    /**
     * 緯度
     */
    protected String lat=null;
    /**
     * 経度
     */
    protected String lon=null;
    /**
     * 運賃区間id
     */
    protected int zoneID=-1;

    protected static final String NAME="stop_name";
    protected static final String NUMBER="stop_number";
    protected static final String DESCRIPTION="stop_description";
    protected static final String LAT="stop_lat";
    protected static final String LON="stop_lon";
    protected static final String ZONE_ID="zone_id";

    public Stop(JPTIdata jpti,Station station){
        this.jpti=jpti;
        this.station=station;
    }
    public Stop(JPTIdata jpti,Station station,JSONObject json){
        this(jpti,station);
        try{
            name=json.optString(NAME);
            description=json.optString(DESCRIPTION);
            lat=json.optString(LAT);
            lon=json.optString(LON);
            zoneID=json.optInt(ZONE_ID,-1);
            number=json.optInt(NUMBER);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(NAME,name);
            if(number>-1){
                json.put(NUMBER,number);
            }
            if(description!=null){
                json.put(DESCRIPTION,description);
            }
            if(lat!=null){
                json.put(LAT,lat);
            }
            if(lon!=null){
                json.put(LON,lon);
            }
            if(zoneID>0){
                json.put(ZONE_ID,zoneID);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(station.stops.contains(this)) {
            return station.stops.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }



}
