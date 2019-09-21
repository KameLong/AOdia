package com.kamelong.aodia.DiagramFragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.kamelong.aodia.AOdiaData.LineFile;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

public class TimeView extends DiagramDefaultView {
    private LineFile lineFile;

    public TimeView (Context context, DiagramOptions option, LineFile lineFile) {
        super(context,option);
        this.lineFile =lineFile;
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        textPaint.setColor(Color.BLACK);
        //時間軸表示に合わせて描画する内容を切り替える
        //隣の文字との間隔が狭くなる時は一部の表示を無くすことで文字がかぶらないようにする
        switch(options.verticalAxis){
            case 0:
                for(int i=0;i<24;i++){
                    if(i*3600- lineFile.diagramStartTime<0){
                        canvas.drawText(String.valueOf(i), ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }else {
                        canvas.drawText(String.valueOf(i), (i*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }
                }
                break;

            case 2:
                if(textPaint.getTextSize()*5<20*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":00", ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":20", ((i+24)*3600+1200- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":40", ((i+24)*3600+2400- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":00", ((i)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":20", ((i)*3600+1200- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":40", ((i)*3600+2400- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 3:
                if(textPaint.getTextSize()*5<15*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":15", ((i+24)*3600+900- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":45", ((i+24)*3600+2700- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":15", ((i)*3600+900- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":45", ((i)*3600+2700- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 1:
                if(textPaint.getTextSize()*5<30*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":00", ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":30", ((i+24)*3600+1800- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":00", ((i)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":30", ((i)*3600+1800- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 7:
                if(textPaint.getTextSize()*5<5*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":05", ((i+24)*3600+300- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":15", ((i+24)*3600+900- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":25", ((i+24)*3600+1500- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":35", ((i+24)*3600+2100- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":45", ((i+24)*3600+2700- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":55", ((i+24)*3600+3300- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":05", ((i)*3600+300- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":15", ((i)*3600+900- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":25", ((i)*3600+1500- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":35", ((i)*3600+2100- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":45", ((i)*3600+2700- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":55", ((i)*3600+3300- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 6:
            case 5:
            case 4:
                if(textPaint.getTextSize()*5<10*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":10", ((i+24)*3600+600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":20", ((i+24)*3600+1200- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":40", ((i+24)*3600+2400- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":50", ((i+24)*3600+3000- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":10", ((i)*3600+600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":20", ((i)*3600+1200- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":40", ((i)*3600+2400- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":50", ((i)*3600+3000- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                if(textPaint.getTextSize()*5<30*60* options.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(i + ":00", ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":30", ((i+24)*3600+1800- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(i + ":00", ((i)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(i + ":30", ((i)*3600+1800- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- lineFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- lineFile.diagramStartTime)* options.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;


        }

    }
    protected int getXsize(){
        return (int)(options.scaleX *60*60*24);
    }
    protected int getYsize(){
        return (int)textPaint.getTextSize();
    }

}
