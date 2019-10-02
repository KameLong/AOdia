package com.kamelong2.OuDia;

import android.content.pm.PackageInfo;

import com.kamelong2.aodia.SDlog;
import com.kamelong2.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 一つの路線ファイルを表す。
 */
public class DiaFile {

    /**
     * ダイヤ一覧
     */
    public ArrayList<Diagram>diagram=new ArrayList<>();
    /**
     * 駅一覧
     */
    public ArrayList<Station>station=new ArrayList<>();
    /**
     * 列車種別一覧
     */
    public ArrayList<TrainType>trainType=new ArrayList<>();
    /**
     * 路線名
     */
    public String name="";
    /**
     * OuDiaバージョン
     */
    public String version="";
    /**
     * コメント
     */
    public String comment="";
    /**
     * その他AOdiaで使わないパラメーター
     */
    public HashMap<String,String> parameterRosen =new HashMap<>();
    public HashMap<String,String> parameterDispProp =new HashMap<>();

    /**
     * 駅間の所要時間
     */
    private ArrayList<Integer>stationTime=new ArrayList<>();
    /**
     * ダイヤグラム開始時間
     */
    public int diagramStartTime=3600*3;


    //AOdia専用オプション
    /**
     * ファイル保存パス
     */
    public String filePath="";
    /**
     * このファイルのメニューあ開いているか？
     */
    public boolean menuOpen=true;

    /**
     * 新規に空路線を生成する
     */
    public DiaFile(){
        version="OuDiaSecond.1.05";
        name="新しい路線";
        trainType.add(new TrainType());
        diagram.add(new Diagram(this));
    }

    /**
     * 指定ファイルパスに新規ダイヤファイルを生成する
     */
    public DiaFile(String filePath){
        this();
        this.filePath=filePath;
    }

    /**
     * ファイルからダイヤを開く
     * @param file　入力ファイル
     * @throws Exception ファイルが読み込めなかった時に返す
     */
    public DiaFile(File file)throws Exception{
        filePath=file.getPath();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        version=br.readLine().split("=",-1)[1];
        double v=1.02;
        try {
            v = Double.parseDouble(version.substring(version.indexOf(".") + 1));
        }catch(Exception e){
            e.printStackTrace();
        }

        if(version.startsWith("OuDia.")||v<1.03){
            //Shift-Jisのファイル
            loadShiftJis(file);
        }else{
            //utf-8のファイル
            loadDiaFile(br);
        }
        //最小所要時間を計算する
        reCalcStationTime();
        System.out.println("読み込み終了");

    }
    /**
     *    ２つのダイヤファイルを結合して新しいダイヤファイルを作る
     *    todo
     */
    public DiaFile(DiaFile[] diaList,int includeStationIndex){
        boolean stationConnect=false;
        ArrayList<Integer>[] stationIndexList=new ArrayList[2];
        stationIndexList[0]=new ArrayList<>();
        stationIndexList[1]=new ArrayList<>();
        for(int i=0;i<diaList[0].getStationNum()&&i<includeStationIndex;i++){
            station.add(new Station(diaList[0].station.get(i)));
        }
        if(diaList[0].getStationNum()>includeStationIndex){
            if(diaList[0].station.get(includeStationIndex).name.equals(diaList[1].station.get(0).name)){
                stationConnect=true;
            }else{
                stationConnect=false;
                station.add(new Station(diaList[0].station.get(includeStationIndex)));
            }
        }
        for(int i=0;i<diaList[1].getStationNum();i++){
            station.add(new Station(diaList[1].station.get(i)));
        }
        for(int i=includeStationIndex+1;i<diaList[0].getStationNum();i++){
            station.add(new Station(diaList[0].station.get(i)));
        }



    }

    /**
     * Shift-JISで書かれたファイルを読み込む
     */
    private void loadShiftJis(File file)throws Exception{
        BufferedReader br = new ShiftJISBufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
        version=br.readLine().split("=",-1)[1];
        loadDiaFile(br);
    }

    /**
     * ダイヤファイルを読み込む
     * @param br 入力ファイル
     * @throws Exception 読み込み失敗
     */
    private void loadDiaFile(BufferedReader br)throws Exception{
            if(!br.readLine().equals("Rosen.")){
                throw new Exception("Rosen is not found");
            }
            name=br.readLine().split("=",-1)[1];
            String line=br.readLine();
            while(line!=null){
                if(line.equals("Eki.")){
                    station.add(new Station(br,this));
                    line=br.readLine();
                    continue;
                }
                if(line.equals("Ressyasyubetsu.")){
                    trainType.add(new TrainType(br));
                    line=br.readLine();
                    continue;
                }
                if(line.equals("Dia.")){
                    diagram.add(new Diagram(this,br));
                    line=br.readLine();
                    continue;
                }
                if(line.startsWith("KitenJikoku=")){
                    String value=line.split("=")[1];
                    if(value.length()==4){
                        diagramStartTime=Integer.parseInt(value.substring(0,2))*60;
                        diagramStartTime+=Integer.parseInt(value.substring(2,4));
                        diagramStartTime=diagramStartTime*60;
                    }else{
                        diagramStartTime=Integer.parseInt(value.substring(0,1))*60;
                        diagramStartTime+=Integer.parseInt(value.substring(1,3));
                        diagramStartTime=diagramStartTime*60;
                    }
                    line=br.readLine();
                    continue;
                }
                if(line.startsWith("DispProp.")){
                    readDispProp(br);
                }
                if(line.startsWith("AOdiaFilePath=")){
                    filePath=line.split("=")[1];
                    line=br.readLine();
                    continue;
                }
                if(line.startsWith("Comment=")){
                    comment=line.split("=",-1)[1].replace("\\n","\n");
                    line=br.readLine();
                    continue;
                }
                if(line.contains("=")){
                    parameterRosen.put(line.substring(0,line.indexOf("=")),line.substring(line.indexOf("=")+1));
                }
                line=br.readLine();
            }

    }
    private void readDispProp(BufferedReader br)throws Exception {
        String line = br.readLine();
        while (!line.equals(".")){
            if(line.contains("=")){
                parameterDispProp.put(line.substring(0,line.indexOf("=")),line.substring(line.indexOf("=")+1));
            }
            line=br.readLine();
        }
    }

    public int getDiaNum(){
        return diagram.size();
    }
    public int getStationNum(){
        return station.size();
    }

    public void reCalcStationTime(){
        stationTime=new ArrayList<>();
        stationTime.add(0);
        for(int i=0;i<getStationNum()-1;i++){
            stationTime.add(stationTime.get(stationTime.size()-1)+getMinReqiredTime(i,i+1));
        }
    }
    /**
     * 最小所要時間のリストを返します。
     * 最小所要時間は別スレッドで計算されている場合がありますので、
     * 計算が終了するまで、スレッドを待機させます。
     * @return
     */
    public ArrayList<Integer> getStationTime(){
        while(stationTime.size()<getStationNum()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationTime;
    }

    /**
     * 最小所要時間を計算する。
     * この関数は処理の完了までにかなりの時間がかかると予想されます。
     */
    protected void calcMinReqiredTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                stationTime.add(0);
                for(int i=0;i<getStationNum()-1;i++){
                    stationTime.add(stationTime.get(stationTime.size()-1)+getMinReqiredTime(i,i+1));
                }
            }
        }).start();
    }
    public Train getTrain(int diagramIndex,int direction,int trainIndex){
        return diagram.get(diagramIndex).trains[direction].get(trainIndex);
    }
    public int getTrainSize(int diagramIndex,int direction){
        return diagram.get(diagramIndex).trains[direction].size();
    }
    /**
     *  駅間最小所要時間を返す。
     *  startStatioin endStationの両方に止まる列車のうち、
     *  所要時間（着時刻-発時刻)の最も短いものを秒単位で返す。
     *  ただし、駅間所要時間が90秒より短いときは90秒を返す。
     *
     *  startStation endStationは便宜上区別しているが、順不同である。
     * @param startStation
     * @param endStation
     * @return time(second)
     */
    public int getMinReqiredTime(int startStation,int endStation){
        int result=360000;
        for(int i=0;i<getDiaNum();i++){
            if(diagram.get(i).name.equals("基準運転時分")){
                result=360000;
                for(int train=0;train<getTrainSize(i,0);train++){
                    int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1)){
                        value+=120;
                    }
                    if(value>0&&result>value){
                        result=value;
                    }
                }
                for(int train=0;train<getTrainSize(i,1);train++){
                    int value=this.getTrain(i,1,train).getRequiredTime(startStation,endStation);
                    if(value>0&&(getTrain(i,1,train).getStop(startStation)!=1||getTrain(i,1,train).getStop(endStation)!=1)){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
                if(result==360000){
                    result=120;
                }
                return result;
            }
        }
        for(int i=0;i<getDiaNum();i++){

            for(int train=0;train<getTrainSize(i,0);train++){
                int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                if(value>0&&(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1)){
                    value+=120;
                }

                if(value>0&&result>value){
                    result=value;
                }
            }
            for(int train=0;train<getTrainSize(i,1);train++){
                int value=getTrain(i,1,train).getRequiredTime(startStation,endStation);
                if(value>0&&(getTrain(i,1,train).getStop(startStation)!=1||getTrain(i,1,train).getStop(endStation)!=1)){
                    value+=120;
                }

                if(value>0&&result>value){
                    result=value;
                }
            }
        }
        if(result==360000){
            result=120;
        }
        if(result<90){
            result=90;
        }

        return result;
    }
    public void saveToFile(String fileName,boolean saveFilePath) throws Exception {
            FileOutputStream fos = new FileOutputStream(fileName);

            //BOM付与
            fos.write(0xef);
            fos.write(0xbb);
            fos.write(0xbf);
            fos.close();
            FileWriter out=new FileWriter(fileName,true);
            out.write("FileType=OuDiaSecond.1.05\r\n");
            out.write("Rosen.\r\n");
            out.write("Rosenmei="+name+"\r\n");
            for(Station s:station){
                s.saveToFile(out);
            }
            for(TrainType type:trainType){
                type.saveToFile(out);
            }
            for(Diagram dia:diagram){
                dia.saveToFile(out);
            }

            out.write("KitenJikoku="+diagramStartTime/3600+String.format("%02d",(diagramStartTime/60)%60)+"\r\n");
            out.write("DiagramDgrYZahyouKyoriDefault=60\r\n");
            if(saveFilePath) {
                out.write("AOdiaFilePath=" + filePath + "\r\n");
            }
            out.write("Comment="+comment.replace("\n","\\n")+"\r\n");
            for(Map.Entry<String,String> value :parameterRosen.entrySet()){
                out.write(value.getKey()+"="+value.getValue()+"\r\n");
            }
            out.write(".\r\n");

            out.write("DispProp.\r\n");
        for(Map.Entry<String,String> value :parameterDispProp.entrySet()){
            out.write(value.getKey()+"="+value.getValue()+"\r\n");
        }

        out.write(".\r\n");
        PackageInfo packageInfo = SDlog.activity.getPackageManager().getPackageInfo(SDlog.activity.getPackageName(), 0);
        out.write("FileTypeAppComment=AOdia v"+packageInfo.versionName+"\r\n");
            out.close();


    }
    public void copyDiagram(int diagramIndex,String diaName){
        diagram.add(new Diagram(diagram.get(diagramIndex)));
        diagram.get(diagram.size()-1).name=diaName;
    }
    public void addNewDiagram(){
        diagram.add(new Diagram(this));
    }
    public void deleteDiagram(int diagramIndex){
        diagram.remove(diagramIndex);
    }

}
