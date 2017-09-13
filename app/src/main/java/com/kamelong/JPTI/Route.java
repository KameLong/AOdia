package com.kamelong.JPTI;

import com.kamelong.tool.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * 路線情報を格納するクラス。
 * Routeは枝分かれを許容しない。
 */
public abstract class Route extends Observable {
    protected JPTIdata jpti;
    /**
     * 所属する法人ID
     */
    protected int agencyID=-1;
    /**
     * 内部番号とか
     */
    protected int number =-1;
    /**
     * 路線名称
     * ※何とか支線は分けて書く
     * 山陰本線仙崎支線とか
     */
    public String name="";
    /**
     * 路線愛称
     */
    protected String nickName=null;
    /**
     * 路線の説明
     */
    protected String description=null;
    /**
     1：高速鉄道（新幹線）
     2：普通鉄道（JR、私鉄等）
     3：地下鉄
     4：路面電車
     5：ケーブルカー
     6：ロープウェイ
     7：高速バス
     8：路線バス
     9：コミュニティバス
     10：フェリー
     11：渡船
     12：飛行機？
     13：その他
     */
    protected int type=-1;
    /**
     * 路線のURL
     */
    protected String url=null;
    /**
     * 路線カラー
     */
    protected Color color=null;
    /**
     * 路線文字色
     */
    protected Color textColor=null;
    protected ArrayList<RouteStation>stationList=new ArrayList<>();
    public ArrayList<TrainType> classList=new ArrayList<>();
    public ArrayList<Trip> tripList=new ArrayList<>();


    protected static final String AGENCY_ID="agency_id";
    protected static final String NO="route_no";
    protected static final String NAME="route_name";
    protected static final String NICKNAME="route_nickname";
    protected static final String DESCRIPTION="route_description";
    protected static final String TYPE="route_type";
    protected static final String URL="route_url";
    protected static final String COLOR="route_color";
    protected static final String TEXT_COLOR="route_text_color";
    protected static final String STATION="route_station";
    protected static final String CLASS="class";
    protected static final String TRIP="trip";

    public Route(JPTIdata jpti){
        this.jpti=jpti;
    }
    public Route(JPTIdata jpti,JSONObject json){
        this(jpti);
        try{
            try{
                agencyID=json.getInt(AGENCY_ID);
            }catch (Exception e){
                System.out.println("Routeに必須項目agency_IDが登録されていません");
                e.printStackTrace();
            }
            number=json.optInt(NO);
            name=json.optString(NAME);
            nickName=json.optString(NICKNAME);
            description=json.optString(DESCRIPTION);
            type=json.optInt(TYPE);
            url=json.optString(URL);
            try{
                color=new Color(json.getString(COLOR));
            }catch(Exception e){
            }
            try{
                textColor=new Color(json.getString(TEXT_COLOR));
            }catch(Exception e){
            }
            try{
                JSONArray classArray=json.getJSONArray(CLASS);
                for(int i=0;i<classArray.length();i++){
                    classList.add(newTrainType(classArray.getJSONObject(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                JSONArray stationArray=json.getJSONArray(STATION);
                for(int i=0;i<stationArray.length();i++){
                    stationList.add(newRouteStation(stationArray.getJSONObject(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                JSONArray tripArray=json.getJSONArray(TRIP);
                for(int i=0;i<tripArray.length();i++){
                    tripList.add(newTrip(tripArray.getJSONObject(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){

        }
    }

    protected abstract TrainType newTrainType(JSONObject json);
    protected abstract RouteStation newRouteStation(JSONObject json);
    protected abstract Trip newTrip(JSONObject json);

    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(AGENCY_ID,agencyID);
            if(number >-1){
                json.put(NO, number);
            }
            json.put(NAME,name);
            if(nickName!=null){
                json.put(NICKNAME,nickName);
            }
            if(description!=null){
                json.put(DESCRIPTION,description);
            }
            if(type>0&&type<13){
                json.put(TYPE,type);
            }
            if(url!=null){
                json.put(URL,url);
            }
            if(color!=null){
                json.put(COLOR,color.getHTMLColor());
            }
            if(textColor!=null){
                json.put(TEXT_COLOR,color.getHTMLColor());
            }
            JSONArray stationArray=new JSONArray();
            for(RouteStation station:stationList){
                stationArray.put(station.makeJSONObject());
            }
            json.put(STATION,stationArray);
            JSONArray classArray=new JSONArray();
            for(TrainType type:classList){
                classArray.put(type.makeJSONObject());
            }
            json.put(CLASS,classArray);
            JSONArray tripArray=new JSONArray();
            for(Trip trip:tripList){
                tripArray.put(trip.makeJSONObject());
            }
            json.put(TRIP,tripArray);

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    /**
     * routeに所属している駅のstatonIDのリスト。
     * @param direction 方向
     * @return
     */
    public ArrayList<Integer>getStationList(int direction){
        ArrayList<Integer>result=new ArrayList<>();
        for(RouteStation station:stationList){
            result.add(station.index());
        }
        if(direction==1){
            Collections.reverse(result);
        }
        return result;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(jpti.routeList.contains(this)) {
            return jpti.routeList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * このObjectが有効な値を持つか
     */
    public boolean isUsed(){
        Iterator<RouteStation>i=stationList.iterator();
        while(i.hasNext()){
            RouteStation station=i.next();
            if(station.station.name.length()==0){
                i.remove();
            }
        }
        return stationList.size()>0;
    }





}
