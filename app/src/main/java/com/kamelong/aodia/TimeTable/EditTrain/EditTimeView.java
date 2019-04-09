package com.kamelong.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaDefaultView;

public class EditTimeView extends AppCompatEditText {
    public String history="";
    protected OnTimeChangeListener onTimeChangeListener=null;
    public int stationNum;
    protected static final int HEIGHT=30;

    public EditTimeView(Context context,int station,int time,boolean editable) {
        super(context);
        if(time<0){
            history="";
        }else {
            history = timeInt2String(time);
        }
        this.setText(history);
        this.setEnabled(editable);
        this.stationNum=station;
        this.setBackgroundColor(Color.argb(255,255,255,255));
        this.setTextColor(Color.BLACK);
        this.setTextSize(15);

        setPadding(10, 5, 10, 5);
        setLines(1);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(8);
        setFilters(filters);
        setGravity(Gravity.RIGHT);
        setGravity(Gravity.CENTER_VERTICAL);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                final int station=EditTimeView.this.stationNum;
                if(isFocused) {
                    String text=EditTimeView.this.getText().toString();
                    EditTimeView.this.setText(text.replace(" ",""));
                }else{
                    String text=EditTimeView.this.getText().toString();
                    if(text.isEmpty()){
                        if(onTimeChangeListener!=null){
                            onTimeChangeListener.onTimeChanged(station,-1);
                        }

                        return;
                    }
                    int time= Train.timeStringToInt(text);
                    if(time<0){
                        setText(history);
                        Toast.makeText(getContext(),"入力文字列は時刻ではありません",Toast.LENGTH_LONG).show();

                    }else{
                        if(onTimeChangeListener!=null) {
                            onTimeChangeListener.onTimeChanged(station, time);
                        }
                    }

                }
            }
        });
        this.setHeight((int)(HEIGHT*context.getResources().getDisplayMetrics().density));
    }
    protected String timeInt2String(int time){
        if(time<0)return"";
        int ss=time%60;
        time=time/60;
        int mm=time%60;
        time=time/60;
        int hh=time%24;
        return String.format("%02d", hh) + " " + String.format("%02d", mm) + " " + String.format("%02d", ss);
    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        ;
        canvas.drawLine(0,getHeight()-1,getWidth(),getHeight()-1,AOdiaDefaultView.blackPaint);
    }
    public void setTime(int time){
        if(time<0){
            setText("");
            return;
        }
        setText(timeInt2String(time));
    }
    public void setOnTimeChangeListener(OnTimeChangeListener listener){
        onTimeChangeListener=listener;
    }
    protected int timeString2Int(String time){
        int hh=0;
        int mm=0;
        int ss=0;
        switch (time.length()){
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

}
