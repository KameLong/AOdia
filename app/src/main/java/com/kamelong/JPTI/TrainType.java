package com.kamelong.JPTI;


import com.kamelong.OuDia.OuDiaTrainType;
import com.kamelong.tool.Color;

import org.json.JSONObject;

/**
 * 列車種別のクラス
 */
public class TrainType {
    private JPTI jpti;
    private Route route;
    /**
     * 種別名
     */
    private String name="";
    /**
     * 種別略称
     */
    private String shortName=null;
    /**
    種別文字色
     */
    private Color textColor=new Color();
    /**
     * 種別ダイヤ色
     */
    private Color diaColor=null;

    /**
     * 種別ダイヤスタイル
     * 0:直線
     * 1:破線
     * 2:点線
     * 3:一点鎖線
     */
    private int diaStyle=0;
    /**
     * 種別ダイヤ太線
     */
    private boolean diaBold=false;
    /**
     * 種別ダイヤ停車駅明示
     */
    private boolean showStop=false;
    /**
     * 種別フォント
     */
    private int font=-1;

    private static final String NAME="class_name";
    private static final String SHORT_NAME="class_short_name";
    private static final String TEXT_COLOR="class_text_color";
    private static final String DIA_COLOR="class_dia_color";
    private static final String STYLE="class_dia_style";
    private static final String BOLD="class_dia_bold";
    private static final String SHOWSTOP="class_dia_showstop";
    private static final String FONT="class_font";

    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;

    public TrainType(JPTI jpti) {
        this.jpti = jpti;
        textColor=new Color();
        diaColor=new Color();
    }

    public TrainType(JPTI jpti, JSONObject json){
        this.jpti=jpti;

        try{
            try{
                name=json.getString(NAME);
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                textColor = new Color(json.optString(TEXT_COLOR,"#000000"));
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                diaColor = new Color(json.getString(DIA_COLOR));
            }catch (Exception e){
                diaColor=textColor;
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
    public TrainType(JPTI jpti, OuDiaTrainType trainType){
        this.jpti=jpti;
        name=trainType.getName();
        shortName=trainType.getShortName();
        textColor=trainType.getTextColor();
        diaColor=trainType.getDiaColor();

        diaStyle=trainType.getLineStyle();
        diaBold=trainType.getLineBold();
        showStop=trainType.getShowStop();
        font=trainType.fontNumber;
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
    public String getName(){
        return name;
    }
    public String getShortName(){
        if(shortName==null){
            return "";
        }
        return shortName;
    }
    public Color getTextColor(){
        return textColor;
    }
    public Color getDiaColor(){
        return diaColor;
    }
    public int getDiaStyle(){
        return diaStyle;
    }
    public boolean getShowStop(){
        return showStop;
    }
    public boolean getDiaBold(){
        return diaBold;
    }
    public int getFontNumber(){
        return font;
    }


}
