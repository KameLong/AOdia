package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * JPTI-OuDia用のJPTIクラス
 * OuDiaとの変換機能を実装している
 */
public class JPTI extends JPTIdata {
    /**
     * 新規作成コンストラクタ
     */
    public JPTI() {
        super();
    }
    /**
     * JPTIのJSONオブジェクトから生成するコンストラクタ
     */
    public JPTI(JSONObject json) {
        super(json);
    }
    public JPTI(File file){
        super(file);
    }
    public JPTI(JSONObject json,File file) {
    super(json,file);
    }
    /**
     * OuDiaから生成するコンストラクタ
     * @param oudiaFile
     */
    public JPTI(OuDiaFile oudiaFile){
        Service service=new Service(this);
        service.loadOuDia(oudiaFile);
        serviceList.add(service);
        Route route=new Route(this,oudiaFile);
        service.addRoute(route);
        routeList.add(route);

        for(int diaNum=0;diaNum<oudiaFile.getDiaNum();diaNum++){
            for(int i=0;i<oudiaFile.getOperationNum(diaNum);i++){
                operationList.add(oudiaFile.getOperation(diaNum,i));
                oudiaFile.getOperation(diaNum,i).move2JPTI(diaNum,getRoute(0).trainIDmap);
            }
        }
    }
    @Override
    protected Agency newAgency(JSONObject json){
        return new Agency(this,json);
    }

    @Override
    protected com.kamelong.JPTI.Route newRoute(JSONObject json) {
        return new Route(this,json);
    }

    @Override
    protected com.kamelong.JPTI.Service newService(JSONObject json) {
        return new Service(this,json);
    }

    @Override
    protected Station newStation(JSONObject json) {
        return new Station(this,json);
    }

    @Override
    protected Calendar newCalendar(JSONObject json){
        return new Calendar(this,json);

    }

    @Override
    protected com.kamelong.JPTI.Operation newOperation(JSONObject json) {
        return new Operation(this,json);
    }

    public Agency getAgency(int index){
        try{
            return (Agency) agency.get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new Agency(this);
        }
    }
    public int getAgencyNum(){
        return agency.size();
    }
    public Calendar getCalendar(int index){
        return (Calendar)calendarList.get(index);
    }
    public Station getStation(int index){
        return (Station)stationList.get(index);
    }
    public Service getService(int index){
        return (Service) serviceList.get(index);
    }
    public int getCalenderNum(){
        return calendarList.size();
    }
    public Route getRoute(int index){
        return (Route)routeList.get(index);
    }

}
