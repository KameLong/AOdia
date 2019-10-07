package com.kamelong.aodia.EditTrain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.OuterTerminal;
import com.kamelong.OuDia.StationTime;
import com.kamelong.OuDia.Train;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TrainTimeEditFragment extends Fragment implements OnTrainChangeListener {
    public Train train;
    public OnTrainChangeListener trainChangeListener;
    public OnFragmentCloseListener fragmentCloseListener;
    private int diaNumber;
    private LineFile lineFile;
    View fragmentContainer;
    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }
    public <T extends View> T findViewById(int id){
        return getActivity().findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {//まずBundleを確認し、fileNum,diaIndex,directを更新する
            Bundle bundle = getArguments();
            int fileNum = bundle.getInt(AOdia.FILE_INDEX);
            diaNumber = bundle.getInt(AOdia.DIA_INDEX);
            int direction = bundle.getInt("direction");
            int trainNum = bundle.getInt(AOdia.TRAIN_INDEX);
            lineFile = getMainActivity().getAOdia().getLineFile(fileNum);
            train = lineFile.getTrain(diaNumber, direction, trainNum);
        } catch (Exception e) {
            SDlog.log(e);
        }

        fragmentContainer = inflater.inflate(R.layout.trainedit_fragment, container, false);
        return fragmentContainer;
    }

    private void init(){
        if(train==null){
            Toast.makeText(getContext(),"原因不明のエラーが発生しました。列車が見つかりません。(TrainTimeEditFragment onViewCreated)",Toast.LENGTH_LONG).show();
            return;
        }
        //列車名
        final EditText trainName = findViewById(R.id.trainName);
        trainName.setText(train.name);
        trainName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(!train.name.equals(trainName.getEditableText().toString())){
                        train.name=(trainName.getEditableText().toString());
                        trainChanged(train);
                    }
                }

            }
        });
        //列車番号
        final EditText trainNumber = findViewById(R.id.trainNumber);
        trainNumber.setText(train.number);
        trainNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if(!train.number.equals(trainNumber.getEditableText().toString())){
                        train.number=(trainNumber.getEditableText().toString());
                        trainChanged(train);
                    }
                }
            }
        });
        //号数
        final EditText trainCount = findViewById(R.id.countNumber);
        trainCount.setText(train.count + "号");
        trainCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String text = trainCount.getEditableText().toString();
                    if (text.length() > 1) {
                        if(!train.count.equals(text.substring(0, text.length() - 1))){
                            train.count=(text.substring(0, text.length() - 1));
                            trainChanged(train);
                        }
                    } else {
                        if(train.count.length()!=0){
                            train.count="";
                            trainChanged(train);

                        }
                    }

                }
            }
        });
        //備考
        final EditText remarkEdit= findViewById(R.id.remarkEdit);
        remarkEdit.setText(train.remark);
        remarkEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    if(!train.remark.equals(remarkEdit.getEditableText().toString())){
                        train.remark=(remarkEdit.getEditableText().toString());
                        trainChanged(train);
                    }
                }
            }
        });

        //列車種別
        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        List<String> typeList = new ArrayList<String>();
        for (TrainType trainType:lineFile.getTrainType()) {
            typeList.add(trainType.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getMainActivity(), android.R.layout.simple_spinner_item, typeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(dataAdapter);
        typeSpinner.setSelection(train.type);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=train.type){
                    train.type=i;
                    trainChanged(train);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        Spinner outerStartSpinner=findViewById(R.id.outerStartName);
        List<String> outerStartName = new ArrayList<String>();
        outerStartName.add("設定なし");
        if(train.getStartStation()>=0) {
            for (OuterTerminal terminal : lineFile.getStation(train.getStartStation()).outerTerminals) {
                outerStartName.add(terminal.outerTerminalName);
            }
        }
        ArrayAdapter<String> outerStartAdapter = new ArrayAdapter<String>(getMainActivity(), android.R.layout.simple_spinner_item,outerStartName);
        outerStartAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outerStartSpinner.setAdapter(outerStartAdapter);
        outerStartSpinner.setSelection(train.getOuterStartStation()+1);
        outerStartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position-1!=train.getOuterStartStation()){
                    train.setOuterStartStation(position-1);
                    trainChanged(train);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner outerEndSpinner=findViewById(R.id.outerEndName);
        List<String> outerEndName = new ArrayList<String>();
        outerEndName.add("設定なし");
        if(train.getEndStation()>=0) {

            for (OuterTerminal terminal : lineFile.getStation(train.getEndStation()).outerTerminals) {
                outerEndName.add(terminal.outerTerminalName);
            }
        }
        ArrayAdapter<String> outerEndAdapter = new ArrayAdapter<String>(getMainActivity(), android.R.layout.simple_spinner_item,outerEndName);
        outerEndAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outerEndSpinner.setAdapter(outerEndAdapter);
        outerEndSpinner.setSelection(train.getOuterEndStation()+1);
        outerEndSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position-1!=train.getOuterEndStation()){
                    train.setOuterEndStation(position-1);
                    trainChanged(train);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final EditText outerStartTime=findViewById(R.id.outerStartTime);
        if(train.getOuterStartTime()>=0){
            outerStartTime.setText(StationTime.timeIntToOuDiaString(train.getOuterStartTime()));
        }else{
            outerStartTime.setText("");
        }
        outerStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    int time=StationTime.timeStringToInt(outerStartTime.getText().toString());
                    if(train.getOuterStartTime()!=time) {
                        train.setOuterStartTime(time);
                        trainChanged(train);
                    }
                }
            }
        });
        final EditText outerEndTime=findViewById(R.id.outerEndTime);
        if(train.getOuterEndStation()>=0){
            outerEndTime.setText(StationTime.timeIntToOuDiaString(train.getOuterEndTime()));
        }else{
            outerEndTime.setText("");
        }
        outerEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    int time=StationTime.timeStringToInt(outerEndTime.getText().toString());
                    if(train.getOuterEndTime()!=time) {
                        train.setOuterEndTime(time);
                        trainChanged(train);
                    }
                }
            }
        });




        //閉じるボタン
        (findViewById(R.id.editSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottomContents2).setVisibility(GONE);
                if(trainChangeListener!=null){
                    trainChangeListener.allTrainChange();
                }
                getMainActivity().getSupportFragmentManager().beginTransaction().remove(TrainTimeEditFragment.this).commit();



            }
        });
    }
    private void initTimeView(){
        final LinearLayout departureTimeLayout = findViewById(R.id.departureTimeLayout);
        final LinearLayout arrivalTimeLayout = findViewById(R.id.arrivalTimeLayout);
        final LinearLayout stopTimeLayout = findViewById(R.id.stopTimeLayout);
        final LinearLayout stopTypeLayout = findViewById(R.id.stopTypeLayout);
        final LinearLayout stopNumberLayout = findViewById(R.id.stopNumerLayout);
        final LinearLayout stationNameLayout= findViewById(R.id.stationNameLayout);

        departureTimeLayout.removeAllViews();
        arrivalTimeLayout.removeAllViews();
        stopTypeLayout.removeAllViews();
        stopTimeLayout.removeAllViews();
        stopNumberLayout.removeAllViews();
        stationNameLayout.removeAllViews();

        for (int i = 0; i < lineFile.getStationNum(); i++) {
            final int stationIndex=train.getStationIndex(i);

            //駅名
            TextView stationNameView = new StationNameTextView(getMainActivity(), lineFile.getStation(stationIndex).name, stationIndex);
            stationNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TrainEditDialog dialog=new TrainEditDialog(getContext(),lineFile.getDiagram(diaNumber),train,stationIndex,TrainTimeEditFragment.this);
                    dialog.show();
                }
            });

            stationNameLayout.addView(stationNameView);

            //発車時刻
            EditTimeView depTime = new EditTimeView(getMainActivity(),  train.getDepTime(stationIndex));
            depTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTimeView2 editText=((MainActivity)getContext()).findViewById(R.id.editTimeLayout);
                    editText.setOnTrainChangedLister(TrainTimeEditFragment.this);
                    editText.setVisibility(VISIBLE);
                    editText.setValues(train,stationIndex,0);
                    ((MainActivity)getContext()).findViewById(R.id.editTimeLayout).setVisibility(VISIBLE);
                }
            });
            departureTimeLayout.addView(depTime);


            //着時刻
            EditTimeView ariTime = new EditTimeView(getMainActivity(), train.getAriTime(stationIndex));
            ariTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditTimeView2 editText=((MainActivity)getContext()).findViewById(R.id.editTimeLayout);
                    editText.setOnTrainChangedLister(TrainTimeEditFragment.this);
                    editText.setVisibility(VISIBLE);
                    editText.setValues(train,stationIndex,1);
                    ((MainActivity)getContext()).findViewById(R.id.editTimeLayout).setVisibility(VISIBLE);
                }
            });
            arrivalTimeLayout.addView(ariTime);
            EditTimeView stopTime=null;
            //発着時刻
            if (train.timeExist(stationIndex,0) && train.timeExist(stationIndex,1)) {
                stopTime = new EditStopTimeView(getMainActivity(), train.getDepTime(stationIndex) - train.getAriTime(stationIndex));
            } else {
                stopTime= new EditStopTimeView(getMainActivity(), -1);
            }
            stopTimeLayout.addView(stopTime);
            EditTrainStopSpinner stopView = new EditTrainStopSpinner(getMainActivity(), stationIndex, lineFile, train);
            stopNumberLayout.addView(stopView);
            stopView.setOnTrainChangeListener(this);
            EditStopTypeSpinner stopTypeView = new EditStopTypeSpinner(getMainActivity(), stationIndex, train);
            stopTypeLayout.addView(stopTypeView);
            stopTypeView.setOnTrainChangeListener(this);


        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            EditTimeView2 editText=((MainActivity)getContext()).findViewById(R.id.editTimeLayout);
                editText.setVisibility(GONE);

            init();
            initTimeView();



            nessTimeCreate();

//            final EditText operationName = (EditText) findViewById(R.id.operationName);
//            operationName.setText(train.operationName);
//            operationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean b) {
//                    if (!b) {
//                        train.operationName = operationName.getEditableText().toString();
//                    }
//                }
//            });
//            operationName.setEnabled(train.leaveYard);
//
//            ToggleButton leaveYead = (ToggleButton) findViewById(R.id.leaveYard);
//            leaveYead.setChecked(train.leaveYard);
//            leaveYead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    train.leaveYard = b;
//                    operationName.setEnabled(b);
//                }
//            });
//            ToggleButton goYead = (ToggleButton) findViewById(R.id.goYard);
//            goYead.setChecked(train.goYard);
//            goYead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    train.goYard = b;
//                }
//            });
//
//            Button beforeButton = (Button) findViewById(R.id.beforeTrain);
//            beforeTrain = lineFile.diagram.get(diaIndex).beforeOperation(train);
//            if (beforeTrain == null) {
//                beforeButton.setText("前運用無し");
//                beforeButton.setEnabled(false);
//            } else {
//                beforeButton.setText(beforeTrain.number + "\n" + timeInt2String4(beforeTrain.getADTime(train.startStation())) + "着");
//                beforeButton.setEnabled(true);
//            }
//            beforeButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    getAOdiaActivity().openTrainEdit(diaIndex, beforeTrain);
//
//                }
//            });
//
//            Button nextButton = (Button) findViewById(R.id.nextTrain);
//            nextTrain = lineFile.diagram.get(diaIndex).nextOperation(train);
//            if (nextTrain == null) {
//                nextButton.setText("前運用無し");
//                nextButton.setEnabled(false);
//            } else {
//                nextButton.setText(nextTrain.number + "\n" + timeInt2String4(nextTrain.getDATime(train.endStation())) + "発");
//                nextButton.setEnabled(true);
//
//            }
//            nextButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    getAOdiaActivity().openTrainEdit(diaIndex, nextTrain);
//
//                }
//            });

        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast("原因不明のエラーが発生しました。"+e.getMessage());
        }
    }

    private void nessTimeCreate() {
        final LinearLayout betweenStationTimeLayout = findViewById(R.id.betweenStationTimeLayout);
        for(int i=0;i<betweenStationTimeLayout.getChildCount();i++){
            betweenStationTimeLayout.getChildAt(i).clearFocus();
        }
        betweenStationTimeLayout.removeAllViews();
        int size = 0;
        int depTime = -1;
        EditBetweenTimeView nessTime=null;
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            nessTime=null;
            int stationIndex = train.getStationIndex(i);
            size++;
            if (train.timeExist(stationIndex,1)) {
                if (depTime >= 0) {
                    nessTime = new EditBetweenTimeView(getMainActivity(), train.getAriTime(stationIndex) - depTime,  size);
                }
                depTime = -1;
                size = 0;
            }
            if (train.timeExist(stationIndex,0)) {
                if (depTime >= 0) {
                    nessTime = new EditBetweenTimeView(getMainActivity(), train.getDepTime(stationIndex) - depTime, size);
                } else {
                    if (size != 0) {
                        nessTime = new EditBetweenTimeView(getMainActivity(), -1, size - 1);
                    }
                }
                depTime = train.getDepTime(stationIndex);
                size = 0;
            }
            if(nessTime!=null) {
                betweenStationTimeLayout.addView(nessTime);

            }

        }
        nessTime = new EditBetweenTimeView(getMainActivity(), -1, size);
        betweenStationTimeLayout.addView(nessTime);
    }

    public void setOnTrainChangeListener(OnTrainChangeListener listener) {
        this.trainChangeListener = listener;
    }


    public void setOnFragmentCloseListener(OnFragmentCloseListener listener) {
        fragmentCloseListener = listener;
    }




    public static String timeInt2String4(int time) {
        if (time < 0) return "";
        time = time / 60;
        int mm = time % 60;
        time = time / 60;
        int hh = time % 24;
        return String.format("%02d", hh) + " " + String.format("%02d", mm);
    }

    @Override
    public void trainChanged(Train train) {
        init();
        initTimeView();
        nessTimeCreate();
        if(trainChangeListener!=null){
            trainChangeListener.trainChanged(train);
        }
    }
    @Override
    public void allTrainChange() {
        init();
        initTimeView();
        nessTimeCreate();
        if(trainChangeListener!=null){
            trainChangeListener.allTrainChange();
        }
    }
}
