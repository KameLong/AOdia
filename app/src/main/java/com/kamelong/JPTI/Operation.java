package com.kamelong.JPTI;


import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
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

    public Operation(JPTI jpti, JsonObject json) {
        this.jpti = jpti;
        operationName = json.getString(OPERATION_NAME, "");
        operationNumber = json.getInt(OPERATION_NO, -1);
        calenderID = json.getInt(CALENDER_ID, -1);

        JsonArray array = json.get(TRIP_LIST).asArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).asObject();
            try {
                trip.add(jpti.getTrip(obj.getInt(TRIP_ID, -1)));
            }catch (Exception e){
                SdLog.log(e);
            }
        }
    }

    public JsonObject makeJSONObject() {
        JsonObject json = new JsonObject();
        try {
            json.add(OPERATION_NAME, operationName);
            json.add(OPERATION_NO, operationNumber);
            json.add(CALENDER_ID, calenderID);
            JsonArray array = new JsonArray();
            for (int i = 0; i < trip.size(); i++) {
                JsonObject obj = new JsonObject();
                obj.add(TRIP_ID, jpti.indexOf(trip.get(i)));
                array.add(obj);
            }
            json.add(TRIP_LIST, array);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonObject();
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

