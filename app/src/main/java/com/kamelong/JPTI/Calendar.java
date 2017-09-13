package com.kamelong.JPTI;

import org.json.JSONObject;

/**
 * 運転日付を決定するデータ
 */
public abstract class Calendar {
    protected JPTIdata jpti;
    protected String name="";

    protected static final String NAME="calendar_name";
    public Calendar(JPTIdata jpti){
        this.jpti=jpti;
    }
    public Calendar(JPTIdata jpti,JSONObject json){
        this(jpti);
        try{
            name=json.optString(NAME,"");
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


}
