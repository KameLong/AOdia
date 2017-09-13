package com.kamelong.JPTI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Route中に使用される、Routeに所属する駅リスト。
 * 時刻表表記に使用される情報はこのクラス内に記述する
 */
public abstract class RouteStation extends Observable{
    /**
     * 親JPTIdata
     */
    public JPTIdata jpti;
    /**
     * 親Route
     */
    protected Route route;
    /**
     * 対応する駅
     */
    public Station station;
    /**
     * 路線キロ程
     */
    protected double km=-1;
    /**
     * 駅ナンバリング
     */
    protected int numbering=-1;
    /**
     * 主要駅かどうか
     */
    protected boolean bigStation=false;
    /**
     2桁の数字文字列で表す
     10の位：上り
     1の位：下り
     +
     0：発のみ
     1：発着
     2：着のみ
     */
    protected int viewStyle=0;
    public static final int VIEWSTYLE_HATU=00;
    public static final int VIEWSTYLE_HATUTYAKU=11;
    public static final int VIEWSTYLE_KUDARITYAKU=02;
    public static final int VIEWSTYLE_NOBORITYAKU=20;
    public static final int VIEWSTYLE_KUDARIHATUTYAKU=10;
    public static final int VIEWSTYLE_NOBORIHATUTYAKU=01;
    /**
     * 境界線を持つかどうか
     */
    protected  boolean border=false;

    protected  static final String STATION_ID="station_id";
    protected  static final String KM="km";
    protected  static final String NUMBERING="station_numbering";
    protected  static final String TYPE="station_type";
    protected  static final String VIEWSTYLE="viewstyle";
    protected  static final String BORDER="border";

    /**
     * 新規作成
     */
    public  RouteStation(JPTIdata jpti,Route route){
        this.jpti=jpti;
        this.route=route;

    }
    public RouteStation(RouteStation oldStation){
        this.jpti=oldStation.jpti;
        this.route=oldStation.route;
        this.station=newStation(oldStation.station);
        this.km=oldStation.km;
        this.numbering=oldStation.numbering;
        this.bigStation=oldStation.bigStation;
        this.viewStyle=oldStation.viewStyle;
        this.border=oldStation.border;
    }
    public RouteStation(JPTIdata jpti,Route route,JSONObject json){
        this(jpti,route);
        try {
            try {
                station = jpti.stationList.get(json.getInt(STATION_ID));
            }catch(Exception e){
                e.printStackTrace();
            }
            km=json.optDouble(KM,-1);
            numbering=json.optInt(NUMBERING);
            bigStation=json.optInt(TYPE)==1;
            viewStyle=json.optInt(VIEWSTYLE);
            border=json.optInt(BORDER)==1;

        }catch(Exception e){

        }
    }
    public JSONObject makeJSONObject (){
        JSONObject json=new JSONObject();
        if(station==null){
            return json;
        }
        try{
            if(station.index()>-1){
                json.put(STATION_ID,station.index());
            }
            if(km>-1){
                json.put(KM,km);
            }
            if(numbering>-1){
                json.put(NUMBERING,numbering);
            }
            if(bigStation){
                json.put(TYPE,1);
            }else{
                json.put(TYPE,0);
            }
            if(viewStyle>-1){
                json.put(VIEWSTYLE,String.format("%02d",viewStyle));
            }
            if(border){
                json.put(BORDER,1);
            }else{
                json.put(BORDER,0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }


    /**
     * 駅リストから指定された駅名を持つ駅インデックスを返す。
     * 駅が存在しないときは-1が返る
     * @param stations 駅リスト
     * @param stationName 指定駅名
     * @return stationsの配列中の何番目が指定駅であるかのインデックス
     */
    protected int findStationID(ArrayList<Station> stations,String stationName){
        for(int i=0;i<stations.size();i++){
            if(stations.get(i).name.equals(stationName)){
                return i;
            }
        }
        return -1;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(route.stationList.contains(this)) {
            return route.stationList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }
    protected abstract Station newStation(Station oldStation);


}
