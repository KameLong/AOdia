package com.kamelong.aodia.AOdiaData;


import com.kamelong.tool.SDlog;

import java.io.PrintWriter;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * １つ路線外発着駅を表します。
 * OuterTerminalは全てStationに管理されます。
 */

public class OuterTerminal implements Cloneable{
    /**
     路線外発着駅名です。
     作業設定画面で用います。
     */
    public String outerTerminalName="";
    /**
     路線外発着駅名の時刻表ビューにおける略称です。
     空の場合は、OuterTerminalEkimeiをそのまま用います。
     文字数制限は当面ありません
     */
    public String outerTerminalTimeTableName ="";	/**
     路線外発着駅名のダイアグラムビューにおける略称です。
     空の場合は、OuterTerminalEkimeiの頭文字になります。
     文字数制限は当面ありません
     */
    public String outerTerminalDiaName="";


    public OuterTerminal(){
    }

    /**
     * 駅名を指定して生成する
     * @param name 路線外駅名
     */
    public OuterTerminal(String name){
        outerTerminalName=name;
        outerTerminalTimeTableName =name;
        if(name.length()>1){
            outerTerminalDiaName=name.substring(0,1);
        }else{
            outerTerminalDiaName=name;
        }
    }
    /**
     * oudiaの1行を読み込む
     */
    public void setValue(String title,String value){
        switch (title){
            case"OuterTerminalEkimei":
                outerTerminalName=value;
                break;
            case"OuterTerminalJikokuRyaku":
                outerTerminalTimeTableName =value;
                break;
            case"OuterTerminalDiaRyaku":
                outerTerminalDiaName=value;
                break;
        }
    }
    /**
     * oudia2nd　形式でファイルを保存する
     */
    void saveToFile(PrintWriter out){
        out.println("OuterTerminal.");
        out.println("OuterTerminalEkimei="+outerTerminalName);
        out.println("OuterTerminalJikokuRyaku="+ outerTerminalTimeTableName);
        out.println("OuterTerminalDiaRyaku="+outerTerminalDiaName);
        out.println(".");
    }
    @Override
    public OuterTerminal clone(){
        try{
            return (OuterTerminal)super.clone();
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new OuterTerminal();
        }
    }

}
