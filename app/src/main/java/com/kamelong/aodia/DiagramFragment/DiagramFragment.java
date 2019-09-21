package com.kamelong.aodia.DiagramFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kamelong.aodia.AOdia;
import com.kamelong.aodia.AOdiaData.LineFile;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * ダイヤグラムを表示するためのFragment
 */
public class DiagramFragment extends AOdiaFragmentCustom {
    public StationView stationView;
    public TimeView timeView;
    public DiagramView diagramView;
    public DiagramOptions setting;

    public int diaIndex = 0;
    private int lineIndex = 0;
    public LineFile lineFile;
    Handler handler = new Handler();
    private boolean autoScroll=false;

    private View fragmentContainer;

    /**
     * DiagramFragment内のタッチジェスチャー
     */
    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {
                private boolean pinchFragX = false;
                private boolean pinchFragY = false;
                private boolean fling = false;

                private float startX1;
                private float startX0;
                private float startY1;
                private float startY0;

                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    //ピンチ、フリングを中断する
                    pinchFragX = false;
                    pinchFragY = false;
                    fling = false;
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {
                    //長押ししたときはDiagramViewのfocusTrainを指定する
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();

                    if (x > stationView.getWidth() && y > timeView.getHeight()) {
                        diagramView.showDetail((int) motionEvent.getX() + setting.scrollX - stationView.getWidth(), (int) motionEvent.getY() + setting.scrollY - timeView.getHeight());
                    }
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent1, MotionEvent motionEvent, float vx, float vy) {
                    float scrolldx = 0;
                    float scrolldy = 0;
                    if (motionEvent.getPointerCount() == 1) {
                        //１本指の時はスクロールする
                        pinchFragX = false;
                        pinchFragY = false;
                        DiagramFragment.this.scrollBy(vx, vy);
                    }
                    if (motionEvent.getPointerCount() == 2) {

                        //二本指の時はピンチをする。
                        //２本の指が押しているポイントが変化しないように座標計算を行う
                        //なお２本の指がx方向y方向においてそれぞれ200ピクセル以下しか離れていないときは、その方向のピンチを無効化する
                        if (pinchFragX) {
                            if (Math.abs(motionEvent.getX(1) - motionEvent.getX(0)) > 200) {
                                scrolldx = -motionEvent.getX(0) + stationView.getWidth() + (setting.scrollX + startX0) / (startX1 - startX0) * (motionEvent.getX(1) - motionEvent.getX(0)) - setting.scrollX;
                                setting.scaleX = ((motionEvent.getX(1) - motionEvent.getX(0)) / (startX1 - startX0) * setting.scaleX);
                                setting.scrollX += scrolldx;
                                startX1 = motionEvent.getX(1) - stationView.getWidth();
                                startX0 = motionEvent.getX(0) - stationView.getWidth();
                            } else {
                                pinchFragX = false;
                            }
                        } else if (Math.abs(motionEvent.getX(1) - stationView.getWidth() - motionEvent.getX(0) + stationView.getWidth()) > 200) {
                            startX0 = motionEvent.getX(0) - stationView.getWidth();
                            startX1 = motionEvent.getX(1) - stationView.getWidth();
                            pinchFragX = true;
                        }

                        if (pinchFragY) {
                            if (Math.abs(motionEvent.getY(1) - motionEvent.getY(0)) > 200) {

                                scrolldy = -motionEvent.getY(0) + timeView.getHeight() + (setting.scrollY + startY0) / (startY1 - startY0) * (motionEvent.getY(1) - motionEvent.getY(0)) - setting.scrollY;
                                setting.scaleY = ((motionEvent.getY(1) - motionEvent.getY(0)) / (startY1 - startY0) * setting.scaleY);
                                setting.scrollY += scrolldy;
                                startY1 = motionEvent.getY(1) - timeView.getHeight();
                                startY0 = motionEvent.getY(0) - timeView.getHeight();

                            } else {
                                pinchFragY = false;
                            }
                        } else if (Math.abs(motionEvent.getY(1) - motionEvent.getY(0)) > 200) {
                            startY0 = motionEvent.getY(0) - timeView.getHeight();
                            startY1 = motionEvent.getY(1) - timeView.getHeight();
                            pinchFragY = true;
                        }
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            SDlog.log(e);
                        }
                        setScale();
                        diagramView.scrollBy((int) scrolldx, (int) scrolldy);
                        stationView.scrollBy(0, (int) scrolldy);
                        timeView.scrollBy((int) scrolldx, 0);
                        scrollTo();

                    }
                    if (motionEvent.getPointerCount() > 2) {
                        //３本指以上の時はピンチを無効化
                        pinchFragX = false;
                        pinchFragY = false;
                    }
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                    //フリングしたときは、別スレッドで等速フリング動作を行う
                    if (e2.getPointerCount() == 1) {
                        final float flingV = -v1;
                        fling = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                float flingSpeed = flingV;
                                while (fling) {
                                    try {
                                        if (flingSpeed > 0) {
                                            flingSpeed = flingSpeed - 100;
                                        } else {
                                            flingSpeed = flingSpeed + 100;
                                        }
                                        if (Math.abs(flingSpeed) < 100) {
                                            fling = false;
                                            return;
                                        }

                                        DiagramFragment.this.scrollBy(flingSpeed / 60, 0);

                                        Thread.sleep(16);
                                    } catch (Exception e) {
                                        SDlog.log(e);
                                        fling = false;
                                    }
                                }
                            }
                        }).start();
                    }
                    return false;
                }
            });

    /**
     * 初期設定をする
     * bundleで必要なデータを送ること
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {

            Bundle bundle = getArguments();
            lineIndex = bundle.getInt(AOdia.FILE_INDEX, 0);
            diaIndex = bundle.getInt(AOdia.DIA_INDEX, 0);
            lineFile = getAOdia().getLineFile(lineIndex);
            if (lineFile.getDiagramNum() <= diaIndex) {
                throw new Exception();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(),"ダイヤを開けませんでした。ダイヤの削除を行った可能性があります。",Toast.LENGTH_LONG).show();
            SDlog.log(e);
            getAOdia().killFragment(this);

        }

        fragmentContainer = inflater.inflate(R.layout.diagram, container, false);

        return fragmentContainer;
    }

    /**
     * Viewが生成されると各種Viewを生成し、追加する。
     *
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            setting = new DiagramOptions((MainActivity) getActivity(), this, lineFile, diaIndex);
            setting.create(this);
            FrameLayout stationFrame = view.findViewById(R.id.station);
            FrameLayout timeFrame = view.findViewById(R.id.time);
            FrameLayout diaFrame = view.findViewById(R.id.diagramFrame);
            diagramView = new DiagramView(getActivity(), setting, lineFile, lineIndex, diaIndex);
            stationView = new StationView(getActivity(), setting, lineFile);
            timeView = new TimeView(getActivity(), setting, lineFile);
            stationFrame.addView(stationView);
            timeFrame.addView(timeView);
            diaFrame.addView(diagramView);

            setting.setDefault(getAOdia().database.getPositionData(lineFile.filePath, diaIndex, 2));


            scrollTo();

        } catch (Exception e) {
            SDlog.log(e);
        }
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //このViewのタッチジェスチャーを保存
                return gesture.onTouchEvent(event);
            }
        });

    }

    /**
     * スクロールする部分の差分が与えられた時の処理
     *
     */
    private void scrollBy(float dx, float dy) {
        setting.scrollX += dx;
        setting.scrollY += dy;
        scrollTo();

    }

    /**
     * DiagramViewを任意の場所にスクロールする。
     * スクロールする場所はscrollX,scrollYに依存する。
     * またこのメソッドは別スレッドないから呼ばれるので、handler.post()を用いている
     * <p>
     * またオートスクロールする場合は、本来のスクロール動作とは異なり、現時刻にスクロールする
     */
    private void scrollTo() {
        try {
            final FrameLayout diagramFrame = getActivity().findViewById(R.id.diagramFrame);
            if (autoScroll) {

                final int width = diagramFrame.getWidth();
                int nowTime = (int) (System.currentTimeMillis() % (24 * 60 * 60 * 1000)) / 1000;//システムの時間
                nowTime = nowTime - lineFile.diagramStartTime + 9 * 60 * 60;//時差
                if (nowTime < 0) {
                    nowTime = nowTime + 24 * 60 * 60;
                }
                if (nowTime > 24 * 60 * 60) {
                    nowTime = nowTime - 24 * 60 * 60;
                }
                setting.scrollX = (int) ((nowTime * setting.scaleX) - width / 2.0f);
            }
            if (setting.scrollY == -1) {
                //scrollY==-1の時はエラーとみなし、スクロールしない
                setting.scrollY = diagramFrame.getScrollY();
            }

            if (setting.scrollX > diagramView.getmWidth() - diagramFrame.getWidth() + 6) {
                setting.scrollX = diagramView.getmWidth() - diagramFrame.getWidth() + 6;
            }
            if (setting.scrollX < 0) {
                setting.scrollX = 0;
            }
            if (setting.scrollY > diagramView.getmHeight() - diagramFrame.getHeight() + 6) {
                setting.scrollY = diagramView.getmHeight() - diagramFrame.getHeight() + 6;
            }
            if (setting.scrollY < 0) {
                setting.scrollY = 0;
            }
            final int mScrollX = setting.scrollX;
            final int mScrollY = setting.scrollY;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //UI処理
                    try {
                        diagramFrame.scrollTo(mScrollX - diagramView.getScrollX(), mScrollY - diagramView.getScrollY());
                        stationView.scrollTo(0, mScrollY);
                        timeView.scrollTo(mScrollX, 0);
                    } catch (Exception e) {
                        SDlog.log(e);
                    }
                    return;
                }
            });
        } catch (Exception e) {
            SDlog.log(e);
        }
    }

    /**
     * 拡大率を設定する。
     */
    private void setScale() {
        if (setting.scaleX < 0.01f) {
            setting.scaleX = 0.01f;
        }
        if (setting.scaleY < 0.05f) {
            setting.scaleY = 0.05f;
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        if(lineFile ==null){
            return;
        }
        try{
            getAOdia().database.updateLineData(lineFile.filePath, diaIndex, setting.scrollX, setting.scrollY, (int) (setting.scaleX * 1000), (int) (setting.scaleY * 1000));
        }catch (Exception e){
            SDlog.log(e);
        }

    }

    public void fitVertical(){
        try {
            FrameLayout diagramFrame = getActivity().findViewById(R.id.diagramFrame);
            float frameSize = diagramFrame.getHeight() - 40;
            float nessTime = lineFile.getStationTime().get(lineFile.getStationNum() - 1);
            setting.scaleY = frameSize / nessTime;
            setScale();
            stationView.invalidate();
            diagramView.invalidate();
        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast("原因不明のエラーが発生しました");
        }

    }
    public void autoScroll(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // マルチスレッドにしたい処理 ここから
                autoScroll=true;
                while(autoScroll){//see stopAutoScroll()

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
                        SDlog.log(e);
                    }
                }
            }
        }).start();
    }

    /**
     * オートスクロールを停止させる
     */
    public  void stopAutoScroll(){
        autoScroll=false;
    }


    @NonNull
    @Override
    public String getName() {
        try {
            String line = lineFile.name;
            if (line.length() > 10) {
                line = line.substring(0, 10);
            }
            String dia = lineFile.diagram.get(diaIndex).name;
            if (dia.length() > 10) {
                dia = dia.substring(0, 10);
            }

            return line + "<" + dia + ">" + "ダイヤグラム";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public LineFile getLineFile() {
        return lineFile;
    }
}


