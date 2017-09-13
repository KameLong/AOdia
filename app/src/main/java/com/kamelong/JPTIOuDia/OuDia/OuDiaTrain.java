package com.kamelong.JPTIOuDia.OuDia;

import com.kamelong.JPTIOuDia.JPTI.*;
import com.kamelong.OuDia.*;

import java.util.ArrayList;

/**
 * JPTI-OuDia用のOuDiaTrain
 */
public class OuDiaTrain extends com.kamelong.OuDia.OuDiaTrain {
    /**
     * 列車の生成には所属するDiaFileが必要となります。
     *
     * @param diaFile 　呼び出し元のDiaFile
     */
    public OuDiaTrain(com.kamelong.OuDia.OuDiaFile diaFile) {
        super(diaFile);
    }

    /**
     * tripListはServiceのRouteの順にならべておくこと
     * 間にnullが入ってもよい
     * @param diaFile
     * @param service
     * @param tripList
     */
    public OuDiaTrain(OuDiaFile diaFile, Service service, ArrayList<Trip> tripList){
        super(diaFile);
        Station station=null;
        int stationIndex=-1;
        for(int i=0;i<service.getRouteNum();i++){
            for(int j=0;j<service.getRoute(i,0).getStationNum();j++){
                RouteStation routeStation=service.getRoute(i,0).getRouteStation(j,service.getRouteDirect(service.getRoute(i,0)));
                if(station!=null&&station==routeStation.getStation()){

                }else{
                    stationIndex++;
                }
                if(tripList.get(i)!=null) {
                    Time time = tripList.get(i).getTime(routeStation.getStation());
                    if (time != null) {
                            if (time.isStop()) {
                                setStopType(stationIndex, STOP_TYPE_STOP);
                            } else {
                                setStopType(stationIndex, STOP_TYPE_PASS);
                            }
                            setArrivalTime(stationIndex, time.getArrivelTime());
                            setDepartureTime(stationIndex, time.getDepartureTime());


                    }else{
                        setStopType(stationIndex, STOP_TYPE_PASS);

                    }
                }else{
                    if(getStopType(stationIndex)==STOP_TYPE_NOSERVICE) {
                        setStopType(stationIndex, STOP_TYPE_NOVIA);
                    }
                }
                station=routeStation.getStation();
            }
        }
        for(int i=0;i<time.length;i++){
            if(timeExist(i)){
                break;
            }
            setStopType(i,STOP_TYPE_NOSERVICE);
        }
        for(int i=time.length-1;i>=0;i--){
            if(timeExist(i)){
                break;
            }
            setStopType(i,STOP_TYPE_NOSERVICE);
        }
        for(int i=0;i<tripList.size();i++){
            if(tripList.get(i)!=null){

            }
        }




    }
    public String getNumber(){
        return  number;
    }
    public int getType(){
        return type;
    }

    /**
     * valueは秒単位の時刻
     * @param station
     * @param value
     */
    public void setArrivalTime(int station,long value){
        if(value>0&&value<0xFFFFFF) {
            time[station] = time[station] & 0xDFFF000000FFFFFFL;
            time[station] = time[station] | 0x2000000000000000L;
            time[station] = time[station] | (value<<24);
        }else{
            if(value>0xFFFFFF){
                System.out.println(value);
            }
        }

    }
    public void setDepartureTime(int station,long value){
        if(value>0&&value<0xFFFFFF) {
            time[station] = time[station] & 0xEFFFFFFFFF000000L;
            time[station] = time[station] | 0x1000000000000000L;
            time[station] = time[station] | (value);
        }else{
            if(value>0xFFFFFF){
                System.out.println(value);
            }
        }
    }

    /**
     * JPTIのTrainTypeから設定する
     * @param value
     */
    public void setType(TrainType value){
        for(int i=0;i<diaFile.getTypeNum();i++){
            if(((OuDiaFile)diaFile).getTrainType(i).getName().equals(value.getName())){
                type=i;
            }
        }
    }

}
