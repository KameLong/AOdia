package com.kamelong.aodia.EditTrainType;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SDlog;

public class EditTrainTypeFragment extends AOdiaFragment {
    int fileIndex = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {//まずBundleを確認し、fileNum,diaNumber,directを更新する
            Bundle bundle = getArguments();
            fileIndex = bundle.getInt("fileIndex", 0);
        } catch (Exception e) {
            SDlog.log(e);
        }
        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.edit_traintype_fragment, container, false);
        try {
            diaFile = getAOdiaActivity().diaFiles.get(fileIndex);
        }catch (Exception e){
            SDlog.log(e);
        }
        return fragmentContainer;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(diaFile==null){
            getAOdiaActivity().killFragment(getAOdiaActivity().fragmentIndex);
            return;
        }
        final LinearLayout typeListLinear=(LinearLayout) findViewById(R.id.typeListLinear);
        for(int i=0;i<diaFile.trainType.size();i++){
            typeListLinear.addView(new EditTrainTypeView(getAOdiaActivity(),diaFile.trainType.get(i)));
        }
        Button addButton=(Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrainType trainType=new TrainType();
                diaFile.trainType.add(trainType);
                typeListLinear.addView(new EditTrainTypeView(getAOdiaActivity(),trainType));

            }
        });

    }
}
