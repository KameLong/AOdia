package com.fc2.web.kamelong.aodia.GTMF;

import com.fc2.web.kamelong.aodia.oudia.Station;

import java.util.ArrayList;


public class GTMFStation extends Station {
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
