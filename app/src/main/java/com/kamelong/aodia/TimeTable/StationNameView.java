package com.kamelong.aodia.TimeTable;


import android.content.Context;
import android.graphics.Canvas;

import com.kamelong.aodia.AOdiaData.LineFile;
import com.kamelong.aodia.AOdiaData.Station;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

public class StationNameView extends TimeTableDefaultView {
    private LineFile lineFile;
    private int direct;
    public StationNameView(Context context, TimeTableOptions options, LineFile lineFile, int direct){
        super(context,options);
        this.lineFile = lineFile;

        this.direct=direct;
    }
    public void onDraw(Canvas canvas){
        int startLine=0;
        if(options.showStartStation){
            startLine+=textSize*1.5;
            canvas.drawText(activity.getString(R.string.startStation), 1,startLine, blackBig);
            startLine+=textSize*0.5;
            startLine+=normalSpace;
            canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);

        }

        for(int i = 0; i< lineFile.getStationNum(); i++){
            int stationNumber=(lineFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station= lineFile.getStation(stationNumber);
            if(station.showAriTime(direct) && !station.showTrack(direct) && station.showDepTime(direct)){
                //大文字の駅名
                startLine+=textSize*1.5;
                canvas.drawText(station.name, 1,startLine, blackBig);
                startLine+=textSize*0.5;

                startLine+=smallSpace;

            }else{
                if(station.showAriTime(direct)){
                    startLine+=textSize;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                }
                if(station.showTrack(direct)){
                    if (station.showDepTime(direct)) {
                        startLine += smallSpace;
                        canvas.drawLine(0, startLine, this.getWidth() - 1, startLine, blackPaint);
                    }
                        startLine+=textSize;
                        canvas.drawText(activity.getString(R.string.stationTrack), 1,startLine, blackPaint);
                    if (station.showAriTime(direct)) {
                        startLine += smallSpace;
                        canvas.drawLine(0, startLine, this.getWidth() - 1, startLine, blackPaint);
                    }
                }
                if(station.showDepTime(direct)){
                    startLine+=textSize;
                    canvas.drawText(station.name, 1,startLine, blackPaint);
                }
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if(checkStation>=0){
                if(lineFile.getStation(checkStation).getBorder()){
                    startLine+=normalSpace;
                    canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);
                }
            }
        }
        if(options.showEndStation){
            startLine+=normalSpace;
            canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);

            startLine+=textSize*1.5;
            canvas.drawText(activity.getString(R.string.endStation), 1,startLine, blackBig);
            startLine+=textSize*0.5;
        }
        if(options.showRemark){
            try {
                startLine+=normalSpace;
                canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);
                int startY = startLine;
                int heightSpace = 18;

                String value= "　"+activity.getString(R.string.remark);
                value=value.replace('ー','｜');
                value=value.replace('（','(');
                value=value.replace('）',')');
                value=value.replace('「','┐');
                value=value.replace('」','└');
                char[] str =value.toCharArray();
                int lineNum = 1;
                int space = heightSpace;
                for (int i = 0; i < str.length; i++) {
                    if (space <= 0) {
                        space = heightSpace;
                        lineNum++;
                    }
                    if (!charIsEng(str[i])) {
                        space--;
                    }
                    space--;
                }
                space = heightSpace;
                int startX = (int) ((getWidth() - lineNum * textSize*1.2f) / 2 + (lineNum-1) * textSize*1.2f);
                startY = startLine;
                for (int i = 0; i < str.length; i++) {
                    if (space <= 0) {
                        space = heightSpace;
                        startX = startX - (int)(textSize*1.2f);
                        startY = (int) (this.getHeight() - 9.1f * textSize);
                    }
                    if (charIsEng(str[i])) {
                        space--;
                        canvas.rotate(90);
                        canvas.drawText(String.valueOf(str[i]), startY, -startX-(textSize*0.2f), blackPaint);
                        canvas.rotate(-90);
                        startY = startY + (int) blackPaint.measureText(String.valueOf(str[i]));
                    } else {
                        space = space - 2;
                        startY = startY +textSize;
                        canvas.drawText(String.valueOf(str[i]), startX, startY, blackPaint);
                    }

                }
            }catch(Exception e){
                SDlog.log(e);
            }
        }




    }
    protected int getXsize(){
        return (int)(textPaint.getTextSize()*5);
    }
    public int getYsize(){
        int startLine=0;
        if(options.showStartStation){
            startLine+=2*textSize;
            startLine+=normalSpace;

        }
        for(int i = 0; i< lineFile.getStationNum(); i++){

            int stationNumber=(lineFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station= lineFile.getStation(stationNumber);
            if(station.showAriTime(direct)){
                startLine+=textSize;
            }
            if(station.showTrack(direct)){
                if(station.showAriTime(direct)){
                    startLine+=smallSpace;
                }
                startLine+=textSize;
            }
            if(station.showDepTime(direct)){
                if(station.showAriTime(direct)){
                    startLine+=smallSpace;
                }
                startLine+=textSize;
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(lineFile.getStation(checkStation).getBorder()){
                startLine+=normalSpace;
            }
        }
        if(options.showEndStation){
            startLine+=2*textSize;
            startLine+=normalSpace;

        }
        if(options.showRemark){
            startLine+=normalSpace;
            startLine+=9.5*textSize;
        }
        startLine+=normalSpace;

        return startLine;
    }
    public int getStationFromY(int posY){
        int startLine=0;
        if (options.showStartStation) {
            startLine += 2 * textSize;
            startLine += normalSpace;

        }
        if (startLine > posY) {
            return -1;
        }

        for(int i = 0; i< lineFile.getStationNum(); i++){

            int stationNumber=(lineFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station= lineFile.getStation(stationNumber);
            if (station.showAriTime(direct)) {
                startLine += textSize;
            }
            if (station.showTrack(direct)) {
                if (station.showAriTime(direct)) {
                    startLine += smallSpace;
                }
                startLine += textSize;
            }
            if (station.showDepTime(direct)) {
                if (station.showAriTime(direct)) {
                    startLine += smallSpace;
                }
                startLine += textSize;
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if (checkStation < 0) {
                checkStation = 0;
            }
            if (lineFile.getStation(checkStation).getBorder()) {
                startLine += normalSpace;
            }
            if (startLine > posY) {
                return i;
            }
        }
        if (options.showEndStation) {
            startLine += 2 * textSize;
            startLine += normalSpace;

        }
        if (options.showRemark) {
            startLine += normalSpace;
            startLine += 9.5 * textSize;
        }
        startLine += normalSpace;

        return -1;
    }



}
