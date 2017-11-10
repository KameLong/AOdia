package com.kamelong.GTFS;

import com.kamelong.JPTI.Station;
import com.kamelong.JPTI.Stop;
import com.kamelong.tool.LoadCsv;

/**
 * Created by kame on 2017/11/03.
 */

public class GtfsStop {
    String stopID="";
    String name="";
    public GtfsStop(LoadCsv csv,int index){
        stopID=csv.getData("stop_id",index);
        name=csv.getData("stop_name",index);
    }
    public void makeJptiData(Stop stop,Station station){
        station.setName(name);
        station.addStop(stop);
        stop.setName(name);

    }
}
