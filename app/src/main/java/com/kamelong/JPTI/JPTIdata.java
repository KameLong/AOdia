package com.kamelong.JPTI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * JPTI-jsonの1ファイルを扱うためのクラス
 */
public abstract class JPTIdata {
    protected static final String JPTI_VERSION="JPTI_version";
    protected static final String AGENCY="agency";
    protected static final String STATION="station";
    protected static final String ROUTE="route";
    protected static final String CALENDAR="calendar";
    protected static final String SERVICE="service";

    public ArrayList<Agency> agency=new ArrayList<>();
    public ArrayList<Station> stationList=new ArrayList<>();
    public ArrayList<Route> routeList=new ArrayList<>();
    public ArrayList<Calendar> calendarList=new ArrayList<>();
    public ArrayList<Service>serviceList=new ArrayList<>();

    /**
     * 新規作成コンストラクタ
     */
    public JPTIdata() {
        //空のJPTIdata
        
    }
    public JPTIdata(File file){
        this(null,file);
    }
    /**
     * JPTIのJSONオブジェクトから生成するコンストラクタ
     */
    public JPTIdata(JSONObject json){
        this(json,null);
    }
    public JPTIdata(JSONObject json,File file){
        if(json==null){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                String str=br.readLine();
                StringBuilder builder=new StringBuilder();
                while(str!=null){
                    builder.append(str);
                    str=br.readLine();
                }
                json=new JSONObject(builder.toString());

            }catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }
        try{
            JSONArray agencyArray=json.getJSONArray(AGENCY);
            for(int i=0;i<agencyArray.length();i++) {
                agency.add(newAgency(agencyArray.getJSONObject(i)));
            }
        }catch(JSONException e){
        }
        try{
            JSONArray stationArray=json.getJSONArray(STATION);
            for(int i=0;i<stationArray.length();i++) {
                stationList.add(newStation(stationArray.getJSONObject(i)));
            }
        }catch(JSONException e){
        }
        try{
            JSONArray calendarArray=json.getJSONArray(CALENDAR);
            for(int i=0;i<calendarArray.length();i++) {
                calendarList.add(newCalendar(calendarArray.getJSONObject(i)));
            }
        }catch(JSONException e){
        }
        try{
            JSONArray routeArray=json.getJSONArray(ROUTE);
            for(int i=0;i<routeArray.length();i++) {
                routeList.add(newRoute(routeArray.getJSONObject(i)));
            }
        }catch(JSONException e){
        }
        try{
            JSONArray serviceArray=json.getJSONArray(SERVICE);
            for(int i=0;i<serviceArray.length();i++) {
                serviceList.add(newService(serviceArray.getJSONObject(i)));
            }
        }catch(JSONException e){
        }




    }
    /**
     * このオブジェクトが持つ時刻データをJSONファイルに書き出す。
     * @param outFile 出力ファイル
     */
    public void makeJSONdata(OutputStreamWriter outFile){
        //まず、不要データを削除
        {
            //StationとRouteのうち不要なものを削除
            ArrayList<Station> usedStationList=new ArrayList<>();//使われているStationのリスト
            Iterator<Route> iRoute=routeList.iterator();
            while(iRoute.hasNext()){
                Route route=iRoute.next();
                if(!route.isUsed()){
                    iRoute.remove();
                    continue;
                }
                Iterator<RouteStation>iRS=route.stationList.iterator();
                while(iRS.hasNext()){
                    Station station= iRS.next().station;
                    if(station.name.length()==0){
                        iRS.remove();
                    }else{
                        usedStationList.add(station);
                    }
                }
            }
            Iterator<Station> i=stationList.iterator();
            while(i.hasNext()){
                Station s=i.next();
                if(!usedStationList.contains(s)){
                    i.remove();
                }
            }
        }
        try {
            JSONObject outJSON = new JSONObject();
            outJSON.put(JPTI_VERSION,"0.2");
            JSONArray agencyArray = new JSONArray();
            for(int i=0;i<agency.size();i++){
                agencyArray.put(agency.get(i).makeJSONObject());
            }
            outJSON.put(AGENCY,agencyArray);
            JSONArray stationArray=new JSONArray();
                for(Station station:stationList){
                    stationArray.put(station.makeJSONObject());
                }
            outJSON.put(STATION,stationArray);
            JSONArray routeArray=new JSONArray();
            for(Route route:routeList){
                routeArray.put(route.makeJSONObject());
            }
            outJSON.put(ROUTE,routeArray);
            JSONArray calendarArray=new JSONArray();
            for(Calendar calendar:calendarList){
                calendarArray.put(calendar.makeJSONObject());
            }
            outJSON.put(CALENDAR,calendarArray);
            JSONArray serviceArray=new JSONArray();
            for(Service service:serviceList){
                serviceArray.put(service.makeJSONObject());
            }
            outJSON.put(SERVICE,serviceArray);
            outFile.write(outJSON.toString());
            outFile.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void makeJSONdata(File file){
        try{
         this.makeJSONdata(new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    protected abstract Agency newAgency(JSONObject json);
    protected abstract Route newRoute(JSONObject json);
    protected abstract Service newService(JSONObject json);
    protected abstract Station newStation(JSONObject json);
    protected abstract Calendar newCalendar(JSONObject json);
}
