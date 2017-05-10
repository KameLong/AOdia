package com.fc2.web.kamelong.aodia.GTFS;

import com.fc2.web.kamelong.aodia.oudia.Station;

import java.util.ArrayList;


public class GTFSStation extends Station {
    public String stopCode;
    public ArrayList<String> stopID=new ArrayList<>();
    public boolean equals(Station station){
        return station.getName().equals(getName());
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Station||obj instanceof GTFSStation){
            return equals((Station)obj);
        }
        return super.equals(obj);
    }

}
