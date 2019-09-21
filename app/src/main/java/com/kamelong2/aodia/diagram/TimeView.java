package com.kamelong2.aodia.diagram;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.aodia.AOdiaDefaultView;

public class TimeView extends AOdiaDefaultView {
    private DiagramSetting setting;
    private DiaFile diaFile;

    public TimeView (Context context, DiagramSetting s, DiaFile d) {
        super(context);
        setting=s;
        diaFile=d;
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        textPaint.setColor(Color.BLACK);
        //時間軸表示に合わせて描画する内容を切り替える
        //隣の文字との間隔が狭くなる時は一部の表示を無くすことで文字がかぶらないようにする
        switch(setting.verticalAxis){
            case 0:
                for(int i=0;i<24;i++){
                    if(i*3600- diaFile.diagramStartTime<0){
                        canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }else {
                        canvas.drawText(String.valueOf(i), (i*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                    }
                }
                break;

            case 2:
                if(textPaint.getTextSize()*5<20*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 3:
                if(textPaint.getTextSize()*5<15*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 1:
                if(textPaint.getTextSize()*5<30*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;
            case 7:
                if(textPaint.getTextSize()*5<5*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":05"), ((i+24)*3600+300- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i+24)*3600+900- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i+24)*3600+1500- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i+24)*3600+2100- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i+24)*3600+2700- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i+24)*3600+3300- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":05"), ((i)*3600+300- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":15"), ((i)*3600+900- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":25"), ((i)*3600+1500- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":35"), ((i)*3600+2100- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":45"), ((i)*3600+2700- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":55"), ((i)*3600+3300- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
            case 6:
            case 5:
            case 4:
                if(textPaint.getTextSize()*5<10*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":10"), ((i+24)*3600+600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i+24)*3600+1200- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i+24)*3600+2400- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i+24)*3600+3000- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":10"), ((i)*3600+600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":20"), ((i)*3600+1200- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":40"), ((i)*3600+2400- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":50"), ((i)*3600+3000- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                if(textPaint.getTextSize()*5<30*60*setting.scaleX){
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i+":00"), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i+24)*3600+1800- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i+":00"), ((i)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                            canvas.drawText(String.valueOf(i+":30"), ((i)*3600+1800- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }else{
                    for(int i=0;i<24;i++){
                        if(i*3600- diaFile.diagramStartTime<0){
                            canvas.drawText(String.valueOf(i), ((i+24)*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }else {
                            canvas.drawText(String.valueOf(i), (i*3600- diaFile.diagramStartTime)* setting.scaleX, (float) (textPaint.getTextSize() * 0.8), textPaint);
                        }
                    }
                }
                break;


        }

    }
    protected int getXsize(){
        return (int)(setting.scaleX *60*60*24);
    }
    protected int getYsize(){
        return (int)textPaint.getTextSize();
    }

}
