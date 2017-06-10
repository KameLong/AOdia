package com.fc2.web.kamelong.aodia.GTFS;

import java.util.ArrayList;

/**
 * GTFS向けのDiaFile
 * １つのrouteIDにつき１つのオブジェクトを生成する
 */
public class GTFSTrain {
    public String routeID;
    public String serviceID;
    public String tripID;
    public ArrayList<Integer> departureTime=new ArrayList<>();
    public ArrayList<Integer> arrivalTime=new ArrayList<>();
    public ArrayList<String> stopID=new ArrayList<>();


    public GTFSTrain(String route, String service, String trip){
        routeID=route;
        serviceID=service;
        tripID=trip;
    }
    public GTFSTrain(){
    }
    public void addNewLine(String[] strs){
        arrivalTime.add(timeStoI(strs[1]));
        departureTime.add(timeStoI(strs[2]));
        stopID.add(strs[3]);
    }
    public int timeStoI(String time){
        String[] timeStrs=time.split(":");
        return Integer.parseInt(timeStrs[0])*3600+Integer.parseInt(timeStrs[1])*60+Integer.parseInt(timeStrs[2]);
    }

    /**
     * この列車がstationIDを持つ駅に停車するかどうかかを返す
     * @param stationId
     * @return
     */
    public boolean containsStation(String stationId){
        return false;

    }


}
