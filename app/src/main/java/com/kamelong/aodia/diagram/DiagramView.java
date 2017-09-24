package com.kamelong.aodia.diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.preference.PreferenceManager;

import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadata.AOdiaTrain;
import com.kamelong.aodia.diadata.AOdiaTrainType;
import com.kamelong.aodia.timeTable.KLView;

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

/**
 * @author KameLong
 * ダイヤグラム画面において、ダイヤグラムを描画するView
 * ダイヤグラム背景の線、ダイヤグラムの列車線、列車番号なども描画する
 *
 */
public class DiagramView extends KLView {
    private AOdiaDiaFile diaFile;
    private DiagramSetting setting;
    private int diaNum=0;
    /**
     * ダイヤグラム画面のスケールサイズ
     * １分当たりのピクセル数で定義しています
     */
    public  float scaleX =15;
    public  float scaleY =42;

    /**
     * ダイヤグラムに表示する列車のリスト
     * DiaFile内で順番が変更されることを考慮し、配列に取得しています。
     */
    ArrayList<AOdiaTrain>[]trainList=new ArrayList[2];
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
    ArrayList<Integer>[] stopMark=new ArrayList[2];


    /**
     * 強調表示されている列車
     * この列車を太線で表示する
     *
     * ダイヤグラム画面内を長押しすることで近くにあるダイヤ線の列車が強調表示に切り替わります
     */
    private AOdiaTrain focsTrain=null;
    /**
     * これがtrueの時は実線表示のみとなり、点線などは使えなくなる
     */
    private boolean onlySolid=false;
    private float defaultLineSize=1;
    DiagramView(Context context){
        super(context);
    }

    /**
     *画面密度から線の太さを決める
     */
    private void getDensity(){
        float density = getResources().getDisplayMetrics().densityDpi / 160f;
        defaultLineSize=density;
    }

    /**
     * diagramPathを作成する
     */
    private void makeDiagramPath(){
        //makeDiagramData
        diagramPath[0]=new  ArrayList<ArrayList<Integer>>();
        diagramPath[1]=new  ArrayList<ArrayList<Integer>>();
        trainList[0]=new ArrayList<AOdiaTrain>();
        trainList[1]=new ArrayList<AOdiaTrain>();
        stopMark[0]=new ArrayList<Integer>();
        stopMark[1]=new ArrayList<Integer>();

        for(int direct=0;direct<2;direct++){
            for (int i = 0; i < this.diaFile.getTrainNum(diaNum, direct); i++) {
                AOdiaTrain train= diaFile.getTrain(diaNum, direct, i);
                //この列車のdiagramPath
                ArrayList<Integer> trainPath=new ArrayList<Integer>();
                //始発部分のパスを追加
                trainPath.add((train.getDepartureTime(train.getStartStation(direct)) - diaFile.getDiagramStartTime()));
                trainPath.add(this.diaFile.getStationTime(train.getStartStation(direct)));
                boolean drawable=true;
                //駅ループ
                for (int j = train.getStartStation(direct) + (1-direct*2);(1-direct*2)* j <(1-direct*2)* (train.getEndStation(direct)+ (1-direct*2)); j=j+(1-direct*2)) {
                    if(drawable&&train.getStopType(j)== AOdiaTrain.STOP_TYPE_NOSERVICE){
                        //描画打ち切り
                        drawable=false;
                        trainPath.add(trainPath.get(trainPath.size()-2));
                        trainPath.add(trainPath.get(trainPath.size()-2));
                    }
                    if(drawable&&train.getStopType(j)== AOdiaTrain.STOP_TYPE_NOVIA){
                        //未処理
                        drawable=false;
                        trainPath.add(trainPath.get(trainPath.size()-2));
                        trainPath.add(trainPath.get(trainPath.size()-2));
                    }

                    if(train.timeExist(j)) {
                        if (train.departExist(j - (1 - 2 * direct))) {
                            //一つ前の駅にも発時刻が存在する場合　最小所要時間を考慮
                            if (!train.arriveExist(j)&&
                                    Math.abs(diaFile.getStationTime(j) - diaFile.getStationTime(j - (1 - 2 * direct))) + 60 < train.getArriveTime(j) - train.getDepartureTime(j - (1 - 2 * direct))) {
                                //最小所要時間以上かかっているので最小所要時間を適用
                                trainPath.add(train.getDepartureTime(j - (1 - 2 * direct)) + Math.abs(this.diaFile.getStationTime().get(j) - this.diaFile.getStationTime().get(j - (1 - 2 * direct))) + 30 - diaFile.getDiagramStartTime());
                            } else {
                                //最小所要時間を採用しないとき
                                trainPath.add(train.getArriveTime(j) - diaFile.getDiagramStartTime());
                            }
                        } else {
                            //一つ前が通過駅の時など
                            trainPath.add(train.getArriveTime(j) - diaFile.getDiagramStartTime());
                        }
                        trainPath.add(this.diaFile.getStationTime().get(j));
                        if (drawable) {
                            //現段階で線描画途中の時は、終端点を追加
                            trainPath.add(trainPath.get(trainPath.size() - 2));
                            trainPath.add(trainPath.get(trainPath.size() - 2));
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
                        if(drawable&&train.getStopType(j)== AOdiaTrain.STOP_TYPE_PASS&&train.getStopType(j + (1 - 2 * direct))== AOdiaTrain.STOP_TYPE_NOVIA&&!train.timeExist(j)){

                            if(train.getPredictionTime(j)<0){
                            }else{
                                //この次の駅から経由なしになるとき
                                trainPath.add(train.getPredictionTime(j)-diaFile.getDiagramStartTime());
                                trainPath.add(this.diaFile.getStationTime().get(j));
                                drawable=false;
                            }
                        }
                        if(!drawable&&train.getStopType(j )== AOdiaTrain.STOP_TYPE_PASS&&train.getStopType(j - (1 - 2 * direct))== AOdiaTrain.STOP_TYPE_NOVIA&&!train.timeExist(j)){
                            if(train.getPredictionTime(j)<0){

                            }else{
                                //この前の駅めで経由なしのとき
                                trainPath.add(train.getPredictionTime(j)-diaFile.getDiagramStartTime());
                                trainPath.add(this.diaFile.getStationTime().get(j));
                                drawable=true;
                            }
                        }
                    }
                    if(train.departExist(j)){
                        //停車時間を描画する
                        trainPath.add((train.getDepartureTime(j)- diaFile.getDiagramStartTime()));
                        trainPath.add( this.diaFile.getStationTime().get(j) );
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
                                    diaFile.getTrainType(train.getType()).getShowStop() &&
                                    j!=train.getStartStation(0)&&
                                    j!=train.getEndStation(0)&&
                                    trainPath.get(trainPath.size() - 4).equals(trainPath.get(trainPath.size() - 6))) {
                                //始発終着駅を除き　停車マークを用意する
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
    }
    DiagramView(Context context,DiagramSetting s, AOdiaDiaFile dia,int num){
        this(context);
        try {
            setting=s;
            diaFile=dia;
            diaNum=num;

            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
            onlySolid=spf.getBoolean("onlySolid",false);


            getDensity();
            makeDiagramPath();



        } catch(Exception e){
            SdLog.log(e);
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
            arr[i] = iter.next()*scaleX/60;
            i++;
            arr[i] = iter.next()*scaleY/60;
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


        for (int i = 0; i < diaFile.getStationNum(); i++) {
            if (diaFile.getStation(i).getBigStation()) {
                paint.setStrokeWidth(defaultLineSize);
            } else {
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,diaFile.getStationTime().get(i) * scaleY / 60, 60*24* scaleX,diaFile.getStationTime().get(i) * scaleY / 60, paint);
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
                if ((direct == 0 && setting.downFrag) || (direct == 1 && setting.upFrag)) {
                    for (int i = 0; i < trainList[direct].size(); i++) {
                        //ダイヤ線色を指定
                        paint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getAOdiaDiaColor());
                        if (focsTrain != null) {
                            //強調表示の列車があるときは半透明化
                            paint.setAlpha(100);
                        }
                        //線の太さを指定
                        if (diaFile.getTrainType(trainList[direct].get(i).getType()).getLineBold()) {
                            paint.setStrokeWidth(defaultLineSize * 2f);
                        } else {
                            paint.setStrokeWidth(defaultLineSize);
                        }

                        //強調ダイヤ線時刻描画
                        if (trainList[direct].get(i) == focsTrain) {
                            //強調表示の線は半透明ではない
                            paint.setAlpha(255);
                            //線の太さを太くする
                            paint.setStrokeWidth(defaultLineSize * 3f);
                            //文字色もダイヤ色に合わせて変更
                            textPaint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getAOdiaDiaColor());
                            textPaint.setAlpha(255);
                            for (int j = 0; j < diaFile.getStationNum(); j++) {
                                if (focsTrain.arriveExist(j)) {
                                    canvas.drawText(String.format("%02d", (focsTrain.getArriveTime(j) / 60) % 60), (focsTrain.getArriveTime(j) - 3 * 3600) * scaleX / 60,diaFile.getStationTime().get(j) * scaleY / 60 + textPaint.getTextSize() *(-0.2f+direct*1.2f), textPaint);
                                }
                                if (focsTrain.departExist(j)) {
                                    canvas.drawText(String.format("%02d", (focsTrain.getDepartureTime(j) / 60) % 60), (focsTrain.getDepartureTime(j) - 3 * 3600) * scaleX / 60 - textPaint.getTextSize() ,diaFile.getStationTime().get(j) * scaleY / 60 + textPaint.getTextSize() *(1-direct*1.2f), textPaint);

                                }
                            }
                        }
                        //指定線種に合わせてダイヤ線を描画
                        if(onlySolid){
                            canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                        }else {
                            switch ((diaFile.getTrainType(trainList[direct].get(i).getType()).getLineStyle())) {
                                case AOdiaTrainType.LINESTYLE_NORMAL:
                                    canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                                    break;
                                case AOdiaTrainType.LINESTYLE_DASH:
                                    drawDashLines(canvas, toArr(diagramPath[direct].get(i)), 10, 10, paint);
                                    break;
                                case AOdiaTrainType.LINESTYLE_DOT:
                                    drawDotLines(canvas, toArr(diagramPath[direct].get(i)), paint);
                                    break;
                                case AOdiaTrainType.LINESTYLE_CHAIN:
                                    drawChainLines(canvas, toArr(diagramPath[direct].get(i)), 10, 10, paint);
                                    break;
                            }
                        }
                    }
                    if (setting.stopFrag) {
                        //停車駅の表示も行う

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
            SdLog.log(e);
        }
    }

    /**
     * 時間軸を描画する
     * DiagramSettingのverticalAxicsの値によってダイヤ線のスタイルが異なる
     * @see DiagramSetting#verticalAxis
     * @param canvas
     */
    private void drawAxis(Canvas canvas){
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
        final int axisHeight=diaFile.getStationTime(diaFile.getStationNum() - 1);


        //1時間ごとの目盛
        //以下太実線
        if(setting.veriticalAxis()==7){
            for (int i = 0; i < 48; i++) {
                canvas.drawLine(scaleX * (30 + 30 * i), 0, scaleX * (30 + 30 * i),axisHeight * scaleY / 60, paint);
            }

        }else {
            for (int i = 0; i < 24; i++) {
                canvas.drawLine(scaleX * (60 + 60 * i), 0, scaleX * (60 + 60 * i),axisHeight* scaleY / 60, paint);
            }
        }
        paint.setStrokeWidth(defaultLineSize*0.5f);
        dotPaint.setStrokeWidth(defaultLineSize*0.5f);
        switch(setting.veriticalAxis()){
            case 1:
                for (int i = 0; i < 24; i++) {
                    //30分ごとの目盛
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i),axisHeight * scaleY / 60, paint);
                }
                break;
            case 2:
                //20分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i),axisHeight * scaleY / 60, paint);
                }
                break;
            case 3:
                //15分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (15 + 60 * i), 0, scaleX * (15 + 60 * i),axisHeight * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i),axisHeight * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (45 + 60 * i), 0, scaleX * (45 + 60 * i),axisHeight * scaleY / 60, paint);
                }
                break;
            case 4:
                //10分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i),axisHeight * scaleY / 60, paint);
                }
                for (int i = 0; i < 48; i++) {
                    if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (10 + 30 * i), 0, scaleX * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        canvas.drawLine(scaleX * (20 + 30 * i), 0, scaleX * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (10 + 30 * i), 0);
                        dotLine.lineTo(scaleX * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60);
                        dotLine.moveTo(scaleX * (20 + 30 * i), 0);
                        dotLine.lineTo(scaleX * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 5:
                //5分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), 0, scaleX * (10 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), 0, scaleX * (50 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (5 + 10 * i), 0, scaleX * (5 + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (5 + 10 * i), 0);
                        dotLine.lineTo(scaleX * (5 + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 6:
                //2分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), 0, scaleX * (10 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), 0, scaleX * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), 0, scaleX * (30 + 60 * i),axisHeight * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), 0, scaleX * (40 + 60 * i),axisHeight * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), 0, scaleX * (50 + 60 * i),axisHeight * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    for(int j=1;j<5;j++){
                        if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * (2 * j + 10 * i), 0, scaleX * (2 * j + 10 * i),axisHeight * scaleY / 60, dot2Paint);
                        }else {
                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * (2 * j + 10 * i), 0);
                            dotLine.lineTo(scaleX * (2 * j + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
            case 7:
                //1分ごとの目盛

                for (int i = 0; i < 24*2; i++) {
                    canvas.drawLine(scaleX * (5 + 30 * i), 0, scaleX * (5 + 30 * i),axisHeight * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (10 + 30 * i), 0, scaleX * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (15 + 30 * i), 0, scaleX * (15 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (20 + 30 * i), 0, scaleX * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                    canvas.drawLine(scaleX * (25 + 30 * i), 0, scaleX * (25 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, paint);
                }
                for (int i = 0; i < 24*12; i++) {
                    for(int j=1;j<5;j++) {
                        if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * (1 * j + 5 * i), 0, scaleX * (1 * j + 5 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        }else{

                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * (1 * j + 5 * i), 0);
                            dotLine.lineTo(scaleX * (1 * j + 5 * i),axisHeight * scaleY / 60);
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
     * @param canvas
     */
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
                    //列車番号を表示する部分のダイヤ線の座標を取得
                    int x1=(int)(diagramPath[direct].get(i).get(pathNum)*scaleX/60);
                    int y1=(int)(diagramPath[direct].get(i).get(pathNum+1)*scaleY/60);
                    int x2=(int)(diagramPath[direct].get(i).get(pathNum+2)*scaleX/60);
                    int y2=(int)(diagramPath[direct].get(i).get(pathNum+3)*scaleY/60);
                    canvas.save();
                    double rad=Math.atan2((double)(y2-y1),(double)(x2-x1));
                    //canvasを回転して
                    canvas.rotate((float) Math.toDegrees(rad),x1,y1);
                    //列車番号を描画
                    textPaint.setColor(diaFile.getTrainType(trainList[direct].get(i).getType()).getAOdiaDiaColor());
                    if(focsTrain==null||focsTrain==trainList[direct].get(i)){
                        textPaint.setAlpha(255);
                    }else{
                        textPaint.setAlpha(100);
                    }
                    //textに表示したい文字列を代入
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
        //これによりnowTimeに現在時刻が入った（秒単位）
        Paint paint=new Paint();
        paint.setColor(Color.argb(255,255,0,0));
        paint.setStrokeWidth(defaultLineSize*1.0f);
        paint.setAntiAlias(true);
        canvas.drawLine(nowTime*scaleX/60,0,nowTime*scaleX/60, diaFile.getStationTime().get(diaFile.getStationNum() - 1)* scaleY / 60,paint);
    }
    /**
     * フォーカスする列車を選択する。
     * ダイヤグラム画面内を長押しすることで実行する。
     * @see #focsTrain フォーカスする列車
     *
     * これらのパラメーターは、DiagramViewの左上を基準とした座標
     * @param x
     * @param y
     */
    public void showDetail(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x * 60 / scaleX);
            y = (int)(y * 60 / scaleY);
            if (y >diaFile.getStationTime().get(diaFile.getStationTime().size() - 1)) {
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

    /**
     * DiagramViewのスケールを変更する
     * 同時にStationView,TimeViewのスケールも変更すること
     * @param x
     * @param y
     *
     * @see StationView#setScale(float, float)
     * @see TimeView#setScale(float, float)
     */
    public void setScale(float x,float y){
        scaleX =x;
        scaleY =y;
        this.layout(0,0, getXsize(),getYsize());
    }

    /**
     * onMesureをオーバーライドすることで
     * このViewのサイズを設定する
     * @see KLView#onMeasure(int, int)
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(MeasureSpec.getSize(heightMeasureSpec)>getYsize()){
            this.setMeasuredDimension(getXsize(),MeasureSpec.getSize(heightMeasureSpec));
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
     * @return
     */

    protected int getXsize(){
        return (int)(1440* scaleX);
    }
    /**
     * このViewの実際の描画範囲のサイズ
     * @return
     */

    protected int getYsize(){
        return (int)(diaFile.getStationTime().get(diaFile.getStationNum()-1)* scaleY /60+(int)textPaint.getTextSize()+4);
    }


    /**
     * 破線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい
     * @param canvas
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param dash1
     * @param dash2
     * @param paint
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
     * @param canvas
     * @param list
     * @param dash1
     * @param dash2
     * @param paint
     *
     * @see #drawDashLine(Canvas, float, float, float, float, float, float, Paint)
     */
    private void drawDashLines(Canvas canvas,float[] list,float dash1,float dash2,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDashLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],dash1,dash2,paint);
        }
    }

    /**
     * 点線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい

     * @param canvas
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param paint
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
     * @param canvas
     * @param list
     * @param paint
     *
     * @see #drawDotLine(Canvas, float, float, float, float, Paint)
     */
    private void drawDotLines(Canvas canvas,float[] list,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawDotLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],paint);
        }
    }

    /**
     * 一点鎖線を描画する
     * canvas.drawLineと同様の使い方ができると考えてよい
     * @param canvas
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param chain
     * @param space
     * @param paint
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
     * @param canvas
     * @param list
     * @param chain
     * @param space
     * @param paint
     *
     * @see #drawChainLine(Canvas, float, float, float, float, float, float, Paint)
     */
    private void drawChainLines(Canvas canvas,float[] list,float chain,float space,Paint paint){
        for(int i=0;i<list.length/4;i++){
            drawChainLine(canvas,list[4*i+0],list[4*i+1],list[4*i+2],list[4*i+3],chain,space,paint);
        }
    }
}