package com.fc2.web.kamelong.aodia.oudia;

import android.content.Context;

import com.fc2.web.kamelong.aodia.SdLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by kame on 2017/02/16.
 */

public class NetgramDiaFile extends DiaFile {
    private String lineID="";
    private ArrayList<String> diaId=new ArrayList<>();
    NetgramDiaFile(Context context,File directory){

    }
    private void loadNetgramFolder(File directory){
        try{
            File folderInfomation=new File(directory.getPath()+"hogehoge");
            BufferedReader folderBr = new BufferedReader(new FileReader(folderInfomation));
            String folderLine=folderBr.readLine();
            folderLine=folderBr.readLine();
            lineID=folderLine.split(",",-1)[0];
            lineName=folderLine.split(",",-1)[1];
            while(folderLine!=null){
                diaId.add(folderLine.split(",",-1)[0]);
                diaName.add(folderLine.split(",",-1)[1]);
                folderLine=folderBr.readLine();

            }

        }catch(Exception e){
            SdLog.log(e);
        }
        try{
            File stationInformation=new File(directory.getPath()+"hogehoge");
            BufferedReader stationBr = new BufferedReader(new FileReader(stationInformation));
            String stationLine=stationBr.readLine();
            stationLine=stationBr.readLine();
            stationLine=stationBr.readLine();
            while(stationLine!=null){
                Station mStation=new Station();
                mStation.setName(stationLine.split(",",-1)[1]);
                mStation.setTimeShow(stationLine.split(",",-1)[2]);
                mStation.setSize(stationLine.split(",",-1)[3]);
                mStation.setBorder(Integer.valueOf(stationLine.split(",",-1)[4]));
                station.add(mStation);
                stationLine=stationBr.readLine();
            }
        }catch(Exception e){
            SdLog.log(e);
        }
        try{
            File trainTypeInformation=new File(directory.getPath()+"hogehoge");
            BufferedReader trainTypeBr = new BufferedReader(new FileReader(trainTypeInformation));
            String trainTypeLine=trainTypeBr.readLine();
            trainTypeLine=trainTypeBr.readLine();
            trainTypeLine=trainTypeBr.readLine();
            while(trainTypeLine!=null){
                TrainType mTrainType=new TrainType();
                mTrainType.setName(trainTypeLine.split(",")[1]);
                mTrainType.setShortName(trainTypeLine.split(",")[2]);
                mTrainType.setTextColor(trainTypeLine.split(",")[3]);
                mTrainType.setDiaColor(trainTypeLine.split(",")[3]);
                mTrainType.setLineStyle(trainTypeLine.split(",")[4]);
                mTrainType.setLineBold(trainTypeLine.split(",")[5]);
                trainTypeLine=trainTypeBr.readLine();
            }
        }catch(Exception e){
            SdLog.log(e);
        }
        for(int dia=0;dia<getDiaNum();dia++){
            for(int direct=0;direct<2;direct++) {
                try {
                    File trainInformation = new File(directory.getPath() + "hogehoge");
                    BufferedReader trainBr = new BufferedReader(new FileReader(trainInformation));
                    String trainLine = trainBr.readLine();
                    trainLine = trainBr.readLine();
                    trainLine = trainBr.readLine();
                    while (trainLine != null) {
                        Train mTrain = new Train(this);
                        mTrain.setType(Integer.parseInt(trainLine.split(",")[1]));
                        mTrain.setName(trainLine.split(",")[2]);
                        mTrain.setNumber(trainLine.split(",")[3]);
                        for (int s = 0; s < getStationNum(); s++) {
                            mTrain.setStationTime(s, trainLine.split(",")[5 + s]);
                        }
                        train.get(dia)[direct].add(mTrain);
                        trainLine = trainBr.readLine();
                    }
                } catch (Exception e) {

                }
            }

        }



    }
}
