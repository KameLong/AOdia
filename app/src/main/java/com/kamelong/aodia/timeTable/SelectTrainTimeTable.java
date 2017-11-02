package com.kamelong.aodia.timeTable;

import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.diadata.AOdiaTrain;

/**
 * Created by kame on 2017/10/18.
 */

public class SelectTrainTimeTable extends TimeTableFragment implements TrainSelectListener{
    private TrainSelectListener trainSelectListener=null;
    @Override
    public String fragmentName(){
        try {
            if (direct == 0) {
                return "運用列車選択　下り時刻表　" + "\n"+ diaFile.getDiaName(diaNumber)  + diaFile.getLineName();

            } else {
                return "運用列車選択　上り時刻表　" + "\n"+ diaFile.getDiaName(diaNumber)  + diaFile.getLineName();

            }
        }catch(Exception e){
            e.printStackTrace();
            return "e";
        }
    }

    @Override
    public void selectTrain(AOdiaTrain train) {
        if(this.trainSelectListener!=null){
            trainSelectListener.selectTrain(train);
        }
        getAOdiaActivity().killFragment(this);
    }
    public void setTrainSelectListener(TrainSelectListener l){
        this.trainSelectListener=l;
    }
}
