package com.kamelong.aodia.EditTrain;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kamelong.OuDia.Train;
import com.kamelong.aodia.R;

//時刻入力のView
public class EditTimeView2 extends LinearLayout {
    //イベントリスナー
    private OnCloseEditTimeView2 onCloseEditTimeView2=null;
    //時刻変更する列車
    private Train train=null;
    //時刻変更する駅
    private int stationIndex=0;
    //到着or発車
    private int AD=0;
    private OnTrainChangeListener trainChangeListener =null;
    public EditTimeView2(Context context) {
        super(context,null);
    }

    public EditTimeView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EditTimeView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.trainedit_edit_time, this);
        ((EditText)findViewById(R.id.editTime)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT){
                    submitTime();
                    return true;
                }
                return false;
            }
        });
        //閉じるボタンが押されたとき
        findViewById(R.id.closeButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTime();
            }
        });
        //時刻編集
        findViewById(R.id.plus10).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(train.lineFile.secondShift[0]);
            }
        });
        findViewById(R.id.plus15).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(train.lineFile.secondShift[1]);
            }
        });
        findViewById(R.id.plus60).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(60);
            }
        });
        findViewById(R.id.plus300).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(300);
            }
        });

        findViewById(R.id.mines10).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(-train.lineFile.secondShift[0]);
            }
        });
        findViewById(R.id.mines15).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(-train.lineFile.secondShift[1]);
            }
        });
        findViewById(R.id.mines60).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(-60);
            }
        });
        findViewById(R.id.mines300).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTime(-300);
            }
        });
        findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                train.setTime(stationIndex,AD,-1);
                EditTimeView2.this.setVisibility(GONE);
                if(trainChangeListener!=null){
                    trainChangeListener.trainChanged(train);
                }

            }
        });
    }

    /**
     * EditTimeViewで編集する時刻を入力します
     */
    public void setValues(Train train,int stationIndex,int AD){
        ((Button) findViewById(R.id.plus10)).setText("+"+train.lineFile.secondShift[0] + "秒");
        ((Button) findViewById(R.id.plus15)).setText("+"+train.lineFile.secondShift[1] + "秒");
        ((Button) findViewById(R.id.mines10)).setText("-" + train.lineFile.secondShift[0] + "秒");
        ((Button) findViewById(R.id.mines15)).setText("-" + train.lineFile.secondShift[1] + "秒");

        this.train=train;
        this.stationIndex=stationIndex;
        this.AD=AD;
        if(!train.timeExist(stationIndex,AD)){
            int time=-1;
            if(train.direction==0){
                for(int i=stationIndex;i>=0;i--){
                    if(train.timeExist(i,0)){
                        time=train.getDepTime(i);
                        break;
                    }
                    if(train.timeExist(i,1)){
                        time=train.getAriTime(i);
                        break;
                    }
                }
                if(time<0) {
                    for (int i = stationIndex; i < train.getStationNum(); i++) {
                        if (train.timeExist(i, 1)) {
                            time = train.getAriTime(i);
                            break;
                        }
                        if (train.timeExist(i, 0)) {
                            time = train.getDepTime(i);
                            break;
                        }
                    }
                }
            }else{
                for (int i = stationIndex; i < train.getStationNum(); i++) {
                    if (train.timeExist(i, 0)) {
                        time = train.getDepTime(i);
                        break;
                    }
                    if (train.timeExist(i, 1)) {
                        time = train.getAriTime(i);
                        break;
                    }
                }
                if(time<0) {
                    for(int i=stationIndex;i>=0;i--){
                        if(train.timeExist(i,1)){
                            time=train.getAriTime(i);
                            break;
                        }
                        if(train.timeExist(i,0)){
                            time=train.getDepTime(i);
                            break;
                        }
                    }
                }
            }
            if(time>=0){
                train.setTime(stationIndex,AD,time);
                switch (train.getStopType(stationIndex)){
                    case 0:
                    case 3:
                        train.setStopType(stationIndex,1);
                }
                if(trainChangeListener!=null){
                    trainChangeListener.trainChanged(train);
                }
            }
        }
        ((EditText)findViewById(R.id.editTime)).setText(timeInt2String(train.getTime(stationIndex,AD)));
    }
    private String timeInt2String(int time){
        if (time < 0) return "";
        int ss = time % 60;
        time = time / 60;
        int mm = time % 60;
        time = time / 60;
        int hh = time % 24;
        return hh+ String.format("%02d", mm) + String.format("%02d", ss);
    }

    /**
     * 指定秒時刻を移動させます
     * @param change
     */
    private void changeTime(int change){
        if(train!=null){
            if(train.timeExist(stationIndex,AD)){
                train.setTime(stationIndex,AD,train.getTime(stationIndex,AD)+change);
            }
            if (AD == 1 && ((CheckBox) findViewById(R.id.after)).isChecked()) {
                train.setTime(stationIndex,0,train.getTime(stationIndex,0)+change);
            }
            if (AD == 0 && ((CheckBox) findViewById(R.id.before)).isChecked()) {
                train.setTime(stationIndex,1,train.getTime(stationIndex,1)+change);
            }
            if ((train.direction == 0 && ((CheckBox) findViewById(R.id.before)).isChecked()) || (train.direction == 1 && ((CheckBox) findViewById(R.id.after)).isChecked())) {
                for(int i=0;i<stationIndex;i++){
                    if(train.timeExist(i,0)){
                        train.setDepTime(i,train.getDepTime(i)+change);
                    }
                    if(train.timeExist(i,1)){
                        train.setAriTime(i,train.getAriTime(i)+change);
                    }
                }

            }
            if ((train.direction == 0 && ((CheckBox) findViewById(R.id.after)).isChecked()) || (train.direction == 1 && ((CheckBox) findViewById(R.id.before)).isChecked())) {
                for(int i=stationIndex+1;i<train.getStationNum();i++){
                    if(train.timeExist(i,0)){
                        train.setDepTime(i,train.getDepTime(i)+change);
                    }
                    if(train.timeExist(i,1)){
                        train.setAriTime(i,train.getAriTime(i)+change);
                    }
                }
            }
        }
            ((EditText)findViewById(R.id.editTime)).setText(timeInt2String(train.getTime(stationIndex,AD)));
        if(trainChangeListener !=null){
            trainChangeListener.trainChanged(train);
        }
    }
    protected int timeString2Int(String time){
        int hh=0;
        int mm=0;
        int ss=0;
        switch (time.length()){
            case 3:
                hh = Integer.parseInt(time.substring(0, 1));
                mm = Integer.parseInt(time.substring(1, 3));
                return hh * 3600 + mm * 60;
            case 4:
                hh = Integer.parseInt(time.substring(0, 2));
                mm = Integer.parseInt(time.substring(2, 4));
                return hh * 3600 + mm * 60;

            case 5:
                hh=Integer.parseInt(time.substring(0,1));
                mm=Integer.parseInt(time.substring(1,3));
                ss=Integer.parseInt(time.substring(3,5));
                return hh*3600+mm*60+ss;
            case 6:
                hh=Integer.parseInt(time.substring(0,2));
                mm=Integer.parseInt(time.substring(2,4));
                ss=Integer.parseInt(time.substring(4,6));
                return hh*3600+mm*60+ss;

        }
        return -1;
    }
    public void setOnTrainChangedLister(OnTrainChangeListener listener){
        trainChangeListener =listener;
    }

    /**
     * 入力時刻を決定する
     */
    private void submitTime(){
        int time=timeString2Int(((EditText)findViewById(R.id.editTime)).getEditableText().toString());
        train.setTime(stationIndex,AD,time);
        if(trainChangeListener!=null){
            trainChangeListener.trainChanged(train);
        }
        this.setVisibility(GONE);
        if(onCloseEditTimeView2!=null){
            onCloseEditTimeView2.onClosed();
        }

    }
    public void setOnCloseEditTimeView2(OnCloseEditTimeView2 listener){
        onCloseEditTimeView2=listener;
    }

}
