package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTI.Route;
import org.json.JSONObject;
public class RouteStation extends com.kamelong.JPTI.RouteStation {
    public RouteStation(JPTIdata jpti, com.kamelong.JPTI.Route route) {
        super(jpti, route);
    }

    public RouteStation(com.kamelong.JPTI.RouteStation oldStation) {
        super(oldStation);
    }

    public RouteStation(JPTIdata jpti, Route route, JSONObject json) {
        super(jpti, route, json);
    }

    @Override
    protected Station newStation(com.kamelong.JPTI.Station oldStation) {
        return newStation(oldStation);
    }

    /**
     * oudiaの駅とJPTIの駅リストからRouteStationを作成する。
     * @param oudiaStation この駅を作るための情報を含んだOuDiaの駅
     */
    public RouteStation(JPTI jpti,Route route,com.kamelong.JPTIOuDia.OuDia.OuDiaStation oudiaStation){
        this(jpti,route);
        //stationIDを指定。JPTIStationList内に同駅名の駅があればそれを使用。
        //もしなければ、新しく駅を作り、JPTIStationListに追加し、そのインデックスを登録
        int id=findStationID(jpti.stationList,oudiaStation.getName());
        if(id!=-1){
            station=jpti.stationList.get(id);
        }else{
            station=new Station(jpti,oudiaStation);
            jpti.stationList.add(station);
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

}
