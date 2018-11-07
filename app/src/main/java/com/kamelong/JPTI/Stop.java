package com.kamelong.JPTI;



import com.eclipsesource.json.JsonObject;

import org.json.JSONObject;

/**
 * 停留所情報を格納するクラス
 */
public class Stop {
    private JPTI jpti;
    private Station station;
    /**
     例：1番線
     例：5番乗り場
     */
    private String name="";
    /**
     * 停留所番号
     例：「1」番線
     例：「5」番乗り場
     */
    private int number=-1;
    /**
     例：大和・海老名方面
     例：110系統 杉田平和町行etc...
     */
    private String description="";
    /**
     * 緯度
     */
    private String lat="";
    /**
     * 経度
     */
    private String lon="";
    /**
     * 運賃区間id
     */
    private int zoneID=-1;

    private static final String NAME="stop_name";
    private static final String NUMBER="stop_number";
    private static final String DESCRIPTION="stop_description";
    private static final String LAT="stop_lat";
    private static final String LON="stop_lon";
    private static final String ZONE_ID="zone_id";

    public Stop(JPTI jpti, Station station){
        this.jpti=jpti;
        this.station=station;
    }
    public Stop(JPTI jpti, JsonObject json){
        this.jpti=jpti;
        try{
            name=json.getString(NAME,"");
            description=json.getString(DESCRIPTION,"");
            lat=json.getString(LAT,"");
            lon=json.getString(LON,"");
            zoneID=json.getInt(ZONE_ID,-1);
            number=json.getInt(NUMBER,-1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    JsonObject makeJSONObject(){
        JsonObject json=new JsonObject();
        try{
            json.add(NAME,name);
            if(number>-1){
                json.add(NUMBER,number);
            }
            if(description.length()>0){
                json.add(DESCRIPTION,description);
            }
            if(lat.length()>0){
                json.add(LAT,lat);
            }
            if(lon.length()>0){
                json.add(LON,lon);
            }
            if(zoneID>0){
                json.add(ZONE_ID,zoneID);
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
    public Station getStation(){
        return station;
    }
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }
    public void setStation(Station staiton){
        this.station=staiton;
    }



}
