package com.kamelong.aodia.EditTrain;

import android.content.Context;

/**
 * 停車時間を表示するためのView
 * TrainTimeEditFragmentで使用
 */
public class StopTimeView extends TimeView {

    public StopTimeView(Context context) {
        super(context);
    }
    @Override
    protected String timeInt2String(int time){
        //「mm ss」形式で表示
        if(time<0)return"";
        int ss=time%60;
        time=time/60;
        int mm=time;
        if(mm<100) {
            return String.format("%02d", mm) + " " + String.format("%02d", ss);
        }else{
            return "##";
        }
    }

}
