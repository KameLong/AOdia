package com.kamelong.OuDia;


import java.io.FileWriter;

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
    public void saveToFile(FileWriter out)throws Exception{
        out.write("OuterTerminal.");
        out.write("OuterTerminalEkimei="+outerTerminalDiaName+"\r\n");
        out.write("OuterTerminalJikokuRyaku="+ outerTerminalTimeTableName +"\r\n");
        out.write("OuterTerminalDiaRyaku="+outerTerminalDiaName+"\r\n");
        out.write(".");
    }
}
