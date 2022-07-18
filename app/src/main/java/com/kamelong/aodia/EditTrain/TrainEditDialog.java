package com.kamelong.aodia.EditTrain;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;

import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

public class TrainEditDialog extends Dialog {
    private OnTrainChangeListener listener;
    public TrainEditDialog(@NonNull Context context, final Diagram timetable, final Train train, final  int stationIndex, final OnTrainChangeListener listener) {
        super(context);
        this.listener=listener;
        setContentView(R.layout.trainedit_train_dialog);
        findViewById(R.id.splitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.splitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int trainIndex=timetable.getTrainIndex(train.direction,train);
                if(trainIndex<0){
                    SDlog.toast("エラー：時刻表内にこの列車が見つかりません。");
                    TrainEditDialog.this.dismiss();
                    return;
                }

                timetable.addTrain(train.direction,trainIndex+1,train.clone(train.lineFile));
                timetable.getTrain(train.direction,trainIndex).endAtThisStation(stationIndex);
                timetable.getTrain(train.direction,trainIndex+1).startAtThisStation(stationIndex);
                TrainEditDialog.this.dismiss();
                if(listener!=null){
                    listener.allTrainChange();
                }
            }
        });
        findViewById(R.id.conbineButton).setEnabled(train.getEndStation()==stationIndex);
        findViewById(R.id.conbineButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int trainIndex=timetable.getTrainIndex(train.direction,train);
                if(trainIndex<0){
                    SDlog.toast("エラー：時刻表内にこの列車が見つかりません。");
                    TrainEditDialog.this.dismiss();
                    return;
                }
                for(int i=trainIndex;i<timetable.getTrainNum(train.direction);i++){
                    Train other=timetable.getTrain(train.direction,i);
                    if(other.getStartStation()==stationIndex){
                        train.conbine(other);
                        timetable.deleteTrain(other);
                    }
                }
                if(listener!=null){
                    listener.allTrainChange();
                }
                TrainEditDialog.this.dismiss();
            }
        });
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainEditDialog.this.dismiss();

            }
        });

    }
}
