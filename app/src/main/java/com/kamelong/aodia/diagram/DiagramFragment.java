package com.kamelong.aodia.Diagram;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

public class DiagramFragment extends AOdiaFragment {
    public StationView stationView;
    public TimeView timeView;
    public DiagramView diagramView;
    public DiagramSetting setting;

    public int diaNumber = 0;
    public DiaFile diaFile;
    Handler handler = new Handler();
    private boolean autoScroll=false;

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
                        diagramView.showDetail((int) motionEvent.getX() + (int) setting.scrollX - stationView.getWidth(), (int) motionEvent.getY() + (int) setting.scrollY - timeView.getHeight());
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
                            SdLog.log(e);
                        }
                        setScale();
                        diagramView.scrollBy((int) scrolldx, (int) scrolldy);
                        stationView.scrollBy(0, (int) scrolldy);
                        timeView.scrollBy((int) scrolldx, 0);
                        scrollTo();

                    }
                    if (motionEvent.getPointerCount() == 3) {
                        //３本指はピンチを無効化
                        pinchFragX = false;
                        pinchFragY = false;
                    }
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                    //フリングしたときは、別スレッドで等速フリング動作を行う
                    if (e2.getPointerCount() == 1) {
                        final float flingV = -v1 / 60;
                        fling = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (fling) {
                                    try {
                                        DiagramFragment.this.scrollBy(flingV, 0);

                                        Thread.sleep(16);
                                    } catch (Exception e) {
                                        SdLog.log(e);
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
            diaNumber = bundle.getInt("diaNumber");
            diaFile = getAOdiaActivity().diaFiles.get(bundle.getInt("fileNumber"));
            if(diaFile.getDiaNum()<=diaNumber){
                throw new Exception();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(),"ダイヤを開けませんでした。ダイヤの削除を行った可能性があります。",Toast.LENGTH_LONG).show();
            SdLog.log(e);
            onDestroy();
            getAOdiaActivity().killFragment(getAOdiaActivity().fragmentIndex);

        }

        fragmentContainer = inflater.inflate(R.layout.diagram, container, false);

        return fragmentContainer;
    }

    /**
     * Viewが生成れると各種Viewを生成し、追加する。
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
        setting = new DiagramSetting(getAOdiaActivity(), this, diaFile, diaNumber);
        setting.create(this);
            FrameLayout stationFrame = view.findViewById(R.id.station);
            FrameLayout timeFrame = view.findViewById(R.id.time);
            FrameLayout diaFrame = view.findViewById(R.id.diagramFrame);
            diagramView = new DiagramView(getAOdiaActivity(), setting, diaFile, diaNumber);
            stationView = new StationView(getAOdiaActivity(), setting, diaFile);
            timeView = new TimeView(getActivity(), setting, diaFile);
            stationFrame.addView(stationView);
            timeFrame.addView(timeView);
            diaFrame.addView(diagramView);

            setting.setDefault(getAOdiaActivity().database.getPositionData(diaFile.filePath,diaNumber,2));


            scrollTo();

        } catch (Exception e) {
            SdLog.log(e);
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
     * @param dx
     * @param dy
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
            if (autoScroll) {

                FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
                final int width = diagramFrame.getWidth();
                int nowTime = (int) (System.currentTimeMillis() % (24 * 60 * 60 * 1000)) / 1000;//システムの時間
                nowTime = nowTime - diaFile.diagramStartTime + 9 * 60 * 60;//時差
                if (nowTime < 0) {
                    nowTime = nowTime + 24 * 60 * 60;
                }
                if (nowTime > 24 * 60 * 60) {
                    nowTime = nowTime - 24 * 60 * 60;
                }
                setting.scrollX = (nowTime * setting.scaleX) - width / 2.0f;
            }
            if (setting.scrollY == -1) {
                //scrollY==-1の時はエラーとみなし、スクロールしない
                setting.scrollY = findViewById(R.id.diagramFrame).getScrollY();
            }
            final FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);

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
            final int mScrollX = (int) setting.scrollX;
            final int mScrollY = (int) setting.scrollY;
            System.out.println(mScrollX);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //UI処理
                    try {
                        findViewById(R.id.diagramFrame).scrollTo(mScrollX - diagramView.getScrollX(), mScrollY - diagramView.getScrollY());
                        stationView.scrollTo(0, mScrollY);
                        timeView.scrollTo(mScrollX, 0);
                    } catch (Exception e) {
                        SdLog.log(e);
                    }
                    return;
                }
            });
        } catch (Exception e) {
            SdLog.log(e);
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
        getAOdiaActivity().database.updateLineData(diaFile.filePath,diaNumber,(int)setting.scrollX,(int)setting.scrollY,(int)(setting.scaleX*100),(int)(setting.scaleY*100));
    }
    public void openTrainEdit(Train train){

    }
    public String fragmentName(){
            return diaFile.name+" "+diaFile.diagram.get(diaNumber).name+" "+"ダイヤグラム";
    }
    public void fitVertical(){
        FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
        float frameSize=diagramFrame.getHeight()-40;
        float nessTime=diaFile.getStationTime().get(diaFile.getStationNum()-1);
        setting.scaleY=frameSize/nessTime;
        setScale();
        stationView.invalidate();
        diagramView.invalidate();

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
                        SdLog.log(e);
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


}


