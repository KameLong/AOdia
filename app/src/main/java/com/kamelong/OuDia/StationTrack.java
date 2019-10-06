package com.kamelong.OuDia;

import java.io.PrintWriter;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 番線１つを表します。
 * 全てのStationTrackはStationによって管理されます
 */
public class StationTrack implements Cloneable{
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
    void setValue(String title,String value){
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
     * oudia2nd 形式でファイルを保存する
     */
    void saveToFile(PrintWriter out){
        out.println("EkiTrack2.");
        out.println("TrackName="+trackName);
        out.println("TrackRyakusyou="+trackShortName);
        if(trackShortNameUp.length()!=0) {
            out.println("TrackNoboriRyakusyou=" + trackShortNameUp);
        }
        out.println(".");
    }
    @Override
    public StationTrack clone() {
        try {
            return (StationTrack) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new StationTrack();
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */


}
