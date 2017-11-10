package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.kamelong.JPTI.JPTI;
import com.kamelong.JPTI.Time;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadata.AOdiaStation;
import com.kamelong.aodia.diadata.AOdiaTrain;

public class TrainTimeView extends KLView {
    private AOdiaDiaFile dia;
    private AOdiaTrain train;
    private AOdiaStation station;
    private JPTI jpti;
    private int direct;
    private boolean secondFrag=false;
    private boolean remarkFrag=false;
    private boolean showPassFrag=false;
    private TrainSelectListener trainSelectListener=null;
    private static final String NOSERVICE_STRING ="∙ ∙";
//    private static final String NOSERVICE_STRING =": :";
    private static final String NOVIA_STRING="| |"
            ;
    private static final String PASS_STRING="レ";
    private static final String NODATA_STRING="○";

    private TrainTimeView(Context context){
        super(context);
    }
    TrainTimeView(Context context, TimeTableFragment timeTableFragment,AOdiaDiaFile diaFile, AOdiaTrain t, int d){
        this(context);
        dia=diaFile;
        station=dia.getStation();
        jpti=dia.getJPTI();
        train=t;
        direct=d;

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        remarkFrag=spf.getBoolean("remark",remarkFrag);
        showPassFrag=spf.getBoolean("showPass",showPassFrag);




    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        long time=System.currentTimeMillis();
        drawTime(canvas);
        if(remarkFrag){
            drawRemark(canvas);
        }
        System.out.println(System.currentTimeMillis()-time);
    }
    private void drawTime(Canvas canvas){
        /**
        int startLine2 = textSize;
        for (int i = 0; i < station.getStationNum(); i++) {
            drawText(canvas,"aaaa", 1, startLine2, textPaint, true);
            startLine2+=textSize;

        }
        if(true){
            return;
        }
**/
        try {
            SdLog.log("drawTime");
            int startLine = textSize;
            textPaint.setColor(train.getTrainType().getTextColor().getAndroidColor());
            if(dia.getService().getTimeTableFont(train.getTrainType().getFontNumber()).itaric){
                textPaint.setTextSkewX(-0.3f);
            }else{
                textPaint.setTextSkewX(0f);
            }
            if(dia.getService().getTimeTableFont(train.getTrainType().getFontNumber()).bold){
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                textPaint.setTypeface(Typeface.DEFAULT);
            }
            for (int i = 0; i < station.getStationNum(); i++) {
                int stationNumber = (station.getStationNum() - 1) * direct + (1 - 2 * direct) * i;
                int border=station.border(stationNumber-direct);
                int timeShow=station.getTimeShow(stationNumber,direct);
                if(border==0&&timeShow==0) {
                    //発のみ
                    if (station.bigStation(stationNumber) && (train.getStopType(stationNumber) == 0&&(station.border(stationNumber-1+direct)==0)&&stationNumber!=0)) {
                        drawText(canvas, "- - - - - - -", 1, startLine, textPaint, true);
                    } else {
                        drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                    }
                    startLine = startLine + textSize;
                }
                if(timeShow==1) {
                    //発着
                    int backwordStation = stationNumber + (direct * 2 - 1);
                    if (backwordStation < 0 || backwordStation >= station.getStationNum()) {
                        if(train.getTime(stationNumber).getArrivalTime()>=0){
                            drawText(canvas, getArriveTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, NOSERVICE_STRING, 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(backwordStation)) {
                            case 0:
                                if(train.getTime(backwordStation)!=null&&train.getTime(stationNumber).getArrivalTime()>=0){
                                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, NOSERVICE_STRING, 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.getTime(backwordStation)!=null&&train.getTime(stationNumber).getArrivalTime()>=0){
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
                    startLine = startLine +(textSize * 7 / 6);

                    textPaint.setColor(train.getTrainType().getTextColor().getAndroidColor());

                    int forwordStation = stationNumber + (1 - direct * 2);
                    if (forwordStation < 0 || forwordStation >= station.getStationNum()) {
                        if(train.getTime(stationNumber).getDepartureTime()>=0){
                            drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                        }else {
                            drawText(canvas, NOSERVICE_STRING, 1, startLine, textPaint, true);
                        }
                    }else {
                        switch (train.getStopType(forwordStation)) {
                            case 0:
                                if(train.getTime(forwordStation)!=null&&train.getTime(stationNumber).getDepartureTime()>=0){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas, NOSERVICE_STRING, 1, startLine, textPaint, true);
                                }
                                break;
                            case 3:
                                if(train.getTime(forwordStation)!=null&&train.getTime(stationNumber).getDepartureTime()>=0){
                                    drawText(canvas,getDepartureTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                                }else {
                                    drawText(canvas,NOVIA_STRING, 1, startLine, textPaint, true);
                                }
                                break;
                            default:
                                drawText(canvas, getDepartureTime(train, stationNumber, direct), 1, startLine, textPaint, true);
                                break;
                        }
                    }
                    startLine = startLine +textSize;
                }
                if(border!=0||timeShow==2){

                    //着のみ
                    drawText(canvas,getArriveTime(train,stationNumber, direct), 1, startLine, textPaint,true);
                    startLine = startLine + textSize;
                    if(border==2){
                        canvas.drawLine(0, startLine  -(textSize*4 / 5), this.getWidth() - 1, startLine - (textSize*4/ 5), blackPaint);
                        startLine = startLine + (textSize  / 6);
                    }
                    if(border==1){
                        canvas.drawLine(0, startLine  -(textSize*2 / 3), this.getWidth() - 1, startLine - (textSize*2/ 3), blackBPaint);
                        startLine = startLine + (textSize  / 3);
                    }

                }
        }
        canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight(), blackPaint);
    }catch(Exception e){
        SdLog.log(e);
    }
}
    private void drawRemark(Canvas  canvas){
        try {
            int startY = (int) (this.getHeight() - 10.5f * textSize);

            canvas.drawLine(0, startY, getWidth(), startY, blackBBPaint);

            if(train.getOperation()!=null){
                if(train.getOperation().getNumber()>=0) {
                    drawText(canvas, train.getOperation().getNumber() + "", 1, (int) (this.getHeight() - 9.4f * textSize), textPaint, true);
                }

            }

            canvas.drawLine(0, startY+1.2f*textSize, getWidth(), startY+1.2f*textSize, blackPaint);

            int heightSpace = 18;

            String value= train.getText();
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
                    //
                    result=result+textSize;
                    result=result+(textSize*7/6);
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
    public int getXsize(){
        int lineTextSize=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("lineTimetableWidth","4"))+1;
        if(secondFrag){
            lineTextSize+=3;
        }
        return (int)(textSize*lineTextSize*0.5f);
    }
    private String getDepartureTime(AOdiaTrain train, int station, int direct){
        Time time=train.getTime(station);
        try {
            switch(train.getStopType(station)){
                case AOdiaTrain.NOSERVICE:
                    return NOSERVICE_STRING;
                case AOdiaTrain.NOVIA:
                    return NOVIA_STRING;
                case AOdiaTrain.PASS:
                    if(showPassFrag&&(time.getArrivalTime()>=0||time.getDepartureTime()>=0)) {
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return PASS_STRING;
                    }
            }
            if (time.getDepartureTime()<0) {
                if (time.getArrivalTime()<0) {
                    return "○";
                }
                return getArriveTime(train,station, direct);
            }
            int second=time.getDepartureTime();
            int ss=second%60;
            second=(second-ss)/60;
            int mm=second%60;
            second=(second-mm)/60;
            int hh=second%60;
            hh=hh%24;
            String result = "";
            if(secondFrag) {
                result =hh + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                result =hh+ String.format("%02d", mm);
            }
            return result;
        }catch(Exception e){
            SdLog.log(e);
        }
        return "○";
    }
    private String getArriveTime(AOdiaTrain train, int station, int direct){
        Time time=train.getTime(station);
        try {
            switch(train.getStopType(station)){
                case AOdiaTrain.NOSERVICE:
                    return NOSERVICE_STRING;
                case AOdiaTrain.NOVIA:
                    return NOVIA_STRING;
                case AOdiaTrain.PASS:
                    if(showPassFrag&&(time.getArrivalTime()>=0||time.getDepartureTime()>=0)){
                        textPaint.setColor(Color.GRAY);
                    }else{
                        return PASS_STRING;
                    }
            }
            if (time.getArrivalTime()<0) {
                if (time.getDepartureTime()>=0) {
                    return getDepartureTime(train,station, direct);
                }
                return NODATA_STRING;
            }
            int second=time.getArrivalTime();
            int ss=second%60;
            second=(second-ss)/60;
            int mm=second%60;
            second=(second-mm)/60;
            int hh=second%60;
            hh=hh%24;
            String result = "";
            if(secondFrag) {
                result = hh + String.format("%02d", mm)+"-"+String.format("%02d", ss);
            }else{
                result = hh + String.format("%02d", mm);
            }
            return result;
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
    public void setOnTrainSelectListener(TrainSelectListener l){
        this.trainSelectListener=l;
    }

}