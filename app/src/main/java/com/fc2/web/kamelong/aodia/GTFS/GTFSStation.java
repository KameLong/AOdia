package com.fc2.web.kamelong.aodia.GTFS;

import com.fc2.web.kamelong.aodia.oudia.Station;

import java.util.ArrayList;


public class GTFSStation extends Station {
    public String stopCode;
    public ArrayList<String> stopID=new ArrayList<>();
    public boolean equals(String str){
        for (String id:stopID) {
            if(str.equals(id)){
                return true;
            }
        }
        return false;
    }

}
