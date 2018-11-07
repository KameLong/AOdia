package com.kamelong.GTFS;


import com.kamelong.JPTI.Route;
import com.kamelong.JPTI.RouteStation;
import com.kamelong.tool.LoadCsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kame on 2017/11/03.
 */

public class GtfsRoute{
    String routeID="";
    String routeLongName="";
    String routeShortName="";
    String routeType="";
    public GtfsRoute(LoadCsv csv,int index){
        routeID=csv.getData("route_id",index);
        routeLongName=csv.getData("route_long_name",index);
        routeShortName=csv.getData("route_short_name",index);
        routeType=csv.getData("route_type",index);

    }
    public void makeJptiRoute(Route route,GTFS gtfs){
        int[][] permutation=new int[gtfs.stops.size()][gtfs.stops.size()];
        for(int i=0;i<permutation.length;i++){
            for(int j=0;j<permutation[i].length;j++){
                if(i==j){
                    permutation[i][j]=i;
                }else{
                    permutation[i][j]=0;
                }
            }
        }
        Map<String,Integer>stopID=new HashMap<>();
        Map<String,Integer>tripList=new HashMap<>();
        for(int i=0;i<gtfs.trips.size();i++){
            if(gtfs.trips.get(i).routeID.equals(routeID)){
                tripList.put(gtfs.trips.get(i).tripID,i);
            }
        }
        for(int i=0;i<gtfs.stops.size();i++){
            stopID.put(gtfs.stops.get(i).stopID,i);
        }
        for(int i=0;i<gtfs.stopTime.size()-1;i++){
            if(tripList.containsKey(gtfs.stopTime.get(i).tripID)){
                if(gtfs.stopTime.get(i).tripID.equals(gtfs.stopTime.get(i+1).tripID)){
                    if(gtfs.trips.get(tripList.get(gtfs.stopTime.get(i).tripID)).direction.equals("0")){
                        permutation[stopID.get(gtfs.stopTime.get(i).stopID)][stopID.get(gtfs.stopTime.get(i+1).stopID)]++;
                    }else{
                        permutation[stopID.get(gtfs.stopTime.get(i+1).stopID)][stopID.get(gtfs.stopTime.get(i).stopID)]++;
                    }
                }
            }
        }
        ArrayList<Integer>sortArray=new ArrayList<>();
        for(int i=0;i<permutation.length;i++){
            boolean frag=false;
            for(int j=0;j<permutation.length;j++){
                if(i!=j){
                    if(permutation[i][j]>0){
                        frag=true;
                        break;
                    }
                    if(permutation[j][i]>0){
                        frag=true;
                        break;
                    }
                }
            }
            if(frag){
                sortArray.add(i);
            }
        }
        stops:
        for(int i=0;i<sortArray.size();i++){
            for(RouteStation s:route.getStationList(0)){
                if(s.getStation().getName().equals(gtfs.stops.get(sortArray.get(i)).name)){
                    continue stops;
                }
            }
            RouteStation station=new RouteStation(route.getJpti(),route,route.getJpti().getStation(gtfs.stops.get(sortArray.get(i)).name));
            route.addRouteStation(station);
        }




    }
}