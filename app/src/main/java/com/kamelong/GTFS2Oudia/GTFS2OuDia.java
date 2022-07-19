package com.kamelong.GTFS2Oudia;

import com.kamelong.OuDia.*;
import com.kamelong.GTFS.*;

import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * GTFSをoudiaにコンバートするためのクラスです
 */
public class GTFS2OuDia {
    /**
     * using route's ID
     * index=0 :down timetable
     * index=1 :up timetable
     */
    public ArrayList<String>[]routeID=new ArrayList[2];
    public String lineName="";
    private GTFS gtfs;
    private String outputDirectoryPath="";

    /**
     * gtfs:使用GTFS1ファイル
     * outputDirectoryPath:出力するディレクトリ
     * lineName:路線名　兼　ファイル名
     */
    public GTFS2OuDia(GTFS gtfs,String outputDirectoryPath,String lineName){
        this.gtfs=gtfs;
        this.outputDirectoryPath=outputDirectoryPath;
        this.lineName=lineName;
        routeID[0]=new ArrayList<>();
        routeID[1]=new ArrayList<>();
    }
    public void addRouteID(String[] lines){
        routeID[Integer.parseInt(lines[3])].add(lines[0]);
    }
    public void setRouteID(ArrayList<String>[] data){
        routeID=data;
    }
    /**
     * 与えられたrouteIDのリストを用いてoudiaファイルを生成します
     */
    public LineFile makeOudiaFile(){
        //使用する下り列車
        ArrayList<GtfsTrain> downGtfsTrain = new ArrayList<>();
        //使用する上り列車
        ArrayList<GtfsTrain> upGtfsTrain = new ArrayList<>();
        int maxStops = 0;
        GtfsTrain maxTrain = null;
        for (String downRouteID : routeID[0]) {
            for (Trip t : gtfs.route.get(downRouteID).trips.values()) {
                GtfsTrain train = new GtfsTrain(t, 0, gtfs.stop);
                if (train.stopTimes.size() > maxStops) {
                    maxStops = train.stopTimes.size();
                    maxTrain = train;
                }
                downGtfsTrain.add(train);

            }
        }
        for (String upRouteID : routeID[1]) {
            for (Trip t : gtfs.route.get(upRouteID).trips.values()) {
                GtfsTrain train = new GtfsTrain(t, 1, gtfs.stop);
                if (train.stopTimes.size() > maxStops) {
                    maxStops = train.stopTimes.size();
                    maxTrain = train;
                }
                upGtfsTrain.add(train);
            }
        }
        System.out.println(maxStops);
        System.out.println(maxTrain);
        ArrayList<String> stationList = new ArrayList<>();
        for (String s : maxTrain.station) {
            stationList.add(s);
        }

        for (GtfsTrain train : downGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                if (!stationList.subList(pos, stationList.size()).contains(train.station.get(i))) {
                    if (i == 0) {
                        stationList.add(0, train.station.get(i));
                    } else {
                        stationList.add(pos + 1, train.station.get(i));
                    }
                    pos++;
                } else {
                    pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        for (GtfsTrain train : upGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                if (!stationList.subList(pos, stationList.size()).contains(train.station.get(i))) {
                    if (i == 0) {
                        stationList.add(0, train.station.get(i));
                    } else {
                        stationList.add(pos + 1, train.station.get(i));
                    }
                    pos++;
                } else {
                    pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                }

            }
        }
        //stationList完成

        for (GtfsTrain train : downGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        for (GtfsTrain train : upGtfsTrain) {
            int pos = 0;
            for (int i = 0; i < train.station.size(); i++) {
                pos += stationList.subList(pos, stationList.size()).indexOf(train.station.get(i));
                train.stationIndex.add(pos);
            }
        }
        //各駅にindexを割り振ることに成功
        if (false) {
            //デバッグ用
            for (GtfsTrain train : downGtfsTrain) {
                for (int i = 0; i < stationList.size(); i++) {
                    if (train.stationIndex.contains(i)) {
                        String station = train.station.get(train.stationIndex.indexOf(i));
                        System.out.println(gtfs.stop.get(station).stop_name);
                    } else {
                        System.out.println("ㇾ");
                    }
                }
                System.out.println("eeee");
            }
            for (GtfsTrain train : upGtfsTrain) {
                for (int i = 0; i < stationList.size(); i++) {
                    if (train.stationIndex.contains(i)) {
                        String station = train.station.get(train.stationIndex.indexOf(i));
                        System.out.println(gtfs.stop.get(station).stop_name);
                    } else {
                        System.out.println("ㇾ");

                    }
                }
                System.out.println("eeee");
            }
        }

        LineFile lineFile = new LineFile();
        lineFile.name=lineName;
        for (int i = 0; i < stationList.size(); i++) {
            Station station =new Station(lineFile);
            lineFile.addStation(-1,station,false);

            station.name = gtfs.stop.get(stationList.get(i)).stop_name;
            if (i == 0) {
                station.showArrival[1]=true;
                station.showDeparture[1]=false;
                station.showArrivalCustom[1]=true;
                station.showDepartureCustom[1]=false;
            }
            if (i == stationList.size() - 1) {
                station.showArrival[0]=true;
                station.showDeparture[0]=false;
                station.showArrivalCustom[0]=true;
                station.showDepartureCustom[0]=false;
            }
        }
        TrainType type=new TrainType();
        type.name="不明な種別";
        lineFile.trainType.add(type);


        for (GtfsTrain train : downGtfsTrain) {
            if(lineFile.getDiaFromName(train.service_id)==null){
                Diagram diagram =new Diagram(lineFile);
                lineFile.diagram.add(diagram);
                diagram.trains[0] = new ArrayList<>();
                diagram.trains[1] = new ArrayList<>();
                diagram.name= train.service_id;
            }
            lineFile.getDiaFromName(train.service_id).trains[0].add(train.toOuDiaTrain(lineFile, 0));
        }
        for (GtfsTrain train : upGtfsTrain) {
            if(lineFile.getDiaFromName(train.service_id)==null){
                Diagram diagram =new Diagram(lineFile);
                lineFile.diagram.add(diagram);
                diagram.trains[0] = new ArrayList<>();
                diagram.trains[1] = new ArrayList<>();
                diagram.name= train.service_id;
            }
            lineFile.getDiaFromName(train.service_id).trains[1].add(train.toOuDiaTrain(lineFile, 1));
        }
        for(Diagram diagram:lineFile.diagram) {
            diagram.sortTrain(0, 0);

            diagram.sortTrain(1, lineFile.getStationNum() - 1);
        }
        return lineFile;


    }
}
