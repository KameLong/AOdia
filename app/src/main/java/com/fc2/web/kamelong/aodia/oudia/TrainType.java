package com.fc2.web.kamelong.aodia.oudia;

import android.graphics.Color;


/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。kamelong.dev@gmail.com
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */

/**
 * @author  kamelong
 * 列車種別を格納するクラス。
 * 1つの列車種別は１つのオブジェクトになります。
 */
public class TrainType {
    /**
     * 種別名
     */
    private String name="";
    /**
     * 種別名略称
     */
    private String shortName="";
    /**
     * 時刻表文字色
     */
    private int textColor=Color.BLACK;
    /**
     * ダイヤグラム線色
     */
    private int diaColor=Color.BLACK;
    /**
     * ダイヤグラムを太線で描画するか
     */
    private boolean boldLine=false;
    /**
     * ダイヤグラムの線のスタイル
     * LINESTYLE_XXXの定数を用いる
     */
    private int lineStyle=LINESTYLE_NORMAL;

    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;
    /**
     * ダイヤグラム上で、停車駅を表示するかどうか
     */
    private boolean showStop=false;

    /**
     * コンストラクタ。
     * 特に情報がない列車種別を作成する。
     * 種別名から文字列
     * 各色はすべて黒
     */
    public TrainType(){
    }

    /**
     * 種別名をセットする。
     * @param value 種別名
     */
    public void setName(String value){
        //shift-jis特有の0x5c問題の解決策です
        String[] dameMoji={"\\","―","ソ","Ы","Ⅸ","噂","浬","欺","圭","構.","蚕","十","申","曾","箪","貼","能","表","暴","予","禄","兔","喀","媾","彌","拿","杤","歃","濬","畚","秉","綵","臀","藹","觸","軆","鐔","饅","鷭","偆","砡","纊","犾"};
        for (String moji: dameMoji){
            value=value.replace(moji+"\\",moji);
        }

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
        String[] dameMoji={"\\","―","ソ","Ы","Ⅸ","噂","浬","欺","圭","構.","蚕","十","申","曾","箪","貼","能","表","暴","予","禄","兔","喀","媾","彌","拿","杤","歃","濬","畚","秉","綵","臀","藹","觸","軆","鐔","饅","鷭","偆","砡","纊","犾"};
        for (String moji: dameMoji){
            value=value.replace(moji+"\\",moji);
        }
        if(value.length()>2) {
            shortName = value.substring(0, 2);
        }else{
            shortName=value;
        }
    }

    /**
     * 略称の取得
     * 略称は秒表示しないときの時刻表に用いる
     * @return 略称
     */
    public String getShortName(){
        return shortName;
    }

    /**
     * 時刻表文字色を取得する
     * @return 色を表すint
     */
    public int getTextColor(){
        return textColor;
    }
    /**
     * ダイヤグラム線色を取得する
     * @return 色を表すint
     */
    public int getDiaColor(){
        return diaColor;
    }

    /**
     * 時刻表文字色をセットする
     * @param color 色を表すint
     */
    public void setTextColor(int color){
        textColor=color;
    }
    /**
     * ダイヤグラム線色をセットする
     * @param color 色を表すint
     */
    public void setDiaColor(int color){
        diaColor=color;
    }

    /**
     * 時刻表文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     *  netgramの色表記は"#rrggbb"の7文字の文字列
     *              これらの違いを踏まえつつ、int型の色を作成します。
     * @param color 色を表す文字列
     */
    public void setTextColor(String color){
        if(color.startsWith("#")){
            int blue=Integer.parseInt(color.substring(5,7),16);
            int green=Integer.parseInt(color.substring(3,5),16);
            int red=Integer.parseInt(color.substring(1,3),16);
            setTextColor(Color.rgb(red,green,blue));

        }else{
            int blue=Integer.parseInt(color.substring(2,4),16);
            int green=Integer.parseInt(color.substring(4,6),16);
            int red=Integer.parseInt(color.substring(6,8),16);
            setTextColor(Color.rgb(red,green,blue));
        }
    }
    /**
     * ダイヤグラム文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     *  netgramの色表記は"#rrggbb"の7文字の文字列
     *              これらの違いを踏まえつつ、int型の色を作成します。
     * @param color 色を表す文字列
     */
    public void setDiaColor(String color) {
        if(color.startsWith("#")){
            int blue=Integer.parseInt(color.substring(5,7),16);
            int green=Integer.parseInt(color.substring(3,5),16);
            int red=Integer.parseInt(color.substring(1,3),16);
            setDiaColor(Color.rgb(red,green,blue));

        }else{
            int blue=Integer.parseInt(color.substring(2,4),16);
            int green=Integer.parseInt(color.substring(4,6),16);
            int red=Integer.parseInt(color.substring(6,8),16);
            setDiaColor(Color.rgb(red,green,blue));
        }

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
}
