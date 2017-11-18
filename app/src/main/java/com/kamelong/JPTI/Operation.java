package com.kamelong.JPTI;


import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.kamelong.aodia.SdLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 運用を管理するクラス
 */

public class Operation {
    private JPTI jpti = null;
    private int calenderID = -1;
    private int operationNumber = -1;
    private String operationName = "";
    private ArrayList<Trip> trip = new ArrayList<>();

    private static final String OPERATION_NO = "operation_No";
    private static final String OPERATION_NAME = "operation_Name";
    private static final String TRIP_LIST = "trip_list";
    private static final String TRIP_ID = "trip_id";
    private static final String CALENDER_ID = "calender_id";

    public Operation() {

    }
    public Operation(JPTI jpti){
        this.jpti=jpti;

    }

    public Operation(JPTI jpti, JSONObject json) {
        this.jpti = jpti;
        operationName = json.optString(OPERATION_NAME, "");
        operationNumber = json.optInt(OPERATION_NO, -1);
        calenderID = json.optInt(CALENDER_ID, -1);

        JSONArray array = json.optJSONArray(TRIP_LIST);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.optJSONObject(i);
            try {
                trip.add(jpti.getTrip(obj.optInt(TRIP_ID, -1)));
            }catch (Exception e){
                SdLog.log(e);
            }
        }
    }

    public JSONObject makeJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(OPERATION_NAME, operationName);
            json.put(OPERATION_NO, operationNumber);
            json.put(CALENDER_ID, calenderID);
            JSONArray array = new JSONArray();
            for (int i = 0; i < trip.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put(TRIP_ID, jpti.indexOf(trip.get(i)));
                array.put(obj);
            }
            json.put(TRIP_LIST, array);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * 運用名
     */
    public String getName() {
        return operationName;
    }

    public void setName(String value){
        operationName=value;
    }
    /**
     * 運用番号変更
     */
    public void setNumber(int number) {
        this.operationNumber = number;
    }

    /**
     * 運用番号
     *
     * @return
     */
    public int getNumber() {
        return operationNumber;
    }


    public int getTripNum() {
        return trip.size();
    }

    public int getCalenderID() {
        return calenderID;
    }

    public void setCalenderID(int value){
        calenderID=value;
    }
    public void addTrip(Trip value){
        trip.add(value);
    }
    public ArrayList<Trip>getTrip(){
        return trip;
    }


}

