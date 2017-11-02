package com.kamelong.JPTI;


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

    public Agency(JPTI jpti, JSONObject json){
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
            if(shortName.length()>0){
                json.put(SHORT_NAME,shortName);
            }
            if(type>0){
                json.put(TYPE,type);
            }
            if(url.length()>0){
                json.put(URL,url);
            }
            if(phone.length()>0){
                json.put(PHONE,phone);
            }
            if(fareUrl.length()>0){
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
    public String getName(){
        return name;
    }
    public void setName(String value){
        name=value;
    }

}
