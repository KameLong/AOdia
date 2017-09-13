package com.kamelong.aodia.timeTable;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kamelong.aodia.detabase.DBHelper;
import com.kamelong.aodia.KLFragment;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.stationInfo.StationIndoDialog;

/**
 * Created by Owner on 2016/11/21.
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
 * 路線時刻表のFragment。
 * 一つの路線時刻表（上り、下りで独立している）につき一つの生成が必要
 * @author kamelong
 */
public class TimeTableFragment extends KLFragment {
	/**
	* このFragmentのcontainer
	*/
    private View fragmentContainer;
	/**
	* MainActivity内には多数のdiaFileが格納されている、どのdiaFileなのかのインデックス
	*/
    private int fileNum=0;
	/**
	*　この路線時刻表で表示すべきダイヤインデックス
	*/
    public int diaNumber=0;
	/**
	*　この路線時刻表で表示すべき方向
	*/
    public int direct=0;
	/**
	*フリングが行われているかのフラグ
	*/
    private boolean fling = false;

    Handler handler = new Handler();

    public TimeTableFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            diaNumber = bundle.getInt("diaN");
            fileNum=bundle.getInt("fileNum");
            direct =  bundle.getInt("direct");
        }catch(Exception e){
            SdLog.log(e);
        }
		//Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.time_table, container, false);
		//このFragment上でのタッチジェスチャーの管理
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    private float flingV = 0;

                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        fling = false;
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent motionEvent) {
                    }
                    @Override
                    public void onLongPress(MotionEvent motionEvent) {

                        int y=(int)motionEvent.getY();
                        int timeTabley=y+findViewById(R.id.trainTimeLinear).getScrollY()-findViewById(R.id.trainNameLinear).getHeight();
                        int station=((StationNameView)((LinearLayout)findViewById(R.id.stationNameLinear)).getChildAt(0)).getStationFromY(timeTabley);
                        if(station<0){
                            return;
                        }
                        SdLog.log("timeTableLongPress", diaFile.getStation(station).getName());
                        StationIndoDialog dialog = new StationIndoDialog(getActivity(),TimeTableFragment.this, diaFile,fileNum,diaNumber,direct,station);
                        dialog.show();

                    }
                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                        TimeTableFragment.this.scrollBy((int) vx, (int) vy);
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                        final float flingV = -v1;
                        fling = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (fling) {
                                    try {
                                        TimeTableFragment.this.scrollBy((int)(flingV*16/1000f), 0);
                                        Thread.sleep(16);
                                    } catch (Exception e) {
                                        fling=false;
                                        SdLog.log(e);
                                    }
                                }
                            }
                        }).start();
                        return false;
                    }
                });

        fragmentContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return fragmentContainer;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the gesture detector as the double tap
        // listener.
        init();

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DBHelper db = new DBHelper(getActivity());
            db.setRecentFile(diaFile.getFilePath(),diaNumber,direct);
            int[] scroll = db.getPositionData(db.getReadableDatabase(), diaFile.getFilePath(), diaNumber, direct);
            db.close();
            scrollTo(scroll[0], scroll[1]);
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    private void init() {
        try {
            try {
                diaFile = ((MainActivity) getActivity()).diaFiles.get(fileNum);
            }catch(Exception e){
                Toast.makeText(getActivity(),"なぜこの場所でエラーが起こるのか不明です。対策したいのですが、理由不明のため対策ができません。情報募集中です！",Toast.LENGTH_LONG);
            }
            if(diaFile==null){
                onDestroy();
                return;
            }
            FrameLayout lineNameFrame = (FrameLayout) findViewById(R.id.lineNameFrame);
            LineNameView lineNameView = new LineNameView(getActivity(), diaFile, diaNumber);
            lineNameFrame.removeAllViews();
            lineNameFrame.addView(lineNameView);
            LinearLayout stationNameLinea = (LinearLayout) findViewById(R.id.stationNameLinear);
            StationNameView stationNameView = new StationNameView(getActivity(), diaFile, direct);
            stationNameLinea.removeAllViews();
            stationNameLinea.addView(stationNameView);

            LinearLayout trainNameLinea = (LinearLayout) findViewById(R.id.trainNameLinear);
            trainNameLinea.removeAllViews();
            TrainNameView[] trainNameViews = new TrainNameView[diaFile.getTrainNum(diaNumber, direct)];
            for (int i = 0; i < diaFile.getTrainNum(diaNumber, direct); i++) {
                trainNameViews[i] = new TrainNameView(getActivity(), diaFile, diaFile.getTrain(diaNumber, direct, i));
                trainNameLinea.addView(trainNameViews[i]);
            }
            LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
            trainTimeLinear.removeAllViews();
            TrainTimeView[] trainTimeViews = new TrainTimeView[diaFile.getTrainNum(diaNumber, direct)];
            for (int i = 0; i < trainNameViews.length; i++) {
                trainTimeViews[i] = new TrainTimeView(getActivity(), diaFile, diaFile.getTrain(diaNumber, direct, i), direct);
                trainTimeLinear.addView(trainTimeViews[i]);
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }
    private void scrollBy(int dx, int dy) {
        final LinearLayout trainTimeLinear = (LinearLayout)findViewById(R.id.trainTimeLinear);
        int scrollX = trainTimeLinear.getScrollX() + dx;
        int scrollY = trainTimeLinear.getScrollY() + dy;
        scrollTo(scrollX,scrollY);
    }
    private void scrollTo(int scrollX, int scrollY) {
        try {
            final LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
            FrameLayout trainTimeFrame = (FrameLayout) findViewById(R.id.trainTimeFrame);
            if (scrollX > 6 + ((TrainTimeView)trainTimeLinear.getChildAt(0)).getXsize() * trainTimeLinear.getChildCount() - trainTimeFrame.getWidth()) {
                scrollX = 6 + ((TrainTimeView)trainTimeLinear.getChildAt(0)).getXsize()  * trainTimeLinear.getChildCount() - trainTimeFrame.getWidth();
            }
            if (scrollX < 0) {
                scrollX = 0;
            }
            if (scrollY >  ((TrainTimeView)trainTimeLinear.getChildAt(0)).getYsize()  - trainTimeFrame.getHeight()) {
                scrollY =  ((TrainTimeView)trainTimeLinear.getChildAt(0)).getYsize()  - trainTimeFrame.getHeight();
            }
            if (scrollY < 0) {
                scrollY = 0;
            }
            final LinearLayout trainNameLinear = (LinearLayout) findViewById(R.id.trainNameLinear);
            final LinearLayout stationNameLinear = (LinearLayout) findViewById(R.id.stationNameLinear);
            final int mscrollX=scrollX;
            final int mscrollY=scrollY;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        trainTimeLinear.scrollTo(mscrollX, mscrollY);
                        trainNameLinear.scrollTo(mscrollX, 0);
                        stationNameLinear.scrollTo(0, mscrollY);
                        return;
                    }catch(Exception e){
                        SdLog.log(e);
                    }
                }
            });
        } catch (Exception e) {
            SdLog.log(e);
            fling=false;
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        try {
            DBHelper db = new DBHelper(getActivity());
            ContentValues cont = new ContentValues();
            String d;
            if (direct == 0) {
                d = "down";
            } else {
                d = "up";
            }
            final LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
            cont.put(d + "ScrollX", "" + trainTimeLinear.getScrollX());
            cont.put(d + "ScrollY", "" + trainTimeLinear.getScrollY());
            db.updateLineData( diaFile.getFilePath(), diaNumber, direct,trainTimeLinear.getScrollX(), trainTimeLinear.getScrollY());

        }catch(Exception e){
            SdLog.log(e);
        }
    }
    public void sortTrain(int station){
        if(station>=0&&station< diaFile.getStationNum()){
            diaFile.sortTrain(diaNumber,direct,station);
            this.init();
        }

    }
    public void goTrain(final int trainNum){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LinearLayout trainTimeLinear = null;
                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        SdLog.log(e);
                        break;
                    }
                    try {
                        trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
                        scrollTo(trainNum * ((TrainTimeView) trainTimeLinear.getChildAt(0)).getXsize(), 0);
                        break;
                    } catch (Exception e) {
                        SdLog.log(e);

                    }
                }
            }
        }).start();


    }
    public View findViewById(int id){
        try{
            return fragmentContainer.findViewById(id);
        }catch(Exception e){
            SdLog.log(e);
        }
        return null;
    }
    @Override
    public String fragmentName(){
        try {
            if (direct == 0) {
                return "下り時刻表　" + diaFile.getDiaName(diaNumber) + "　" + diaFile.getLineName();

            } else {
                return "上り時刻表　" + diaFile.getDiaName(diaNumber) + "　" + diaFile.getLineName();

            }
        }catch(Exception e){
            return "";
        }
    }

}
