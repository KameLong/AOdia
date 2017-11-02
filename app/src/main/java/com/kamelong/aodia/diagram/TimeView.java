package com.kamelong.aodia.diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.timeTable.KLView;

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
 */

/**
 * @author KameLong
 * TimeViewはダイヤグラム画面上部の時刻表示の部分に使われます。
 * ダイヤ線が細かい時は分単位まで表示する
 */
public class TimeView extends KLView {
    private float scaleX =15;
    private float scaleY =42;
    private DiagramSetting setting;
    private AOdiaDiaFile diaFile;
    private int diagramStartTime=0;
    TimeView (Context context,DiagramSetting s,AOdiaDiaFile d) {
        super(context);
        setting=s;
        diaFile=d;
        diagramStartTime=diaFile.getService().getDiagramStartTime();
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        textPaint.setColor(Color.BLACK);
        //時間軸表示に合わせて描画する内容を切り替える
        //隣の文字との間隔が狭くなる時は一部の表示を無くすことで文字がかぶらないようにする
        switch(setting.veriticalAxis()){
            case 0:
                for(int i=0;i<24;i++){
                    if(i*3600- diagramStartTime<0){
                        canvas.drawText(String.valueOf(i), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }else {
                        canvas.drawText(String.valueOf(i), (i*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }
                }
                break;

            case 2:
                if(textPaint.getTextSize()*5<20*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 3:
                if(textPaint.getTextSize()*5<15*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 1:
                if(textPaint.getTextSize()*5<30*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 7:
                if(textPaint.getTextSize()*5<5*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":05"), ((i+24)*3600+300- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i+24)*3600+1500- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i+24)*3600+2100- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i+24)*3600+3300- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":05"), ((i)*3600+300- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i)*3600+1500- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i)*3600+2100- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i)*3600+3300- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 6:
            case 5:
            case 4:
                if(textPaint.getTextSize()*5<10*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":10"), ((i+24)*3600+600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i+24)*3600+3000- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":10"), ((i)*3600+600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i)*3600+3000- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                if(textPaint.getTextSize()*5<30*scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diagramStartTime)/60* scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;


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
