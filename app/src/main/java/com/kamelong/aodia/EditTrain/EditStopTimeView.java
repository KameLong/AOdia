package com.kamelong.aodia.EditTrain;

import android.content.Context;

public class EditStopTimeView extends EditTimeView {

    public EditStopTimeView(Context context,int time) {
        super(context,time);
    }
    @Override
    protected String timeInt2String(int time){
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
