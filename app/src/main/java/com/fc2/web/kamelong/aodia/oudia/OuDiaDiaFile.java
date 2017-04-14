package com.fc2.web.kamelong.aodia.oudia;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.fc2.web.kamelong.aodia.SdLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
    public OuDiaDiaFile(Context context,File file){
        activity=(Activity)context;
        try {
            FileInputStream is = new FileInputStream(file);
            filePath=file.getPath();
            if(file.getPath().endsWith(".oud")){
                InputStreamReader filereader = new InputStreamReader(is, "SJIS");
                BufferedReader br = new BufferedReader(filereader);
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
                                        t.setTime(line.split("=",-1)[1], direct);
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
                            mTrainType.setTextColor(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("DiagramSenColor")){
                            mTrainType.setDiaColor(line.split("=",-1)[1]);
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
                            mStation.setTimeShow(line.split("=",-1)[1]);
                        }
                        if(line.split("=",-1)[0].equals("Ekikibo")){
                            mStation.setSize(line.split("=",-1)[1]);
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

}
