package com.kamelong.aodia.diadataOld;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Route;
import com.kamelong.JPTI.Service;
import com.kamelong.JPTI.Station;
import com.kamelong.JPTI.Time;
import com.kamelong.JPTI.Train;
import com.kamelong.JPTI.TrainType;
import com.kamelong.JPTI.Trip;
import com.kamelong.aodia.diadata.AOdiaDiaFile;

import java.util.ArrayList;

/**
 * Created by kame on 2017/10/29.
 */

public class AOdiaTrain {
    public static final int NOSERVICE=0;
    public static final int STOP=1;
    public static final int PASS=2;
    public static final int NOVIA=3;

    boolean isused=false;

    Time[] timeList;
    /**
     * 運用として使われるときのみ使われる
     */
    ArrayList<Trip> tripList=new ArrayList<>();
    public int[] stopTypeList;
    Train train;
    AOdiaDiaFile diaFile;
    AOdiaStation station;
    JPTI jpti;
    Service service;
    private AOdiaOperation operation=null;
    public AOdiaTrain(){

        isused=false;
    }
    public void addTrip(Trip trip){
        if(!isused){
            tripList.add(trip);
        }
    }
    public AOdiaTrain(AOdiaDiaFile diaFile,Train train){
        this.diaFile=diaFile;
        this.train=train;
        isused=true;
        timeList=new Time[station.getStationNum()];
        stopTypeList=new int[station.getStationNum()];
        for(int i=0;i<station.getStationNum();i++){
            Route route=station.getRouteByStationIndex(i);
            Trip trip=train.searchTrip(route);
            if(trip==null){
                timeList[i]=null;
                stopTypeList[i]=NOVIA;
            }else{
            timeList[i]=trip.searchTime(station.getStation(i));
            if(timeList[i]==null){
                stopTypeList[i]=NOVIA;
            }else{
                if(timeList[i].isStop()){
                    stopTypeList[i]=STOP;
                }else{
                    stopTypeList[i]=PASS;
                }
            }
            }

        }
        for(int i=0;i<station.getStationNum();i++) {
            if(stopTypeList[i]==NOVIA){
                stopTypeList[i]=NOSERVICE;
            }else{
                break;
            }
        }
        for(int i=station.getStationNum()-1;i>=0;i--) {
            if(stopTypeList[i]==NOVIA){
                stopTypeList[i]=NOSERVICE;
            }else{
                break;
            }
        }
    }
    public boolean isUsed(){
        return isused;
    }
    public Time getTime(int index){
        return timeList[index];
    }
    public int getStopType(int index){
        return stopTypeList[index];
    }
    public TrainType getTrainType(){
        return train.getTrainType();
    }
    public int getCalendarID(){
        return train.getCalendarID();
    }
    public int getDirect(){
        return train.getDirect();
    }
    public String getNumber(){
        return train.getNumber();
    }
    public String getName(){
        return train.getName();
    }
    public String getCount(){
        return train.getCount();
    }
    public String getText(){
        return train.getText();
    }

    public int getRequiredTime(int startStation,int endStation){
        if(timeList[startStation]!=null&&timeList[endStation]!=null){
            if(timeList[startStation].getDATime()!=-1&&timeList[endStation].getADTime()!=-1){
                return timeList[endStation].getADTime()-timeList[startStation].getDATime();
            }
        }
        return -1;
    }
    public int getStartStation(int direct){
        if(direct==0){
            for(int i=0;i<station.getStationNum();i++){
                if(timeList[i]!=null&&timeList[i].getDATime()>=0){
                    return i;
                }
            }
            return -1;
        }else{
            return getEndStation(0);
        }
    }
    public int getStartStation(){
        return getStartStation(getDirect());
    }
    public int getEndStation(int direct){
        if(direct==0) {
            for (int i = station.getStationNum() - 1; i >= 0; i--) {
                if (timeList[i] != null && timeList[i].getDATime() >= 0) {
                    return i;
                }
            }
            return -1;
        }else{
            return getStartStation(0);
        }
    }
    public int getEndStation(){
        return getEndStation(getDirect());
    }
    public int getPredictionTime(Station station){
        for(int i=0;i<this.station.getStationNum();i++){
            if(station==this.station.getStation(i)){
                if(getPredictionTime(i)>=0){
                    return getPredictionTime(i);
                }
            }
        }
        return -1;
    }
    public int getPredictionTime(int index){
        if(stopTypeList[index]==NOSERVICE||stopTypeList[index]==NOVIA){
            return -1;
        }
        if(timeList[index]!=null&&timeList[index].getDATime()>=0){
            return timeList[index].getDATime();
        }
        int startTime=-1;
        int endTime=-1;
        int startStation=-1;
        int endStation=-1;
        for(int i=index;i>=0;i--){
            if(timeList[i]!=null&&timeList[i].getDATime()>0){
                startStation=i;
                if(getDirect()==0){
                    startTime=timeList[i].getDATime();
                }else{
                    startTime=timeList[i].getADTime();
                }
                break;
            }
        }
        for(int i=index;i<station.getStationNum();i++){
            if(timeList[i]!=null&&timeList[i].getDATime()>0){
                endStation=i;
                if(getDirect()==0){
                    endTime=timeList[i].getADTime();
                }else{
                    endTime=timeList[i].getDATime();
                }
                break;
            }
        }
        if(startStation<0||endStation<0){
            return -1;
        }
        int backTime=0;
        int nextTime=0;
        {
            int startRoute = service.getRouteList(0).indexOf(station.routeMap.get(startStation));
            int endRoute = service.getRouteList(0).indexOf(station.routeMap.get(index));
            if (startRoute == endRoute) {
                backTime=station.getStationTime().get(index)-station.getStationTime().get(startStation);

            } else {
                ArrayList<Integer>routePass=new ArrayList<>();
                if (r(routePass, station.connect, startRoute, endRoute)) {
                    backTime += station.getStationTime().get(station.getStationID(station.borderStation.get(startRoute)))-station.getStationTime().get(startStation);
                    for(int i=1;i<routePass.size()-1;i++){
                        backTime+=station.getStationTime().get(station.getStationID(station.borderStation.get(routePass.get(i))))-station.getStationTime().get(station.getStationID(station.borderStation.get(routePass.get(i)-1))+1);
                    }
                    backTime+= station.getStationTime().get(index)-station.getStationTime().get(station.getStationID(station.borderStation.get(endRoute-1))+1);
                } else {
                    return -1;
                }
            }
        }

        {
            int startRoute = service.getRouteList(0).indexOf(station.routeMap.get(index));
            int endRoute = service.getRouteList(0).indexOf(station.routeMap.get(endStation));
            if (startRoute == endRoute) {
                nextTime=station.getStationTime().get(endStation)-station.getStationTime().get(index);

            } else {
                ArrayList<Integer>routePass=new ArrayList<>();
                if (r(routePass, station.connect, startRoute, endRoute)) {
                    nextTime += station.getStationTime().get(station.getStationID(station.borderStation.get(startRoute)))-station.getStationTime().get(index);
                    for(int i=1;i<routePass.size()-1;i++){
                        backTime+=station.getStationTime().get(station.getStationID(station.borderStation.get(routePass.get(i))))-station.getStationTime().get(station.getStationID(station.borderStation.get(routePass.get(i)-1))+1);
                    }
                    nextTime+= station.getStationTime().get(endStation)-station.getStationTime().get(station.getStationID(station.borderStation.get(endRoute-1))+1);
                } else {
                    return -1;
                }
            }
        }
        return (endTime-startTime)*backTime/(nextTime+backTime)+startTime;
    }
    private boolean r(ArrayList<Integer>result,boolean[][] matrix,int start,int goal){
        if(matrix[start][goal]){
            result.add(goal);
            return true;
        }
        for(int i=goal;i>start;i--){
            if(matrix[start][i]){
                result.add(i);
                if(r(result,matrix,i,goal)){
                    return true;
                }else{
                    result.remove(result.size()-1);
                }
            }
        }
        return false;
    }
    public AOdiaDiaFile getDiaFile(){
        return diaFile;
    }
    public void addOperation(AOdiaOperation ope){
        operation=ope;
    }
    public AOdiaOperation getOperation(){
        return operation;
    }

    /**
     * OperationのremoveTrain以外から呼び出すの禁止
     */
    public void removeOperation(){
        operation=null;
    }

    public ArrayList<Trip>getTrips(){
        if(isused){
            return train.getTrip();
        }else{
            return tripList;
        }
    }
    public boolean containTrip(Trip trip){
        return train.getTrip().contains(trip);
    }


}
