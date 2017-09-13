package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadata.AOdiaStation;
/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */
/**
 * Created by Owner on 2016/11/21.
 */

public class StationNameView extends KLView {
    private AOdiaDiaFile dia;
    private int direct;
    StationNameView(Context context, AOdiaDiaFile diaFile,int d){
        super(context);
        dia=diaFile;
        direct=d;
    }
    public void onDraw(Canvas canvas){
        int startLine=(int)blackPaint.getTextSize();
        for(int i=0;i<dia.getStationNum();i++){
            int stationNumber=(dia.getStationNum()-1)*direct+(1-2*direct)*i;
            AOdiaStation station=dia.getStation(stationNumber);
            switch (station.getTimeShow(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    canvas.drawText(dia.getStation(stationNumber).getName(), 1,startLine, blackPaint);
                    startLine=startLine+(int)blackPaint.getTextSize();
                    break;
                case 2:
                    //着のみ
                    canvas.drawText(dia.getStation(stationNumber).getName(), 1,startLine, blackPaint);
                    startLine=startLine+(int)blackPaint.getTextSize();
                    break;
                case 3:
                    //発着
                    canvas.drawText(dia.getStation(stationNumber).getName(), 1,startLine+(int)(textPaint.getTextSize()*4/6), blackBig);
                    startLine=startLine+(int)(textPaint.getTextSize()*13/6);
                    break;
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(dia.getStation(checkStation).border()){
                canvas.drawLine(0, startLine-(int)(textPaint.getTextSize()*4/5),this.getWidth()-1,startLine-(int)(textPaint.getTextSize()*4/5),blackPaint);
                startLine=startLine+(int)(textPaint.getTextSize()*1/6);
            }
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("remark",false)){
            int startY = (int) (this.getHeight() - 9.3f * textSize);
            canvas.drawLine(0, startY, getWidth(), startY, blackBPaint);
            int startX=(getWidth()-textSize)/2;
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("備",startX,startY,blackPaint);
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("考",startX,startY,blackPaint);
        }
    }
    public int getYsize(){
        int result=textSize;
        for(int i=0;i<dia.getStationNum();i++){
            int stationNumber=(dia.getStationNum()-1)*direct+(1-2*direct)*i;
            AOdiaStation station=dia.getStation(stationNumber);
            switch (station.getTimeShow(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    result=result+textSize;
                    break;
                case 2:
                    //着のみ
                    result=result+textSize;
                    break;
                case 3:
                    //発着
                    result=result+(textSize*7/6);
                    result=result+textSize;
                    break;
            }
            //もし境界線が存在する駅なら境界線を考える
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(dia.getStation(checkStation).border()){
                result=result+(textSize*1/6);
            }
        }
        result=result-(int)(textSize*5/6);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("remark",false)){
            result=result+(int)(textSize*9.4f);
        }
        return result;
    }
    public int getStationFromY(int posY){
        int linepos=0;
        for(int i=0;i<dia.getStationNum();i++){
            int stationNumber=(dia.getStationNum()-1)*direct+(1-2*direct)*i;

            AOdiaStation station=dia.getStation(stationNumber);
            switch (station.getTimeShow(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    linepos=linepos+textSize;
                    break;
                case 2:
                    //着のみ
                    linepos=linepos+textSize;
                    break;
                case 3:
                    //発着
                    linepos=linepos+(textSize*7/6);
                    linepos=linepos+textSize;
                    break;
            }
            //もし境界線が存在する駅なら境界線を考える
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(dia.getStation(checkStation).border()){
                linepos=linepos+(textSize*1/6);
            }
            if(posY<linepos){
                if(stationNumber<0){
                    stationNumber=0;
                }
                return stationNumber;
            }
        }
        return -1;
    }
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*5);
    }
}
