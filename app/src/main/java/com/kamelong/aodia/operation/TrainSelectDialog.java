package com.kamelong.aodia.operation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.EditTimeTable.LineTrainTimeFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadataOld.AOdiaOperation;

/**
 * Created by kame on 2017/10/14.
 */

class TrainSelectDialog extends Dialog{
    private AOdiaDiaFile diaFile;
    private LineTrainTimeFragment fragment;
    private AOdiaOperation ope=null;
    private int fileNum;
    int station;
    private int diaNum;
    private AOdiaActivity activity;

    private LinearLayout contentView;


    public TrainSelectDialog(Context context, AOdiaDiaFile dia, int fileN, int diaN,AOdiaOperation ope){
        super(context);
        diaFile=dia;
        fragment=null;
        diaNum=diaN;
        fileNum=fileN;
        activity=(AOdiaActivity)context;
        this.ope=ope;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_ACTION_BA);
        setTitle("運用列車追加");
        contentView=new LinearLayout(getContext());
//        contentView.setOrientation();
        setContentView(R.layout.file_select_dialog);
        init();

    }
    private void init(){
        try {
            Button downTimetable =findViewById(R.id.downTimeTableButton);
            downTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrainSelectDialog.this.dismiss();
                }
            });
            Button upTimetable = findViewById(R.id.upTimeTableButton);
            upTimetable.setText("上り時刻表");
            upTimetable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrainSelectDialog.this.dismiss();
                }
            });
            Button diagram = findViewById(R.id.diagramButton);
            diagram.setText("ダイヤグラム");
            diagram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrainSelectDialog.this.dismiss();
                }
            });

        }catch(Exception e){
            SdLog.log(e);
        }

    }

}
