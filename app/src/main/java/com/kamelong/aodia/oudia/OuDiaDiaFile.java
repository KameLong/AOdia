package com.kamelong.aodia.oudia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.kamelong.aodia.SdLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kame on 2017/02/16.
 */

public class OuDiaDiaFile extends DiaFile {
    /**
     * 非推奨コンストラクタ。
     * @param context MainActivity
     */
    public OuDiaDiaFile (Context context){
        activity=(Activity)context;
        loadSample();
    }
    /**
     * 推奨コンストラクタ。
     * @param context MainActivityになると思われる
     * @param file 開きたいファイル @code null then サンプルファイルを開く
     *
     * コンストラクタでは読み込みファイルが与えられるので、そのファイルを読み込む。
     * 読み込む処理はloadDia,loadNetgramに書かれているので適宜呼び出す。
     * oudファイルはShiftJisで書かれているので考慮する必要がある。
     *
     * ダイヤを読み込んだ後に最小所要時間を別スレッドで作成する。
     * 最小所要時間を必要とする際は、この計算が終了しているかどうかをチェックする必要がある。
     *
     * fileがnullの時はnullPointerExceptionが発生するため、サンプルを読み込む。
     * その他のエラーが発生した際にはToastでエラーを吐いた後、アプリを終了する。
     *
     */
    InputStream is;
    boolean download=true;
    public OuDiaDiaFile(Context context,final File file){
        activity=(Activity)context;
        try {
            filePath=file.getPath();
            if(file.getPath().startsWith("http")){
                download=true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            is = new URL(filePath).openStream();
                            InputStreamReader filereader = new InputStreamReader(is, "Shift_JIS");
                            BufferedReader br = new ShiftJISBufferedReader(filereader);
                            loadDia(br);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        download=false;


                    }
                }).start();
                try {
                    while (download) {
                        Thread.sleep(100);

                    }
                }catch(Exception e){

                }
                return;

            }
            is= new FileInputStream(file);
            if(file.getPath().endsWith(".oud")||file.getPath().endsWith(".oud2")){
                InputStreamReader filereader = new InputStreamReader(is, "Shift_JIS");
                BufferedReader br = new ShiftJISBufferedReader(filereader);
                loadDia(br);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //別スレッドで最小所要時間を計算する
                    calcMinReqiredTime();
                }
            }).start();

        }catch(NullPointerException e){
            loadSample();
        } catch (Exception e) {
            Toast.makeText(activity, "ファイルを開くことができませんでした。", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    /**
     * サンプルファイル（sample.oud）を読み込む。
     * 最終的にはloadDiaを呼び出す
     */
    private void loadSample(){
        try {
            System.out.println(activity.getExternalFilesDir(null).getPath()+"/files/sample.oud");
            FileInputStream is = new FileInputStream(new File(activity.getExternalFilesDir(null).getPath()+"/sample.oud"));
            InputStreamReader filereader = new InputStreamReader(is, "SJIS");
            BufferedReader br = new BufferedReader(filereader);
            loadDia(br);
            filePath=activity.getExternalFilesDir(null).getPath()+"/sample.oud";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //別スレッドで最小所要時間を計算する
                    calcMinReqiredTime();
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(activity, "サンプルダイヤを開けません。致命的なエラーが発生しました。", Toast.LENGTH_LONG).show();
            activity.finish();
            e.printStackTrace();
        }
    }
    /**
     * oudファイルを読み込んでオブジェクトを構成する。
     * @param br  BufferReader of .oud fille.  forbidden @null
     */
    private void loadDia(BufferedReader br){
        int diaNum=-1;
        try{
            String line="";
            while((line=br.readLine())!=null) {
                if(line.equals("Dia.")){
                    line=br.readLine();
                    diaName.add(line.split("=",-1)[1]);
                    ArrayList<Train>[] trainArray=new ArrayList[2];
                    trainArray[0]=new ArrayList<Train>();
                    trainArray[1]=new ArrayList<Train>();
                    while(!line.equals(".")){
                        if(line.equals("Ressya.")){
                            int direct=0;
                            Train t=new Train(this);
                            while(!line.equals(".")){

                                if(line.split("=",-1)[0].equals("Houkou")){
                                    if(line.split("=",-1)[1].equals("Kudari")){
                                        direct=0;
                                    }
                                    if(line.split("=",-1)[1].equals("Nobori")){
                                        direct=1;
                                    }
                                }

                                if(line.split("=",-1)[0].equals("Syubetsu")) {
                                    t.setType(Integer.parseInt(line.split("=",-1)[1]));
                                }
                                if(line.split("=",-1)[0].equals("Ressyamei")) {
                                    t.setName(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Gousuu")) {
                                    t.setCount(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Ressyabangou")) {
                                    t.setNumber(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("Bikou")) {
                                    t.setRemark(line.split("=",-1)[1]);
                                }

                                if(line.split("=",-1)[0].equals("EkiJikoku")) {
                                    try {
                                        setTrainTime(t,line.split("=",-1)[1], direct);
                                    }catch(Exception e){
                                        SdLog.log(e);
                                    }
                                }

                                line=br.readLine();
                            }
                            if(direct!=-1) {
                                trainArray[direct].add(t);
                            }
                        }
                        line=br.readLine();
                        //Diaの終わりは２つの終了行が並んだ時
                        if(line.equals(".")){
                            line=br.readLine();
                        }
                    }
                    trainArray[0].trimToSize();
                    trainArray[1].trimToSize();
                    train.add(trainArray);
                }
                if(line.equals("Ressyasyubetsu.")){
                    TrainType mTrainType=new TrainType();
                    while(!line.equals(".")){
                        if(line.split("=",-1)[0].equals("Syubetsumei")){
                            mTrainType.setName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ryakusyou")){
                            mTrainType.setShortName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("JikokuhyouMojiColor")){
                            setTrainTypeTextColor(mTrainType,line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenColor")){
                            setTrainTypeDiaColor(mTrainType,line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenStyle")){
                            mTrainType.setLineStyle(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenIsBold")){
                            mTrainType.setLineBold(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("StopMarkDrawType")){
                            mTrainType.setShowStop(line.split("=",-1)[1]);
                        }
                        line=br.readLine();
                    }
                    trainType.add(mTrainType);
                }
                if(line.equals("Eki.")){
                    Station mStation=new Station();
                    while(!line.equals(".")){
                        if(line.split("=",-1)[0].equals("Ekimei")){
                            mStation.setName(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ekijikokukeisiki")){
                            setStationTimeShow(mStation,line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ekikibo")){
                            setStationSize(mStation,line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Kyoukaisen")){
                            mStation.setBorder(Integer.valueOf(line.split("=",-1)[1]));
                        }
                        line=br.readLine();
                    }
                    station.add(mStation);
                }
                if(line.equals("Rosen.")){
                    line=br.readLine();
                    lineName=line.split("=",-1)[1];
                }
                if(line.split("=",-1)[0].equals("Comment")){
                    comment=line.split("=",-1)[1];
                    comment=comment.replace("\\n","\n");
                }
            }
        }catch(Exception e1){
            e1.printStackTrace();
            Toast.makeText(activity, "ファイルの読み込みに失敗しました。", Toast.LENGTH_LONG).show();
        }
        if(checkDiaFile()){

        }else{
            Toast.makeText(activity, "ファイルの読み込みに失敗しました。", Toast.LENGTH_LONG).show();
        }
        calcMinReqiredTime();
    }
    /**
     * OuDiaのEkikiboの文字列から駅規模を入力する。
     * @param value OuDiaファイル内のEkikiboの文字列
     */
    public void setStationSize(Station station,String value){
        switch (value){
            case "Ekikibo_Ippan":
                station.setSize(0);
                break;
            case "Ekikibo_Syuyou":
                station.setSize(1);
                break;

            case "0":
                station.setSize(0);
                break;
            case "1":
                station.setSize(1);
                break;
        }
    }
    /**
     * OuDiaのJikokukeisikiの文字列から時刻表示形式を入力する。
     * @param value OuDiaファイル内のJikokukeisikiの文字列
     */
    public void setStationTimeShow(Station station,String value){
        switch (value){
            case "Jikokukeisiki_Hatsu":
                station.setTimeShow(5);
                break;
            case "Jikokukeisiki_Hatsuchaku":
                station.setTimeShow(15);
                break;
            case "Jikokukeisiki_NoboriChaku":
                station.setTimeShow(9);
                break;
            case "Jikokukeisiki_KudariChaku":
                station.setTimeShow(6);
                break;
            case "Jikokukeisiki_KudariHatsuchaku":
                station.setTimeShow(7);
                break;
            case "Jikokukeisiki_NoboriHatsuchaku":
                station.setTimeShow(13);
                break;

        }
    }
    /**
     * この列車の発着時刻を入力します。
     * oudiaのEkiJikoku形式の文字列を発着時刻に変換し、入力していきます。
     * @param str　oudiaファイル　EkiJikoku=の形式の文字列
     * @param direct　方向
     */
    public void setTrainTime(Train train,String str,int direct){
        try {
            String[] timeString = str.split(",");
            for (int i = 0; i < timeString.length; i++) {
                if (timeString[i].length() == 0) {
                    train.setStopType((1 - 2 * direct) * i + direct * (getStationNum()- 1), Train.STOP_TYPE_NOSERVICE);
                } else {
                    if (!timeString[i].contains(";")) {
                        train.setStopType((1 - 2 * direct) * i + direct * (getStationNum() - 1),Integer.parseInt(timeString[i]));
                    } else {
                        train.setStopType((1 - 2 * direct) * i + direct * (getStationNum()- 1), Integer.parseInt(timeString[i].split(";")[0]));
                        try {
                            String stationTime = timeString[i].split(";")[1];
                            if (!stationTime.contains("/")) {
                                train.setDepartTime((1 - 2 * direct) * i + direct * (getStationNum() - 1), stationTime);
                            } else {
                                if( stationTime.split("/").length==2) {
                                    train.setArriveTime((1 - 2 * direct) * i + direct * (getStationNum() - 1), stationTime.split("/")[0]);
                                    train.setDepartTime((1 - 2 * direct) * i + direct * (getStationNum()- 1), stationTime.split("/")[1]);
                                }else{
                                    train.setArriveTime((1 - 2 * direct) * i + direct * (getStationNum()- 1), stationTime.split("/")[0]);
                                }
                            }
                        } catch (Exception e) {
                            SdLog.log(e);
                        }
                    }
                }
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }
    /**
     * 時刻表文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     *  netgramの色表記は"#rrggbb"の7文字の文字列
     *              これらの違いを踏まえつつ、int型の色を作成します。
     * @param color 色を表す文字列
     */
    public void setTrainTypeTextColor(TrainType type,String color){
            int blue=Integer.parseInt(color.substring(2,4),16);
            int green=Integer.parseInt(color.substring(4,6),16);
            int red=Integer.parseInt(color.substring(6,8),16);
            type.setTextColor(Color.rgb(red,green,blue));
    }
    /**
     * ダイヤグラム文字色をセットする
     *  oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     *  netgramの色表記は"#rrggbb"の7文字の文字列
     *              これらの違いを踏まえつつ、int型の色を作成します。
     * @param color 色を表す文字列
     */
    public void setTrainTypeDiaColor(TrainType type,String color) {
            int blue=Integer.parseInt(color.substring(2,4),16);
            int green=Integer.parseInt(color.substring(4,6),16);
            int red=Integer.parseInt(color.substring(6,8),16);
            type.setDiaColor(Color.rgb(red,green,blue));
    }
    /*
    public void setStationTime(int station,String str){
        if(str.contains(";")){
            if(str.split(";",-1)[0].contains("_")){
                setStopType(station,Integer.parseInt(str.split(";",-1)[0].split("_",-1)[0]));
            }else{
                setStopType(station,Integer.parseInt(str.split(";",-1)[0]));
            }
            if(str.split(";",-1)[1].contains("/")){
                setArriveTime(station,str.split(";",-1)[1].split("/",-1)[0]);
                setDepartTime(station,str.split(";",-1)[1].split("/",-1)[1]);
            }else{
                setDepartTime(station,str.split(";",-1)[1]);
            }
        }else{
            setStopType(station,STOP_TYPE_NOSERVICE);
        }
    }
     */



}
