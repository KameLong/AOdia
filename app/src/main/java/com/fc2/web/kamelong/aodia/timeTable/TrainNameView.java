package com.fc2.web.kamelong.aodia.timeTable;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fc2.web.kamelong.aodia.SdLog;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.oudia.Train;
import com.fc2.web.kamelong.aodia.timeTable.KLView;

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
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */
public class TrainNameView extends KLView {
    private DiaFile dia;
    private Train train;
    private int direct;
    private boolean secondFrag=false;
    private boolean showTrainName=false;
    TrainNameView(Context context){
        super(context);
    }
    TrainNameView(Context context,DiaFile diaFile,Train t){
        this(context);
        dia=diaFile;
        train=t;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        secondFrag=spf.getBoolean("secondSystem",secondFrag);
        showTrainName=spf.getBoolean("trainName",showTrainName);
    }
    public void onDraw(Canvas canvas){

        int startLine=(int)textPaint.getTextSize();
        textPaint.setColor(dia.getTrainType(train.getType()).getTextColor());
        if(secondFrag) {
            canvas.drawText(dia.getTrainType(train.getType()).getName(), 1, startLine, textPaint);
        }else{
            canvas.drawText(dia.getTrainType(train.getType()).getShortName(), 1, startLine, textPaint);
        }
        startLine=startLine+(int)textPaint.getTextSize();
        canvas.drawText(train.getNumber(), 1,startLine, textPaint);


        canvas.drawLine(this.getWidth()-1, 0,this.getWidth()-1,this.getHeight(),blackPaint);
        if(showTrainName) {
            canvas.drawLine(0,textPaint.getTextSize()*2.1f,getWidth(),textPaint.getTextSize()*2.1f,blackPaint);
            drawTrainName(canvas);
        }
    }
    private void drawTrainName(Canvas canvas){
        try {
            int heightSpace = 12;
            String value= train.getName();
            value=value.replace('ー','｜');
            value=value.replace('（','(');
            value=value.replace('）',')');
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
            int startY = (int) (this.getHeight() - 8.1f * textSize);
            for (int i = 0; i < str.length; i++) {
                if (space <= 0) {
                    space = heightSpace;
                    startX = startX - (int)(textSize*1.2f);
                    startY = (int) (this.getHeight() - 8.1f * textSize);
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


        float textSize=textPaint.getTextSize();
        if(train.getCount().length()>0){
            String gousuu=train.getCount().substring(0,train.getCount().length()-1);
            ;
            canvas.drawText(gousuu,(getWidth()-textPaint.measureText(gousuu))/2,getHeight()-textSize*1.2f,textPaint);
            canvas.drawText("号",(getWidth()-textPaint.measureText("号"))/2,getHeight()-textSize*0.2f,textPaint);
        }
    }
    private boolean charIsEng(char c){
        return c<256;
    }
    protected int getYsize(){
        if(showTrainName){
            return (int)(textPaint.getTextSize()*10.2f);
        }else{
            return (int)(textPaint.getTextSize()*2.2f);
        }
    }
    public int getXsize(){
        if(secondFrag){
            return (int)(textPaint.getTextSize()*4.0f);
        }
        return (int)(textPaint.getTextSize()*2.5f);
    }
}
