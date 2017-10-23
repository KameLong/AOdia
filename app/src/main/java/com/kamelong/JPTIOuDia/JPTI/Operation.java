package com.kamelong.JPTIOuDia.JPTI;

import org.json.JSONObject;

/**
 * Created by kame on 2017/10/22.
 */

public class Operation extends com.kamelong.JPTI.Operation {
    public Operation() {
        super();
    }

    public Operation(JPTI jpti,JSONObject json) {
        super(jpti,json);
    }
    public int getCalenderID(){
        return calenderID;
    }
    public Trip getTrip(int index){
        return ((Route)jpti.routeList.get(0)).getTrip(tripID.get(index));
    }
    public int getBlockID(int index){
        return tripID.get(index);
    }

}
