package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.*;
import com.kamelong.JPTI.Station;
import org.json.JSONObject;

/**
 * Created by kame on 2017/09/04.
 */
public class Stop extends com.kamelong.JPTI.Stop {
    public Stop(JPTIdata jpti, com.kamelong.JPTI.Station station) {
        super(jpti, station);
    }

    public Stop(JPTIdata jpti, Station station, JSONObject json) {
        super(jpti, station, json);
    }
    public void setName(String value){
        name=value;
    }
    public String getName(){
        return name;
    }
}
