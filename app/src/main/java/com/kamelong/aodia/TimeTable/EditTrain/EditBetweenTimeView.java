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

public class EditBetweenTimeView extends EditStopTimeView {
    public EditBetweenTimeView(Context context,int station,int time,boolean editable, int lNum) {
        super(context,station,time,editable);
        this.setHeight((int)(HEIGHT*lNum*context.getResources().getDisplayMetrics().density));
    }

}
