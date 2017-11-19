package com.kamelong.JPTI;


import android.os.Handler;

import com.eclipsesource.json.*;
import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.OuDia.OuDiaStation;
import com.kamelong.OuDia.OuDiaTrain;
import com.kamelong.OuDia.OuDiaTrainType;
import com.kamelong.aodia.AOdiaIO.ProgressDialog;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * JPTI-jsonの1ファイルを扱うためのクラス
 */
public class JPTI {
    private static final String JPTI_VERSION = "JPTI_version";
    private static final String AGENCY = "agency";
    private static final String STATION = "station";
    private static final String ROUTE = "route";
    private static final String CALENDAR = "calendar";
    private static final String SERVICE = "service";
    private static final String OPERATION = "operation";
    private static final String STOP = "stop";
    private static final String TRIP = "trip";
    private static final String TRAINTYPE = "traintype";


    public ArrayList<Agency> agency = new ArrayList<>();
    public ArrayList<Station> stationList = new ArrayList<>();
    public ArrayList<Stop> stopList = new ArrayList<>();
    public ArrayList<Route> routeList = new ArrayList<>();
    public ArrayList<Service> serviceList = new ArrayList<>();
    public ArrayList<Trip> tripList = new ArrayList<>();
    public ArrayList<Calendar> calendarList = new ArrayList<>();
    public ArrayList<Operation> operationList = new ArrayList<>();
    public ArrayList<TrainType> trainTypeList = new ArrayList<>();


    /**
     * 新規作成コンストラクタ
     */
    public JPTI() {
        //空のJPTIdata
    }

    /**
     * ファイルからJPTIを作成する
     *
     * @param file
     */
    public JPTI(File file,Handler handler,final  ProgressDialog dialog) {
        try {
            long time=System.currentTimeMillis();

            System.out.println(System.currentTimeMillis()-time);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            System.out.println(System.currentTimeMillis()-time);
            String str = br.readLine();
            System.out.println(System.currentTimeMillis()-time);

            JsonObject json = Json.parse(new FileReader(file)).asObject();
            System.out.println(System.currentTimeMillis()-time);

            /*
            System.out.println(System.currentTimeMillis()-time);
            FileInputStream input = new FileInputStream(file);
            System.out.println(System.currentTimeMillis()-time);
            int size = input.available();
            byte[] buffer = new byte[size];
            System.out.println(System.currentTimeMillis()-time);
            input.read(buffer);
            input.close();
            System.out.println(System.currentTimeMillis()-time);

            // Json読み込み
            String str = new String(buffer);
            */
            System.out.println(System.currentTimeMillis()-time);


            loadJson(json,handler,dialog);
            System.out.println(System.currentTimeMillis()-time);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * JPTIのJSONオブジェクトから生成するコンストラクタ
     */
    public JPTI(JsonObject json,Handler handler,final  ProgressDialog dialog) {
        loadJson(json,handler,dialog);
    }

    /**
     * OuDiaから生成するコンストラクタ
     * @param oudiaFile
     */
    public JPTI(OuDiaFile oudiaFile){
        Service service=new Service(this);
        service.loadOuDia(oudiaFile);
        serviceList.add(service);
        ArrayList<Integer> borderList=oudiaFile.getBorders();
        int startStation=0;
        for(int border:borderList){
            if(border-startStation>0){
                routeList.add(new Route(this,oudiaFile,startStation,border));
                service.addRoute(routeList.get(routeList.size()-1),0);
                startStation=border;
                if(oudiaFile.getStation(border).border()){
                    startStation++;
                }

            }
        }
        for(int i=0;i<oudiaFile.getTypeNum();i++){
            trainTypeList.add(newTrainType(oudiaFile.getTrainType(i)));
        }
        for(int i=0;i<oudiaFile.getDiaNum();i++){
            Calendar calendar=newCalendar();
            calendar.setName(oudiaFile.getDiaName(i));
            calendarList.add(calendar);
        }

        service.loadOuDia2(oudiaFile);

    }


    private void loadJson(JsonObject json,Handler handler,final  ProgressDialog dialog) {
        try {
            JsonArray agencyArray = json.get(AGENCY).asArray();
            for (int i = 0; i < agencyArray.size(); i++) {
                agency.add(newAgency(agencyArray.get(i)));
            }
        }catch()
        try{
            JSONArray stopArray=json.getJSONArray(STOP);
            for(int i=0;i<stopArray.length();i++){
                stopList.add(newStop(stopArray.getJSONObject(i)));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            JSONArray stationArray = json.getJSONArray(STATION);
            for (int i = 0; i < stationArray.length(); i++) {
                stationList.add(newStation(stationArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }
        try {
            JSONArray routeArray = json.getJSONArray(ROUTE);
            for (int i = 0; i < routeArray.length(); i++) {
                routeList.add(newRoute(routeArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }

        try {
            JSONArray calendarArray = json.getJSONArray(CALENDAR);
            for (int i = 0; i < calendarArray.length(); i++) {
                calendarList.add(newCalendar(calendarArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }
        try {
            JSONArray trainTypeArray = json.getJSONArray(TRAINTYPE);
            for (int i = 0; i < trainTypeArray.length(); i++) {
                trainTypeList.add(newTrainType(trainTypeArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }
        try {
            JSONArray tripArray = json.getJSONArray(TRIP);
            final int size=tripArray.length();
            for (int i = 0; i < tripArray.length(); i++) {
                tripList.add(newTrip(tripArray.getJSONObject(i)));
                if(i%30!=0)continue;
                final int t=i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(t,size+30);
                    }
                });

            }
        } catch (JSONException e) {
        }

        try {
            JSONArray serviceArray = json.getJSONArray(SERVICE);
            for (int i = 0; i < serviceArray.length(); i++) {
                serviceList.add(newService(serviceArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
        }
        try {
            JSONArray operation = json.getJSONArray(OPERATION);
            for (int i = 0; i < operation.length(); i++) {
                operationList.add(newOperation(operation.getJSONObject(i)));
            }
        } catch (Exception e) {

        }


    }

    /**
     * このオブジェクトが持つ時刻データをJSONファイルに書き出す。
     *
     * @param outFile 出力ファイル
     */
    private void makeJSONdata(OutputStreamWriter outFile, Handler handler, final ProgressDialog dialog) {

        //まず、不要データを削除
            /*
        {
            //StationとRouteのうち不要なものを削除
            ArrayList<Station> usedStationList = new ArrayList<>();//使われているStationのリスト
            Iterator<Route> iRoute = routeList.iterator();
            while (iRoute.hasNext()) {
                Route route = iRoute.next();
                if (!route.isUsed()) {
                    iRoute.remove();
                    continue;
                }
                Iterator<RouteStation> iRS = route.stationList.iterator();
                while (iRS.hasNext()) {
                    Station station = iRS.next().station;
                    if (station.name.length() == 0) {
                        iRS.remove();
                    } else {
                        usedStationList.add(station);
                    }
                }
            }
            Iterator<Station> i = stationList.iterator();
            while (i.hasNext()) {
                Station s = i.next();
                if (!usedStationList.contains(s)) {
                    i.remove();
                }
            }
        }
        */
        try {
            JSONObject outJSON = new JSONObject();
            outJSON.put(JPTI_VERSION, "1.0");
            JSONArray agencyArray = new JSONArray();
            for (int i = 0; i < agency.size(); i++) {
                agencyArray.put(agency.get(i).makeJSONObject());
            }
            outJSON.put(AGENCY, agencyArray);
            JSONArray stationArray = new JSONArray();
            for (Station station : stationList) {
                stationArray.put(station.makeJSONObject());
            }
            outJSON.put(STATION, stationArray);
            JSONArray routeArray = new JSONArray();
            for (Route route : routeList) {
                routeArray.put(route.makeJSONObject());
            }
            outJSON.put(ROUTE, routeArray);
            JSONArray stopArray = new JSONArray();
            for (Stop stop : stopList) {
                stopArray.put(stop.makeJSONObject());
            }
            outJSON.put(STOP,stopArray);
            JSONArray calendarArray = new JSONArray();
            for (Calendar calendar : calendarList) {
                calendarArray.put(calendar.makeJSONObject());
            }
            outJSON.put(CALENDAR, calendarArray);
            JSONArray trainTypeArray=new JSONArray();
            for(TrainType trainType:trainTypeList){
                trainTypeArray.put(trainType.makeJSONObject());
            }
            outJSON.put(TRAINTYPE,trainTypeArray);
            JSONArray serviceArray = new JSONArray();
            for (Service service : serviceList) {
                serviceArray.put(service.makeJSONObject());
            }
            outJSON.put(SERVICE, serviceArray);
            JSONArray operationArray = new JSONArray();
            for (Operation operation : operationList) {
                operationArray.put(operation.makeJSONObject());
            }
            outJSON.put(OPERATION, operationArray);
            JSONArray tripArray=new JSONArray();
            int i=0;
            for(Trip trip:tripList){
                i++;
                tripArray.put(trip.makeJSONObject());
                if(i%30!=0)continue;

                final int t=i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(t,getTripSize()+30);
                    }
                });
            }
            outJSON.put(TRIP,tripArray);

            outFile.write(outJSON.toString());
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeJSONdata(File file, Handler handler, ProgressDialog dialog) {
        try {
            this.makeJSONdata(new OutputStreamWriter(new FileOutputStream(file.getPath())),handler,dialog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Agency newAgency(JSONObject json) {
        return new Agency(this, json);
    }
    private Agency newAgency() {
        return new Agency(this);
    }

    private Route newRoute(JSONObject json) {
        return new Route(this, json);
    }
    protected Route newRoute(){
        return new Route(this);
    }

    private Service newService(JSONObject json) {
        return new Service(this, json);
    }
    private Service newService() {
        return new Service(this,routeList);
    }

    private Station newStation(JSONObject json) {
        return new Station(this, json);
    }
    private Station newStation(OuDiaStation s) {
        return new Station(this, s);
    }
    private Station newStation() {
        return new Station(this);
    }

    private Calendar newCalendar(JSONObject json) {
        return new Calendar(this, json);
    }
    protected Calendar newCalendar(){
        return new Calendar(this);
    }
    protected TrainType newTrainType(JSONObject json){
        return new TrainType(this,json);
    }
    protected TrainType newTrainType(){
        return new TrainType(this);
    }
    protected TrainType newTrainType(OuDiaTrainType type){
        return new TrainType(this,type);
    }


    private Operation newOperation(JSONObject json) {
        return new Operation(this, json);
    }

    protected Trip newTrip(JSONObject json) {
        return new Trip(this,  json);
    }
    protected Trip newTrip(Route route, Calendar calendar, OuDiaFile oudia, OuDiaTrain train, int startStation, int endStation, int blockID){
        return new Trip(this,route,calendar,oudia,train,startStation,endStation,blockID);
    }

    protected Trip newTrip(Route route) {
        return new Trip(this, route);
    }

    protected Time newTime(Trip trip, JSONObject json) {
        return new Time(this, trip, json);
    }

    protected Stop newStop(JSONObject json) {
        return new Stop(this,json);
    }
    protected Stop newStop(Station station) {
        return new Stop(this,station);
    }

    public int getAgencySize() {
        return agency.size();
    }

    public int getRouteSize() {
        return routeList.size();
    }

    public int getStationSize() {
        return stationList.size();
    }

    public int getStopSize() {
        return stopList.size();
    }

    public int getTripSize() {
        return tripList.size();
    }

    public int getCalendarSize() {
        return calendarList.size();
    }

    public int getTrainTypeSize() {
        return trainTypeList.size();
    }

    public int getOperationSize() {
        return operationList.size();
    }

    public int getServiceSize() {
        return serviceList.size();
    }

    public Agency getAgency(int index) {
        return agency.get(index);
    }

    public Route getRoute(int index) {
        return routeList.get(index);
    }

    public Station getStation(int index) {
        return stationList.get(index);
    }
    public Station getStation(String name){
        for(Station s:stationList){
            if(s.getName().equals(name)){
                return s;
            }
        }
        return null;
    }

    public Stop getStop(int index) {
        return stopList.get(index);
    }

    public Trip getTrip(int index) {
        return tripList.get(index);
    }

    public Calendar getCalendar(int index) {
        return calendarList.get(index);
    }

    public TrainType getTrainType(int index) {
        try {
            return trainTypeList.get(index);
        }catch(ArrayIndexOutOfBoundsException e){
            return trainTypeList.get(0);
        }
    }

    public Operation getOpetarion(int index) {
        return operationList.get(index);
    }

    public Service getService(int index) {
        return serviceList.get(index);
    }
    public int indexOf(Agency value){
        return agency.indexOf(value);
    }
    public int indexOf(Route value){
        return routeList.indexOf(value);
    }
    public int indexOf(Station value){
        return stationList.indexOf(value);
    }
    public int indexOf(Stop value){
        return stopList.indexOf(value);
    }
    public int indexOf(Trip value){
        return tripList.indexOf(value);
    }
    public int indexOf(Calendar value){
        return calendarList.indexOf(value);
    }
    public int indexOf(Operation value){
        return operationList.indexOf(value);
    }
    public int indexOf(Service value){
        return serviceList.indexOf(value);
    }
    public int indexOf(TrainType type){
        return trainTypeList.indexOf(type);
    }

    public int getAgencyIDByName(String name){
        for(int i=0;i<agency.size();i++){
            if(agency.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
    public Agency addNewAgency(){
        agency.add(newAgency());
        return agency.get(agency.size()-1);
    }
    /**
     * 駅リストから指定された駅名を持つ駅インデックスを返す。
     * 駅が存在しないときは-1が返る
     * @param stationName 指定駅名
     * @return stationsの配列中の何番目が指定駅であるかのインデックス
     */
    public int getStationIDByName(String stationName){
        for(int i=0;i<stationList.size();i++){
            if(stationList.get(i).name.equals(stationName)){
                return i;
            }
        }
        return -1;
    }
    public Route addNewRoute(){
        routeList.add(newRoute());
        return routeList.get(routeList.size()-1);
    }
    public Station addNewStation(OuDiaStation s){
        Station station=newStation(s);
        stationList.add(station);
        return station;
    }
    public Station addNewStation(String name){
        for(Station s:stationList){
            if(s.getName().equals(name)){
                return s;
            }
        }
        Station station=newStation();
        stationList.add(station);
        return station;
    }
    public int getStopIDByName(Station station, String name){
        for(int i=0;i<stopList.size();i++){
            if(getStop(i).getStation()==station&&getStop(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
    public Stop addNewStop(Station station){
        Stop stop=newStop(station);
        stopList.add(stop);
        return stop;
    }
    public Trip addNewTrip(Route route, Calendar calendar, OuDiaFile oudia, OuDiaTrain train, int startStation, int endStation, int blockID){
        Trip trip=newTrip(route,calendar,oudia,train,startStation,endStation,blockID);
        tripList.add(trip);
        return trip;
    }
    public Trip addNewTrip(Route route){
        Trip trip=newTrip(route);
        tripList.add(trip);
        return trip;
    }

    public void resetOperation(){
        operationList=new ArrayList<>();
    }
    public void addOperation(Operation ope){
        operationList.add(ope);
    }
    public Calendar addNewCalendar(){
        Calendar calendar=newCalendar();
        calendarList.add(calendar);
        return calendar;
    }
    public void makeService(){
        Service service=newService();
        serviceList.add(service);
    }
    public TrainType addTrainType(){
        TrainType type=newTrainType();
        trainTypeList.add(type);
        return type;
    }

}
