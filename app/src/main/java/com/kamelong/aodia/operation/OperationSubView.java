package com.kamelong.aodia.operation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.aodia.R;
import com.kamelong.aodia.diadata.AOdiaTrain;

/**
 * Created by kame on 2017/10/12.
 */

public class OperationSubView extends LinearLayout {
    private View layout;
    AOdiaTrain train;
    public OperationSubView(Context context, AOdiaTrain train) {
        super(context);
        this.train=train;
        int direct=train.getDirect();

        if(train.isUsed()) {
            layout = LayoutInflater.from(context).inflate(R.layout.operation_sub_view, this);
            ((TextView) layout.findViewById(R.id.trainNumber)).setText(train.getNumber());
            ((TextView) layout.findViewById(R.id.trainType)).setText(train.getTrainType().getName());
            if (direct == 0) {
                ((TextView) layout.findViewById(R.id.startName)).setText(train.getDiaFile().getStation().getName(train.getStartStation(direct)));
                ((TextView) layout.findViewById(R.id.startTime)).setText(timeString(train.getTime(train.getStartStation(direct)).getDATime()));
                ((TextView) layout.findViewById(R.id.endName)).setText(train.getDiaFile().getStation().getName(train.getEndStation(direct)));
                ((TextView) layout.findViewById(R.id.endTime)).setText(timeString(train.getTime(train.getEndStation(direct)).getADTime()));
                ((TextView) layout.findViewById(R.id.directString)).setText("→");
            } else {
                ((TextView) layout.findViewById(R.id.endName)).setText(train.getDiaFile().getStation().getName(train.getStartStation(direct)));
                ((TextView) layout.findViewById(R.id.endTime)).setText(timeString(train.getTime(train.getStartStation(direct)).getDATime()));
                ((TextView) layout.findViewById(R.id.startName)).setText(train.getDiaFile().getStation().getName(train.getEndStation(direct)));
                ((TextView) layout.findViewById(R.id.startTime)).setText(timeString(train.getTime(train.getEndStation(direct)).getADTime()));
                ((TextView) layout.findViewById(R.id.directString)).setText("←");
            }
            ((Button) layout.findViewById(R.id.addButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    OperationView operationView = (OperationView) (OperationSubView.this.getParent().getParent().getParent());
                    operationView.addNewTrip(OperationSubView.this);
                }
            });
            ((Button) layout.findViewById(R.id.deleteButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    OperationView operationView = (OperationView) (OperationSubView.this.getParent().getParent().getParent());
                    operationView.deleteTrip(OperationSubView.this);
                }
            });
        }else{
            layout = LayoutInflater.from(context).inflate(R.layout.operation_sub_view_nouse, this);

        }

    }
    private static String timeString(int second){
        int ss=second%60;
        second=(second-ss)/60;
        int mm=second%60;
        second=(second-mm)/60;
        int hh=second%60;
        hh=hh%24;
        return String.format("%2d", hh) + String.format("%02d", mm);


    }

}
