package com.kamelong.OuDia;

import java.util.ArrayList;

public class StationTime {
    /**
     『駅扱』を表します。
     */
    public int stopType=STOP_TYPE_NOSERVICE;
    public static final int STOP_TYPE_NOSERVICE = 0;
    public static final int STOP_TYPE_STOP = 1;
    public static final int STOP_TYPE_PASS = 2;
    public static final int STOP_TYPE_NOVIA = 3;

    /**
     * 着時刻
     * 着時刻が存在しない時は負の数となります
     */
    public int ariTime=-1;
    /**
     * 発時刻
     * 発時刻が存在しない時は負の数となります
     */
    public int depTime=-1;
    /**
     * 番線
     * デフォルト=-1
     */
    public int stopTrack=-1;

    /**
     * 前作業一覧
     */
    public ArrayList<StationTimeOperation>beforeOperations=new ArrayList<>();
    /**
     * 後作業一覧
     */
    public ArrayList<StationTimeOperation>afterOperations=new ArrayList<>();

    public StationTime clone() throws CloneNotSupportedException {
        StationTime other=(StationTime)super.clone();
        for(StationTimeOperation operation:beforeOperations){
            other.beforeOperations.add(operation.clone());
        }
        for(StationTimeOperation operation:afterOperations){
            other.afterOperations.add(operation.clone());
        }
        return other;
    }





}
