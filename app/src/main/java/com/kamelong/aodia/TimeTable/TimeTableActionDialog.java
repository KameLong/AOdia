package com.kamelong.aodia.TimeTable;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.kamelong.OuDia.Diagram;
import com.kamelong.aodia.EditTrain.OnTrainChangeListener;
import com.kamelong.aodia.R;

public class TimeTableActionDialog extends Dialog {

    public TimeTableActionDialog(Context context, final  Diagram diagram, final int direction, final OnTrainChangeListener listener) {
        super(context);
        setContentView(R.layout.timetable_action_dialog);
        findViewById(R.id.sortNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.sortNumber(direction);
                TimeTableActionDialog.this.dismiss();
                listener.allTrainChange();
            }
        });
        findViewById(R.id.sortType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.sortType(direction);
                TimeTableActionDialog.this.dismiss();
                listener.allTrainChange();
            }
        });
        findViewById(R.id.sortName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.sortName(direction);
                TimeTableActionDialog.this.dismiss();
                listener.allTrainChange();
            }
        });
        findViewById(R.id.sortRemark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.sortRemark(direction);
                TimeTableActionDialog.this.dismiss();
                listener.allTrainChange();
            }
        });
        findViewById(R.id.combineTrainNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagram.combineByTrainNumber(direction);
                TimeTableActionDialog.this.dismiss();
                listener.allTrainChange();
            }
        });

    }
}
