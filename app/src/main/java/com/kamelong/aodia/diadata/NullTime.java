package com.kamelong.aodia.diadata;

import com.kamelong.JPTI.Station;
import com.kamelong.JPTI.Time;

/**
 * Created by kame on 2017/10/29.
 */

public class NullTime extends Time {
    @Override
    public int getTime(){
        return -1;
    }
    public Station getStation(){
        return null;
    }
    public boolean isStop(){
        return false;
    }
    public int getArrivalTime(){
        return -1;
    }
    public int getDepartureTime(){
        return -1;
    }
    public int getADTime(){
        return -1;
    }
    public int getDATime(){
        return -1;
    }


}
