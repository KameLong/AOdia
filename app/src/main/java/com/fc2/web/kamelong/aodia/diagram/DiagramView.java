package com.fc2.web.kamelong.aodia.diagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.oudia.Train;
import com.fc2.web.kamelong.aodia.oudia.TrainType;
import com.fc2.web.kamelong.aodia.timeTable.KLView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Created by kame on 2016/11/30.
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
public class DiagramView extends KLView {
    private DiaFile diaFile;
    private DiagramSetting setting;
    //削るべき
    private ArrayList<Integer>stationTime=new ArrayList<Integer>();
    private int diaNum=0;
    public  float scaleX =15;
    public  float scaleY =42;

    ArrayList<ArrayList<Integer>>[] diagramPath=new ArrayList[2];
    ArrayList<Train>[]trainList=new ArrayList[2];
    ArrayList<Integer>[] stopMark=new ArrayList[2];

    private Train focsTrain=null;
    private float defaultLineSize=1;
    DiagramView(Context context){
        super(context);
    }
    /**
     *画面密度から線の太さを決める
     */
    private void getDensity(){
        //get display density
        float density = getResources().getDisplayMetrics().densityDpi / 160f;
        defaultLineSize=density;
    }

    DiagramView(Context context,DiagramSetting s, DiaFile dia,int num){
        this(context);
        try {
            setting=s;
            diaFile=dia;
            diaNum=num;
            getDensity();
            //make stationTime
            stationTime=dia.getStationTime();

            //makeDiagramData
            diagramPath[0]=new  ArrayList<ArrayList<Integer>>();
            diagramPath[1]=new  ArrayList<ArrayList<Integer>>();
            trainList[0]=new ArrayList<Train>();
            trainList[1]=new ArrayList<Train>();
            stopMark[0]=new ArrayList<Integer>();
            stopMark[1]=new ArrayList<Integer>();

            for(int direct=0;direct<2;direct++){
                for (int i = 0; i < diaFile.getTrainNum(diaNum, direct); i++) {
                    Train train= dia.getTrain(diaNum, direct, i);
                    ArrayList<Integer> trainPath=new ArrayList<Integer>();
                    trainPath.add((train.getDepartureTime(train.getStartStation(direct)) - dia.getDiagramStartTime()));
                    trainPath.add(stationTime.get(train.getStartStation(direct)));
                    boolean drawable=true;
                    //駅ループ
                    for (int j = train.getStartStation(direct) + (1-direct*2);(1-direct*2)* j <(1-direct*2)* (train.getEndStation(direct)+ (1-direct*2)); j=j+(1-direct*2)) {
                        if(drawable&&(train.getStopType(j)==0||train.getStopType(j)==3)){
                            drawable=false;
                            trainPath.add(trainPath.get(trainPath.size()-2));
                            trainPath.add(trainPath.get(trainPath.size()-2));
                        }
                        if(train.timeExist(j)) {
                            if(direct==0&&j>0&&train.getStopType(j-1)==1&&(!train.arriveExist(j))&&
                                    stationTime.get(j)-stationTime.get(j-1)+60<train.getDepartureTime(j)-train.getDepartureTime(j-1)) {
                                //最小所要時間判定
                                trainPath.add(train.getDepartureTime(j - 1) + stationTime.get(j) - stationTime.get(j - 1) + 30 - dia.getDiagramStartTime());
                            }else if(direct==1&&j < dia.getStationNum() - 1 && train.getStopType(j + 1) == 1&&(!train.arriveExist(j))&&
                                    stationTime.get(j + 1) - stationTime.get(j) + 60 < train.getArriveTime(j) - train.getDepartureTime(j + 1)){
                                trainPath.add(train.getDepartureTime(j+1)+stationTime.get(j+1)-stationTime.get(j)+30 - 10800);
                            }else {
                                if(train.arriveExist(j)){
                                    trainPath.add((train.getArriveTime(j) - dia.getDiagramStartTime()));
                                }else{
                                    trainPath.add((train.getDepartureTime(j) - dia.getDiagramStartTime()));
                                }
                            }
                            trainPath.add( stationTime.get(j));
                            if(drawable) {
                                trainPath.add(trainPath.get(trainPath.size() - 2));
                                trainPath.add(trainPath.get(trainPath.size() - 2));
                            }
                            try {
                                if (trainPath.get(trainPath.size() - 4) - trainPath.get(trainPath.size() - 6) < -60 * 60 * 12) {
                                    trainPath.set(trainPath.size() - 4, trainPath.get(trainPath.size() - 4) + 60 * 60 * 24);
                                    trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 6)- 60 * 60 * 24);//x
                                    trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 6));//y
                                    trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 2));//x
                                    trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 6));//y
                                }
                            }catch(Exception e){
                                //no probrem
                            }
                            drawable=true;
                        }else{

                        }
                        if(train.getDepartureTime(j)>0){
                            trainPath.add((train.getDepartureTime(j)- dia.getDiagramStartTime()));
                            trainPath.add( stationTime.get(j) );
                            if(drawable) {
                                trainPath.add(trainPath.get(trainPath.size()-2));
                                trainPath.add(trainPath.get(trainPath.size()-2));
                                try {
                                    if (trainPath.get(trainPath.size() - 4) - trainPath.get(trainPath.size() - 6) < -60 * 60 * 12) {
                                        trainPath.set(trainPath.size() - 4, trainPath.get(trainPath.size() - 4) + 60 * 60 * 24);
                                        trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 6)- 60 * 60 * 24);//x
                                        trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 4));//y
                                        trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 2));//x
                                        trainPath.add(trainPath.size()-2,trainPath.get(trainPath.size() - 4));//y
                                    }
                                }catch(Exception e){
                                    //no probrem
                                }
                            }else{
                                drawable=false;
                            }

                            try {
                                if (train.getStopType(j) == 1 && dia.getTrainType(train.getType()).getShowStop() &&j!=train.getStartStation(0)&&j!=train.getEndStation(0)&& trainPath.get(trainPath.size() - 4).equals(trainPath.get(trainPath.size() - 6))) {
                                    stopMark[direct].add(trainPath.get(trainPath.size() - 4));
                                    stopMark[direct].add(trainPath.get(trainPath.size() - 3));
                                }
                            }catch(Exception e){
                                SdLog.log(e);
                            }
                        }
                    }
                    if(drawable) {
                        trainPath.add(trainPath.get(trainPath.size() - 2));
                        trainPath.add(trainPath.get(trainPath.size() - 2));

                    }
                    diagramPath[direct].add(trainPath);
                    trainList[direct].add(train);
                }
            }


        } catch(Exception e){
            SdLog.log(e);
        }

    }
    public  float[] toArr(List<Integer> list){
        // List<Integer> -> int[]
        int l = list.size();
        float[] arr = new float[l];
        Iterator<Integer> iter = list.iterator();
        for (int i=0;i<l;i++){
            arr[i] = iter.next()*scaleX/60;
            i++;
            arr[i] = iter.next()*scaleY/60;
        }
        return arr;
    }
    private void drawStationLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200, 200, 200));


        for (int i = 0; i < diaFile.getStationNum(); i++) {
            if (diaFile.getStation(i).getBigStation()) {
                paint.setStrokeWidth(defaultLineSize);
            } else {
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0, stationTime.get(i) * scaleY / 60, 60*24* scaleX, stationTime.get(i) * scaleY / 60, paint);
        }
    }
    public void drawTrain(Canvas canvas){
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (int direct = 0; direct < 2; direct++) {
                if ((direct == 0 && setting.downFrag) || (direct == 1 && setting.upFrag)) {
                    for (int i = 0; i < trainList[direct].size(); i++) {
                        paint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getDiaColor());
                        if (focsTrain != null) {
                            paint.setAlpha(100);
                        }
                        if (diaFile.getTrainType(trainList[direct].get(i).getType()).getLineBold()) {
                            paint.setStrokeWidth(defaultLineSize * 2f);
                        } else {
                            paint.setStrokeWidth(defaultLineSize);
                        }

                        //強調ダイヤ線時刻描画
                        if (trainList[direct].get(i) == focsTrain) {
                            paint.setAlpha(255);
                            paint.setStrokeWidth(defaultLineSize * 3f);
                            textPaint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getDiaColor());
                            textPaint.setAlpha(255);
                            for (int j = 0; j < diaFile.getStationNum(); j++) {
                                if (focsTrain.arriveExist(j)) {
                                    canvas.drawText(String.format("%02d", (focsTrain.getArriveTime(j) / 60) % 60), (focsTrain.getArriveTime(j) - 3 * 3600) * scaleX / 60, stationTime.get(j) * scaleY / 60 + textPaint.getTextSize() *(-0.2f+direct*1.2f), textPaint);
                                }
                                if (focsTrain.departExist(j)) {
                                    canvas.drawText(String.format("%02d", (focsTrain.getDepartureTime(j) / 60) % 60), (focsTrain.getDepartureTime(j) - 3 * 3600) * scaleX / 60 - textPaint.getTextSize() , stationTime.get(j) * scaleY / 60 + textPaint.getTextSize() *(1-direct*1.2f), textPaint);

                                }
                            }
                        }
                        switch ((diaFile.getTrainType(trainList[direct].get(i).getType()).getLineStyle())) {
                            case TrainType.LINESTYLE_NORMAL:
                                canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                                break;
                            case TrainType.LINESTYLE_DASH:
                                drawDashLines(canvas, toArr(diagramPath[direct].get(i)), 10, 10, paint);
                                break;
                            case TrainType.LINESTYLE_DOT:
                                drawDotLines(canvas, toArr(diagramPath[direct].get(i)), paint);
                                break;
                            case TrainType.LINESTYLE_CHAIN:
                                drawChainLines(canvas, toArr(diagramPath[direct].get(i)), 10, 10, paint);
                                break;
                        }
                    }
                    if (setting.stopFrag) {
                        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
                        Point p = new Point();
                        display.getSize(p);
                        ;
                        paint.setColor(Color.BLACK);
                        paint.setStrokeWidth(defaultLineSize);
                        for (int i = 0; i < stopMark[direct].size() / 2; i++) {
                            canvas.drawCircle(stopMark[direct].get(2 * i) * scaleX / 60, stopMark[direct].get(2 * i + 1) * scaleY / 60, defaultLineSize * 2f, paint);
                        }
                    }
                }
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        try {
            drawAxis(canvas);
            drawStationLine(canvas);
            drawNowTime(canvas);
            drawTrain(canvas);
            drawTrainNumber(canvas);
        } catch (Exception e) {
            SdLog.log(e);
        }
    }
    private void drawAxis(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200, 200, 200));
        Paint dotPaint = new Paint();
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 10.0f}, 0)); // 5pixel描いたら5pixel描かないを繰り返す
        dotPaint.setColor(Color.rgb(200, 200, 200));
        Paint dot2Paint = new Paint();
        dot2Paint.setAntiAlias(true);
        dot2Paint.setStyle(Paint.Style.STROKE);
        dot2Paint.setColor(Color.rgb(200, 200, 200));
        dot2Paint.setStrokeWidth(defaultLineSize*0.25f);
        //1時間ごとの目盛
        //以下太実線
        paint.setStrokeWidth(defaultLineSize);
        if(setting.veriticalAxis()==7){
            for (int i = 0; i < 48; i++) {
                canvas.drawLine(scaleX * (30 + 30 * i), 0, scaleX * (30 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
            }

        }else {
            for (int i = 0; i < 24; i++) {
                canvas.drawLine(scaleX * (60 + 60 * i), 0, scaleX * (60 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
            }
        }
        paint.setStrokeWidth(defaultLineSize*0.5f);
        dotPaint.setStrokeWidth(defaultLineSize*0.5f);
        switch(setting.veriticalAxis()){
            case 1:
                for (int i = 0; i < 24; i++) {
                    //30分ごとの目盛
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                break;
            case 2:
                //20分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                break;
            case 3:
                //15分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (15 + 60 * i), 0, scaleX * (15 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (45 + 60 * i), 0, scaleX * (45 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                break;
            case 4:
                //10分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 48; i++) {
                    if( stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (10 + 30 * i), 0, scaleX * (10 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        canvas.drawLine(scaleX * (20 + 30 * i), 0, scaleX * (20 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                    }else {
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (10 + 30 * i), 0);
                        dotLine.lineTo(scaleX * (10 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60);
                        dotLine.moveTo(scaleX * (20 + 30 * i), 0);
                        dotLine.lineTo(scaleX * (20 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60);
                        canvas.drawPath(dotLine, dotPaint);
                    }


                }
                break;
            case 5:
                //5分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), 0, scaleX * (10 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), 0, scaleX * (50 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    if( stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (5 + 10 * i), 0, scaleX * (5 + 10 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (5 + 10 * i), 0);
                        dotLine.lineTo(scaleX * (5 + 10 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 6:
                //2分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), 0, scaleX * (10 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), 0, scaleX * (50 + 60 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    for(int j=1;j<5;j++){
                        if( stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * (2 * j + 10 * i), 0, scaleX * (2 * j + 10 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        }else {
                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * (2 * j + 10 * i), 0);
                            dotLine.lineTo(scaleX * (2 * j + 10 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
            case 7:
                //1分ごとの目盛

                for (int i = 0; i < 24*2; i++) {
                    canvas.drawLine(scaleX * (5 + 30 * i), 0, scaleX * (5 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (10 + 30 * i), 0, scaleX * (10 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (15 + 30 * i), 0, scaleX * (15 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 30 * i), 0, scaleX * (20 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (25 + 30 * i), 0, scaleX * (25 + 30 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*12; i++) {
                    for(int j=1;j<5;j++) {
                        if( stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * (1 * j + 5 * i), 0, scaleX * (1 * j + 5 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        }else{

                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * (1 * j + 5 * i), 0);
                            dotLine.lineTo(scaleX * (1 * j + 5 * i), stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
        }

    }
    private void drawTrainNumber(Canvas canvas){
        for(int direct=0;direct<2;direct++){
            if((direct==0&&setting.downFrag)||(direct==1&&setting.upFrag)) {
                for(int i=0;i<trainList[direct].size();i++){
                    int pathNum=-1;
                    for(int j=0;j+3<diagramPath[direct].get(i).size();j=j+2){
                        if(diagramPath[direct].get(i).get(j+1)!=diagramPath[direct].get(i).get(j+3)){
                            pathNum=j;
                            break;
                        }
                    }
                    if(pathNum<0)continue;
                    int x1=(int)(diagramPath[direct].get(i).get(pathNum)*scaleX/60);
                    int y1=(int)(diagramPath[direct].get(i).get(pathNum+1)*scaleY/60);
                    int x2=(int)(diagramPath[direct].get(i).get(pathNum+2)*scaleX/60);
                    int y2=(int)(diagramPath[direct].get(i).get(pathNum+3)*scaleY/60);
                    canvas.save();
                    double rad=Math.atan2((double)(y2-y1),(double)(x2-x1));
                    canvas.rotate((float) Math.toDegrees(rad),x1,y1);
                    textPaint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getDiaColor());
                    if(focsTrain==null||focsTrain==trainList[direct].get(i)){
                        textPaint.setAlpha(255);
                    }else{
                        textPaint.setAlpha(100);
                    }
                    String text="";
                    if(setting.numberFrag){
                        if(trainList[direct].get(i).getNumber().length()>0) {
                            text = text + trainList[direct].get(i).getNumber() + "   ";
                        }
                    }
                    if(setting.nameFrag){
                        if(trainList[direct].get(i).getName().length()>0) {
                            text = text + trainList[direct].get(i).getName() + "  ";
                        }
                        if(trainList[direct].get(i).getCount().length()>0) {
                            text = text + trainList[direct].get(i).getCount();
                        }
                    }
                    if(rad>0) {
                        canvas.drawText(text, x1 + (int) (textPaint.getTextSize() / Math.tan(rad)), y1 - textPaint.getTextSize() / 6, textPaint);
                    }else{
                        canvas.drawText(text, x1+(int) (textPaint.getTextSize()), y1 - textPaint.getTextSize() / 6, textPaint);
                    }
                    canvas.restore();

                }
            }
        }
    }

    private void drawNowTime(Canvas canvas){
        if(setting.autoScrollState==0){
            return;
        }
        int nowTime=(int)(System.currentTimeMillis()%(24*60*60*1000))/1000;
        //9*60*60 は時差
        nowTime= nowTime-diaFile.getDiagramStartTime()+9*60*60;
        if(nowTime<0){
            nowTime=nowTime+24*60*60;
        }
        if(nowTime>24*60*60){
            nowTime=nowTime-24*60*60;
        }
        Paint paint=new Paint();
        paint.setColor(Color.argb(255,255,0,0));
        paint.setStrokeWidth(defaultLineSize*1.0f);
        paint.setAntiAlias(true);
        canvas.drawLine(nowTime*scaleX/60,0,nowTime*scaleX/60, stationTime.get(diaFile.getStationNum() - 1) * scaleY / 60,paint);
    }
    /**
     * ダイヤグラム内の特定の列車をフォーカスする。
     * @param x
     * @param y
     */
    public void showDetail(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x * 60 / scaleX);
            y = (int)(y * 60 / scaleY);
            if (y > stationTime.get(stationTime.size() - 1)) {
                return;
            }
            //描画しているダイヤ線のうちタッチポイントに最も近いものを検出します。
            float minDistance = 4000;
            int minTrainNum = -1;
            int minTrainDirect = -1;
            for(int direct=0;direct<2;direct++){
                if((direct==0&&setting.downFrag)||(direct==1&&setting.upFrag)) {
                    for (int i = 0; i < diagramPath[direct].size(); i++) {
                        for (int j = 0; j < diagramPath[direct].get(i).size() / 4; j++) {
                            if (diagramPath[direct].get(i).get(4 * j) < x && diagramPath[direct].get(i).get(4 * j + 2) > x) {
                                float distance;
                                if (true) {
                                    //xの差のほうが大きい
                                    distance = scaleY / 60f * Math.abs(((float) diagramPath[direct].get(i).get(4 * j + 3) - (float) diagramPath[direct].get(i).get(4 * j + 1)) /
                                            ((float) diagramPath[direct].get(i).get(4 * j + 2) - (float) diagramPath[direct].get(i).get(4 * j)) * (x - diagramPath[direct].get(i).get(4 * j)) + diagramPath[direct].get(i).get(4 * j + 1) - y);
                                } else {
                                    //yの差のほうが大きい
                                    distance = scaleX * Math.abs((diagramPath[direct].get(i).get(4 * j + 2) - diagramPath[direct].get(i).get(4 * j)) /
                                            (diagramPath[direct].get(i).get(4 * j + 3) - diagramPath[direct].get(i).get(4 * j + 1)) * (y - diagramPath[direct].get(i).get(4 * j + 1)) + diagramPath[direct].get(i).get(4 * j) - x);
                                }
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    minTrainNum = i;
                                    minTrainDirect=direct;
                                }
                            }
                        }
                    }
                }}

            if(minTrainNum < 0){
                focsTrain=null;
                this.invalidate();
                return;
            }
            if(focsTrain==trainList[minTrainDirect].get(minTrainNum)){
                focsTrain=null;
            }else {
                focsTrain = trainList[minTrainDirect].get(minTrainNum);
            }
            this.invalidate();

        }catch(Exception e){
            SdLog.log(e);
        }

    }
    public void setScale(float x,float y){
        scaleX =x;
        scaleY =y;
        this.layout(0,0, getXsize(),getYsize());
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.getSize(heightMeasureSpec)>getYsize()){
            this.setMeasuredDimension(getXsize(),MeasureSpec.getSize(heightMeasureSpec));
        }else{
            this.setMeasuredDimension(getXsize(),getYsize());
        }
    }


    public int getmHeight(){
        return getYsize();
    }
    public int getmWidth(){
        return getXsize();
    }
    protected int getXsize(){
        return (int)(1440* scaleX);
    }
    protected int getYsize(){
        return (int)(stationTime.get(diaFile.getStationNum()-1)* scaleY /60+(int)textPaint.getTextSize()+4);
    }


    private void drawDashLine(Canvas canvas,float x1,float y1,float x2,float y2,float dash1,float dash2,Paint paint){
        float x=(x2-x1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float y=(y2-y1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        if(x>y){
            for(int i=0;i<(x2-x1)/(dash1+dash2)/x;i++){
                canvas.drawLine(x1+(dash1+dash2)*x*i,y1+(dash1+dash2)*y*i,x1+((dash1+dash2)*i+dash1)*x,y1+((dash1+dash2)*i+dash1)*y,paint);
            }
        }else{
            for(int i=0;i<(y2-y1)/(dash1+dash2)/y;i++){
                canvas.drawLine(x1+(dash1+dash2)*x*i,y1+(dash1+dash2)*y*i,x1+((dash1+dash2)*i+dash1)*x,y1+((dash1+dash2)*i+dash1)*y,paint);
            }
        }

    }
    private void drawDashLines(Canvas canvas,float[] list,float dash1,float dash2,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDashLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],dash1,dash2,paint);
        }
    }
    private void drawDotLine(Canvas canvas,float x1,float y1,float x2,float y2,Paint paint){
        float x=(x2-x1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float y=(y2-y1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float loop = paint.getStrokeWidth() * 2;
        if(x>y) {
            for (int i = 0; i < (x2 - x1) / (loop) / x; i++) {
                canvas.drawPoint(x1 + (loop) * x * i, y1 + (loop) * y * i, paint);
            }
        }
        for (int i = 0; i < (y2 - y1) / (loop) / y; i++) {
            canvas.drawPoint(x1 + (loop) * x * i, y1 + (loop) * y * i, paint);
        }
    }
    private void drawDotLines(Canvas canvas,float[] list,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDotLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],paint);
        }
    }
    private void drawChainLine(Canvas canvas,float x1,float y1,float x2,float y2,float chain,float space,Paint paint){
        float x=(x2-x1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float y=(y2-y1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float loop=chain+2*space;
        for(int i=0;i<(x2-x1)/(loop)/x;i++){
            canvas.drawLine(x1+(loop)*x*i,y1+(loop)*y*i,x1+(loop*i+chain)*x,y1+(loop*i+chain)*y,paint);
            canvas.drawPoint(x1+(loop*i+chain+space)*x,y1+(loop*i+chain+space)*y,paint);
        }
    }
    private void drawChainLines(Canvas canvas,float[] list,float chain,float space,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawChainLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],chain,space,paint);
        }
    }
}
