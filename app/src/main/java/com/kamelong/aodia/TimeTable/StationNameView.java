package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Station;
import com.kamelong.aodia.AOdiaDefaultView;

public class StationNameView extends AOdiaDefaultView {
    private DiaFile diaFile;
    private int direct;
    public StationNameView(Context context,DiaFile diaFile,int direct){
        super(context);
        this.diaFile=diaFile;

        this.direct=direct;
    }
    public void onDraw(Canvas canvas){
        int startLine=0;
        for(int i=0;i<diaFile.getStationNum();i++){
            int stationNumber=(diaFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station=diaFile.station.get(stationNumber);
            switch (station.getTimeTableStyle(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    startLine=startLine+textSize;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    break;
                case 2:
                    //着のみ
                    startLine=startLine+textSize;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    break;
                case 3:
                    //発着
                    startLine=startLine+textSize*13/6;
                    canvas.drawText(station.name, 1,startLine-(int)(textPaint.getTextSize()*3/6), blackBig);
                    break;
                case 5:
                    //発番線
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    canvas.drawText("出発番線", 1,startLine, blackPaint);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    break;
                case 6:
                    //着番線
                    startLine+=textSize ;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    canvas.drawText("到着番線", 1,startLine, blackPaint);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    break;
                case 7:
                    //発着番線
                    startLine+=textSize ;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    canvas.drawText("着発番線", 1,startLine, blackPaint);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                    break;
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(diaFile.station.get(checkStation).getBorder()){
                startLine=startLine+(int)(textPaint.getTextSize()*1/3);
                canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);
            }
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("showRemark",true)){
            int startY = (int) (this.getHeight() - 9.3f * textSize);
            canvas.drawLine(0, startY, getWidth(), startY, blackBPaint);
            int startX=(getWidth()-textSize)/2;
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("備",startX,startY,blackPaint);
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("考",startX,startY,blackPaint);
        }


    }
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*5);
    }
    public int getYsize(){
        int startLine=0;
        for(int i=0;i<diaFile.getStationNum();i++){
            int stationNumber=(diaFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station=diaFile.station.get(stationNumber);
            switch (station.getTimeTableStyle(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    startLine=startLine+textSize;
                    break;
                case 2:
                    //着のみ
                    startLine=startLine+textSize;
                    break;
                case 3:
                    //発着
                    startLine=startLine+textSize*13/6;
                    break;
                case 5:
                    //発番線
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    break;
                case 6:
                    //着番線
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    break;
                case 7:
                    //発着番線
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    break;
            }
            //もし境界線が存在する駅なら境界線を考える
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(diaFile.station.get(checkStation).getBorder()){
                startLine+=(textSize*1/3);
            }
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("showRemark",true)){
            startLine+=(int)(textSize*9.4f);
        }
        startLine+=textSize/3;
        return startLine;
    }
    public int getStationFromY(int posY){
        int startLine=0;
        for(int i=0;i<diaFile.getStationNum();i++){
            int stationNumber=(diaFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station=diaFile.station.get(stationNumber);
            switch (station.getTimeTableStyle(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    startLine=startLine+textSize;
                    break;
                case 2:
                    //着のみ
                    startLine=startLine+textSize;
                    break;
                case 3:
                    //発着
                    startLine=startLine+textSize*13/6;
                    break;
                case 5:
                    //発番線
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    break;
                case 6:
                    //着番線
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    break;
                case 7:
                    //発着番線
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    startLine+=textSize / 5;
                    startLine+=textSize ;
                    break;
            }
            //もし境界線が存在する駅なら境界線を考える
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(diaFile.station.get(checkStation).getBorder()){
                startLine+=(textSize*1/3);
            }
            if(posY<startLine){
                if(stationNumber<0){
                    stationNumber=0;
                }
                return stationNumber;
            }
        }
        return -1;
    }



}
