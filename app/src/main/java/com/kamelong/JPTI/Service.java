package com.kamelong.JPTI;

import com.kamelong.tool.Color;
import com.kamelong.tool.Font;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 時刻表路線を記録するクラス
 * 時刻表路線はOuDiaファイル１つに対応する
 */
public abstract class Service {
    protected JPTIdata jpti;

    protected  String name="";
    public Map<Route,Integer> route=new LinkedHashMap<>();
    protected int stationWidth=-1;
    protected int trainWidth=-1;
    protected String startTime=null;
    protected int defaulyStationSpace=-1;
    protected String comment=null;

    protected Color diaTextColor=null;
    protected Color diaBackColor=null;
    protected Color diaTrainColor=null;
    protected  Color diaAxisColor=null;
    protected ArrayList<Font> timeTableFont=new ArrayList();
    protected Font timeTableVFont=null;
    protected Font diaStationFont=null;
    protected Font diaTimeFont=null;
    protected Font diaTrainFont=null;
    protected Font commentFont=null;

    protected  static final String NAME="service_name";
    protected  static final String ROUTE="route_array";
    protected  static final String ROUTE_ID="route_id";
    protected  static final String DIRECTION ="direction";
    protected  static final String STATION_WIDTH="station_width";
    protected static final String TRAIN_WIDTH="train_width";
    protected static final String START_TIME="timetable_start_time";
    protected static final String STATION_SPACING="station_spacing";
    protected static final String COMMENT="comment_text";

    protected static final String DIA_TEXT_COLOR="dia_text_color";
    protected static final String DIA_BACK_COLOR="dia_back_color";
    protected static final String DIA_TRAIN_COLOR="dia_train_color";
    protected static final String DIA_AXICS_COLOR="dia_axics_color";
    protected static final String TIMETABLE_FONT="font_timetable";
    protected static final String TIMETABLE_VFONT="font_vfont";
    protected static final String DIA_STATION_FONT="font_dia_station";
    protected static final String DIA_TIME_FONT="font_dia_time";
    protected static final String DIA_TRAIN_FONT="font_dia_train";
    protected static final String COMMENT_FONT="font_comment";

    public Service(JPTIdata jpti){
        this.jpti=jpti;
    }
    public Service(JPTIdata jpti,JSONObject json){
        this(jpti);
        try{

            name=json.optString(NAME,"");
            JSONArray routeArray=json.optJSONArray(ROUTE);
            for(int i=0;i<routeArray.length();i++){
                route.put(jpti.routeList.get(routeArray.getJSONObject(i).optInt(ROUTE_ID,0)),routeArray.getJSONObject(i).optInt(DIRECTION,0));
            }
            stationWidth=json.optInt(STATION_WIDTH,7);
            trainWidth=json.optInt(TRAIN_WIDTH,5);
            startTime=json.optString(START_TIME);
            defaulyStationSpace=json.optInt(START_TIME);
            comment=json.optString(COMMENT);
            diaTextColor=new Color(Long.decode(json.optString(DIA_TEXT_COLOR,"#000000")).intValue());
            diaBackColor=new Color(Long.decode(json.optString(DIA_BACK_COLOR,"#ffffff")).intValue());
            diaTrainColor=new Color(Long.decode(json.optString(DIA_TRAIN_COLOR,"#000000")).intValue());
            diaAxisColor=new Color(Long.decode(json.optString(DIA_AXICS_COLOR,"#000000")).intValue());
            JSONArray timeTableFontArray=json.optJSONArray(TIMETABLE_FONT);
            for(int i=0;i<timeTableFontArray.length();i++){
                timeTableFont.add(new Font(timeTableFontArray.getJSONObject(i)));
            }
            timeTableVFont=new Font(json.getJSONObject(TIMETABLE_VFONT));
            diaStationFont=new Font(json.getJSONObject(DIA_STATION_FONT));
            diaTimeFont=new Font(json.getJSONObject(DIA_TIME_FONT));
            diaTrainFont=new Font(json.getJSONObject(DIA_TRAIN_FONT));
            commentFont=new Font(json.getJSONObject(COMMENT_FONT));


        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(NAME,name);
            JSONArray routeArray=new JSONArray();
            for (Map.Entry<Route, Integer> bar : route.entrySet()) {
                JSONObject routeObject=new JSONObject();
                routeObject.put(ROUTE_ID,bar.getKey().index());
                routeObject.put(DIRECTION,bar.getValue());
                routeArray.put(routeObject);
            }
            json.put(ROUTE,routeArray);
            if(stationWidth>-1){
                json.put(STATION_WIDTH,stationWidth);
            }
            if(trainWidth>-1){
                json.put(TRAIN_WIDTH,trainWidth);
            }
            if(startTime!=null){
                json.put(START_TIME,startTime);
            }
            if(defaulyStationSpace>-1){
                json.put(STATION_SPACING,defaulyStationSpace);
            }
            if(comment!=null){
                json.put(COMMENT,comment);
            }
            if(diaTextColor!=null){
                json.put(DIA_TEXT_COLOR,diaTextColor.getHTMLColor());
            }
            if(diaBackColor!=null){
                json.put(DIA_BACK_COLOR,diaBackColor.getHTMLColor());
            }
            if(diaTrainColor!=null){
                json.put(DIA_TRAIN_COLOR,diaTrainColor.getHTMLColor());
            }
            if(diaAxisColor!=null){
                json.put(DIA_AXICS_COLOR,diaAxisColor.getHTMLColor());
            }
            JSONArray timetableFontArray=new JSONArray();
            for(Font font:timeTableFont){
                timetableFontArray.put(font.makeJSONObject());
            }
            json.put(TIMETABLE_FONT,timetableFontArray);
            if(timeTableVFont!=null){
                json.put(TIMETABLE_VFONT,timeTableVFont.makeJSONObject());
            }
            if(diaStationFont!=null){
                json.put(DIA_STATION_FONT,diaStationFont.makeJSONObject());
            }
            if(diaTimeFont!=null){
                json.put(DIA_TIME_FONT,diaTimeFont.makeJSONObject());
            }
            if(diaTrainFont!=null){
                json.put(DIA_TRAIN_FONT,diaTrainFont.makeJSONObject());
            }
            if(commentFont!=null){
                json.put(COMMENT_FONT,commentFont.makeJSONObject());
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    protected static int timeString2Int(String time){
        int hh=Integer.parseInt(time.split(":",-1)[0]);
        int mm=Integer.parseInt(time.split(":",-1)[1]);
        int ss=Integer.parseInt(time.split(":",-1)[2]);
        return hh*3600+mm*60+ss;
    }
    protected static String timeInt2String(int time){
        int ss=time%60;
        time=time/60;
        int mm=time%60;
        time=time/60;
        int hh=time%24;
        return String.format("%02d",hh)+":"+String.format("%02d",mm)+":"+String.format("%02d",ss);
    }
    /**
     * 路線のRouteのリストを渡す
     * @return
     */
    public ArrayList<Route>getRouteList(int direction){
        ArrayList<Route> result=new ArrayList<>();
        for(Route i:route.keySet()){
            result.add(i);
        }
        if(direction==1){
            Collections.reverse(result);
        }
        return result;
    }

    /**
     * routeIDからそのrouteがservice中の何番目に位置するかを返す
     * @return routeIDが存在しないときは-1を返す
     */
    public int routeIndex(Route mRoute,int direction){
        int result=-1;
        int i=0;
        for(Route id:route.keySet()){
            if(id==mRoute){
                result=i;
                break;
            }
            i++;
        }
        if(result==-1){
            //このrouteIDはservice内に存在しないので-1を返す
            return -1;
        }
        if(direction==1){
            result=route.size()-result-1;
        }
        return result;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(jpti.serviceList.contains(this)) {
            return jpti.serviceList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }
    public String getName(){
        return name;
    }
    public int getTimeTableFontNum(){
        return timeTableFont.size();
    }
    public Font getTimeTableFont(int index){
        return timeTableFont.get(index);
    }
    public Font getTimeTableVFont(){
        return timeTableVFont;
    }
    public Font getDiaStationFont(){
        return diaStationFont;
    }
    public Font getDiaTimeFont(){
        return diaTimeFont;
    }
    public Font getDiaTrainFont(){
        return diaTrainFont;
    }
    public Font getCommentFont(){
        return commentFont;
    }
    public Color getDiaTextColor(){
        return diaTextColor;
    }
    public Color getDiaBackColor(){
        return diaBackColor;
    }
    public Color getDiaTrainColor(){
        return diaTrainColor;
    }
    public Color getDiaAxisColor(){
        return diaAxisColor;
    }


}
