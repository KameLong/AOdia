package com.kamelong.GTFS;

import com.kamelong.tool.LoadCsv;

/**
 * Created by kame on 2017/11/03.
 */

public class GtfsTrips {
    String direction="";
    String routeID="";
    String serviceID="";
    String tripHeadSign="";
    String tripID="";
    public GtfsTrips(LoadCsv csv,int index){
        direction=csv.getData("direction_id",index);
        routeID=csv.getData("route_id",index);
        serviceID=csv.getData("service_id",index);
        tripHeadSign=csv.getData("trip_headsign",index);
        tripID=csv.getData("trip_id",index);
    }

}
