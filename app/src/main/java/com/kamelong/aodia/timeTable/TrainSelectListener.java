package com.kamelong.aodia.timeTable;

import com.kamelong.aodia.diadataOld.AOdiaTrain;

import java.util.ArrayList;

/**
 * 列車が選択されたことを通知するリスナー
 */

public interface TrainSelectListener{
    public void selectTrain(AOdiaTrain train);
    public void selectTrain(ArrayList<AOdiaTrain> train);


}
