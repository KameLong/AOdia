package com.kamelong.JPTIOuDia.OuDia;

import com.kamelong.JPTIOuDia.JPTI.*;
import com.kamelong.tool.Color;
import com.kamelong.tool.Font;

import java.io.File;
import java.util.ArrayList;

/**
 * OuDia-JPTI用OuDiaファイル
 */
public class OuDiaFile extends com.kamelong.OuDia.OuDiaFile{
    public OuDiaFile(){
    }

    public OuDiaFile(File file) {
        super(file);
    }
    public OuDiaFile(JPTI jpti){
        this(jpti,jpti.getService(0));
    }

    public OuDiaFile(JPTI jpti, Service service){
        super();
        //fileType
        fileType="OuDia.1.02";
        lineName=service.getName();
        //まず駅一覧、種別を作る
        Station station=null;
        for(int i=0;i<service.getRouteNum();i++){
            for(int j=0;j<service.getRoute(i,0).getStationNum();j++){
                RouteStation routeStation=service.getRoute(i,0).getRouteStation(j,service.getRouteDirect(service.getRoute(i,0)));

/*                if(station==routeStation.getStation()){
                    this.getStation(this.station.size()-1).setTimeShow(OuDiaStation.SHOW_HATUTYAKU);

                }else{
                    if(j==0&&i!=0){
                        this.getStation(this.station.size()-1).setBorder(true);
                    }
                    this.station.add(new OuDiaStation(routeStation));
                }
                */
                station=routeStation.getStation();
                this.station.add(newOuDiaStation(routeStation));
            }
            for(int j=0;j<service.getRoute(i,0).getTrainTypeNum();j++){
                boolean existTrainType=false;
                for(int k=0;k<trainType.size();k++){
                    if(getTrainType(k).compare(service.getRoute(i,0).getTrainType(j))){
                        existTrainType=true;
                        break;
                    }
                }
                if(!existTrainType) {
                    trainType.add(newOuDiaTrainType(service.getRoute(i, 0).getTrainType(j)));
                }

            }
        }

        for(int diaNum=0;diaNum<jpti.getCalenderNum();diaNum++) {
            ArrayList<? extends com.kamelong.OuDia.OuDiaTrain>[] diaTrain = new ArrayList[2];
            for (int direct = 0; direct < 2; direct++) {
                ArrayList<Integer> useBlockID = new ArrayList<>();
                ArrayList<OuDiaTrain> trains = new ArrayList<>();
                for (int i = 0; i < service.getRouteNum(); i++) {
                    Route route = service.getRoute(i, 0);
                    for (int j = 0; j < route.getTripNum(); j++) {
                        Trip trip = route.getTrip(j);
                        if (trip.getCalender().index() == diaNum &&trip.getDirect()==(direct+service.getRouteDirect(service.getRoute(i, direct)))%2&& !useBlockID.contains(trip.getBlockID())) {
                            ArrayList<Trip> trips = new ArrayList<>();
                            for (int k = 0; k < service.getRouteNum(); k++) {
                                trips.add(service.getRoute(k, 0).getTripByBlockID(trip.getBlockID(),(direct+service.getRouteDirect(service.getRoute(k, direct)))%2));
                            }
                            OuDiaTrain newTrain=newOuDiaTrain(service,trips);
                            newTrain.setType(trip.getType());
                            newTrain.setName(trip.getName());
                            newTrain.setNumber(trip.getNumber());

                            trains.add(newTrain);
                            useBlockID.add(trip.getBlockID());
                        }

                    }
                }
                diaTrain[direct]=trains;
            }
            train.add(diaTrain);
            diaName.add(jpti.getCalendar(diaNum).getName());
        }
            //フォントを作る
            for(int i=0;i<6;i++){
                if(service.getTimeTableFontNum()>i) {
                    jikokuhyouFont.add(service.getTimeTableFont(i));
                }else{
                    jikokuhyouFont.add(Font.OUDIA_DEFAULT);
                }
            }

        if(service.getTimeTableVFont()!=null){
            jikokuVFont =service.getTimeTableVFont();
        }
        if(service.getDiaStationFont()!=null){
            diaEkimeiFont =service.getDiaStationFont();
        }
        if(service.getDiaTimeFont()!=null){
            diaJikokuFont =service.getDiaTimeFont();
        }
        if(service.getDiaTrainFont()!=null){
            diaRessyaFont =service.getDiaTrainFont();
        }
        if(service.getCommentFont()!=null){
            commentFont =service.getCommentFont();
        }
        if(service.getDiaTextColor()!=null){
            diaMojiColor=service.getDiaTextColor();
        }
        if(service.getDiaBackColor()!=null){
            diaHaikeiColor=service.getDiaBackColor();
        }
        if(service.getDiaTrainColor()!=null){
            diaResyaColor=service.getDiaTrainColor();
        }
        if(service.getDiaAxisColor()!=null){
            diaJikuColor=service.getDiaAxisColor();
        }

    }
    @Override
    protected OuDiaStation newStation(){

        return new OuDiaStation();
    }
    @Override
    protected OuDiaTrain newTrain(){
        return new OuDiaTrain(this);
    }
    @Override
    protected OuDiaTrainType newTrainType(){
        return new OuDiaTrainType();
    }
    public String getLineName(){
        return lineName;
    }
    public int getStationNameLength(){
        return stationNameLength;
    }
    public int getTrainWidth(){
        return trainWidth;
    }
    public int getStartTime(){
        return diagramStartTime;
    }
    public int getStationDistanceDefault(){
        return zahyouKyoriDefault;
    }
    public String getComment(){
        return comment;
    }
    public Color getDiaTextColor(){
        return diaMojiColor;
    }
    public Color getBackGroundColor(){
        return diaHaikeiColor;
    }
    public Color getTrainColor(){
        return diaResyaColor;
    }
    public Color getAxisColor(){
        return diaJikuColor;
    }
    public ArrayList<Font> getTableFont(){
        return jikokuhyouFont;
    }
    public Font getVfont(){
        return jikokuVFont;
    }
    public Font getStationFont(){
        return diaEkimeiFont;
    }
    public Font getDiaTimeFont(){
        return diaJikokuFont;
    }
    public Font getCommnetFont(){
        return commentFont;
    }
    public Font getDiaTextFont(){
        return diaRessyaFont;
    }
    public OuDiaStation getStation(int index){
        try{
            return (OuDiaStation) station.get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new OuDiaStation();
        }
    }
    @Override
    public int getStationNum(){
        return station.size();
    }
    public int getTrainTypeNum(){
        return trainType.size();
    }
    public OuDiaTrainType getTrainType(int index){
        try{
            return (OuDiaTrainType)trainType.get(index);
        }catch (Exception e){
            return new OuDiaTrainType();
        }
    }
    public int getDiaNum(){
        return diaName.size();
    }
    public String getDiaName(int index){
        try{
            return diaName.get(index);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
    public int getTrainNum(int dia,int direction){
        return train.get(dia)[direction].size();
    }
    public OuDiaTrain getTrain(int dia,int direction,int index){
        try {
            return (OuDiaTrain) train.get(dia)[direction].get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new OuDiaTrain(this);
        }

    }
    protected OuDiaTrain newOuDiaTrain(Service service,ArrayList<Trip> trips){
        return new OuDiaTrain(this,service, trips);
    }
    protected OuDiaStation newOuDiaStation(RouteStation station){
        return new OuDiaStation(station);
    }
    protected OuDiaTrainType newOuDiaTrainType(TrainType type){
        return new OuDiaTrainType(type);
    }



}
