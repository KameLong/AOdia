package com.kamelong.aodia.Diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.preference.PreferenceManager;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Train;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.AOdiaDefaultView;
import com.kamelong.aodia.SDlog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiagramView extends AOdiaDefaultView{
    private DiagramSetting setting;
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
    private ArrayList<Train> focsTrain=new ArrayList<>();
    private float defaultLineSize=1;

    public DiaFile diaFile;
    public Diagram timeTable;
    public int diaNumber=0;
    public ArrayList<Integer>stationTime;
    private boolean onlySolid=false;
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
    DiagramView(Context context, DiagramSetting s, DiaFile diafile, int diaNum){
        this(context);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(context);
        onlySolid=spf.getBoolean("onlySolid",false);

        try {
            setting=s;
            this.diaFile=diafile;
            diaNumber=diaNum;
            timeTable=diafile.diagram.get(diaNum);
            stationTime=diafile.getStationTime();
            getDensity();
            makeDiagramPath();



        } catch(Exception e){
            SDlog.log(e);
        }

    }

    /**
     * diagramPathを作成する
     */
    private void makeDiagramPath(){
        //makeDiagramData
        diagramPath[0]=new  ArrayList<ArrayList<Integer>>();
        diagramPath[1]=new  ArrayList<ArrayList<Integer>>();
        stopMark[0]=new ArrayList<Integer>();
        stopMark[1]=new ArrayList<Integer>();

        for(int direct=0;direct<2;direct++){
            for (int i = 0; i < timeTable.trains[direct].size(); i++) {
                Train train= timeTable.trains[direct].get(i);
                //この列車のdiagramPath
                ArrayList<Integer> trainPath=new ArrayList<Integer>();
                if(train.startStation()==-1){
                    diagramPath[direct].add(trainPath);
                    continue;
                }
                //始発部分のパスを追加
                trainPath.add((train.getDepartureTime(train.startStation()) - diaFile.diagramStartTime));
                trainPath.add(stationTime.get(train.startStation()));
                boolean drawable=true;
                //駅ループ
                for (int j = train.startStation() + (1-direct*2);(1-direct*2)* j <(1-direct*2)* (train.endStation()+ (1-direct*2)); j=j+(1-direct*2)) {
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
                        if (train.departExist(j - (1 - 2 * direct))) {
                            //一つ前の駅にも発時刻が存在する場合　最小所要時間を考慮
                            if (!train.arriveExist(j)&&
                                    Math.abs(stationTime.get(j) - stationTime.get(j - (1 - 2 * direct))) + 60 < train.getArrivalTime(j) - train.getDepartureTime(j - (1 - 2 * direct))) {
                                //最小所要時間以上かかっているので最小所要時間を適用
                                trainPath.add(train.getADTime(j - (1 - 2 * direct)) + Math.abs(stationTime.get(j) - stationTime.get(j - (1 - 2 * direct))) + 30 - diaFile.diagramStartTime);
                            } else {
                                //最小所要時間を採用しないとき
                                trainPath.add(train.getADTime(j) - diaFile.diagramStartTime);
                            }
                        } else {
                            //一つ前が通過駅の時など
                            trainPath.add(train.getADTime(j) - diaFile.diagramStartTime);
                        }
                        trainPath.add(stationTime.get(j));
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
                        if(drawable&&train.getStopType(j)==2&&train.getStopType(j + (1 - 2 * direct))==3&&!train.timeExist(j)){

                            if(train.getPredictionTime(j)<0){
                            }else{
                                //この次の駅から経由なしになるとき
                                trainPath.add(train.getPredictionTime(j)-diaFile.diagramStartTime);
                                trainPath.add(stationTime.get(j));
                                drawable=false;
                            }
                        }
                        if(!drawable&&train.getStopType(j )==2&&train.getStopType(j - (1 - 2 * direct))==3&&!train.timeExist(j)){
                            if(train.getPredictionTime(j)<0){

                            }else{
                                //この前の駅めで経由なしのとき
                                trainPath.add(train.getPredictionTime(j)-diaFile.diagramStartTime);
                                trainPath.add(stationTime.get(j));
                                drawable=true;
                            }
                        }
                    }
                    if(train.departExist(j)){
                        //停車時間を描画する
                        trainPath.add((train.getDATime(j)- diaFile.diagramStartTime));
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
                                    diaFile.trainType.get(train.type).stopmark &&
                                    j!=train.startStation()&&
                                    j!=train.endStation()&&
                                    trainPath.get(trainPath.size() - 4).equals(trainPath.get(trainPath.size() - 6))) {
                                //始発終着駅を除き　停車マークを用意する
                                stopMark[direct].add(trainPath.get(trainPath.size() - 4));
                                stopMark[direct].add(trainPath.get(trainPath.size() - 3));
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
                diagramPath[direct].add(trainPath);

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
            arr[i] = iter.next()*setting.scaleX;
            i++;
            arr[i] = iter.next()*setting.scaleY;
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
            if (diaFile.station.get(i).bigStation) {
                paint.setStrokeWidth(defaultLineSize);
            } else {
                paint.setStrokeWidth(defaultLineSize*0.5f);
            }
            canvas.drawLine(0,diaFile.getStationTime().get(i) * setting.scaleY, 60*24* setting.scaleX *60,diaFile.getStationTime().get(i) * setting.scaleY, paint);
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
                if ((direct == 0 && setting.showDownTrain) || (direct == 1 && setting.showUpTrain)) {
                    for (int i = 0; i < diaFile.getTrainSize(diaNumber,direct); i++) {
                        //ダイヤ線色を指定
                        paint.setColor(diaFile.trainType.get(diaFile.getTrain(diaNumber,direct,i).type).diaColor.getAndroidColor());
                        if (focsTrain.size()!=0) {
                            //強調表示の列車があるときは半透明化
                            paint.setAlpha(100);
                        }
                        //線の太さを指定
                        if (diaFile.trainType.get(diaFile.getTrain(diaNumber,direct,i).type).bold) {
                            paint.setStrokeWidth(defaultLineSize * 2f);
                        } else {
                            paint.setStrokeWidth(defaultLineSize);
                        }

                        //強調ダイヤ線時刻描画
                        if (focsTrain.contains(diaFile.getTrain(diaNumber,direct,i))) {
                            //強調表示の線は半透明ではない
                            Train train=diaFile.getTrain(diaNumber,direct,i);
                            paint.setAlpha(255);
                            //線の太さを太くする
                            paint.setStrokeWidth(defaultLineSize * 3f);
                            //文字色もダイヤ色に合わせて変更
                            textPaint.setColor(diaFile.trainType.get(diaFile.getTrain(diaNumber,direct,i).type).diaColor.getAndroidColor());
                            textPaint.setAlpha(255);
                            for (int j = 0; j < diaFile.getStationNum(); j++) {
                                if (train.arriveExist(j)) {
                                    canvas.drawText(String.format("%02d", (train.getArrivalTime(j) / 60) % 60), (train.getArrivalTime(j) - diaFile.diagramStartTime) * setting.scaleX *60 / 60,diaFile.getStationTime().get(j) * setting.scaleY + textPaint.getTextSize() *(-0.2f+direct*1.2f), textPaint);
                                }
                                if (train.departExist(j)) {
                                    canvas.drawText(String.format("%02d", (train.getDepartureTime(j) / 60) % 60), (train.getDepartureTime(j) - diaFile.diagramStartTime) * setting.scaleX *60 / 60 - textPaint.getTextSize() ,diaFile.getStationTime().get(j) * setting.scaleY + textPaint.getTextSize() *(1-direct*1.2f), textPaint);

                                }
                            }
                        }
                        if(onlySolid){
                            canvas.drawLines(toArr(diagramPath[direct].get(i)), paint);
                        }else {
                            //指定線種に合わせてダイヤ線を描画
                            switch ((diaFile.trainType.get(diaFile.getTrain(diaNumber, direct, i).type).lineStyle)) {
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
                    if (setting.showTrainStop) {
                        //停車駅の表示も行う

                        paint.setColor(Color.BLACK);
                        paint.setStrokeWidth(defaultLineSize);
                        for (int i = 0; i < stopMark[direct].size() / 2; i++) {
                            canvas.drawCircle(stopMark[direct].get(2 * i) * setting.scaleX *60 / 60, stopMark[direct].get(2 * i + 1) * setting.scaleY, defaultLineSize * 2f, paint);
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
        final int axisHeight=stationTime.get(diaFile.getStationNum() - 1);


        //1時間ごとの目盛
        //以下太実線
        if(setting.verticalAxis ==7){
            for (int i = 0; i < 48; i++) {
                canvas.drawLine(setting.scaleX *60 * (30 + 30 * i), 0, setting.scaleX *60 * (30 + 30 * i),axisHeight * setting.scaleY, paint);
            }

        }else {
            for (int i = 0; i < 24; i++) {
                canvas.drawLine(setting.scaleX *60 * (60 + 60 * i), 0, setting.scaleX *60 * (60 + 60 * i),axisHeight* setting.scaleY, paint);
            }
        }
        paint.setStrokeWidth(defaultLineSize*0.5f);
        dotPaint.setStrokeWidth(defaultLineSize*0.5f);
        switch(setting.verticalAxis){
            case 1:
                for (int i = 0; i < 24; i++) {
                    //30分ごとの目盛
                    canvas.drawLine(setting.scaleX *60 * (30 + 60 * i), 0, setting.scaleX *60 * (30 + 60 * i),axisHeight * setting.scaleY, paint);
                }
                break;
            case 2:
                //20分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(setting.scaleX *60 * (20 + 60 * i), 0, setting.scaleX *60 * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (40 + 60 * i), 0, setting.scaleX *60 * (40 + 60 * i),axisHeight * setting.scaleY, paint);
                }
                break;
            case 3:
                //15分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(setting.scaleX *60 * (15 + 60 * i), 0, setting.scaleX *60 * (15 + 60 * i),axisHeight * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (30 + 60 * i), 0, setting.scaleX *60 * (30 + 60 * i),axisHeight * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (45 + 60 * i), 0, setting.scaleX *60 * (45 + 60 * i),axisHeight * setting.scaleY, paint);
                }
                break;
            case 4:
                //10分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(setting.scaleX *60 * (30 + 60 * i), 0, setting.scaleX *60 * (30 + 60 * i),axisHeight * setting.scaleY, paint);
                }
                for (int i = 0; i < 48; i++) {
                    if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY>2048) {
                        canvas.drawLine(setting.scaleX *60 * (10 + 30 * i), 0, setting.scaleX *60 * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, dot2Paint);
                        canvas.drawLine(setting.scaleX *60 * (20 + 30 * i), 0, setting.scaleX *60 * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(setting.scaleX *60 * (10 + 30 * i), 0);
                        dotLine.lineTo(setting.scaleX *60 * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY);
                        dotLine.moveTo(setting.scaleX *60 * (20 + 30 * i), 0);
                        dotLine.lineTo(setting.scaleX *60 * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 5:
                //5分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(setting.scaleX *60 * (10 + 60 * i), 0, setting.scaleX *60 * (10 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (20 + 60 * i), 0, setting.scaleX *60 * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (30 + 60 * i), 0, setting.scaleX *60 * (30 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (40 + 60 * i), 0, setting.scaleX *60 * (40 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (50 + 60 * i), 0, setting.scaleX *60 * (50 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY>2048) {
                        canvas.drawLine(setting.scaleX *60 * (5 + 10 * i), 0, setting.scaleX *60 * (5 + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, dot2Paint);
                    }else{
                        Path dotLine = new Path();
                        dotLine.moveTo(setting.scaleX *60 * (5 + 10 * i), 0);
                        dotLine.lineTo(setting.scaleX *60 * (5 + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY);
                        canvas.drawPath(dotLine, dotPaint);
                    }
                }
                break;
            case 6:
                //2分ごとの目盛
                for (int i = 0; i < 24; i++) {
                    canvas.drawLine(setting.scaleX *60 * (10 + 60 * i), 0, setting.scaleX *60 * (10 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (20 + 60 * i), 0, setting.scaleX *60 * (20 + 60 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (30 + 60 * i), 0, setting.scaleX *60 * (30 + 60 * i),axisHeight * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (40 + 60 * i), 0, setting.scaleX *60 * (40 + 60 * i),axisHeight * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (50 + 60 * i), 0, setting.scaleX *60 * (50 + 60 * i),axisHeight * setting.scaleY, paint);
                }
                for (int i = 0; i < 24*6; i++) {
                    for(int j=1;j<5;j++){
                        if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY>2048) {
                            canvas.drawLine(setting.scaleX *60 * (2 * j + 10 * i), 0, setting.scaleX *60 * (2 * j + 10 * i),axisHeight * setting.scaleY, dot2Paint);
                        }else {
                            Path dotLine = new Path();
                            dotLine.moveTo(setting.scaleX *60 * (2 * j + 10 * i), 0);
                            dotLine.lineTo(setting.scaleX *60 * (2 * j + 10 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY);
                            canvas.drawPath(dotLine, dotPaint);
                        }
                    }
                }
                break;
            case 7:
                //1分ごとの目盛

                for (int i = 0; i < 24*2; i++) {
                    canvas.drawLine(setting.scaleX *60 * (5 + 30 * i), 0, setting.scaleX *60 * (5 + 30 * i),axisHeight * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (10 + 30 * i), 0, setting.scaleX *60 * (10 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (15 + 30 * i), 0, setting.scaleX *60 * (15 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (20 + 30 * i), 0, setting.scaleX *60 * (20 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                    canvas.drawLine(setting.scaleX *60 * (25 + 30 * i), 0, setting.scaleX *60 * (25 + 30 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, paint);
                }
                for (int i = 0; i < 24*12; i++) {
                    for(int j=1;j<5;j++) {
                        if(diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY>2048) {
                            canvas.drawLine(setting.scaleX *60 * (1 * j + 5 * i), 0, setting.scaleX *60 * (1 * j + 5 * i),diaFile.getStationTime().get(diaFile.getStationNum() - 1) * setting.scaleY, dot2Paint);
                        }else{

                            Path dotLine = new Path();
                            dotLine.moveTo(setting.scaleX *60 * (1 * j + 5 * i), 0);
                            dotLine.lineTo(setting.scaleX *60 * (1 * j + 5 * i),axisHeight * setting.scaleY);
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
            if((direct==0&&setting.showDownTrain)||(direct==1&&setting.showUpTrain)) {
                for(int i=0;i<diaFile.getTrainSize(diaNumber,direct);i++){
                    int pathNum=-1;
                    for(int j=0;j+3<diagramPath[direct].get(i).size();j=j+2){
                        if(diagramPath[direct].get(i).get(j+1)!=diagramPath[direct].get(i).get(j+3)){
                            pathNum=j;
                            break;
                        }
                    }
                    if(pathNum<0)continue;
                    //列車番号を表示する部分のダイヤ線の座標を取得
                    int x1=(int)(diagramPath[direct].get(i).get(pathNum)*setting.scaleX *60/60);
                    int y1=(int)(diagramPath[direct].get(i).get(pathNum+1)*setting.scaleY);
                    int x2=(int)(diagramPath[direct].get(i).get(pathNum+2)*setting.scaleX *60/60);
                    int y2=(int)(diagramPath[direct].get(i).get(pathNum+3)*setting.scaleY);
                    canvas.save();
                    double rad=Math.atan2((double)(y2-y1),(double)(x2-x1));
                    //canvasを回転して
                    canvas.rotate((float) Math.toDegrees(rad),x1,y1);
                    //列車番号を描画
                    textPaint.setColor(diaFile.trainType.get(diaFile.getTrain(diaNumber,direct,i).type).diaColor.getAndroidColor());
                    if(focsTrain.size()==0||focsTrain.contains(diaFile.getTrain(diaNumber,direct,i))){
                        textPaint.setAlpha(255);
                    }else{
                        textPaint.setAlpha(100);
                    }
                    //textに表示したい文字列を代入
                    String text="";
                    if(setting.numberState%2==1){
                        if(diaFile.getTrain(diaNumber,direct,i).number.length()>0) {
                            text = text + diaFile.getTrain(diaNumber,direct,i).number + "   ";
                        }
                    }
                    if(setting.numberState/2==1){
                        if(diaFile.getTrain(diaNumber,direct,i).name.length()>0) {
                            text = text + diaFile.getTrain(diaNumber,direct,i).name + "  ";
                        }
                        if(diaFile.getTrain(diaNumber,direct,i).count.length()>0) {
                            text = text + diaFile.getTrain(diaNumber,direct,i).count;
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
        nowTime= nowTime-diaFile.diagramStartTime+9*60*60;
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
        canvas.drawLine(nowTime*setting.scaleX *60/60,0,nowTime*setting.scaleX *60/60, diaFile.getStationTime().get(diaFile.getStationNum() - 1)* setting.scaleY,paint);
    }
    /**
     * フォーカスする列車を選択する。
     * ダイヤグラム画面内を長押しすることで実行する。
     * @see #focsTrain フォーカスする列車
     *
     * これらのパラメーターは、DiagramViewの左上を基準とした座標
     */
    public void showDetail(int x,int y) {
        try {
            //まずタッチポイントから実際の秒単位のタッチ場所を検出します。
            x =(int)( x / setting.scaleX );
            y = (int)(y / setting.scaleY);
            if (y >diaFile.getStationTime().get(diaFile.getStationTime().size() - 1)) {
                return;
            }
            //描画しているダイヤ線のうちタッチポイントに最も近いものを検出します。
            float minDistance = 4000;
            int minTrainNum = -1;
            int minTrainDirect = -1;
            for(int direct=0;direct<2;direct++){
                if((direct==0&&setting.showDownTrain)||(direct==1&&setting.showUpTrain)) {
                    for (int i = 0; i < diagramPath[direct].size(); i++) {
                        for (int j = 0; j < diagramPath[direct].get(i).size() / 4; j++) {
                            if (diagramPath[direct].get(i).get(4 * j) < x && diagramPath[direct].get(i).get(4 * j + 2) > x) {
                                float distance;
                                if (true) {
                                    //xの差のほうが大きい
                                    distance = setting.scaleY * Math.abs(((float) diagramPath[direct].get(i).get(4 * j + 3) - (float) diagramPath[direct].get(i).get(4 * j + 1)) /
                                            ((float) diagramPath[direct].get(i).get(4 * j + 2) - (float) diagramPath[direct].get(i).get(4 * j)) * (x - diagramPath[direct].get(i).get(4 * j)) + diagramPath[direct].get(i).get(4 * j + 1) - y);
                                } else {
                                    //yの差のほうが大きい
                                    distance = setting.scaleX *60 * Math.abs((diagramPath[direct].get(i).get(4 * j + 2) - diagramPath[direct].get(i).get(4 * j)) /
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
                focsTrain=new ArrayList<>();
                this.invalidate();
                return;
            }
            if(!focsTrain.contains(diaFile.getTrain(diaNumber,minTrainDirect,minTrainNum))){
                focsTrain=new ArrayList<>();
                final Train t=diaFile.getTrain(diaNumber,minTrainDirect,minTrainNum);
                focsTrain.add(t);
                Train beforeT=diaFile.diagram.get(diaNumber).beforeOperation(t);
                while(beforeT!=null) {
                    focsTrain.add(beforeT);
                    beforeT=diaFile.diagram.get(diaNumber).beforeOperation(beforeT);
                }
                Train afterT=diaFile.diagram.get(diaNumber).nextOperation(t);
                while(afterT!=null) {
                    focsTrain.add(afterT);
                    afterT=diaFile.diagram.get(diaNumber).nextOperation(afterT);
                }
            }else{
                focsTrain=new ArrayList<>();
            }
            this.invalidate();

        }catch(Exception e){
            SDlog.log(e);
        }

    }
    /**
     * onMesureをオーバーライドすることで
     * このViewのサイズを設定する
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
        return (int)(1440*60* setting.scaleX);
    }
    /**
     * このViewの実際の描画範囲のサイズ
     * @return
     */

    protected int getYsize(){
        if(diaFile.getStationNum()==0)return 1000;
        return (int)(diaFile.getStationTime().get(diaFile.getStationNum()-1)* setting.scaleY+(int)textPaint.getTextSize()+4);
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
