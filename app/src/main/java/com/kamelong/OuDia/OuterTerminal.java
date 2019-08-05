package com.kamelong.OuDia;


/**
 * 路線外発着駅
 */

class OuterTerminal {
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
    public String outerTerminalJikokuName="";	/**
     路線外発着駅名のダイアグラムビューにおける略称です。
     空の場合は、OuterTerminalEkimeiの頭文字になります。
     文字数制限は当面ありません
     */
    public String outerTerminalDiaName="";

    public OuterTerminal(String name){
        outerTerminalName=name;
        outerTerminalJikokuName=name;
        if(name.length()>1){
            outerTerminalDiaName=name.substring(0,1);
        }else{
            outerTerminalDiaName=name;
        }
    }
}
