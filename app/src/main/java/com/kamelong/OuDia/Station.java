package com.kamelong.OuDia;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Deque;

public class Station {
    public DiaFile diaFile;
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

    public int[] timeTableStyle=new int[]{0x00001,0x00001};
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
     駅ID
     同名駅が存在する場合、これを区別するために使用します。
     駅がContにset,insertされるとき、空いている番号が付与されます。
     */
    public int stationID=-1;
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
     分岐駅設定・環状線設定による、駅の繋がりをマッピングしたものです。
     stationIndexLoopに、分岐の基幹駅及び環状線の起点駅・終点駅がstationIndexの降順に収まります。
     stationIndexBrunchOriginSideには、stationIndexLoopの先頭の駅を基幹駅とする派生駅がstationindexの降順に収まります。
     stationIndexBrunchTerminalSideには、stationiIndexLoopの末尾の駅を基幹駅とする派生駅がstationindexの降順に収まります。
     brunchLoopPositionは、これらの中における、当駅の位置を表します。
     INT_MIN:当駅が起点方派生駅の場合、この値が返されます
     -1:当駅が単独駅(分岐駅及び環状線と無関係)の場合、この値が返されます
     0以上:当駅が基幹駅及び起点駅・終点駅の場合、
     iEkiIndexLoop内におけるIndexを返します。
     INT_MAX:当駅が終点方派生駅の場合、この値が返されます

     これらの要素は、駅編集及びより上位の編集が行われるたびに更新されます。
     */
    public int brunchLoopPosition;
    Deque<Integer> stationIndexBrunchOriginSide;
    Deque<Integer> stationIndexLoop;
    Deque<Integer> stationIndexBrunchTerminalSide;

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
    public boolean[] showDepatureCustom={false,false};


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




    public Station(DiaFile diaFile){
        this.diaFile=diaFile;
        name="新規作成";

        tracks.add(new StationTrack("1番線","1"));
        tracks.add(new StationTrack("2番線","2"));


    }

    /**
     * OuDiaファイル1行の情報を読み取ります
     * @param title
     * @param value
     */
    public void setValue(String title, String value){
        switch (title){
            case "Ekimei":
                name=value;
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
                break;
            case  "UpMain":
                stopMain[1]=Integer.parseInt(value);
                break;
            case "BrunchCoreEkiIndex":
                brunchCoreStationIndex=Integer.valueOf(value);
                break;
            case "BrunchOpposite":
                brunchOpposite=value.equals("1");
                break;
            case "LoopOriginEkiIndex":
                loopOriginStationIndex=Integer.valueOf(value);
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
                nextStationDistance=Integer.valueOf(value);
                break;
            case "JikokuhyouTrackOmit":
                omitTrack=value.equals("1");
                break;
            case "JikokuhyouOperationOrigin":
                //todo;
                break;
            case "JikokuhyouOperationTerminal":
                //todo;
                break;
            case "JikokuhyouJikokuDisplayKudari":
                showArrivalCustom[0]=value.split(",")[0].equals("1");
                showDepatureCustom[0]=value.split(",")[1].equals("1");
                break;
            case "JikokuhyouJikokuDisplayNobori":
                showArrivalCustom[1]=value.split(",")[0].equals("1");
                showDepatureCustom[1]=value.split(",")[1].equals("1");
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
        }
    }


    /**
     * oudiaファイルの文字列形式からtimetableStyleを読み込みます
     * @param str
     */
    private void setTimeTableStyle(String str){
        switch (str){
            case "Jikokukeisiki_Hatsu":
                showArrival[0]=false;
                showArrival[1]=false;
                showDeparture[0]=true;
                showDeparture[1]=true;
                break;
            case "Jikokukeisiki_Hatsuchaku":
                showArrival[0]=true;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=true;
                break;
            case "Jikokukeisiki_NoboriChaku":
                showArrival[0]=false;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=false;
                break;
            case "Jikokukeisiki_KudariChaku":
                showArrival[0]=true;
                showArrival[1]=false;
                showDeparture[0]=false;
                showDeparture[1]=true;
                break;
            case "Jikokukeisiki_NoboriHatsuChaku":
                showArrival[0]=false;
                showArrival[1]=true;
                showDeparture[0]=true;
                showDeparture[1]=true;
                break;
            case "Jikokukeisiki_KudariHatsuChaku":
                showArrival[0]=true;
                showArrival[1]=true;
                showDeparture[0]=false;
                showDeparture[1]=true;
                break;
            default:
                showArrival[0]=false;
                showArrival[1]=false;
                showDeparture[0]=true;
                showDeparture[1]=true;
        }
    }
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
     * oudiaファイルの文字列形式からDiagramRessyajouhouHyoujiを読み込みます
     * @param value
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


    public boolean getBorder(){
        if(border)return true;
        if(brunchCoreStationIndex!=-1&&brunchCoreStationIndex>diaFile.station.indexOf(this))return true;
        int stationIndex=diaFile.station.indexOf(this);
        if(stationIndex<diaFile.getStationNum()-1){
            int b=diaFile.station.get(stationIndex+1).brunchCoreStationIndex;
            if(b>=0&&b<stationIndex){
                return true;
            }
        }
        return false;
    }

    /**
     * 路線外発着駅の名前を取得する
     * @param index
     * @return
     */
    public String getOuterTerminalStationName(int index){
        return outerTerminals.get(index).outerTerminalName;
    }
    public void setOuterTerminalStationName(int index,String name){
        outerTerminals.get(index).outerTerminalName=name;
    }



    public void saveToFile(FileWriter out) throws Exception {
        out.write("Eki.\r\n");

        out.write("Ekimei="+name+"\r\n");
        if(shortName.length()!=0){
            out.write("EkimeiJikokuRyaku="+shortName+"\r\n");
        }
        out.write("Ekijikokukeisiki="+getTimeTableStyle()+"\r\n");
        if(bigStation){
            out.write("Ekikibo=Ekikibo_Syuyou\r\n");
        }
        switch(showDiagramInfo[0]){
            case 1:
                out.write("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Anytime\r\n");
                break;
            case 2:
                out.write("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Not\r\n");
                break;
        }
        switch(showDiagramInfo[1]){
            case 1:
                out.write("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Anytime\r\n");
                break;
            case 2:
                out.write("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Not\r\n");
                break;
        }
        out.write("DownMain="+stopMain[0]+"\r\n");
        out.write("UpMain="+stopMain[1]+"\r\n");
        out.write("EkiTrack2Cont.");
        for(int i=0;i<tracks.size();i++){
            tracks.get(i).saveToFile(out);
        }
        out.write(".");
        out.write("OuterTerminal.");
        for(int i=0;i<outerTerminals.size();i++){
            outerTerminals.get(i).saveToFile(out);
        }
        out.write(".");

        if(showDiagramTrack) {
            out.write("DiagramTrackDisplay=1\r\n");
        }
        if(brunchCoreStationIndex>=0){
            out.write("BrunchCoreEkiIndex="+brunchCoreStationIndex+"\r\n");
        }
        if(brunchOpposite){
            out.write("BrunchOpposite=1\r\n");
        }
        if(loopOriginStationIndex>=0){
            out.write("LoopOriginEkiIndex="+loopOriginStationIndex+"\r\n");
        }
        if(loopOpposite){
            out.write("LoopOpposite=1\r\n");
        }
        if(showtrack[0]){
            out.write("JikokuhyouTrackDisplayKudari=1\r\n");
        }
        if(showtrack[1]){
            out.write("JikokuhyouTrackDisplayNobori=1\r\n");
        }
        if(showDiagramTrack){
            out.write("DiagramTrackDisplay=1\r\n");
        }
        if(nextStationDistance>0){
            out.write("NextEkiDistance="+nextStationDistance+"\r\n");
        }
        if(omitTrack){
            out.write("JikokuhyouTrackOmit=1\r\n");
        }
        //todo "JikokuhyouOperationOrigin";
        //todo "JikokuhyouOperationTerminal";
        out.write("JikokuhyouJikokuDisplayKudari="+boolean2String(showArrivalCustom[0])+","+boolean2String(showDepatureCustom[0])+"\r\n");
        out.write("JikokuhyouJikokuDisplayNobori="+boolean2String(showArrivalCustom[1])+","+boolean2String(showDepatureCustom[1])+"\r\n");
        out.write("JikokuhyouSyubetsuChangeDisplayKudari="+showTrainNumberCustom[0]+","+showTrainOperationCustom[0]+","+showTrainTypeCustom[0]+","+showTrainNameCustom[0]+"\r\n");
        out.write("JikokuhyouSyubetsuChangeDisplayNobori="+showTrainNumberCustom[1]+","+showTrainOperationCustom[1]+","+showTrainTypeCustom[1]+","+showTrainNameCustom[1]+"\r\n");
        out.write(".\r\n");
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



}
