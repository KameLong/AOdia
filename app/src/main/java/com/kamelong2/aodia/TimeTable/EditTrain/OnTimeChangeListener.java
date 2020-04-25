package com.kamelong2.aodia.TimeTable.EditTrain;

/**
 * 時刻が変更させた時のイベント
 */
public interface OnTimeChangeListener {
    void onTimeChanged(int station,int AD);
}
