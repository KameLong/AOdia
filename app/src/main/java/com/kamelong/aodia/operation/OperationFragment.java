package com.kamelong.aodia.operation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadataOld.AOdiaOperation;

import java.util.ArrayList;

/**
 * 運用を確認するFragment
 */

public class OperationFragment extends AOdiaFragment{
    ArrayList<AOdiaOperation>operationArrayList=new ArrayList<>();
    private int fileNum=0;
    private int diaNum=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {

            Bundle bundle = getArguments();
            fileNum=bundle.getInt("fileNum");
            diaNum=bundle.getInt("diaNum");
        }catch(Exception e){
            SdLog.log(e);
        }
        setAodiaActivity((AOdiaActivity) getAodiaActivity());
        setFragmentContainer(inflater.inflate(R.layout.operation_fragment, container, false));
        setDiaFile(getAodiaActivity().getDiaFiles().get(fileNum));
        if(getDiaFile() ==null){
            onDestroy();
            return getFragmentContainer();
        }
        return getFragmentContainer();
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public String fragmentName(){
        try {
            return "";
        }catch(Exception e){
            e.printStackTrace();
            return "運用";
        }
    }
    public void addNewOpeView(OperationView opeView){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
        for(int i=0;i<listView.getChildCount();i++){
            if(listView.getChildAt(i)==opeView){
                AOdiaOperation ope=new AOdiaOperation(getDiaFile(),diaNum);
                listView.addView(new OperationView(getAodiaActivity(),this,ope, getDiaFile(),fileNum,diaNum),i);
                break;
            }
        }
    }
    private void addNewOpeView(){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
                AOdiaOperation ope=new AOdiaOperation(getDiaFile(),diaNum);
                listView.addView(new OperationView(getAodiaActivity(),this,ope, getDiaFile(),fileNum,diaNum));
    }

    public void deleteOpeView(OperationView operationView){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
        for(int i=0;i<listView.getChildCount();i++){
            if(listView.getChildAt(i)==operationView){
                operationView.operation.removeAllTrain();
                listView.removeView(operationView);
                break;
            }
        }
    }



}
