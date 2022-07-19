package com.kamelong.GTFS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class Route {
    public String route_id;
    public String route_name;
    private String parent_route_id;
    public Map<String,Trip> trips=new HashMap<>();
    public Route(String[] list,int idIndex,int nameIndex,int parentIndex){//,int idColumn,int nameColumn,int parentRouteColumn){
        route_id=list[idIndex];
        route_name=list[nameIndex];

//        parent_route_id=list[parentRouteColumn];
    }
    public void addTrip(Trip mTrip){
        trips.put(mTrip.trip_id,mTrip);
    }
    public void addStopTime(StopTime time){
        for(Trip t :trips.values()){
            if(time.trip_id.equals(t.trip_id)){
                t.addStopTime(time);
            }
        }
    }
    public String getStartStation(){
        HashMap<String,Integer>map=new HashMap<>();
        for(Trip t :trips.values()){
            if(map.keySet().contains(t.stopTimes.get(0).stop_id)){
                map.put(t.stopTimes.get(0).stop_id,map.get(t.stopTimes.get(0).stop_id)+1);
            }else{
                map.put(t.stopTimes.get(0).stop_id,1);
            }
        }
        List<Map.Entry<String,Integer>> list=new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> -o1.getValue()+o2.getValue());
        return list.get(0).getKey();
    }
    public String getEndStation(){
        HashMap<String,Integer>map=new HashMap<>();
        for(Trip t :trips.values()){
            if(map.keySet().contains(t.stopTimes.get(t.stopTimes.size()-1).stop_id)){
                map.put(t.stopTimes.get(t.stopTimes.size()-1).stop_id,map.get(t.stopTimes.get(t.stopTimes.size()-1).stop_id)+1);
            }else{
                map.put(t.stopTimes.get(t.stopTimes.size()-1).stop_id,1);
            }
        }
        List<Map.Entry<String,Integer>> list=new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> -o1.getValue()+o2.getValue());
        return list.get(0).getKey();
    }
}
