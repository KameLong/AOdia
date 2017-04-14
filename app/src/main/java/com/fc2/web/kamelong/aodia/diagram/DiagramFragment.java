package com.fc2.web.kamelong.aodia.diagram;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fc2.web.kamelong.aodia.detabase.DBHelper;
import com.fc2.web.kamelong.aodia.KLFragment;
import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;

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
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */

public class DiagramFragment extends KLFragment {
    View fragmentContainer=null;
    StationView stationView;
    TimeView timeView;
    DiagramView diagramView;
    private float startX1;
    private float startX0;
    private float startY1;
    private float startY0;
    private float scrollX;
    private float scrollY;
    private boolean pinchFragX=false;
    private boolean pinchFragY=false;
    float scaleX;
    float scaleY;
    private DiagramSetting setting;
    private int fileNum=0;
    public int diaNumber=0;
    Handler handler = new Handler(); // (1)

    public DiagramFragment() {
        super();
    }

    // 初期フォルダ
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        try {

            Bundle bundle = getArguments();
            diaNumber = bundle.getInt("diaN");
            fileNum=bundle.getInt("fileNum");
        }catch(Exception e){
            SdLog.log(e);
        }
        fragmentContainer = inflater.inflate(R.layout.diagram, container, false);

        return fragmentContainer;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        diaFile=((MainActivity) getActivity()).diaFiles.get(fileNum);
//        setting=((MainActivity)getActivity()).diagramSetting;
        setting=new DiagramSetting(getActivity());
        setting.create(this);
        try {
            FrameLayout stationFrame = (FrameLayout) view.findViewById(R.id.station);
            FrameLayout timeFrame = (FrameLayout) view.findViewById(R.id.time);
            FrameLayout diaFrame = (FrameLayout) view.findViewById(R.id.diagramFrame);
            diagramView = new DiagramView(getActivity(), setting,diaFile, diaNumber);
            stationView = new StationView(getActivity(),setting,diaFile,diaNumber);
            stationFrame.addView(stationView);
            timeView = new TimeView(getActivity(),setting,diaFile);
            timeFrame.addView(timeView);
            diaFrame.addView(diagramView);


            if (diaFile.getTrainNum(0, 0) < 100) {
                scaleX = 10;
                scaleY = 20;
            } else {
                scaleX = 15;
                scaleY = 30;
            }
            setScale();
        }
        catch(Exception e){
            SdLog.log(e);
        }
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    private boolean fling = false;
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        pinchFragX=false;
                        pinchFragY=false;
                        fling = false;
                        return true;
                    }
                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent){
                        return false;
                    }
                    @Override
                    public void onShowPress(MotionEvent motionEvent) {
                    }
                    @Override
                    public void onLongPress(MotionEvent motionEvent) {

                        int x=(int)motionEvent.getX();
                        int y=(int)motionEvent.getY();

                        if(x>stationView.getWidth()&&y>timeView.getHeight()){
                            diagramView.showDetail((int )motionEvent.getX()+(int)scrollX-stationView.getWidth(),(int )motionEvent.getY()+(int)scrollY-timeView.getHeight());
                        }
                    }

                    @Override
                    public boolean onScroll(MotionEvent motionEvent1, MotionEvent motionEvent, float vx, float vy) {
                        float scrolldx=0;
                        float scrolldy=0;
                        if(motionEvent.getPointerCount()==1){
                            pinchFragX=false;
                            pinchFragY=false;
                            DiagramFragment.this.scrollBy(vx,  vy);
                        }
                        if(motionEvent.getPointerCount()==2){

                            if(pinchFragX) {
                                if(Math.abs(motionEvent.getX(1) - motionEvent.getX(0))>200) {
                                    scrolldx=- motionEvent.getX(0)+stationView.getWidth()+(scrollX+startX0)/(startX1-startX0)*(motionEvent.getX(1)-motionEvent.getX(0))-scrollX;
                                    scaleX =  ((motionEvent.getX(1) - motionEvent.getX(0)) / (startX1 - startX0) * scaleX);
                                    scrollX=scrollX+scrolldx;
                                    startX1=motionEvent.getX(1)-stationView.getWidth();
                                    startX0=motionEvent.getX(0)-stationView.getWidth();
                                }else{
                                    pinchFragX=false;
                                }
                            }else if(Math.abs(motionEvent.getX(1)-stationView.getWidth()-motionEvent.getX(0)+stationView.getWidth())>200){
                                startX0 = motionEvent.getX(0)-stationView.getWidth();
                                startX1 = motionEvent.getX(1)-stationView.getWidth();
                                pinchFragX=true;
                            }

                            if(pinchFragY) {
                                if(Math.abs(motionEvent.getY(1) - motionEvent.getY(0))>200){

                                    scrolldy=- motionEvent.getY(0)+timeView.getHeight()+(scrollY+startY0)/(startY1-startY0)*(motionEvent.getY(1)-motionEvent.getY(0))-scrollY;
                                    scaleY =  ((motionEvent.getY(1) - motionEvent.getY(0)) / (startY1 - startY0) * scaleY);
                                    scrollY=scrollY+scrolldy;
                                    startY1=motionEvent.getY(1)-timeView.getHeight();
                                    startY0=motionEvent.getY(0)-timeView.getHeight();

                                }else{
                                    pinchFragY=false;
                                }
                            }else if(Math.abs(motionEvent.getY(1)-motionEvent.getY(0))>200){
                                startY0 = motionEvent.getY(0)-timeView.getHeight();
                                startY1 = motionEvent.getY(1)-timeView.getHeight();
                                pinchFragY=true;
                            }
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                SdLog.log(e);
                            }
                            setScale();
                            diagramView.scrollBy((int)scrolldx,(int)scrolldy);
                            stationView.scrollBy(0,(int)scrolldy);
                            timeView.scrollBy((int)scrolldx,0);
                            scrollTo();

                        }
                        if(motionEvent.getPointerCount()==3){
                            pinchFragX=false;
                            pinchFragY=false;
                        }
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                        if(e2.getPointerCount()==1) {
                            final float flingV = -v1 / 60;
                            fling = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (fling) {
                                        try {
                                            DiagramFragment.this.scrollBy( flingV, 0);

                                            Thread.sleep(16);
                                        } catch (Exception e) {
                                            SdLog.log(e);
                                            fling=false;
                                        }
                                    }
                                }
                            }).start();
                        }
                        return false;
                    }
                });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });



    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    private void scrollBy(float dx,float dy){
        scrollX+=dx;
        scrollY+=dy;

        scrollTo();

    }

    private void scrollTo(){

        try {
            if (autoScroll) {
                FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
                final int width = diagramFrame.getWidth();
                int nowTime = (int) (System.currentTimeMillis() % (24 * 60 * 60 * 1000)) / 1000;
                nowTime = nowTime - diaFile.getDiagramStartTime() + 9 * 60 * 60;
                if (nowTime < 0) {
                    nowTime = nowTime + 24 * 60 * 60;
                }
                if (nowTime > 24 * 60 * 60) {
                    nowTime = nowTime - 24 * 60 * 60;
                }
                scrollX =  (nowTime * scaleX / 60) - width / 2;
            }
            if (scrollY == -1) {
                scrollY = findViewById(R.id.diagramFrame).getScrollY();
            }
            FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);

            if (scrollX > diagramView.getmWidth() - diagramFrame.getWidth() + 6) {
                scrollX = diagramView.getmWidth() - diagramFrame.getWidth() + 6;
            }
            if (scrollX <0) {
                scrollX = 0;
            }
            if (scrollY > diagramView.getmHeight() - diagramFrame.getHeight() + 6) {
                scrollY = diagramView.getmHeight() - diagramFrame.getHeight() + 6;
            }
            if (scrollY <0) {
                scrollY = 0;
            }
            final int mScrollX = (int)scrollX;
            final int mScrollY = (int)scrollY;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        findViewById(R.id.diagramFrame).scrollTo(mScrollX -diagramView.getScrollX(), mScrollY-diagramView.getScrollY());
                        stationView.scrollTo(0, mScrollY);
                        timeView.scrollTo(mScrollX, 0);
                    }catch(Exception e){
                        SdLog.log(e);
                    }
                    return;
                }
            });
        }catch(Exception e){
            SdLog.log(e);
        }
    }
    private void setScale(){
        if(scaleX<0.5f){
            scaleX=0.5f;
        }
        if(scaleY<0.5f){
            scaleY=0.5f;
        }

        diagramView.setScale(scaleX,scaleY);
        stationView.setScale(scaleX,scaleY);
        timeView.setScale(scaleX,scaleY);
    }
    public void moveToTrain(int trainNum){

    }
    @Override
    public void onStop(){
        int scrollX=findViewById(R.id.diagramFrame).getScrollX();
        int scrollY=findViewById(R.id.diagramFrame).getScrollY();
        super.onStop();
        try {
            DBHelper db = new DBHelper(getActivity());
            ContentValues cont = new ContentValues();
            cont.put("diaScrollX", "" + findViewById(R.id.diagramFrame).getScrollX());
            cont.put("diaScrollY", "" + findViewById(R.id.diagramFrame).getScrollY());
            cont.put("diaScaleX", "" + scaleX);
            cont.put("diaScaleY", "" + scaleY);
            db.update(db.getWritableDatabase(),  diaFile.getFilePath(), diaNumber, cont);
            db.setRecentFile(diaFile.getFilePath(),diaNumber,2);

        }catch(Exception e){
            SdLog.log(e);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            DBHelper db = new DBHelper(getActivity());
            int[] scroll = db.getPositionData(db.getReadableDatabase(),  diaFile.getFilePath(), diaNumber, 2);
            scaleX=scroll[2];
            scaleY=scroll[3];
            if(scaleX<0.5f){
                scaleX=10;
            }
            if(scaleY<0.5f){
                scaleY=20;
            }
            scrollX=scroll[0];
            scrollY=scroll[1];
            scrollTo();
            setScale();
        }catch(Exception e){
            SdLog.log(e);
        }
    }
    private boolean autoScroll=false;
    public void autoScroll(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // マルチスレッドにしたい処理 ここから
                autoScroll=true;
                while(autoScroll){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            scrollTo();
                            diagramView.invalidate();
                        }
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        SdLog.log(e);
                    }
                }
            }
        }).start();
    }
    public  void stopAutoScroll(){
        autoScroll=false;
    }
    public void fitVertical(){
        FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
        float frameSize=diagramFrame.getHeight()-40;
        float nessTime=diaFile.getStationTime().get(diaFile.getStationNum()-1);
        scaleY=frameSize/nessTime*60;
        setScale();
        stationView.invalidate();

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
        return "ダイヤグラム　"+diaFile.getDiaName(diaNumber)+"　"+diaFile.getLineName();
    }
}
