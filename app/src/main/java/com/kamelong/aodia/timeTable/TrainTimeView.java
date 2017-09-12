package com.kamelong.aodia.timeTable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.oudia.DiaFile;
import com.kamelong.aodia.oudia.Station;
import com.kamelong.aodia.oudia.Train;
import com.kamelong.aodia.timeTable.KLView;

public class TrainTimeView extends KLView {
    private DiaFile dia;
    private Train train;
    private int direct;
    private boolean secondFrag=false;
    private boolean remarkFrag=false;
    private boolean showPassFrag=false;

    TrainTimeView(Context context){
        super(context);
    }
    TrainTimeView(Context context,DiaFile diaFile,Train t,int d){
        this(context);
        dia=diaFile;
        train=t;
        direct=d;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        remarkFrag=spf.getBoolean("remark",remarkFrag);
        showPassFrag=spf.getBoolean("showPass",showPassFrag);
    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawTime(canvas);
        if(remarkFrag){
            drawRemark(canvas);
        }
    }
    private void drawTime(Canvas canvas){
        try {
            int startLine = textSize;
            for (int i = 0; i < dia.getStationNum(); i++) {
                textPaint.setColor(dia.getTrainType(train.getType()).getTextColor());
                int stationNumber = (dia.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
                Station station = dia.getStation(stationNumber);
                switch (station.getTimeShow(direct)) {
                    case 0:
                        break;
                    case 1:
                        //発のみ
                        if (station.getBigStation() && (train.getStopType(stationNumber) == 0)) {
                            drawText(canvas,"- - - - - - -", 1, startLine, textPaint,true);
                        } else {
                            drawText( canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                        }
                        startLine = startLine +textSize;
                        break;
                    case 2:
                        //着のみ
                        drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                        startLine = startLine + textSize;
                        break;
                    case 3:
                        //発着
                        int backwordStation = stationNumber + (direct * 2 - 1);
                        if (backwordStation < 0 || backwordStation >= dia.getStationNum()) {
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
                        canvas.drawLine(0, startLine + (int) (textSize / 5.0f), this.getWidth() - 1, startLine + (int) (textSize/ 5.0f), blackPaint);
                        startLine = startLine + (int) (textSize * 7 / 6);

                        textPaint.setColor(dia.getTrainType(train.getType()).getTextColor());

                        int forwordStation = stationNumber + (1 - direct * 2);
                        if (forwordStation < 0 || forwordStation >= dia.getStationNum()) {
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
                        startLine = startLine +textSize;
                        break;
                }
                //もし境界線が存在する駅なら境界線を引く
                //上り時刻表の時は次の駅が境界線ありなら境界線を引く
                int checkStation = stationNumber - direct;
                if (checkStation < 0) {
                    checkStation = 0;
                }
                if (dia.getStation(checkStation).border()) {
                    canvas.drawLine(0, startLine - (int) (textSize * 4 / 5), this.getWidth() - 1, startLine - (int) (textSize* 4 / 5), blackPaint);
                    startLine = startLine + (int) (textSize* 1 / 6);
                }
            }
            canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight(), blackPaint);
        }catch(Exception e){
            SdLog.log(e);
        }
    }
    private void drawRemark(Canvas  canvas){
        try {
            int startY = (int) (this.getHeight() - 9.3f * textSize);
            canvas.drawLine(0, startY, getWidth(), startY, blackBPaint);
            int heightSpace = 18;

            String value= train.getRemark();
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

        /*
        int namespace=18;



        char[] name=train.getRemark().replace('ー','｜').toCharArray();
        String[] text=new String[name.length];
        int textIndex=0;
        for(int i=0;i<name.length;i++){
            if(charIsEng(name[i])){
                if(charIsNumber(name[i])){
                    text[textIndex]=String.valueOf(name[i]);
                    while(i+1<name.length&&charIsNumber(name[i+1])){
                        i++;
                        text[textIndex]+=String.valueOf(name[i]);
                    }
                }else{
                    text[textIndex]=String.valueOf(name[i]);
                    while(i+1<name.length&&!charIsNumber(name[i+1])&&charIsEng(name[i+1])){
                        i++;
                        text[textIndex]+=String.valueOf(name[i]);
                    }
                }
            }else{
                text[textIndex]=String.valueOf(name[i]);
            }
            textIndex++;
        }
        int lineNum=1;
        int space=namespace;
        for(int i=0;i<textIndex&&i<text.length;i++){
            if(space<=0){
                space=namespace;
                lineNum++;
            }
            if(text[i].length()>2){
                if(space-text[i].length()<0){
                    if(space==namespace){
                        space=0;
                    }else{
                        lineNum++;
                        space=0;
                    }
                }else {
                    space = space - text[i].length();
                }
            }else{
                space=space-2;
            }
        }

        int startX=(int)((getWidth()-lineNum*textSize)/2+(lineNum-1)*textSize);
        startY=startY+(int)(textSize*0.1f);
        space=namespace;
        System.out.println(startX);
        for(int i=0;i<textIndex&&i<text.length;i++){
            if(text[i].length()>2){
                if(space-text[i].length()<0){
                    if(space==namespace){
                        space=0;
                    }else{
                        startX-=textSize;
                        space=0;
                    }
                    canvas.rotate(90,0,0);
                    canvas.drawText(text[i],startY+(namespace-space)*textSize/2,startX,textPaint);
                    canvas.rotate(-90,0,0);
                }else {
                    canvas.rotate(90,0,0);
                    canvas.drawText(text[i],startY+(namespace-space)*textSize/2,-startX,textPaint);
                    canvas.rotate(-90,0,0);
                    space = space - text[i].length();
                }
            }else{
                space=space-2;
                canvas.drawText(text[i],startX,startY+(namespace-space)*textSize/2,textPaint);
            }
            if(space<=0){
                space=namespace;
                startX-=textSize;
            }
        }
        */

    }
    private boolean charIsEng(char c){
        return c<256;
    }
    public int getYsize(){
        int result=textSize;
        for(int i=0;i<dia.getStationNum();i++){
            int stationNumber=(dia.getStationNum()-1)*direct+(1-2*direct)*i;
            Station station=dia.getStation(stationNumber);
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
        if(remarkFrag){
            result=result+(int)(textSize*9.4f);
        }
        return result;
    }
    public int getXsize(){
        int lineTextSize=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("lineTimetableWidth","4"))+1;
        if(secondFrag){
            lineTextSize+=3;
        }
        return (int)(textSize*lineTextSize*0.5f);
    }
    public String getDepartureTime(Train train,int station,int direct){
        try {
            switch(train.getStopType(station)){
                case Train.STOP_TYPE_NOSERVICE:
                    return ": :";
                case Train.STOP_TYPE_NOVIA:
                    return "| |";
                case Train.STOP_TYPE_PASS:
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
                case Train.STOP_TYPE_NOSERVICE:
                    return ": :";
                case Train.STOP_TYPE_NOVIA:
                    return "| |";
                case Train.STOP_TYPE_PASS:
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
            int second=train.getArriveTime(station);
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
    private void drawText(Canvas canvas,String text,int x,int y,Paint paint,boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }

}