package com.kamelong.OuDia;

import com.kamelong.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DiaFile {


    public ArrayList<Diagram>diagram=new ArrayList<>();
    public ArrayList<Station>station=new ArrayList<>();
    public ArrayList<TrainType>trainType=new ArrayList<>();

    public String name="";
    public String version="";
    public String comment="";

    private ArrayList<Integer>stationTime=new ArrayList<>();
    public int diagramStartTime=3600*3;


    //AOdia専用オプション
    public String filePath="";
    public boolean menuOpen=true;
    public DiaFile(File file)throws Exception{
        filePath=file.getPath();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        version=br.readLine().split("=",-1)[1];
        double v=Double.parseDouble(version.substring(version.indexOf(".")+1));
        if(version.startsWith("OuDia.")||v<1.03){
            loadShiftJis(file);
        }else{
            loadDiaFile(br);
        }
        reCalcStationTime();
        System.out.println("読み込み終了");

    }
    private void loadShiftJis(File file)throws Exception{
        BufferedReader br = new ShiftJISBufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
        String nouse=br.readLine();
        loadDiaFile(br);
    }
    private void loadDiaFile(BufferedReader br){
        try {
            br.readLine();//Rosen.
            name=br.readLine().split("=",-1)[1];
            String line=br.readLine();
            while(line!=null){
                if(line.equals("Eki.")){
                    station.add(new Station(br,this));
                }
                if(line.equals("Ressyasyubetsu.")){
                    trainType.add(new TrainType(br));
                }
                if(line.equals("Dia.")){
                    diagram.add(new Diagram(this,br));
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
                }
                if(line.startsWith("Comment=")){
                    comment=line.split("=",-1)[1].replace("\\n","\n");
                }
                line=br.readLine();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getDiaNum(){
        return diagram.size();
    }
    public int getStationNum(){
        return station.size();
    }

    public void reCalcStationTime(){
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
                for(int train=0;train<getTrainSize(i,0);train++){
                    int value=getTrain(i,0,train).getRequiredTime(startStation,endStation);
                    if(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1){
                        value+=120;
                    }
                    if(value>0&&result>value){
                        result=value;
                    }
                }
                for(int train=0;train<getTrainSize(i,1);train++){
                    int value=this.getTrain(i,1,train).getRequiredTime(startStation,endStation);
                    if(getTrain(i,0,train).getStop(startStation)!=1||getTrain(i,0,train).getStop(endStation)!=1){
                        value+=120;
                    }

                    if(value>0&&result>value){
                        result=value;
                    }
                }
                if(result==360000){
                    result=120;
                }
                return result;            }
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
    public void saveToFile(String fileName){
        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            //BOM付与
            fos.write(0xef);
            fos.write(0xbb);
            fos.write(0xbf);
            fos.close();
            FileWriter out=new FileWriter(fileName,true);
            out.write("FileType=OuDiaSecond.1.04\r\n");
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
            out.write("Comment="+comment.replace("\n","\\n")+"\r\n");
            out.write(".\r\n");
            out.write("DispProp.\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック;Bold=1\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック;Itaric=1\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック;Bold=1;Itaric=1\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nJikokuhyouFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nJikokuhyouVFont=PointTextHeight=9;Facename=@ＭＳ ゴシック\r\nDiaEkimeiFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nDiaJikokuFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nDiaRessyaFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nCommentFont=PointTextHeight=9;Facename=ＭＳ ゴシック\r\nDiaMojiColor=00000000\r\nDiaHaikeiColor=00FFFFFF\r\nDiaRessyaColor=00000000\r\nDiaJikuColor=00C0C0C0\r\nJikokuhyouBackColor=00FFFFFF\r\nJikokuhyouBackColor=00F0F0F0\r\nJikokuhyouBackColor=00FFFFFF\r\nJikokuhyouBackColor=00FFFFFF\r\nStdOpeTimeLowerColor=00E0E0FF\r\nStdOpeTimeHigherColor=00FFFFE0\r\nStdOpeTimeUndefColor=0080FFFF\r\nStdOpeTimeIllegalColor=00A0A0A0\r\nEkimeiLength=6\r\nJikokuhyouRessyaWidth=8\r\nAnySecondIncDec1=10\r\nAnySecondIncDec2=-10\r\nDisplayRessyamei=1\r\nDisplayOuterTerminalEkimeiOriginSide=0\r\nDisplayOuterTerminalEkimeiTerminalSide=0\r\nDiagramDisplayOuterTerminal=0\r\n.\r\nFileTypeAppComment=OuDiaSecond Ver. 1.04.03");
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void copyDiagram(int diagramIndex,String diaName){
        diagram.add(new Diagram(diagram.get(diagramIndex)));
        diagram.get(diagram.size()-1).name=diaName;
    }

}
