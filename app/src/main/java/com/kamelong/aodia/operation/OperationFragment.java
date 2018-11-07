package com.kamelong.aodia.operation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadata.AOdiaOperation;
import com.kamelong.aodia.diadata.AOdiaTrain;

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
        activity=(AOdiaActivity)getActivity();
        fragmentContainer=inflater.inflate(R.layout.operation_fragment, container, false);
        diaFile=activity.diaFiles.get(fileNum);
        if(diaFile==null){
            onDestroy();
            return fragmentContainer;
        }
        return fragmentContainer;
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();
        ArrayList<AOdiaOperation>operationList=diaFile.operationList.get(diaNum);
        ((LinearLayout)findViewById(R.id.opeListView)).removeAllViews();
        if(operationList.size()==0) {
            AOdiaOperation operation=new AOdiaOperation(diaFile,diaNum);
            operationList.add(operation);
            OperationView v=new OperationView(getActivity(),this,operation,diaFile,fileNum,diaNum);
            ((LinearLayout)findViewById(R.id.opeListView)).addView(v);
        }else{
            for(int i=0;i<operationList.size();i++){
                OperationView v=new OperationView(getActivity(),this,operationList.get(i),diaFile,fileNum,diaNum);
                ((LinearLayout)findViewById(R.id.opeListView)).addView(v);
            }
        }
        System.out.println(((EditText)((LinearLayout)findViewById(R.id.opeListView)).getChildAt(0).findViewById(R.id.opeNameEdit)).getEditableText().toString());

        findViewById(R.id.addNewOpe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewOpeView();
            }
        });

    }
    @Override
    public String fragmentName(){
        try {
            return diaFile.getDiaName(diaNum) + ":運用\n" + diaFile.getLineName();
        }catch(Exception e){
            e.printStackTrace();
            return "運用";
        }
    }
    public void addNewOpeView(OperationView opeView){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
        for(int i=0;i<listView.getChildCount();i++){
            if(listView.getChildAt(i)==opeView){
                AOdiaOperation ope=new AOdiaOperation(diaFile,diaNum);
                diaFile.operationList.get(diaNum).add(i,ope);
                listView.addView(new OperationView(getActivity(),this,ope,diaFile,fileNum,diaNum),i);
                break;
            }
        }
    }
    private void addNewOpeView(){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
                AOdiaOperation ope=new AOdiaOperation(diaFile,diaNum);
                diaFile.operationList.get(diaNum).add(ope);
                listView.addView(new OperationView(getActivity(),this,ope,diaFile,fileNum,diaNum));
    }

    public void deleteOpeView(OperationView operationView){
        final LinearLayout listView=(LinearLayout) findViewById(R.id.opeListView);
        for(int i=0;i<listView.getChildCount();i++){
            if(listView.getChildAt(i)==operationView){
                operationView.operation.removeAllTrain();
                diaFile.operationList.get(diaNum).remove(operationView.operation);
                listView.removeView(operationView);
                break;
            }
        }
    }



}
