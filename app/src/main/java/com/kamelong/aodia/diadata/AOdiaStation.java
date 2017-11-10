package com.kamelong.aodia.diadata;

/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */

import android.util.SparseArray;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Route;
import com.kamelong.JPTI.RouteStation;
import com.kamelong.JPTI.Service;
import com.kamelong.JPTI.Station;

import java.util.ArrayList;

/**
 *
 * 路線駅データを格納するクラス。
 * @author  KameLong
 */
public class AOdiaStation{
    private AOdiaDiaFile diaFile=null;
    private Service service=null;
    private JPTI jpti=null;

    ArrayList<RouteStation>stationList=new ArrayList<>();
    ArrayList<Boolean>borderStationList=new ArrayList<>();
    ArrayList<RouteStation>borderStation=new ArrayList<>();
    SparseArray<Route> routeMap=new SparseArray<>();
    boolean connect[][];
    /**
     * 最小所要時間
     */
    private ArrayList<Integer>stationTime=new ArrayList<>();

    static final int BOTH=2;
    static final int ARRIVE=1;
    static final int DEPART=0;

    public AOdiaStation(AOdiaDiaFile diaFile){
        this.diaFile=diaFile;
        this.service=diaFile.getService();
        this.jpti=diaFile.getJPTI();

        for(int i=0;i<service.getRouteList(0).size();i++){
            Route route=service.getRouteList(0).get(i);
            for(RouteStation station:route.getStationList(0)){
                stationList.add(station);
                routeMap.put(stationList.size()-1,route);
                borderStationList.add(false);

            }
            borderStation.add(stationList.get(stationList.size()-1));
            borderStationList.set(borderStationList.size()-1,true);

        }
        borderStation.remove(borderStation.size()-1);
        borderStationList.set(borderStationList.size()-1,false);

        connect=new boolean[service.getRouteList(0).size()][service.getRouteList(0).size()];
        for(int x=0;x<service.getRouteList(0).size();x++){
            ArrayList<RouteStation>s=service.getRouteList(0).get(x).getStationList(0);
            for(int y=0;y<service.getRouteList(0).size();y++){
                if(s.get(s.size()-1).getStation()==service.getRouteList(0).get(y).getStationList(0).get(0).getStation()){
                    connect[x][y]=true;
                }else{
                    connect[x][y]=false;
                }
            }
        }

    }

    public String getName(int index){
        return stationList.get(index).getName();
    }

    /**
     * 駅名の略称として、最初の5文字のみ表示する機能を用いる際に使う
     * @return
     */
    public String getShortName(int index){
        String result=getName(index);
        if(result.length()>5){
            return result.substring(0,5);
        }
        return result;
    }

    /**
     * 境界駅かどうかを返す。
     * 境界駅では無いとき=0
     * 境界駅（路線分岐終点)=1
     * 境界駅（分岐駅)=2
     */
    public int border(int index){
        if(index>=stationList.size()||index<0){
            return 0;
        }
        if(borderStationList.get(index)){
            if(stationList.get(index).getName().equals(stationList.get(index+1).getName())){
                return 2;
            }else{
                return 1;
            }
        }
        return 0;
    }
    /**
     * 総駅数を返す
     */
    public int getStationNum(){
        return stationList.size();
    }


    /**
     * 発着表示をするかどうかを返す
     * @param pos 発、着どちらの情報を取得したいか　STOP_ARRIVE,STOP_DEPARTから選択
     * @param direct 取得したい方向（上り(=1)か下り(=0)か）
     * @return 時刻を表示するときはtrueそうでないときはfalse
     */
    public boolean getTimeShow(int index,int pos,int direct){
        int viewStyle=0;
        if(direct==1) {
            viewStyle = stationList.get(index).getViewStyle() / 10;
        }
        if(direct==0) {
            viewStyle = stationList.get(index).getViewStyle() %10;
        }
        switch (pos){
            case ARRIVE:
                return viewStyle==1||viewStyle==2;

            case DEPART:
                return viewStyle==0||viewStyle==2;
            default:
                return false;
        }
    }
    public int getTimeShow(int index,int direct){
        int viewStyle=0;
        if(direct==1) {
            viewStyle = stationList.get(index).getViewStyle() / 10;
        }
        if(direct==0) {
            viewStyle = stationList.get(index).getViewStyle() %10;
        }
        return viewStyle;
    }


    /**
     * 駅規模が主要駅かどうかを返す。
     * @return
     */
    public boolean bigStation(int index){
        return stationList.get(index).isBigStation();
    }
    public Station getStation(int index){
        return stationList.get(index).getStation();
    }
    public Route getRouteByStationIndex(int index){
        return routeMap.get(index);
    }
    /**
     * 最小所要時間を計算する。
     * この関数は処理の完了までにかなりの時間がかかると予想されます。
     * 別スレッドでの実行を推奨します
     */
    public void calcMinReqiredTime(){
        stationTime.add(0);
        for(int i=0;i<getStationNum()-1;i++){
            if(stationList.get(i).getName().equals(stationList.get(i+1).getName())){
                stationTime.add(stationTime.get(stationTime.size()-1));
            }else {
                stationTime.add(stationTime.get(stationTime.size() - 1) + getMinReqiredTime(i, i + 1));
            }
        }
    }
    /**
     *  駅間最小所要時間を返す。
     *  startStatioin endStationの両方に時刻が存在する列車のうち、
     *  所要時間（着時刻-発時刻)の最も短いものを秒単位で返す。
     *  ただし、駅間所要時間が60秒より短いときは60秒を返す。
     *
     *  startStation endStationは便宜上区別しているが、順不同である。
     * @param startStation
     * @param endStation
     * @return time(second)
     */
    private int getMinReqiredTime(int startStation, int endStation){
        int result=360000;
        for(int i=0;i<diaFile.getDiaNum();i++){
            if(diaFile.getDiaName(i).equals("基準運転時分")){
                for(int train=0;train<diaFile.getTimeTable(i,0).trainList.size();train++){
                    int value=diaFile.getTimeTable(i,0).getTrain(train).getRequiredTime(startStation,endStation);
                    if (value > 0 && result > value) {
                        result = value;
                    }
                }
                for(int train=0;train<diaFile.getTimeTable(i,1).trainList.size();train++){
                    int value=diaFile.getTimeTable(i,1).getTrain(train).getRequiredTime(startStation,endStation);
                    if (value > 0 && result > value) {
                        result = value;
                    }
                }
                if(result==360000){
                    result=120;
                }
                if(result<60){
                    result=60;
                }

                return result;
            }
        }
        for(int i=0;i<diaFile.getDiaNum();i++){
            for(int train=0;train<diaFile.getTimeTable(i,0).trainList.size();train++){
                int value=diaFile.getTimeTable(i,0).getTrain(train).getRequiredTime(startStation,endStation);
                if (value > 0 && result > value) {
                    result = value;
                }
            }
            for(int train=0;train<diaFile.getTimeTable(i,1).trainList.size();train++){
                int value=diaFile.getTimeTable(i,1).getTrain(train).getRequiredTime(startStation,endStation);
                if (value > 0 && result > value) {
                    result = value;
                }
            }
        }
        if(result==360000){
            result=120;
        }
        if(result<60){
            result=60;
        }

        return result;
    }
    /**
     * 最小所要時間のリストを返します。
     * 最小所要時間は別スレッドで計算されている場合がありますので、
     * 計算が終了するまで、スレッドを待機させます。
     * @return
     */
    public ArrayList<Integer> getStationTime(){
        while(stationTime.size()<getStationNum()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationTime;
    }
    public RouteStation getRouteStation(int index){
        return stationList.get(index);
    }
    public int getStationID(RouteStation s){
        return stationList.indexOf(s);

    }

}
