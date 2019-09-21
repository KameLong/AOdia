package com.kamelong.tool;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * java.awt.ColorはAndroidでは使えない
 * そもそもjava.awt.Colorはあまり好きではないのでColorクラスを自作
 */

public class Color implements Cloneable{
    public static final Color BLACK=new Color("#000000");
    public static final Color WHITE=new Color("#FFFFFF");

    private int alpha=255;
    private int red=0;
    private int green=0;
    private int blue=0;

    /**
     * デフォルトは黒色を作る
     */
    public Color(){
        alpha=255;
        red=0;
        green=0;
        blue=0;
    }
    /**
     * HTMLの色記述形式からColorを作成する
     * #rgb
     * #rrggbb
     * #aarrggbb
     * の形式に対応
     * @param str
     */
    public Color(String str){
        if(str.startsWith("#")){
            str=str.substring(1);
        }
        switch (str.length()){
            case 3:
                red=Integer.parseInt(str.substring(0,1), 16);
                green=Integer.parseInt(str.substring(1,2),16);
                blue=Integer.parseInt(str.substring(2,3),16);
                break;
            case 6:
                red=Integer.parseInt(str.substring(0,2), 16);
                green=Integer.parseInt(str.substring(2,4),16);
                blue=Integer.parseInt(str.substring(4,6),16);
                break;
            case 8:
                alpha=Integer.parseInt(str.substring(0,2), 16);
                red=Integer.parseInt(str.substring(2,4), 16);
                green=Integer.parseInt(str.substring(4,6),16);
                blue=Integer.parseInt(str.substring(6,8),16);
                break;
        }
    }

    /**
     * Androidの32bitカラーより作成
     * @param color
     */
    public Color(int color){
        alpha = (color >> 24) & 0xff; // or color >>> 24
        red = (color >> 16) & 0xff;
        green = (color >>  8) & 0xff;
        blue = (color      ) & 0xff;
    }

    public Color(int alpha,int red,int green,int blue){
        this.alpha=alpha;
        this.red=red;
        this.green=green;
        this.blue=blue;
    }
    /**
     * HTML形式の色情報を出力する
     * @return
     */
    public String getHTMLColor(){
        String result ="#"+String.format("%02X",red);
        result += String.format("%02X",green);
        result += String.format("%02X",blue);
        return result;
    }

    /**
     * Android形式の色情報を出力する
     * @return
     */
    public int getAndroidColor(){
        int result=alpha<<24;
        result+=red<<16;
        result+=green<<8;
        result+=blue;
        return result;
    }

    /**
     * OuDiaの色形式で出力する
     * @return
     */
    public String getOudiaString(){
        String result="00";
        result+=String.format("%02X",blue);
        result+=String.format("%02X",green);
        result+=String.format("%02X",red);
        return result;
    }

    /**
     * OuDiaの色形式を入力する
     * @param value
     */
    public void setOuDiaColor(String value){
            red = Integer.parseInt(value.substring(6, 8), 16);
            green = Integer.parseInt(value.substring(4, 6), 16);
            blue = Integer.parseInt(value.substring(2, 4), 16);
    }


    public int getAlpha(){
        return alpha;
    }
    public int getRed(){
        return red;
    }
    public int getGreen(){
        return green;
    }
    public int getBlue(){
        return blue;
    }
    public void setAlpha(int value){
        if(value>255){
            value=255;
        }
        alpha=value;
    }
    public void setRed(int value){
        if(value>255){
            value=255;
        }
        red=value;
    }
    public void setGreen(int value){
        if(value>255){
            value=255;
        }
        green=value;
    }
    public void setBlue(int value){
        if(value>255){
            value=255;
        }
        blue=value;
    }
    public Color clone(){
        try{
            return (Color)super.clone();
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Color();
        }
    }
}
