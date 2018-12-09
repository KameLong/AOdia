package com.kamelong.OuDia;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Station {
    public DiaFile diaFile;
    public String name="";
    /**
     * 上り番線表示
     * 上り着時刻表示
     * 上り番線表示
     * 下り着時刻表示
     * 下り発時刻表示
     * 下り発時刻表示
     */
    public int timeTableStyle=0b001001;
    public boolean bigStation =false;
    public ArrayList<String> trackName=new ArrayList<>();
    public ArrayList<String> trackshortName=new ArrayList<>();
    public int[] stopMain=new int[]{1,2};
    public int brunchStationIndex=-1;
    public boolean border=false;
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

}
