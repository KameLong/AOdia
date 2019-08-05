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
    /**
     * 発着表示
     *
     * 二進数bitで表す
     * 上から順に
     *
     * カスタマイズ時刻表発着番線表示
     * ダイヤグラム番線表示
     * 番線表示
     * 着時刻表示
     * 発時刻表示
     */
    public int[] timeTableStyle=new int[]{0b00001,0b00001};
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

    public Station(DiaFile diaFile){
        this.diaFile=diaFile;
        name="新規作成";
        trackName.add("");
        trackshortName.add("");
        trackName.add("1番線");
        trackName.add("2番線");
        trackshortName.add("1");
        trackshortName.add("2");

    }

    /**
     *
     * @param br readLineでEki.を得た後
     */
    public Station(BufferedReader br,DiaFile diaFile)throws Exception{
        this.diaFile=diaFile;
        trackName.add("");
        trackshortName.add("");
        String line = br.readLine();
        while (!line.equals(".")){
            if(line.equals("EkiTrack2Cont.")){
                while(!line.equals(".")){
                    if(line.equals("EkiTrack2.")){
                        while(!line.equals(".")) {
                            if(line.split("=",-1)[0].equals("TrackName")){
                                trackName.add(line.split("=",-1)[1]);
                                if(trackName.get(trackName.size()-1).length()==0){
                                    trackName.set(trackName.size()-1,trackName.size()-1+"番線");
                                }
                            }
                            if(line.split("=",-1)[0].equals("TrackRyakusyou")){
                                trackshortName.add(line.split("=",-1)[1]);
                                if(trackshortName.get(trackshortName.size()-1).length()==0){
                                    trackshortName.set(trackshortName.size()-1,trackshortName.size()-1+"");
                                }
                            }
                            line=br.readLine();
                        }
                    }
                    line=br.readLine();
                }
            }
            String title=line.split("=",-1)[0];
            if(title.equals("Ekimei")){
                name=line.split("=",-1)[1];
            }
            if(title.equals("Ekijikokukeisiki")){
                setTimeTableStyle(line.split("=",-1)[1]);
            }
            if(title.equals("Ekikibo")){
                if(line.split("=",-1)[1].equals("Ekikibo_Syuyou")){
                    bigStation =true;
                }else{
                    bigStation =false;
                }
            }
            if(title.equals("JikokuhyouTrackDisplayKudari")){
                timeTableStyle=timeTableStyle|0b000100;
            }
            if(title.equals("JikokuhyouTrackDisplayNobori")){
                timeTableStyle=timeTableStyle|0b100000;
            }
            if(title.equals("BrunchCoreEkiIndex")){
                brunchStationIndex=Integer.parseInt(line.split("=",-1)[1]);
            }
            if(title.equals("Kyoukaisen")){
                border=line.split("=",-1)[1].equals("1");
            }
            if(title.equals("DownMain")){
                stopMain[0]=Integer.parseInt(line.split("=",-1)[1]);
            }
            if(title.equals("UpMain")){
                stopMain[1]=Integer.parseInt(line.split("=",-1)[1]);
            }

            line=br.readLine();
        }
        //ここから駅データに不正がないかチェック
        if(trackName.size()==1){
            trackName.add("1番線");
            trackName.add("2番線");
            trackshortName.add("1");
            trackshortName.add("2");
        }
        for(int i=0;i<2;i++) {
            if (trackName.size() <= stopMain[i]) {
                stopMain[i]=i+1;
            }
            if (trackName.size() <= stopMain[i]) {
                stopMain[i]=1;
            }
        }
        for(int i=trackshortName.size();i<trackName.size();i++){
            trackshortName.add(i+"");

        }

    }
    public Station(Station old){
        diaFile=old.diaFile;
        name=old.name;
        timeTableStyle=old.timeTableStyle;
        bigStation=old.bigStation;
        trackName=new ArrayList<>(old.trackName);
        trackshortName=new ArrayList<>(old.trackshortName);
        stopMain[0]=old.stopMain[0];
        stopMain[1]=old.stopMain[1];
        brunchStationIndex=old.brunchStationIndex;
        border=old.border;
    }
    private void setTimeTableStyle(String str){
        timeTableStyle=timeTableStyle&0b100100;
        switch (str){
            case "Jikokukeisiki_Hatsu":
                timeTableStyle=timeTableStyle|0b001001;
                break;
            case "Jikokukeisiki_Hatsuchaku":
                timeTableStyle=timeTableStyle|0b011011;
                break;
            case "Jikokukeisiki_NoboriChaku":
                timeTableStyle=timeTableStyle|0b010001;
                break;
            case "Jikokukeisiki_KudariChaku":
                timeTableStyle=timeTableStyle|0b001010;
                break;
            case "Jikokukeisiki_NoboriHatsuChaku":
                timeTableStyle=timeTableStyle|0b011001;
                break;
            case "Jikokukeisiki_KudariHatsuChaku":
                timeTableStyle=timeTableStyle|0b001011;
                break;
            default:
                timeTableStyle=timeTableStyle|0b001001;
        }
    }

    public int getTimeTableStyle(int direction){
        switch (direction){
            case 0:
                return timeTableStyle&0b000111;
            case 1:
                return (timeTableStyle&0b111000)/8;
        }
        return 0;
    }
    public boolean getBorder(){
        if(border)return true;
        if(brunchStationIndex!=-1&&brunchStationIndex>diaFile.station.indexOf(this))return true;
        int stationIndex=diaFile.station.indexOf(this);
        if(stationIndex<diaFile.getStationNum()-1){
            int b=diaFile.station.get(stationIndex+1).brunchStationIndex;
            if(b>=0&&b<stationIndex){
                return true;
            }
        }

        return false;

    }
    public void setShowArival(int direction,boolean b){
        if(direction==0) {
            timeTableStyle = timeTableStyle & 0b111101;
            if(b) {
                timeTableStyle = timeTableStyle | 0b000010;
            }
        }else{
            timeTableStyle = timeTableStyle & 0b101111;
            if(b) {
                timeTableStyle = timeTableStyle | 0b010000;
            }
        }
    }
    public void setShowStop(int direction,boolean b){
        if(direction==0) {
            timeTableStyle = timeTableStyle & 0b111011;
            if(b) {
                timeTableStyle = timeTableStyle | 0b000100;
            }
        }else{
            timeTableStyle = timeTableStyle & 0b011111;
            if(b) {
                timeTableStyle = timeTableStyle | 0b100000;
            }
        }
    }
    public void setShowDepart(int direction,boolean b){
        if(direction==0) {
            timeTableStyle = timeTableStyle & 0b111110;
            if(b) {
                timeTableStyle = timeTableStyle | 0b000001;
            }
        }else{
            timeTableStyle = timeTableStyle & 0b110111;
            if(b) {
                timeTableStyle = timeTableStyle | 0b001000;
            }
        }
    }
    public void saveToFile(FileWriter out) throws Exception {
            out.write("Eki.\r\n");
            out.write("Ekimei="+name+"\r\n");
            String style="";
            switch (timeTableStyle & 0b011011){
                case 0b001001:
                    style="Jikokukeisiki_Hatsu";
                    break;
                case 0b011011:
                    style="Jikokukeisiki_Hatsuchaku";
                    break;
                case 0b001010:
                    style="Jikokukeisiki_KudariChaku";
                    break;
                case 0b010001:
                    style="Jikokukeisiki_NoboriChaku";
                    break;
                case 0b001011:
                    style="Jikokukeisiki_KudariHatsuchaku";
                    break;
                case 0b011001:
                    style="Jikokukeisiki_NoboriHatsuchaku";
                    break;
                default:
                    style="Jikokukeisiki_Hatsu";
                    break;
            }
            if((timeTableStyle&0b000100)!=0){
                out.write("JikokuhyouTrackDisplayKudari=1\r\n");
            }
        if((timeTableStyle&0b100000)!=0){
            out.write("JikokuhyouTrackDisplayNobori=1\r\n");
        }
            out.write("Ekijikokukeisiki="+style+"\r\n");
            if(bigStation){
                out.write("Ekikibo="+"Ekikibo_Syuyou"+"\r\n");
            }else{
                out.write("Ekikibo="+"Ekikibo_Ippan"+"\r\n");
            }
            out.write("DownMain="+stopMain[0]+"\r\n");
            out.write("UpMain="+stopMain[1]+"\r\n");
            if(brunchStationIndex>=0) {
                out.write("BrunchCoreEkiIndex=" + brunchStationIndex + "\r\n");
            }
            if(border){
                out.write("Kyoukaisen=1\r\n");
            }
            out.write("EkiTrack2Cont.\r\n");
            for(int i=1;i<trackName.size();i++){
                out.write("EkiTrack2.\r\n");
                out.write("TrackName="+trackName.get(i)+"\r\n");
                out.write("TrackRyakusyou="+trackshortName.get(i)+"\r\n");
                out.write(".\r\n");
            }

            out.write(".\r\n");
            out.write(".\r\n");
    }


    public class EkiTrack{

    }

}
