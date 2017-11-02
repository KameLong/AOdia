package com.kamelong.JPTI;

import com.kamelong.OuDia.OuDiaStation;

import org.json.JSONObject;

/**
 * Route中に使用される、Routeに所属する駅リスト。
 * 時刻表表記に使用される情報はこのクラス内に記述する
 */
public class RouteStation{
    /**
     * 親JPTIdata
     */
    private JPTI jpti;
    /**
     * 親Route
     */
    private Route route;
    /**
     * 対応する駅
     */
    public Station station;
    /**
     * 路線キロ程
     */
    private double km=-1;
    /**
     * 駅ナンバリング
     */
    private int numbering=-1;
    /**
     * 主要駅かどうか
     */
    private boolean bigStation=false;
    /**
     2桁の数字文字列で表す
     10の位：上り
     1の位：下り
     +
     0：発のみ
     1：発着
     2：着のみ
     */
    private int viewStyle=0;
    public static final int VIEWSTYLE_HATU=00;
    public static final int VIEWSTYLE_HATUTYAKU=11;
    public static final int VIEWSTYLE_KUDARITYAKU=02;
    public static final int VIEWSTYLE_NOBORITYAKU=20;
    public static final int VIEWSTYLE_KUDARIHATUTYAKU=10;
    public static final int VIEWSTYLE_NOBORIHATUTYAKU=01;
    /**
     * 境界線を持つかどうか
     */
    private boolean border=false;

    private static final String STATION_ID="station_id";
    private static final String KM="km";
    private static final String NUMBERING="station_numbering";
    private static final String TYPE="station_type";
    private static final String VIEWSTYLE="viewstyle";
    private static final String BORDER="border";

    /**
     * 新規作成
     */
    private RouteStation(JPTI jpti, Route route){
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
    public RouteStation(JPTI jpti, Route route, JSONObject json){
        this(jpti,route);
        try {
            try {
                station = jpti.getStation(json.getInt(STATION_ID));
            }catch(Exception e){
                e.printStackTrace();
            }
            km=json.optDouble(KM,-1);
            numbering=json.optInt(NUMBERING,-1);
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
                json.put(STATION_ID,jpti.indexOf(station));
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



    private Station newStation(Station oldStation){
        return new Station(jpti);
    }
    /**
     * oudiaの駅とJPTIの駅リストからRouteStationを作成する。
     * @param oudiaStation この駅を作るための情報を含んだOuDiaの駅
     */
    public RouteStation(JPTI jpti, Route route, OuDiaStation oudiaStation){
        this(jpti,route);
        //stationIDを指定。JPTIStationList内に同駅名の駅があればそれを使用。
        //もしなければ、新しく駅を作り、JPTIStationListに追加し、そのインデックスを登録
        int id=jpti.getStationIDByName(oudiaStation.getName());
        if(id!=-1){
            station=jpti.getStation(id);
        }else{
            station=jpti.addNewStation(oudiaStation);
        }
        bigStation=oudiaStation.getBigStation();
        border=oudiaStation.border();

        //viewstyleの指定
        //上り
        int viewStyleInt=0;
        switch (oudiaStation.getTimeShow(1)){
            case 1:
                viewStyleInt+=0;
                break;
            case 2:
                viewStyleInt+=2;
                break;
            case 3:
                viewStyleInt+=1;
                break;
        }
        viewStyleInt=viewStyleInt*10;
        switch (oudiaStation.getTimeShow(0)){
            case 1:
                viewStyleInt+=0;
                break;
            case 2:
                viewStyleInt+=2;
                break;
            case 3:
                viewStyleInt+=1;
                break;
        }
        viewStyle=viewStyleInt;
    }
    public Station getStation(){
        return (Station)station;
    }
    public boolean isBigStation(){
        return  bigStation;
    }
    public int getViewStyle(){
        return viewStyle;
    }
    public String getName(){
        return station.getName();
    }
    public void setViewStyle(int value){
        viewStyle=value;
    }



}
