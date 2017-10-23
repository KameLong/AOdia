package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrain;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Route extends com.kamelong.JPTI.Route {

    public Route(JPTIdata jpti) {
        super(jpti);
    }

    public Route(JPTIdata jpti, JSONObject json) {
        super(jpti, json);
    }

    @Override
    protected TrainType newTrainType(JSONObject json) {
        return new TrainType(jpti,this,json);
    }

    @Override
    protected RouteStation newRouteStation(JSONObject json) {
        return new RouteStation(jpti,this,json);
    }

    @Override
    protected Trip newTrip(JSONObject json) {
        return new Trip(jpti,this,json);
    }

    Map<OuDiaTrain,Integer>trainIDmap=new HashMap<>();


    /**
     * OuDiaファイルの路線の一部分から生成する。
     * @param oudia
     */
    public Route(JPTI jpti, OuDiaFile oudia){
        this(jpti);
        int agencyIndex=-1;
        for(int i=0;i<jpti.agency.size();i++){
            if(jpti.getAgency(i).getName().equals("oudia:"+oudia.getLineName())){
                agencyIndex=i;
            }
        }
        if(agencyIndex!=-1){
            agencyID=agencyIndex;
        }else{
            agencyID=jpti.agency.size();
            Agency agency=new Agency(jpti);
            agency.setName("oudia:"+oudia.getLineName());
            jpti.agency.add(agency);
        }
        name=oudia.getLineName();
        type=2;
        for(int i=0;i<oudia.getStationNum();i++){
            RouteStation station=new RouteStation(jpti,this,oudia.getStation(i));
            stationList.add(station);
        }
        for(int i=0;i<oudia.getTypeNum();i++){
            classList.add(new TrainType(jpti,this,oudia.getTrainType(i)));
        }
        int trainID=0;
        for(int diaNum=0;diaNum<oudia.getDiaNum();diaNum++) {
            String diaName=oudia.getDiaName(diaNum);
            int calendarID;
            int calendar=-1;
            for(int i=0;i<jpti.calendarList.size();i++){
                if(jpti.getCalendar(i).getName().equals(diaName)){
                    calendar=i;
                }
            }
            if(calendar!=-1){
                calendarID=calendar;
            }else{
                calendarID=jpti.calendarList.size();
                Calendar newCalendar=new Calendar(jpti);
                newCalendar.setName(diaName);
                jpti.calendarList.add(newCalendar);
            }


            for (int i = 0; i < oudia.getTrainNum(diaNum, 0); i++) {
                OuDiaTrain train = oudia.getTrain(diaNum, 0, i);
                tripList.add(new Trip(train,trainID, 0,jpti.getCalendar(calendarID), oudia,jpti,this));
                trainIDmap.put(train,trainID);
                trainID++;
            }
            for (int i = 0; i < oudia.getTrainNum(diaNum, 1); i++) {
                int useStationNum = 0;
                OuDiaTrain train = oudia.getTrain(diaNum, 1, i);
                tripList.add(new Trip(train,trainID, 1, jpti.getCalendar(calendarID), oudia,jpti,this));
                trainIDmap.put(train,trainID);
                trainID++;
            }
        }
    }
    public int getTrainTypeNum(){
        return classList.size();
    }
    public TrainType getTrainType(int index){
        try {
            return (TrainType)classList.get(index);
        }catch(Exception e){
            e.printStackTrace();
            return new TrainType(jpti,this);
        }
    }
    public int getStationNum(){
        return stationList.size();
    }
    public RouteStation getRouteStation(int index,int direct){
        return (RouteStation) stationList.get(index*(1-2*direct)+direct*(stationList.size()-1));
    }
    public int getTripNum(){
        return tripList.size();
    }
    public Trip getTrip(int index){
        return (Trip)tripList.get(index);
    }
    public Trip getTripByBlockID(int blockID,int direct){
        for(int i=0;i<getTripNum();i++){
            if(getTrip(i).getBlockID()==blockID&&getTrip(i).getDirect()==direct){
                return getTrip(i);
            }
        }
        return null;
    }
    public void addRouteStation(RouteStation newStation){
        stationList.add(newStation);
        setChanged();
        notifyObservers();
        clearChanged();

    }
    public void addRouteStation(int index, RouteStation newStation){
        stationList.add(index,newStation);
        setChanged();
        notifyObservers();
        clearChanged();
    }
    public void removeRouteStation(RouteStation station){
        stationList.remove(station);
        setChanged();
        notifyObservers();
        clearChanged();
    }
    public RouteStation getRouteStation(int index){
        return (RouteStation) stationList.get(index);
    }



}
