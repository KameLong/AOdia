package com.kamelong.aodia.TimeTable.EditTrain;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.Toast;

import com.kamelong.tool.SDlog;

public class EditStopTimeView extends EditTimeView {

    public EditStopTimeView(Context context,int station,int time,boolean editable) {
        super(context,station,time,editable);

        InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(5);
        this.setFilters(filters);
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                final int station=EditStopTimeView.this.stationNum;
                if(isFocused) {
                    String text=EditStopTimeView.this.getText().toString();
                    EditStopTimeView.this.setText(text.replace(" ",""));
                }else{
                    String text=EditStopTimeView.this.getText().toString();
                    if(text.isEmpty()){
                        if(onTimeChangeListener!=null){
                            onTimeChangeListener.onTimeChanged(station,-1);
                        }

                        return;
                    }
                    int time= timeString2Int(text);
                    setText(timeInt2String(time));

                    if(time<0){
                        setText(history);
                        Toast.makeText(getContext(),"入力文字列は時刻ではありません2",Toast.LENGTH_LONG).show();

                    }else{
                        if(onTimeChangeListener!=null) {
                            onTimeChangeListener.onTimeChanged(station, time);
                        }
                    }

                }
            }
        });

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
    protected int timeString2Int(String time){
        try {
            int mm = 0;
            int ss = 0;
            switch (time.length()) {
                case 1:
                    ss = Integer.parseInt(time);
                    return ss;
                case 2:
                    ss = Integer.parseInt(time);
                    return ss;

                case 3:
                    mm = Integer.parseInt(time.substring(0, 1));
                    ss = Integer.parseInt(time.substring(1, 3));
                    return mm * 60 + ss;
                case 4:
                    mm = Integer.parseInt(time.substring(0, 2));
                    ss = Integer.parseInt(time.substring(2, 4));
                    return mm * 60 + ss;

            }
        }catch (Exception e){
            SDlog.log(e);
        }
        return -1;
    }

}
