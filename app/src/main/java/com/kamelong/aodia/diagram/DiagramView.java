package com.kamelong.aodia.diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.preference.PreferenceManager;

import com.kamelong.JPTI.TrainType;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaStation;
import com.kamelong.aodia.diadataOld.AOdiaTrain;
import com.kamelong.aodia.timeTable.KLView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/*
   This file is part of AOdia.

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
    private AOdiaStation station;
    private DiagramSetting setting;
    private int diaNum=0;
    private int diagramStartTime=0;

    /**
     * ダイヤグラム画面のスケールサイズ
     * １分当たりのピクセル数で定義しています
     */
    private float scaleX =15;
    private float scaleY =42;

    /**
     * ダイヤグラムに表示する列車のリスト
     * DiaFile内で順番が変更されることを考慮し、配列に取得しています。
     */
    private ArrayList<AOdiaTrain>[]trainList=new ArrayList[2];
    /**
     * ダイヤグラム描画に用いるパス
     * diagramPath[0]は下りダイヤ
     * diagramPath[1]は上りダイヤ
     * diagrampath[x].get(i)には、１列車のダイヤグラム描画パスがArrayListで入っている
     * このArrayListには描画する線の数*4個のIntegerが入っており、
     * １つの線当たり(startX,startY,endX,endY)の４つの値が入っている
     *
     */
    private ArrayList<ArrayList<Integer>>[] diagramPath=new ArrayList[2];
    /**
     * 停車マークを描画する点
     * １つのマーク当たり(xPoint,yPoint)の２つの値が追加される
     */
    private ArrayList<Integer>[] stopMark=new ArrayList[2];


    /**
     * 強調表示されている列車
     * これらの列車を太線で表示する
     *
     * ダイヤグラム画面内を長押しすることで近くにあるダイヤ線の列車が強調表示に切り替わります
     */
    public ArrayList<AOdiaTrain> focusTrain=new ArrayList<>();
    /**
     * これがtrueの時は実線表示のみとなり、点線などは使えなくなる
     */
    private boolean onlySolid=false;
    private float defaultLineSize=1;
    private final int yshift=30;
    private ArrayList<Integer>stationTime=new ArrayList<>();
    private DiagramView(Context context){
        super(context);
    }

    /**
     *画面密度から線の太さを決める
     */
    private void getDensity(){
        defaultLineSize=getResources().getDisplayMetrics().densityDpi / 160f;
    }

    /**
     * diagramPathを作成する
     */
    private void makeDiagramPath(){

    }
    DiagramView(Context context,DiagramSetting s, AOdiaDiaFile dia,int num){
        this(context);
        try {
            setting=s;
            diaFile=dia;
            stationTime=station.getStationTime();
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
     */
    private float[] toArr(List<Integer> list){
        // List<Integer> -> int[]
        int l = list.size();
        float[] arr = new float[l];
        Iterator<Integer> iter = list.iterator();
        for (int i=0;i<l;i++){
            arr[i] = iter.next()*scaleX/60;
            i++;
            arr[i] = iter.next()*scaleY/60+yshift;
        }
        return arr;
    }

    /**
     * 駅軸を描画する
     *
     */
    private void drawStationLine(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(200, 200, 200));


        for (int i = 0; i < station.getStationNum(); i++) {
            if (station.getRouteStation(i).isBigStation()) {
                paint.setStrokeWidth(defaultLineSize);
            } else {
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,stationTime.get(i) * scaleY / 60+yshift, 60*24* scaleX,stationTime.get(i) * scaleY / 60+yshift, paint);
        }
    }

    /**
     * 列車を描画する
     */
    private void drawTrain(Canvas canvas){
        try {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (int direct = 0; direct < 2; direct++) {
                if ((direct == 0 && setting.downFrag) || (direct == 1 && setting.upFrag)) {
                    for (int i = 0; i < trainList[direct].size(); i++) {
                        //ダイヤ線色を指定
                        paint.setColor(trainList[direct].get(i).getTrainType().getDiaColor().getAndroidColor());
                        if (focusTrain.size()>0) {
                            //強調表示の列車があるときは半透明化
                            paint.setAlpha(100);
                        }
                        //線の太さを指定
                        if (trainList[direct].get(i).getTrainType().getDiaBold()) {
                            paint.setStrokeWidth(defaultLineSize * 2f);
                        } else {
                            paint.setStrokeWidth(defaultLineSize);
                        }

                        //強調ダイヤ線時刻描画
                        if (focusTrain.contains(trainList[direct].get(i))) {
                            AOdiaTrain train=trainList[direct].get(i);
                            //強調表示の線は半透明ではない
                            paint.setAlpha(255);
                            //線の太さを太くする
                            paint.setStrokeWidth(defaultLineSize * 3f);
                            //文字色もダイヤ色に合わせて変更
                            Companion.getTextPaint().setColor(trainList[direct].get(i).getTrainType().getDiaColor().getAndroidColor());
                            Companion.getTextPaint().setAlpha(255);
                            for (int j = 0; j < station.getStationNum(); j++) {
                                if (train.getTime(j)!=null&&train.getTime(j).getArrivalTime()>=0) {
                                    canvas.drawText(String.format(Locale.JAPAN,"%02d", (train.getTime(j).getArrivalTime() / 60) % 60), convertTime(train.getTime(j).getArrivalTime()) * scaleX / 60, stationTime.get(j) * scaleY / 60 + Companion.getTextPaint().getTextSize() * (-0.2f + direct * 1.2f)+yshift, Companion.getTextPaint());
                                }
                                if (train.getTime(j)!=null&&train.getTime(j).getDepartureTime()>=0) {
                                    canvas.drawText(String.format(Locale.JAPAN,"%02d", (train.getTime(j).getDepartureTime() / 60) % 60), convertTime(train.getTime(j).getDepartureTime()) * scaleX / 60 - Companion.getTextPaint().getTextSize(), stationTime.get(j) * scaleY / 60 + Companion.getTextPaint().getTextSize() * (1 - direct * 1.2f)+yshift, Companion.getTextPaint());

                                }
                            }
                        }
                        //指定線種に合わせてダイヤ線を描画
                        if(onlySolid){
                            canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                        }else {
                            switch (trainList[direct].get(i).getTrainType().getDiaStyle()) {
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

                        Paint paint2=new Paint();
                        paint2.setAntiAlias(true);
                        paint2.setStyle(Paint.Style.STROKE);
                        paint2.setStrokeWidth(defaultLineSize);
                        if(trainList[direct].get(i).getOperation()!=null){
                            if(trainList[direct].get(i).getOperation().getNext(trainList[direct].get(i))!=null&&trainList[direct].get(i).getOperation().getNext(trainList[direct].get(i)).isUsed()){
                                AOdiaTrain train1=trainList[direct].get(i);
                                AOdiaTrain train2=train1.getOperation().getNext(trainList[direct].get(i));
                                if(train1.getEndStation()==train2.getStartStation()){
                                    if(train1.getDirect()==0&&train2.getDirect()==1){
                                        canvas.drawArc(new RectF(
                                                        convertTime(train1.getTime(train1.getEndStation()).getArrivalTime())*scaleX/60,
                                                        stationTime.get(train1.getEndStation())*scaleY/60-25+yshift,
                                                        convertTime(train2.getTime(train1.getEndStation()).getDepartureTime())*scaleX/60,
                                                        stationTime.get(train1.getEndStation())*scaleY/60+25+yshift)
                                                , 0, 180, false, paint2);
                                    }
                                    if(train1.getDirect()==1&&train2.getDirect()==0){
                                        canvas.drawArc(new RectF(
                                                        convertTime(train1.getTime(train1.getEndStation()).getArrivalTime())*scaleX/60,
                                                        stationTime.get(train1.getEndStation())*scaleY/60-25+yshift,
                                                        convertTime(train2.getTime(train1.getEndStation()).getDepartureTime())*scaleX/60,
                                                        stationTime.get(train1.getEndStation())*scaleY/60+25+yshift)
                                                , 180, 180, false, paint2);
                                    }
                                }

                            }
                        }
                    }
                    if (setting.stopFrag) {
                        //停車駅の表示も行う

                        paint.setColor(Color.BLACK);
                        paint.setStrokeWidth(defaultLineSize);
                        for (int i = 0; i < stopMark[direct].size() / 2; i++) {
                            canvas.drawCircle(stopMark[direct].get(2 * i) * scaleX / 60, stopMark[direct].get(2 * i + 1) * scaleY / 60+yshift, defaultLineSize * 2f, paint);
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
        final int axisHeight=stationTime.get(station.getStationNum() - 1);


        //1時間ごとの目盛
        //以下太実線
        if(setting.veriticalAxis()==7){
            for (int i = 0; i < 48; i++) {
                canvas.drawLine(scaleX * (30 + 30 * i), yshift, scaleX * (30 + 30 * i),axisHeight * scaleY / 60+yshift, paint);
            }

        }else {
            for (int i = 0; i < 24; i++) {
                canvas.drawLine(scaleX * (60 + 60 * i), yshift, scaleX * (60 + 60 * i),axisHeight* scaleY / 60+yshift, paint);
            }
        }
        paint.setStrokeWidth(defaultLineSize*0.5f);
        dotPaint.setStrokeWidth(defaultLineSize*0.5f);
        switch(setting.veriticalAxis()){
            case 1:
                for (int i = 0; i < 24; i++) {
                    //30分ごとの目盛
                    canvas.drawLine(scaleX * (30 + 60 * i), yshift, scaleX * (30 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                }
                break;
            case 2:
                //20分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (20 + 60 * i), yshift, scaleX * (20 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), yshift, scaleX * (40 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                }
                break;
            case 3:
                //15分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (15 + 60 * i), yshift, scaleX * (15 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), yshift, scaleX * (30 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (45 + 60 * i), yshift, scaleX * (45 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                }
                break;
            case 4:
                //10分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (30 + 60 * i), yshift, scaleX * (30 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                }
                for (int i = 0; i < 48; i++) {
                    if(stationTime.get(station.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (10 + 30 * i), yshift, scaleX * (10 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60, dot2Paint);
                        canvas.drawLine(scaleX * (20 + 30 * i), yshift, scaleX * (20 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (10 + 30 * i), yshift);
                        dotLine.lineTo(scaleX * (10 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift);
                        dotLine.moveTo(scaleX * (20 + 30 * i), yshift);
                        dotLine.lineTo(scaleX * (20 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 5:
                //5分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), yshift, scaleX * (10 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), yshift, scaleX * (20 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), yshift, scaleX * (30 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), yshift, scaleX * (40 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), yshift, scaleX * (50 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    if(stationTime.get(station.getStationNum() - 1) * scaleY / 60>2048) {
                        canvas.drawLine(scaleX * (5 + 10 * i), yshift, scaleX * (5 + 10 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(scaleX * (5 + 10 * i), yshift);
                        dotLine.lineTo(scaleX * (5 + 10 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 6:
                //2分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(scaleX * (10 + 60 * i), yshift, scaleX * (10 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (20 + 60 * i), yshift, scaleX * (20 + 60 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (30 + 60 * i), yshift, scaleX * (30 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (40 + 60 * i), yshift, scaleX * (40 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (50 + 60 * i), yshift, scaleX * (50 + 60 * i),axisHeight * scaleY / 60+yshift, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    for(int j=1;j<5;j++){
                        if(stationTime.get(station.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * (2 * j + 10 * i), yshift, scaleX * (2 * j + 10 * i),axisHeight * scaleY / 60+yshift, dot2Paint);
                        }else {
                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * (2 * j + 10 * i), yshift);
                            dotLine.lineTo(scaleX * (2 * j + 10 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
            case 7:
                //1分ごとの目盛

                for (int i = 0; i < 24*2; i++) {
                    canvas.drawLine(scaleX * (5 + 30 * i), yshift, scaleX * (5 + 30 * i),axisHeight * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (10 + 30 * i), yshift, scaleX * (10 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (15 + 30 * i), yshift, scaleX * (15 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (20 + 30 * i), yshift, scaleX * (20 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                    canvas.drawLine(scaleX * (25 + 30 * i), yshift, scaleX * (25 + 30 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, paint);
                }
                for (int i = 0; i < 24*12; i++) {
                    for(int j=1;j<5;j++) {
                        if(stationTime.get(station.getStationNum() - 1) * scaleY / 60>2048) {
                            canvas.drawLine(scaleX * ( j + 5 * i), yshift, scaleX * (j + 5 * i),stationTime.get(station.getStationNum() - 1) * scaleY / 60+yshift, dot2Paint);
                        }else{

                            Path dotLine = new Path();
                            dotLine.moveTo(scaleX * ( j + 5 * i), yshift);
                            dotLine.lineTo(scaleX * ( j + 5 * i),axisHeight * scaleY / 60+yshift);
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
            if((direct==0&&setting.downFrag)||(direct==1&&setting.upFrag)) {
                for(int i=0;i<trainList[direct].size();i++){
                    int pathNum=-1;
                    for(int j=0;j+3<diagramPath[direct].get(i).size();j=j+2){
                        if(!diagramPath[direct].get(i).get(j+1).equals(diagramPath[direct].get(i).get(j+3))){
                            pathNum=j;
                            break;
                        }
                    }
                    if(pathNum<0)continue;
                    //列車番号を表示する部分のダイヤ線の座標を取得
                    int x1=(int)(diagramPath[direct].get(i).get(pathNum)*scaleX/60);
                    int y1=(int)(diagramPath[direct].get(i).get(pathNum+1)*scaleY/60)+yshift;
                    int x2=(int)(diagramPath[direct].get(i).get(pathNum+2)*scaleX/60);
                    int y2=(int)(diagramPath[direct].get(i).get(pathNum+3)*scaleY/60)+yshift;
                    canvas.save();
                    double rad=Math.atan2((double)(y2-y1),(double)(x2-x1));
                    //canvasを回転して
                    canvas.rotate((float) Math.toDegrees(rad),x1,y1);
                    //列車番号を描画
                    Companion.getTextPaint().setColor(trainList[direct].get(i).getTrainType().getDiaColor().getAndroidColor());
                    if(focusTrain.size()==0||focusTrain.contains(trainList[direct].get(i))){
                        Companion.getTextPaint().setAlpha(255);
                    }else{
                        Companion.getTextPaint().setAlpha(100);
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
                        canvas.drawText(text, x1 + (int) (Companion.getTextPaint().getTextSize() / Math.tan(rad)), y1 - Companion.getTextPaint().getTextSize() / 6, Companion.getTextPaint());
                    }else{
                        canvas.drawText(text, x1+(int) (Companion.getTextPaint().getTextSize()), y1 - Companion.getTextPaint().getTextSize() / 6, Companion.getTextPaint());
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
        nowTime= nowTime-diagramStartTime+9*60*60;
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
        canvas.drawLine(nowTime*scaleX/60,0,nowTime*scaleX/60, stationTime.get(station.getStationNum() - 1)* scaleY / 60+yshift,paint);
    }
    /**
     * フォーカスする列車を選択する。
     * ダイヤグラム画面内を長押しすることで実行する。
     * @see #focusTrain フォーカスする列車
     *
     * これらのパラメーターは、DiagramViewの左上を基準とした座標
     */
    public void showDetail(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x * 60 / scaleX);
            y = (int)(y * 60 / scaleY)-yshift;
            if (y >stationTime.get(stationTime.size() - 1)) {
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
                focusTrain=new ArrayList<>();
                this.invalidate();
                return;
            }
            if(focusTrain.contains(trainList[minTrainDirect].get(minTrainNum))){
                focusTrain=new ArrayList<>();
            }else {

                if(trainList[minTrainDirect].get(minTrainNum).getOperation()==null){
                    focusTrain=new ArrayList<>();
                    focusTrain.add(trainList[minTrainDirect].get(minTrainNum));

                }else{
                    focusTrain = trainList[minTrainDirect].get(minTrainNum).getOperation().getTrain();

                }

            }
            this.invalidate();

        }catch(Exception e){
            SdLog.log(e);
        }

    }

    /**
     * DiagramViewのスケールを変更する
     * 同時にStationView,TimeViewのスケールも変更すること
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
     */
    public int getmHeight(){
        return getYsize();
    }
    /**
     * このViewの実際の描画範囲のサイズ
     */

    public int getmWidth(){
        return getXsize();
    }
    /**
     * このViewの実際の描画範囲のサイズ
     */

    public  int getXsize(){
        return (int)(1440* scaleX);
    }
    /**
     * このViewの実際の描画範囲のサイズ
     */

    public int getYsize(){
        return (int)(stationTime.get(station.getStationNum()-1)* scaleY /60+(int) Companion.getTextPaint().getTextSize()+4+yshift*2);
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
    private int convertTime(int time){
        if(time<diagramStartTime){
            return time+24*60*60-diagramStartTime;
        }
        return time-diagramStartTime;

    }
    /**
     * フォーカスする列車を選択する。
     * ダイヤグラム画面内を長押しすることで実行する。
     * @see #focusTrain フォーカスする列車
     *
     * これらのパラメーターは、DiagramViewの左上を基準とした座標
     */
    public void setTrain(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x * 60 / scaleX);
            y = (int)(y * 60 / scaleY)-yshift;
            if (y >stationTime.get(stationTime.size() - 1)) {
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

            if(trainList[minTrainDirect].get(minTrainNum)==null){
                return;
            }
            if(focusTrain.contains(trainList[minTrainDirect].get(minTrainNum))){
                focusTrain.remove(trainList[minTrainDirect].get(minTrainNum));
            }else {
                if(trainList[minTrainDirect].get(minTrainNum).getOperation()==null){
                    focusTrain.add(trainList[minTrainDirect].get(minTrainNum));
                }else{
                    SdLog.toast("この列車は既に運用が登録されています");
                }
            }
            this.invalidate();

        }catch(Exception e){
            SdLog.log(e);
        }

    }



}
