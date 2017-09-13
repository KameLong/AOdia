package com.kamelong.JPTI;

import org.json.JSONObject;

/**
 * 会社情報を記述するクラス
 */
public abstract class Agency {
    protected JPTIdata jpti;
    //メンバ変数
    /**
     * 法人名（正式名称）
     */
    protected String name="会社名未定義";
    /**
     * 法人名（一般的名称）
     */
    protected String shortName=null;
    /**
     *　法人番号
     */
    protected int number=1;
    /**
     * 親会社id
     * 問題：idの定義とは何なのか
     *
     */
    protected int parentNo=0;
    /**
     1：第一種鉄道事業者
     2：第二種鉄道事業者
     3：第三種鉄道事業者
     4：一般乗合バス事業者
     5：旅客船等運行事業者
     6：航空運送事業者
     7：その他
     */
    protected int type=1;
    /**
     * 事業者URL
     */
    protected String url=null;
    /**
     * 代表電話番号
     * ※お客様相談センター的なところ？
     */
    protected String phone=null;
    /**
     * 乗車券オンライン購入サイトURL
     */
    protected String fareUrl=null;

    //static final変数

    public static final String NO="agency_no";
    public static final String PARENT_ID="parent_agency_id";
    public static final String NAME="agency_name";
    public static final String SHORT_NAME="agency_short_name";
    public static final String TYPE="agency_type";
    public static final String URL="agency_url";
    public static final String PHONE="agency_phone";
    public static final String FARE_URL="agency_fare_url";


    public Agency(JPTIdata jpti){
        this.jpti=jpti;
    }

    public Agency(JPTIdata jpti,JSONObject json){
        this(jpti);
        try{
            try {
                name = json.getString(NAME);
                number = json.getInt(NO);
            }catch(Exception e){
                System.out.println("会社情報において必要な情報が不足しています。");
                System.out.println("agency_name,agency_noの項目が存在するか確認してください");
            }
            parentNo=json.optInt(PARENT_ID);
            shortName=json.optString(SHORT_NAME);
            type=json.optInt(TYPE);
            url=json.optString(URL);
            phone=json.optString(PHONE);
            fareUrl=json.optString(FARE_URL);

        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public JSONObject makeJSONObject(){
        JSONObject json = new JSONObject();
        try {
            json.put(NAME, name);
            json.put(NO,number);
            if(parentNo>0){
                json.put(PARENT_ID,parentNo);
            }
            if(shortName!=null){
                json.put(SHORT_NAME,shortName);
            }
            if(type>0){
                json.put(TYPE,type);
            }
            if(url!=null){
                json.put(URL,url);
            }
            if(phone!=null){
                json.put(PHONE,phone);
            }
            if(fareUrl!=null){
                json.put(FARE_URL,fareUrl);
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

}
