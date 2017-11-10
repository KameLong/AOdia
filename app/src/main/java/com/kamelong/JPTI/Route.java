package com.kamelong.JPTI;

import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.tool.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 路線情報を格納するクラス。
 * Routeは枝分かれを許容しない。
 */
public class Route {
    private JPTI jpti;
    /**
     * 所属する法人ID
     */
    private int agencyID=-1;
    /**
     * 内部番号とか
     */
    private int number =-1;
    /**
     * 路線名称
     * ※何とか支線は分けて書く
     * 山陰本線仙崎支線とか
     */
    private String name="";
    /**
     * 路線愛称
     */
    private String nickName="";
    /**
     * 路線の説明
     */
    private String description="";
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
    private int type=-1;
    /**
     * 路線のURL
     */
    private String url="";
    /**
     * 路線カラー
     */
    private Color color=null;
    /**
     * 路線文字色
     */
    private Color textColor=null;
    ArrayList<RouteStation>stationList=new ArrayList<>();


    private static final String AGENCY_ID="agency_id";
    private static final String NO="route_no";
    private static final String NAME="route_name";
    private static final String NICKNAME="route_nickname";
    private static final String DESCRIPTION="route_description";
    private static final String TYPE="route_type";
    private static final String URL="route_url";
    private static final String COLOR="route_color";
    private static final String TEXT_COLOR="route_text_color";
    private static final String STATION="route_station";
    protected static final String CLASS="class";
    protected static final String TRIP="trip";


    public Route(JPTI jpti){
        this.jpti=jpti;
    }
    public Route(JPTI jpti,JSONObject json){
        this(jpti);
        try{
            try{
                agencyID=json.getInt(AGENCY_ID);
            }catch (Exception e){
                System.out.println("Routeに必須項目agency_IDが登録されていません");
                e.printStackTrace();
            }
            number=json.optInt(NO,-1);
            name=json.optString(NAME,"");
            nickName=json.optString(NICKNAME,"");
            description=json.optString(DESCRIPTION,"");
            type=json.optInt(TYPE,2);
            url=json.optString(URL,"");
            try{
                color=new Color(json.getString(COLOR));
            }catch(Exception e){
            }
            try{
                textColor=new Color(json.getString(TEXT_COLOR));
            }catch(Exception e){
            }
            try{
                JSONArray stationArray=json.getJSONArray(STATION);
                for(int i=0;i<stationArray.length();i++){
                    stationList.add(newRouteStation(stationArray.getJSONObject(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (Exception e){

        }
    }


    private RouteStation newRouteStation(JSONObject json){
        return new RouteStation(jpti,this,json);
    }
    /**
     * OuDiaファイルの路線の一部分から生成する。
     * @param oudia
     * @param startStation 開始駅
     * @param endStaton 終了駅
     */
    public Route(JPTI jpti, OuDiaFile oudia, int startStation, int endStaton){
        this(jpti);
        agencyID=jpti.getAgencyIDByName("oudia:"+oudia.getLineName());
        if(agencyID==-1){
            agencyID=jpti.getAgencySize();
            Agency agency=jpti.addNewAgency();
            agency.setName("oudia:"+oudia.getLineName());
        }
        name=oudia.getStation(startStation).getName()+"~"+oudia.getStation(endStaton).getName();
        type=2;
        for(int i=startStation;i<endStaton+1;i++){
            RouteStation station=new RouteStation(jpti,this,oudia.getStation(i));
            if(i==startStation){
                station.setViewStyle(20);
            }
            if(i==endStaton){
                station.setViewStyle(02);
            }
            stationList.add(station);
        }
    }


    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(AGENCY_ID,agencyID);
            if(number >-1){
                json.put(NO, number);
            }
            json.put(NAME,name);
            if(nickName.length()>0){
                json.put(NICKNAME,nickName);
            }
            if(description.length()>0){
                json.put(DESCRIPTION,description);
            }
            if(type>0&&type<13){
                json.put(TYPE,type);
            }
            if(url.length()>0){
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

    public ArrayList<RouteStation>getStationList(int direction){
        if(direction==0){
            return stationList;
        }else{
            ArrayList<RouteStation> result=new ArrayList<>();
            for(RouteStation s:stationList){
                result.add(0,s);
            }
            return result;
        }
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
    public JPTI getJpti(){
        return jpti;
    }

    public void addRouteStation(RouteStation station){
        stationList.add(station);
    }
    public String getName(){
        return name;
    }




}
