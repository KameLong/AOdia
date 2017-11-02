package com.kamelong.aodia.diadata;

import android.content.Context;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Operation;
import com.kamelong.JPTI.Service;
import com.kamelong.aodia.AOdiaActivity;
import java.util.ArrayList;

/**
 * AOdiaで使用するDiaFile
 * OuDiaの処理をベースにしている。
 */

public class AOdiaDiaFile {
    private AOdiaActivity activity;//このアプリのアクティビティー
    private JPTI jpti = null;
    private Service service = null;
    private AOdiaStation stations = null;
    protected ArrayList<AOdiaTimeTable[]> trainList = new ArrayList<>();

    public ArrayList<ArrayList<AOdiaOperation>>operationList=new ArrayList<>();
    private String filePath = "";
    /**
     * メニューを開いているかどうか
     */
    public boolean menuOpen = true;

    /**
     * 運用のリストダイヤの数だけ、リストを用意する
     */
//    public ArrayList<ArrayList<Operation>> operationList = new ArrayList<>();


    /**
     * 推奨コンストラクタ。
     * コンストラクタでは読み込みJPTI,Serviceが与えられる
     * ダイヤを読み込んだ後に最小所要時間をこのスレッドで作成する
     *
     * @param context MainActivityになると思われる
     */
    public AOdiaDiaFile(Context context, JPTI jpti, Service service, String filePath) {
        activity = (AOdiaActivity) context;
        this.jpti = jpti;
        this.service = service;
        this.filePath = filePath;

        stations = new AOdiaStation(this);
        for (int diaNum = 0; diaNum < jpti.getCalendarSize(); diaNum++) {
            AOdiaTimeTable[] timeTable = new AOdiaTimeTable[2];
            timeTable[0] = new AOdiaTimeTable(this,diaNum,0);
            timeTable[1] = new AOdiaTimeTable(this,diaNum,1);
            trainList.add(timeTable);
            ArrayList<AOdiaOperation>operationL=new ArrayList<>();
            operationList.add(operationL);
        }
        for(int i=0;i<jpti.getOperationSize();i++){
            Operation ope=jpti.getOpetarion(i);
            operationList.get(ope.getCalenderID()).add(new AOdiaOperation(this,ope));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                stations. calcMinReqiredTime();
            }
        }).start();
    }

    public Service getService() {
        return service;
    }

    public JPTI getJPTI() {
        return jpti;
    }

    public String getLineName() {
        return service.getName();
    }

    public String getDiaName(int index) {
        return jpti.getCalendar(index).getName();
    }

    public AOdiaStation getStation() {
        return stations;
    }

    public AOdiaTimeTable getTimeTable(int diaNum, int direct) {
        return trainList.get(diaNum)[direct];
    }


    /**
     * ファイルパスを返す。
     * ロードしたファイルのパスで保存データを整理するため。
     *
     * @return
     */
    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String path) {
        filePath = path;
    }
    public int getDiaNum(){
        return trainList.size();
    }
    public String getComment(){
        return service.getComment();
    }

    public void saveAOdia(){
        jpti.resetOperation();
        for(int diaNum=0;diaNum<getDiaNum();diaNum++){
            for(AOdiaOperation ope:operationList.get(diaNum)){
                jpti.addOperation(ope.getJPTIoperation());

            }

        }
    }



}
