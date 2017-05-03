package com.fc2.web.kamelong.aodia.GTMF;

import android.app.Activity;

import com.fc2.web.kamelong.aodia.SdLog;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * GTMFを読み込むためのクラス
 */
public class GTMFFile {
    private String folderPath;
    private ArrayList<String>tripIDList=new ArrayList<>();
    private ArrayList<String>routeIDList=new ArrayList<>();
    private ArrayList<String>serviceIDList=new ArrayList<>();

    public ArrayList<GTMFTrain>trainList=new ArrayList<>();
    public ArrayList<GTMFStation>stationList=new ArrayList<>();

    /**
     * zipファイルのパスからzipを展開する、
     * @param zipFilePath
     */
    public GTMFFile(Activity activity,String zipFilePath) {
        ZipInputStream in = null;
        ZipEntry zipEntry = null;
        BufferedOutputStream out = null;

        int len = 0;
        try {
            folderPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
            File folder = new File(folderPath);
            folder.mkdir();
            in = new ZipInputStream(new FileInputStream(zipFilePath));


            // ZIPファイルに含まれるエントリに対して順にアクセス
            while ((zipEntry = in.getNextEntry()) != null) {
                File newfile = new File(zipEntry.getName());


                // 出力用ファイルストリームの生成
                out = new BufferedOutputStream(
                        new FileOutputStream(folder + "/" + newfile.getName())
                );

                // エントリの内容を出力
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                in.closeEntry();
                out.close();
                out = null;
            }
        } catch (Exception e) {
            SdLog.log(e);
        }
        loadStops();
        loadTrips();
        loadStopTimes();
        System.out.println(stationList);
    }
    /**
     stop_times.txtを読み込む
     */
    private void loadStopTimes(){
        File stopTimes=new File(folderPath+"/stop_times.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(stopTimes));
            String str=br.readLine();
            str=br.readLine();
            String tripID=null;
            GTMFTrain mtrain=new GTMFTrain();
            while(str!=null){
                String[] strs=str.split(",");
                if(!strs[0].equals(tripID)){
                    int tripIndex=tripIDList.indexOf(strs[0]);
                    mtrain=new GTMFTrain(routeIDList.get(tripIndex),serviceIDList.get(tripIndex),tripIDList.get(tripIndex));
                    trainList.add(mtrain);
                    tripID=strs[0];
                }
                mtrain.addNewLine(strs);
                str=br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadTrips(){
        File stopTimes=new File(folderPath+"/trips.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(stopTimes));
            String str=br.readLine();
            str=br.readLine();
            while(str!=null){
                routeIDList.add(str.split(",")[0]);
                serviceIDList.add(str.split(",")[1]);
                tripIDList.add(str.split(",")[2]);
                str=br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadStops(){
        File stopTimes=new File(folderPath+"/stops.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(stopTimes));
            String str=br.readLine();
            str=br.readLine();
            String stopCode=null;
            GTMFStation station=new GTMFStation();
            while(str!=null){
                String[] strs=str.split(",");
                if(!strs[1].equals(stopCode)){
                    stopCode=strs[1];
                    station=new GTMFStation();
                    station.setName(strs[2]);
                    station.stopCode=stopCode;
                    stationList.add(station);
                }
                station.stopID.add(strs[0]);
                str=br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
