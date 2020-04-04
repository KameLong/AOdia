package com.kamelong.aodia.EditTrain;

import android.content.Context;

/**
 * 駅間時間を表示するためのView
 * TrainTimeEditFragmentで使用
 */
public class BetweenTimeView extends StopTimeView {
    public BetweenTimeView(Context context, int time, int lNum) {
        super(context,time);
        this.setHeight(HEIGHT*lNum);
    }

}
