package com.kamelong.OuDia;

import com.kamelong.tool.SDlog;

import java.io.PrintWriter;
import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 時刻表中の１つの駅を表します。
 * 実路線構造と関係なく、時刻表上で異なる位置にある駅は別の駅となります。
 * 時刻表中に同一駅が複数現れた場合も、Stationオブジェクトを共通化せず、別々のオブジェクトとしてください。
 */
public class Station implements Cloneable{
    public LineFile lineFile;
    public String stationID="";
    /**
     * 駅名
     */
    public String name="";
    public String shortName="";
    /**
     * 着時刻を表示するか
     */
    public boolean[] showArrival={false,false};
    /**
     * 発時刻を表示するか
     */
    public boolean[] showDeparture={true,true};
    /**
     * 発着番線を表示するか
     */
    public boolean[] showtrack={false,false};

    /**
     * ダイヤグラム列車情報表示
     * 0:始発なら表示
     * 1:常に表示
     * 2:表示しない
     */
    public int[] showDiagramInfo={0,0};
    public boolean showDiagramTrack=false;

    /**
     * 駅規模
     */
    public boolean bigStation =false;
    //ダイヤグラム列車情報表示は未対応

    public ArrayList<StationTrack>tracks=new ArrayList<>();
    /**
     境界線あり。

     この属性が true の駅では、時刻表画面の
     下り方向の下に、太い境界線を描画します。
     この属性は、駅ビューと時刻表ビューに適用されます。
     なおSecond1.02(FileType1.01)からは、分岐駅設定から
     境界線位置を特定するため、このパラメータは大きな意味を持ちません。
     ただし、旧ファイル形式を読み込むとき、一度この値を設定してから、
     分岐駅設定の解読を行うため、残しておきます
     */
    public boolean border=false;

    /**
     主本線
     0以上、番線数未満を指定。
     列車に番線を設定する場合の、初期値になります。
     Ver2.0 列車側の主本線指定撤廃により、-1が不要になります
     */

    public int[] stopMain=new int[]{0,1};

    /**
     分岐駅設定の基幹駅駅Index
     -1:分岐駅設定は無効
     0以上:基幹駅のStationIndex
     */
    public int brunchCoreStationIndex=-1;
    /**
     分岐駅設定が有効な時、通常とは反対向きに合流しているとみなします。
     具体的には、下り着時刻駅から下方の駅に対し、分岐駅指定している場合、
     通常は[Y]型の路線を意味しますが、この値がtrueの場合、
     [u]の右が下に伸びた感じの路線形状イメージになります。
     */
    public boolean brunchOpposite=false;
    /**
     環状線設定の起点駅駅Index
     -1:環状線設定は無効
     0以上:起点駅のStationIndex
     */
    public int loopOriginStationIndex=-1;

    /**
     環状駅設定が有効な時、通常とは反対向きに合流しているとみなします。
     この値が有効の時は環状線が○ではなく雫型の路線イメージになります。
     */
    public boolean  loopOpposite;

    /**
     この駅から繋がる路線外始発終着駅名
     列車の始発終着駅設定で、「路線外発着」を選択した時に、
     この中から駅名を選択します。

     */
    public ArrayList<OuterTerminal>outerTerminals=new ArrayList<>();
    /**
     この駅から次の駅までの距離(秒)
     デフォルト値は0で、0の場合は、「ダイヤグラムの既定の駅間幅」
     を用います。
     値の範囲は0以上です。
     */
    public int nextStationDistance;


    /**
     作業表示欄設定
     この変数で指定された数だけ、時刻表のこの駅の欄に、
     下り前作業・上り後作業表示欄が設けられます。
     0-3の間で設定されます。
     index=0 終点側
     index=1 起点側
     */
    public int[] stationOperationNum=new int[]{0,0};
    /**
     カスタマイズ時刻表ビューでの着時刻表示設定

     true:この駅で着時刻を表示します
     false:この駅では着時刻を表示しません。
     */
    public boolean[] showArrivalCustom={false,false};


    /**
     時刻表ビューでの発時刻表示設定

     true:この駅で発時刻を表示します
     false:この駅では発時刻を表示しません。
     */
    public boolean[] showDepartureCustom ={true,true};


    /**
     カスタマイズ時刻表ビューでの列車番号表示設定

     2:この駅で全列車の列車番号を表示します
     1:この駅で種別変更を行った列車のみ列車番号を表示します
     0:この駅では列車番号を表示しません。
     */
    public int[] showTrainNumberCustom={0,0};


    /**
     カスタマイズ時刻表ビューでの運用番号表示設定

     2:この駅で全列車の運用番号を表示します
     1:この駅で種別変更を行った列車のみ運用番号を表示します
     0:この駅では列車番号を表示しません。
     */
    public int[] showTrainOperationCustom={0,0};


    /**
     カスタマイズ時刻表ビューでの列車種別表示設定

     2:この駅で全列車の列車種別を表示します
     1:この駅で種別変更を行った列車のみ列車種別を表示します
     0:この駅では列車番号を表示しません。
     */
    public int[] showTrainTypeCustom={0,0};


    /**
     カスタマイズ時刻表ビューでの列車名・号数表示設定

     2:この駅で全列車の列車名・号数を表示します
     1:この駅で種別変更を行った列車のみ列車名・号数を表示します
     0:この駅では列車番号を表示しません。
     */
    public int[] showTrainNameCustom={0,0};


    /**
     * 通常時刻表で番線を表示するか
     */
    public boolean omitTrack=false;

    public Station(LineFile lineFile) {
        this.lineFile = lineFile;
        name="新規作成";
    }
    /**
     * OuDiaファイル1行の情報を読み取ります
     */
    void setValue(String title, String value){
        switch (title){
            case "Ekimei":
                name=value;
                break;
            case "stationID":
                stationID=value;
                break;
            case "EkimeiJikokuRyaku":
                shortName=value;
                break;
            case "Ekijikokukeisiki":
                setTimeTableStyle(value);
                break;
            case "Ekikibo":
                bigStation=value.equals("Ekikibo_Syuyou");
                break;
            case "DiagramRessyajouhouHyoujiKudari":
                setShowDiagramInfo(0,value);
                break;
            case "DiagramRessyajouhouHyoujiNobori":
                setShowDiagramInfo(1,value);
                break;
            case  "DownMain":
                stopMain[0]=Integer.parseInt(value);
                if (Double.parseDouble(lineFile.version.substring(lineFile.version.indexOf(".") + 1)) <= 1.06) {
                    stopMain[0]--;
                }
                break;
            case  "UpMain":
                stopMain[1]=Integer.parseInt(value);
                if (Double.parseDouble(lineFile.version.substring(lineFile.version.indexOf(".") + 1)) <= 1.06) {
                    stopMain[1]--;
                }
                break;
            case "BrunchCoreEkiIndex":
                brunchCoreStationIndex=Integer.parseInt(value);
                break;
            case "BrunchOpposite":
                brunchOpposite=value.equals("1");
                break;
            case "LoopOriginEkiIndex":
                loopOriginStationIndex=Integer.parseInt(value);
                break;
            case "LoopOpposite":
                loopOpposite=value.equals("1");
                break;
            case "JikokuhyouTrackDisplayKudari":
                showtrack[0]=value.equals("1");
                break;
            case "JikokuhyouTrackDisplayNobori":
                showtrack[1]=value.equals("1");
                break;
            case "DiagramTrackDisplay":
                showDiagramTrack=value.equals("1");
                break;
            case "NextEkiDistance":
                nextStationDistance=Integer.parseInt(value);
                break;
            case "JikokuhyouTrackOmit":
                omitTrack=value.equals("1");
                break;
            case "JikokuhyouOperationOrigin":
                stationOperationNum[0]=Integer.parseInt(value);
                break;
            case "JikokuhyouOperationTerminal":
                stationOperationNum[1]=Integer.parseInt(value);
                break;
            case "JikokuhyouJikokuDisplayKudari":
                showArrivalCustom[0]=value.split(",")[0].equals("1");
                showDepartureCustom[0]=value.split(",")[1].equals("1");
                break;
            case "JikokuhyouJikokuDisplayNobori":
                showArrivalCustom[1]=value.split(",")[0].equals("1");
                showDepartureCustom[1]=value.split(",")[1].equals("1");
                break;
            case "JikokuhyouSyubetsuChangeDisplayKudari":
                showTrainNumberCustom[0]=Integer.parseInt(value.split(",")[0]);
                showTrainOperationCustom[0]=Integer.parseInt(value.split(",")[1]);
                showTrainTypeCustom[0]=Integer.parseInt(value.split(",")[2]);
                showTrainNameCustom[0]=Integer.parseInt(value.split(",")[3]);
                break;
            case "JikokuhyouSyubetsuChangeDisplayNobori":
                showTrainNumberCustom[1]=Integer.parseInt(value.split(",")[0]);
                showTrainOperationCustom[1]=Integer.parseInt(value.split(",")[1]);
                showTrainTypeCustom[1]=Integer.parseInt(value.split(",")[2]);
                showTrainNameCustom[1]=Integer.parseInt(value.split(",")[3]);
                break;
            case "Kyoukaisen":
                border=value.equals("1");
        }
    }
    /**
     * oudiaファイルの文字列形式からtimetableStyleを読み込みます
     */
    private void setTimeTableStyle(String str){
        switch (str){
            case "Jikokukeisiki_Hatsu":
                showArrival[0]=false;
                showArrival[1]=false;
                showDeparture[0]=true;
                showDeparture[1]=true;
                showArrivalCustom[0]=false;
                showArrivalCustom[1]=false;
                showDepartureCustom[0]=true;
                showDepartureCustom[1]=true;
                break;
            case "Jikokukeisiki_Hatsuchaku":
                showArrival[0]=true;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=true;
                showArrivalCustom[0]=true;
                showArrivalCustom[1]=true;
                showDepartureCustom[0]=true;
                showDepartureCustom[1]=true;
                break;
            case "Jikokukeisiki_NoboriChaku":
                showArrival[0]=false;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=false;
                showArrivalCustom[0]=false;
                showArrivalCustom[1]=true;
                showDepartureCustom[0]=true;
                showDepartureCustom[1]=false;

                break;
            case "Jikokukeisiki_KudariChaku":
                showArrival[0]=true;
                showArrival[1]=false;
                showDeparture[0]=false;
                showDeparture[1]=true;
                showArrivalCustom[0]=true;
                showArrivalCustom[1]=false;
                showDepartureCustom[0]=false;
                showDepartureCustom[1]=true;
                break;
            case "Jikokukeisiki_NoboriHatsuChaku":
                showArrival[0]=false;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=true;
                showArrivalCustom[0]=false;
                showArrivalCustom[1]=true;
                showDepartureCustom[0]=true;
                showDepartureCustom[1]=true;
                break;
            case "Jikokukeisiki_KudariHatsuChaku":
                showArrival[0]=true;
                showArrival[1]=true;
                showDeparture[0]=false;
                showDeparture[1]=true;
                showArrivalCustom[0]=true;
                showArrivalCustom[1]=true;
                showDepartureCustom[0]=false;
                showDepartureCustom[1]=true;

                break;
            default:
                showArrival[0]=false;
                showArrival[1]=false;
                showDeparture[0]=true;
                showDeparture[1]=true;
                showArrivalCustom[0]=false;
                showArrivalCustom[1]=false;
                showDepartureCustom[0]=true;
                showDepartureCustom[1]=true;
        }
    }

    /**
     * 発着表示からOuDia2ndファイルのJikokukeisikiを求める
     */
    private String getTimeTableStyle(){
        int result=0;
        if(showArrival[1]){
            result+=8;
        }
        if(showDeparture[1]){
            result+=4;
        }
        if(showArrival[0]){
            result+=2;
        }
        if(showDeparture[0]){
            result+=1;
        }
        switch (result){
            case 5:
                return "Jikokukeisiki_Hatsu";
            case 15:
                return "Jikokukeisiki_Hatsuchaku";
            case 6:
                return "Jikokukeisiki_KudariChaku";
            case 9:
                return "Jikokukeisiki_NoboriChaku";
            case 13:
                return "Jikokukeisiki_NoboriHatsuChaku";
            case 7:
                return "Jikokukeisiki_KudariHatsuChaku";
            default:
                return "Jikokukeisiki_Hatsu";
        }
    }
    /**
     * 発着表示からOuDiaファイルのJikokukeisikiを求める
     */

    private String getTimeTableStyleOuDia(){
        int result=0;
        if(showArrivalCustom[1]){
            result+=8;
        }
        if(showDepartureCustom[1]){
            result+=4;
        }
        if(showArrivalCustom[0]){
            result+=2;
        }
        if(showDepartureCustom[0]){
            result+=1;
        }
        switch (result){
            case 5:
                return "Jikokukeisiki_Hatsu";
            case 15:
                return "Jikokukeisiki_Hatsuchaku";
            case 6:
                return "Jikokukeisiki_KudariChaku";
            case 9:
                return "Jikokukeisiki_NoboriChaku";
            default:
                return "Jikokukeisiki_Hatsu";
        }
    }

    /**
     * oudiaファイルの文字列形式からDiagramRessyajouhouHyoujiを読み込みます
     */
    private void setShowDiagramInfo(int direction,String value){
        switch (value){
            case "DiagramRessyajouhouHyouji_Anytime":
                showDiagramInfo[direction]=1;
                break;
            case "DiagramRessyajouhouHyouji_Not":
                showDiagramInfo[direction]=2;
                break;
        }
    }

    /**
     * OuDia2ndで廃止されたborder変数ですが、
     * 2ndの形式からborder情報を復元します
     */
    public boolean getBorder(){
        if(border)return true;
        if (brunchCoreStationIndex != -1 && brunchCoreStationIndex > lineFile.station.indexOf(this))
            return true;
        int stationIndex = lineFile.station.indexOf(this);
        if (stationIndex < lineFile.getStationNum() - 1) {
            int b = lineFile.station.get(stationIndex + 1).brunchCoreStationIndex;
            return b >= 0 && b < stationIndex;
        }
        return false;
    }

    /**
     * @return 番線数
     */
    public int getTrackNum() {
        return tracks.size();
    }

    /**
     * @return 番線名
     */
    public String getTrackName(int trackIndex) {
        return tracks.get(trackIndex).trackName;
    }

    /**
     * @return 番線略称
     */
    public String getTrackShortName(int trackIndex) {
        if(trackIndex<0||trackIndex>= tracks.size()){
            return "";
        }
        String result=tracks.get(trackIndex).trackShortName;
        if(result==null||result.length()==0){
            return tracks.get(trackIndex).trackName;
        }
        return result;
    }


    /**
     * oudia2nd形式で保存します
     */
    void saveToFile(PrintWriter out){
        out.println("Eki.");
        out.println("Ekimei="+name);
        out.println("stationID="+stationID);
        if(shortName.length()!=0){
            out.println("EkimeiJikokuRyaku="+shortName);
        }
        out.println("Ekijikokukeisiki="+getTimeTableStyle());
        if(bigStation){
            out.println("Ekikibo=Ekikibo_Syuyou");
        }else{
            out.println("Ekikibo=Ekikibo_Ippan");
        }
        switch(showDiagramInfo[0]){
            case 1:
                out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Anytime");
                break;
            case 2:
                out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Not");
                break;
        }
        switch(showDiagramInfo[1]){
            case 1:
                out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Anytime");
                break;
            case 2:
                out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Not");
                break;
        }
        out.println("DownMain="+stopMain[0]);
        out.println("UpMain="+stopMain[1]);
        out.println("EkiTrack2Cont.");
        for(int i=0;i<tracks.size();i++){
            tracks.get(i).saveToFile(out);
        }
        out.println(".");
        for(int i=0;i<outerTerminals.size();i++){
            outerTerminals.get(i).saveToFile(out);
        }

        if(showDiagramTrack) {
            out.println("DiagramTrackDisplay=1");
        }
        if(brunchCoreStationIndex>=0){
            out.println("BrunchCoreEkiIndex="+brunchCoreStationIndex);
        }
        if(brunchOpposite){
            out.println("BrunchOpposite=1");
        }
        if(loopOriginStationIndex>=0){
            out.println("LoopOriginEkiIndex="+loopOriginStationIndex);
        }
        if(loopOpposite){
            out.println("LoopOpposite=1");
        }
        if(showtrack[0]){
            out.println("JikokuhyouTrackDisplayKudari=1");
        }
        if(showtrack[1]){
            out.println("JikokuhyouTrackDisplayNobori=1");
        }
        if(showDiagramTrack){
            out.println("DiagramTrackDisplay=1");
        }
        if(nextStationDistance>0){
            out.println("NextEkiDistance="+nextStationDistance);
        }
        if(omitTrack){
            out.println("JikokuhyouTrackOmit=1");
        }
        if(stationOperationNum[0]>0){
            out.println("JikokuhyouOperationOrigin="+stationOperationNum[0]);
        }
        if(stationOperationNum[1]>0){
            out.println("JikokuhyouOperationTerminal="+stationOperationNum[1]);
        }
        out.println("JikokuhyouJikokuDisplayKudari="+boolean2String(showArrivalCustom[0])+","+boolean2String(showDepartureCustom[0]));
        out.println("JikokuhyouJikokuDisplayNobori="+boolean2String(showArrivalCustom[1])+","+boolean2String(showDepartureCustom[1]));
        out.println("JikokuhyouSyubetsuChangeDisplayKudari="+showTrainNumberCustom[0]+","+showTrainOperationCustom[0]+","+showTrainTypeCustom[0]+","+showTrainNameCustom[0]);
        out.println("JikokuhyouSyubetsuChangeDisplayNobori="+showTrainNumberCustom[1]+","+showTrainOperationCustom[1]+","+showTrainTypeCustom[1]+","+showTrainNameCustom[1]);
        out.println(".");
    }

    /**
     * oudia形式で保存します
     */
    void saveToOuDiaFile(PrintWriter out){
        out.println("Eki.");
        out.println("Ekimei="+name);
        out.println("stationID="+stationID);
        out.println("Ekijikokukeisiki="+getTimeTableStyleOuDia());
        if(bigStation){
            out.println("Ekikibo=Ekikibo_Syuyou");
        }else{
            out.println("Ekikibo=Ekikibo_Ippan");
        }
        if(getBorder()){
            out.println("Kyoukaisen=1");

        }
        switch(showDiagramInfo[0]){
            case 1:
                out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Anytime");
                break;
            case 2:
                out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Not");
                break;
        }
        switch(showDiagramInfo[1]){
            case 1:
                out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Anytime");
                break;
            case 2:
                out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Not");
                break;
        }
        out.println(".");
    }

    /**
     * booleanが真の時に1、偽の時に0を返す
     */
    private String boolean2String(boolean value){
        if(value)
        {
            return "1";
        }else{
            return "0";
        }
    }

    /**
     * 駅を複製します
     * @param lineFile 複製した駅の親LineFile
     * @return 複製した駅
     */
    public Station clone(LineFile lineFile){
        try {
            Station result = (Station) super.clone();
            result.lineFile=lineFile;
            result.showArrival = showArrival.clone();
            result.showArrivalCustom = showArrivalCustom.clone();
            result.showDeparture = showDeparture.clone();
            result.showDepartureCustom = showDepartureCustom.clone();
            result.showDiagramInfo = showDiagramInfo.clone();
            result.showtrack = showtrack.clone();
            result.showTrainNameCustom = showTrainNameCustom.clone();
            result.showTrainNumberCustom = showTrainNumberCustom.clone();
            result.showTrainOperationCustom = showTrainOperationCustom.clone();
            result.showTrainTypeCustom = showTrainTypeCustom.clone();
            result.stationOperationNum = stationOperationNum.clone();
            result.stopMain = stopMain.clone();
            result.outerTerminals = new ArrayList<>();
            for (OuterTerminal terminal : outerTerminals) {
                result.outerTerminals.add(terminal.clone());
            }
            result.tracks = new ArrayList<>();
            for (StationTrack track : tracks) {
                result.tracks.add(track.clone());
            }
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new Station(lineFile);
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */


    /**
     * 着時刻を表示するか
     * Custom時刻表を基準にします
     */
    public boolean showAriTime(int direction) {
        return showArrivalCustom[direction];
    }

    /**
     * 発着番線を表示するか
     * Custom時刻表を基準にします
     */
    public boolean showTrack(int direction) {
        return showtrack[direction];
    }

    /**
     * 発時刻を表示するか
     * Custom時刻表を基準にします
     */
    public boolean showDepTime(int direction) {
        return showDepartureCustom[direction];
    }

    /**
     * 路線外駅名を返します
     */
    public String getOuterStationTimeTableName(int index){
        try {
            if (outerTerminals.get(index).outerTerminalTimeTableName.length() != 0) {
                return outerTerminals.get(index).outerTerminalTimeTableName;
            }
            return outerTerminals.get(index).outerTerminalName;
        }catch (Exception e){
            SDlog.log(e);
            return null;
        }
    }


    /**
     * 番線名を入力します
     */
    public void setTrackName(int index, String value) {
        if (index < 0 || index >= getTrackNum()) {
            return;
        }
        tracks.get(index).trackName = value;

    }

    /**
     * 番線略称を入力します
     */
    public void setTrackShortName(int index, String value) {
        if (index < 0 || index >= getTrackNum()) {
            return;
        }
        tracks.get(index).trackShortName = value;
    }

    /**
     * 発着番線を追加します
     */
    public void addTrack(StationTrack track) {
        tracks.add(track);
    }

    /**
     * 発着番線を削除します。
     * 主発着番線に指定されている場合　削除せずfalseを返す
     */
    public boolean deleteTrack(int index) {
        if(stopMain[Train.DOWN]==index){
            return false;
        }
        if(stopMain[Train.UP]==index){
            return false;
        }
        if(stopMain[Train.DOWN]>index){
            stopMain[Train.DOWN]--;
        }
        if(stopMain[Train.UP]>index){
            stopMain[Train.UP]--;
        }
        final int stationIndex = lineFile.station.indexOf(this);

        for (Diagram dia : lineFile.diagram) {
            for (Train train : dia.trains[0]) {
                if (train.getStopTrack(stationIndex) == index) {
                    train.setStopTrack(stationIndex, -1);
                }
                if (train.getStopTrack(stationIndex) > index) {
                    train.setStopTrack(stationIndex, train.getStopTrack(stationIndex) - 1);
                }
            }
            for (Train train : dia.trains[1]) {
                if (train.getStopTrack(stationIndex) == index) {
                    train.setStopTrack(stationIndex, -1);
                }
                if (train.getStopTrack(stationIndex) > index) {
                    train.setStopTrack(stationIndex, train.getStopTrack(stationIndex) - 1);
                }
            }
        }
        tracks.remove(index);
        return true;

    }

    /**
     * 路線外始終着駅を追加します
     */
    public void addOuterTerminal(OuterTerminal terminal) {
        outerTerminals.add(terminal);
    }

    /**
     * 路線外始終着駅を削除します
     * 列車が使用している場合　false
     * 削除に成功した場合　true
     */
    public boolean deleteOuterTerminal(OuterTerminal terminal) {
        int index=outerTerminals.indexOf(terminal);
        if(index<outerTerminals.size()&&index>=0){
return deleteOuterTerminal(index);
        }
        return true;
    }

        public boolean deleteOuterTerminal(int index) {
        final int stationIndex = lineFile.station.indexOf(this);
        for (Diagram dia : lineFile.diagram) {
            for(int direction=0;direction<2;direction++) {
                for (Train train : dia.trains[direction]) {
                    StationTime sTime = train.stationTimes.get(stationIndex);
                    for (StationTimeOperation ope : sTime.beforeOperations) {
                        if (ope.operationType == 4 && ope.intData1 == index) {
                            return false;
                        }
                    }
                    for (StationTimeOperation ope : sTime.afterOperations) {
                        if (ope.operationType == 4 && ope.intData1 == index) {
                            return false;
                        }
                    }
                }
            }
        }
        for (Diagram dia : lineFile.diagram) {
            for(int direction=0;direction<2;direction++) {
                for (Train train : dia.trains[direction]) {
                    StationTime sTime = train.stationTimes.get(stationIndex);
                    for (StationTimeOperation ope : sTime.beforeOperations) {
                        if (ope.operationType == 4 && ope.intData1 > index) {
                            ope.intData1--;
                        }
                    }
                    for (StationTimeOperation ope : sTime.afterOperations) {
                        if (ope.operationType == 4 && ope.intData1 > index) {
                            ope.intData1--;
                        }
                    }
                }
            }
        }
        outerTerminals.remove(index);
        return true;

    }

    /**
     * 路線が逆転されることに従い、この駅の発着時刻表示情報なども反転されます
     */
    public void reverse(){
        reverse(this.showArrival);
        reverse(this.showArrivalCustom);
        reverse(this.showDeparture);
        reverse(this.showDepartureCustom);
        reverse(this.showDiagramInfo);
        reverse(this.showtrack);
        reverse(this.showTrainNameCustom);
        reverse(this.showTrainNumberCustom);
        reverse(this.showTrainOperationCustom);
        reverse(this.showTrainTypeCustom);
        reverse(this.stationOperationNum);
        reverse(this.stopMain);
        //分岐駅情報も反転対象
        if(brunchCoreStationIndex>=0) {
            brunchCoreStationIndex = lineFile.getStationNum() - brunchCoreStationIndex - 1;
        }
        if(loopOriginStationIndex>=0){
            loopOriginStationIndex=lineFile.getStationNum()-loopOriginStationIndex-1;

        }
    }

    /**
     * オブジェクトを逆転させる
     */
    private static <T> void reverse(T[] task){
        T temp=task[0];
        task[0]=task[1];
        task[1]=temp;

    }

    /**
     * boolean  型の配列を逆転させる
     */
    private static void reverse(boolean[] task){
        boolean temp=task[0];
        task[0]=task[1];
        task[1]=temp;

    }

    private void reverse(int [] task){
        int temp=task[0];
        task[0]=task[1];
        task[1]=temp;

    }

}
