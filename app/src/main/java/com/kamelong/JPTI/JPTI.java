package com.kamelong.JPTI;


import android.os.Handler;

import com.eclipsesource.json.*;
import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.OuDia.OuDiaStation;
import com.kamelong.OuDia.OuDiaTrain;
import com.kamelong.OuDia.OuDiaTrainType;
import com.kamelong.aodia.AOdiaIO.ProgressDialog;
import com.kamelong.aodia.SdLog;


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


    public int diagramStartHour =3;


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
                agency.add(newAgency(agencyArray.get(i).asObject()));
            }
        }catch(Exception e){

        }
        try{
            JsonArray stopArray=json.get(STOP).asArray();
            for(int i=0;i<stopArray.size();i++){
                stopList.add(newStop(stopArray.get(i).asObject()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            JsonArray stationArray = json.get(STATION).asArray();
            for (int i = 0; i < stationArray.size(); i++) {
                stationList.add(newStation(stationArray.get(i).asObject()));
            }
        } catch (Exception e) {
        }
        try {
            JsonArray routeArray = json.get(ROUTE).asArray();
            for (int i = 0; i < routeArray.size(); i++) {
                routeList.add(newRoute(routeArray.get(i).asObject()));
            }
        } catch (Exception e) {
        }

        try {
            JsonArray calendarArray = json.get(CALENDAR).asArray();
            for (int i = 0; i < calendarArray.size(); i++) {
                calendarList.add(newCalendar(calendarArray.get(i).asObject()));
            }
        } catch (Exception e) {
        }
        try {
            JsonArray trainTypeArray = json.get(TRAINTYPE).asArray();
            for (int i = 0; i < trainTypeArray.size(); i++) {
                trainTypeList.add(newTrainType(trainTypeArray.get(i).asObject()));
            }
        } catch (Exception e) {
        }
        JsonArray serviceArra = json.get(SERVICE).asArray();
        if(serviceArra.size()>0){
            diagramStartHour =Service.timeString2Int(serviceArra.get(0).asObject().getString("timetable_start_time","300"))/3600;
        }

        try {
            JsonArray tripArray = json.get(TRIP).asArray();
            final int size=tripArray.size();
            for (int i = 0; i < tripArray.size(); i++) {
                tripList.add(newTrip(tripArray.get(i).asObject()));
                if(i%30!=0)continue;
                final int t=i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(t,size+30);
                    }
                });

            }
        } catch (Exception e) {
        }
        try {
            JsonArray serviceArray = json.get(SERVICE).asArray();
            for (int i = 0; i < serviceArray.size(); i++) {
                serviceList.add(newService(serviceArray.get(i).asObject()));
            }
        } catch (Exception e) {
        }

        try {
            JsonArray operation = json.get(OPERATION).asArray();
            for (int i = 0; i < operation.size(); i++) {
                operationList.add(newOperation(operation.get(i).asObject()));
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
            JsonObject outJSON = new JsonObject();
            outJSON.add(JPTI_VERSION, "1.0");
            JsonArray agencyArray = new JsonArray();
            for (int i = 0; i < agency.size(); i++) {
                agencyArray.add(agency.get(i).makeJSONObject());
            }
            outJSON.add(AGENCY, agencyArray);
            JsonArray stationArray = new JsonArray();
            for (Station station : stationList) {
                stationArray.add(station.makeJSONObject());
            }
            outJSON.add(STATION, stationArray);
            JsonArray routeArray = new JsonArray();
            for (Route route : routeList) {
                routeArray.add(route.makeJSONObject());
            }
            outJSON.add(ROUTE, routeArray);
            JsonArray stopArray = new JsonArray();
            for (Stop stop : stopList) {
                stopArray.add(stop.makeJSONObject());
            }
            outJSON.add(STOP,stopArray);
            JsonArray calendarArray = new JsonArray();
            for (Calendar calendar : calendarList) {
                calendarArray.add(calendar.makeJSONObject());
            }
            outJSON.add(CALENDAR, calendarArray);
            JsonArray trainTypeArray=new JsonArray();
            for(TrainType trainType:trainTypeList){
                trainTypeArray.add(trainType.makeJSONObject());
            }
            outJSON.add(TRAINTYPE,trainTypeArray);
            JsonArray serviceArray = new JsonArray();
            for (Service service : serviceList) {
                serviceArray.add(service.makeJSONObject());
            }
            outJSON.add(SERVICE, serviceArray);
            JsonArray operationArray = new JsonArray();
            for (Operation operation : operationList) {
                operationArray.add(operation.makeJSONObject());
            }
            outJSON.add(OPERATION, operationArray);
            JsonArray tripArray=new JsonArray();
            int i=0;
            for(Trip trip:tripList){
                i++;
                tripArray.add(trip.makeJSONObject());
                if(i%30!=0)continue;

                final int t=i;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setProgress(t,getTripSize()+30);
                    }
                });
            }
            outJSON.add(TRIP,tripArray);

            outJSON.writeTo(outFile);
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
        }catch(OutOfMemoryError e){
            SdLog.toast("メモリ不足エラー");
        }
    }

    private Agency newAgency(JsonObject json) {
        return new Agency(this, json);
    }
    private Agency newAgency() {
        return new Agency(this);
    }

    private Route newRoute(JsonObject json) {
        return new Route(this, json);
    }
    protected Route newRoute(){
        return new Route(this);
    }

    private Service newService(JsonObject json) {
        return new Service(this, json);
    }
    private Service newService() {
        return new Service(this,routeList);
    }

    private Station newStation(JsonObject json) {
        return new Station(this, json);
    }
    private Station newStation(OuDiaStation s) {
        return new Station(this, s);
    }
    private Station newStation() {
        return new Station(this);
    }

    private Calendar newCalendar(JsonObject json) {
        return new Calendar(this, json);
    }
    protected Calendar newCalendar(){
        return new Calendar(this);
    }
    protected TrainType newTrainType(JsonObject json){
        return new TrainType(this,json);
    }
    protected TrainType newTrainType(){
        return new TrainType(this);
    }
    protected TrainType newTrainType(OuDiaTrainType type){
        return new TrainType(this,type);
    }


    private Operation newOperation(JsonObject json) {
        return new Operation(this, json);
    }

    protected Trip newTrip(JsonObject json) {
        return new Trip(this,  json);
    }
    protected Trip newTrip(Route route, Calendar calendar, OuDiaFile oudia, OuDiaTrain train, int startStation, int endStation, int blockID){
        return new Trip(this,route,calendar,oudia,train,startStation,endStation,blockID);
    }

    protected Trip newTrip(Route route) {
        return new Trip(this, route);
    }

    protected Time newTime(Trip trip, JsonObject json) {
        return new Time(this, trip, json);
    }

    protected Stop newStop(JsonObject json) {
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
