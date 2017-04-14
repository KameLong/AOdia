package com.fc2.web.kamelong.aodia.diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.timeTable.KLView;

/**
 * Created by kame on 2016/12/01.
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
public class TimeView extends KLView {
    public  float scaleX =15;
    public  float scaleY =42;
    private DiagramSetting setting;
    private DiaFile diaFile;
    TimeView (Context context,DiagramSetting s,DiaFile d) {
        super(context);
        setting=s;
        diaFile=d;
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        textPaint.setColor(Color.BLACK);
        switch(setting.veriticalAxis()){
            case 0:
                for(int i=0;i<24;i++){
                    if(i*3600- diaFile.getDiagramStartTime()<0){
                        canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }else {
                        canvas.drawText(String.valueOf(i), (i*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }
                }
                break;

            case 2:
                if(textPaint.getTextSize()*5<20*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 3:
                if(textPaint.getTextSize()*5<15*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 1:
                if(textPaint.getTextSize()*5<30*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 7:
                if(textPaint.getTextSize()*5<5*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":05"), ((i+24)*3600+300- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i+24)*3600+1500- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i+24)*3600+2100- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i+24)*3600+3300- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":05"), ((i)*3600+300- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i)*3600+1500- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i)*3600+2100- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i)*3600+3300- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 6:
            case 5:
            case 4:
                if(textPaint.getTextSize()*5<10*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":10"), ((i+24)*3600+600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i+24)*3600+3000- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":10"), ((i)*3600+600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i)*3600+3000- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                if(textPaint.getTextSize()*5<30*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.getDiagramStartTime()<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.getDiagramStartTime())/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;


        }


        for(int i=3;i<24;i++){
            canvas.drawText(String.valueOf(i),(i-3)*60* scaleX,(float)(textPaint.getTextSize()*0.8),textPaint);
        }
        for(int i=24;i<27;i++){
            canvas.drawText(String.valueOf(i-24),(i-3)*60* scaleX,(float)(textPaint.getTextSize()*0.8),textPaint);
        }
    }
    protected int getXsize(){
        return (int)(scaleX *60*24);
    }
    protected int getYsize(){
        return (int)textPaint.getTextSize();
    }
    public void setScale(float x,float y){
        scaleX =x;
        scaleY =y;
    }
}
