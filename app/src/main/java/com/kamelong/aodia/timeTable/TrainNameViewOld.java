package com.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;

import com.kamelong.JPTI.JPTI;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaTrain;

/**
 * Created by kamelong on 2016/11/21.
 */
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
class TrainNameViewOld extends KLView {
    private AOdiaDiaFile dia;
    private JPTI jpti;
    private AOdiaTrain train;
    private int direct;
    private boolean secondFrag=false;
    private boolean showTrainName=false;
    private TrainNameViewOld(Context context){
        super(context);
    }
    TrainNameViewOld(Context context, AOdiaDiaFile diaFile, AOdiaTrain t){
        this(context);
        dia=diaFile;
        train=t;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        showTrainName=spf.getBoolean("trainName",showTrainName);
    }
    public void onDraw(Canvas canvas){

        int startLine=(int) Companion.getTextPaint().getTextSize();
        Companion.getTextPaint().setColor(train.getTrainType().getTextColor().getAndroidColor());

        if(secondFrag) {
            drawText(canvas,train.getTrainType().getName(), 1, startLine, Companion.getTextPaint(),true);
        }else{
            drawText(canvas,train.getTrainType().getShortName(), 1, startLine, Companion.getTextPaint(),true);
        }
        startLine=startLine+(int) Companion.getTextPaint().getTextSize();
        drawText(canvas,train.getNumber(), 1, startLine, Companion.getTextPaint(),true);

        canvas.drawLine(this.getWidth()-1, 0,this.getWidth()-1,this.getHeight(), Companion.getBlackPaint());
    }
    private void drawTrainName(Canvas canvas){
        try {
            int heightSpace = 12;
            String value= train.getName();
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
            int startX = (int) ((getWidth() - lineNum * Companion.getTextSize() *1.2f) / 2 + (lineNum-1) * Companion.getTextSize() *1.2f);
            int startY = (int) (this.getHeight() - 8.1f * Companion.getTextSize());
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int)(Companion.getTextSize() *1.2f);
                    startY = (int) (this.getHeight() - 8.1f * Companion.getTextSize());
                }
                if (charIsEng(str[i])) {
                    space--;
                    canvas.save();
                    canvas.rotate(90,0,0);
                    drawText(canvas,String.valueOf(str[i]), startY+2,(int)( -startX-(Companion.getTextSize() *0.2f)), Companion.getTextPaint(),false);
                    canvas.restore();
                    startY = startY + (int) Companion.getTextPaint().measureText(String.valueOf(str[i]));
                } else {
                    space = space - 2;
                    startY = startY + Companion.getTextSize();
                    drawText(canvas,String.valueOf(str[i]), startX, startY, Companion.getTextPaint(),false);
                }

            }
        }catch(Exception e){
            SdLog.log(e);
        }


        float textSize= Companion.getTextPaint().getTextSize();
        if(train.getCount().length()>0){
            String gousuu=train.getCount().substring(0,train.getCount().length()-1);
            drawText(canvas,gousuu,0,(int)(getHeight()-textSize*1.2f), Companion.getTextPaint(),true);
            drawText(canvas,"号",0,(int)(getHeight()-textSize*0.2f), Companion.getTextPaint(),true);
        }
    }
    private boolean charIsEng(char c){
        return c<256;
    }
    public int getYsize(){
            return (int)(Companion.getTextPaint().getTextSize()*2.2f);
    }
    public int getXsize(){
        int lineTextSize=Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("lineTimetableWidth","4"))+1;
        if(secondFrag){
            lineTextSize+=3;
        }
        return (int)(Companion.getTextSize() *lineTextSize*0.5f);
    }
    private void drawText(Canvas canvas, String text, int x, int y, Paint paint, boolean centerFrag){
        if(centerFrag){
            canvas.drawText(text,(this.getWidth()-2- paint.measureText(text))/2,y,paint);
        }else{
            canvas.drawText(text,x,y,paint);
        }
    }
}
