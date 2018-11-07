package com.kamelong.JPTI;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import org.json.JSONObject;

/**
 * 会社情報を記述するクラス
 */
public class Agency {
    private JPTI jpti;
    //メンバ変数
    /**
     * 法人名（正式名称）
     */
    private String name="会社名未定義";
    /**
     * 法人名（一般的名称）
     */
    private String shortName="";
    /**
     *　法人番号
     */
    private int number=1;
    /**
     * 親会社id
     * 問題：idの定義とは何なのか
     *
     */
    private int parentNo=0;
    /**
     1：第一種鉄道事業者
     2：第二種鉄道事業者
     3：第三種鉄道事業者
     4：一般乗合バス事業者
     5：旅客船等運行事業者
     6：航空運送事業者
     7：その他
     */
    private int type=1;
    /**
     * 事業者URL
     */
    private String url="";
    /**
     * 代表電話番号
     * ※お客様相談センター的なところ？
     */
    private String phone="";
    /**
     * 乗車券オンライン購入サイトURL
     */
    private String fareUrl="";

    //static final変数

    private static final String NO="agency_no";
    private static final String PARENT_ID="parent_agency_id";
    private static final String NAME="agency_name";
    private static final String SHORT_NAME="agency_short_name";
    private static final String TYPE="agency_type";
    private static final String URL="agency_url";
    private static final String PHONE="agency_phone";
    private static final String FARE_URL="agency_fare_url";


    public Agency(JPTI jpti){
        this.jpti=jpti;
    }

    public Agency(JPTI jpti, JsonObject json){
        this(jpti);
        try{
            try {
                name = json.getString(NAME,"");
                number = json.getInt(NO,1);
            }catch(Exception e){
                System.out.println("会社情報において必要な情報が不足しています。");
                System.out.println("agency_name,agency_noの項目が存在するか確認してください");
            }
            parentNo=json.getInt(PARENT_ID,0);
            shortName=json.getString(SHORT_NAME,"");
            type=json.getInt(TYPE,1);
            url=json.getString(URL,"");
            phone=json.getString(PHONE,"");
            fareUrl=json.getString(FARE_URL,"");

        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public JsonObject makeJSONObject(){
        JsonObject json = new JsonObject();
        try {
            json.add(NAME, name);
            json.add(NO,number);
            if(parentNo>0){
                json.add(PARENT_ID,parentNo);
            }
            if(shortName.length()>0){
                json.add(SHORT_NAME,shortName);
            }
            if(type>0){
                json.add(TYPE,type);
            }
            if(url.length()>0){
                json.add(URL,url);
            }
            if(phone.length()>0){
                json.add(PHONE,phone);
            }
            if(fareUrl.length()>0){
                json.add(FARE_URL,fareUrl);
            }
            return json;

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(jpti.agency.contains(this)) {
            return jpti.agency.indexOf(this);
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
