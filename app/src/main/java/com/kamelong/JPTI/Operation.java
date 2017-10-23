package com.kamelong.JPTI;

import com.kamelong.JPTIOuDia.JPTI.JPTI;
import com.kamelong.aodia.SdLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 運用を管理するクラス
 */

public class Operation {
    protected JPTIdata jpti=null;
    protected int calenderID=-1;
    protected int operationNumber=-1;
    protected String operationName="";
    protected ArrayList<Integer> routeID=new ArrayList<>();
    protected ArrayList<Integer> tripID=new ArrayList<>();

    public static final String OPERATION_NO="operation_No";
    public static final String OPERATION_NAME="operation_Name";
    public static final String TRIP_LIST="trip_list";
    public static final String ROUTE_ID="route_id";
    public static final String TRIP_ID="trip_id";
    public static final String CALENDER_ID="calender_id";

    public Operation(){

    }
    public Operation(JPTI jpti, JSONObject json){
        this.jpti=jpti;
        operationName = json.optString(OPERATION_NAME, "");
        operationNumber = json.optInt(OPERATION_NO, -1);
        calenderID=json.optInt(CALENDER_ID,-1);

        JSONArray array = json.optJSONArray(TRIP_LIST);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.optJSONObject(i);
            routeID.add(obj.optInt(ROUTE_ID, -1));
            tripID.add(obj.optInt(TRIP_ID, -1));
        }
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try {
            json.put(OPERATION_NAME, operationName);
            json.put(OPERATION_NO,operationNumber);
            json.put(CALENDER_ID,calenderID);
            JSONArray array=new JSONArray();
            for(int i=0;i<routeID.size();i++){
                JSONObject obj=new JSONObject();
                obj.put(ROUTE_ID,routeID.get(i));
                obj.put(TRIP_ID,tripID.get(i));
                array.put(obj);
            }
            json.put(TRIP_LIST,array);
            return json;
        }catch(JSONException e){
            SdLog.log(e);
        }
        return new JSONObject();
    }
    /**
     * 運用名
     */
    public String getName(){
        return operationName;
    }
    /**
     * 運用番号変更
     */
    public void setNumber(int number){
        this.operationNumber=number;
    }

    /**
     * 運用番号
     * @return
     */
    public int getNumber(){
        return operationNumber;
    }


    public int getTripNum(){
        return tripID.size();
    }
    public int getCalenderID(){
        return calenderID;
    }


}

