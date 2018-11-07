package com.kamelong.aodia.TimeTable.EditTrain;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.R;

public class TrainEditDialog extends Dialog {
    public DiaFile diaFile;
    public int station=0;
    public int direct=0;
    public Train train;
    OnTrainEditInterface trainEditInterface;

    public TrainEditDialog(Context context, DiaFile dia, int d, int s,Train train) {
        super(context);
        diaFile=dia;
        station=s;
        direct=d;
        this.train=train;

    }
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.train_edit_dialog);
        init();
    }
    public void init(){
        try{
            TextView stationNameView = (TextView) findViewById(R.id.stationNameView);
            String text1=diaFile.station.get(station).name;
            if(text1.length()>10){
                text1=text1.substring(0,10);
            }
            stationNameView.setText(text1);
            Button beforeStationButton = (Button) findViewById(R.id.beforeStationButton);
            String text3=diaFile.station.get(station).name;


            if (station - (1 - 2 * direct) >= 0 && station - (1 - 2 * direct) < diaFile.getStationNum()) {
                String text2=diaFile.station.get(station - (1 - 2 * direct)).name;
                if(text2.length()>6){
                    text2=text2.substring(0,6);
                }

                beforeStationButton.setText("⇦" + text2);
                beforeStationButton.setVisibility(View.VISIBLE);
            } else {
                beforeStationButton.setVisibility(View.INVISIBLE);
            }
            beforeStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station - (1 - 2 * direct);
                    TrainEditDialog.this.init();
                }
            });
            Button afterStationButton = (Button) findViewById(R.id.afterStationButton);
            if (station + (1 - 2 * direct) >= 0 && station + (1 - 2 * direct) < diaFile.getStationNum()) {
                String text2=diaFile.station.get(station + (1 - 2 * direct)).name;
                if(text2.length()>6){
                    text2=text2.substring(0,6);
                }

                afterStationButton.setText(text2 + "⇨");
                afterStationButton.setVisibility(View.VISIBLE);
            } else {
                afterStationButton.setVisibility(View.INVISIBLE);
            }
            afterStationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    station = station + (1 - 2 * direct);
                    TrainEditDialog.this.init();
                }
            });

            findViewById(R.id.downTimeTableButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(trainEditInterface!=null){
                        trainEditInterface.trainSplit(train,station);
                    }
                    TrainEditDialog.this.dismiss();

                }
            });
            findViewById(R.id.combineTrainButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(trainEditInterface!=null){
                        trainEditInterface.trainCombine(train,station);
                    }
                    TrainEditDialog.this.dismiss();

                }
            });
            findViewById(R.id.copyButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(trainEditInterface!=null){
                        trainEditInterface.trainCopy(train);
                    }
                    TrainEditDialog.this.dismiss();


                }
            });
            findViewById(R.id.insertTrainButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(trainEditInterface!=null){
                        trainEditInterface.trainInsert(train);
                    }
                    TrainEditDialog.this.dismiss();

                }
            });
            findViewById(R.id.deleteBotton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(trainEditInterface!=null){
                        trainEditInterface.trainDelete(train);
                    }
                    TrainEditDialog.this.dismiss();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setOnTrainEditInterface(OnTrainEditInterface i){
        trainEditInterface=i;
    }
    public void initStop(){

    }

}
