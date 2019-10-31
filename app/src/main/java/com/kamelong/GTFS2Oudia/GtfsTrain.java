package com.kamelong.GTFS2Oudia;

import com.kamelong.GTFS.Stop;
import com.kamelong.GTFS.StopTime;
import com.kamelong.GTFS.Trip;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Train;

import java.util.ArrayList;
import java.util.Map;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class GtfsTrain extends Trip {
    public ArrayList<Integer> stationIndex=new ArrayList<>();
    public ArrayList<String> station=new ArrayList<>();

    public GtfsTrain(String[] lines) {
        super(lines);
    }
    public GtfsTrain(Trip trip, int direction, Map<String, Stop> stops){
        this.trip_id=trip.trip_id;
        this.route_id=trip.route_id;
        this.service_id=trip.service_id;
        this.stopTimes=trip.stopTimes;

        if(direction==0){
            for(int i=0;i<stopTimes.size();i++){
                station.add(stops.get(stopTimes.get(i).stop_id).parent_station);
            }
        }else{
            for(int i=0;i<stopTimes.size();i++){
                station.add(stops.get(stopTimes.get(stopTimes.size()-i-1).stop_id).parent_station);
            }

        }
    }
    public int timeString2Int(String time) {
        int result=0;
        try {
            if (time.split(":").length == 3) {
                result += Integer.parseInt(time.split(":")[0]) * 3600;
                result += Integer.parseInt(time.split(":")[1]) * 60;
                result += Integer.parseInt(time.split(":")[2]);
                return result;
            }
            if (time.split(":").length == 2) {
                result += Integer.parseInt(time.split(":")[0]) * 3600;
                result += Integer.parseInt(time.split(":")[1]) * 60;
                return result;
            }
            return -1;
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }


    }

    public Train toOuDiaTrain(LineFile lineFile, int direction){
        Train result=new Train(lineFile,direction);
        for(int i=0;i<lineFile.station.size();i++){
            int station=stationIndex.indexOf(i);
            if(station>=0){
                StopTime stop=stopTimes.get(station*(1-2*direction)+direction*(stopTimes.size()-1));
                result.setStopType(i,1);
                result.setAriTime(i,timeString2Int(stop.arrival_time));
                result.setDepTime(i,timeString2Int(stop.departure_time));
            }else{
                result.setStopType(i,2);
            }
        }
        for(int i=0;i<lineFile.station.size();i++){
            if(result.getStopType(i)==1){
                break;
            }else{
                result.setStopType(i,0);

            }
        }
        for(int i=lineFile.station.size()-1;i>=0;i--){
            if(result.getStopType(i)==1){
                break;
            }else{
                result.setStopType(i,0);

            }
        }
            return result;
    }
}
