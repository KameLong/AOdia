package com.kamelong.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaDefaultView;

public class EditStopTimeView extends EditTimeView {

    public EditStopTimeView(Context context,int station,int time,boolean editable) {
        super(context,station,time,editable);

        InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(5);
        setFilters(filters);
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
