package com.kamelong.aodia.timeTable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

/**
 * Created by kame on 2017/11/19.
 */

public class TrainTimeEditFragment extends TimeTableFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            diaNumber = bundle.getInt("diaN");
            fileNum=bundle.getInt("fileNum");
            direct =  bundle.getInt("direct");
        }catch(Exception e){
            SdLog.log(e);
        }
        fragmentContainer = inflater.inflate(R.layout.train_time_edit_fragment, container, false);
        fragmentContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return fragmentContainer;
    }
    @Override
    public String fragmentName(){
        return "列車編集";
    }
}