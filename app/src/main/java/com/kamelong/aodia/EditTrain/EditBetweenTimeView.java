package com.kamelong.aodia.EditTrain;

import android.content.Context;

public class EditBetweenTimeView extends EditStopTimeView {
    public EditBetweenTimeView(Context context,int time, int lNum) {
        super(context,time);
        this.setHeight((int)(HEIGHT*lNum));
    }

}
