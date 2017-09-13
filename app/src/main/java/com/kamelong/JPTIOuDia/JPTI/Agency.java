package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import org.json.JSONObject;

public class Agency extends com.kamelong.JPTI.Agency {
    public Agency(JPTIdata jpti) {
        super(jpti);
    }

    public Agency(JPTIdata jpti, JSONObject json) {
        super(jpti, json);
    }
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }
}
