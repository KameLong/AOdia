package com.kamelong.aodia.EditStation;

public interface StationEditInterface {
    /**
     *
     * @param stationIndex 追加する駅index
     */
    void addNewStation(int stationIndex);
    void removeStation(int stationIndex);
    void renewStationName(int stationIndex);
}
