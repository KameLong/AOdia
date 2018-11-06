package com.kamelong.aodia.TimeTable;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.TimeTable.EditTrain.OnFragmentCloseListener;
import com.kamelong.aodia.TimeTable.EditTrain.OnTrainChangeListener;
import com.kamelong.aodia.TimeTable.EditTrain.TrainTimeEditFragment;

public class TimeTableFragment extends AOdiaFragment {
    public int fileNum = 0;
    public int diaNum = 0;
    public int direction = 0;
    public int editTrain=-1;

    private boolean fling = false;
    Handler handler = new Handler();

    private Diagram diagram =null;

    TimeTableSetting setting;

    public TimeTableFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setting=new TimeTableSetting();
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            diaNum = bundle.getInt("diagramIndex",0);
            fileNum=bundle.getInt("fileIndex",0);
            direction =  bundle.getInt("direction",0);
            editTrain=bundle.getInt("trainEdit",-1);
        }catch(Exception e){
            SdLog.log(e);
        }
        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.time_table, container, false);
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.OnGestureListener() {

                    private float flingV = 0;

                    @Override
                    public boolean onDown(MotionEvent motionEvent) {

                        fling=false;
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent motionEvent) {
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent motionEvent) {

                        int y=(int)motionEvent.getY();
                        int x=(int)motionEvent.getX();
                        int timeTabley=y+findViewById(R.id.trainTimeLinear).getScrollY()-findViewById(R.id.trainNameLinear).getHeight();
                        int timeTablex=x+findViewById(R.id.trainTimeLinear).getScrollX()-findViewById(R.id.stationNameLinear).getWidth();
                        int station=((StationNameView)((LinearLayout)findViewById(R.id.stationNameLinear)).getChildAt(0)).getStationFromY(timeTabley);
                        final int train=timeTablex/(((LinearLayout)findViewById(R.id.trainTimeLinear)).getChildAt(0).getWidth());
                        SdLog.log("timeTableLongPress", station);
                        SdLog.log("timeTableLongPress", train);
                        if(train<0){
                            return;
                        }
                        if(train>=diaFile.getTrainSize(diaNum,direction)){
                            return;
                        }
                        if(station<0){
                            return;
                        }

                        SdLog.log("timeTableLongPress", diaFile.station.get(station).name);
                        openTrainEditFragment(train);



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
        gesture.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                findViewById(R.id.bottomContents).setVisibility(View.GONE);
                if(editTrain>=0){
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                }
                editTrain=-1;
                int y=(int)motionEvent.getY();
                int timeTabley=y+findViewById(R.id.trainTimeLinear).getScrollY()-findViewById(R.id.trainNameLinear).getHeight();
                int station=((StationNameView)((LinearLayout)findViewById(R.id.stationNameLinear)).getChildAt(0)).getStationFromY(timeTabley);
                if(station<0){
                    return false;
                }
                StationInfoDialog dialog = new StationInfoDialog(getActivity(),TimeTableFragment.this, diaFile,fileNum,diaNum,direction,station);
                dialog.setOnTrainChangeListener(new OnTrainChangeListener() {
                    @Override
                    public void trainChanged() {}
                    @Override
                    public void trainReset() {
                        TimeTableFragment.this.trainReset();
                    }
                });
                dialog.show();
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }
        });
        fragmentContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        FloatingActionButton fab0=(FloatingActionButton)findViewById(R.id.fabTrainTime);
        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diagram.reNewOperation();
                init();

            }
        });

        return fragmentContainer;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            diaFile = getAOdiaActivity().diaFiles.get(fileNum);
            diagram =diaFile.diagram.get(diaNum);
        }catch(Exception e){
            SdLog.log(e);
            Toast.makeText(getActivity(),"なぜこの場所でエラーが起こるのか不明です。対策したいのですが、理由不明のため対策ができません。情報募集中です！",Toast.LENGTH_LONG).show();
        }
        if(diaFile==null){
            Toast.makeText(getActivity(),"ダイヤファイルが見つかりませんでした",Toast.LENGTH_LONG).show();
            getAOdiaActivity().killFragment(getAOdiaActivity().fragmentIndex);
            return;
        }
        setting.create(this);
        init();
        super.onViewCreated(view, savedInstanceState);
        if(editTrain<0||editTrain>=diagram.trains[direction].size()) {
            try {
                moveToTrainIndex(getAOdiaActivity().database.getPositionData(diaFile.filePath, diaNum, direction)[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else {
            openTrainEditFragment(editTrain);
            moveToTrainIndex(editTrain);
        }
    }
    private void init() {
        try {
            FrameLayout lineNameFrame = (FrameLayout) findViewById(R.id.lineNameFrame);
            LineNameView lineNameView = new LineNameView(getActivity(), diaFile, diaNum);
            lineNameFrame.removeAllViews();
            lineNameFrame.addView(lineNameView);
            LinearLayout stationNameLinea = (LinearLayout) findViewById(R.id.stationNameLinear);
            StationNameView stationNameView = new StationNameView(getActivity(), diaFile, direction);
            stationNameLinea.removeAllViews();
            stationNameLinea.addView(stationNameView);

            LinearLayout trainNameLinea = (LinearLayout) findViewById(R.id.trainNameLinear);
            trainNameLinea.removeAllViews();
            TrainNameView[] trainNameViews = new TrainNameView[diaFile.getTrainSize(diaNum,direction)];
            for (int i = 0; i < trainNameViews.length; i++) {
                trainNameViews[i] = new TrainNameView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,i));
                trainNameLinea.addView(trainNameViews[i]);
            }
            LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
            trainTimeLinear.removeAllViews();
            TrainTimeView[] trainTimeViews = new TrainTimeView[diaFile.getTrainSize(diaNum,direction)];
            for (int i = 0; i < trainNameViews.length; i++) {
                trainTimeViews[i] = new TrainTimeView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,i),direction);
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
        onStop();
    }
    public int getNowTrainIndex(){
        try {
            final LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
            int xPoint = trainTimeLinear.getScrollX();
            return xPoint / ((TrainTimeView) trainTimeLinear.getChildAt(0)).getXsize();
        }catch (Exception e){
            return 0;
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        try {
            getAOdiaActivity().database.updateLineData(diaFile.filePath, diaNum, direction, getNowTrainIndex());
        }catch (Exception e){

        }
    }
    public void moveToTrainIndex(int trainIndex){
        if(trainIndex<0){
            return;
        }
        final LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
        if(trainIndex>trainTimeLinear.getChildCount()){
            trainIndex=trainTimeLinear.getChildCount()-1;
        }
        try {
            scrollTo(((TrainTimeView) trainTimeLinear.getChildAt(0)).getXsize() * trainIndex, trainTimeLinear.getScrollY());
        }catch (Exception e){
            Toast.makeText(getAOdiaActivity(),"時刻表内に列車がありません",Toast.LENGTH_LONG).show();
        }
    }
    public void sortTrain(int stationIndex){
        if(stationIndex>=0&&stationIndex< diaFile.getStationNum()){
            diagram.sortTrain(direction,stationIndex);
            this.init();
        }
    }
    public void openTrainEditFragment(final int trainIndex){
        findViewById(R.id.bottomContents).setVisibility(View.VISIBLE);
        TrainTimeEditFragment fragment=new TrainTimeEditFragment();
        Bundle args=new Bundle();
        args.putInt("fileNumber",fileNum);
        args.putInt("diaNumber", diaNum);
        args.putInt("direction", direction);
        args.putInt("trainNumber", trainIndex);
        fragment.setArguments(args);

        FragmentManager fragmentManager=getAOdiaActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomContents,fragment);
        fragmentTransaction.commit();
        if(editTrain>=0){
            ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
            ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
        }
        ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(trainIndex).setBackgroundColor(Color.rgb(255,255,200));
        ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(trainIndex).setBackgroundColor(Color.rgb(255,255,200));
        editTrain=trainIndex;

        fragment.setOnTrainChangeListener(new OnTrainChangeListener() {
            @Override
            public void trainChanged() {
                LinearLayout trainNameLinea = (LinearLayout) findViewById(R.id.trainNameLinear);
                if(trainIndex<0||trainIndex>=trainNameLinea.getChildCount()){
                    return;
                }
                trainNameLinea.removeViewAt(trainIndex);
                trainNameLinea.addView(new TrainNameView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,trainIndex)),trainIndex);
                LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
                trainTimeLinear.removeViewAt(trainIndex);
                trainTimeLinear.addView(new TrainTimeView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,trainIndex),direction),trainIndex);
                if(editTrain>=0) {
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
                }
            }

            @Override
            public void trainReset() {
                LinearLayout trainNameLinea = (LinearLayout) findViewById(R.id.trainNameLinear);
                trainNameLinea.removeAllViews();
                LinearLayout trainTimeLinear = (LinearLayout) findViewById(R.id.trainTimeLinear);
                trainTimeLinear.removeAllViews();
                for (int i = 0; i < diaFile.diagram.get(diaNum).trains[direction].size(); i++) {
                    trainTimeLinear.addView(new TrainTimeView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,i),direction));
                    trainNameLinea.addView(new TrainNameView(getActivity(), diaFile, diaFile.getTrain(diaNum,direction,i)));
                }

            }
        });
        fragment.setOnFragmentCloseListener(new OnFragmentCloseListener() {
            @Override
            public void fragmentClose() {
                findViewById(R.id.bottomContents).setVisibility(View.GONE);
                if(!diagram.trains[direction].get(diagram.trains[direction].size()-1).isnull()){
                    diagram.trains[direction].add(new Train(diaFile,direction));

                }
                trainReset();
                if(editTrain>=0){
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                }
                editTrain=-1;
            }
        });
    }

    public String fragmentName(){
        if(direction==0){
            return diaFile.name+" "+diagram.name+" "+"下り時刻表";
        }else{
            return diaFile.name+" "+diagram.name+" "+"上り時刻表";
        }
    }
    public void trainReset(){
        diagram.reNewOperation();
        init();
        if(editTrain>=0){
            if(editTrain>=0) {
                ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
                ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
            }
        }


    }
}
