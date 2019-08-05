package com.kamelong.OuDia;

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

    public StationTrack(String name){
        trackName=name;
        trackShortName=name;
        trackShortNameUp="";
    }


}
