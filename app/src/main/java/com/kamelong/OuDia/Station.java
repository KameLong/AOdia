package com.kamelong.OuDia;

import java.io.BufferedReader;
import java.util.ArrayList;

public class Station {
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

    /**
     *
     * @param br readLineでEki.を得た後
     */
    public Station(BufferedReader br){
        trackName.add("");
        trackshortName.add("");
        try {
            String line = br.readLine();
            while (!line.equals(".")){
                if(line.equals("EkiTrack2Cont.")){
                    while(!line.equals(".")){
                        if(line.equals("EkiTrack2.")){
                            while(!line.equals(".")) {
                                if(line.split("=",-1)[0].equals("TrackName")){
                                    trackName.add(line.split("=",-1)[1]);
                                }
                                if(line.split("=",-1)[0].equals("TrackTrackRyakusyou")){
                                    trackshortName.add(line.split("=",-1)[1]);
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
                line=br.readLine();
            }
        }catch (Exception e){
         e.printStackTrace();
        }
        if(trackName.size()==1){
            trackName.add("1番線");
            trackName.add("2番線");
            trackshortName.add("1");
            trackshortName.add("2");
        }

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

}
