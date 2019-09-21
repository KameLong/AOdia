package com.kamelong2.aodia.TimeTable.EditTrain;

import com.kamelong2.OuDia.Train;

public interface OnTrainEditInterface {
    void trainSplit(Train train,int station);
    void trainCombine(Train train,int station);
    void trainCopy(Train train);
    void trainInsert(Train train);
    void trainDelete(Train train);

}
