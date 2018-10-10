package com.kamelong.aodia.TimeTable.EditTrain;

import com.kamelong.OuDia.Train;

public interface OnTrainEditInterface {
    void trainSplit(Train train,int station);
    void trainCombine(Train train,int station);
    void trainCopy(Train train);
    void trainInsert(Train train);
    void trainDelete(Train train);

}
