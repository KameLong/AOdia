package com.kamelong.GTFS;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
public class Stop {
    public String stop_id;
    public String stop_name;
    public String parent_station;
    public Stop(String[] line,int idIndex,int nameIndex,int parentIndex){
        stop_id=line[idIndex];
        stop_name=line[nameIndex];
        if(parentIndex>=0){
            parent_station=line[parentIndex];
        }else{

            parent_station=stop_id;
        }

    }
}
