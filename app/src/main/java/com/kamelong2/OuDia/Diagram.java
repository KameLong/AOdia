package com.kamelong2.OuDia;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Diagram {
    public String name="";
    public ArrayList<Train>[] trains=new ArrayList[2];
    public DiaFile diaFile;

    public Diagram(DiaFile diaFile){
        this.diaFile=diaFile;
        name="新しいダイヤ";
        trains[0]=new ArrayList<>();
        trains[0].add(new Train(diaFile,0));
        trains[1]=new ArrayList<>();
        trains[1].add(new Train(diaFile,1));
    }
    public Diagram(DiaFile diaFile,BufferedReader br)throws Exception{
        this.diaFile=diaFile;
        trains[0]=new ArrayList<>();
        trains[1]=new ArrayList<>();
            String line=br.readLine();
            while(!line.equals(".")){
                if(line.startsWith("DiaName")){
                    name=line.split("=",-1)[1];
                }
                if(line.equals("Kudari.")){
                    while(!line.equals(".")){
                        if(line.equals("Ressya.")){
                            trains[0].add(new Train(diaFile,0,br));
                        }
                        line=br.readLine();
                    }
                }
                if(line.equals("Nobori.")){
                    while(!line.equals(".")){
                        if(line.equals("Ressya.")){
                            trains[1].add(new Train(diaFile,1,br));
                        }
                        line=br.readLine();
                    }
                }
                line=br.readLine();
            }

    }
    public Diagram(Diagram old){
        diaFile=old.diaFile;
        name=old.name;
        for(int direction=0;direction<2;direction++){
            trains[direction]=new ArrayList<>();
            for(Train t:old.trains[direction]){
                trains[direction].add(new Train(t));
            }
        }
    }
    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param direction 並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    public void sortTrain(int direction,int stationNumber){
        long startTime=System.currentTimeMillis();
        long startCount=Train.count2;
        Train[] trainList=trains[direction].toArray(new Train[0]);

        //ソートする前の順番を格納したクラス
        ArrayList<Integer> sortBefore=new ArrayList<>();
        //ソートした後の順番を格納したクラス
        ArrayList<Integer> sortAfter=new ArrayList<>();

        for(int i=0;i<trainList.length;i++){
            sortBefore.add(i);
        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber)>0&&!trainList[sortBefore.get(i)].checkDoubleDay()) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }
        //この時点で基準駅に予測時間を設定できるものはソートされている
        if(direction==0) {
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より後方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    searchStation:
                    for(int i=station;i>0;i--){
                        //境界線がある駅の次の駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i-1,station});
                            for(int j=i;j<station;j++){
                                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{j});
                            }
                            station=i;
                            continue baseStation;
                        }
                    }
                    for(int i=station;i<diaFile.getStationNum();i++){
                        //境界線がある駅が分岐駅である可能性を探る
                        if(diaFile.station.get(station-1).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1,i});
                            for(int j=i;j<station;j++){
                                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{j});
                            }
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station-1});
            }
//            基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station < diaFile.getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i-1});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i,station});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station});

            }
        }else{
            //ここからは基準駅を通らない列車についてソートを行う
            //基準駅より前方で運行する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber; station > 0; station--) {
                if(diaFile.station.get(station-1).getBorder()){
                    for(int i=station;i>0;i--){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{i-1,station});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station,i});
                            continue baseStation;
                        }
                    }
                }
                addTrainInSort2(sortBefore,sortAfter,trainList,new int[]{station});
            }


            //基準駅より後方から出発する列車に着いてソートを行う
            baseStation:
            for (int station = stationNumber+1; station <diaFile. getStationNum(); station++) {
                if(diaFile.station.get(station-1).getBorder()) {
                    for (int i = station; i > 0; i--) {
                        if (diaFile.station.get(station).name.equals(diaFile.station.get(i-1).name)) {
                            addTrainInSort1(sortBefore, sortAfter, trainList, new int[]{station, i - 1});
                            continue baseStation;
                        }
                    }
                }
                if(diaFile.station.get(station).getBorder()){
                    for(int i=station+1;i<diaFile.getStationNum();i++){
                        if(diaFile.station.get(station).name.equals(diaFile.station.get(i).name)){
                            addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{i,station});
                            continue baseStation;
                        }
                    }

                }
                addTrainInSort1(sortBefore,sortAfter,trainList,new int[]{station});
            }

        }

        for(int i=0;i<sortBefore.size();i++) {
            if (trainList[sortBefore.get(i)].getPredictionTime(stationNumber)>0) {
                //今からsortAfterに追加する列車の基準駅の時間
                int baseTime=trainList[sortBefore.get(i)].getPredictionTime(stationNumber);
                int j;
                for(j=sortAfter.size();j>0;j--) {
                    if(trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)>0&&trainList[sortAfter.get(j-1)].getPredictionTime(stationNumber)<baseTime){
                        break;
                    }
                }
                sortAfter.add(j,sortBefore.get(i));
                sortBefore.remove(i);
                i--;
            }
        }

            sortAfter.addAll(sortBefore);
        ArrayList<Train> trainAfter=new ArrayList<>();
        for(int i=0;i<sortAfter.size();i++){
            trainAfter.add(trainList[sortAfter.get(i)]);
        }
        trains[direction]=trainAfter;
        long endTime=System.currentTimeMillis();
        long endCount=Train.count2;

        System.out.println("sortTime:"+(endTime-startTime));
        System.out.println("count:"+(endCount-startCount));

    }

    private void addTrainInSort1(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int station[]){
        for (int i = sortBefore.size(); i >0; i--) {
            int baseTime = trains[sortBefore.get(i-1)].getArrivalTime(station[0]);
            if (baseTime < 0||trains[sortBefore.get(i-1)].checkDoubleDay()) {
                continue;
            }
            int j =0;
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
    private void addTrainInSort2(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, Train[] trains, int[] station){
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

    public void splitTrain(Train train,int station){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                Train newTrain=new Train(train);
                train.endTrain(station);
                newTrain.startTrain(station);
                trains[direction].add(i+1,newTrain);
                return;
            }
        }
    }
    public void combineTrain(Train train,int station){
        int direction=train.direction;
        int i=0;
        for(i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                break;
            }
        }
        for(;i<trains[direction].size();i++){
            if(trains[direction].get(i).type==train.type){
                if(trains[direction].get(i).startStation()==station){
                    train.combine(trains[direction].get(i),station);
                    trains[direction].remove(i);
                    return;
                }
            }
        }
    }
    public void copyTrain(Train train){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                trains[direction].add(i+1,new Train(train));
                return;
            }
        }
    }
    public void insertTrain(Train train){
        int direction=train.direction;
        for(int i=0;i<trains[direction].size();i++){
            if(trains[direction].get(i)==train){
                trains[direction].add(i,new Train(diaFile,direction));
                return;
            }
        }
    }
    public void deleteTrain(Train train){
        int direction=train.direction;
        trains[direction].remove(train);
        if(trains[direction].size()==0){
            trains[direction].add(new Train(diaFile,direction));
        }
    }

    public Train nextOperation(Train train){
        int endStation=train.endStation();
        if(endStation<0)return null;
        int endTime=train.getADTime(endStation);
        if(endTime<0)return null;
        Train bestTrain=null;
        int bestTime=10000000;
        int stop=train.getStop(endStation);
        if(stop==0){
            stop=diaFile.station.get(endStation).stopMain[train.direction];
        }
        for(Train t:trains[0]){
            int stop2=t.getStop(endStation);
            if(stop2==0){
                stop2=diaFile.station.get(endStation).stopMain[0];
            }
            if(stop==stop2){
                if(t.getDATime(endStation)>endTime&&t.getDATime(endStation)<bestTime){
                    bestTrain=t;
                    bestTime=t.getDATime(endStation);
                }
            }
        }
        for(Train t:trains[1]){
            int stop2=t.getStop(endStation);
            if(stop2==0){
                stop2=diaFile.station.get(endStation).stopMain[1];
            }
            if(stop==stop2){
                if(t.getDATime(endStation)>endTime&&t.getDATime(endStation)<bestTime){
                    bestTrain=t;
                    bestTime=t.getDATime(endStation);
                }
            }
        }
        if(bestTrain==null){
            return null;
        }
        if(bestTrain.startStation()==endStation&&!bestTrain.leaveYard){
            return bestTrain;
        }
        return null;

    }
    public Train beforeOperation(Train train){
        int startStation=train.startStation();
        if(startStation<0)return null;
        int startTime=train.getDATime(startStation);
        if(startTime<0)return null;
        Train bestTrain=null;
        int bestTime=0;
        int stop=train.getStop(startStation);
        if(stop==0){
            stop=diaFile.station.get(startStation).stopMain[train.direction];
        }
        for(Train t:trains[0]){
            int stop2=t.getStop(startStation);
            if(stop2==0){
                stop2=diaFile.station.get(startStation).stopMain[0];
            }

            if(stop==stop2){
                if(t.getADTime(startStation)<startTime&&t.getADTime(startStation)>bestTime){
                    bestTrain=t;
                    bestTime=t.getADTime(startStation);
                }
            }
        }
        for(Train t:trains[1]){
            int stop2=t.getStop(startStation);
            if(stop2==0){
                stop2=diaFile.station.get(startStation).stopMain[1];
            }

            if(stop==stop2){
                if(t.getADTime(startStation)<startTime&&t.getADTime(startStation)>bestTime){
                    bestTrain=t;
                    bestTime=t.getADTime(startStation);

                }
            }
        }
        if(bestTrain==null){
            return null;
        }
        if(bestTrain.endStation()==startStation&&!bestTrain.goYard){
            return bestTrain;
        }
        return null;

    }

    public void reNewOperation(){
        for(Train t:trains[0]) {
            if(!t.leaveYard){
                t.operationName="";
            }
        }
        for(Train t:trains[1]) {
            if(!t.leaveYard){
                t.operationName="";
            }
        }
        for(Train t:trains[0]) {
            if(t.leaveYard){
                Train train=t;
                String operationName=train.operationName;
                while(train!=null){
                    train.operationName=operationName;
                    train=nextOperation(train);
                }
            }
        }
        for(Train t:trains[1]) {
            if(t.leaveYard){
                Train train=t;
                String operationName=train.operationName;
                while(train!=null){
                    train.operationName=operationName;
                    train=nextOperation(train);
                }
            }
        }

    }
    public void saveToFile(FileWriter out) throws Exception {
            out.write("Dia.\r\n");
            out.write("DiaName="+name+"\r\n");
            out.write("Kudari.\r\n");
            for(Train t:trains[0]){
                t.saveToFile(out);
            }
            out.write(".\r\n");
            out.write("Nobori.\r\n");
            for(Train t:trains[1]){
                t.saveToFile(out);
            }
            out.write(".\r\n");
            out.write(".\r\n");
    }

}
