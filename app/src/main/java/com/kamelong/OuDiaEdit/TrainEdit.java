package com.kamelong.OuDiaEdit;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.StationTime;
import com.kamelong.OuDia.Train;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

public class TrainEdit extends Train{
    public TrainEdit(DiaFile diaFile, int direction) {
        super(diaFile, direction);
    }
    public void setDepartureTime(int station, long value) {
        //todo
    }
    public void setArrivalTime(int station, long value) {
        //todo
    }
    public void setStop(int station, long value) {
        //todo
    }

    public void setStopType(int station, long value) {
        //todo
    }

    /**
     * @param station 路線中の駅index, 上り時刻表では時刻表駅順の逆
     * @return 発時刻を返します　発時刻が存在しないときは-1が返ります
     */
    public int getDepartureTime(int station) {
        return stationTimes.get(station).depTime;
    }
    /**
     * @param station 路線中の駅index, 上り時刻表では時刻表駅順の逆
     * @return 着時刻を返します 着時刻が存在しないときは-1が返ります
     */
    public int getArrivalTime(int station) {
        return stationTimes.get(station).ariTime;
    }

    /**
     * @param station 路線中の駅index, 上り時刻表では時刻表駅順の逆
     * @return 発着番線を返します
     */
    public int getStop(int station) {
        return stationTimes.get(station).stopTrack;
    }
    /**
     * @param station 路線中の駅index, 上り時刻表では時刻表駅順の逆
     * @return 停車種別を返します
     */

    public int getStopType(int station) {
        return stationTimes.get(station).stopType;
    }

    /**
     * @param station
     * @return 発時刻か着時刻が時刻が存在する時 trueが帰ります
     */
    public boolean timeExist(int station) {
        return stationTimes.get(station).timeExist();
    }

    public boolean departExist(int station) {
        return stationTimes.get(station).timeExist(BOUND_OUT);
    }

    public boolean arriveExist(int station) {
        return stationTimes.get(station).timeExist(BOUND_IN);
    }

    public int getADTime(int station) {
        if (arriveExist(station)) {
            return getArrivalTime(station);
        }
        if (departExist(station)) {
            return getDepartureTime(station);
        }
        return -1;
    }

    public int getDATime(int station) {
        if (departExist(station)) {
            return getDepartureTime(station);
        }
        if (arriveExist(station)) {
            return getArrivalTime(station);
        }
        return -1;
    }

    /**
     * 始発駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     */
    public int startStation() {
        switch (direction) {
            case 0:
                for (int i = 0; i < stationTimes.size(); i++) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = stationTimes.size() - 1; i >= 0; i--) {
                    if (timeExist(i)) return i;
                }
                break;
        }
        return -1;
    }

    /**
     * 終着駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     *
     * @return
     */
    public int endStation() {
        switch (direction) {
            case 0:
                for (int i = stationTimes.size() - 1; i >= 0; i--) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = 0; i < stationTimes.size(); i++) {
                    if (timeExist(i)) return i;
                }
                break;
        }
        return -1;
    }

    /**
     * @param startStation
     * @param endStation
     * @return 任意の２駅間の所要時間
     */
    public int getRequiredTime(int startStation, int endStation) {
        if (timeExist(startStation) && timeExist(endStation)) {
            if ((endStation - startStation) * (1 - direction * 2) > 0) {
                return getADTime(endStation) - getDATime(startStation);

            } else {
                return getADTime(startStation) - getDATime(endStation);

            }
        } else {
            return -1;
        }

    }

    public int getPredictionTime(int station, int AD) {

        if (AD == 1 && arriveExist(station)) {
            return getArrivalTime(station);
        }
        if (timeExist(station)) {
            return getDATime(station);
        }
        if (getStopType(station) == StationTime.STOP_TYPE_NOVIA || getStopType(station) == STOP_TYPE_PASS) {
            //通過時間を予測します
            int afterTime = -1;//後方の時刻あり駅の発車時間
            int beforeTime = -1;//後方の時刻あり駅の発車時間
            int afterMinTime = 0;//後方の時刻あり駅までの最小時間
            int beforeMinTime = 0;//前方の時刻あり駅までの最小時間

            ArrayList<Integer> minstationTime = diaFile.getStationTime();

            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間
            for (int i = station + 1; i < diaFile.getStationNum(); i++) {
                if (getStopType(i) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i) ==StationTime. STOP_TYPE_NOVIA || getStopType(i - 1) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i - 1) == StationTime.STOP_TYPE_NOVIA) {
                    continue;
                }
                afterMinTime = afterMinTime + minstationTime.get(i) - minstationTime.get(i - 1);
                if (timeExist(i)) {
                    if(direction==0){
                        afterTime = getADTime(i);
                    }else{
                        afterTime = getDATime(i);
                    }
                    break;
                }
            }
            if (afterTime < 0) {
                SDlog.log("予測時間", "afterTime");
                //対象駅より先の駅で駅時刻が存在する駅がなかった
                return -1;
            }
            //対象駅より前方の駅で駅時刻が存在する駅までの最小所要時間と駅時刻
            int startStation = 0;
            for (int i = station; i > 0; i--) {
                if (getStopType(i) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i) == StationTime.STOP_TYPE_NOVIA || getStopType(i - 1) == StationTime.STOP_TYPE_NOSERVICE || getStopType(i - 1) == StationTime.STOP_TYPE_NOVIA) {
                    continue;
                }
                beforeMinTime = beforeMinTime + minstationTime.get(i) - minstationTime.get(i - 1);
                if (timeExist(i - 1)) {
                    if(direction==0){
                        beforeTime = getDATime(i - 1);
                    }else{
                        beforeTime = getADTime(i - 1);
                    }

                    startStation = i - 1;
                    break;
                }
            }
            if (beforeTime < 0) {
                return -1;
            }
            return getDepartureTime(startStation) + (afterTime - beforeTime) * beforeMinTime / (afterMinTime + beforeMinTime);
        }
        return -1;
    }

    public int getPredictionTime(int station) {
        return getPredictionTime(station, 0);
    }

    /**
     * 日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    public boolean checkDoubleDay() {
        int time = getDepartureTime(startStation());
        for (int i = startStation(); i < endStation(); i++) {
            if (timeExist(i)) {
                if (getDepartureTime(i) - time < -12 * 60 * 60 || getDepartureTime(i) - time > 12 * 60 * 60) {
                    SDlog.log("doubleDay");
                    return true;
                }
                time = getDepartureTime(i);
            }
        }
        return false;
    }

    public void endTrain(int station) {
        setDepartureTime(station, -1);
        if (direction == 0) {
            for (int i = station + 1; i < getStationNum(); i++) {
                stationTimes.get(i).reset();
            }
        } else {
            for (int i = station - 1; i >= 0; i--) {
                stationTimes.get(i).reset();
            }
        }
    }

    public void startTrain(int station) {
        setArrivalTime(station, -1);
        if (direction == 0) {
            for (int i = station - 1; i >= 0; i--) {
                stationTimes.get(i).reset();
            }
        } else {
            for (int i = station + 1; i < getStationNum(); i++) {
                stationTimes.get(i).reset();
            }
        }
    }

    /**
     * staiton 以下にtrainを接続します
     */
    public void combine(Train train, int station) {
        setDepartureTime(station, train.getDepartureTime(station));
        if (direction == 0) {
            for (int i = station + 1; i < getStationNum(); i++) {
                stationTimes.set(i,train.stationTimes.get(i).clone());
            }
        } else {
            for (int i = station - 1; i >= 0; i--) {
                stationTimes.set(i,train.stationTimes.get(i).clone());
            }
        }

    }


}
