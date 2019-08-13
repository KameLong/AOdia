package com.kamelong.OuDia;

import com.kamelong.aodia.SDlog;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Train implements Cloneable{
    public static long count2=0;
    public static final int DEPART = 0;
    public static final int ARRIVE = 1;
    public static final int BOUND_OUT=0;
    public static final int BOUND_IN=1;

    public DiaFile diaFile;
    /**
     この列車の列車方向を示します。

     コンストラクタで決まります。
     */
    public int direction = BOUND_OUT;


    /**
     * 列車種別のindex
     */
    public int type = 0;
    /**
     * 列車番号
     */
    public String number = "";
    /**
     * 列車名
     */
    public String name = "";
    /**
     * 列車号数
     */
    public String count = "";
    /**
     * 備考
     */
    public String remark = "";
    /**
     この列車から次の列車への接続を、種別変更とみなすかどうか
     この列車と次の列車が同方向、この列車の末端作業が次列車接続(なし)の場合に、
     この値がtrueなら、時刻表ビューが表示モードの時、同一行に表示します
     */
    public Boolean typeChange=false;

    /**
     この列車の各駅の時刻。
     要素数は、『駅』(DiaFile.stations) の数に等しくなります。
     添え字は『駅index』です。
     初期状態では、要素数は 0 となります。
     */
    public ArrayList<StationTime> stationTimes=new ArrayList<>();

    public Train(DiaFile diaFile, int direction) {
        this.diaFile = diaFile;
        this.direction = direction;
    }
    public void setValue(String title,String value){
        switch (title) {
            case "Syubetsu":
                type = Integer.parseInt(value);
                break;
            case "Ressyabangou":
                number = value;
                break;
            case "Ressyamei":
                name = value;
                break;
            case "Gousuu":
                count = value;
                break;
            case "EkiJikoku":
                setOuDiaTime(value.split(",", -1));
                break;
            case "RessyaTrack":
                setOuDiaTrack(value.split(",", -1));
                break;
            case "Bikou":
                remark = value;
                break;
        }
        if(title.startsWith("Operation")){
            //運用処理　未実装
        }
    }

    /**
     * Ekijikoku行の読み込みを行う
     * @param value
     */
    private void setOuDiaTime(String[] value) {
        stationTimes=new ArrayList<>();
        for(int i=0;i<diaFile.getStationNum();i++){
            stationTimes.add(new StationTime());
        }
        for (int i = 0; i < value.length && i < diaFile.getStationNum(); i++) {
            int station = direction * (diaFile.getStationNum() - 1) + (1 - 2 * direction) * i;
            stationTimes.get(station).setStationTime(value[i]);
        }
    }

    /**
     * OuDia2ndの番線行の読み込みを行う。
     * @param value
     */
    private void setOuDiaTrack(String[] value) {
        if (direction == 1) {
            System.out.println("test");
        }
        for (int i = 0; i < value.length && i < time.length; i++) {
            int station = direction * (stationNum - 1) + (1 - 2 * direction) * i;

            if (value[i].length() == 0) {
                setStop(station, 0);
                continue;
            }
            if (value[i].contains(";")) {
                setStop(station, Integer.parseInt(value[i].split(";")[0]));
                if (station == startStation()) {
                    if (value[i].split(";", -1)[1].startsWith("2")) {
                        leaveYard = true;
                        if (value[i].split(";")[1].contains("/")) {
                            operationName = value[i].split(";")[1].split("/", -1)[1];
                        }
                    }
                } else {
                    if (value[i].split(";")[1].startsWith("2")) {
                        goYard = true;
                    }
                }
            } else {
                setStop(station, Integer.valueOf(value[i]));
            }
        }
    }
    public void saveToFile(PrintWriter out) throws Exception {
        out.println("Ressya.");
        if (direction == 0) {
            out.println("Houkou=Kudari");
        } else {
            out.println("Houkou=Nobori");
        }
        out.println("Syubetsu=" + type );
        if (number.length() > 0) {
            out.println("Ressyabangou=" + number );
        }
        if (name.length() > 0) {
            out.println("Ressyamei=" + name );
        }
        if (count.length() > 0) {
            out.println("Gousuu=" + count );
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(true));
        out.println("RessyaTrack=" + getTrackOudia() );
        if (remark.length() > 0) {
            out.println("Bikou=" + remark );
        }
        out.println(".");

    }
    public void saveToOuDiaFile(PrintWriter out) throws Exception {
        out.println("Ressya.");
        if (direction == 0) {
            out.println("Houkou=Kudari");
        } else {
            out.println("Houkou=Nobori");
        }
        out.println("Syubetsu=" + type );
        if (number.length() > 0) {
            out.println("Ressyabangou=" + number );
        }
        if (name.length() > 0) {
            out.println("Ressyamei=" + name );
        }
        if (count.length() > 0) {
            out.println("Gousuu=" + count );
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(false));
        if (remark.length() > 0) {
            out.println("Bikou=" + remark );
        }
        out.println(".");

    }

    /**
     * OuDia形式の駅時刻行を作成します。
     * @param secondFrag trueの時oudia2nd形式に対応します。
     * @return
     */
    protected String getEkijikokuOudia(boolean secondFrag) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < stationTimes.size(); i++) {
            int station = getStationIndex(i);
            result.append(stationTimes.get(station).getOuDiaString(secondFrag));
            result.append(",");
        }
        return result.toString();
    }


    /**
     * 上り下りの時刻表駅順から、路線駅順を返します。
     * 下りの時は時刻表駅順は路線駅順と同じ
     * 上りの時は時刻表駅順は路線駅順の逆になります。
     */
    public int getStationIndex(int index){
        if(direction==0){
            return index;
        }else{
            return stationTimes.size()-index-1;
        }
    }

    @Override
    public Train clone() throws CloneNotSupportedException {
        Train result=(Train)super.clone();
        result.stationTimes=new ArrayList<>();
        for(StationTime time:stationTimes){
            result.stationTimes.add(time.clone());
        }
        return result;
    }

    public void setDepartureTime(int station, long value) {
        if (value < 0) {
            time[station] = time[station] & 0xFFFFFFFFFF000000L;
            return;
        }

        if (value > 0x7FFFFF) {
            return;
        }
        time[station] = time[station] & 0xFFFFFFFFFF000000L;
        time[station] = time[station] | 0x0000000000800000L;
        time[station] = time[station] | (value);

    }

    public int getDepartureTime(int station) {
        if ((time[station] & 0x0000000000800000L) == 0) {
            return -1;
        }
        int result = (int) (time[station] & 0x00000000007FFFFFL);
        if (result < diaFile.diagramStartTime) {
            return result + 24 * 3600;
        } else {
            return result;
        }
    }

    public void setArrivalTime(int station, long value) {
        if (value < 0) {
            time[station] = time[station] & 0xFFFF000000FFFFFFL;
            return;
        }
        if (value > 0x7FFFFF) {
            return;
        }
        time[station] = time[station] & 0xFFFF000000FFFFFFL;
        time[station] = time[station] | 0x0000800000000000L;
        time[station] = time[station] | (value << 24);
    }

    public int getArrivalTime(int station) {

        if ((time[station] & 0x0000800000000000L) == 0) {
            return -1;
        }
        int result = (int) ((time[station] & 0x00007FFFFF000000L) >>> 24);
        if (result < diaFile.diagramStartTime) {
            return result + 24 * 3600;
        } else {
            return result;
        }
    }

    public void setStop(int station, long value) {
        if (value < 0 || value > 255) {
            return;
        }
        time[station] = time[station] & 0xFF00FFFFFFFFFFFFL;
        time[station] = time[station] | (value << 48);
    }

    public int getStop(int station) {
        return (int) ((time[station] & 0x00FF000000000000L) >>> 48);
    }

    public void setStopType(int station, long value) {
        if (value < 0 || value > 3) {
            return;
        }
        time[station] = time[station] & 0xF0FFFFFFFFFFFFFFL;
        time[station] = time[station] | (value << 56);
    }

    public int getStopType(int station) {
        return (int) ((time[station] & 0x0F00000000000000L) >>> 56);
    }

    public boolean timeExist(int station) {
        count2++;

        return (time[station] & 0x0000800000800000L) != 0;
    }

    public boolean departExist(int station) {
        count2++;

        return (time[station] & 0x0000000000800000L) != 0;
    }

    public boolean arriveExist(int station) {
        count2++;

        return (time[station] & 0x0000800000000000L) != 0;
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
                for (int i = 0; i < time.length; i++) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = time.length - 1; i >= 0; i--) {
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
                for (int i = time.length - 1; i >= 0; i--) {
                    if (timeExist(i)) return i;
                }
                break;
            case 1:
                for (int i = 0; i < time.length; i++) {
                    if (timeExist(i)) return i;
                }
                break;
        }
        return -1;
    }

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
        if (getStopType(station) == STOP_TYPE_NOVIA || getStopType(station) == STOP_TYPE_PASS) {
            //通過時間を予測します
            int afterTime = -1;//後方の時刻あり駅の発車時間
            int beforeTime = -1;//後方の時刻あり駅の発車時間
            int afterMinTime = 0;//後方の時刻あり駅までの最小時間
            int beforeMinTime = 0;//前方の時刻あり駅までの最小時間

            ArrayList<Integer> minstationTime = diaFile.getStationTime();

            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間
            for (int i = station + 1; i < diaFile.getStationNum(); i++) {
                if (getStopType(i) == STOP_TYPE_NOSERVICE || getStopType(i) == STOP_TYPE_NOVIA || getStopType(i - 1) == STOP_TYPE_NOSERVICE || getStopType(i - 1) == STOP_TYPE_NOVIA) {
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
                if (getStopType(i) == STOP_TYPE_NOSERVICE || getStopType(i) == STOP_TYPE_NOVIA || getStopType(i - 1) == STOP_TYPE_NOSERVICE || getStopType(i - 1) == STOP_TYPE_NOVIA) {
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
            for (int i = station + 1; i < stationNum; i++) {
                time[i] = 0;
            }
        } else {
            for (int i = station - 1; i >= 0; i--) {
                time[i] = 0;
            }
        }
    }

    public void startTrain(int station) {
        setArrivalTime(station, -1);
        if (direction == 0) {
            for (int i = station - 1; i >= 0; i--) {
                time[i] = 0;
            }
        } else {
            for (int i = station + 1; i < stationNum; i++) {
                time[i] = 0;
            }
        }
    }

    public void combine(Train train, int station) {
        setDepartureTime(station, train.getDepartureTime(station));
        if (direction == 0) {
            for (int i = station + 1; i < stationNum; i++) {
                time[i] = train.time[i];
            }
        } else {
            for (int i = station - 1; i >= 0; i--) {
                time[i] = train.time[i];
            }
        }

    }


    public void editStationSubmit(ArrayList<Integer> editStation) {
        long[] newTime = new long[editStation.size()];
        for (int i = 0; i < editStation.size(); i++) {
            if (editStation.get(i) < 0) {
                newTime[i] = 0;
                if (i > 0) {
                    switch ((int) ((newTime[i - 1] & 0x0F00000000000000L) >>> 56)) {
                        case 0:
                            newTime[i] = 0x0000000000000000L;
                            break;
                        case 3:
                            newTime[i] = 0x0300000000000000L;
                            break;
                        default:
                            newTime[i] = 0x0200000000000000L;

                    }
                    newTime[i] = newTime[i - 1] & 0x0F00000000000000L;
                }
            } else {
                newTime[i] = time[editStation.get(i)];
            }
        }
        if (!editStation.contains(endStation())) {
            goYard = false;
        }
        if (!editStation.contains(startStation())) {
            leaveYard = false;
        }
        time = newTime;
        stationNum = time.length;
    }

    public boolean isnull() {
        for (int i = 0; i <diaFile.getStationNum(); i++) {
            if (stationTimes.get(i).stopType!=0) return false;
        }
        return true;
    }


}
