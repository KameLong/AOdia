package com.kamelong.JPTI;



import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.kamelong.OuDia.OuDiaStation;



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
    public Station(JPTI jpti, JsonObject json){
        this(jpti);
        try{
                name =json.getString(NAME,"駅名不明");
            subName=json.getString(SUBNAME,"");
            type=json.getInt(TYPE,0);
                description = json.getString(DESCRIPTION, "");
            lat=json.getString(LAT,"");
            lon=json.getString(LON,"");
            url=json.getString(URL,"");
            wheelcharBoarding=json.getString(WHEELCHAIR,"");
            JsonArray stopArray=json.get(STOP).asArray();
            for(int i=0;i<stopArray.size();i++){
                stops.add(jpti.getStop(stopArray.get(i).asInt()));
                jpti.getStop(stopArray.get(i).asInt()).setStation(this);
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



    public JsonObject makeJSONObject(){
        JsonObject json=new JsonObject();
        try {
            json.add(NAME, name);
            if(subName!=null){
                json.add(SUBNAME,subName);
            }
            if(type>0){
                json.add(TYPE,type);
            }
            if(description!=null) {
                json.add(DESCRIPTION, description);
            }
            if(lat!=null) {
                json.add(LAT,lat);
            }
            if(lon!=null) {
                json.add(LON,lon);
            }
            if(url!=null) {
                json.add(URL,url);
            }
            if(wheelcharBoarding!=null) {
                json.add(WHEELCHAIR,wheelcharBoarding);
            }
            json.add(STOP,makeStopsListJSON());
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;

    }
    private JsonArray makeStopsListJSON(){
        JsonArray array=new JsonArray();
        for(Stop stop:stops){
            array.add(jpti.indexOf(stop));
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
