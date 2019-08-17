package com.kamelong.OuDia;


import com.kamelong.tool.SDlog;

import java.io.PrintWriter;

/**
 * 路線外発着駅
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
                outerTerminalDiaName=value;
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
     * oudia2nd ver1.07形式でファイルを保存する
     * @param out
     * @throws Exception
     */
    public void saveToFile(PrintWriter out)throws Exception{
        out.println("OuterTerminal.");
        out.println("OuterTerminalEkimei="+outerTerminalDiaName);
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
