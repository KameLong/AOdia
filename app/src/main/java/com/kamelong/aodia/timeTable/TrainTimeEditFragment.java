package com.kamelong.aodia.timeTable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

/**
 * Created by kame on 2017/11/19.
 */

public class TrainTimeEditFragment extends TimeTableFragmentOld {
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
        setFragmentContainer(inflater.inflate(R.layout.train_time_edit_fragment, container, false));
        getFragmentContainer().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return getFragmentContainer();
    }
    @Override
    public String fragmentName(){
        return "列車編集";
    }
}