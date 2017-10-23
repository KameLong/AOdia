package com.kamelong.aodia.diadata;

import android.content.Context;

import com.kamelong.JPTIOuDia.JPTI.JPTI;
import com.kamelong.JPTIOuDia.JPTI.Route;
import com.kamelong.JPTIOuDia.JPTI.RouteStation;
import com.kamelong.JPTIOuDia.JPTI.Service;
import com.kamelong.JPTIOuDia.JPTI.Station;
import com.kamelong.JPTIOuDia.JPTI.TrainType;
import com.kamelong.JPTIOuDia.JPTI.Trip;
import com.kamelong.JPTIOuDia.OuDia.OuDiaFile;
import com.kamelong.JPTIOuDia.OuDia.OuDiaStation;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrain;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrainType;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.SdLog;
import com.kamelong.tool.Font;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AOdiaで使用するDiaFile
 * OuDiaの処理をベースにしている。
 */

public class AOdiaDiaFile extends OuDiaFile{
    AOdiaActivity activity;//このアプリのアクティビティー
    /**
     * メニューを開いているかどうか
     */
    public boolean menuOpen=true;
    /**
     * 最小所要時間
     */
    protected ArrayList<Integer>stationTime=new ArrayList<Integer>();

    /**
     * 運用のリストダイヤの数だけ、リストを用意する
     */
    public ArrayList<ArrayList<Operation>>operationList=new ArrayList<>();


    /**
     * 推奨コンストラクタ。
     * コンストラクタでは読み込みファイルが与えられるので、そのファイルを読み込む。
     * ダイヤを読み込んだ後に最小所要時間をこのスレッドで作成する
     * @param context MainActivityになると思われる
     * @param file 開きたいファイル @code null then サンプルファイルを開く
     */
    public AOdiaDiaFile(Context context, final File file){
        super(file);
        //運用の準備をする
        for(int i=0;i<getDiaNum();i++){
            operationList.add(new ArrayList<Operation>());
        }
        activity=(AOdiaActivity)context;
        filePath=file.getPath();
        calcMinReqiredTime();
    }
    public AOdiaDiaFile(Context context){
        this(context,new File(context.getExternalFilesDir(null).getPath()+"/sample.oud"));

    }
    public AOdiaDiaFile(JPTI jpti){
        super();
        Service service=jpti.getService(0);
        Map<Integer,AOdiaTrain> trainMap=new HashMap<>();
        //fileType
        fileType="OuDia.1.02";
        lineName=service.getName();
        //まず駅一覧、種別を作る
        Station station=null;
        for(int i=0;i<service.getRouteNum();i++){
            for(int j=0;j<service.getRoute(i,0).getStationNum();j++){
                RouteStation routeStation=service.getRoute(i,0).getRouteStation(j,service.getRouteDirect(service.getRoute(i,0)));

/*                if(station==routeStation.getStation()){
                    this.getStation(this.station.size()-1).setTimeShow(OuDiaStation.SHOW_HATUTYAKU);

                }else{
                    if(j==0&&i!=0){
                        this.getStation(this.station.size()-1).setBorder(true);
                    }
                    this.station.add(new OuDiaStation(routeStation));
                }
                */
                station=routeStation.getStation();
                this.station.add(newOuDiaStation(routeStation));
            }
            for(int j=0;j<service.getRoute(i,0).getTrainTypeNum();j++){
                boolean existTrainType=false;
                for(int k=0;k<trainType.size();k++){
                    if(getTrainType(k).compare(service.getRoute(i,0).getTrainType(j))){
                        existTrainType=true;
                        break;
                    }
                }
                if(!existTrainType) {
                    trainType.add(newOuDiaTrainType(service.getRoute(i, 0).getTrainType(j)));
                }

            }
        }

        for(int diaNum=0;diaNum<jpti.getCalenderNum();diaNum++) {
            ArrayList<? extends com.kamelong.OuDia.OuDiaTrain>[] diaTrain = new ArrayList[2];
            for (int direct = 0; direct < 2; direct++) {
                ArrayList<Integer> useBlockID = new ArrayList<>();
                ArrayList<OuDiaTrain> trains = new ArrayList<>();
                for (int i = 0; i < service.getRouteNum(); i++) {
                    Route route = service.getRoute(i, 0);
                    for (int j = 0; j < route.getTripNum(); j++) {
                        Trip trip = route.getTrip(j);
                        if (trip.getCalender().index() == diaNum &&trip.getDirect()==(direct+service.getRouteDirect(service.getRoute(i, direct)))%2&& !useBlockID.contains(trip.getBlockID())) {
                            ArrayList<Trip> trips = new ArrayList<>();
                            for (int k = 0; k < service.getRouteNum(); k++) {
                                trips.add(service.getRoute(k, 0).getTripByBlockID(trip.getBlockID(),(direct+service.getRouteDirect(service.getRoute(k, direct)))%2));
                            }
                            AOdiaTrain newTrain=newTrain(service,trips);
                            trainMap.put(trips.get(0).getBlockID(),newTrain);
                            newTrain.setType(trip.getType());
                            newTrain.setName(trip.getName());
                            newTrain.setNumber(trip.getNumber());

                            trains.add(newTrain);
                            useBlockID.add(trip.getBlockID());
                        }

                    }
                }
                diaTrain[direct]=trains;
            }
            train.add(diaTrain);
            diaName.add(jpti.getCalendar(diaNum).getName());
        }
        //フォントを作る
        for(int i=0;i<6;i++){
            if(service.getTimeTableFontNum()>i) {
                jikokuhyouFont.add(service.getTimeTableFont(i));
            }else{
                jikokuhyouFont.add(Font.OUDIA_DEFAULT);
            }
        }

        if(service.getTimeTableVFont()!=null){
            jikokuVFont =service.getTimeTableVFont();
        }
        if(service.getDiaStationFont()!=null){
            diaEkimeiFont =service.getDiaStationFont();
        }
        if(service.getDiaTimeFont()!=null){
            diaJikokuFont =service.getDiaTimeFont();
        }
        if(service.getDiaTrainFont()!=null){
            diaRessyaFont =service.getDiaTrainFont();
        }
        if(service.getCommentFont()!=null){
            commentFont =service.getCommentFont();
        }
        if(service.getDiaTextColor()!=null){
            diaMojiColor=service.getDiaTextColor();
        }
        if(service.getDiaBackColor()!=null){
            diaHaikeiColor=service.getDiaBackColor();
        }
        if(service.getDiaTrainColor()!=null){
            diaResyaColor=service.getDiaTrainColor();
        }
        if(service.getDiaAxisColor()!=null){
            diaJikuColor=service.getDiaAxisColor();
        }
        for(int i=0;i<getDiaNum();i++){
            operationList.add(new ArrayList<Operation>());
        }

        for(int i=0;i<jpti.operationList.size();i++){
            Operation ope=new Operation((com.kamelong.JPTIOuDia.JPTI.Operation) jpti.operationList.get(i),trainMap);
            int diaNum=jpti.operationList.get(i).getCalenderID();
            if(diaNum>=0){
                operationList.get(diaNum).add(ope);

            }
        }
        calcMinReqiredTime();


    }
    /**
     * OuDiaTrainを生成する
     * これをオーバーライドすることで任意のOuDiaTrainを継承したTrainクラスで生成できる
     */
    protected AOdiaTrain newTrain(){
        return new AOdiaTrain(this);
    }
    protected AOdiaTrain newTrain(Service service,ArrayList<Trip> trips){
        return new AOdiaTrain(this,service,trips);
    }
    /**
     * OuDiaTrainTypeを生成する
     * これをオーバーライドすることで任意のOuDiaTrainTypeを継承したTrainクラスで生成できる
     */
    protected AOdiaTrainType newTrainType(){
        return new AOdiaTrainType();
    }
    /**
     * OuDiaStationを生成する
     * これをオーバーライドすることで任意のOuDiaStationを継承したStationクラスで生成できる
     */
    protected AOdiaStation newStation(){
        return new AOdiaStation();
    }
    /**
     * OuDiaStationを生成する
     * これをオーバーライドすることで任意のOuDiaStationを継承したStationクラスで生成できる
     */


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
            station.add(new AOdiaStation());
            result=false;
        }
        if(trainType.size()==0){
            trainType.add(new AOdiaTrainType());
            result=false;
        }
        if(train.size()==0){
            ArrayList<AOdiaTrain>[] trainArray=new ArrayList[2];
            trainArray[0]=new ArrayList<AOdiaTrain>();
            trainArray[1]=new ArrayList<AOdiaTrain>();
            train.add(trainArray);
            result=false;
        }
        return result;
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
    public AOdiaTrain getTrain(int dia,int direction,int index){
        try {
            return (AOdiaTrain) train.get(dia)[direction].get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new AOdiaTrain(this);
        }

    }
    /**
     * ダイヤグラム基準時間を返す。
     * @return
     */
    public int getDiagramStartTime(){
        return diagramStartTime;
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
    public AOdiaTrainType getTrainType(int index){
        try{
            return (AOdiaTrainType)trainType.get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new AOdiaTrainType();
        }
    }
    public AOdiaStation getStation(int index){
        try{
            return (AOdiaStation) station.get(index);
        }catch (Exception e){
            e.printStackTrace();
            return new AOdiaStation();
        }
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
        AOdiaTrain[] trains=train.get(diaNum)[direct].toArray(new AOdiaTrain[0]);

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
                if(getStation(station-1).border()){
                    searchStation:
                    for(int i=station;i>0;i--){
                        //境界線がある駅の次の駅が分岐駅である可能性を探る
                        if(getStation(station).getName().equals(getStation(i-1).getName())){
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
                        if(getStation(station-1).getName().equals(getStation(i).getName())){
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
                if(getStation(station-1).border()){
                    for(int i=station;i>0;i--){
                        if(getStation(station).getName().equals(getStation(i-1).getName())){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{station,i-1});
                            continue baseStation;
                        }
                    }
                }
                if(getStation(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStation(station).getName().equals(getStation(i).getName())){
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
                if(getStation(station-1).border()){
                    for(int i=station;i>0;i--){
                        if(getStation(station).getName().equals(getStation(i-1).getName())){
                            addTrainInSort2(sortBefore,sortAfter,trains,new int[]{i-1,station});
                            continue baseStation;
                        }
                    }
                }
                if(getStation(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStation(station).getName().equals(getStation(i).getName())){
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
                if(getStation(station-1).border()) {
                    for (int i = station; i > 0; i--) {
                        if (getStation(station).getName().equals(getStation(i - 1).getName())) {
                            addTrainInSort1(sortBefore, sortAfter, trains, new int[]{station, i - 1});
                            continue baseStation;
                        }
                    }
                }
                if(getStation(station).border()){
                    for(int i=station+1;i<getStationNum();i++){
                        if(getStation(station).getName().equals(getStation(i).getName())){
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
        ArrayList<AOdiaTrain> trainAfter=new ArrayList<>();
        for(int i=0;i<sortAfter.size();i++){
            trainAfter.add(trains[sortAfter.get(i)]);
        }
        train.get(diaNum)[direct]=trainAfter;
    }

    private void addTrainInSort1(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, AOdiaTrain[] trains, int station[]){
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
    private void addTrainInSort2(ArrayList<Integer> sortBefore, ArrayList<Integer> sortAfter, AOdiaTrain[] trains, int station[]){
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
                    if(trains[sortAfter.get(j - 1)].getPredictionTime(station[0],AOdiaTrain.ARRIVE)>0&&trains[sortAfter.get(j - 1)].getPredictionTime(station[1],AOdiaTrain.ARRIVE)>0) {
                        sortTime = Math.min(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], AOdiaTrain.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], AOdiaTrain.ARRIVE));
                    }else{
                        sortTime = Math.max(
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[0], AOdiaTrain.ARRIVE),
                                trains[sortAfter.get(j - 1)].getPredictionTime(station[1], AOdiaTrain.ARRIVE));

                    }
                }else{
                    sortTime = trains[sortAfter.get(j - 1)].getPredictionTime(station[0],AOdiaTrain.ARRIVE);
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
    public int getOperationNum(int diaNum){
        return operationList.get(diaNum).size();
    }
    public Operation getOperation(int diaNum,int index){
        return operationList.get(diaNum).get(index);
    }
    protected OuDiaTrain newOuDiaTrain(Service service, ArrayList<Trip> trips){
        return new AOdiaTrain(this,service, trips);
    }
    protected OuDiaStation newOuDiaStation(RouteStation station){
        return new AOdiaStation(station);
    }
    protected OuDiaTrainType newOuDiaTrainType(TrainType type){
        return new AOdiaTrainType(type);
    }

    public void setFilePath(String path){
        filePath=path;
    }



}
