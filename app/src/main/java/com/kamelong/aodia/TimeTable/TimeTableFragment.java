package com.kamelong.aodia.TimeTable;

import android.graphics.Color;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.EditTrain.OnFragmentCloseListener;
import com.kamelong.aodia.EditTrain.OnTrainChangeListener;
import com.kamelong.aodia.EditTrain.TrainPasteDialog;
import com.kamelong.aodia.EditTrain.TrainPasteDialogInterface;
import com.kamelong.aodia.EditTrain.TrainTimeEditFragment;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.StationTimeTable.OnSortButtonClickListener;
import com.kamelong.aodia.StationTimeTable.StationInfoDialog;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

public class TimeTableFragment extends AOdiaFragmentCustom implements OnTrainChangeListener {
    public int lineIndex = 0;
    public int diaIndex = 0;
    public int direction = 0;
    public int editTrain = 0;
    private boolean fling = false;

    Handler handler = new Handler();

    private LineFile lineFile;
    private Diagram timetable;
    private TimeTableOptions options;

    private View fragmentContainer;
    private int shiftTime=-100000;
    private int shiftTime2=0;


    public TimeTableFragment() {
        super();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaIndex,directを更新する
            Bundle bundle = getArguments();
            diaIndex = bundle.getInt(AOdia.DIA_INDEX, 0);
            lineIndex = bundle.getInt(AOdia.FILE_INDEX, 0);
            direction = bundle.getInt(AOdia.DIRECTION, 0);
            editTrain = bundle.getInt(AOdia.TRAIN_INDEX, -1);
        } catch (Exception e) {
            SDlog.log(e);
        }
        fragmentContainer = inflater.inflate(R.layout.timetable_fragment, container, false);
        options = new TimeTableOptions(getActivity(),fragmentContainer,this);

        //タッチジェスチャーを実装
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        //flingを止める
                        fling = false;
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
                    }

                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                        //単純にスクロール
                        TimeTableFragment.this.scrollBy((int) vx, (int) vy);
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                        //flingを開始
                        final float flingV = -v1;
                        fling = true;
                        new Thread(new Runnable() {
                            @Override
                            //別スレッドでflingし続ける。fling=falseになると停止
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
                                        TimeTableFragment.this.scrollBy((int) (flingSpeed * 16 / 1000f), 0);
                                        Thread.sleep(16);
                                    } catch (Exception e) {
                                        fling = false;
                                        SDlog.log(e);
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
                //何もしない
                return false;
            }
            @Override
            public boolean onDoubleTap(MotionEvent motionEvent) {
                //タップ位置から駅を特定し、駅編集画面を出す
                findViewById(R.id.bottomContents2).setVisibility(View.GONE);
                //editTrainが設定されているときは設定を解除する
                if (editTrain >= 0&&editTrain<timetable.getTrainNum(direction)) {
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 255));
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 255));
                    editTrain = -1;
                }
                int y = (int) motionEvent.getY();
                int timeTabley = y + findViewById(R.id.trainTimeLinear).getScrollY() - findViewById(R.id.trainNameLinear).getHeight();
                int station = ((StationNameView) ((LinearLayout) findViewById(R.id.stationNameLinear)).getChildAt(0)).getStationFromY(timeTabley);
                if(direction==1){
                    station=lineFile.getStationNum()-station-1;
                }
                if (station >= 0 && station < lineFile.getStationNum()) {
                    StationInfoDialog dialog = new StationInfoDialog(getContext(), lineFile, diaIndex, direction, station);
                    dialog.show();
                    dialog.setOnSortListener(new OnSortButtonClickListener() {
                        @Override
                        public void onSortCicked(int stationIndex) {
                            lineFile.sortTrain(diaIndex, direction, stationIndex);
                            TimeTableFragment.this.allTrainChange();

                        }
                    });
                    return true;
                }
                TimeTableActionDialog dialog=new TimeTableActionDialog(getContext(),lineFile.getDiagram(diaIndex),direction,TimeTableFragment.this);
                dialog.show();


                return true;
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
        return fragmentContainer;
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        try {
            lineFile = getAOdia().getLineFile(lineIndex);
            if(lineFile!=null) {
                timetable = lineFile.getDiagram(diaIndex);
            }
        }catch(Exception e){
            SDlog.log(e);
        }
        if(lineFile==null){
            Toast.makeText(getActivity(),"ダイヤファイルが見つかりませんでした",Toast.LENGTH_LONG).show();
            getAOdia().killFragment(this);
            return;
        }
        init();
        super.onViewCreated(view, savedInstanceState);

    }
    public void onStart(){
        super.onStart();
        if(lineFile==null){
            getAOdia().killFragment(this);
            return;
        }
        if(editTrain<0||editTrain>=timetable.trains[direction].size()) {
        }else {
            openTrainEditFragment(timetable.trains[direction].get(editTrain));
            final LinearLayout timeList=findViewById(R.id.trainTimeLinear);
            if(timeList.getChildCount()>0){
                try {
                    int scrollX=editTrain*options.getTrainWidth()*TimeTableDefaultView.textSize/2;
                    scrollTo(scrollX,0);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void init() {
        try {
            //列車の最後に空白列車を入れる
            if(timetable.getTrainNum(direction)==0||!timetable.getTrain(direction,timetable.getTrainNum(direction)-1).isnull()){
                timetable.addTrain(direction,timetable.getTrainNum(direction),new Train(lineFile,direction));
            }
            FrameLayout lineNameFrame = findViewById(R.id.lineNameFrame);
            LineNameView lineNameView = new LineNameView(getActivity(), options);
            lineNameFrame.removeAllViews();
            lineNameFrame.addView(lineNameView);
            LinearLayout stationNameLinea = findViewById(R.id.stationNameLinear);
            StationNameView stationNameView = new StationNameView(getActivity(),options, lineFile, direction);
            stationNameLinea.removeAllViews();
            stationNameLinea.addView(stationNameView);

            LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
            trainNameLinea.removeAllViews();
            TrainNameView[] trainNameViews = new TrainNameView[timetable.getTrainNum(direction)];
            for (int i = 0; i < trainNameViews.length; i++) {

                trainNameViews[i] = new TrainNameView(getActivity(),this,options,timetable.getTrain(direction,i));
                trainNameLinea.addView(trainNameViews[i]);
            }
            LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            trainTimeLinear.removeAllViews();
            TrainTimeView[] trainTimeViews = new TrainTimeView[timetable.getTrainNum(direction)];
            for (int i = 0; i < trainNameViews.length; i++) {
                trainTimeViews[i] = new TrainTimeView(getActivity(), options, lineFile,timetable.getTrain(direction,i),direction);
                trainTimeLinear.addView(trainTimeViews[i]);
            }
            int[] pos = getAOdia().database.getPositionData(lineFile.filePath, diaIndex, direction);
            scrollTo(pos[0], pos[1]);

        }catch(Exception e){
            SDlog.log(e);
        }
    }


    private void scrollBy(int dx, int dy) {
        final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
        int scrollX = trainTimeLinear.getScrollX() + dx;
        int scrollY = trainTimeLinear.getScrollY() + dy;
        scrollTo(scrollX,scrollY);
    }
    private void scrollTo(int scrollX, int scrollY) {
        /*
         * AOdia時刻表スクロールは３枚のパネルを連動して動かす必要がある。
         * trainTimeを基準にして
         * x方向のスクロールとtrainNameのスクロールは一致する必要があり
         * y方向のスクロールとstationNameのスクロールは一致する必要がある
         *
         */
        try {
            final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            final FrameLayout trainTimeFrame = findViewById(R.id.trainTimeFrame);
            final LinearLayout trainNameLinear = findViewById(R.id.trainNameLinear);
            final LinearLayout stationNameLinear = findViewById(R.id.stationNameLinear);
            if(trainTimeLinear.getChildCount()==0){
                return;
            }
            //スクロール量の限界設定
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
            //run内で使うためにfinal化
            final int mscrollX=scrollX;
            final int mscrollY=scrollY;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        trainTimeLinear.scrollTo(mscrollX, mscrollY);
                        trainNameLinear.scrollTo(mscrollX, 0);
                        stationNameLinear.scrollTo(0, mscrollY);
                    }catch(Exception e){
                        SDlog.log(e);
                    }
                }
            });
        } catch (Exception e) {
            SDlog.log(e);
            fling=false;
            onStop();
        }
    }

    private <T extends View>T findViewById(int id){
        return fragmentContainer.findViewById(id);

    }

    @Override
    @NonNull
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
            if (direction == 0) {
                return line + "<" + dia + ">" + getString(R.string.downwardTimeTable);
            } else {
                return line + "<" + dia + ">" + getString(R.string.upwardTimeTable);
            }
        } catch (Exception e) {
            if (direction == 0) {
                return getString(R.string.downwardTimeTable);
            } else {
                return getString(R.string.upwardTimeTable);
            }
        }

    }
    public void openTrainEditFragment(Train train){
        final int trainIndex=timetable.getTrainIndex(direction,train);
        findViewById(R.id.bottomContents2).setVisibility(View.VISIBLE);
        TrainTimeEditFragment fragment=new TrainTimeEditFragment();
        Bundle args=new Bundle();
        args.putInt(AOdia.FILE_INDEX, lineIndex);
        args.putInt(AOdia.DIA_INDEX, diaIndex);
        args.putInt(AOdia.DIRECTION, direction);
        args.putInt(AOdia.TRAIN_INDEX, trainIndex);
        fragment.setArguments(args);

        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomContents,fragment);
        fragmentTransaction.commit();
        if(editTrain>=0&&editTrain<timetable.getTrainNum(direction)){
            ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
            ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
        }
        ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(trainIndex).setBackgroundColor(Color.rgb(255,255,200));
        ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(trainIndex).setBackgroundColor(Color.rgb(255,255,200));
        editTrain=trainIndex;

        fragment.setOnTrainChangeListener(this);
        fragment.setOnFragmentCloseListener(new OnFragmentCloseListener() {
            @Override
            public void fragmentClose() {
//                findViewById(R.id.bottomContents).setVisibility(View.GONE);
//                if(!diagram.trains[direction].get(diagram.trains[direction].size()-1).isnull()){
//                    diagram.trains[direction].add(new Train(lineFile,direction));
//
//                }
//                trainReset();
                if(editTrain>=0){
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255,255,255));
                }
                editTrain=-1;
            }
        });
    }
    public void trainCopy(){
        LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
        ArrayList<Train> copyTrain=new ArrayList<>();

        for(int i=0;i<trainNameLinea.getChildCount();i++){
            TrainNameView nameView=(TrainNameView)trainNameLinea.getChildAt(i);
            if(nameView.selected){
                copyTrain.add(timetable.getTrain(direction,i));
                nameView.setSelected(false);
            }
        }
        ((MainActivity)getActivity()).getAOdia().copyTrain=copyTrain;
        if(copyTrain.size()==0){
            SDlog.toast("列車が選択されていません");
        }else{
            SDlog.toast("列車をコピーしました");
        }
        shiftTime=-100000;
    }
    public void trainPaste() {
        final ArrayList<Train> copyTrain = ((MainActivity) getActivity()).getAOdia().copyTrain;
        if (copyTrain.size() == 0) {
            SDlog.toast("列車がコピーされていません");
            return;
        }
        if (lineFile.getStationNum() != copyTrain.get(0).getStationNum()) {
            SDlog.toast("コピー元と駅数が一致しません");
            return;
        }
        if(shiftTime==-100000){
            final TrainPasteDialog dialog=new TrainPasteDialog(getContext());
            dialog.setTrainPasteDialogInterface(new TrainPasteDialogInterface() {
                @Override
                public void onOkClicked(int shiftTime) {
                    TimeTableFragment.this.shiftTime=shiftTime;
                    shiftTime2=shiftTime;
                    LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
                    int pasteIndex = -1;
                    for (int i = 0; i < trainNameLinea.getChildCount(); i++) {
                        TrainNameView nameView = (TrainNameView) trainNameLinea.getChildAt(i);
                        if (nameView.selected) {
                            pasteIndex = i;
                            break;
                        }
                    }
                    if(pasteIndex==-1){
                        pasteIndex=timetable.getTrainNum(direction);
                    }
                    for (Train cTrain : copyTrain){
                        timetable.addTrain(direction,pasteIndex, cTrain.clone(lineFile));
                        timetable.getTrain(direction,pasteIndex).shiftTime(shiftTime);

                        pasteIndex++;
                    }
                    dialog.dismiss();
                    final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
                    final int scrollX = trainTimeLinear.getScrollX();
                    final int scrollY = trainTimeLinear.getScrollY();
                    getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

                    init();
                    SDlog.toast("列車を貼り付けました");
                    if(pasteIndex<timetable.getTrainNum(direction)) {
                        ((TrainNameView) trainNameLinea.getChildAt(pasteIndex)).selected = true;
                    }
                }
                @Override
                public void onCancelClicked() {
                    dialog.dismiss();

                }
            });
            dialog.show();
        }else{
            shiftTime2+=shiftTime;
            LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
            int pasteIndex = -1;
            for (int i = 0; i < trainNameLinea.getChildCount(); i++) {
                TrainNameView nameView = (TrainNameView) trainNameLinea.getChildAt(i);
                if (nameView.selected) {
                    pasteIndex = i;
                    break;
                }
            }
            if(pasteIndex==-1){
                pasteIndex=timetable.getTrainNum(direction);
            }
            for (Train cTrain : copyTrain){
                timetable.addTrain(direction,pasteIndex, cTrain.clone(lineFile));
                timetable.getTrain(direction,pasteIndex).shiftTime(shiftTime2);
                pasteIndex++;
            }
            final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            final int scrollX = trainTimeLinear.getScrollX();
            final int scrollY = trainTimeLinear.getScrollY();
            getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

            init();
            SDlog.toast("列車を貼り付けました");
            if(pasteIndex>=0&&pasteIndex<timetable.getTrainNum(direction)) {
                ((TrainNameView) trainNameLinea.getChildAt(pasteIndex)).selected = true;
            }


        }

    }
    public void trainCut(){
        LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
        ArrayList<Train> copyTrain=new ArrayList<>();

        for(int i=0;i<trainNameLinea.getChildCount();i++){
            TrainNameView nameView=(TrainNameView)trainNameLinea.getChildAt(i);
            if(nameView.selected){
                copyTrain.add(timetable.getTrain(direction,i));
            }
        }
        for(Train train:copyTrain){
            timetable.deleteTrain(train);
        }
        ((MainActivity)getActivity()).getAOdia().copyTrain=copyTrain;
        if(copyTrain.size()==0){
            SDlog.toast("列車が選択されていません");
        }else{
            SDlog.toast("列車を切り取りました");
        }
        final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
        final int scrollX = trainTimeLinear.getScrollX();
        final int scrollY = trainTimeLinear.getScrollY();
        getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

        init();
        shiftTime=-100000;

    }

    @Override

    public void trainChanged(Train train) {
        LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
        int trainViewIndex = 0;
        for (trainViewIndex = 0; trainViewIndex < trainNameLinea.getChildCount(); trainViewIndex++) {
            if (((TrainNameView) trainNameLinea.getChildAt(trainViewIndex)).train == train) {
                break;
            }
        }
        if (trainViewIndex == trainNameLinea.getChildCount()) {
            return;
        }
        trainNameLinea.removeViewAt(trainViewIndex);
        trainNameLinea.addView(new TrainNameView(getActivity(), TimeTableFragment.this, options, timetable.getTrain(direction, trainViewIndex)), trainViewIndex);
        LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
        trainTimeLinear.removeViewAt(trainViewIndex);
        trainTimeLinear.addView(new TrainTimeView(getActivity(), options, lineFile, lineFile.getTrain(diaIndex, direction, trainViewIndex), direction), trainViewIndex);
        if (editTrain >= 0) {
            ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
            ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(editTrain).setBackgroundColor(Color.rgb(255, 255, 200));
        }



        final int scrollX = trainTimeLinear.getScrollX();
        final int scrollY = trainTimeLinear.getScrollY();
        getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

        init();

    }

    @Override
    public void allTrainChange() {
        final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
        final int scrollX = trainTimeLinear.getScrollX();
        final int scrollY = trainTimeLinear.getScrollY();
        getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

        init();
    }


    public void invalidate(){
         final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
         final int scrollX = trainTimeLinear.getScrollX();
         final int scrollY = trainTimeLinear.getScrollY();
         getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

         init();
     }

     @Override
    public void onStop() {
        if(lineFile==null){
            return;
        }
        try {
            final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            final int scrollX = trainTimeLinear.getScrollX();
            final int scrollY = trainTimeLinear.getScrollY();
            getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);
        }catch (Exception e){
            SDlog.log(e);
        }

         super.onStop();
     }
     @Override
    public LineFile getLineFile(){
        return lineFile;
     }

}
