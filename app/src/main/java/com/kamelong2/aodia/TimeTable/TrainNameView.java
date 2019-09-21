package com.kamelong2.aodia.TimeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaDefaultView;
import com.kamelong2.aodia.SDlog;

public class TrainNameView extends AOdiaDefaultView {
    private DiaFile diaFile;
    private Train train;
    private int direct;
    private boolean secondFrag=false;
    private boolean showTrainName=true;
    private int textWidth=5;


    TrainNameView(Context context){
        super(context);
    }
    TrainNameView(Context context,DiaFile diaFile,Train t){
        this(context);
        this.diaFile=diaFile;
        train=t;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        showTrainName=spf.getBoolean("trainName",showTrainName);
        textWidth=Integer.parseInt(spf.getString("lineTimetableWidth",textWidth+""));
    }
    protected int getYsize(){
        if(showTrainName){
            return (int)(textPaint.getTextSize()*11.2f);
        }else{
            return (int)(textPaint.getTextSize()*3.2f);
        }
    }
    public void onDraw(Canvas canvas){
        int startLine=textSize;
        textPaint.setColor(diaFile.trainType.get(train.type).textColor.getAndroidColor());
        drawText(canvas,train.operationName, 5,startLine, textPaint,true);
        startLine=startLine+textSize;
        drawText(canvas,train.number, 5,startLine, textPaint,true);
        startLine=startLine+textSize;
        drawText(canvas,diaFile.trainType.get(train.type).shortName, 5,startLine, textPaint,true);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(getContext());
        canvas.drawLine(getWidth()-1,0,getWidth()-1,getHeight(),blackPaint);
        if(showTrainName) {
            canvas.drawLine(0,textPaint.getTextSize()*3.3f,getWidth(),textPaint.getTextSize()*3.3f,blackPaint);
            drawTrainName(canvas);
        }
    }

    private void drawTrainName(Canvas canvas){
        try {
            int heightSpace = 12;
            String value= train.name;
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
            int startY = (int) (this.getHeight() - 8f * textSize);
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int)(textSize*1.2f);
                    startY = (int) (this.getHeight() - 8f * textSize);
                }
                if (charIsEng(str[i])) {
                    space--;
                    canvas.save();
                    canvas.rotate(90,0,0);
                    drawText(canvas,String.valueOf(str[i]), startY+2,(int)( -startX-(textSize*0.2f)), textPaint,false);
                    canvas.restore();
                    startY = startY + (int) textPaint.measureText(String.valueOf(str[i]));
                } else {
                    space = space - 2;
                    startY = startY +textSize;
                    drawText(canvas,String.valueOf(str[i]), startX, startY, textPaint,false);
                }

            }
        }catch(Exception e){
            SDlog.log(e);
        }


        float textSize=textPaint.getTextSize();
        if(train.count.length()>0){
            String gousuu=train.count;
            drawText(canvas,gousuu,0,(int)(getHeight()-textSize*1.2f),textPaint,true);
            drawText(canvas,"号",0,(int)(getHeight()-textSize*0.2f),textPaint,true);
        }
    }
    private boolean charIsEng(char c){
        return c<256;
    }
    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }
    protected int getXsize(){
        if(secondFrag) {
            return (int) (textPaint.getTextSize() *(textWidth+3)/2);
        }else{
            return (int) (textPaint.getTextSize() *textWidth/2);

        }
    }
}
