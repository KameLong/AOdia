package com.kamelong.aodia.EditTrainType;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

public class EditTrainTypeFragment extends AOdiaFragmentCustom {
    int fileIndex;
    LineFile lineFile;
    View fragmentContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            fileIndex = bundle.getInt(AOdia.FILE_INDEX, 0);
        } catch (Exception e) {
            SDlog.log(e);
        }
        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.traintype_edit_fragment, container, false);
        try {
            lineFile = getAOdia().getLineFile(fileIndex);
        } catch (Exception e) {
            SDlog.log(e);
        }
        return fragmentContainer;
    }

    private void init() {
        if (lineFile == null) {
            getAOdia().killFragment(this);
            return;
        }
        final LinearLayout typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear);
        typeListLinear.removeAllViews();
        for (int i = 0; i < lineFile.trainType.size(); i++) {
            typeListLinear.addView(new EditTrainTypeView(getMainActivity(), lineFile.trainType.get(i)));
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (lineFile == null) {
            getAOdia().killFragment(this);
            return;
        }

    }
    @Override
    public void onStart(){
        super.onStart();
        init();
        final FloatingActionButton copyButton = fragmentContainer.findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear);
                ArrayList<TrainType> copyTrainType = new ArrayList<>();
                for (int i = 0; i < lineFile.trainType.size(); i++) {
                    if (((EditTrainTypeView) typeListLinear.getChildAt(i)).checked) {
                        copyTrainType.add(lineFile.getTrainType().get(i));
                    }
                }
                getAOdia().copyTrainType = copyTrainType;
                SDlog.toast("列車種別をコピーしました");
            }
        });
        final FloatingActionButton deleteButton = fragmentContainer.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear);
                ArrayList<TrainType> deleteType = new ArrayList<>();
                for (int i = 0; i < lineFile.trainType.size(); i++) {
                    if (((EditTrainTypeView) typeListLinear.getChildAt(i)).checked) {
                        deleteType.add(lineFile.getTrainType().get(i));
                    }
                }
                for (TrainType type : deleteType) {
                    if (lineFile.deleteTrainType(type)) {

                    } else {
                        SDlog.toast(type.name + ":路線内で使われているため、削除できません");
                        break;
                    }
                }
                init();
            }
        });
        final FloatingActionButton pasteButton = fragmentContainer.findViewById(R.id.pasteButton);
        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear);
                ArrayList<TrainType> copyTrainType = getAOdia().copyTrainType;
                int i = 0;
                for (i = 0; i < lineFile.trainType.size(); i++) {
                    if (((EditTrainTypeView) typeListLinear.getChildAt(i)).checked) {
                        break;
                    }
                }
                for (TrainType type : copyTrainType) {
                    lineFile.addTrainType(i, type.clone());
                    i++;
                }
                SDlog.toast("列車種別を貼り付けました");
                init();
            }
        });
        final FloatingActionButton addButton = fragmentContainer.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout typeListLinear = fragmentContainer.findViewById(R.id.typeListLinear);
                ArrayList<TrainType> copyTrainType = getAOdia().copyTrainType;
                int i = 0;
                for (i = 0; i < lineFile.trainType.size(); i++) {
                    if (((EditTrainTypeView) typeListLinear.getChildAt(i)).checked) {
                        break;
                    }
                }
                lineFile.addTrainType(i, new TrainType());
                init();
            }
        });
    }


    @NonNull
    @Override
    public String getName() {
        try {
            String line = lineFile.name;
            if (line.length() > 10) {
                line = line.substring(0, 10);
            }
            return line + "\n" + "列車種別";
        } catch (Exception e) {
            return "列車種別";
        }
    }

    @Override
    public LineFile getLineFile() {
        return lineFile;
    }
}
