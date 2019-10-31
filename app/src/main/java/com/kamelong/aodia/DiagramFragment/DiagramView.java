package com.kamelong.aodia.DiagramFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Train;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.EditTrain.OnTrainChangeListener;
import com.kamelong.aodia.EditTrain.TrainTimeEditFragment;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

public class DiagramView extends DiagramDefaultView{
    /**
     * ダイヤグラム描画に用いるパス
     * diagramPath[0]は下りダイヤ
     * diagramPath[1]は上りダイヤ
     * diagrampath[x].get(i)には、１列車のダイヤグラム描画パスがArrayListで入っている
     * このArrayListには描画する線の数*4個のIntegerが入っており、
     * １つの線当たり(startX,startY,endX,endY)の４つの値が入っている
     *
     */
    ArrayList<ArrayList<Integer>>[] diagramPath=new ArrayList[2];
    /**
     * 停車マークを描画する点
     * １つのマーク当たり(xPoint,yPoint)の２つの値が追加される
     */
    ArrayList<ArrayList<Integer>>[] stopMark=new ArrayList[2];
    /**
     * 強調表示されている列車
     * この列車を太線で表示する
     *
     * ダイヤグラム画面内を長押しすることで近くにあるダイヤ線の列車が強調表示に切り替わります
     */
    private Train focsTrain=null;
    private float defaultLineSize=1;

    public LineFile lineFile;
    public Diagram timeTable;
    public int lineIndex=0;
    public int diaIndex =0;
    public ArrayList<Integer>stationTime;
    private boolean onlySolid=false;
    /**
     *画面密度から線の太さを決める
     */
    private void getDensity(){
        float density = getResources().getDisplayMetrics().densityDpi / 160f;
        defaultLineSize=density;
    }

    DiagramView(Context context, DiagramOptions option, LineFile lineFile, int lineIndex, int diaIndex) {
        super(context,option);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        onlySolid=spf.getBoolean("onlySolid",false);

        try {
            this.lineFile =lineFile;
            this.diaIndex = diaIndex;
            timeTable = lineFile.diagram.get(diaIndex);
            stationTime=lineFile.getStationTime();
            getDensity();
            makeDiagramPath();



        } catch(Exception e){
            SDlog.log(e);
        }

    }
private void makeDiagramPath(Train train,int direct,int trainIndex){
    //この列車のdiagramPath
    ArrayList<Integer> trainPath=new ArrayList<Integer>();
    ArrayList<Integer> trainStopMark=new ArrayList<Integer>();
    if(trainIndex<0){
        try {
            Thread.sleep(10);
        }catch (Exception e){

        }
        trainIndex=timeTable.getTrainIndex(direct,train);
        if(trainIndex<0){
            return;
        }
    }
    if(train.getTimeStartStation()==-1){
        if(diagramPath[direct].size()<=trainIndex) {
            diagramPath[direct].add(trainPath);
        }else{
            diagramPath[direct].set(trainIndex,trainPath);
        }
        return;
    }
    //始発部分のパスを追加
    trainPath.add(getDiagramTime(train.getDepTime(train.getTimeStartStation())));
    trainPath.add(stationTime.get(train.getTimeStartStation()));
    boolean drawable=true;
    //駅ループ
    for (int j = train.getTimeStartStation() + (1-direct*2);(1-direct*2)* j <(1-direct*2)* (train.getTimeEndStation()+ (1-direct*2)); j=j+(1-direct*2)) {
        if(drawable&&train.getStopType(j)==0){
            //描画打ち切り
            drawable=false;
            trainPath.add(trainPath.get(trainPath.size()-2));
            trainPath.add(trainPath.get(trainPath.size()-2));
        }
        if(drawable&&train.getStopType(j)==3){
            //未処理
            drawable=false;
            trainPath.add(trainPath.get(trainPath.size()-2));
            trainPath.add(trainPath.get(trainPath.size()-2));
        }

        if(train.timeExist(j)) {
            if (train.timeExist(j - (1 - 2 * direct),Train.DEPART)) {
                //一つ前の駅にも発時刻が存在する場合　最小所要時間を考慮
                if (!train.timeExist(j,Train.ARRIVE)&&
                        Math.abs(stationTime.get(j) - stationTime.get(j - (1 - 2 * direct))) + 60 <= train.getDepTime(j) - train.getDepTime(j - (1 - 2 * direct))) {
                    //最小所要時間以上かかっているので最小所要時間を適用
                    trainPath.add(getDiagramTime(train.getTime(j - (1 - 2 * direct),Train.DEPART,true) + Math.abs(stationTime.get(j) - stationTime.get(j - (1 - 2 * direct))) + 30));
                } else {
                    //最小所要時間を採用しないとき
                    trainPath.add(getDiagramTime(train.getTime(j,Train.ARRIVE,true) ));
                }
            } else {
                //一つ前が通過駅の時など
                trainPath.add(getDiagramTime(train.getTime(j,Train.ARRIVE,true) ));
            }
            trainPath.add(stationTime.get(j));
            if (drawable) {
                //現段階で線描画途中の時は、終端点を追加
                trainPath.add(trainPath.get(trainPath.size()-2));
                trainPath.add(trainPath.get(trainPath.size()-2));
            }
            drawable=true;
            try {
                //もしパスが12時間以上遡るのなら、日付をまたいでいると判断する
                if (trainPath.get(trainPath.size() - 4) - trainPath.get(trainPath.size() - 6) < -60 * 60 * 12) {
                    trainPath.set(trainPath.size() - 4, trainPath.get(trainPath.size() - 4) + 60 * 60 * 24);
                    trainPath.add(trainPath.size() - 2, trainPath.get(trainPath.size() - 6) - 60 * 60 * 24);//x
                    trainPath.add(trainPath.size() - 2, trainPath.get(trainPath.size() - 6));//y
                    trainPath.add(trainPath.size() - 2, trainPath.get(trainPath.size() - 2));//x
                    trainPath.add(trainPath.size() - 2, trainPath.get(trainPath.size() - 6));//y
                }
            } catch (Exception e) {
                //no problem
            }
        }else{
            if(drawable&&train.getStopType(j)==2&&train.getStopType(j + (1 - 2 * direct))==3&&!train.timeExist(j)){

                if(train.getPredictionTime(j)<0){
                }else{
                    //この次の駅から経由なしになるとき
                    trainPath.add(getDiagramTime(train.getPredictionTime(j)));
                    trainPath.add(stationTime.get(j));
                    drawable=false;
                }
            }
            if(!drawable&&train.getStopType(j )==2&&train.getStopType(j - (1 - 2 * direct))==3&&!train.timeExist(j)){
                if(train.getPredictionTime(j)<0){

                }else{
                    //この前の駅めで経由なしのとき
                    trainPath.add(getDiagramTime(train.getPredictionTime(j)));
                    trainPath.add(stationTime.get(j));
                    drawable=true;
                }
            }
        }
        if(train.timeExist(j,Train.DEPART)){
            //停車時間を描画する
            trainPath.add(getDiagramTime(train.getTime(j,Train.DEPART,true)));
            trainPath.add(stationTime.get(j) );
            if(drawable) {
                trainPath.add(trainPath.get(trainPath.size()-2));
                trainPath.add(trainPath.get(trainPath.size()-2));
                try {
                    //もしパスが12時間以上遡るのなら、日付をまたいでいると判断する
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
            }
            try {
                if (train.getStopType(j) == 1 &&
                        lineFile.trainType.get(train.type).stopmark &&
                        j!=train.getTimeStartStation()&&
                        j!=train.getTimeEndStation()&&
                        trainPath.get(trainPath.size() - 4).equals(trainPath.get(trainPath.size() - 6))) {
                    //始発終着駅を除き　停車マークを用意する
                    trainStopMark.add(trainPath.get(trainPath.size() - 4));
                    trainStopMark.add(trainPath.get(trainPath.size() - 3));
                }
            }catch(Exception e){
                SDlog.log(e);
            }
        }
    }
    if(drawable) {
        trainPath.add(trainPath.get(trainPath.size() - 2));
        trainPath.add(trainPath.get(trainPath.size() - 2));

    }
    if(diagramPath[direct].size()<=trainIndex) {
        diagramPath[direct].add(trainPath);
    }else{
        diagramPath[direct].set(trainIndex,trainPath);
    }
    if(stopMark[direct].size()<=trainIndex) {
        stopMark[direct].add(trainStopMark);
    }else{
        stopMark[direct].set(trainIndex,trainStopMark);
    }

}
    /**
     * diagramPathを作成する
     */
    private void makeDiagramPath(){
        //makeDiagramData
        diagramPath[0]=new  ArrayList<ArrayList<Integer>>();
        diagramPath[1]=new  ArrayList<ArrayList<Integer>>();
        stopMark[0]=new ArrayList<>();
        stopMark[1]=new ArrayList<>();

        for(int direct=0;direct<2;direct++){
            for (int i = 0; i < timeTable.trains[direct].size(); i++) {
                Train train= timeTable.trains[direct].get(i);
                makeDiagramPath(train,direct,i);

            }
        }
    }


    /**
     * floatのArrayListをArrayに変換する
     * @param list
     * @return
     */
    public  float[] toArr(List<Integer> list){
        // List<Integer> -> int[]
        int l = list.size();
        float[] arr = new float[l];
        Iterator<Integer> iter = list.iterator();
        for (int i=0;i<l;i++){
            arr[i] = iter.next()* options.scaleX;
            i++;
            arr[i] = iter.next()* options.scaleY;
        }
        return arr;
    }

    /**
     * 駅軸を描画する
     *
     * @param canvas
     */
    private void drawStationLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200, 200, 200));


        for (int i = 0; i < lineFile.getStationNum(); i++) {
            if (lineFile.station.get(i).bigStation) {
                paint.setStrokeWidth(defaultLineSize);
            } else {
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0, stationTime.get(i) * options.scaleY, 60*24* options.scaleX *60, stationTime.get(i) * options.scaleY, paint);
        }
    }

    /**
     * 列車を描画する
     * @param canvas
     */
    public void drawTrain(Canvas canvas){
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (int direct = 0; direct < 2; direct++) {
                if ((direct == 0 && options.showDownTrain) || (direct == 1 && options.showUpTrain)) {
                    for (int i = 0; i < lineFile.getTrainNum(diaIndex,direct); i++) {
                        //ダイヤ線色を指定
                        paint.setColor(lineFile.trainType.get(lineFile.getTrain(diaIndex,direct,i).type).diaColor.getAndroidColor());
                        if (focsTrain!=null) {
                            //強調表示の列車があるときは半透明化
                            paint.setAlpha(100);
                        }
                        //線の太さを指定
                        if (lineFile.trainType.get(lineFile.getTrain(diaIndex,direct,i).type).bold) {
                            paint.setStrokeWidth(defaultLineSize * 2f);
                        } else {
                            paint.setStrokeWidth(defaultLineSize);
                        }

                        //強調ダイヤ線時刻描画
                        if (focsTrain==(lineFile.getTrain(diaIndex,direct,i))) {
                            //強調表示の線は半透明ではない
                            Train train= lineFile.getTrain(diaIndex,direct,i);
                            paint.setAlpha(255);
                            //線の太さを太くする
                            paint.setStrokeWidth(defaultLineSize * 3f);
                            //文字色もダイヤ色に合わせて変更
                            textPaint.setColor(lineFile.trainType.get(lineFile.getTrain(diaIndex,direct,i).type).diaColor.getAndroidColor());
                            textPaint.setAlpha(255);
                            for (int j = 0; j < lineFile.getStationNum(); j++) {
                                if (train.timeExist(j,Train.ARRIVE)) {
                                    canvas.drawText(String.format("%02d", (train.getAriTime(j) / 60) % 60), (train.getAriTime(j) - lineFile.diagramStartTime) * options.scaleX *60 / 60, stationTime.get(j) * options.scaleY + textPaint.getTextSize() *(-0.2f+direct*1.2f), textPaint);
                                }
                                if (train.timeExist(j,Train.DEPART)) {
                                    canvas.drawText(String.format("%02d", (train.getDepTime(j) / 60) % 60), (train.getDepTime(j) - lineFile.diagramStartTime) * options.scaleX *60 / 60 - textPaint.getTextSize() , stationTime.get(j) * options.scaleY + textPaint.getTextSize() *(1-direct*1.2f), textPaint);

                                }
                            }
                        }
                        if(onlySolid){
                            canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                        }else {
                            //指定線種に合わせてダイヤ線を描画
                            switch ((lineFile.trainType.get(lineFile.getTrain(diaIndex, direct, i).type).lineStyle)) {
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
                    }
                    if (options.showTrainStop) {
                        //停車駅の表示も行う

                        paint.setColor(Color.BLACK);
                        paint.setStrokeWidth(defaultLineSize);
                        for(ArrayList<Integer> stops:stopMark[direct]){
                            for (int i = 0; i < stops.size() / 2; i++) {
                                canvas.drawCircle(stops.get(2 * i) * options.scaleX *60 / 60, stops.get(2 * i + 1) * options.scaleY, defaultLineSize * 2f, paint);
                            }

                        }
                    }
                }
            }
        }catch(Exception e){
            SDlog.log(e);
        }
    }


    /**
     * onDrawをオーバーライドしたもの。
     * 描画処理はこの中に記述する
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        try {
            //縦軸線を描画
            drawAxis(canvas);
            //横軸戦を描画
            drawStationLine(canvas);
            //現在時間を描画（オプション）
            drawNowTime(canvas);
            //ダイヤ線を描画
            drawTrain(canvas);
            //列車番号を描画
            drawTrainNumber(canvas);
        } catch (Exception e) {
            SDlog.log(e);
        }
    }

    /**
     * 時間軸を描画する
     * DiagramSettingのverticalAxicsの値によってダイヤ線のスタイルが異なる
     * @param canvas
     */
    private void drawAxis(Canvas canvas){
        if(lineFile.getStationNum()==0){
            return;
        }
        //通常線
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setStrokeWidth(defaultLineSize);

        //点線
        Paint dotPaint = new Paint(paint);
        dotPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 10.0f}, 0)); // 10pixel描いたら10pixel描かないを繰り返す
        //点線の代わりに用いる細線
        Paint dot2Paint = new Paint(paint);
        dot2Paint.setStrokeWidth(defaultLineSize*0.25f);

        //縦線の高さ
        final int axisHeight=stationTime.get(lineFile.getStationNum() - 1);


        //1時間ごとの目盛
        //以下太実線
        if(options.verticalAxis ==7){
            for (int i = 0; i < 48; i++) {
                canvas.drawLine(options.scaleX *60 * (30 + 30 * i), 0, options.scaleX *60 * (30 + 30 * i),axisHeight * options.scaleY, paint);
            }

        }else {
            for (int i = 0; i < 24; i++) {
                canvas.drawLine(options.scaleX *60 * (60 + 60 * i), 0, options.scaleX *60 * (60 + 60 * i),axisHeight* options.scaleY, paint);
            }
        }
        paint.setStrokeWidth(defaultLineSize*0.5f);
        dotPaint.setStrokeWidth(defaultLineSize*0.5f);
        switch(options.verticalAxis){
            case 1:
                for (int i = 0; i < 24; i++) {
                    //30分ごとの目盛
                    canvas.drawLine(options.scaleX *60 * (30 + 60 * i), 0, options.scaleX *60 * (30 + 60 * i),axisHeight * options.scaleY, paint);
                }
                break;
            case 2:
                //20分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(options.scaleX *60 * (20 + 60 * i), 0, options.scaleX *60 * (20 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (40 + 60 * i), 0, options.scaleX *60 * (40 + 60 * i),axisHeight * options.scaleY, paint);
                }
                break;
            case 3:
                //15分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(options.scaleX *60 * (15 + 60 * i), 0, options.scaleX *60 * (15 + 60 * i),axisHeight * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (30 + 60 * i), 0, options.scaleX *60 * (30 + 60 * i),axisHeight * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (45 + 60 * i), 0, options.scaleX *60 * (45 + 60 * i),axisHeight * options.scaleY, paint);
                }
                break;
            case 4:
                //10分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(options.scaleX *60 * (30 + 60 * i), 0, options.scaleX *60 * (30 + 60 * i),axisHeight * options.scaleY, paint);
                }
                for (int i = 0; i < 48; i++) {
                    if(stationTime.get(lineFile.getStationNum() - 1) * options.scaleY>2048) {
                        canvas.drawLine(options.scaleX *60 * (10 + 30 * i), 0, options.scaleX *60 * (10 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, dot2Paint);
                        canvas.drawLine(options.scaleX *60 * (20 + 30 * i), 0, options.scaleX *60 * (20 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(options.scaleX *60 * (10 + 30 * i), 0);
                        dotLine.lineTo(options.scaleX *60 * (10 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY);
                        dotLine.moveTo(options.scaleX *60 * (20 + 30 * i), 0);
                        dotLine.lineTo(options.scaleX *60 * (20 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 5:
                //5分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(options.scaleX *60 * (10 + 60 * i), 0, options.scaleX *60 * (10 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (20 + 60 * i), 0, options.scaleX *60 * (20 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (30 + 60 * i), 0, options.scaleX *60 * (30 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (40 + 60 * i), 0, options.scaleX *60 * (40 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (50 + 60 * i), 0, options.scaleX *60 * (50 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    if(stationTime.get(lineFile.getStationNum() - 1) * options.scaleY>2048) {
                        canvas.drawLine(options.scaleX *60 * (5 + 10 * i), 0, options.scaleX *60 * (5 + 10 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(options.scaleX *60 * (5 + 10 * i), 0);
                        dotLine.lineTo(options.scaleX *60 * (5 + 10 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 6:
                //2分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(options.scaleX *60 * (10 + 60 * i), 0, options.scaleX *60 * (10 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (20 + 60 * i), 0, options.scaleX *60 * (20 + 60 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (30 + 60 * i), 0, options.scaleX *60 * (30 + 60 * i),axisHeight * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (40 + 60 * i), 0, options.scaleX *60 * (40 + 60 * i),axisHeight * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (50 + 60 * i), 0, options.scaleX *60 * (50 + 60 * i),axisHeight * options.scaleY, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    for(int j=1;j<5;j++){
                        if(stationTime.get(lineFile.getStationNum() - 1) * options.scaleY>2048) {
                            canvas.drawLine(options.scaleX *60 * (2 * j + 10 * i), 0, options.scaleX *60 * (2 * j + 10 * i),axisHeight * options.scaleY, dot2Paint);
                        }else {
                            Path dotLine = new Path();
                            dotLine.moveTo(options.scaleX *60 * (2 * j + 10 * i), 0);
                            dotLine.lineTo(options.scaleX *60 * (2 * j + 10 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
            case 7:
                //1分ごとの目盛

                for (int i = 0; i < 24*2; i++) {
                    canvas.drawLine(options.scaleX *60 * (5 + 30 * i), 0, options.scaleX *60 * (5 + 30 * i),axisHeight * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (10 + 30 * i), 0, options.scaleX *60 * (10 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (15 + 30 * i), 0, options.scaleX *60 * (15 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (20 + 30 * i), 0, options.scaleX *60 * (20 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                    canvas.drawLine(options.scaleX *60 * (25 + 30 * i), 0, options.scaleX *60 * (25 + 30 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, paint);
                }
                for (int i = 0; i < 24*12; i++) {
                    for(int j=1;j<5;j++) {
                        if(stationTime.get(lineFile.getStationNum() - 1) * options.scaleY>2048) {
                            canvas.drawLine(options.scaleX *60 * (1 * j + 5 * i), 0, options.scaleX *60 * (1 * j + 5 * i), stationTime.get(lineFile.getStationNum() - 1) * options.scaleY, dot2Paint);
                        }else{

                            Path dotLine = new Path();
                            dotLine.moveTo(options.scaleX *60 * (1 * j + 5 * i), 0);
                            dotLine.lineTo(options.scaleX *60 * (1 * j + 5 * i),axisHeight * options.scaleY);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
        }

    }

    /**
     * 列車番号・列車名を描画する
     * ダイヤ線の傾きに合わせて描画する
     * 上り列車、下り列車で描画する場所が違うので注意
     */
    private void drawTrainNumber(Canvas canvas){
        for(int direct=0;direct<2;direct++){
            if((direct==0&& options.showDownTrain)||(direct==1&& options.showUpTrain)) {
                for(int i = 0; i< lineFile.getTrainNum(diaIndex,direct); i++){
                    int pathNum=-1;
                    for(int j=0;j+3<diagramPath[direct].get(i).size();j=j+2){
                        if(diagramPath[direct].get(i).get(j+1)!=diagramPath[direct].get(i).get(j+3)){
                            pathNum=j;
                            break;
                        }
                    }
                    if(pathNum<0)continue;
                    //列車番号を表示する部分のダイヤ線の座標を取得
                    int x1=(int)(diagramPath[direct].get(i).get(pathNum)* options.scaleX *60/60);
                    int y1=(int)(diagramPath[direct].get(i).get(pathNum+1)* options.scaleY);
                    int x2=(int)(diagramPath[direct].get(i).get(pathNum+2)* options.scaleX *60/60);
                    int y2=(int)(diagramPath[direct].get(i).get(pathNum+3)* options.scaleY);
                    canvas.save();
                    double rad=Math.atan2((double)(y2-y1),(double)(x2-x1));
                    //canvasを回転して
                    canvas.rotate((float) Math.toDegrees(rad),x1,y1);
                    //列車番号を描画
                    textPaint.setColor(lineFile.trainType.get(lineFile.getTrain(diaIndex,direct,i).type).diaColor.getAndroidColor());
                    if(focsTrain==null||focsTrain==(lineFile.getTrain(diaIndex,direct,i))){
                        textPaint.setAlpha(255);
                    }else{
                        textPaint.setAlpha(100);
                    }
                    //textに表示したい文字列を代入
                    String text="";
                    if(options.numberState%2==1){
                        if(lineFile.getTrain(diaIndex,direct,i).number.length()>0) {
                            text = text + lineFile.getTrain(diaIndex,direct,i).number + "   ";
                        }
                    }
                    if(options.numberState/2==1){
                        if(lineFile.getTrain(diaIndex,direct,i).name.length()>0) {
                            text = text + lineFile.getTrain(diaIndex,direct,i).name + "  ";
                        }
                        if(lineFile.getTrain(diaIndex,direct,i).count.length()>0) {
                            text = text + lineFile.getTrain(diaIndex,direct,i).count;
                        }
                    }
                    //文字列を描画
                    if(rad>0) {
                        canvas.drawText(text, x1 + (int) (textPaint.getTextSize() / Math.tan(rad)), y1 - textPaint.getTextSize() / 6, textPaint);
                    }else{
                        canvas.drawText(text, x1+(int) (textPaint.getTextSize()), y1 - textPaint.getTextSize() / 6, textPaint);
                    }
                    //canvasの回転をもとに戻す
                    canvas.restore();

                }
            }
        }
    }

    /**
     * 現在時刻の線を描画する
     * @param canvas
     */
    private void drawNowTime(Canvas canvas){
        if(lineFile.getStationNum()==0){
            return;
        }
        if(options.autoScrollState==0){
            return;
        }
        int nowTime=(int)(System.currentTimeMillis()%(24*60*60*1000))/1000;
        //9*60*60 は時差
        nowTime= nowTime- lineFile.diagramStartTime+9*60*60;
        if(nowTime<0){
            nowTime=nowTime+24*60*60;
        }
        if(nowTime>24*60*60){
            nowTime=nowTime-24*60*60;
        }
        //これによりnowTimeに現在時刻が入った（秒単位）
        Paint paint=new Paint();
        paint.setColor(Color.argb(255,255,0,0));
        paint.setStrokeWidth(defaultLineSize*1.0f);
        paint.setAntiAlias(true);
        canvas.drawLine(nowTime* options.scaleX *60/60,0,nowTime* options.scaleX *60/60, stationTime.get(lineFile.getStationNum() - 1)* options.scaleY,paint);
    }
    /**
     * フォーカスする列車を選択する。
     * ダイヤグラム画面内を長押しすることで実行する。
     * @see #focsTrain フォーカスする列車
     *
     * これらのパラメーターは、DiagramViewの左上を基準とした座標
     * フォーカスした列車の編集画面を開く
     */

    public void showDetail(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x / options.scaleX );
            y = (int)(y / options.scaleY);
            if (y > stationTime.get(stationTime.size() - 1)) {
                return;
            }
            //描画しているダイヤ線のうちタッチポイントに最も近いものを検出します。
            float minDistance = 4000;
            int minTrainNum = -1;
            int minTrainDirect = -1;
            for(int direct=0;direct<2;direct++){
                if((direct==0&& options.showDownTrain)||(direct==1&& options.showUpTrain)) {
                    for (int i = 0; i < diagramPath[direct].size(); i++) {
                        for (int j = 0; j < diagramPath[direct].get(i).size() / 4; j++) {
                            if (diagramPath[direct].get(i).get(4 * j) < x && diagramPath[direct].get(i).get(4 * j + 2) > x) {
                                float distance;
                                if (true) {
                                    //xの差のほうが大きい
                                    distance = options.scaleY * Math.abs(((float) diagramPath[direct].get(i).get(4 * j + 3) - (float) diagramPath[direct].get(i).get(4 * j + 1)) /
                                            ((float) diagramPath[direct].get(i).get(4 * j + 2) - (float) diagramPath[direct].get(i).get(4 * j)) * (x - diagramPath[direct].get(i).get(4 * j)) + diagramPath[direct].get(i).get(4 * j + 1) - y);
                                } else {
                                    //yの差のほうが大きい
                                    distance = options.scaleX *60 * Math.abs((diagramPath[direct].get(i).get(4 * j + 2) - diagramPath[direct].get(i).get(4 * j)) /
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
            if(focsTrain!=(lineFile.getTrain(diaIndex,minTrainDirect,minTrainNum))){
                focsTrain=lineFile.getTrain(diaIndex,minTrainDirect,minTrainNum);
                this.invalidate();
                openTrainEdit(focsTrain);
            }else{
                focsTrain=null;
                this.invalidate();
            }

        }catch(Exception e){
            SDlog.log(e);
        }
   }
    /**
     * 列車編集画面を表示する
     */
    private void openTrainEdit(Train train){
        final int trainIndex=lineFile.getDiagram(diaIndex).getTrainIndex(train);
        activity.findViewById(R.id.bottomContents2).setVisibility(View.VISIBLE);
        TrainTimeEditFragment fragment=new TrainTimeEditFragment();
        Bundle args=new Bundle();
        args.putInt(AOdia.FILE_INDEX, lineIndex);
        args.putInt(AOdia.DIA_INDEX, diaIndex);
        args.putInt(AOdia.DIRECTION, train.direction);
        args.putInt(AOdia.TRAIN_INDEX, trainIndex);
        fragment.setArguments(args);
        fragment.setOnTrainChangeListener(new OnTrainChangeListener() {
            @Override
            public void trainChanged(Train train) {
                makeDiagramPath(train,train.direction,timeTable.getTrainIndex(train));
                invalidate();

            }

            @Override
            public void allTrainChange() {
                makeDiagramPath();
                invalidate();

            }

        });

        FragmentManager fragmentManager=((MainActivity)getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomContents,fragment);
        fragmentTransaction.commit();

    }
    /**
     * onMesureをオーバーライドすることで
     * このViewのサイズを設定する
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(View.MeasureSpec.getSize(heightMeasureSpec)>getYsize()){
            this.setMeasuredDimension(getXsize(), View.MeasureSpec.getSize(heightMeasureSpec));
        }else{
            this.setMeasuredDimension(getXsize(),getYsize());
        }
    }


    /**
     * このViewの実際の描画範囲のサイズ
     * @return
     */
    public int getmHeight(){
        return getYsize();
    }
    /**
     * このViewの実際の描画範囲のサイズ
     * @return
     */

    public int getmWidth(){
        return getXsize();
    }
    /**
     * このViewの実際の描画範囲のサイズ
     */

    protected int getXsize(){
        return (int)(1440*60* options.scaleX);
    }
    /**
     * このViewの実際の描画範囲のサイズ
     */
    protected int getYsize(){
        if(lineFile.getStationNum()==0)return 1000;
        return (int)(stationTime.get(lineFile.getStationNum()-1)* options.scaleY+(int)textPaint.getTextSize()+4);
    }
    /**
     * 破線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい
     */
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

    /**
     * 配列を用いてdrawDashLineを実行する
     */
    private void drawDashLines(Canvas canvas,float[] list,float dash1,float dash2,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDashLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],dash1,dash2,paint);
        }
    }

    /**
     * 点線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい
     */
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
    /**
     * 配列を用いてdrawDotLineを実行する
     */
    private void drawDotLines(Canvas canvas,float[] list,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDotLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],paint);
        }
    }

    /**
     * 一点鎖線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい
     */
    private void drawChainLine(Canvas canvas,float x1,float y1,float x2,float y2,float chain,float space,Paint paint){
        float x=(x2-x1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float y=(y2-y1)/(float)Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
        float loop=chain+2*space;
        for(int i=0;i<(x2-x1)/(loop)/x;i++){
            canvas.drawLine(x1+(loop)*x*i,y1+(loop)*y*i,x1+(loop*i+chain)*x,y1+(loop*i+chain)*y,paint);
            canvas.drawPoint(x1+(loop*i+chain+space)*x,y1+(loop*i+chain+space)*y,paint);
        }
    }

    /**
     * drawChainLineを配列を用いて描画する
     */
    private void drawChainLines(Canvas canvas,float[] list,float chain,float space,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawChainLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],chain,space,paint);
        }
    }

    private int getDiagramTime(int time){
        if(time<0){
            return -1;
        }
        if(time<lineFile.diagramStartTime){
            return time-lineFile.diagramStartTime+24*3600;
        }
        return time-lineFile.diagramStartTime;
    }

}
