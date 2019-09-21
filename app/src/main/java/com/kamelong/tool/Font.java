package com.kamelong.tool;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * フォントスタイル情報を管理する
 */
public class Font implements Cloneable{
    public static final Font OUDIA_DEFAULT=new Font("ＭＳ ゴシック",9,false,false);
    /**
     * フォント高さ
     */
    public int height=-1;
    /**
     * フォント名
     */
    public String name=null;
    /**
     * 太字なら１
     */
    public boolean bold=false;
    /**
     * 斜体なら１
     */
    public boolean itaric=false;

    private static final String HEIGHT="height";
    private static final String NAME="facename";
    private static final String BOLD="bold";
    private static final String ITARIC="itaric";

    public Font(){

    }
    public Font(String value){
        String[] valueList=value.split(";");
        for(String s :valueList){
            String t=s.split("=")[0];
            String v=s.split("=")[1];
            switch (t){
                case"PointTextHeight":
                    height=Integer.parseInt(v);
                    break;
                case "Facename":
                    name=v;
                    break;
                case "Bold":
                    bold=v.equals("1");
                    break;
                case "Itaric":
                    itaric=v.equals("1");
                    break;
            }
        }
    }
    public String getOuDiaString(){
        StringBuilder result=new StringBuilder();
        result.append("PointTextHeight=").append(height);
        if(name!=null){
            result.append(";Facename=").append(name);
        }else{
            result.append(";Facename=").append("ＭＳ ゴシック");
        }
        if(bold){
            result.append(";Bold=1");
        }
        if(itaric){
            result.append("Itaric=1");
        }
        return result.toString();
    }
    private Font(String name, int height, boolean bold, boolean itaric){
        this.name=name;
        this.height=height;
        this.bold=bold;
        this.itaric=itaric;
    }

    public Font clone(){
        try {
            return (Font) super.clone();
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Font();
        }
    }


}
