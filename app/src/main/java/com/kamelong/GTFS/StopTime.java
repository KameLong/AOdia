package com.kamelong.GTFS;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class StopTime {
    public String trip_id;
    public String arrival_time;
    public String departure_time;
    public String stop_id;
    public int stop_sequence;
    public StopTime(String[] lines){
        trip_id=lines[0];
        arrival_time=lines[1];
        departure_time=lines[2];
        stop_id=lines[3];
        stop_sequence=Integer.parseInt(lines[4]);
    }
}
