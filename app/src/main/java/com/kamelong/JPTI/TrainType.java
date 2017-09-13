package com.kamelong.JPTI;

import com.kamelong.tool.Color;

import org.json.JSONObject;
/**
 * 列車種別のクラス
 */
public abstract class TrainType {
    protected JPTIdata jpti;
    protected  Route route;
    /**
     * 種別名
     */
    protected String name="";
    /**
     * 種別略称
     */
    protected  String shortName=null;
    /**
    種別文字色
     */
    protected Color textColor=new Color();
    /**
     * 種別ダイヤ色
     */
    protected Color diaColor=null;

    /**
     * 種別ダイヤスタイル
     * 0:直線
     * 1:破線
     * 2:点線
     * 3:一点鎖線
     */
    protected int diaStyle=0;
    /**
     * 種別ダイヤ太線
     */
    protected boolean diaBold=false;
    /**
     * 種別ダイヤ停車駅明示
     */
    protected boolean showStop=false;
    /**
     * 種別フォント
     */
    protected int font=-1;

    protected static final String NAME="class_name";
    protected static final String SHORT_NAME="class_short_name";
    protected static final String TEXT_COLOR="class_text_color";
    protected static final String DIA_COLOR="class_dia_color";
    protected static final String STYLE="class_dia_style";
    protected  static final String BOLD="class_dia_bold";
    protected  static final String SHOWSTOP="class_dia_showstop";
    protected static final String FONT="class_font";


    public TrainType(JPTIdata jpti,Route route){
        this.jpti=jpti;
        this.route=route;
    }
    public TrainType(JPTIdata jpti,Route route,JSONObject json){
        this(jpti,route);

        try{
            try{
                name=json.getString(NAME);
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                String color=json.optString(TEXT_COLOR,"#000000");
                textColor = new Color(color);
            }catch(Exception e){
                e.printStackTrace();
            }
            shortName=json.optString(SHORT_NAME);
            try{
                String color=json.optString(TEXT_COLOR,"#000000");
                textColor = new Color(color);
            }catch(Exception e) {
                e.printStackTrace();
            }
            diaStyle=json.optInt(STYLE);
            if(json.optInt(BOLD)==1){
                diaBold=true;
            }
            if(json.optInt(SHOWSTOP)==1){
                showStop=true;
            }
            font=json.optInt(FONT,-1);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            json.put(NAME,name);
            if(shortName!=null){
                json.put(SHORT_NAME,shortName);
            }
            if(textColor!=null){
                json.put(TEXT_COLOR,textColor.getHTMLColor());
            }
            if(diaColor!=null){
                json.put(DIA_COLOR,diaColor.getHTMLColor());
            }
            json.put(STYLE,diaStyle);
            if(diaBold){
                json.put(BOLD,1);
            }else{
                json.put(BOLD,0);
            }
            if(showStop){
                json.put(SHOWSTOP,1);
            }else{
                json.put(SHOWSTOP,0);
            }
            if(font>-1){
                json.put(FONT,font);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    /**
     * このObjectはjpti中のリストの何番目に位置するのかを返す
     */
    public int index(){
        if(route.classList.contains(this)) {
            return route.classList.indexOf(this);
        }else{
            Exception e=new Exception();
            e.printStackTrace();
            return -1;
        }
    }


}
