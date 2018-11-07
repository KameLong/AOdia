package com.kamelong.aodia.TimeTable.EditTrain;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.ToggleButton;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Train;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;

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
            SdLog.log(e);
        }

        fragmentContainer = inflater.inflate(R.layout.train_edit_view, container, false);
        return fragmentContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            int stationNum = (1 - train.direction * 2) * i + train.direction * (diaFile.getStationNum() - 1);
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
                    int stationNum = (1 - train.direction * 2) * station + train.direction * (diaFile.getStationNum() - 1);
                    ((EditTimeView) departureTimeLayout.getChildAt(stationNum + 2)).setTime(train.getDepartureTime(station));
                    if (train.departExist(stationNum) && train.arriveExist(stationNum)) {
                        ((EditTimeView) stopTimeLayout.getChildAt(stationNum + 2)).setTime(train.getDepartureTime(station) - train.getArrivalTime(station));
                    } else {
                        ((EditTimeView) stopTimeLayout.getChildAt(stationNum + 2)).setTime(-1);
                    }
                    nessTimeCreate();
                    trainChange();
                    final EditText operationName = (EditText) findViewById(R.id.operationName);
                    operationName.setText(train.operationName);
                    Button beforeButton = (Button) findViewById(R.id.beforeTrain);
                    beforeTrain= diaFile.diagram.get(diaNumber).beforeOperation(train);
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
            departureTimeLayout.addView(depTime);

            EditTimeView ariTime = new EditTimeView(getAOdiaActivity(), stationNum, train.getArrivalTime(stationNum), true);
            ariTime.setOnTimeChangeListener(new OnTimeChangeListener() {
                @Override
                public void onTimeChanged(int station, int time) {
                    train.setArrivalTime(station, time);
                    int stationNum = (1 - train.direction * 2) * station + train.direction * (diaFile.getStationNum() - 1);

                    ((EditTimeView) arrivalTimeLayout.getChildAt(stationNum + 2)).setTime(train.getArrivalTime(station));
                    if (train.departExist(stationNum) && train.arriveExist(stationNum)) {
                        ((EditTimeView) stopTimeLayout.getChildAt(stationNum + 2)).setTime(train.getDepartureTime(station) - train.getArrivalTime(station));
                    } else {
                        ((EditTimeView) stopTimeLayout.getChildAt(stationNum + 2)).setTime(-1);
                    }
                    nessTimeCreate();
                    trainChange();
                    final EditText operationName = (EditText) findViewById(R.id.operationName);
                    operationName.setText(train.operationName);
                    Button beforeButton = (Button) findViewById(R.id.beforeTrain);
                    beforeTrain= diaFile.diagram.get(diaNumber).beforeOperation(train);
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
            if (train.departExist(stationNum) && train.arriveExist(stationNum)) {
                EditTimeView stopTime = new EditStopTimeView(getAOdiaActivity(), stationNum, train.getDepartureTime(stationNum) - train.getArrivalTime(stationNum), false);
                stopTimeLayout.addView(stopTime);
            } else {
                EditTimeView stopTime = new EditStopTimeView(getAOdiaActivity(), stationNum, -1, false);
                stopTimeLayout.addView(stopTime);
            }
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
                train.leaveYard = b;
                operationName.setEnabled(b);
            }
        });
        ToggleButton goYead = (ToggleButton) findViewById(R.id.goYard);
        goYead.setChecked(train.goYard);
        goYead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                train.goYard = b;
            }
        });

        Button beforeButton = (Button) findViewById(R.id.beforeTrain);
        beforeTrain= diaFile.diagram.get(diaNumber).beforeOperation(train);
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
                getAOdiaActivity().openTrainEdit(diaNumber,beforeTrain);

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
                getAOdiaActivity().openTrainEdit(diaNumber,nextTrain);

            }
        });



    }

    private void nessTimeCreate() {
        final LinearLayout betweenStationTimeLayout = ((LinearLayout) findViewById(R.id.betweenStationTimeLayout));
        betweenStationTimeLayout.removeAllViews();
        int size = 0;
        int depTime = -1;
        for (int i = 0; i < diaFile.getStationNum(); i++) {
            int stationNum = (1 - train.direction * 2) * i + train.direction * (diaFile.getStationNum() - 1);
            size++;
            if (train.arriveExist(stationNum)) {
                if (depTime >= 0) {
                    EditTimeView nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, train.getArrivalTime(stationNum) - depTime, false, size);
                    betweenStationTimeLayout.addView(nessTime);
                }
                depTime = -1;
                size = 0;
            }
            if (train.departExist(stationNum)) {
                if (depTime >= 0) {
                    EditTimeView nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, train.getDepartureTime(stationNum) - depTime, false, size);
                    betweenStationTimeLayout.addView(nessTime);
                } else {
                    if (size != 0) {
                        EditTimeView nessTime = new EditBetweenTimeView(getAOdiaActivity(), stationNum, -1, false, size - 1);
                        betweenStationTimeLayout.addView(nessTime);
                    }
                }
                depTime = train.getDepartureTime(stationNum);
                size = 0;
            }


        }
        EditTimeView nessTime = new EditBetweenTimeView(getAOdiaActivity(), diaFile.getStationNum(), -1, false, size);
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
