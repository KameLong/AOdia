package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import org.json.JSONObject;

public class Calendar extends com.kamelong.JPTI.Calendar {
    public Calendar(JPTIdata jpti) {
        super(jpti);
    }

    public Calendar(JPTIdata jpti, JSONObject json) {
        super(jpti, json);
    }
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }

}
