package com.kamelong.aodia.diagram;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.detabase.DBHelper;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

import static android.content.Context.MODE_PRIVATE;
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
 * このFragmentはダイヤグラムを表示するためのものです。
 * ダイヤグラムの表示は３つの部分に分かれています
 * １、駅部分：駅名を左端に固定表示する。固定表示とはいっても、ダイヤグラムのスクロールに合わせて上下移動をする。
 * ２、時刻部分：ダイヤグラム上部の時刻目盛を表示する。ダイヤグラムに合わせてスクロールする
 * ３、ダイヤグラム部分：このFragmentのメイン。ダイヤグラムを描画する。左上座標は他の二つのサイズによって決まるが、スクロール量はこのViewが基本となる。
 *
 * ・ダイヤグラムViewのスクロール
 * 優先度を設けているので確認すること
 * 優先度１：StationViewが入るFrameLayout(stationFrame)を左端に配置する。（画面左上を基準に配置する）。
 * 優先度２：TimeViewが入るFrameLayout(timeFrame)をStationFrameの右側、Fragment内上部に配置する（左端を基準に配置する）
 * 優先度３：DiagramViewが入るFrameLayoutが入るFrameLayout(diagramParent)をStationFrameの右側、timeFrameの下側に配置する
 *
 * この段階でdiagramFrameのサイズは高さが(Fragmentの高さ-TimeViewの高さ)、幅が(Fragmentの幅-StationViewの幅)になっているはずである
 * もしDiagramViewのサイズがdiagramParentのサイズより大きい場合、スクロール可能である
 * DiagramViewが２つの入れ子状のFrameLayoutに入っている理由は、スクロールした際に再描画させない目的と、ピンチイン等をしたときに画面のぶれを防止するためである
 *
 * このため、本来DiagramViewがスクロールされる場面では、DiagramFrameがスクロールされ、合わせてTimeViewのx方向、 StationViewのy方向がスクロールされる。
 *
 * ・拡大動作について
 * ピンチインアウトを行うと拡大縮小動作が行われる。
 * diagramFrameのスクロール位置を固定したまま、拡大率を変えるために、DiagramFrame内でDiagramViewをスクロールさせる点に注意が必要である
 *
 * @author KameLong
 *
 */
public class DiagramFragment extends AOdiaFragment {
    /**
     * このFragment全体のView
     */
    private View fragmentContainer=null;

    /**
     * このFragment内に内包されるView
     */
    private StationView stationView;
    private TimeView timeView;
    private DiagramView diagramView;

    /**
     * スクロール量、拡大サイズの保持に用いられる
     */
    private float scrollX;
    private float scrollY;
    private float scaleX;
    private float scaleY;
    /**
     * ダイヤ詳細設定のデータ
     */
    private DiagramSetting setting;
    /**
     * このFragmentで開いているデータについての情報
     */
    private int fileNum=0;
    private int diaNumber=0;
    private Handler handler = new Handler(); // (1)
    private boolean autoScroll=false;


    /**
     * DiagramFragment内のタッチジェスチャー
     */
    private final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {
                private boolean pinchFragX=false;
                private boolean pinchFragY=false;
                private boolean fling = false;

                private float startX1;
                private float startX0;
                private float startY1;
                private float startY0;

                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    //ピンチ、フリングを中断する
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

                    //長押ししたときはDiagramViewのfocusTrainを指定する
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
                        //１本指の時はスクロールする
                        pinchFragX=false;
                        pinchFragY=false;
                        DiagramFragment.this.scrollBy(vx,  vy);
                    }
                    if(motionEvent.getPointerCount()==2){

                        //二本指の時はピンチをする。
                        //２本の指が押しているポイントが変化しないように座標計算を行う
                        //なお２本の指がx方向y方向においてそれぞれ200ピクセル以下しか離れていないときは、その方向のピンチを無効化する
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
                        //３本指はピンチを無効化
                        pinchFragX=false;
                        pinchFragY=false;
                    }
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                    //フリングしたときは、別スレッドで等速フリング動作を行う
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


    /**
     * 初期設定をする
     * bundleで必要なデータを送ること
     */
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

    /**
     * Viewが生成れると各種Viewを生成し、追加する。
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try{
            try {
                diaFile = ((AOdiaActivity) getActivity()).diaFiles.get(fileNum);
            }catch(Exception e){
                SdLog.log(e);
                Toast.makeText(getActivity(),"なぜこの場所でエラーが起こるのか不明です。対策したいのですが、理由不明のため対策ができません。情報募集中です！",Toast.LENGTH_LONG);

            }
            if(diaFile==null){
                Toast.makeText(getActivity(),"ファイルが閉じられています",Toast.LENGTH_SHORT);
                onDestroy();
                return;
            }
        }catch(Exception e){
            Toast.makeText(getActivity(),"ファイルが閉じられています",Toast.LENGTH_SHORT);
            onDestroy();
            return;

        }

        //新しくDiagramSettingを作成する
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


            //デフォルトscaleはTrainNumに依存する
            if (diaFile.getTimeTable(0, 0).getTrainNum() < 100) {
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
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //このViewのタッチジェスチャーを保存
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

    /**
     * スクロールする部分の差分が与えられた時の処理
     * @param dx
     * @param dy
     */
    private void scrollBy(float dx,float dy){
        scrollX+=dx;
        scrollY+=dy;

        scrollTo();

    }

    /**
     * DiagramViewを任意の場所にスクロールする。
     * スクロールする場所はscrollX,scrollYに依存する。
     * またこのメソッドは別スレッドないから呼ばれるので、handler.post()を用いている
     *
     * またオートスクロールする場合は、本来のスクロール動作とは異なり、現時刻にスクロールする
     * @see #scrollX,#scrollY,#autoScroll
     */
    private void scrollTo(){
        try {
            if (autoScroll) {

                FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
                final int width = diagramFrame.getWidth();
                int nowTime = (int) (System.currentTimeMillis() % (24 * 60 * 60 * 1000)) / 1000;//システムの時間
                nowTime = nowTime - diaFile.getService().getDiagramStartTime() + 9 * 60 * 60;//時差
                if (nowTime < 0) {
                    nowTime = nowTime + 24 * 60 * 60;
                }
                if (nowTime > 24 * 60 * 60) {
                    nowTime = nowTime - 24 * 60 * 60;
                }
                scrollX =  (nowTime * scaleX / 60) - width / 2;
            }
            if (scrollY == -1) {
                //scrollY==-1の時はエラーとみなし、スクロールしない
                scrollY = findViewById(R.id.diagramFrame).getScrollY();
            }
            final FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);

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
                    //UI処理
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

    /**
     * 拡大率を設定する。
     */
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

    /**
     * 未実装関数
     * 指定の列車インデックスの列車の座標まで移動する
     * @param trainNum
     */
    public void moveToTrain(int trainNum){

    }

    /**
     * Fragment停止時に行う処理。
     *
     */
    @Override
    public void onStop(){
        try {
            setting.saveChange();
            SharedPreferences preference=getActivity().getSharedPreferences("AOdiaPreference",MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("RecentFilePath",diaFile.getFilePath());
            editor.putInt("RecentDiaNum",diaNumber);
            editor.putInt("RecentDirect",2);
            editor.apply();
            DBHelper db = new DBHelper(getActivity());
            db.updateLineData(diaFile.getFilePath(), diaNumber,(int)scrollX,(int)scrollY,(int)(scaleX*100f),(int)(scaleY*100f));
        }catch(Exception e){
            SdLog.log(e);
        }finally {
            super.onStop();

        }
    }

    /**
     * Fragment開始時に行われる処理。
     * データベースからの読み込みが行われる
     */
    @Override
    public void onStart() {
        super.onStart();
        try {
            DBHelper db = new DBHelper(getActivity());
            int[] scroll = db.getPositionData(db.getReadableDatabase(),  diaFile.getFilePath(), diaNumber, 2);
            scaleX=scroll[2]/100f;
            scaleY=scroll[3]/100f;
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

    /**
     * autoScrollを実行する。
     * 別スレッドを展開し、オートスクロール業務を行わせる
     */
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

    /**
     * ダイヤグラムのscaleYを今の画面サイズにちょうど収まるよう調整する。
     */
    public void fitVertical(){
        FrameLayout diagramFrame = (FrameLayout) findViewById(R.id.diagramFrame);
        float frameSize=diagramFrame.getHeight()-40;
        float nessTime=diaFile.getStation().getStationTime().get(diaFile.getStation().getStationNum()-1);
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
    public String fragmentName() {
        return "ダイヤグラム　" + diaFile.getDiaName(diaNumber) + "\n" + diaFile.getLineName();
    }
    @Override
    public String fragmentHash(){
        try{
            return "Diagram-"+diaFile.getFilePath()+"-"+diaNumber;
        }catch (Exception e){
            Toast.makeText(getActivity(),"error-DiagramFragment-fragmentHash-E1",Toast.LENGTH_SHORT).show();
            return "";
        }
    }

}
