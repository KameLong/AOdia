package com.kamelong.OuDia;

import com.kamelong.JPTI.TrainType;
import com.kamelong.tool.Color;


/**
 */
public class OuDiaTrainType {
    /**
     * 種別名
     */
    protected String name="";
    /**
     * 種別名略称
     */
    protected String shortName="";
    /**
     * 時刻表文字色
     */
    protected Color textColor=new Color();
    /**
     * ダイヤグラム線色
     */
    protected Color diaColor=new Color();
    /**
     * ダイヤグラムを太線で描画するか
     */
    protected boolean boldLine=false;
    /**
     * ダイヤグラムの線のスタイル
     * LINESTYLE_XXXの定数を用いる
     */
    protected int lineStyle=LINESTYLE_NORMAL;

    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;

    /**
     * ダイヤグラム上で、停車駅を表示するかどうか
     */
    protected boolean showStop=false;
    public int fontNumber=0;

    /**
     * コンストラクタ。
     * 特に情報がない列車種別を作成する。
     * 種別名から文字列
     * 各色はすべて黒
     */
    public OuDiaTrainType(){
    }

    /**
     * oudia保存形式のテキストデータを作成する
     * @return
     */
    public StringBuilder makeTrainTypeText(){
        StringBuilder result=new StringBuilder("Ressyasyubetsu.");
        result.append("\r\nSyubetsumei=").append(name);
        result.append("\r\nRyakusyou=").append(shortName);
        result.append("\r\nJikokuhyouMojiColor=").append(textColor.getOudiaString());
        result.append("\r\nDiagramSenColor=").append(diaColor.getOudiaString());
        switch (lineStyle){
            case LINESTYLE_NORMAL:
                result.append("\r\nDiagramSenStyle=").append("SenStyle_Jissen");
                break;
            case LINESTYLE_DASH:
                result.append("\r\nDiagramSenStyle=").append("SenStyle_Hasen");
                break;
            case LINESTYLE_DOT:
                result.append("\r\nDiagramSenStyle=").append("SenStyle_Tensen");
                break;
            case LINESTYLE_CHAIN:
                result.append("\r\nDiagramSenStyle=").append("SenStyle_Ittensasen");
                break;
        }
        if(boldLine) {
            result.append("\r\nDiagramSenIsBold=1");
        }
        if(showStop){
            result.append("\r\nStopMarkDrawType=").append("EStopMarkDrawType_DrawOnStop");
        }
        result.append("\r\nJikokuhyouFontIndex=").append(fontNumber);
        result.append("\r\n.\r\n");
        return result;

    }




    /**
     * 種別名をセットする。
     * @param value 種別名
     */
    public void setName(String value){
        name=value;
    }

    /**
     * 種別名の取得
     * @return 種別名
     */
    public String getName(){
        return name;
    }

    /**
     * 略称をセットする
     *  略称は最大２文字とします。
     * @param value 略称
     */
    public void setShortName(String value){
        //shift-jis特有の0x5c問題の解決策です
        if(value.length()>2) {
            shortName = value.substring(0, 2);
        }else{
            shortName=value;
        }
    }
    /**
     * 時刻表文字色をセットする
     * @param color 色を表すint
     */
    public void setTextColor(Color color){
        textColor=color;
    }
    /**
     * ダイヤグラム線色をセットする
     * @param color 色を表すint
     */
    public void setDiaColor(Color color){
        diaColor=color;
    }



    /**
     * ダイヤグラム線スタイルをセットします。
     *  oudiaでは線スタイルは文字列で管理しているので、
     *  それぞれの文字列と一致しているかどうかを確認し、lineStyleに数値を代入します。
     * @param value oudiaのSenStyle項目の文字列
     */
    public void setLineStyle(String value){
        switch (value){
            case "SenStyle_Jissen":
                lineStyle=LINESTYLE_NORMAL;
                break;
            case "SenStyle_Hasen":
                lineStyle=LINESTYLE_DASH;
                break;
            case "SenStyle_Tensen":
                lineStyle=LINESTYLE_DOT;
                break;
            case "SenStyle_Ittensasen":
                lineStyle=LINESTYLE_CHAIN;
                break;
            case "0":
                lineStyle=LINESTYLE_NORMAL;
                break;
            case "1":
                lineStyle=LINESTYLE_DASH;
                break;
            case "2":
                lineStyle=LINESTYLE_DOT;
                break;
            case "3":
                lineStyle=LINESTYLE_CHAIN;
                break;
        }
    }

    /**
     * 線スタイルを取得します
     * @return 線スタイルを表すint
     */
    public int getLineStyle(){
        return lineStyle;
    }

    /**
     * ダイヤグラム線を太字にするかをセットします。
     * @param value 太字にするなら正の数　細いままなら0
     */
    public void setLineBold(int value){
        if(value==0){
            boldLine=false;
        }else{
            boldLine=true;
        }
    }
    /**
     * ダイヤグラム線を太字にするかをセットします。
     *  oudiaのファイルで対応する項目が"1"か"0"なので、
     *  この手法で処理します
     * @param value 太字にするときは"1"
     */
    public void setLineBold(String value){
        if(value.equals("1")){
            boldLine=true;
        }else{
            boldLine=false;
        }
    }

    /**
     * 太字にするかどうかを返す
     * @return 太字にするときtrue
     */
    public boolean getLineBold() {
        return boldLine;
    }

    /**
     * 停車駅表示のセット
     * @param value oudiaの停車駅表示を示す文字列
     */
    public void setShowStop(String value){
        if(value.equals("EStopMarkDrawType_DrawOnStop")) {
            showStop = true;
        }else{
            showStop=false;
        }
    }

    /**
     * 停車駅表示を取得する
     * @return 停車駅表示を行うとき、trueを返す
     */
    public boolean getShowStop(){
        return showStop;
    }
    /**
     * 時刻表文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     * @param color 色を表す文字列
     */
    void setTextColor(String color){
        textColor.setOuDiaColor(color);
    }
    /**
     * ダイヤグラム文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     * @param color 色を表す文字列
     */
    void setDiaColor(String color) {
        diaColor.setOuDiaColor(color);
    }
    public OuDiaTrainType(TrainType trainType){
        super();
        name=trainType.getName();
        if(trainType.getShortName()!=null){
            shortName=trainType.getShortName();

        }
        textColor=trainType.getTextColor();
        if(trainType.getDiaColor()!=null){
            diaColor=getDiaColor();
        }else{
            diaColor=new Color(textColor.getAndroidColor());
        }
        lineStyle=trainType.getDiaStyle();
        boldLine=trainType.getDiaBold();
        showStop=trainType.getShowStop();
        fontNumber=trainType.getFontNumber();
        if(fontNumber<0||fontNumber>6){
            fontNumber=0;
        }


    }

    public String getShortName(){
        return shortName;
    }
    public Color getTextColor(){
        return textColor;
    }
    public Color getDiaColor(){
        return diaColor;
    }
    public boolean compare(TrainType another){
        return name.equals(another.getName());
    }

}
