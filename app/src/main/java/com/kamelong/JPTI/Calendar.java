package com.kamelong.JPTI;



import com.eclipsesource.json.JsonObject;

import org.json.JSONObject;

/**
 * 運転日付を決定するデータ
 */
public class Calendar {
    private JPTI jpti;
    private String name="";

    private static final String NAME="calendar_name";
    public Calendar(JPTI jpti){
        this.jpti=jpti;
    }
    public Calendar(JPTI jpti, JsonObject json){
        this(jpti);
        try{
            name=json.getString(NAME,"");
        }catch(Exception e){

        }
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(NAME,name);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(jpti.calendarList.contains(this)) {
            return jpti.calendarList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }


}
