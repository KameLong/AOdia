package com.kamelong.OuDia;

import com.kamelong.tool.Color;
import com.kamelong.tool.SDlog;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Diagram implements Cloneable{
    public static final int TIMETABLE_BACKCOLOR_NUM = 4;
    /**
     ダイヤの名称です。
     （例） "平日ダイヤ" など
     CentDedRosen に包含される CentDedDia では、
     この属性は一意でなくてはなりません。
     */
    public String name="";
    /**
     時刻表画面における基本背景色のIndexです
     単色時、種別色時の空行、縦縞・横縞・市松模様時
     および、基準運転時分機能有効時に用います。
     範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    public int mainBackColorIndex=0;
    /**
     時刻表画面における補助背景色のIndexです
     縦縞・横縞・市松模様時に用います。
     範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    public int subBackColorIndex=0;
    /**
     時刻表画面における背景色パターンのIndexです
     0:単色
     1:種別色
     2:縦縞
     3:横縞
     4:市松模様
     */
    public int timeTableBackPatternIndex=0;
    /**
     * ダイヤにふくまれる列車
     * [0]下り時刻表
     * [1]上り時刻表
     */
    public ArrayList<Train>[] trains=new ArrayList[2];
    public DiaFile diaFile;

    public Diagram(){

    }
    public Diagram(DiaFile diaFile){
        this.diaFile=diaFile;
        name="新しいダイヤ";
        trains[0]=new ArrayList<>();
        trains[0].add(new Train(diaFile,0));
        trains[1]=new ArrayList<>();
        trains[1].add(new Train(diaFile,1));
    }
    public void setValue(String title,String value){
        switch (title){
            case "DiaName":
                name=value;
                break;
            case "MainBackColorIndex":
                mainBackColorIndex=Integer.parseInt(value);
                break;
            case "SubBackColorIndex":
                subBackColorIndex=Integer.parseInt(value);
                break;
            case "BackPatternIndex":
                timeTableBackPatternIndex=Integer.parseInt(value);
                break;
        }
    }

    public void saveToFile(PrintWriter out) throws Exception {
        out.println("Dia.");
        out.println("DiaName="+name);
        out.println("MainBackColorIndex"+mainBackColorIndex);
        out.println("SubBackColorIndex"+subBackColorIndex);
        out.println("BackPatternIndex"+timeTableBackPatternIndex);
        out.println("Kudari.");
        for(Train t:trains[0]){
            t.saveToFile(out);
        }
        out.println(".");
        out.println("Nobori.");
        for(Train t:trains[1]){
            t.saveToFile(out);
        }
        out.println(".");
        out.println(".");
    }
    public void saveToOuDiaFile(PrintWriter out) throws Exception {
        out.println("Dia.");
        out.println("DiaName="+name);
        out.write("Kudari.");
        for(Train t:trains[0]){
            t.saveToOuDiaFile(out);
        }
        out.write(".");
        out.write("Nobori.");
        for(Train t:trains[1]){
            t.saveToOuDiaFile(out);
        }
        out.write(".");
        out.write(".");
    }
    @Override
    public Diagram clone(){
        try {
            Diagram result = (Diagram) super.clone();
            result.trains[0] = new ArrayList<>();
            for (Train train : trains[0]) {
                result.trains[0].add(train.clone());
            }
            result.trains[1] = new ArrayList<>();
            for (Train train : trains[1]) {
                result.trains[1].add(train.clone());
            }
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Diagram();
        }
    }

}
