package com.kamelong.aodia.oudia;

import android.app.Activity;

import java.util.ArrayList;

/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */


/**
 * @author  KameLong
 * oudiaの路線データを格納するクラス。
 * 一つのoudiaファイルは一つのDiaFileに対応する
 * 内部に複数のダイヤを格納することができるが、駅リスト、種別リストは一つしか持てない
 *
 * v0.9追記
 * oudiaに限らず、他形式のデータに関してもこのクラスで保持する。
 * 将来的には抽象クラス化する必要があるかも。
 * 当面はこのままデータを保持します。
 */

abstract public class DiaFile{
    /**
     * メニューを開いているかどうか
     */
    public boolean menuOpen=true;
    /**
     * アプリ実行アクティビティーの保持。おそらくMainActivityとなるであろう
     */
    protected Activity activity;
    /**
     * 開いたファイルのパス。
     * このパスをもとにファイルの識別等の処理を行う。
     */
    protected String filePath;

    /**
     *路線名。
     */
    protected String lineName="";
    /**
     * ダイヤ名。
     * 一ファイル内に複数のダイヤを格納することができるためArrayListを用いる
     */
    protected ArrayList<String> diaName=new ArrayList<String>();
    /**
     * 駅
     * 駅の数は不定
     */
    protected ArrayList<Station> station=new ArrayList<Station>();
    /**
     * 種別
     * 種別の数は不定
     */
    protected ArrayList<TrainType> trainType=new ArrayList<TrainType>();
    /**
     * Trainは1本の列車に相当する
     * 最初のArrayListはダイヤの数に相当する
     * ArrayListの中に配列があるが、これは上りと下りの２つ(確定)の時刻表があるため、配列を用いている
     * 配列の内部に再びArrayListがあるが、これは各時刻表中の列車の本数分の大きさを持つ
     */
    protected ArrayList<ArrayList<Train>[]> train=new ArrayList<ArrayList<Train>[]>();
    /**
     * コメント。
     * oudiaデータには路線ごとにコメントがついている。
     * ダイヤごとにコメントをつけたい場合はArrayListに拡張しないといけない。
     */
    protected String comment="";
    /**
     * ダイヤグラム起点時刻。
     * 今は3:00に固定されているが、oudiaに設定項目がある以上機能追加を考えたほうがよい。
     */
    protected int diagramStartTime=10800;
    /**
     * 最小所要時間
     */
    protected ArrayList<Integer>stationTime=new ArrayList<Integer>();

    /**
     * このオブジェクトの生成に成功したかチェックする。
     * チェックポイント
     * １、駅数が０ではいけない
     * ２、種別数が０ではいけない
     * ３、ダイヤ数が０ではいけない
     * @return 修正した場合falseを返す、修正が必要ないときtrue
     */
    protected boolean checkDiaFile(){
        boolean result=true;
        if(station.size()==0){
            station.add(new Station());
            result=false;
        }
        if(trainType.size()==0){
            trainType.add(new TrainType());
            result=false;
        }
        if(train.size()==0){
            ArrayList<Train>[] trainArray=new ArrayList[2];
            trainArray[0]=new ArrayList<Train>();
            trainArray[1]=new ArrayList<Train>();
            train.add(trainArray);
            result=false;
        }
        return result;
    }

    /**
     * 路線名を返す。
     * @return menber lineName
     */
    public String getLineName(){
        return lineName;
    }

    /**
     * 駅数を返す。
     * @return size of station(ArrayList)
     */
    public int getStationNum(){
        return station.size();
    }

    /**
     * 主要駅を返す。
     * 現在未使用
     * @return sum of main station
     */
    public int getMainStationNum(){
        int result=0;
        for(int i=0;i<station.size();i++){
            if(station.get(i).getBigStation()){
                result++;
            }
        }
        return result;
    }

    /**
     * 駅名を返す。
     * stationNumが範囲外の場合は空文字列を返す
     * getStation(stationNum).getName();とほぼ同じ機能
     * @param stationNum index of station, stationNum>=0
     * @return station name
     */
    public String getStationName(int stationNum){
        try{
            return station.get(stationNum).getName();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
    /**
     * この路線の全ての駅のリストを返す
     */
    public String[] getStationNameList(){
        String[] result=new String[station.size()];
        for(int i=0;i<station.size();i++){
            result[i]=getStationName(i);
        }
        return result;
    }


    /**
     * 駅を返す。
     * stationNumが範囲外の場合は空の駅を返す
     * @param stationNum index of station, 0<=stationNum<size of station
     * @return Station
     */
    public Station getStation(int stationNum){
        try{
            return station.get(stationNum);
        }catch(Exception e){
            e.printStackTrace();
            return new Station();
        }
    }

    /**
     * 種別を返す。
     * numが範囲外の場合は空の種別を返す
     * @param num index of traintype, 0<=num<size of trainType
     * @return TrainType
     */
    public TrainType getTrainType(int num){
        try{
            return trainType.get(num);
        }
        catch(Exception e){
            e.printStackTrace();
            return new TrainType();
        }
    }

    /**
     * 列車数を返す。
     * 何らかのエラーが発生した場合は迷わず0を返す
     * @param diaNum index of dia
     * @param direct down=0,up=1
     * @return number of selected timetable's train
     */
    public int getTrainNum(int diaNum,int direct){
        try {
            return train.get(diaNum)[direct].size();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 列車を返す。
     * @param diaNum index of dia
     * @param direct down=0,up=1
     * @param trainNum index of train
     * @return Train
     */
    public Train getTrain(int diaNum,int direct,int trainNum){
        try {
            return train.get(diaNum)[direct].get(trainNum);
        }catch(Exception e){
            e.printStackTrace();
            return new Train(this);
        }
    }

    /**
     * 種別の数を返す。
     * @return size of trainTyle
     */
    public int getTypeNum(){
        return trainType.size();
    }

    /**
     * ダイヤ数を返す。
     * @return size of train
     */
    public int getDiaNum(){
        return train.size();
    }

    /**
     * ダイヤ名を返す
     * @param diaN index of dia
     * @return name of dia
     */
    public String getDiaName(int diaN){
        try {
            return diaName.get(diaN);
        }catch(Exception e){
            e.printStackTrace();
            return "e";
        }
    }
    /**
     * 基準運転時間が定義されている時に最小所要時間を返す
     *
     */
    public int getMinReqiredTime2(int diaNum,int startStation,int endStation) {
        int result=360000;
            for(int train=0;train<this.train.get(diaNum)[0].size();train++){
                int value=this.getTrain(diaNum,0,train).getRequiredTime(startStation,endStation);
                if(value>0&&result>value){
                    result=value;
                }
            }
            for(int train=0;train<this.train.get(diaNum)[1].size();train++){
                int value=this.getTrain(diaNum,1,train).getRequiredTime(startStation,endStation);
                if(value>0&&result>value){
                    result=value;
                }
        }
        if(result==360000){
            result=120;
        }
        return result;

    }
    /**
     *  駅間最小所要時間を返す。
     *  startStatioin endStationの両方に時刻が存在する列車のうち、
     *  所要時間（着時刻-発時刻)の最も短いものを秒単位で返す。
     *  ただし、駅間所要時間が60秒より短いときは60秒を返す。
     *
     *  startStation endStationは便宜上区別しているが、順不同である。
     * @param startStation
     * @param endStation
     * @return time(second)
     */
    public int getMinReqiredTime(int startStation,int endStation){
        int result=360000;
        for(int i=0;i<getDiaNum();i++){
            if(getDiaName(i).equals("基準運転時分")){
                return getMinReqiredTime2(i,startStation,endStation);
            }
        }
        for(int i=0;i<this.train.size();i++){

            for(int train=0;train<this.train.get(i)[0].size();train++){
                int value=this.getTrain(i,0,train).getRequiredTime(startStation,endStation);
                    if (value > 0 && result > value) {
                        result = value;
                    }
            }
            for(int train=0;train<this.train.get(i)[1].size();train++){
                int value=this.getTrain(i,1,train).getRequiredTime(startStation,endStation);
                    if (value > 0 && result > value) {
                        result = value;
                    }
            }
        }
        if(result==360000){
            result=120;
        }
        if(result<60){
            result=60;
        }

        return result;
    }

    /**
     * コメントの文字列を返す。
     * @return コメント
     */
    public String getComment(){
        return comment;
    }

    /**
     * ダイヤグラム基準時間を返す。
     * @return
     */
    public int getDiagramStartTime(){
        return diagramStartTime;
    }

    /**
     * ファイルパスを返す。
     * ロードしたファイルのパスで保存データを整理するため。
     * @return
     */
    public String getFilePath(){
        return filePath;
    }

    /**
     * 最小所要時間を計算する。
     * この関数は処理の完了までにかなりの時間がかかると予想されます。
     * 別スレッドでの実行を推奨します
     */
    protected void calcMinReqiredTime(){
        stationTime.add(0);
        for(int i=0;i<getStationNum()-1;i++){
            stationTime.add(stationTime.get(stationTime.size()-1)+getMinReqiredTime(i,i+1));
        }
    }

    /**
     * 最小所要時間のリストを返します。
     * 最小所要時間は別スレッドで計算されている場合がありますので、
     * 計算が終了するまで、スレッドを待機させます。
     * @return
     */
    public ArrayList<Integer> getStationTime(){
        while(stationTime.size()<getStationNum()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationTime;
    }
    /**
     * 始発駅からの累計最小所要時間を返します。
     * 最小所要時間は別スレッドで計算されている場合がありますので、
     * 計算が終了するまで、スレッドを待機させます。
     * @return
     */
    public int getStationTime(int station){
        if(station<0||station>=getStationNum()){
            return 0;
        }
        while(stationTime.size()<getStationNum()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return stationTime.get(station);
    }

    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param diaNum 並び替え対象ダイヤ
     * @param direct 並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    public void sortTrain(int diaNum,int direct,int stationNumber){
        /*並び替えるときの列車indexを格納するリスト
         *
         */
        Train[] trains=train.get(diaNum)[direct].toArray(new Train[0]);

        //ソートする前の順番を格納したクラス
        ArrayList<Integer> sortBefore=new ArrayList<Integer>();
        //ソートした後の順番を格納したクラス
        ArrayList<Integer> sortAfter=new ArrayList<Integer>();

        for(int i=0;i<train.get(diaNum)[direct].size();i++){
            sortBefore.add(i);
        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trains[sortBefore.get(i)].getPredictionTime(stationNumber)>0&&!trains[sortBefore.get(i)].checkDoubleDay()) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trains[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j=0;
                for(j=sortAfter.size();j>0;j--) {
                    if(trains[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }
        //この時点で基準駅に予測時間を設定できるものはソートされている
        if(direct==0) {
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より後方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(this.station.get(station-1).border()){
                    searchStation:
                    for(int i=station;i>0;i--){
                        //境界線がある駅の次の駅が分岐駅である可能性を探る
                        if(getStationName(station).equals(getStationName(i-1))){
                            addTrainInSort1(sortBefore,sortAfter,trains,new int[]{i-1,station});
                            for(int j=i;j<station;j++){
                                addTrainInSort2(sortBefore,sortAfter,trains,new int[]{j});
                            }
                            station=i;
                            continue baseStation;
                        }
                    }
                    for(int i=station;i<getStationNum();i++){
                        //境界線がある駅が分岐駅である可能性を探る
                        if(getStationName(station-1).equals(getStationName(i))){
                            addTrainInSort1(sortBefore,sortAfter,trains,new int[]{station-1,i});
                            for(int j=i;j<station;j++){
                                addTrainInSort1(sortBefore,sortAfter,trains,new int[]{j});
                            }
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort1(sortBefore,sortAfter,trains,new int[]{station-1});
            }
//            基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station < getStationNum(); station++) {
                if(this.station.get(station-1).border()){
                    for(int i=station;i>0;i--){
                        if(getStationName(station).equals(getStationName(i-1))){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{station,i-1});
                            continue baseStation;
                        }
                    }
                }
                if(this.station.get(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStationName(station).equals(getStationName(i))){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{i,station});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trains,new int[]{station});

            }
        }else{
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より前方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(this.station.get(station-1).border()){
                    for(int i=station;i>0;i--){
                        if(getStationName(station).equals(getStationName(i-1))){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{i-1,station});
                            continue baseStation;
                        }
                    }
                }
                if(this.station.get(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStationName(station).equals(getStationName(i))){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{station,i});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trains,new int[]{station});
            }


            //基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station < getStationNum(); station++) {
                if(this.station.get(station-1).border()) {
                    for (int i = station; i > 0; i--) {
                        if (getStationName(station).equals(getStationName(i - 1))) {
                            addTrainInSort1(sortBefore, sortAfter, trains, new int[]{station, i - 1});
                            continue baseStation;
                        }
                    }
                }
                if(this.station.get(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStationName(station).equals(getStationName(i))){
                            addTrainInSort1(sortBefore,sortAfter,trains,new int[]{i,station});
                            continue baseStation;
                        }
                    }

                }
                addTrainInSort1(sortBefore,sortAfter,trains,new int[]{station});
            }

        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trains[sortBefore.get(i)].getPredictionTime(stationNumber)>0) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trains[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j=0;
                for(j=sortAfter.size();j>0;j--) {
                    if(trains[sortAfter.get(j-1)].getPredictionTime(stationNumber)>0&&trains[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }

        for(int i=0;i<sortBefore.size();i++) {
            sortAfter.add(sortBefore.get(i));
        }
        ArrayList<Train> trainAfter=new ArrayList<Train>();
        for(int i=0;i<sortAfter.size();i++){
            trainAfter.add(trains[sortAfter.get(i)]);
        }
        train.get(diaNum)[direct]=trainAfter;
    }

    private void addTrainInSort1(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int station[]){
        for (int i = sortBefore.size(); i >0; i--) {
            int baseTime = trains[sortBefore.get(i-1)].getArriveTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i-1)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = 0; j < sortAfter.size(); j++) {

                int sortTime;
                if(station.length==2) {
                    sortTime = Math.max(trains[sortAfter.get(j)].getPredictionTime(station[0]), trains[sortAfter.get(j)].getPredictionTime(station[1]));
                }else{
                    sortTime =trains[sortAfter.get(j)].getPredictionTime(station[0]);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime >= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i - 1));
                sortBefore.remove(i-1);
            }
        }
    }
    private void addTrainInSort2(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int station[]){
        for (int i = 0; i < sortBefore.size(); i++) {
            int baseTime = trains[sortBefore.get(i)].getDepartureTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i)].checkDoubleDay()) {
                continue;
            }
            int j = 0;
            boolean frag = false;

            for (j = sortAfter.size(); j > 0; j--) {
                int sortTime;
                if(station.length==2){
                    if(trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE)>0&&trains[sortAfter.get(j - 1)].getPredictionTime(station[1],Train.ARRIVE)>0) {
                        sortTime = Math.min(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE));
                    }else{
                        sortTime = Math.max(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], Train.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], Train.ARRIVE));

                    }
                }else{
                    sortTime = trains[sortAfter.get(j - 1)].getPredictionTime(station[0],Train.ARRIVE);
                }
                if (sortTime < 0) {
                    continue;
                }
                frag = true;
                if (sortTime <= baseTime) {
                    break;
                }
            }
            if (frag) {
                sortAfter.add(j, sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }

        }

    }

}
