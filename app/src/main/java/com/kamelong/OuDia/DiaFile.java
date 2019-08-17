package com.kamelong.OuDia;

import android.content.pm.PackageInfo;

import com.kamelong.tool.SDlog;
import com.kamelong.tool.Color;
import com.kamelong.tool.Font;
import com.kamelong.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
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

    /**
     * デフォルトコンストラクタ
     */
    public DiaFile(){

    }

    /**
     * ファイルからダイヤを開く
     * @param file　入力ファイル
     * @throws Exception ファイルが読み込めなかった時に返す
     */
    public DiaFile(File file)throws Exception{
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
        System.out.println("読み込み終了");

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
    public Train getTrain(int diagramIndex,int direction,int trainIndex){
        return diagram.get(diagramIndex).trains[direction].get(trainIndex);
    }
    public int getTrainSize(int diagramIndex,int direction){
        return diagram.get(diagramIndex).trains[direction].size();
    }
    public void saveToFile(String fileName,boolean saveFilePath) throws Exception {
        FileOutputStream fos = new FileOutputStream(fileName);
        //BOM付与
        fos.write(0xef);
        fos.write(0xbb);
        fos.write(0xbf);
        fos.close();
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos)));
        out.println("FileType=OuDiaSecond.1.06");
        out.println("Rosen.");
        out.println("Rosenmei="+name);
        out.println("KudariDiaAlias="+diaNameDefault[0]);
        out.println("NoboriDiaAlias="+diaNameDefault[1]);
        for(Station s:station){
            s.saveToFile(out);
        }
        for(TrainType type:trainType){
            type.saveToFile(out);
        }
        for(Diagram dia:diagram){
            dia.saveToFile(out);
        }

        out.println("KitenJikoku="+(diagramStartTime/3600)+String.format("%02d",(diagramStartTime/60)%60));
        out.println("DiagramDgrYZahyouKyoriDefault="+ stationSpaceDefault );
        out.println("EnableOperation="+operationStyle);
        out.println("Comment="+comment.replace("\n","\\n"));
        out.println(".");

        out.println("DispProp.");
        for(Font font:timeTableFont){
            out.println("JikokuhyouFont="+font.getOuDiaString());
        }
        out.println("JikokuhyouVFont="+timeTableVFont.getOuDiaString());
        out.println("DiaEkimeiFont="+diaStationNameFont.getOuDiaString());
        out.println("DiaJikokuFont="+diaTimeFont.getOuDiaString());
        out.println("DiaRessyaFont="+diaTrainFont.getOuDiaString());
        out.println("CommentFont="+commentFont.getOuDiaString());
        out.println("DiaMojiColor="+diaTextColor.getOudiaString());
        out.println("DiaHaikeiColor="+diaBackColor.getOudiaString());
        out.println("DiaRessyaColorr="+diaTrainColor.getOudiaString());
        out.println("DiaJikuColor="+diaAxicsColor.getOudiaString());
        for(Color color :timeTableBackColor){
            out.println("JikokuhyouBackColor="+color.getOudiaString());
        }
        out.println("StdOpeTimeLowerColor="+stdOpeTimeLowerColor.getOudiaString());
        out.println("StdOpeTimeHigherColor="+stdOpeTimeHigherColor.getOudiaString());
        out.println("StdOpeTimeUndefColor="+stdOpeTimeUndefColor.getOudiaString());
        out.println("StdOpeTimeIllegalColor="+stdOpeTimeIllegalColor.getOudiaString());
        out.println("EkimeiLength="+(stationNameWidth));
        out.println("JikokuhyouRessyaWidth="+trainWidth);
        out.println("AnySecondIncDec1="+secondShift[0]);
        out.println("AnySecondIncDec2="+secondShift[1]);
        out.println("DisplayRessyamei="+(showTrainName?"1":"0"));
        out.println("DisplayOuterTerminalEkimeiOriginSide="+(showOuterTerminalOrigin?"1":"0"));
        out.println("DisplayOuterTerminalEkimeiTerminalSide="+(showOuterTerminalTerminal?"1":"0"));
        out.println("EDiagramDisplayOuterTerminal="+(showOuterTerminal?"1":"0"));
        out.println(".");
        PackageInfo packageInfo = SDlog.activity.getPackageManager().getPackageInfo(SDlog.activity.getPackageName(), 0);
        out.println("FileTypeAppComment=AOdia v"+packageInfo.versionName);
        out.close();
    }
    public void saveToOuDiaFile(String fileName) throws Exception {
        PrintWriter out = new PrintWriter
                (new BufferedWriter(new OutputStreamWriter
                        (new FileOutputStream(new File(fileName)),"Shift-JIS")));
        out.println("FileType=OuDia.1.02");
        out.println("Rosen.");
        out.println("Rosenmei="+name);
        for(Station s:station){
            s.saveToOuDiaFile(out);
        }
        for(TrainType type:trainType){
            type.saveToOuDiaFile(out);
        }
        for(Diagram dia:diagram){
            dia.saveToOuDiaFile(out);
        }

        out.println("KitenJikoku="+(diagramStartTime/3600)+String.format("%02d",(diagramStartTime/60)%60));
        out.println("DiagramDgrYZahyouKyoriDefault="+ stationSpaceDefault );
        out.println("Comment="+comment.replace("\n","\\n"));
        out.println(".");

        out.println("DispProp.");
        for(Font font:timeTableFont){
            out.println("JikokuhyouFont="+font.getOuDiaString());
        }
        out.println("JikokuhyouVFont="+timeTableVFont.getOuDiaString());
        out.println("DiaEkimeiFont="+diaStationNameFont.getOuDiaString());
        out.println("DiaJikokuFont="+diaTimeFont.getOuDiaString());
        out.println("DiaRessyaFont="+diaTrainFont.getOuDiaString());
        out.println("CommentFont="+commentFont.getOuDiaString());
        out.println("DiaMojiColor="+diaTextColor.getOudiaString());
        out.println("DiaHaikeiColor="+diaBackColor.getOudiaString());
        out.println("DiaRessyaColorr="+diaTrainColor.getOudiaString());
        out.println("DiaJikuColor="+diaAxicsColor.getOudiaString());
        out.println("EkimeiLength="+(stationNameWidth));
        out.println("JikokuhyouRessyaWidth="+trainWidth);
        out.println(".");
        PackageInfo packageInfo = SDlog.activity.getPackageManager().getPackageInfo(SDlog.activity.getPackageName(), 0);
        out.println("FileTypeAppComment=AOdia v"+packageInfo.versionName);
        out.close();
    }


}
