package com.kamelong.GTFS;


import com.kamelong.JPTI.Time;
import com.kamelong.tool.LoadCsv;

public class GtfsStopTime {
    String arrivalTime;
    String departureTime;
    String stopID;
    String tripID;
    String stopSequence;
    public GtfsStopTime(LoadCsv csv,int index){
        arrivalTime=csv.getData("arrival_time",index);
        departureTime=csv.getData("departure_time",index);
        stopID=csv.getData("stop_id",index);
        tripID=csv.getData("trip_id",index);
        stopSequence=csv.getData("stop_sequence",index);

    }
    public Time getTime(Time time){
        time.setArrivalTime(arrivalTime);
        time.setDepartureTime(departureTime);
        time.setStop(true);
        return time;
    }

}
