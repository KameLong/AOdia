package com.kamelong2.aodia.TimeTable.EditTrain;

import android.content.Context;

public class EditBetweenTimeView extends EditStopTimeView {
    public EditBetweenTimeView(Context context,int station,int time,boolean editable, int lNum) {
        super(context,station,time,editable);
        this.setHeight((int)(HEIGHT*lNum*context.getResources().getDisplayMetrics().density));
    }

}
