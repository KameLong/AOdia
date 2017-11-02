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
    private AOdiaStation station;
    private int direct;
    StationNameView(Context context, AOdiaDiaFile diaFile,int d){
        super(context);
        dia=diaFile;
        this.station =diaFile.getStation();
        direct=d;
    }
    public void onDraw(Canvas canvas){
        int startLine=(int)blackPaint.getTextSize();
        for(int i = 0; i< station.getStationNum(); i++){
            int stationNumber=(station.getStationNum()-1)*direct+(1-2*direct)*i;

            switch(station.border(stationNumber-direct)){
                case 0:
                    switch (station.getTimeShow(stationNumber,direct)){
                        case 0:
                            //発のみ
                            canvas.drawText(station.getName(stationNumber), 1,startLine, blackPaint);
                            startLine=startLine+(int)blackPaint.getTextSize();
                            break;
                        case 1:
                            //発着
                            canvas.drawText(station.getName(stationNumber), 1,startLine+(int)(textPaint.getTextSize()*4/6), blackBig);
                            startLine=startLine+(int)(textPaint.getTextSize()*13/6);
                            break;
                        case 2:
                            //着のみ
                            canvas.drawText(station.getName(stationNumber), 1,startLine, blackPaint);
                            startLine=startLine+(int)blackPaint.getTextSize();
                            break;
                    }
                    break;
                case 1:
                    //着のみ
                    canvas.drawText(station.getName(stationNumber), 1,startLine, blackPaint);
                    startLine=startLine+(int)blackPaint.getTextSize();
                    canvas.drawLine(0, startLine-(int)(textPaint.getTextSize()*2/3),this.getWidth()-1,startLine-(int)(textPaint.getTextSize()*2/3),blackBPaint);
                    startLine=startLine+(int)(textPaint.getTextSize()*1/3);
                    break;
                case 2:
                    //発着
                    canvas.drawText(station.getName(stationNumber), 1,startLine+(int)(textPaint.getTextSize()*4/6), blackBig);
                    startLine=startLine+(int)(textPaint.getTextSize()*13/6);
                    i++;
                    break;
            }
        }
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("remark",false)){
            int startY = (int) (this.getHeight() - 10.5f * textSize);
            canvas.drawLine(0, startY, getWidth(), startY, blackBBPaint);
            canvas.drawText("運用番号",0,startY+1.0f*textSize,blackPaint);
            startY+=1.2f*textSize;
            canvas.drawLine(0, startY, getWidth(), startY, blackPaint);
            int startX=(getWidth()-textSize)/2;
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("備",startX,startY,blackPaint);
            startY=startY+(int)(textSize*1.5f);
            canvas.drawText("考",startX,startY,blackPaint);
        }
    }
    public int getYsize(){
        int result=textSize;
        for(int i = 0; i< station.getStationNum(); i++){
            int stationNumber=(station.getStationNum()-1)*direct+(1-2*direct)*i;
            switch(station.border(stationNumber-direct)){
                case 0:
                    switch (station.getTimeShow(stationNumber,direct)){
                        case 0:
                            //発のみ
                            result=result+textSize;
                            break;
                        case 1:
                            //発着
                            result=result+(textSize*7/6);
                            result=result+textSize;
                            break;
                        case 2:
                            //着のみ
                            result=result+textSize;
                            break;
                    }
                    break;
                case 1:
                    //着のみ
                    result=result+textSize;
                    result=result+(textSize/3);
                    break;
                case 2:
                    //発着
                    result=result+(textSize*7/6);
                    result=result+textSize;
                    i++;
                    break;
            }


        }
        result=result-(textSize*4/6);

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(spf.getBoolean("remark",false)){
            result=result+(int)(textSize*10.6f);
        }
        return result;
    }
    public int getStationFromY(int posY){
        int linePos=0;

        for(int i = 0; i< station.getStationNum(); i++) {
            int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
            switch (station.border(stationNumber - direct)) {
                case 0:
                    switch (station.getTimeShow(stationNumber, direct)) {
                        case 0:
                            //発のみ
                            linePos = linePos + textSize;
                            break;
                        case 1:
                            //発着
                            linePos = linePos + (textSize * 7 / 6);
                            linePos = linePos + textSize;
                            break;
                        case 2:
                            //着のみ
                            linePos = linePos + textSize;
                            break;
                    }
                    break;
                case 1:
                    //着のみ
                    linePos = linePos + textSize;
                    linePos = linePos + (textSize / 3);
                    break;
                case 2:
                    //発着
                    linePos = linePos + (textSize * 7 / 6);
                    linePos = linePos + textSize;
                    i++;
                    break;
            }
            if(posY<linePos){
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
