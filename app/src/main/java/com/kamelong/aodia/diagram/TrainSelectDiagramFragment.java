package com.kamelong.aodia.diagram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.diadataOld.AOdiaOperation;
import com.kamelong.aodia.diadataOld.AOdiaTrain;
import com.kamelong.aodia.timeTable.TrainSelectListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class TrainSelectDiagramFragment extends DiagramFragment{
    TrainSelectListener trainSelectListener=null;
    AOdiaOperation operation=null;

    /**
     * 初期設定をする
     * bundleで必要なデータを送ること
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        try {

            Bundle bundle = getArguments();
            diaNumber = bundle.getInt("diaN");
            fileNum=bundle.getInt("fileNum");
        }catch(Exception e){
            SdLog.log(e);
        }
        fragmentContainer = inflater.inflate(R.layout.diagram, container, false);

        return fragmentContainer;
    }


    /**
     * Viewが生成れると各種Viewを生成し、追加する。
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(diagramView!=null) {
            ArrayList<AOdiaTrain> trains=new ArrayList<>();
            for(AOdiaTrain train:operation.trains){
                trains.add(train);
            }

            diagramView.focusTrain = trains;
            System.out.println(diagramView.focusTrain);
            System.out.println(operation.trains);

            diagramView.invalidate();
        }
        findViewById(R.id.setOperation).setVisibility(View.VISIBLE);
        findViewById(R.id.setOperation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trainSelectListener!=null){
                    Collections.sort( diagramView.focusTrain, new Comparator<AOdiaTrain>(){
                        @Override
                        public int compare(AOdiaTrain a, AOdiaTrain b){
                            int aTime=a.getTime(a.getStartStation()).getDATime();
                            return 0;
                        }
                    });
                    trainSelectListener.selectTrain(diagramView.focusTrain);
                }

                getAOdiaActivity().killFragment(TrainSelectDiagramFragment.this);

            }
        });
    }

    @Override
    protected void longPress(MotionEvent event){
        //長押ししたときはDiagramViewのfocusTrainを指定する
        int x=(int)event.getX();
        int y=(int)event.getY();

        if(x>stationView.getWidth()&&y>timeView.getHeight()){
            diagramView.setTrain((int )event.getX()+(int)scrollX-stationView.getWidth(),(int )event.getY()+(int)scrollY-timeView.getHeight());
        }
    }
    public void setOnTrainSelectListener(TrainSelectListener listener, AOdiaOperation operation){
        this.trainSelectListener=listener;
        this.operation=operation;
        if(diagramView!=null) {
            ArrayList<AOdiaTrain> trains=new ArrayList<>();
            for(AOdiaTrain train:operation.trains){
                trains.add(train);
            }

            diagramView.focusTrain = trains;
            System.out.println(diagramView.focusTrain);
            System.out.println(operation.trains);

            diagramView.invalidate();
        }

    }
    @Override
    public String fragmentName() {
        return "";
    }


}
