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
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

public class TimeTableFragment extends AOdiaFragmentCustom implements OnTrainChangeListener , GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {
    public static final String FRAGMENT_NAME="TimeTableFragment";

    public int lineIndex = 0;
    public int diaIndex = 0;
    public int direction = 0;
    public Train editTrain=null;

    public int editTrainIndex=-1;

    private boolean fling = false;

    Handler handler = new Handler();

    private LineFile lineFile;
    //timetableで使用する列車を格納する。
    private ArrayList<Train>trains;
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
            editTrainIndex = bundle.getInt(AOdia.TRAIN_INDEX, -1);
        if(savedInstanceState!=null){
            editTrainIndex = savedInstanceState.getInt(AOdia.TRAIN_INDEX, -1);
        }
        fragmentContainer = inflater.inflate(R.layout.timetable_fragment, container, false);
        options = new TimeTableOptions(getActivity(), fragmentContainer, this);

        //タッチジェスチャーを実装
        final GestureDetector gesture = new GestureDetector(getActivity(), this);
        fragmentContainer.setOnTouchListener((v, event) -> gesture.onTouchEvent(event));
        } catch (Exception e) {
            getAOdia().killFragment(this);
            SDlog.log(e);
        }
        return fragmentContainer;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        try {
            lineFile = getAOdia().getLineFile(lineIndex);
        }catch(Exception e){
            SDlog.log(e);
        }
        if(lineFile==null){
            Toast.makeText(getActivity(),"ダイヤファイルが見つかりませんでした",Toast.LENGTH_LONG).show();
            getAOdia().killFragment(this);
            return;
        }
        super.onViewCreated(view, savedInstanceState);

    }
    public void onStart(){
        super.onStart();
        if(lineFile==null){
            getAOdia().killFragment(this);
            return;
        }
        //列車の最後に空白列車を入れる
        if(lineFile.getDiagram(diaIndex).getTrainNum(direction)==0||!lineFile.getDiagram(diaIndex).getTrain(direction,lineFile.getDiagram(diaIndex).getTrainNum(direction)-1).isnull()){
            lineFile.getDiagram(diaIndex).addTrain(direction,lineFile.getDiagram(diaIndex).getTrainNum(direction),new Train(lineFile,direction));
        }
        trains=lineFile.getDiagram(diaIndex).trains[direction];

        if(editTrainIndex<0||editTrainIndex>=trains.size()) {
        }else {
            editTrain=trains.get(editTrainIndex);
            openTrainEditFragment(editTrain);
            final LinearLayout timeList=findViewById(R.id.trainTimeLinear);
            if(timeList.getChildCount()>0){
                try {
                    int scrollX=editTrainIndex*options.getTrainWidth()*TimeTableDefaultView.textSize/2;
                    scrollTo(scrollX,0);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        init();
    }
    private void init() {
        try {
            final FrameLayout lineNameFrame = findViewById(R.id.lineNameFrame);
            final LineNameView lineNameView = new LineNameView(getActivity(), options);
            lineNameFrame.removeAllViews();
            lineNameFrame.addView(lineNameView);
            final LinearLayout stationNameLinea = findViewById(R.id.stationNameLinear);
            final StationNameView stationNameView = new StationNameView(getActivity(),options, lineFile, direction);
            stationNameLinea.removeAllViews();
            stationNameLinea.addView(stationNameView);

            LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
            trainNameLinea.removeAllViews();
            trains=lineFile.getDiagram(diaIndex).trains[direction];

            for (Train train :trains) {
                if(lineFile.getTrainType(train.type).showInTimeTable){
                    TrainNameView trainNameView = new TrainNameView(getActivity(), this, options, train);
                    trainNameLinea.addView(trainNameView);
                }
            }
            LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            trainTimeLinear.removeAllViews();
            for (Train train :trains) {
                if(lineFile.trainType.get(train.type).showInTimeTable){
                    TrainTimeView trainTimeView = new TrainTimeView(getActivity(), options, lineFile,train,direction,this);
                    trainTimeLinear.addView(trainTimeView);
                }
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
            handler.post(() -> {
                try {
                    trainTimeLinear.scrollTo(mscrollX, mscrollY);
                    trainNameLinear.scrollTo(mscrollX, 0);
                    stationNameLinear.scrollTo(0, mscrollY);
                }catch(Exception e){
                    SDlog.log(e);
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

    @Override
    public String getHash() {
        return FRAGMENT_NAME+"-"+diaIndex+"-"+direction;
    }

    public void openTrainEditFragment(Train train){
        findViewById(R.id.bottomContents2).setVisibility(View.VISIBLE);
        TrainTimeEditFragment fragment=new TrainTimeEditFragment();
        Bundle args=new Bundle();
        args.putInt(AOdia.FILE_INDEX, getAOdia().getLineFileIndex(lineFile));
        args.putInt(AOdia.DIA_INDEX, diaIndex);
        args.putInt(AOdia.DIRECTION, direction);
        args.putInt(AOdia.TRAIN_INDEX, trains.indexOf(train));
        fragment.setArguments(args);

        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottomContents,fragment);
        fragmentTransaction.commit();
        final LinearLayout trainNameLinear=findViewById(R.id.trainNameLinear);
        if(editTrain!=null){
            for(int i=0;i< trainNameLinear.getChildCount();i++){
                if(((TrainNameView)trainNameLinear.getChildAt(i)).train==editTrain){
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                }
                if(((TrainNameView)trainNameLinear.getChildAt(i)).train==train){
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,200));
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,200));
                }
            }
        }
        editTrain=train;

        fragment.setOnTrainChangeListener(this);
        fragment.setOnFragmentCloseListener(() -> {
            if(editTrain!=null){
                for(int i=0;i< trainNameLinear.getChildCount();i++){
                    if(((TrainNameView)trainNameLinear.getChildAt(i)).train==editTrain){
                        ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                        ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                    }
                }
            }
            editTrain=null;
        });
    }
    public void trainCopy(){
        LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
        ArrayList<Train> copyTrain=new ArrayList<>();

        for(int i=0;i<trainNameLinea.getChildCount();i++){
            TrainNameView nameView=(TrainNameView)trainNameLinea.getChildAt(i);
            if(nameView.selected){
                copyTrain.add(nameView.train);
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
                    try{

                    TimeTableFragment.this.shiftTime=shiftTime;
                    shiftTime2=shiftTime;
                    LinearLayout trainNameLinea = findViewById(R.id.trainNameLinear);
                    int pasteIndex = -1;
                    for (int i = 0; i < trainNameLinea.getChildCount(); i++) {
                        TrainNameView nameView = (TrainNameView) trainNameLinea.getChildAt(i);
                        if (nameView.selected) {
                            pasteIndex = trains.indexOf(nameView.train);
                            break;
                        }
                    }
                    if(pasteIndex==-1){
                        pasteIndex=trains.size();
                    }
                    for (Train cTrain : copyTrain){
                        lineFile.getDiagram(diaIndex).addTrain(direction,pasteIndex, cTrain.clone(lineFile));
                        trains.get(pasteIndex).shiftTime(shiftTime);

                        pasteIndex++;
                    }
                    dialog.dismiss();
                    final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
                    final int scrollX = trainTimeLinear.getScrollX();
                    final int scrollY = trainTimeLinear.getScrollY();
                    getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

                    init();
                    SDlog.toast("列車を貼り付けました");
                    if(pasteIndex<trains.size()) {
                        ((TrainNameView) trainNameLinea.getChildAt(pasteIndex)).selected = true;
                    }
                    }catch(Exception e){
                        SDlog.log(e);
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
                pasteIndex=trains.size();
            }
            for (Train cTrain : copyTrain){
                lineFile.getDiagram(diaIndex).addTrain(direction,pasteIndex, cTrain.clone(lineFile));
                trains.get(pasteIndex).shiftTime(shiftTime2);
                pasteIndex++;
            }
            final LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
            final int scrollX = trainTimeLinear.getScrollX();
            final int scrollY = trainTimeLinear.getScrollY();
            getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

            init();
            SDlog.toast("列車を貼り付けました");
            if(pasteIndex>=0&&pasteIndex<trains.size()) {
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
                copyTrain.add(trains.get(i));
            }
        }
        for(Train train:copyTrain){
            lineFile.getDiagram(diaIndex).deleteTrain(train);
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
        LinearLayout trainNameLinear = findViewById(R.id.trainNameLinear);
        int trainViewIndex = 0;
        for (trainViewIndex = 0; trainViewIndex < trainNameLinear.getChildCount(); trainViewIndex++) {
            if (((TrainNameView) trainNameLinear.getChildAt(trainViewIndex)).train == train) {
                break;
            }
        }
        if (trainViewIndex == trainNameLinear.getChildCount()) {
            return;
        }
        trainNameLinear.removeViewAt(trainViewIndex);
        trainNameLinear.addView(new TrainNameView(getActivity(), TimeTableFragment.this, options, trains.get( trainViewIndex)), trainViewIndex);
        LinearLayout trainTimeLinear = findViewById(R.id.trainTimeLinear);
        trainTimeLinear.removeViewAt(trainViewIndex);
        trainTimeLinear.addView(new TrainTimeView(getActivity(), options, lineFile, lineFile.getTrain(diaIndex, direction, trainViewIndex), direction,this), trainViewIndex);
        if(editTrain!=null){
            for(int i=0;i< trainNameLinear.getChildCount();i++){
                if(((TrainNameView)trainNameLinear.getChildAt(i)).train==editTrain){
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                }
            }
        }
        editTrain=null;



        final int scrollX = trainTimeLinear.getScrollX();
        final int scrollY = trainTimeLinear.getScrollY();
        getAOdia().database.updateLineData(lineFile.filePath, diaIndex, direction, scrollX, scrollY);

        init();

    }

    @Override
    public void allTrainChange() {
        if(!trains.get(trains.size()-1).isnull()){
            trains.add(new Train(lineFile,direction));
        }
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
        super.onStop();
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

    }
    @Override
    public LineFile getLineFile(){

        return lineFile;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            if(editTrain!=null) {
                outState.putInt(AOdia.TRAIN_INDEX, trains.indexOf(editTrain));
            }else{
                outState.putInt(AOdia.TRAIN_INDEX, -1);

            }
        }catch (Exception e){
            SDlog.log(e);
            outState.putInt(AOdia.TRAIN_INDEX, -1);
        }
        super.onSaveInstanceState(outState);
    }
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
        //別スレッドでflingし続ける。fling=falseになると停止
        new Thread(() -> {
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
        }).start();
        return false;
    }

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
        final LinearLayout trainNameLinear=findViewById(R.id.trainNameLinear);
        if(editTrain!=null){
            for(int i=0;i< trainNameLinear.getChildCount();i++){
                if(((TrainNameView)trainNameLinear.getChildAt(i)).train==editTrain){
                    ((LinearLayout) findViewById(R.id.trainTimeLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                    ((LinearLayout) findViewById(R.id.trainNameLinear)).getChildAt(i).setBackgroundColor(Color.rgb(255,255,255));
                }
            }
        }
        editTrain=null;
        int y = (int) motionEvent.getY();
        int timeTabley = y + findViewById(R.id.trainTimeLinear).getScrollY() - findViewById(R.id.trainNameLinear).getHeight();
        if(timeTabley>findViewById(R.id.trainTimeLinear).getScrollY()) {
            int station = ((StationNameView) ((LinearLayout) findViewById(R.id.stationNameLinear)).getChildAt(0)).getStationFromY(timeTabley);
            if (direction == 1) {
                station = lineFile.getStationNum() - station - 1;
            }
            if (station >= 0 && station < lineFile.getStationNum()) {
                StationDialog dialog = new StationDialog((MainActivity) getActivity(), lineFile, diaIndex, direction, station, TimeTableFragment.this);
                dialog.show();
                dialog.setOnSortListener(stationIndex -> {
                    lineFile.sortTrain(diaIndex, direction, stationIndex);
                    TimeTableFragment.this.allTrainChange();

                });
                return true;
            }
        }
        TimeTableActionDialog dialog=new TimeTableActionDialog(getContext(),lineFile.getDiagram(diaIndex),direction,TimeTableFragment.this);
        dialog.show();


        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }
}