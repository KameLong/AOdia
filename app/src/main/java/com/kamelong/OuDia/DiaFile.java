package com.kamelong.OuDia;

import android.content.pm.PackageInfo;

import com.kamelong.aodia.SDlog;
import com.kamelong.tool.Color;
import com.kamelong.tool.Font;
import com.kamelong.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 一つの路線ファイルを表す。
 */
public class DiaFile {
    /**
     * 路線名
     */
    public String name="";

    /**
     * ダイヤグラム開始時間
     */
    public int diagramStartTime=3600*3;
    /**
     ダイヤグラムの既定の駅間幅。

     列車設定のない駅間の、ダイヤグラムビュー上での
     縦方向の幅を『ダイヤグラムエンティティY座標』単位(秒)で指定します。

     既定値は 60 です。
     */
    public int stationSpaceDefault =60;

    /**
     * コメント
     */
    public String comment="";
    /**
     運用機能の有効無効
     路線ファイルのプロパティで変更できます
     0:運用機能は無効です。
     1:運用機能は簡易モードです。
     列車の接続のみを行い、運用番号は設定しません。
     2:運用機能は通常モードです
     規定値は0です
     */
    public int operationStyle=0;
    /**
     ダイヤの別名です。
     （例） "北行" など
     */
    public String[] diaNameDefault={"下り時刻表","上り時刻表"};

    /**
     * 駅一覧
     */
    public ArrayList<Station>station=new ArrayList<>();
    /**
     * 列車種別一覧
     */
    public ArrayList<TrainType>trainType=new ArrayList<>();
    /**
     * ダイヤ一覧
     */
    public ArrayList<Diagram>diagram=new ArrayList<>();


    /**
     * OuDiaバージョン
     */
    public String version="";




    /**
     * その他AOdiaで使わないパラメーター
     */
    public HashMap<String,String> parameterRosen =new HashMap<>();
    public HashMap<String,String> parameterDispProp =new HashMap<>();

    /**
     * 駅間の所要時間
     */
    private ArrayList<Integer>stationTime=new ArrayList<>();
    //DispProp
    /**
     * 時刻表フォント
     */
    public ArrayList<Font>timeTableFont=new ArrayList<>();
    /**
     * 時刻表Vフォント
     * 縦書きに使う？
     */
    public Font timeTableVFont=new Font();
    /**
     * ダイヤ駅名フォント
     */
    public Font diaStationNameFont=new Font();
    /**
     * ダイヤ時刻フォント
     */
    public Font diaTimeFont=new Font();

    /**
     * ダイヤ列車フォント
     */
    public Font diaTrainFont=new Font();
    /**
     * コメントフォント
     */
    public Font commentFont=new Font();
    /**
     * ダイヤ文字色
     */
    public Color diaTextColor =new Color();
    /**
     * ダイヤ背景色
     */
    public Color diaBackColor=new Color();
    /**
     * ダイヤ列車色
     */
    public Color diaTrainColor=new Color();
    /**
     * ダイヤ軸色
     */
    public Color diaAxicsColor=new Color();
    /**
     * 時刻表背景色
     */
    public ArrayList<Color>timeTableBackColor=new ArrayList<>();
    /**
     * StdOpeTimeLowerColor
     */
    public Color stdOpeTimeLowerColor=new Color();
    /**
     * StdOpeTimeHigherColor
     */
    public Color stdOpeTimeHigherColor=new Color();
    /**
     * StdOpeTimeUndefColor
     */
    public Color stdOpeTimeUndefColor=new Color();
    /**
     * StdOpeTimeIllegalColor
     */
    public Color stdOpeTimeIllegalColor=new Color();
    /**
     * 駅名の長さ
     */
    public int stationNameWidth=6;
    /**
     * 時刻用列車幅
     */
    public int trainWidth=5;
    /**
     * 秒単位移動量
     * 二つある
     */
    public int[] secondShift={5,15};
    /**
     * 列車名欄表示
     */
    public boolean showTrainName=true;
    /**
     * 路線外終着駅を起点側に表示する
     */
    public boolean showOuterTerminalOrigin=false;
    /**
     * 路線外終着駅を終点側に表示する
     */
    public boolean showOuterTerminalTerminal=false;
    /**
     * 路線外終着駅を表示するか
     */
    public boolean showOuterTerminal=false;

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
        version="OuDiaSecond.1.06";
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
        int direction=0;
        station=new ArrayList<>();
        trainType=new ArrayList<>();
        diagram=new ArrayList<>();
        String property="";
        Stack<String> propertyStack=new Stack<>();
        String line=br.readLine();
        Station tempStation=new Station(this);
        StationTrack tempTrack=new StationTrack();
        OuterTerminal tempOuterTerminal=new OuterTerminal();
        TrainType tempType=new TrainType();
        Diagram tempDia=new Diagram(this);
        Train tempTrain=new Train(this,0);
        while(line!=null){
            if(line.equals(".")){
                //読み込みプロパティ終了
                property=propertyStack.pop();
            }else if(line.endsWith(".")){
                property=line.substring(0,line.length()-1);
                switch(property){
                    case "Eki":
                        tempStation=new Station(this);
                        station.add(tempStation);
                        break;
                    case "EkiTrack2":
                        tempTrack=new StationTrack();
                        tempStation.tracks.add(tempTrack);
                        break;
                    case "OuterTerminal":
                        tempOuterTerminal=new OuterTerminal();
                        tempStation.outerTerminals.add(tempOuterTerminal);
                        break;
                    case "Ressyasyubetsu":
                        tempType=new TrainType();
                        trainType.add(tempType);
                        break;
                    case "Dia":
                        tempDia=new Diagram(this);
                        diagram.add(tempDia);
                        break;
                    case "Kudari":
                        direction=0;
                        break;
                    case "Nobori":
                        direction=1;
                    case "Ressya":
                        tempTrain=new Train(this,direction);
                        tempDia.trains[direction].add(tempTrain);
                }
            }
            if(line.contains("=")){
                String title=line.substring(0,line.indexOf("="));
                String value=line.substring(line.indexOf("=")+1);
                switch(property){
                    case "Eki":
                        tempStation.setValue(title,value);
                        break;
                    case "EkiTrack2":
                        tempTrack.setValue(title,value);
                        break;
                    case "OuterTerminal":
                        tempOuterTerminal.setValue(title,value);
                        break;
                    case "Ressyasyubetsu":
                        tempType.setValue(title,value);
                        break;
                    case "Dia":
                        tempDia.setValue(title,value);
                        break;
                    case "Ressya":
                        tempTrain.setValue(title,value);
                        break;
                    case "Rosen":
                        this.setValue(title,value);
                        break;
                    case "DispProp":
                        this.setValue(title,value);
                        break;
                }
            }

            line=br.readLine();
        }
    }
    private void setValue(String title,String value){
        switch(title){
            case "Rosenmei":
                name=value;
                break;
            case "KudariDiaAlias":
                if(value.length()!=0){
                    diaNameDefault[0]=value;
                }
                break;
            case "NoboriDiaAlias":
                if(value.length()!=0){
                    diaNameDefault[1]=value;
                }
                break;
            case "KitenJikoku":
                switch(value.length()){
                    case 3:
                        diagramStartTime=3600*Integer.parseInt(value.substring(0,1))+60*Integer.parseInt(value.substring(1,3));
                        break;
                    case 4:
                        diagramStartTime=3600*Integer.parseInt(value.substring(0,2))+60*Integer.parseInt(value.substring(2,4));
                        break;
                    default:
                        diagramStartTime=60*Integer.parseInt(value);
                }
                break;
            case "EnableOperation":
                operationStyle=Integer.parseInt(value);
                break;
            case "Comment":
                comment=value.replace("\\n","\n");
                break;
            case "JikokuhyouFont":
                timeTableFont.add(new Font(value));
                break;
            case "JikokuhyouVFont":
                timeTableVFont=new Font(value);
                break;
            case "DiaEkimeiFont":
                diaStationNameFont=new Font(value);
                break;
            case "DiaJikokuFont":
                diaTimeFont=new Font(value);
                break;
            case "DiaRessyaFont":
                diaTrainFont=new Font(value);
                break;
            case "CommentFont":
                commentFont=new Font(value);
                break;
            case "DiaMojiColor":
                diaTextColor.setOuDiaColor(value);
                break;
            case "DiaHaikeiColor":
                diaBackColor.setOuDiaColor(value);
                break;
            case "DiaRessyaColor":
                diaTrainColor.setOuDiaColor(value);
                break;
            case "DiaJikuColor":
                diaAxicsColor.setOuDiaColor(value);
                break;
            case "JikokuhyouBackColor":
                Color color=new Color();
                color.setOuDiaColor(value);
                timeTableBackColor.add(color);
                break;
            case "StdOpeTimeLowerColor":
                stdOpeTimeLowerColor.setOuDiaColor(value);
                break;
            case "StdOpeTimeHigherColor":
                stdOpeTimeHigherColor.setOuDiaColor(value);
                break;
            case "StdOpeTimeUndefColor":
                stdOpeTimeUndefColor.setOuDiaColor(value);
                break;
            case "StdOpeTimeIllegalColor":
                stdOpeTimeIllegalColor.setOuDiaColor(value);
                break;
            case "EkimeiLength":
                stationNameWidth=Integer.parseInt(value);
                break;
            case "JikokuhyouRessyaWidth":
                trainWidth=Integer.parseInt(value);
                break;
            case "AnySecondIncDec1":
                secondShift[0]=Integer.parseInt(value);
                break;
            case "AnySecondIncDec2":
                secondShift[1]=Integer.parseInt(value);
                break;
            case "DisplayRessyamei":
                showTrainName=value.equals("1");
                break;
            case "DisplayOuterTerminalEkimeiOriginSide":
                showOuterTerminalOrigin=value.equals("1");
                break;
            case "DisplayOuterTerminalEkimeiTerminalSide":
                showOuterTerminalTerminal=value.equals("1");
                break;
            case "DiagramDisplayOuterTerminal":
                showOuterTerminal=value.equals("1");
                break;
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
        out.write("FileType=OuDiaSecond.1.06\r\n");
        out.write("Rosen.\r\n");
        out.write("Rosenmei="+name+"\r\n");
        out.write("KudariDiaAlias="+diaNameDefault[0]+"\r\n");
        out.write("NoboriDiaAlias="+diaNameDefault[1]+"\r\n");
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
        out.write("DiagramDgrYZahyouKyoriDefault="+ stationSpaceDefault +"\r\n");
        out.write("EnableOperation="+operationStyle+"\r\n");
        if(saveFilePath) {
            out.write("AOdiaFilePath=" + filePath + "\r\n");
        }
        out.write("Comment="+comment.replace("\n","\\n")+"\r\n");
        for(Map.Entry<String,String> value :parameterRosen.entrySet()){
            out.write(value.getKey()+"="+value.getValue()+"\r\n");
        }
        out.write(".\r\n");

        out.write("DispProp.\r\n");
        for(Font font:timeTableFont){
            out.write("JikokuhyouFont="+font.getOuDiaString()+"\r\n");
        }
        out.write("JikokuhyouVFont="+timeTableVFont.getOuDiaString()+"\r\n");
        out.write("DiaEkimeiFont="+diaStationNameFont.getOuDiaString()+"\r\n");
        out.write("DiaJikokuFont="+diaTimeFont.getOuDiaString()+"\r\n");
        out.write("DiaRessyaFont="+diaTrainFont.getOuDiaString()+"\r\n");
        out.write("CommentFont="+commentFont.getOuDiaString()+"\r\n");
        out.write("DiaMojiColor="+diaTextColor.getOudiaString()+"\r\n");
        out.write("DiaHaikeiColor="+diaBackColor.getOudiaString()+"\r\n");
        out.write("DiaRessyaColorr="+diaTrainColor.getOudiaString()+"\r\n");
        out.write("DiaJikuColor="+diaAxicsColor.getOudiaString()+"\r\n");
        for(Color color :timeTableBackColor){
            out.write("JikokuhyouBackColor="+color.getOudiaString()+"\r\n");
        }
        out.write("StdOpeTimeLowerColor="+stdOpeTimeLowerColor.getOudiaString()+"\r\n");
        out.write("StdOpeTimeHigherColor="+stdOpeTimeHigherColor.getOudiaString()+"\r\n");
        out.write("StdOpeTimeUndefColor="+stdOpeTimeUndefColor.getOudiaString()+"\r\n");
        out.write("StdOpeTimeIllegalColor="+stdOpeTimeIllegalColor.getOudiaString()+"\r\n");
        out.write("EkimeiLength="+(stationNameWidth+"\r\n");
        out.write("JikokuhyouRessyaWidth="+trainWidth+"\r\n");
        out.write("AnySecondIncDec1="+secondShift[0]+"\r\n");
        out.write("AnySecondIncDec2="+secondShift[1]+"\r\n");
        out.write("DisplayRessyamei="+(showTrainName?"1":"0")+"\r\n");
        out.write("DisplayOuterTerminalEkimeiOriginSide="+(showOuterTerminalOrigin?"1":"0")+"\r\n");
        out.write("DisplayOuterTerminalEkimeiTerminalSide="+(showOuterTerminalTerminal?"1":"0")+"\r\n");
        out.write("EDiagramDisplayOuterTerminal="+(showOuterTerminal?"1":"0")+"\r\n");
        out.write(".\r\n");
        PackageInfo packageInfo = SDlog.activity.getPackageManager().getPackageInfo(SDlog.activity.getPackageName(), 0);
        out.write("FileTypeAppComment=AOdia v"+packageInfo.versionName+"\r\n");
        out.close();


    }
    public void copyDiagram(int diagramIndex,String diaName){
        diagram.add((Diagram) diagram.get(diagramIndex).clone());
        diagram.get(diagram.size()-1).name=diaName;
    }
    public void addNewDiagram(){
        diagram.add(new Diagram(this));
    }
    public void deleteDiagram(int diagramIndex){
        diagram.remove(diagramIndex);
    }

}
