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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * 列車時刻を編集するためのFragment
 */
public class TrainTimeEditFragment extends Fragment implements OnTimeChangeListener,OnTrainChangeListener {
    private OnTrainChangeListener trainChangeListener;
    private OnFragmentCloseListener fragmentCloseListener;
    private int diaNumber;
    private LineFile lineFile;
    public Train train;
    private View fragmentContainer;

    private int focusStation=-1;
    private int focusAD=-1;


    private MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }
    public <T extends View> T findViewById(int id){
        return fragmentContainer.findViewById(id);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {//まずBundleを確認し、fileNum,diaIndex,directを更新する
            Bundle bundle = getArguments();
            assert bundle != null:"bundle is null";
            int fileNum = bundle.getInt(AOdia.FILE_INDEX);
            diaNumber = bundle.getInt(AOdia.DIA_INDEX);
            int direction = bundle.getInt(AOdia.DIRECTION);
            int trainNum = bundle.getInt(AOdia.TRAIN_INDEX);
            if(trainNum<0){
                return fragmentContainer;
            }

            lineFile = getMainActivity().getAOdia().getLineFile(fileNum);
            assert lineFile!=null:"lineFile is null. lineFileCount="+getMainActivity().getAOdia().getLineFileList().size()+".lineFIleIndex="+fileNum;
            train = lineFile.getTrain(diaNumber, direction, trainNum);
        }
        catch (Exception e) {
            SDlog.log(e);
        }
        fragmentContainer = inflater.inflate(R.layout.trainedit_fragment, container, false);
        return fragmentContainer;


    }

    private void init(){
        if(train==null){
            Toast.makeText(getContext(),getMainActivity().getString(R.string.undefined_error)+getMainActivity().getString(R.string.eroor_trainNotFound)+"(TrainTimeEditFragment onViewCreated)",Toast.LENGTH_LONG).show();
            return;
        }
        //列車名
        final EditText trainName = findViewById(R.id.trainName);
        trainName.setText(train.name);
        trainName.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if(!train.name.equals(trainName.getEditableText().toString())){
                    train.name=(trainName.getEditableText().toString());
                    trainChanged(train);
                }
            }

        });
        //列車番号
        final EditText trainNumber = findViewById(R.id.trainNumber);
        trainNumber.setText(train.number);
        trainNumber.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                if(!train.number.equals(trainNumber.getEditableText().toString())){
                    train.number=(trainNumber.getEditableText().toString());
                    trainChanged(train);
                }
            }
        });
        //号数
        final EditText trainCount = findViewById(R.id.countNumber);
        trainCount.setText(train.count + getMainActivity().getString(R.string.count));
        trainCount.setOnFocusChangeListener((view, b) -> {
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
        });
        //備考
        final EditText remarkEdit= findViewById(R.id.remarkEdit);
        remarkEdit.setText(train.remark);
        remarkEdit.setOnFocusChangeListener((view, b) -> {
            if(!b){
                if(!train.remark.equals(remarkEdit.getEditableText().toString())){
                    train.remark=(remarkEdit.getEditableText().toString());
                    trainChanged(train);
                }
            }
        });

        //列車種別
        Spinner typeSpinner = findViewById(R.id.typeSpinner);
        List<String> typeList = new ArrayList<>();
        for (TrainType trainType:lineFile.getTrainType()) {
            typeList.add(trainType.name);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getMainActivity(), android.R.layout.simple_spinner_item, typeList);
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
        List<String> outerStartName = new ArrayList<>();
        outerStartName.add(getMainActivity().getString(R.string.outerStationNull));
        if(train.getStartStation()>=0) {
            for (OuterTerminal terminal : lineFile.getStation(train.getStartStation()).outerTerminals) {
                outerStartName.add(terminal.outerTerminalName);
            }
        }
        ArrayAdapter<String> outerStartAdapter = new ArrayAdapter<>(getMainActivity(), android.R.layout.simple_spinner_item, outerStartName);
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
        List<String> outerEndName = new ArrayList<>();
        outerEndName.add(getMainActivity().getString(R.string.outerStationNull));
        if(train.getEndStation()>=0) {

            for (OuterTerminal terminal : lineFile.getStation(train.getEndStation()).outerTerminals) {
                outerEndName.add(terminal.outerTerminalName);
            }
        }
        ArrayAdapter<String> outerEndAdapter = new ArrayAdapter<>(getMainActivity(), android.R.layout.simple_spinner_item, outerEndName);
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
        outerStartTime.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                int time=StationTime.timeStringToInt(outerStartTime.getText().toString());
                if(train.getOuterStartTime()!=time) {
                    train.setOuterStartTime(time);
                    trainChanged(train);
                }
            }
        });
        final EditText outerEndTime=findViewById(R.id.outerEndTime);
        if(train.getOuterEndStation()>=0){
            outerEndTime.setText(StationTime.timeIntToOuDiaString(train.getOuterEndTime()));
        }else{
            outerEndTime.setText("");
        }
        outerEndTime.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                int time=StationTime.timeStringToInt(outerEndTime.getText().toString());
                if(train.getOuterEndTime()!=time) {
                    train.setOuterEndTime(time);
                    trainChanged(train);
                }
            }
        });


        //閉じるボタン
        (findViewById(R.id.editSubmit)).setOnClickListener(v -> {
            try {
                getActivity().findViewById(R.id.bottomContents2).setVisibility(GONE);
                if (trainChangeListener != null) {
                    trainChangeListener.allTrainChange();
                }
                getMainActivity().getSupportFragmentManager().beginTransaction().remove(TrainTimeEditFragment.this).commit();
                if(fragmentCloseListener!=null){
                    fragmentCloseListener.fragmentClose();
                }
            }catch (Exception e){
                SDlog.log(e);
            }



        });
    }

    /**
     * 発着時刻の更新を行います。
     * Viewを作成するとき。初期操作はinitDepAriTimeViewを使ってください。
     */
    private void upDateDepAriTimeView(){
        final LinearLayout departureTimeLayout = findViewById(R.id.departureTimeLayout);
        final LinearLayout arrivalTimeLayout = findViewById(R.id.arrivalTimeLayout);
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            final int stationIndex = train.getStationIndex(i);
            TimeView depTime = (TimeView) departureTimeLayout.getChildAt(i);
            depTime.setTime(train.getDepTime(stationIndex));
            TimeView ariTime = (TimeView) arrivalTimeLayout.getChildAt(i);
            ariTime.setTime(train.getAriTime(stationIndex));
        }

    }
    private void initDepAriTimeView() {
        if(lineFile==null||train==null){
            return;
        }

        final LinearLayout departureTimeLayout = findViewById(R.id.departureTimeLayout);
        final LinearLayout arrivalTimeLayout = findViewById(R.id.arrivalTimeLayout);
        departureTimeLayout.removeAllViews();
        arrivalTimeLayout.removeAllViews();
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            final int stationIndex = train.getStationIndex(i);
            //発車時刻
            TimeView depTime = new TimeView(getMainActivity(),  train.getDepTime(stationIndex));
            depTime.setOnClickListener(v -> {
                final TimeView view=(TimeView)v;
                for(int stationIndex12 = 0; stationIndex12 <lineFile.getStationNum(); stationIndex12++){
                    ((TimeView)departureTimeLayout.getChildAt(stationIndex12)).setCheck(false);
                    ((TimeView)arrivalTimeLayout.getChildAt(stationIndex12)).setCheck(false);
                }
                view.setCheck(true);

                EditTimeView editText=((MainActivity)getContext()).findViewById(R.id.editTimeLayout);
                editText.setOnTimeChangedLister(TrainTimeEditFragment.this);
                editText.setVisibility(VISIBLE);
                editText.setValues(train,stationIndex,0);
                editText.setOnCloseEditTimeViewListener(() -> view.setCheck(false));
                ((MainActivity)getContext()).findViewById(R.id.editTimeLayout).setVisibility(VISIBLE);


            });
            departureTimeLayout.addView(depTime);


            //着時刻
            TimeView ariTime = new TimeView(getMainActivity(), train.getAriTime(stationIndex));
            ariTime.setOnClickListener(v -> {
                final TimeView view=(TimeView)v;

                for(int stationIndex1 = 0; stationIndex1 <lineFile.getStationNum(); stationIndex1++){
                    ((TimeView)departureTimeLayout.getChildAt(stationIndex1)).setCheck(false);
                    ((TimeView)arrivalTimeLayout.getChildAt(stationIndex1)).setCheck(false);
                }
                view.setCheck(true);

                EditTimeView editText=((MainActivity)getContext()).findViewById(R.id.editTimeLayout);
                editText.setOnTimeChangedLister(TrainTimeEditFragment.this);
                editText.setVisibility(VISIBLE);
                editText.setValues(train,stationIndex,1);
                ((MainActivity)getContext()).findViewById(R.id.editTimeLayout).setVisibility(VISIBLE);
                editText.setOnCloseEditTimeViewListener(() -> view.setCheck(false));
            });
            arrivalTimeLayout.addView(ariTime);

        }
    }
    private void initStopTimeView(){
        if(lineFile==null){
            return;
        }
        final LinearLayout stopTimeLayout = findViewById(R.id.stopTimeLayout);
        final LinearLayout stopTypeLayout = findViewById(R.id.stopTypeLayout);
        final LinearLayout stopNumberLayout = findViewById(R.id.stopNumerLayout);
        final LinearLayout stationNameLayout= findViewById(R.id.stationNameLayout);

        stopTypeLayout.removeAllViews();
        stopTimeLayout.removeAllViews();
        stopNumberLayout.removeAllViews();
        stationNameLayout.removeAllViews();

        for (int i = 0; i < lineFile.getStationNum(); i++) {
            final int stationIndex=train.getStationIndex(i);

            //駅名
            TextView stationNameView = new StationNameTextView(getMainActivity(), lineFile.getStation(stationIndex).name, stationIndex);
            stationNameView.setOnClickListener(view -> {
                TrainEditDialog dialog=new TrainEditDialog(getContext(),lineFile.getDiagram(diaNumber),train,stationIndex,TrainTimeEditFragment.this);
                dialog.show();
            });

            stationNameLayout.addView(stationNameView);

            TimeView stopTime=null;
            //発着時刻
            if (train.timeExist(stationIndex,0) && train.timeExist(stationIndex,1)) {
                stopTime = new StopTimeView(getMainActivity(), train.getDepTime(stationIndex) - train.getAriTime(stationIndex));
            } else {
                stopTime= new StopTimeView(getMainActivity(), -1);
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
        if(lineFile==null){
            return;
        }

        try {
            EditTimeView editText=findViewById(R.id.editTimeLayout);
                editText.setVisibility(GONE);
            init();
            initDepAriTimeView();
            initStopTimeView();
            nessTimeCreate();


        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast(getMainActivity().getString(R.string.undefined_error)+e.getMessage());
        }
    }

    private void nessTimeCreate() {
        final LinearLayout betweenStationTimeLayout = findViewById(R.id.betweenStationTimeLayout);
        for(int i=0;i<betweenStationTimeLayout.getChildCount();i++){
            betweenStationTimeLayout.getChildAt(i).clearFocus();
        }
        betweenStationTimeLayout.removeAllViews();
        int size = 0;
        int time = -1;
        BetweenTimeView nessTime;
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            nessTime=null;
            int stationIndex = train.getStationIndex(i);
            size++;
            if (train.timeExist(stationIndex,1)) {
                if (time >= 0) {
                    nessTime = new BetweenTimeView(getMainActivity(), train.getAriTime(stationIndex) - time,  size);
                }
                time = train.getAriTime(stationIndex);
                size = 0;
            }else if (train.timeExist(stationIndex,0)) {
                if (time >= 0) {
                    nessTime = new BetweenTimeView(getMainActivity(), train.getDepTime(stationIndex) - time, size);
                } else {
                    if (size != 0) {
                        nessTime = new BetweenTimeView(getMainActivity(), -1, size - 1);
                    }
                }
            }
            if (train.timeExist(stationIndex,0)) {
                time = train.getDepTime(stationIndex);
                size = 0;
            }
            if(nessTime!=null) {
                betweenStationTimeLayout.addView(nessTime);
            }

        }
        nessTime = new BetweenTimeView(getMainActivity(), -1, size);
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
    public void onTimeChanged(int station, int AD) {
        final LinearLayout departureTimeLayout = findViewById(R.id.departureTimeLayout);
        final LinearLayout arrivalTimeLayout = findViewById(R.id.arrivalTimeLayout);
        if(train.getStopType(station)==StationTime.STOP_TYPE_NOSERVICE){
            train.setStopType(station,StationTime.STOP_TYPE_STOP);
        }
        if(AD==Train.DEPART){
            ((TimeView)departureTimeLayout.getChildAt(station)).setTime(train.getTime(station,AD));
        }else{
            ((TimeView)arrivalTimeLayout.getChildAt(station)).setTime(train.getTime(station,AD));
        }
        upDateDepAriTimeView();
        initStopTimeView();
        nessTimeCreate();
    }

    @Override
    public void trainChanged(Train train) {
        init();
        initDepAriTimeView();
        initStopTimeView();
        nessTimeCreate();

    }

    @Override
    public void allTrainChange() {
        init();
        initDepAriTimeView();
        initStopTimeView();
        nessTimeCreate();

    }
}
