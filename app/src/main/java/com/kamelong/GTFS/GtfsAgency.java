package com.kamelong.GTFS;

import com.kamelong.JPTI.Agency;
import com.kamelong.JPTI.JPTI;
import com.kamelong.tool.LoadCsv;

/**
 * Created by kame on 2017/11/03.
 */

public class GtfsAgency {
    String name="";
    public GtfsAgency(LoadCsv csv,int index){
        name=csv.getData("agency_name",index);
    }
    public void makeJptiAgency(Agency agency){
        agency.setName(name);
    }
}
