package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * JPTI-OuDia用のServiceクラス
 * OuDiaとの変換機能を実装している
 */
public class Service extends com.kamelong.JPTI.Service{
    public Service(JPTIdata jpti){
        super(jpti);
    }
    public Service(JPTIdata jpti, JSONObject json){
        super(jpti,json);
    }

    public void loadOuDia(OuDiaFile diaFile){
        name=diaFile.getLineName();
        stationWidth=diaFile.getStationNameLength();
        trainWidth=diaFile.getTrainWidth();
        startTime=timeInt2String(diaFile.getStartTime());
        defaulyStationSpace=diaFile.getStationDistanceDefault();
        comment=diaFile.getComment();
        diaTextColor=diaFile.getDiaTextColor();
        diaBackColor=diaFile.getBackGroundColor();
        diaTrainColor=diaFile.getTrainColor();
        diaAxisColor=diaFile.getAxisColor();
        timeTableFont=diaFile.getTableFont();
        timeTableVFont=diaFile.getVfont();
        diaStationFont=diaFile.getStationFont();
        diaTimeFont=diaFile.getDiaTimeFont();
        diaTrainFont=diaFile.getDiaTextFont();
        commentFont=diaFile.getCommnetFont();
    }
    public int getRouteNum(){
        return route.size();
    }
    public Route getRoute(int index,int direct){
        return (Route)route.keySet().toArray()[index*(1-2*direct)+direct*(route.size()-1)];
    }
    public int getRouteDirect(Route mroute){
        return route.get(mroute);
    }
    public void addRoute(Route route){
        this.route.put(route,0);
    }

}
