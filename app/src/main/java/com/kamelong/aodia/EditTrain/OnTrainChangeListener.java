package com.kamelong.aodia.EditTrain;

import com.kamelong.aodia.AOdiaData.Train;

public interface OnTrainChangeListener {
    void trainChanged(Train train);
    void allTrainChange();
}
