package com.kamelong.JPTIOuDia.OuDia;

import com.kamelong.JPTIOuDia.JPTI.RouteStation;
import com.kamelong.OuDia.*;

/**
 * OuDia-JPTI用のOuDiaStation
 */
public class OuDiaStation extends com.kamelong.OuDia.OuDiaStation {
    public OuDiaStation(){
        super();
    }
    public OuDiaStation(RouteStation routeStation){
        this.name=routeStation.getStation().getName();
        if(routeStation.isBigStation()){
            this.size=1;
        }
        switch (routeStation.getViewStyle()){
            case RouteStation.VIEWSTYLE_HATU:
                timeShow=SHOW_HATU;
                break;
            case RouteStation.VIEWSTYLE_HATUTYAKU:
                timeShow=SHOW_HATUTYAKU;
                break;
            case RouteStation.VIEWSTYLE_KUDARITYAKU:
                timeShow=SHOW_KUDARITYAKU;
                break;
            case RouteStation.VIEWSTYLE_NOBORITYAKU:
                timeShow=SHOW_NOBORITYAKU;
                break;
            case RouteStation.VIEWSTYLE_NOBORIHATUTYAKU:
                timeShow=SHOW_NOBORIHATUTYAKU;
                break;
            case RouteStation.VIEWSTYLE_KUDARIHATUTYAKU:
                timeShow=SHOW_KUDARIHATUTYAKU;
                break;
        }

    }
    public boolean border(){
        return border==1;
    }
    public String getName(){
        return name;
    }
    public boolean getBigStation(){
        return size==1;
    }
    /**
     * 発着表示を表す整数を返す。
     * 方向のみ指定し、発着両方の情報を返す。
     * @param direct 取得したい方向（上り(=1)か下り(=0)か）
     * @return 着時刻を表示するとき+2,発時刻を表示するとき+1
     */
    public int getTimeShow(int direct){
        switch(direct){
            case 0:
                return timeShow%4;
            case 1:
                return (timeShow/4)%4;
            default:
                return 0;
        }
    }
    public void setTimeShow(int value){
        super.setTimeShow(value);
    }
    public void setBorder(boolean value){
        if(value) {
            this.border = 1;
        }else{
            this.border=0;
        }
    }
}
