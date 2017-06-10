package com.fc2.web.kamelong.aodia.GTFS;

import com.fc2.web.kamelong.aodia.oudia.DiaFile;

import java.util.ArrayList;

/**
 * GTFSのデータをベースにしたダイヤファイル
 * GTFSの元データに加え、時刻表に追加する駅リスト、必須駅リストを準備する
 */
public class GTFSdiaFile extends DiaFile {
    public GTFSdiaFile(GTFSFile originFile, ArrayList<GTFSStation> stationList,ArrayList<Integer> nessStationList){
        lineName=originFile.agencyName;
        for(int i=0;i<originFile.trainList.size();i++){
        }


    }
}
