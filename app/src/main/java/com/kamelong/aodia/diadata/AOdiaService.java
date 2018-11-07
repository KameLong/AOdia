package com.kamelong.aodia.diadata;


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Route;
import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.tool.Color;
import com.kamelong.tool.Font;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 時刻表路線を記録するクラス
 * 時刻表路線はOuDiaファイル１つに対応する
 */
public class AOdiaService {
    private JPTI jpti;

    private String name="";
    private int stationWidth=-1;
    private int trainWidth=-1;
    private String startTime=null;
    private int defaulyStationSpace=-1;
    private String comment=null;

    private Color diaTextColor=null;
    private Color diaBackColor=null;
    private Color diaTrainColor=null;
    private Color diaAxisColor=null;
    private ArrayList<Font> timeTableFont=new ArrayList();
    private Font timeTableVFont=null;
    private Font diaStationFont=null;
    private Font diaTimeFont=null;
    private Font diaTrainFont=null;
    private Font commentFont=null;


    public String dataBaseName="hankyukyoto.db";
    public String dataBaseDirectory="/storage/emulated/0/Android/data/com.kamelong.aodia/files/";
    public ArrayList<Integer>routeID=new ArrayList<>(Arrays.asList(1,2,6,5,4));

    public ArrayList<Integer>direction=new ArrayList<>(Arrays.asList(0,0,0,0,0));
    public ArrayList<Integer>blockID=new ArrayList<>(Arrays.asList(
            1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,897,898,899,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998,999,1000,1001,1002,1003,1004,1005,1006,1007,1624,1625,1626,1627,1628,1629,1630,1631,1632,1633,1634,1635,1636,1637,1638,1639,1640,1641,1642,1643,1644,1645,1646,1647,1648,1649,1650,1651,1652,1653,1654,1655,1656,1657,1658,1659,1660,1661,1662,1663,1664,1665,1666,1667,1668,1669,1670,1671,1672,1673,1674,1675,1676,1677,1678,1679,1680,1681,1682,1683,1684,1685,1686,1687,1688,1689,1690,1691,1692,1693,1694,1695,1696,1697,1698,1699,1700,1701,1702,1703,1704,1705,1706,1707,1708,1709,1710,1711,1712,1713,1714,1715,1716,1717,1718,1719,1720,1721,1722,1723,1724,1725,1726,1727,1998,1999,2000,2001,2002,2003,2004,2005,2006,2007,2008,2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023,2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037,2038,2039,2040,2041,2042,2043,2044,2045,2046,2047,2197,2198,2199,2200,2201));
    //public ArrayList<Integer>blockID=new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12));





    protected static final String NAME="service_name";
    protected static final String ROUTE="route_array";
    private static final String ROUTE_ID="route_id";
    private static final String DIRECTION ="direction";
    private static final String STATION_WIDTH="station_width";
    private static final String TRAIN_WIDTH="train_width";
    private static final String START_TIME="timetable_start_time";
    private static final String STATION_SPACING="station_spacing";
    private static final String COMMENT="comment_text";
    private static final String TRAIN="train";

    private static final String DIA_TEXT_COLOR="dia_text_color";
    private static final String DIA_BACK_COLOR="dia_back_color";
    private static final String DIA_TRAIN_COLOR="dia_train_color";
    private static final String DIA_AXICS_COLOR="dia_axics_color";
    private static final String TIMETABLE_FONT="font_timetable";
    private static final String TIMETABLE_VFONT="font_vfont";
    private static final String DIA_STATION_FONT="font_dia_station";
    private static final String DIA_TIME_FONT="font_dia_time";
    private static final String DIA_TRAIN_FONT="font_dia_train";
    private static final String COMMENT_FONT="font_comment";

    public AOdiaService(){

    }

    public AOdiaService(JPTI jpti){
        this.jpti=jpti;
    }
    public AOdiaService(JPTI jpti, JsonObject json){
        this(jpti);
        try{

            name=json.getString(NAME,"");
            JsonArray routeArray=json.get(ROUTE).asArray();
            for(int i=0;i<routeArray.size();i++){
            }
            stationWidth=json.getInt(STATION_WIDTH,7);
            trainWidth=json.getInt(TRAIN_WIDTH,5);
            startTime=json.getString(START_TIME,"300");

            defaulyStationSpace=json.getInt(STATION_SPACING,60);
            comment=json.getString(COMMENT,"");
            diaTextColor=new Color(Long.decode(json.getString(DIA_TEXT_COLOR,"#000000")).intValue());
            diaBackColor=new Color(Long.decode(json.getString(DIA_BACK_COLOR,"#ffffff")).intValue());
            diaTrainColor=new Color(Long.decode(json.getString(DIA_TRAIN_COLOR,"#000000")).intValue());
            diaAxisColor=new Color(Long.decode(json.getString(DIA_AXICS_COLOR,"#000000")).intValue());
            JsonArray timeTableFontArray=json.get(TIMETABLE_FONT).asArray();
            for(int i=0;i<timeTableFontArray.size();i++){
                timeTableFont.add(new Font(timeTableFontArray.get(i).asObject()));
            }
            timeTableVFont=new Font(json.get(TIMETABLE_VFONT).asObject());
            diaStationFont=new Font(json.get(DIA_STATION_FONT).asObject());
            diaTimeFont=new Font(json.get(DIA_TIME_FONT).asObject());
            diaTrainFont=new Font(json.get(DIA_TRAIN_FONT).asObject());
            commentFont=new Font(json.get(COMMENT_FONT).asObject());

            JsonArray trainArray=json.get(TRAIN).asArray();
            for(int i=0;i<trainArray.size();i++){
            }


        }catch(Exception e){
            e.printStackTrace();

        }
    }
    public AOdiaService(JPTI jpti, ArrayList<Route>routeList){
        this.jpti=jpti;
        for(Route r:routeList){
        }
    }

    public JsonObject makeJSONObject(){
        JsonObject json=new JsonObject();
        try{
            json.add(NAME,name);
            if(stationWidth>-1){
                json.add(STATION_WIDTH,stationWidth);
            }
            if(trainWidth>-1){
                json.add(TRAIN_WIDTH,trainWidth);
            }
            if(startTime!=null){
                json.add(START_TIME,startTime);
            }
            if(defaulyStationSpace>-1){
                json.add(STATION_SPACING,defaulyStationSpace);
            }
            if(comment!=null){
                json.add(COMMENT,comment);
            }
            if(diaTextColor!=null){
                json.add(DIA_TEXT_COLOR,diaTextColor.getHTMLColor());
            }
            if(diaBackColor!=null){
                json.add(DIA_BACK_COLOR,diaBackColor.getHTMLColor());
            }
            if(diaTrainColor!=null){
                json.add(DIA_TRAIN_COLOR,diaTrainColor.getHTMLColor());
            }
            if(diaAxisColor!=null){
                json.add(DIA_AXICS_COLOR,diaAxisColor.getHTMLColor());
            }
            JsonArray timetableFontArray=new JsonArray();
            for(Font font:timeTableFont){
                timetableFontArray.add(font.makeJsonObject());
            }
            json.add(TIMETABLE_FONT,timetableFontArray);
            if(timeTableVFont!=null){
                json.add(TIMETABLE_VFONT,timeTableVFont.makeJsonObject());
            }
            if(diaStationFont!=null){
                json.add(DIA_STATION_FONT,diaStationFont.makeJsonObject());
            }
            if(diaTimeFont!=null){
                json.add(DIA_TIME_FONT,diaTimeFont.makeJsonObject());
            }
            if(diaTrainFont!=null){
                json.add(DIA_TRAIN_FONT,diaTrainFont.makeJsonObject());
            }
            if(commentFont!=null){
                json.add(COMMENT_FONT,commentFont.makeJsonObject());
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public void loadOuDia(OuDiaFile diaFile){
        name=diaFile.getLineName();
        stationWidth=diaFile.getStationNameLength();
        trainWidth=diaFile.getTrainWidth();
        startTime=timeInt2String(diaFile.getStartTime());
        defaulyStationSpace=diaFile.getStationDistanceDefault();
        comment=diaFile.getComment();
        diaTextColor=diaFile.getDiaTextColor();
        diaBackColor=diaFile.getBackGroundColor();
        diaTrainColor=diaFile.getTrainColor();
        diaAxisColor=diaFile.getAxisColor();
        timeTableFont=diaFile.getTableFont();
        timeTableVFont=diaFile.getVfont();
        diaStationFont=diaFile.getStationFont();
        diaTimeFont=diaFile.getDiaTimeFont();
        diaTrainFont=diaFile.getDiaTextFont();
        commentFont=diaFile.getCommnetFont();
    }
    public void loadOuDia2(OuDiaFile diaFile){
        int blockID=0;
        for(int diaNum=0;diaNum<diaFile.getDiaNum();diaNum++){
            for(int i=0;i<diaFile.getTrainNum(diaNum,0);i++){
//                trainList.add(new Train(jpti,this,jpti.getCalendar(diaNum),diaFile,diaFile.getTrain(diaNum,0,i),blockID));
                blockID++;
            }
            for(int i=0;i<diaFile.getTrainNum(diaNum,1);i++){
//                trainList.add(new Train(jpti,this,jpti.getCalendar(diaNum),diaFile,diaFile.getTrain(diaNum,1,i),blockID));
                blockID++;
            }
        }

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
        if(direction==1){
            Collections.reverse(result);
        }
        return result;
    }

    /**
     * routeIDからそのrouteがservice中の何番目に位置するかを返す
     * @return routeIDが存在しないときは-1を返す
     */
    public int routeIndex(Route mRoute, int direction){
        int result=-1;
        int i=0;
        if(result==-1){
            //このrouteIDはservice内に存在しないので-1を返す
            return -1;
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
        try {
            return timeTableFont.get(index);
        }catch(ArrayIndexOutOfBoundsException e){
            return new Font();
        }
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

    public String getComment(){
        return comment;
    }
    public int getDiagramStartTime(){
        if(startTime==null){
            return 60*60*3;
        }
        return (timeString2Int(startTime))/3600*3600;
    }




}
