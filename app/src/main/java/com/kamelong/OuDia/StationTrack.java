package com.kamelong.OuDia;

import java.io.FileWriter;

public class StationTrack {
    /**
     番線名
     */
    public String trackName="";
    /**
     番線略称(共通or下り)
     */
    public String trackShortName="";
    /**
     番線略称(上り)
     この要素は空白も可能です。
     空白の場合、m_strTrackRyakusyouが上下両方に対応します。
     */
    public String trackShortNameUp="";

    public StationTrack(String name,String shortName){
        trackName=name;
        trackShortName=shortName;
        trackShortNameUp="";
    }
    public StationTrack(){

    }
    /**
     * oudiaファイルを1行読み込む
     */
    public void setValue(String title,String value){
        switch (title){
            case"TrackName":
                trackName=value;
                break;
            case"TrackRyakusyou":
                trackShortName=value;
                break;
            case"TrackNoboriRyakusyou":
                trackShortNameUp=value;
                break;
        }
    }

    /**
     * oudia2nd ver1.07形式でファイルを保存する
     * @param out
     * @throws Exception
     */
    public void saveToFile(FileWriter out)throws Exception{
        out.write("EkiTrack2.");
        out.write("TrackName="+trackName+"\r\n");
        out.write("TrackRyakusyou="+trackShortName+"\r\n");
        out.write("TrackNoboriRyakusyou="+trackShortNameUp+"\r\n");
        out.write(".");
    }
}
