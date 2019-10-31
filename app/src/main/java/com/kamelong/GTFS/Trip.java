package com.kamelong.GTFS;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class Trip {
    public String route_id;
    public String service_id;
    public String trip_id;
    public ArrayList<StopTime>stopTimes=new ArrayList<>();
    public Trip(){

    }
    public Trip(String[] lines){
        route_id=lines[0];
        service_id=lines[1];
        trip_id=lines[2];
    }
    public void addStopTime(StopTime tIme){
        stopTimes.add(tIme);
    }
}
