package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.Train;
import com.kamelong.tool.SDlog;

public class TrainTimeView extends TimeTableDefaultView {
    private LineFile lineFile;
    private Train train;
    public int direct=0;

   public TrainTimeView(Context context, TimeTableOptions options, LineFile lineFile, Train train, int direct){
        super(context,options);
        this.lineFile =lineFile;
        this.direct=direct;
        this.train=train;
    }

    protected int getXsize(){
       return options.getTrainWidth()*textSize/2;
    }

    public void onDraw(Canvas canvas){
        textPaint.setColor(train.getTextColor().getAndroidColor());
        int startLine=0;
        if(options.showStartStation){
            String outerStart=train.getOuterStartStationName();
            if(outerStart!=null){
                if(outerStart.length()>options.getTrainWidth()/2){
                    outerStart=outerStart.substring(0,options.getTrainWidth()/2);
                }
                startLine+=textSize;
                drawText(canvas,outerStart, 1, startLine, textPaint,true);
                startLine+=textSize;
                drawText(canvas,getTimeText(train.getOuterStartTime()), 1, startLine, textPaint,true);

            }else{
                startLine+=textSize;
                drawText(canvas,"・・", 1, startLine, textPaint,true);
                startLine+=textSize;
                drawText(canvas,"・・", 1, startLine, textPaint,true);
            }
            startLine+=normalSpace;
            canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackBPaint);
        }
        for(int i = 0; i< lineFile.getStationNum(); i++){
            textPaint.setColor(train.getTextColor().getAndroidColor());

            int stationNumber=(lineFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station= lineFile.getStation(stationNumber);
            if(station.showAriTime(direct)){
                startLine+=textSize;
                drawText(canvas,getAriTimeText(stationNumber), 1, startLine, textPaint,true);
            }
            if(station.showTrack(direct)){
                if (station.showDepTime(direct)) {
                    startLine+=smallSpace;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                }
                startLine+=textSize;
                drawText(canvas,getStopTrackText(stationNumber), 1, startLine, textPaint,true);
                if (station.showAriTime(direct)) {
                    startLine += smallSpace;
                    canvas.drawLine(0, startLine, this.getWidth() - 1, startLine, blackPaint);
                }
            }
            if(station.showDepTime(direct)){
                if (station.showAriTime(direct) && !station.showTrack(direct)) {
                    startLine+=smallSpace;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                }
                startLine+=textSize;
                drawText(canvas,getDepTimeText(stationNumber), 1, startLine, textPaint,true);
            }
            //もし境界線が存在する駅なら境界線を引く
            //上り時刻表の時は次の駅が境界線ありなら境界線を引く
            int checkStation=stationNumber-direct;
            if(checkStation<0){
                checkStation=0;
            }
            if(lineFile.getStation(checkStation).getBorder()){
                startLine+=normalSpace;
                canvas.drawLine(0, startLine,this.getWidth()-1,startLine,blackBPaint);
            }
        }
        if(options.showEndStation){
            startLine+=normalSpace;
            canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackBPaint);
            String outerEnd=train.getOuterEndStationName();
            if(outerEnd!=null){
                if(outerEnd.length()>options.getTrainWidth()/2){
                    outerEnd=outerEnd.substring(0,options.getTrainWidth()/2);
                }
                startLine+=textSize;
                drawText(canvas,outerEnd, 1, startLine, textPaint,true);
                startLine+=textSize;
                drawText(canvas,getTimeText(train.getOuterEndTime()), 1, startLine, textPaint,true);

            }else{
                startLine+=textSize;
                drawText(canvas,"・・", 1, startLine, textPaint,true);
                startLine+=textSize;
                drawText(canvas,"・・", 1, startLine, textPaint,true);
            }
        }
        if(options.showRemark){
            try {
                startLine+=normalSpace;

                int startY = startLine;
                canvas.drawLine(0, startY, getWidth(), startY, blackBPaint);
                int heightSpace = 18;

                String value= train.remark;
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
                        canvas.drawText(String.valueOf(str[i]), startY, -startX-(textSize*0.2f), textPaint);
                        canvas.rotate(-90);
                        startY = startY + (int) textPaint.measureText(String.valueOf(str[i]));
                    } else {
                        space = space - 2;
                        startY = startY +textSize;
                        canvas.drawText(String.valueOf(str[i]), startX, startY, textPaint);
                    }

                }
            }catch(Exception e){
                SDlog.log(e);
            }
        }
        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),blackPaint);

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
            if (checkStation >= 0) {
                if (lineFile.getStation(checkStation).getBorder()) {
                    startLine += normalSpace;
                }
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


    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }
    private String getTimeText(int time){
       if(time<0) {
           return "○";
       }
       try {
           int ss = time % 60;
           time = (time - ss) / 60;
           int mm = time % 60;
           time = (time - mm) / 60;
           int hh = time % 60;
           hh = hh % 24;
           String timeS = "";
           if (options.showSecond) {
               timeS = String.format("%2d", hh) + String.format("%02d", mm) + "-" + String.format("%02d", ss);
           } else {
               timeS = String.format("%2d", hh) + String.format("%02d", mm);
           }
           return timeS;
       }catch (Exception e){
           SDlog.log(e);
           return "○";
       }
    }
    private String getDepTimeText(int station){
        try {
            //まず、停車種別で決まる文字列を振るい落とす
            switch(train.getStopType(station)){
                case 0:
                    if((train.getEndStation()-station)*(station-train.getStartStation())>0){
                        return "| |";
                    }
                    return "・・";
                case 3:
                    return "| |";
                case 2:
                    if(options.showPassTime&&train.timeExist(station)) {
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return "レ";
                    }
            }
            //発着表示であり、発時刻が存在せず、次の駅が運行なしや、経由なしの場合
            if(lineFile.getStation(station).showAriTime(direct)&&!train.timeExist(station,0)){
                int nextStation=0;
                if(direct==0){
                    nextStation=station+1;
                }else{
                    nextStation=station-1;
                }
                if(nextStation>=0&&nextStation<lineFile.getStationNum()){
                    switch (train.getStopType(nextStation)){
                        case 0:
                            if((train.getEndStation()-station)*(station-train.getStartStation())>0){
                                return "| |";
                            }
                            return "・・";
                        case 3:
                            return "| |";
                    }
                }
            }

            //発時刻が存在しない場合の処理
            if (!train.timeExist(station,0)) {
                if(train.timeExist(station,1)&&!lineFile.getStation(station).showAriTime(direct)){
                    return getAriTimeText(station);
                }
                return "○";
            }
            //ここでようやく時刻を表示する手順になる
            int time=train.getDepTime(station);
            int ss=time%60;
            time=(time-ss)/60;
            int mm=time%60;
            time=(time-mm)/60;
            int hh=time%60;
            hh=hh%24;
            String timeS = "";
            if(options.showSecond) {
                timeS = String.format("%2d", hh) + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                timeS = String.format("%2d", hh) + String.format("%02d", mm);
            }
            return timeS;
        }catch(Exception e){
            SDlog.log(e);
        }
        return "○";
    }
    private String getAriTimeText(int station){
        try {
            //まず、停車種別で決まる文字列を振るい落とす
            switch(train.getStopType(station)){
                case 0:
                    if((train.getEndStation()-station)*(station-train.getStartStation())>0){
                        return "| |";
                    }

                    return "・・";
                case 3:
                    return "| |";
                case 2:
                    if(options.showPassTime&&train.timeExist(station)) {
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return "レ";
                    }
            }
            //発着表示であり、着時刻が存在せず、前の駅が運行なしや、経由なしの場合
            if (lineFile.getStation(station).showDepTime(direct)) {
                int nextStation=0;
                if(direct==0){
                    nextStation=station-1;
                }else{
                    nextStation=station+1;
                }
                if(nextStation>=0&&nextStation<lineFile.getStationNum()){
                    switch (train.getStopType(nextStation)){
                        case 0:
                            if((train.getEndStation()-station)*(station-train.getStartStation())>0){
                                return "| |";
                            }

                            return "・・";
                        case 3:
                            return "| |";
                    }
                }
            }
            //着時刻が存在しない場合の処理
            if (!train.timeExist(station,1)) {
                if((train.timeExist(station,0)&&!lineFile.getStation(station).showDepTime(direct))){
                    return getDepTimeText(station);
                }
                return "○";
            }
            //ここでようやく時刻を表示する手順になる
            int time=train.getAriTime(station);
            int ss=time%60;
            time=(time-ss)/60;
            int mm=time%60;
            time=(time-mm)/60;
            int hh=time%60;
            hh=hh%24;
            String timeS = "";
            if(options.showSecond) {
                timeS = String.format("%2d", hh) + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                timeS = String.format("%2d", hh) + String.format("%02d", mm);
            }
            return timeS;
        }catch(Exception e){
            SDlog.log(e);
        }
        return "○";
    }
    private String getStopTrackText(int station){
       int stopTrack=train.getStopTrack(station);
       if(train.getStopType(station)==1||train.getStopType(station)==2) {
           return lineFile.getStation(station).getTrackShortName(stopTrack);
       }else{
           return "";
       }
    }

}
