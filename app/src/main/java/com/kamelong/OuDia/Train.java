package com.kamelong.OuDia;

import com.kamelong.aodia.SdLog;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Train {
    public DiaFile diaFile;
    public String name="";
    public String number="";
    public String count="";
    public String remark="";
    public int type=0;
    public String operationName="";
    public boolean leaveYard=false;
    public boolean goYard=false;
    public int direction=0;
    public int stationNum=0;
    /**
     *  1-24bit departure
     * 25-48bit arrive
     * 49-56bit stopNumber
     * 57-60bit stopType
     */
    public long[] time;

    public static final int DEPART=0;
    public static final int ARRIVE=1;



    public Train(DiaFile diaFile,int direction){
        this.diaFile=diaFile;
        this.direction=direction;
        this.stationNum=diaFile.getStationNum();
        time=new long[stationNum];
        for(int i=0;i<time.length;i++) {
            time[i] = 0;
        }
    }
    public Train(Train train){
        this(train.diaFile,train.direction);
        name=train.name;
        number=train.number;
        count=train.count;
        remark=train.remark;
        type=train.type;
        for(int i=0;i<stationNum;i++){
            time[i]=train.time[i];
        }
    }
    public Train(DiaFile diaFile,int direction,BufferedReader br)throws Exception{
        this(diaFile,direction);
            String line = br.readLine();
            while(!line.equals(".")){
                String title=line.split("=",-1)[0];
                String value=line.split("=",-1)[1];
                switch (title){
                    case "Syubetsu":
                        type=Integer.parseInt(value);
                        break;
                    case "Ressyabangou":
                        number=value;
                        break;
                    case "Ressyamei":
                        name=value;
                        break;
                    case "Gousuu":
                        count=value;
                        break;
                    case "EkiJikoku":
                        setOuDiaTime(value.split(",",-1));
                        break;
                    case "RessyaTrack":
                        setOuDiaTrack(value.split(",",-1));
                        break;
                    case "Bikou":
                        remark=value;
                        break;
                    case "OperationNumber":
                        operationName=value;
                        break;
                }
                line=br.readLine();
            }

    }
    private void setOuDiaTime(String[] value){
        for(int i=0;i<value.length&&i<time.length;i++){
            int station=direction*(stationNum-1)+(1-2*direction)*i;
            if(value[i].length()==0){
                setStopType(station,0);
                continue;
            }
            if(!value[i].contains(";")){
                setStopType(station,Integer.parseInt(value[i]));
                continue;
            }
            setStopType(station,Integer.parseInt(value[i].split(";",-1)[0]));
            String str=value[i].split(";",-1)[1];
            if(str.contains("/")){
                setArrivalTime(station,timeStringToInt(str.split("/",-1)[0]));
                if(str.split("/",-1)[1].length()!=0) {
                    setDepartureTime(station, timeStringToInt(str.split("/", -1)[1]));
                }
            }else{
                setDepartureTime(station,timeStringToInt(str));
            }
        }

    }
    private void setOuDiaTrack(String[] value){
        if(direction==1){
            System.out.println("test");
        }
        for(int i=0;i<value.length&&i<time.length;i++) {
            int station=direction*(stationNum-1)+(1-2*direction)*i;

            if (value[i].length() == 0) {
                setStop(station, 0);
                continue;
            }
            if (value[i].contains(";")) {
                setStop(station, Integer.parseInt(value[i].split(";")[0]));
                if(station==startStation()){
                    if(value[i].split(";",-1)[1].startsWith("2")){
                        leaveYard=true;
                        if(value[i].split(";")[1].contains("/")){
                            operationName=value[i].split(";")[1].split("/",-1)[1];
                        }
                    }
                }
                else{
                    if(value[i].split(";")[1].startsWith("2")) {
                        goYard = true;
                    }
                }
            } else {
                setStop(station, Integer.valueOf(value[i]));
            }
        }
    }

    /**
     * 文字列形式の時刻を秒の数値に変える
     * @param sTime
     * @return
     */
    public static int timeStringToInt(String sTime){
        int hh=0;
        int mm=0;
        int ss=0;
        switch (sTime.length()){
            case 3:
                hh=Integer.parseInt(sTime.substring(0,1));
                mm=Integer.parseInt(sTime.substring(1,3));
                break;
            case 4:
                hh=Integer.parseInt(sTime.substring(0,2));
                mm=Integer.parseInt(sTime.substring(2,4));
                break;
            case 5:
                hh=Integer.parseInt(sTime.substring(0,1));
                mm=Integer.parseInt(sTime.substring(1,3));
                ss=Integer.parseInt(sTime.substring(3,5));
                break;
            case 6:
                hh=Integer.parseInt(sTime.substring(0,2));
                mm=Integer.parseInt(sTime.substring(2,4));
                ss=Integer.parseInt(sTime.substring(4,6));
                break;
            default:
                return -1;
        }
        if(hh>23||hh<0){
            return -1;
        }
        if(mm>59||mm<0){
            return -1;
        }
        if(ss>59||ss<0){
            return -1;
        }
        return 3600*hh+60*mm+ss;

    }
    private static String timeIntToString(int time){
        if(time<0)return"";
        int ss=time%60;
        time=time/60;
        int mm=time%60;
        time=time/60;
        int hh=time%24;
        return String.format("%02d", hh)  + String.format("%02d", mm) + String.format("%02d", ss);
    }

    public void setDepartureTime(int station,long value){
        if(value<0){
            time[station]=time[station]&0xFFFFFFFFFF000000L;
            return;
        }

        if(value>0x7FFFFF){
            return;
        }
        time[station]=time[station]&0xFFFFFFFFFF000000L;
        time[station]=time[station]|0x0000000000800000L;
        time[station]=time[station]|(value);

    }
    public int getDepartureTime(int station){
        if((time[station]&0x0000000000800000L)==0){
            return -1;
        }
        int result=(int)(time[station]&0x00000000007FFFFFL);
        if(result<diaFile.diagramStartTime){
            return result+24*3600;
        }else {
            return result;
        }
    }
    public void setArrivalTime(int station,long value){
        if(value<0){
            time[station]=time[station]&0xFFFF000000FFFFFFL;
            return;
        }
        if(value>0x7FFFFF){
            return;
        }
        time[station]=time[station]&0xFFFF000000FFFFFFL;
        time[station]=time[station]|0x0000800000000000L;
        time[station]=time[station]|(value<<24);
    }
    public int getArrivalTime(int station){
        if((time[station]&0x0000800000000000L)==0){
            return -1;
        }
        int result=(int)((time[station]&0x00007FFFFF000000L)>>>24);
        if(result<diaFile.diagramStartTime){
            return result+24*3600;
        }else {
            return result;
        }
    }
    public void setStop(int station,long value){
        if(value<0||value>255){
            return;
        }
        time[station]=time[station]&0xFF00FFFFFFFFFFFFL;
        time[station]=time[station]|(value<<48);
    }
    public int getStop(int station){
        return (int)((time[station]&0x00FF000000000000L)>>>48);
    }
    public void setStopType(int station,long value){
        if(value<0||value>15){
            return;
        }
        time[station]=time[station]&0xF0FFFFFFFFFFFFFFL;
        time[station]=time[station]|(value<<56);
    }
    public int getStopType(int station){
        return (int)((time[station]&0x0F00000000000000L)>>>56);
    }
    public boolean timeExist(int station){
        return (time[station]&0x0000800000800000L)!=0;
    }
    public boolean departExist(int station){
        return (time[station]&0x0000000000800000L)!=0;
    }
    public boolean arriveExist(int station){
        return (time[station]&0x0000800000000000L)!=0;
    }
    public int getADTime(int station){
        if(arriveExist(station)){
            return getArrivalTime(station);
        }
        if(departExist(station)){
            return getDepartureTime(station);
        }
        return -1;
    }
    public int getDATime(int station){
        if(departExist(station)){
            return getDepartureTime(station);
        }
        if(arriveExist(station)){
            return getArrivalTime(station);
        }
        return -1;
    }
    /**
     * 始発駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     * @return
     */
    public int startStation(){
        switch (direction){
            case 0:
                for(int i=0;i<time.length;i++){
                    if(timeExist(i))return i;
                }
                break;
            case 1:
                for(int i=time.length-1;i>=0;i--){
                    if(timeExist(i))return i;
                }
                break;
        }
        return -1;
    }
    /**
     * 終着駅を返す。
     * これ列車に時刻が存在しなければ-1を返す
     * @return
     */
    public int endStation(){
        switch (direction){
            case 0:
                for(int i=time.length-1;i>=0;i--){
                    if(timeExist(i))return i;
                }
                break;
            case 1:
                for(int i=0;i<time.length;i++){
                    if(timeExist(i))return i;
                }
                break;
        }
        return -1;
    }

    public int getRequiredTime(int startStation,int endStation){
        if(timeExist(startStation)&&timeExist(endStation)) {
            if ((endStation - startStation) * (1 - direction * 2) > 0) {
                return getADTime(endStation)-getDATime(startStation);

            } else {
                return getADTime(startStation)-getDATime(endStation);

            }
        }else{
            return -1;
        }

    }
    public static final int STOP_TYPE_NOSERVICE=0;
    public static final int STOP_TYPE_STOP=1;
    public static final int STOP_TYPE_PASS=2;
    public static final int STOP_TYPE_NOVIA=3;
    public int getPredictionTime(int station,int AD){
        if(AD==1&&arriveExist(station)){
            return getArrivalTime(station);
        }
        if(timeExist(station)){
            return getDATime(station);
        }
        if(getStopType(station)==2||getStopType(station)==1){
            //通過時間を予測します
            int afterTime=-1;//後方の時刻あり駅の発車時間
            int beforeTime=-1;//後方の時刻あり駅の発車時間
            int afterMinTime=0;//後方の時刻あり駅までの最小時間
            int beforeMinTime=0;//前方の時刻あり駅までの最小時間

            ArrayList<Integer> minstationTime=diaFile.getStationTime();

            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間と
            for(int i=station+1;i<diaFile.getStationNum();i++){
                if(getStopType(i)==STOP_TYPE_NOSERVICE||getStopType(i)==STOP_TYPE_NOVIA||getStopType(i-1)==STOP_TYPE_NOSERVICE||getStopType(i-1)==STOP_TYPE_NOVIA){
                    continue;
                }
                afterMinTime=afterMinTime+minstationTime.get(i)-minstationTime.get(i-1);
                if(timeExist(i)){
                    afterTime=getDepartureTime(i);
                    break;
                }
            }
            if(afterTime<0){
                SdLog.log("予測時間","afterTime");
                //対象駅より先の駅で駅時刻が存在する駅がなかった
                return -1;
            }
            //対象駅より前方の駅で駅時刻が存在する駅までの最小所要時間と駅時刻
            int startStation=0;
            for(int i=station;i>0;i--){
                if(getStopType(i)==STOP_TYPE_NOSERVICE||getStopType(i)==STOP_TYPE_NOVIA||getStopType(i-1)==STOP_TYPE_NOSERVICE||getStopType(i-1)==STOP_TYPE_NOVIA){
                    continue;
                }
                beforeMinTime=beforeMinTime+minstationTime.get(i)-minstationTime.get(i-1);
                if(timeExist(i-1)){
                    beforeTime=getDepartureTime(i-1);
                    startStation=i-1;
                    break;
                }
            }
            if(beforeTime<0){
                return -1;
            }
            return getDepartureTime(startStation)+(afterTime-beforeTime)*beforeMinTime/(afterMinTime+beforeMinTime);
        }
        return -1;
    }

    public int getPredictionTime(int station){
        return getPredictionTime(station,0);
    }
    /**
     *日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    public boolean checkDoubleDay(){
        int time=getDepartureTime(startStation());
        for(int i=startStation();i<endStation();i++){
            if(timeExist(i)){
                if(getDepartureTime(i)-time<-12*60*60||getDepartureTime(i)-time>12*60*60){
                    SdLog.log("doubleDay");
                    return true;
                }
                time=getDepartureTime(i);
            }
        }
        return false;
    }

    public void endTrain(int station){
        setDepartureTime(station,-1);
        if(direction==0){
            for(int i=station+1;i<stationNum;i++){
                time[i]=0;
            }
        }else{
            for(int i=station-1;i>=0;i--){
                time[i]=0;
            }
        }
    }
    public void startTrain(int station){
        setArrivalTime(station,-1);
        if(direction==0){
            for(int i=station-1;i>=0;i--){
                time[i]=0;
            }
        }else{
            for(int i=station+1;i<stationNum;i++){
                time[i]=0;
            }
        }
    }
    public void combine(Train train,int station){
        setDepartureTime(station,train.getDepartureTime(station));
        if(direction==0){
            for(int i=station+1;i<stationNum;i++){
                time[i]=train.time[i];
            }
        }else{
            for(int i=station-1;i>=0;i--){
                time[i]=train.time[i];
            }
        }

    }

    public void saveToFile(FileWriter out){
        try{
            out.write("Ressya.\r\n");
            if(direction==0){
                out.write("Houkou=Kudari\r\n");
            }else{
                out.write("Houkou=Nobori\r\n");
            }
            out.write("Syubetsu="+type+"\r\n");
            if(number.length()>0){
                out.write("Ressyabangou="+number+"\r\n");
            }
            if(name.length()>0){
                out.write("Ressyamei="+name+"\r\n");
            }
            if(count.length()>0){
                out.write("Gousuu="+count+"\r\n");
            }
            out.write("EkiJikoku="+getEkijikokuOudia()+"\r\n");
            out.write("RessyaTrack="+getTrackOudia()+"\r\n");
            if(remark.length()>0){
                out.write("Bikou="+remark+"\r\n");
            }
            out.write(".\r\n");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String getEkijikokuOudia(){
        StringBuilder result=new StringBuilder("");
        for(int i=0;i<time.length;i++){

            int station=i*(1-direction*2)+direction*(time.length-1);
            if(getStopType(station)==0){
                result.append(",");
                continue;
            }
            result.append(getStopType(station));
            if(arriveExist(station)||departExist(station)) {
                result.append(";");
            }
            if(arriveExist(station)){
                result.append(timeIntToString(getArrivalTime(station)));
                result.append("/");
            }
            if(departExist(station)){
                result.append(timeIntToString(getDepartureTime(station)));
            }
            if(i!=time.length-1) {
                result.append(",");
            }
        }
        return result.toString();
    }
    private String getTrackOudia(){
        StringBuilder result=new StringBuilder("");
        for(int i=0;i<time.length;i++){
            int station=i*(1-direction*2)+direction*(time.length-1);
            result.append(getStop(station));
            if(station==startStation()&&leaveYard){
                    result.append(";2/");
                result.append(operationName);
            }
            if(station==endStation()&&goYard){
                result.append(";2");
            }
            if(i!=time.length-1) {
                result.append(",");
            }
        }
        return result.toString();

    }
    public void editStationSubmit(ArrayList<Integer> editStation){
        long[] newTime=new long[editStation.size()];
        for(int i=0;i<editStation.size();i++){
            if(editStation.get(i)<0){
                newTime[i]=0;
                if(i>0){
                    switch ((int)((newTime[i-1]&0x0F00000000000000L)>>>56)){
                        case 0:
                            newTime[i]=0x0000000000000000L;
                            break;
                        case 3:
                            newTime[i]=0x0300000000000000L;
                            break;
                            default:
                                newTime[i]=0x0200000000000000L;

                    }
                    newTime[i]=newTime[i-1]&0x0F00000000000000L;
                }
            }else{
                newTime[i]=time[editStation.get(i)];
            }
        }
        if(!editStation.contains(endStation())){
            goYard=false;
        }
        if(!editStation.contains(startStation())){
            leaveYard=false;
        }

        time=newTime;
        stationNum=time.length;
    }
    public boolean isnull(){
        for(int i=0;i<time.length;i++)
        {
            if(time[i]!=0)return false;
        }
        return true;
    }



}
