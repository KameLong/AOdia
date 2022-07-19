package com.kamelong.GTFS;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * GTFSの１フォルダを読み込むためのクラス
 */
public class GTFS {
    public String filePath;
    public Map<String ,Route> route=new HashMap<>();
    public Map<String ,Stop> stop=new HashMap<>();
    public Map<String ,Calendar> calendar=new HashMap<>();
    public GTFS(String path) throws FileNotFoundException {
        filePath = path;


        //ファイル存在チェック
        System.out.println(filePath + "/routes.txt");

        if (!new File(filePath + "/routes.txt").exists()) {
            throw new FileNotFoundException("routes.txt");
        }
        if (!new File(filePath + "/stops.txt").exists()) {
            throw new FileNotFoundException("stops.txt");
        }
        if (!new File(filePath + "/trips.txt").exists()) {
            throw new FileNotFoundException("trips.txt");
        }
        if (!new File(filePath + "/stop_times.txt").exists()) {
            throw new FileNotFoundException("stop_times.txt");
        }
        if (!new File(filePath + "/calendar.txt").exists()) {
            throw new FileNotFoundException("calendar.txt");
        }

        //ファイル読み込み開始

        try {
            BufferedReader routeFile = new BufferedReader(new FileReader(new File(filePath + "/routes.txt")));
            String str = routeFile.readLine();
            {
                List<String> strList= Arrays.asList(str.split(","));
                int idColumn=0;
                int nameColumn=strList.indexOf("route_long_name");
                int parentRouteColumn=strList.indexOf("jp_parent_route_id");

                str = routeFile.readLine();
                while (str != null) {
                    Route mroute = new Route(str.split(","),idColumn,nameColumn,parentRouteColumn);
                    route.put(mroute.route_id, mroute);
                    str = routeFile.readLine();
                }
            }
            BufferedReader stopsFile = new BufferedReader(new FileReader(new File(filePath + "/stops.txt")));
            {
                str = stopsFile.readLine();
                List<String> strList= Arrays.asList(str.split(","));
                int idColumn=0;
                int nameColumn=strList.indexOf("stop_name");
                int parentColumn=strList.indexOf("parent_station");
                str = stopsFile.readLine();
                while (str != null) {
                    Stop mstop = new Stop(str.split(",",-1),idColumn,nameColumn,parentColumn);
                    stop.put(mstop.stop_id, mstop);
                    str = stopsFile.readLine();
                }
            }
            BufferedReader calendarFile = new BufferedReader(new FileReader(new File(filePath + "/calendar.txt")));
            str = calendarFile.readLine();
            str = calendarFile.readLine();
            while (str != null) {
                Calendar mCalendar = new Calendar(str.split(","));
                calendar.put(mCalendar.service_id,mCalendar);
                str =calendarFile.readLine();
            }
            BufferedReader tripsFile = new BufferedReader(new FileReader(new File(filePath + "/trips.txt")));
            str = tripsFile.readLine();
            str = tripsFile.readLine();
            while (str != null) {
                Trip mTrip = new Trip(str.split(","));
                route.get(mTrip.route_id).addTrip(mTrip);
                str = tripsFile.readLine();
            }
            BufferedReader stopTimesFile = new BufferedReader(new FileReader(new File(filePath + "/stop_times.txt")));
            str = stopTimesFile.readLine();
            str = stopTimesFile.readLine();
            while (str != null) {
                StopTime mStopTime = new StopTime(str.split(","));
                for(Route r : route.values()){
                    r.addStopTime(mStopTime);
                }
                str = stopTimesFile.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

