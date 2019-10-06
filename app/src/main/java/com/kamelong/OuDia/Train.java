package com.kamelong.OuDia;

import com.kamelong.tool.Color;
import com.kamelong.tool.SDlog;

import java.io.PrintWriter;
import java.util.ArrayList;

import static com.kamelong.OuDia.StationTime.STOP_TYPE_NOSERVICE;
import static com.kamelong.OuDia.StationTime.STOP_TYPE_NOVIA;
import static com.kamelong.OuDia.StationTime.STOP_TYPE_PASS;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 列車１つを表します
 */
public class Train implements Cloneable {
    public LineFile lineFile;
    /**
     この列車の列車方向を示します。

     コンストラクタで決まります。
     */
    public int direction = DOWN;


    public static final int DEPART = 0;
    public static final int ARRIVE = 1;
    //下り
    public static final int DOWN =0;
    //上り
    public static final int UP =1;



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
     この列車の各駅の時刻。
     要素数は、『駅』(DiaFile.stations) の数に等しくなります。
     添え字は『駅index』です。
     初期状態では、要素数は 0 となります。
     */
    public ArrayList<StationTime> stationTimes=new ArrayList<>();

    /**
     * デフォルトコンストラクタ
     * @param lineFile この列車が含まれるDiaFile
     * @param direction　進行方向　上り:1,下り:0
     */
    public Train(LineFile lineFile, int direction) {
        this.lineFile = lineFile;
        this.direction = direction;
        this.stationTimes=new ArrayList<>();
        for(int i = 0; i< lineFile.getStationNum(); i++){
            stationTimes.add(new StationTime(this));
        }
    }

    /**
     * OuDiaファイルの１行を読み込みます
     */
    void setValue(String title,String value){
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
            if(title.contains(".")){
                title=title.substring(9);
                String[] stations=title.split("\\.",-1);
                int index=getStationIndex(Integer.parseInt(stations[0].substring(0,stations[0].length()-1)));
                ArrayList<StationTimeOperation> operationList;
                if(stations[0].substring(stations[0].length()-1).equals("B")){
                    operationList=stationTimes.get(index).beforeOperations;
                }else{
                    operationList=stationTimes.get(index).afterOperations;
                }
                for(int i=1;i<stations.length;i++){
                    int index2=Integer.parseInt(stations[i].substring(0,stations[i].length()-1));
                    if(stations[i].substring(stations[i].length()-1).equals("B")){
                        operationList=operationList.get(index2).beforeOperation;
                    }else{
                        operationList=operationList.get(index2).afterOperation;
                    }
                }
                for(String s :value.split(",",-1)){
                    operationList.add(new StationTimeOperation(s));
                }


            }else{
                int index=getStationIndex(Integer.parseInt(title.substring(9,title.length()-1)));
                if(title.substring(title.length()-1).equals("B")){
                    for(String s:value.split(",")){
                        stationTimes.get(index).beforeOperations.add(new StationTimeOperation(s));
                    }
                }else{
                    for(String s:value.split(",")){
                        stationTimes.get(index).afterOperations.add(new StationTimeOperation(s));
                    }
                }

            }
        }
    }

    /**
     * Ekijikoku行の読み込みを行う
     * @param value
     */
    private void setOuDiaTime(String[] value) {
        stationTimes=new ArrayList<>();
        for(int i = 0; i< lineFile.getStationNum(); i++){
            stationTimes.add(new StationTime(this));
        }
        for (int i = 0; i < value.length && i < lineFile.getStationNum(); i++) {
            stationTimes.get(getStationIndex(i)).setStationTime(value[i]);
        }

    }

    /**
     * OuDia2ndの番線行の読み込みを行う。
     * @param value
     */
    private void setOuDiaTrack(String[] value) {
        for (int i = 0; i < value.length && i < stationTimes.size(); i++) {
            stationTimes.get(getStationIndex(i)).setTrack(value[i]);
        }
    }

    /**
     * OuDiaSecond形式で保存します
     * @param out
     */
    void saveToFile(PrintWriter out){
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
        if (remark.length() > 0) {
            out.println("Bikou=" + remark );
        }
        if(getStartStation()>=0) {
            if (stationTimes.get(getStartStation()).beforeOperations.size() > 0) {
                saveOperationToFile(out, stationTimes.get(getStartStation()).beforeOperations, "Operation" + getStationIndex(getStartStation()) + "B");
            }
            if (stationTimes.get(getEndStation()).afterOperations.size() > 0) {
                saveOperationToFile(out, stationTimes.get(getEndStation()).afterOperations, "Operation" + getStationIndex(getEndStation()) + "A");
            }
            }
            out.println(".");
    }
    private void saveOperationToFile(PrintWriter out,ArrayList<StationTimeOperation>target,String title){
        if(target.size()==0)return;
        String result=title+"=";
        for(int i=0;i<target.size();i++) {
            result+=target.get(i).getOuDiaString()+",";
        }
        out.println(result.substring(0,result.length()-1));


    }
    void saveToOuDiaFile(PrintWriter out){
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
    private String getEkijikokuOudia(boolean secondFrag) {
        StringBuilder result = new StringBuilder();
        if(stationTimes.size()> lineFile.getStationNum()){
            System.out.println("駅数オーバーフロー");
            return "";
        }
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
            return lineFile.getStationNum()-index-1;
        }
    }

    /**
     *
     * @param lineFile 親LineFile
     * @return コピーした列車
     */
    public Train clone(LineFile lineFile){
        try {
            Train result = (Train) super.clone();
            result.lineFile=lineFile;
            result.stationTimes = new ArrayList<>();
            for (StationTime time : stationTimes) {
                StationTime clone=time.clone(this);
                clone.train=result;
                result.stationTimes.add(clone);
            }
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Train(lineFile,direction);
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */


    /**
     * 駅数を返します
     */
    public int getStationNum(){
        return stationTimes.size();
    }

    /**
     * この列車がすべての駅で運行なしの場合、
     * 使用されていないnull列車とします
     */
    public boolean isnull() {
        for (int i = 0; i < lineFile.getStationNum(); i++) {
            if (stationTimes.get(i).stopType!=0) return false;
        }
        return true;
    }

    /**
     * 列車の始発駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    public int getStartStation(){
        if(direction==0){
            for(int i=0;i<stationTimes.size();i++){
                switch (getStopType(i)){
                    case 1:
                    case 2:
                        return i;
                }
            }
        }else{
            for(int i=stationTimes.size()-1;i>=0;i--){
                switch (getStopType(i)){
                    case 1:
                    case 2:
                        return i;
                }
            }

        }
        return -1;
    }
    /**
     * 列車の時刻が存在する最初の駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    public int getTimeStartStation(){
        if(direction==0){
            for(int i=0;i<stationTimes.size();i++){
                if(timeExist(i))return i;
            }
        }else{
            for(int i=stationTimes.size()-1;i>=0;i--){
                if(timeExist(i))return i;
            }

        }
        return -1;
    }
    /**
     * 列車の種着駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    public int getEndStation(){
        if(direction==1){
            for(int i=0;i<stationTimes.size();i++){
                switch (getStopType(i)){
                    case 1:
                    case 2:
                        return i;
                }
            }
        }else{
            for(int i=stationTimes.size()-1;i>=0;i--){
                switch (getStopType(i)){
                    case 1:
                    case 2:
                        return i;
                }
            }

        }
        return -1;
    }
    /**
     * 列車の種着駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    public int getTimeEndStation(){
        if(direction==1){
            for(int i=0;i<stationTimes.size();i++){
                if(timeExist(i))return i;
            }
        }else{
            for(int i=stationTimes.size()-1;i>=0;i--){
                if(timeExist(i))return i;
            }

        }
        return -1;
    }




    public String getOperationNumber() {
        //todo
        return "";
    }


    /**
     * 列車の文字色
     */
    public Color getTextColor() {
        return lineFile.trainType.get(type).textColor;
    }

    /**
     * 列車の種別名
     */
    public String getTypeName() {
        return lineFile.trainType.get(type).name;
    }

    /**
     * 列車の種別略称
     */
    public String getTypeShortName() {
        return lineFile.trainType.get(type).shortName;
    }

    /**
     * 指定駅の停車タイプを返します
     * @return int(0-3)
     */

    public int getStopType(int stationIndex) {
        if(stationIndex<0||stationIndex>=getStationNum()){
            return STOP_TYPE_NOSERVICE;
        }
        return stationTimes.get(stationIndex).stopType;
    }

    /**
     * 指定駅の停車タイプを設定します
     */
    public void setStopType(int stationIndex, int type) {
        stationTimes.get(stationIndex).stopType=(byte)type;
        if(type==0){
            stationTimes.get(stationIndex).setAriTime(-1);
            stationTimes.get(stationIndex).setDepTime(-1);
        }
    }

    /**
     * 指定駅に有効な時刻が存在するか（着時刻、発時刻の片方でもあればよい)
     */
    public boolean timeExist(int stationIndex) {
        return stationTimes.get(stationIndex).timeExist();
    }
    /**
     * 指定駅に有効な時刻が存在するか（着時刻、発時刻別)
     * AD=0:dep,AD=1:Ari
     */

    public boolean timeExist(int stationIndex, int AD) {
        return stationTimes.get(stationIndex).timeExist(AD);
    }

    /**
     * 着時刻取得
     */
    public int getDepTime(int station) {
        return stationTimes.get(station).getDepTime();
    }

    /**
     * 着時刻設定
     */
    public void setDepTime(int station, int value) {
        stationTimes.get(station).setDepTime(value);
    }

    /**
     * 発時刻取得
     */
    public int getAriTime(int station) {
        return stationTimes.get(station).getAriTime();
    }

    /**
     * 発時刻設定
     */
    public void setAriTime(int station, int value) {
        stationTimes.get(station).setAriTime(value);
    }

    /**
     * 時刻取得(発時刻、着時刻別）
     */
    public int getTime(int station,int AD){
        if(AD==0){
            return  getDepTime(station);
        }else{
            return getAriTime(station);
        }
    }
    /**
     * 時刻取得(発時刻、着時刻別）
     * useOther=trueの時、該当時刻が存在しないとき、代わりに同一駅の（発時刻、着時刻)を使用する。
     * 両方ともないときは-1
     */
    public int getTime(int station,int AD,boolean useOther){
        if(useOther){
            if(timeExist(station,AD)){
                return getTime(station,AD);
            }
            return getTime(station,(AD+1)%2);

        }
        return getTime(station,AD);
    }
    /**
     * 時刻設定(発時刻、着時刻別）
     */

    public void setTime(int station,int AD,int time){
        if(AD==0){
            setDepTime(station,time);
        }else{
            setAriTime(station,time);
        }
    }

    /**
     * 発着番線取得
     */
    public int getStopTrack(int station) {
        int result=stationTimes.get(station).stopTrack;
        if(result<0){
            return lineFile.station.get(station).stopMain[direction];
        }
        return result;
    }

    /**
     * 発着番線設定
     */
    public void setStopTrack(int station, int value) {
        stationTimes.get(station).stopType=(byte)value;
    }

    /**
     * 路線外始発駅名
     * 路線外始発駅が存在しない場合はnullが返る
     */
    public String getOuterStartStationName() {
        int startStation=getStartStation();
        if(startStation<0){
            return null;
        }
        for(StationTimeOperation operation:stationTimes.get(startStation).beforeOperations){
            if(operation.operationType==4){
                return lineFile.getStation(startStation).getOuterStationTimeTableName(operation.intData1);
            }
        }
        return null;
    }
    public int getOuterStartStation(){
        int startStation=getStartStation();
        if(startStation<0){
            return -1;
        }
        for(StationTimeOperation operation:stationTimes.get(startStation).beforeOperations){
            if(operation.operationType==4){
                return operation.intData1;
            }
        }
        return -1;
    }

    /**
     * 路線外始発駅始発時刻
     */
    public int getOuterStartTime() {
        int startStation=getStartStation();
        if(startStation<0){
            return -1;
        }
        for(StationTimeOperation operation:stationTimes.get(startStation).beforeOperations){
            if(operation.operationType==4){
                return operation.time1;
            }
        }
        return -1;
    }

    /**
     * 路線外終着駅名
     * 路線外終着駅が存在しない場合はnullが返る
     */
    public String getOuterEndStationName() {
        int endStation=getEndStation();
        if(endStation<0){
            return null;
        }
        for(StationTimeOperation operation:stationTimes.get(endStation).afterOperations){
            if(operation.operationType==4){
                return lineFile.getStation(endStation).getOuterStationTimeTableName(operation.intData1);
            }
        }
        return null;
    }
    public int getOuterEndStation(){
        int endStation=getEndStation();
        if(endStation<0){
            return -1;
        }
        for(StationTimeOperation operation:stationTimes.get(endStation).afterOperations){
            if(operation.operationType==4){
                return operation.intData1;
            }
        }
        return -1;
    }


    /**
     * 路線外終着駅時刻
     */
    public int getOuterEndTime() {
        int endStation=getEndStation();
        if(endStation<0){
            return -1;
        }
        for(StationTimeOperation operation:stationTimes.get(endStation).afterOperations){
            if(operation.operationType==4){
                return operation.time1;
            }
        }
        return -1;
    }

    /**
     * 全駅の時刻をshift秒移動させる
     * @param shift
     */
    public void shiftTime(int shift){
        for(StationTime time:stationTimes){
            time.shiftDep(shift);
            time.shiftAri(shift);
        }
    }

    /**
     * 当駅始発にする
     * @param stationIndex
     */
    public void startAtThisStation(int stationIndex){
        if(direction==0){
            for (int i=0;i<stationIndex;i++){
                stationTimes.get(i).reset();
            }
        }else{
            for (int i=stationIndex+1;i<getStationNum();i++){
                stationTimes.get(i).reset();
            }

        }
        setAriTime(stationIndex,-1);
    }
    /**
     * 当駅止めにする
     * @param stationIndex
     */
    public void endAtThisStation(int stationIndex){
        if(direction==0){
            for (int i=stationIndex+1;i<getStationNum();i++){
                stationTimes.get(i).reset();
            }
        }else{
            for (int i=0;i<stationIndex;i++){
                stationTimes.get(i).reset();
            }
        }
        setDepTime(stationIndex,-1);
    }

    /**
     * 列車を結合する
     * 結合駅の出発時刻はotherのものを用いる
     * @param other
     */
    public void conbine(Train other){
        int endStation=this.getEndStation();
        if(direction==0){
            for(int i=endStation+1;i<getStationNum();i++){
                stationTimes.set(i,other.stationTimes.get(i).clone(this));
            }
        }else{
            for(int i=0;i<endStation;i++){
                stationTimes.set(i,other.stationTimes.get(i).clone(this));
            }
        }
        setDepTime(endStation,other.getDepTime(endStation));
    }

    /**
     * 2駅間の所要時間を返す。もし片方の駅に時刻がなければ-1を返す
     */
    public int reqTime(int station1,int station2){
        if(timeExist(station1)&&timeExist(station2)){
            if((1-direction*2)*(station2-station1)>0){
                return getTime(station2,ARRIVE,true)-getTime(station1,DEPART,true);
            }else{
                return getTime(station1,ARRIVE,true)-getTime(station2,DEPART,true);
            }
        }
        return -1;
    }

    /**
     * 列車の通過予想時刻
     * この駅を列車が通過しないと判断したら-1が返る
     */
    public int getPredictionTime(int station) {
        return getPredictionTime(station,DEPART);
    }
    /**
     * 列車の通過予想時刻
     * AD=1の時、着時刻が存在する場合は着時刻っを優先する
     */
    public int getPredictionTime(int station, int AD) {
        if (AD == 1 && timeExist(station,ARRIVE)) {
            return getAriTime(station);
        }
        if (timeExist(station)) {
            return getTime(station,AD,true);
        }
        if (getStopType(station) == STOP_TYPE_NOVIA || getStopType(station) == STOP_TYPE_PASS) {
            //通過時間を予測します
            int afterTime = -1;//後方の時刻あり駅の発車時間
            int beforeTime = -1;//後方の時刻あり駅の発車時間
            int afterMinTime = 0;//後方の時刻あり駅までの最小時間
            int beforeMinTime = 0;//前方の時刻あり駅までの最小時間

            ArrayList<Integer> minstationTime = lineFile.getStationTime();

            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間
            for (int i = station + 1; i < lineFile.getStationNum(); i++) {
                if (getStopType(i) == STOP_TYPE_NOSERVICE || getStopType(i) == STOP_TYPE_NOVIA || getStopType(i - 1) == STOP_TYPE_NOSERVICE || getStopType(i - 1) == STOP_TYPE_NOVIA) {
                    continue;
                }
                afterMinTime = afterMinTime + minstationTime.get(i) - minstationTime.get(i - 1);
                if (timeExist(i)) {
                    if(direction==0){
                        afterTime = getTime(i,ARRIVE,true);
                    }else{
                        afterTime = getTime(i,DEPART,true);
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
                        beforeTime = getTime(i - 1,DEPART,true);
                    }else{
                        beforeTime = getTime(i - 1,ARRIVE,true);
                    }

                    startStation = i - 1;
                    break;
                }
            }
            if (beforeTime < 0) {
                return -1;
            }
            return getDepTime(startStation) + (afterTime - beforeTime) * beforeMinTime / (afterMinTime + beforeMinTime);
        }
        return -1;
    }

    /**
     * 分岐で経由なしを用いる場合はbrunch=true
     * @param index
     * @param brunch
     */
    public void addNewStation(int index,boolean brunch){
        StationTime time=new StationTime(this);
        if(brunch){
            if (index > 0 && index < stationTimes.size()) {
                if (getStopType(index - 1) != STOP_TYPE_NOSERVICE && getStopType(index ) != STOP_TYPE_NOSERVICE) {
                    time.stopType = STOP_TYPE_NOVIA;

                }
            }


        }else {
            if (index > 0 && index < stationTimes.size()) {
                if (getStopType(index - 1) == StationTime.STOP_TYPE_STOP || getStopType(index - 1) == STOP_TYPE_PASS) {
                    if (getStopType(index) == StationTime.STOP_TYPE_STOP || getStopType(index) == STOP_TYPE_PASS) {
                        time.stopType = STOP_TYPE_PASS;
                    }
                }
                if (getStopType(index - 1) == STOP_TYPE_NOVIA) {
                    if (getStopType(index) == StationTime.STOP_TYPE_NOVIA) {
                        time.stopType = STOP_TYPE_NOVIA;
                    }
                }
            }
        }
        stationTimes.add(index,time);
    }

    /**
     * 日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    public boolean checkDoubleDay() {
        int time = getDepTime(getStartStation());
        for (int i = getStartStation(); i < getEndStation() + 1; i++) {
            if (timeExist(i)) {
                if (getTime(i, DEPART, true) - time < -12 * 60 * 60 || getTime(i, DEPART, true) - time > 12 * 60 * 60) {
                    return true;
                }
                time = getDepTime(i);
            }
        }
        return false;
    }
    public void setOuterEndStation(int outer){
        int station=getEndStation();
        if(stationTimes.get(station).afterOperations.size()>0){
            stationTimes.get(station).afterOperations.get(0).operationType=4;
            stationTimes.get(station).afterOperations.get(0).intData1=outer;


        }
    }
    public void setOuterStartStation(int outer){
        int station=getStartStation();
        if(stationTimes.get(station).beforeOperations.size()>0){
            stationTimes.get(station).beforeOperations.get(0).operationType=4;
            stationTimes.get(station).beforeOperations.get(0).intData1=outer;
        }
    }
    public void setOuterStartTime(int time){
        int station=getStartStation();
        if(station<0){
            SDlog.toast("空列車に路線外始発駅を設定する事はできません");
            return;
        }
        if(stationTimes.get(station).beforeOperations.size()>0){
            stationTimes.get(station).beforeOperations.get(0).operationType=4;
            stationTimes.get(station).beforeOperations.get(0).time1=time;
        }
    }
    public void setOuterEndTime(int time){
        int station=getEndStation();
        if(station<0){
            SDlog.toast("空列車に路線外始発駅を設定する事はできません");
            return;
        }
        if(stationTimes.get(station).afterOperations.size()>0){
            stationTimes.get(station).afterOperations.get(0).operationType=4;
            stationTimes.get(station).afterOperations.get(0).time1=time;


        }
    }


}
