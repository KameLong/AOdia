package com.kamelong.JPTI;

import android.app.IntentService;

import com.eclipsesource.json.JsonObject;
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
    /**
     * 発着番線の表示を行うかどうか？
     * 10進数表記10の位：上り、1の位：下り
     * 1は表示、0は非表示
     */
    private int showStopNum=0;
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
    private static final String SHOW_STOPNUM="show_stop_num";
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
    public RouteStation(JPTI jpti,Route route,Station station){
        this.jpti=jpti;
        this.route=route;
        this.station=station;
    }
    public RouteStation(JPTI jpti, Route route, JsonObject json){
        this(jpti,route);
        try {
                station = jpti.getStation(json.getInt(STATION_ID,0));
            km=json.getDouble(KM,-1);
            numbering=json.getInt(NUMBERING,-1);
            bigStation=json.getInt(TYPE,0)==1;
            viewStyle= Integer.parseInt(json.getString(VIEWSTYLE,"0"));
            showStopNum= Integer.parseInt(json.getString(SHOW_STOPNUM,"0"));
            border=json.getInt(BORDER,0)==1;

        }catch(Exception e){
            e.printStackTrace();

        }
    }
    public JsonObject makeJSONObject (){
        JsonObject json=new JsonObject();
        if(station==null){
            return json;
        }
        try{
                json.add(STATION_ID,jpti.indexOf(station));
            if(km>-1){
                json.add(KM,km);
            }
            if(numbering>-1){
                json.add(NUMBERING,numbering);
            }
            if(bigStation){
                json.add(TYPE,1);
            }else{
                json.add(TYPE,0);
            }
            if(viewStyle>-1){
                json.add(VIEWSTYLE,String.format("%02d",viewStyle));
            }
            if(showStopNum>-1){
                json.add(SHOW_STOPNUM,String.format("%02d",viewStyle));
            }
            if(border){
                json.add(BORDER,1);
            }else{
                json.add(BORDER,0);
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
    public int getViewStyle(int direct){
        if(direct==0){
            return viewStyle%10;
        }else{
            return viewStyle/10;
        }
    }
    public String getName(){
        return station.getName();
    }
    public void setViewStyle(int value){
        viewStyle=value;
    }
    public boolean getShowStopNum(int direct){
        if(direct==1){
            return showStopNum/10==1;
        }else{
            return showStopNum%10==1;
        }
    }



}
