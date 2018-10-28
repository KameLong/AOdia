package com.kamelong.aodia.TimeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaDefaultView;
import com.kamelong.aodia.SdLog;

public class TrainTimeView extends AOdiaDefaultView {
    public DiaFile diaFile=null;
    public Train train=null;
    public int direct=0;
    /**
     * 秒単位時刻
     */
    private boolean secondFrag=false;
    private boolean showPassFrag=false;
    private boolean remarkFrag=false;
    private int textWidth=5;
    public TrainTimeView(Context context, DiaFile diaFile, Train train,int direct){
        super(context);
        this.diaFile=diaFile;
        this.direct=direct;
        this.train=train;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",false);
        showPassFrag=spf.getBoolean("showPass",false);
        remarkFrag=spf.getBoolean("showRemark",true);
        textWidth=Integer.parseInt(spf.getString("lineTimetableWidth",textWidth+""));
    }

    protected int getXsize(){
        if(secondFrag) {
            return (int) (textPaint.getTextSize() *(textWidth+3)/2);
        }else{
            return (int) (textPaint.getTextSize() *textWidth/2);

        }
    }
    public void onDraw(Canvas canvas){
        drawTime(canvas);
        if(remarkFrag)drawRemark(canvas);
        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),blackPaint);

    }
    private void drawTime(Canvas canvas){
        int startLine=0;
        for(int i=0;i<diaFile.getStationNum();i++){
            textPaint.setColor(diaFile.trainType.get(train.type).textColor.getAndroidColor());
            int stationNumber=(diaFile.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station=diaFile.station.get(stationNumber);
            switch (station.getTimeTableStyle(direct)){
                case 0:
                    break;
                case 1:
                    //発のみ
                    startLine=startLine+textSize;
                    if (station.bigStation && (train.getStopType(stationNumber) == 0)) {
                        drawText(canvas,"- - - - - - -", 1, startLine, textPaint,true);
                    } else {
                        drawText( canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                    }
                    break;
                case 2:
                    //着のみ
                    startLine=startLine+textSize;
                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                    break;
                case 3:
                    //発着
                    startLine=startLine+textSize;
                    int backwordStation = stationNumber + (direct * 2 - 1);
                    if (backwordStation < 0 || backwordStation >= diaFile.getStationNum()) {
                        if(train.arriveExist(stationNumber)){
                            drawText(canvas, getArriveTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, ": :", 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(backwordStation)) {
                            case 0:
                                if(train.arriveExist(stationNumber)){
                                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, ": :", 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.arriveExist(stationNumber)){
                                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, "| |", 1, startLine, textPaint, true);
                                }
                                break;
                            default:
                                drawText(canvas, getArriveTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                                break;
                        }

                    }
                    startLine=startLine+textSize/6;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine=startLine+textSize;

                    int forwordStation = stationNumber + (1 - direct * 2);
                    if (forwordStation < 0 || forwordStation >= diaFile.getStationNum()) {
                        if(train.departExist(stationNumber)){
                            drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, ": :", 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(forwordStation)) {
                            case 0:
                                if(train.departExist(stationNumber)){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, ": :", 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.departExist(stationNumber)){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, "| |", 1, startLine, textPaint, true);
                                }
                                break;
                            default:
                                drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                                break;
                        }
                    }
                    break;
                case 5:
                    //発番線
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    if(train.getStopType(stationNumber)==1||train.getStopType(stationNumber)==2){
                    if(train.getStop(stationNumber)==0){
                        drawText(canvas, station.trackshortName.get(station.stopMain[direct]), 1, startLine, textPaint, true);
                    }else{
                        drawText(canvas, station.trackshortName.get(train.getStop(stationNumber)), 1, startLine, textPaint, true);
                    }}
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                    break;
                case 6:
                    //着番線
                    startLine+=textSize ;
                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    if(train.getStopType(stationNumber)==1||train.getStopType(stationNumber)==2){
                    if(train.getStop(stationNumber)==0){
                        drawText(canvas, station.trackshortName.get(station.stopMain[direct]), 1, startLine, textPaint, true);
                    }else{
                        drawText(canvas, station.trackshortName.get(train.getStop(stationNumber)), 1, startLine, textPaint, true);
                    }}
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    break;
                case 7:
                    //発着番線
                    startLine+=textSize ;
                    backwordStation = stationNumber + (direct * 2 - 1);
                    if (backwordStation < 0 || backwordStation >= diaFile.getStationNum()) {
                        if(train.arriveExist(stationNumber)){
                            drawText(canvas, getArriveTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, ": :", 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(backwordStation)) {
                            case 0:
                                if(train.arriveExist(stationNumber)){
                                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, ": :", 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.arriveExist(stationNumber)){
                                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, "| |", 1, startLine, textPaint, true);
                                }
                                break;
                            default:
                                drawText(canvas, getArriveTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                                break;
                        }

                    }
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    if(train.getStopType(stationNumber)==1||train.getStopType(stationNumber)==2){

                        if(train.getStop(stationNumber)==0){
                        drawText(canvas, station.trackshortName.get(station.stopMain[direct]), 1, startLine, textPaint, true);
                    }else{
                        drawText(canvas, station.trackshortName.get(train.getStop(stationNumber)), 1, startLine, textPaint, true);
                    }}
                    startLine+=textSize / 5;
                    canvas.drawLine(0, startLine , this.getWidth() - 1, startLine, blackPaint);
                    startLine+=textSize ;
                    forwordStation = stationNumber + (1 - direct * 2);
                    if (forwordStation < 0 || forwordStation >= diaFile.getStationNum()) {
                        if(train.departExist(stationNumber)){
                            drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, ": :", 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(forwordStation)) {
                            case 0:
                                if(train.departExist(stationNumber)){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, ": :", 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.departExist(stationNumber)){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, "| |", 1, startLine, textPaint, true);
                                }
                                break;
                            default:
                                drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                                break;
                        }
                    }
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
        if(remarkFrag){
            startLine+=(int)(textSize*9.4f);
        }
        startLine+=textSize/3;
        return startLine;
    }
    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }
    public String getDepartureTime(Train train,int station,int direct){
        try {
            switch(train.getStopType(station)){
                case 0:
                    return ": :";
                case 3:
                    return "| |";
                case 2:
                    if(showPassFrag&&train.timeExist(station)) {
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return "レ";
                    }
            }
            if (!train.departExist(station)) {
                if (!train.arriveExist(station)) {
                    return "○";
                }
                return getArriveTime(train,station, direct);
            }
            int second=train.getDepartureTime(station);
            int ss=second%60;
            second=(second-ss)/60;
            int mm=second%60;
            second=(second-mm)/60;
            int hh=second%60;
            hh=hh%24;
            String time = "";
            if(secondFrag) {
                time = String.format("%2d", hh) + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                time = String.format("%2d", hh) + String.format("%02d", mm);
            }
            return time;
        }catch(Exception e){
            SdLog.log(e);
        }
        return "○";
    }
    private String getArriveTime(Train train,int station,int direct){
        try {
            switch(train.getStopType(station)){
                case 0:
                    return ": :";
                case 3:
                    return "| |";
                case 2:
                    if(showPassFrag&&train.timeExist(station)) {
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return "レ";
                    }
            }
            if (!train.arriveExist(station)) {
                if (!train.departExist(station)) {
                    return "○";
                }
                return getDepartureTime(train,station, direct);
            }
            int second=train.getArrivalTime(station);
            int ss=second%60;
            second=(second-ss)/60;
            int mm=second%60;
            second=(second-mm)/60;
            int hh=second%60;
            hh=hh%24;
            String time = "";
            if(secondFrag) {
                time = String.format("%2d", hh) + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                time = String.format("%2d", hh) + String.format("%02d", mm);
            }
            return time;
        }catch(Exception e){
            SdLog.log(e);
        }
        return "○";
    }

    private void drawRemark(Canvas  canvas){
        try {
            int startY = (int) (this.getHeight() - 9.3f * textSize);
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
            startY = (int) (this.getHeight() - 9.1f * textSize);
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
            SdLog.log(e);
        }
    }
    private boolean charIsEng(char c){
        return c<256;
    }

}
