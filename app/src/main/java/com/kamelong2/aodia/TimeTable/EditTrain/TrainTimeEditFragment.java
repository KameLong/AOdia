package com.kamelong2.aodia.TimeTable.EditTrain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.kamelong.aodia.R;
import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaActivity;
import com.kamelong2.aodia.SDlog;

import java.util.ArrayList;
import java.util.List;

public class TrainTimeEditFragment extends Fragment implements OnTrainEditInterface {
    public Train train;
    public Train beforeTrain;
    public Train nextTrain;
    public OnTrainChangeListener trainChangeListener;
    public OnFragmentCloseListener fragmentCloseListener;
    int diaNumber;
    DiaFile diaFile;
    View fragmentContainer;
    public AOdiaActivity getAOdiaActivity(){
        return (AOdiaActivity)getActivity();
    }
    public View findViewById(int id){
        return getActivity().findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            int fileNum = bundle.getInt("fileNumber");
            diaNumber = bundle.getInt("diaNumber");
            int direction = bundle.getInt("direction");
            int trainNum = bundle.getInt("trainNumber");
            diaFile = getAOdiaActivity().diaFiles.get(fileNum);
            train = diaFile.getTrain(diaNumber, direction, trainNum);
        } catch (Exception e) {
            SDlog.log(e);
        }

        fragmentContainer = inflater.inflate(R.layout.old_train_edit_view, container, false);
        return fragmentContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(train==null){
            Toast.makeText(getContext(),"原因不明のエラーが発生しました。列車が見つかりません。(TrainTimeEditFragment onViewCreated)",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            final EditText trainName = ((EditText) findViewById(R.id.trainName));
            trainName.setText(train.name);
            trainName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        train.name = trainName.getEditableText().toString();
                        trainChange();
                    }

                }
            });
            final EditText trainNumber = ((EditText) findViewById(R.id.trainNumber));
            trainNumber.setText(train.number);
            trainNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        train.number = trainNumber.getEditableText().toString();
                        trainChange();
                    }
                }
            });
            final EditText trainCount = ((EditText) findViewById(R.id.countNumber));
            trainCount.setText(train.count + "号");
            trainCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        String text = trainCount.getEditableText().toString();
                        if (text.length() > 1) {
                            train.count = text.substring(0, text.length() - 1);
                        } else {
                            train.count = "";
                        }
                        trainChange();

                    }
                }
            });
            final EditText remarkEdit=(EditText)findViewById(R.id.remarkEdit);
            remarkEdit.setText(train.remark);
            remarkEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b){
                        train.remark=remarkEdit.getEditableText().toString();
                    }
                }
            });

            Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
            List<String> typeList = new ArrayList<String>();
            for (int type = 0; type < diaFile.trainType.size(); type++) {
                typeList.add(diaFile.trainType.get(type).name);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getAOdiaActivity(), android.R.layout.simple_spinner_item, typeList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(dataAdapter);
            typeSpinner.setSelection(train.type);
            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    train.type = i;
                    trainChange();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            final LinearLayout departureTimeLayout = (LinearLayout) findViewById(R.id.departureTimeLayout);
            final LinearLayout arrivalTimeLayout = (LinearLayout) findViewById(R.id.arrivalTimeLayout);
            final LinearLayout stopTimeLayout = (LinearLayout) findViewById(R.id.stopTimeLayout);
            final LinearLayout stopTypeLayout = (LinearLayout) findViewById(R.id.stopTypeLayout);
            final LinearLayout stopNumberLayout = (LinearLayout) findViewById(R.id.stopNumerLayout);

            for (int i = 0; i < diaFile.getStationNum(); i++) {
                final int stationNum = (1 - train.direction * 2) * i + train.direction * (diaFile.getStationNum() - 1);
                TextView textView = new StationNameTextView(getAOdiaActivity(), diaFile.station.get(stationNum).name, stationNum);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TrainEditDialog dialog = new TrainEditDialog(getActivity(), diaFile, train.direction, ((StationNameTextView) view).stationNumber, train);
                        dialog.setOnTrainEditInterface(TrainTimeEditFragment.this);
                        dialog.show();
                    }
                });
                ((LinearLayout) findViewById(R.id.stationNameLayout)).addView(textView);
                EditTimeView depTime = new EditTimeView(getAOdiaActivity(), stationNum, train.getDepartureTime(stationNum), true);
                depTime.setOnTimeChangeListener(new OnTimeChangeListener() {
                    @Override
                    public void onTimeChanged(int station, int time) {
                        train.setDepartureTime(station, time);
                        if(train.getStopType(station)==0||train.getStopType(station)==3){
                            train.setStopType(station,1);

                        }
                        int stationNum = (1 - train.direction * 2) * station + train.direction * (diaFile.getStationNum() - 1);
                        ((EditTimeView) departureTimeLayout.getChildAt(stationNum)).setTime(train.getDepartureTime(station));
                        if (train.departExist(station) && train.arriveExist(station)) {
                            ((EditTimeView) stopTimeLayout.getChildAt(stationNum)).setTime(train.getDepartureTime(station) - train.getArrivalTime(station));
                        } else {
                            ((EditTimeView) stopTimeLayout.getChildAt(stationNum)).setTime(-1);
                        }
                        ((EditStopTypeSpinner)stopTypeLayout.getChildAt(stationNum)).setSelection(train.getStopType(station));
                        nessTimeCreate();
                        trainChange();
                        final EditText operationName = (EditText) findViewById(R.id.operationName);
                        operationName.setText(train.operationName);
                        Button beforeButton = (Button) findViewById(R.id.beforeTrain);
                        beforeTrain = diaFile.diagram.get(diaNumber).beforeOperation(train);
                        if (beforeTrain == null) {
                            beforeButton.setText("前運用無し");
                            beforeButton.setEnabled(false);
                        } else {
                            beforeButton.setText(beforeTrain.number + "\n" + timeInt2String4(beforeTrain.getADTime(train.startStation())) + "着");
                            beforeButton.setEnabled(true);
                        }
                        Button nextButton = (Button) findViewById(R.id.nextTrain);
                        nextTrain = diaFile.diagram.get(diaNumber).nextOperation(train);
                        if (nextTrain == null) {
                            nextButton.setText("次運用無し");
                            nextButton.setEnabled(false);
                        } else {
                            nextButton.setText(nextTrain.number + "\n" + timeInt2String4(nextTrain.getDATime(train.endStation())) + "発");
                            nextButton.setEnabled(true);

                        }
                    }
                });
                departureTimeLayout.addView(depTime);

                EditTimeView ariTime = new EditTimeView(getAOdiaActivity(), stationNum, train.getArrivalTime(stationNum), true);
                ariTime.setOnTimeChangeListener(new OnTimeChangeListener() {
                    @Override
                    public void onTimeChanged(int station, int time) {
                        train.setArrivalTime(station, time);
                        if(train.getStopType(station)==0||train.getStopType(station)==3){
                            train.setStopType(station,1);

                        }

                        int stationNum = (1 - train.direction * 2) * station + train.direction * (diaFile.getStationNum() - 1);
                        ((EditStopTypeSpinner)stopTypeLayout.getChildAt(stationNum)).setSelection(train.getStopType(station));

                        ((EditTimeView) arrivalTimeLayout.getChildAt(stationNum)).setTime(train.getArrivalTime(station));
                        if (train.departExist(station) && train.arriveExist(station)) {
                            ((EditTimeView) stopTimeLayout.getChildAt(stationNum)).setTime(train.getDepartureTime(station) - train.getArrivalTime(station));
                        } else {
                            ((EditTimeView) stopTimeLayout.getChildAt(stationNum)).setTime(-1);
                        }
                        nessTimeCreate();
                        trainChange();
                        final EditText operationName = (EditText) findViewById(R.id.operationName);
                        operationName.setText(train.operationName);
                        Button beforeButton = (Button) findViewById(R.id.beforeTrain);
                        beforeTrain = diaFile.diagram.get(diaNumber).beforeOperation(train);
                        if (beforeTrain == null) {
                            beforeButton.setText("前運用無し");
                            beforeButton.setEnabled(false);
                        } else {
                            beforeButton.setText(beforeTrain.number + "\n" + timeInt2String4(beforeTrain.getADTime(train.startStation())) + "着");
                            beforeButton.setEnabled(true);
                        }
                        Button nextButton = (Button) findViewById(R.id.nextTrain);
                        nextTrain = diaFile.diagram.get(diaNumber).nextOperation(train);
                        if (nextTrain == null) {
                            nextButton.setText("前運用無し");
                            nextButton.setEnabled(false);
                        } else {
                            nextButton.setText(nextTrain.number + "\n" + timeInt2String4(nextTrain.getDATime(train.endStation())) + "発");
                            nextButton.setEnabled(true);

                        }
                    }
                });
                arrivalTimeLayout.addView(ariTime);
                EditTimeView stopTime=null;
                if (train.departExist(stationNum) && train.arriveExist(stationNum)) {
                    stopTime = new EditStopTimeView(getAOdiaActivity(), stationNum, train.getDepartureTime(stationNum) - train.getArrivalTime(stationNum), true);
                } else {
                     stopTime= new EditStopTimeView(getAOdiaActivity(), stationNum, -1, true);
                }
                stopTime.setOnTimeChangeListener(new OnTimeChangeListener(){
                    @Override
                    public void onTimeChanged(int station, int stopTime){
                        if (stopTime < 0) return;
                        if(train.departExist(station)&&train.arriveExist(station)){
                            int shiftTime = stopTime - (train.getDepartureTime(station) - train.getArrivalTime(station));
                            int time = train.getDepartureTime(station);
                            if (time >= 0) {
                                time = time + shiftTime;
                            }
                            if (time < 0) {
                                time += 24 * 3600;
                            }
                            time = time % (24 * 3600);
                            train.setDepartureTime(station, time);
                            if (train.direction == 0) {
                                for (int i = station + 1; i < diaFile.getStationNum(); i++) {
                                    int depTime = train.getDepartureTime(i);
                                    if (depTime >= 0) {
                                        depTime = depTime + shiftTime;
                                        if (depTime < 0) {
                                            depTime += 24 * 3600;
                                        }
                                    }
                                    depTime = depTime % (24 * 3600);
                                    train.setDepartureTime(i, depTime);
                                    int ariTime = train.getArrivalTime(i);
                                    if (ariTime >= 0) {
                                        ariTime = ariTime + shiftTime;
                                        if (ariTime < 0) {
                                            ariTime += 24 * 3600;
                                        }
                                    }
                                    ariTime = ariTime % (24 * 3600);
                                    train.setArrivalTime(i, ariTime);
                                }
                            } else {
                                for (int i = 0; i < station; i++) {
                                    int depTime = train.getDepartureTime(i);
                                    if (depTime >= 0) {
                                        depTime = depTime + shiftTime;
                                        if (depTime < 0) {
                                            depTime += 24 * 3600;
                                        }
                                    }
                                    depTime = depTime % (24 * 3600);
                                    train.setDepartureTime(i, depTime);
                                    int ariTime = train.getArrivalTime(i);
                                    if (ariTime >= 0) {
                                        ariTime = ariTime + shiftTime;
                                        if (ariTime < 0) {
                                            ariTime += 24 * 3600;
                                        }
                                    }
                                    ariTime = ariTime % (24 * 3600);
                                    train.setArrivalTime(i, ariTime);
                                }

                            }
                        }else if(train.departExist(station)){
                            train.setArrivalTime(station,train.getDepartureTime(station)-stopTime);
                        }else if(train.arriveExist(station)){
                            train.setDepartureTime(station,train.getArrivalTime(station)+stopTime);
                        }
                        for (int i = 0; i < diaFile.getStationNum(); i++) {
                            ((EditTimeView) departureTimeLayout.getChildAt(i)).setTime(train.getDepartureTime((diaFile.getStationNum() - 1) * train.direction + i * (1 - 2 * train.direction)));
                            ((EditTimeView) arrivalTimeLayout.getChildAt(i)).setTime(train.getArrivalTime((diaFile.getStationNum() - 1) * train.direction + i * (1 - 2 * train.direction)));
                        }

                        nessTimeCreate();
                        trainChange();
                        final EditText operationName = (EditText) findViewById(R.id.operationName);
                        operationName.setText(train.operationName);
                        Button beforeButton = (Button) findViewById(R.id.beforeTrain);
                        beforeTrain = diaFile.diagram.get(diaNumber).beforeOperation(train);
                        if (beforeTrain == null) {
                            beforeButton.setText("前運用無し");
                            beforeButton.setEnabled(false);
                        } else {
                            beforeButton.setText(beforeTrain.number + "\n" + timeInt2String4(beforeTrain.getADTime(train.startStation())) + "着");
                            beforeButton.setEnabled(true);
                        }
                        Button nextButton = (Button) findViewById(R.id.nextTrain);
                        nextTrain = diaFile.diagram.get(diaNumber).nextOperation(train);
                        if (nextTrain == null) {
                            nextButton.setText("前運用無し");
                            nextButton.setEnabled(false);
                        } else {
                            nextButton.setText(nextTrain.number + "\n" + timeInt2String4(nextTrain.getDATime(train.endStation())) + "発");
                            nextButton.setEnabled(true);

                        }
                    }
                });
                stopTimeLayout.addView(stopTime);
                EditTrainStopSpinner stopView = new EditTrainStopSpinner(getAOdiaActivity(), stationNum, diaFile, train);
                stopNumberLayout.addView(stopView);
                stopView.setOnTrainChangeListener(trainChangeListener);
                EditStopTypeSpinner stopTypeView = new EditStopTypeSpinner(getAOdiaActivity(), stationNum, train);
                stopTypeLayout.addView(stopTypeView);
                stopTypeView.setOnTrainChangeListener(trainChangeListener);


            }
            nessTimeCreate();
            Button button = (Button) findViewById(R.id.editSubmit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fragmentCloseListener != null) {
                        fragmentCloseListener.fragmentClose();
                    }
                }
            });

            final EditText operationName = (EditText) findViewById(R.id.operationName);
            operationName.setText(train.operationName);
            operationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        train.operationName = operationName.getEditableText().toString();
                    }
                }
            });
            operationName.setEnabled(train.leaveYard);

            ToggleButton leaveYead = (ToggleButton) findViewById(R.id.leaveYard);
            leaveYead.setChecked(train.leaveYard);
            leaveYead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SDlog.toast("注意：OuDia2ndの運用機能は次バージョンのAOdiaでは廃止される予定です。");

                    train.leaveYard = b;
                    operationName.setEnabled(b);
                }
            });
            ToggleButton goYead = (ToggleButton) findViewById(R.id.goYard);
            goYead.setChecked(train.goYard);
            goYead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SDlog.toast("注意：OuDia2ndの運用機能は次バージョンのAOdiaでは廃止される予定です。");

                    train.goYard = b;
                }
            });

            Button beforeButton = (Button) findViewById(R.id.beforeTrain);
            beforeTrain = diaFile.diagram.get(diaNumber).beforeOperation(train);
            if (beforeTrain == null) {
                beforeButton.setText("前運用無し");
                beforeButton.setEnabled(false);
            } else {
                beforeButton.setText(beforeTrain.number + "\n" + timeInt2String4(beforeTrain.getADTime(train.startStation())) + "着");
                beforeButton.setEnabled(true);
            }
            beforeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SDlog.toast("注意：OuDia2ndの運用機能は次バージョンのAOdiaでは廃止される予定です。");
                    getAOdiaActivity().openTrainEdit(diaNumber, beforeTrain);

                }
            });

            Button nextButton = (Button) findViewById(R.id.nextTrain);
            nextTrain = diaFile.diagram.get(diaNumber).nextOperation(train);
            if (nextTrain == null) {
                nextButton.setText("前運用無し");
                nextButton.setEnabled(false);
            } else {
                nextButton.setText(nextTrain.number + "\n" + timeInt2String4(nextTrain.getDATime(train.endStation())) + "発");
                nextButton.setEnabled(true);

            }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SDlog.toast("注意：OuDia2ndの運用機能は次バージョンのAOdiaでは廃止される予定です。");
                    getAOdiaActivity().openTrainEdit(diaNumber, nextTrain);

                }
            });

        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast("原因不明のエラーが発生しました。"+e.getMessage());
        }
    }

    private void nessTimeCreate() {
        final LinearLayout betweenStationTimeLayout = ((LinearLayout) findViewById(R.id.betweenStationTimeLayout));
        for(int i=0;i<betweenStationTimeLayout.getChildCount();i++){
            betweenStationTimeLayout.getChildAt(i).clearFocus();
        }
        betweenStationTimeLayout.removeAllViews();
        int size = 0;
        int depTime = -1;
        EditBetweenTimeView nessTime=null;
        for (int i = 0; i < diaFile.getStationNum(); i++) {
            nessTime=null;
            int stationNum = (1 - train.direction * 2) * i + train.direction * (diaFile.getStationNum() - 1);
            size++;
            if (train.arriveExist(stationNum)) {
                if (depTime >= 0) {
                    nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, train.getArrivalTime(stationNum) - depTime, true, size);
                }
                depTime = -1;
                size = 0;
            }
            if (train.departExist(stationNum)) {
                if (depTime >= 0) {
                    nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, train.getDepartureTime(stationNum) - depTime, true, size);
                } else {
                    if (size != 0) {
                        nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, -1, false, size - 1);
                    }
                }
                depTime = train.getDepartureTime(stationNum);
                size = 0;
            }
            if(nessTime!=null) {
                nessTime.setOnTimeChangeListener(new OnTimeChangeListener() {
                    @Override
                    public void onTimeChanged(int station, int time) {
                        if(train.direction==0){
                            //下り列車
                            int baseTime=train.getADTime(station);
                            for(int i=station-1;i>=0;i--){
                                if(train.timeExist(i)){
                                    baseTime=baseTime-train.getDATime(i);
                                    break;
                                }
                            }
                            int shiftTime=time-baseTime;
                            for (int i = station ; i < diaFile.getStationNum(); i++) {
                                int depTime = train.getDepartureTime(i);
                                if (depTime >= 0) {
                                    depTime = depTime + shiftTime;
                                    if (depTime < 0) {
                                        depTime += 24 * 3600;
                                    }
                                }
                                depTime = depTime % (24 * 3600);
                                train.setDepartureTime(i, depTime);
                                int ariTime = train.getArrivalTime(i);
                                if (ariTime >= 0) {
                                    ariTime = ariTime + shiftTime;
                                    if (ariTime < 0) {
                                        ariTime += 24 * 3600;
                                    }
                                }
                                ariTime = ariTime % (24 * 3600);
                                train.setArrivalTime(i, ariTime);
                            }

                        }else{
                            //上り列車
                            int baseTime=train.getADTime(station);
                            for(int i=station+1;i<train.stationNum;i++){
                                if(train.timeExist(i)){
                                    baseTime=baseTime-train.getDATime(i);
                                    break;
                                }
                            }
                            int shiftTime=time-baseTime;
                            for (int i = 0; i <= station; i++) {
                                int depTime = train.getDepartureTime(i);
                                if (depTime >= 0) {
                                    depTime = depTime + shiftTime;
                                    if (depTime < 0) {
                                        depTime += 24 * 3600;
                                    }
                                }
                                depTime = depTime % (24 * 3600);
                                train.setDepartureTime(i, depTime);
                                int ariTime = train.getArrivalTime(i);
                                if (ariTime >= 0) {
                                    ariTime = ariTime + shiftTime;
                                    if (ariTime < 0) {
                                        ariTime += 24 * 3600;
                                    }
                                }
                                ariTime = ariTime % (24 * 3600);
                                train.setArrivalTime(i, ariTime);
                            }

                        }
                        final LinearLayout departureTimeLayout = (LinearLayout) findViewById(R.id.departureTimeLayout);
                        final LinearLayout arrivalTimeLayout = (LinearLayout) findViewById(R.id.arrivalTimeLayout);

                        for (int i = 0; i < diaFile.getStationNum(); i++) {
                            ((EditTimeView) departureTimeLayout.getChildAt(i)).setTime(train.getDepartureTime((diaFile.getStationNum() - 1) * train.direction + i * (1 - 2 * train.direction)));
                            ((EditTimeView) arrivalTimeLayout.getChildAt(i)).setTime(train.getArrivalTime((diaFile.getStationNum() - 1) * train.direction + i * (1 - 2 * train.direction)));
                        }

                    }

                });
                betweenStationTimeLayout.addView(nessTime);

            }

        }
        nessTime = new EditBetweenTimeView(getAOdiaActivity(), diaFile.getStationNum(), -1, false, size);
        betweenStationTimeLayout.addView(nessTime);
    }

    public void setOnTrainChangeListener(OnTrainChangeListener listener) {
        this.trainChangeListener = listener;
    }

    private void trainChange() {
        if (trainChangeListener != null) {
            trainChangeListener.trainChanged();
        }
    }

    public void setOnFragmentCloseListener(OnFragmentCloseListener listener) {
        fragmentCloseListener = listener;
    }


    @Override
    public void trainSplit(Train train, int station) {
        fragmentCloseListener.fragmentClose();
        diaFile.diagram.get(diaNumber).splitTrain(train, station);
        if (trainChangeListener != null) {
            trainChangeListener.trainReset();
        }
    }

    @Override
    public void trainCombine(Train train, int station) {
        fragmentCloseListener.fragmentClose();
        diaFile.diagram.get(diaNumber).combineTrain(train, station);
        if (trainChangeListener != null) {
            trainChangeListener.trainReset();
        }


    }

    @Override
    public void trainCopy(Train train) {
        fragmentCloseListener.fragmentClose();
        diaFile.diagram.get(diaNumber).copyTrain(train);
        if (trainChangeListener != null) {
            trainChangeListener.trainReset();
        }


    }

    @Override
    public void trainInsert(Train train) {
        fragmentCloseListener.fragmentClose();
        diaFile.diagram.get(diaNumber).insertTrain(train);
        if (trainChangeListener != null) {
            trainChangeListener.trainReset();
        }

    }

    @Override
    public void trainDelete(Train train) {
        fragmentCloseListener.fragmentClose();
        diaFile.diagram.get(diaNumber).deleteTrain(train);
        if (trainChangeListener != null) {
            trainChangeListener.trainReset();
        }
    }

    public static String timeInt2String4(int time) {
        if (time < 0) return "";
        time = time / 60;
        int mm = time % 60;
        time = time / 60;
        int hh = time % 24;
        return String.format("%02d", hh) + " " + String.format("%02d", mm);
    }

}
