package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import org.json.JSONObject;

import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * Created by kame on 2017/09/04.
 */
public class Station extends com.kamelong.JPTI.Station {
    public Station(JPTIdata jpti) {
        super(jpti);
    }

    public Station(com.kamelong.JPTI.Station oldStation) {
        super(oldStation);
    }

    public Station(JPTIdata jpti, JSONObject json) {
        super(jpti, json);
    }

    @Override
    protected com.kamelong.JPTI.Stop newStop(JSONObject json) {
        return new Stop(jpti,this,json);
    }

    /**
     * OuDia形式の駅から作成する場合
     */
    public Station(JPTI jpti,com.kamelong.JPTIOuDia.OuDia.OuDiaStation oudiaStation){
        this(jpti);
        name=oudiaStation.getName();
        int oudiaIndex=findOuDiaStation();
        if(oudiaIndex==-1) {
            Stop stop=new Stop(jpti,this);
            stop.setName("FromOuDia");
            stops.add(stop);
        }

    }
    public Stop getStop(int index){
        return (Stop)stops.get(index);
    }
    public int findOuDiaStation(){
        for(int i=0;i<stops.size();i++){
            if(getStop(i).getName().equals("FromOuDia")){
                return i;
            }
        }
        return -1;
    }
    public String getName(){
        return name;
    }

}
