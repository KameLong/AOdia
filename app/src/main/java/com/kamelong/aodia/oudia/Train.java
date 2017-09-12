package com.kamelong.aodia.oudia;

import android.util.Log;
import com.kamelong.aodia.SdLog;
import org.json.JSONArray;
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
 * 列車データを格納するクラス。
 * 一つの列車に関するデータはここに格納する
 * Stationクラスには全種類のダイヤ形式で統一できる入力と、出力を書く。
 * それぞれのダイヤ形式に合わせた変換はxxxDiaFileクラスに記述する
 * @author  KameLong
 */
public class Train {
    /**
     * 駅扱いの定数。long timeの9~12bitがstop typeに対応する。
     */
    public static final int STOP_TYPE_STOP=1;
    public static final int STOP_TYPE_PASS=2;
    public static final int STOP_TYPE_NOSERVICE=0;
    public static final int STOP_TYPE_NOVIA=3;
    /**
     * 列車種別
     */
    private int type = 0;
    /**
     * 列車番号
     */
    private String number ="";
    /**
     * 列車名
     */
    private String name="";
    /**
     * 号数
     */
    private String count="";
    /**
     * 備考
     */
    private String remark="";//備考
    /**
     * １列車の駅依存の情報を格納する。
     * このデータは駅数分できるため、サイズの大きいオブジェクトはメモリを圧迫します。
     * 省メモリのため文字列などを用いず、すべてlongで表記します。
     * longは64bitなので、各ビットごとに役割を持たせたいます。
     * 先頭より　
     * 4bit フラグエリア：どの情報が存在するのかを示したもの（1:存在する,0:存在しない)
     *       [free,free,着時刻の存在,発時刻の存在,free,free,free,free]
     * 4bit 駅扱いを記述する。この4bitの値がそのままstopTypeとなる
     * 8bit 空き領域(free)
     * 24bit 着時刻（秒単位）
     * 24bit 発時刻（秒単位）
     */

    private long time[];
    /**
     * この列車が所属するDiaFile
     */
    private DiaFile diaFile;
    /**
     * 日付をまたぐ列車はtrueになります.
     */
    public boolean doubleDay=false;

    /**
     * 列車の生成には所属するDiaFileが必要となります。
     * @param dia　呼び出し元のDiaFile
     */
    public Train(DiaFile dia) {
        diaFile=dia;
        try {
            time = new long[diaFile.getStationNum()];
            for (int i = 0; i < time.length; i++) {
                time[i] = 0;
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    /**
     * 列車番号を設定します
     * @param value　列車番号
     */
    public void setNumber(String value) {
        try {
            if (value.length() == 0) {
                number = "";
                return;
            }
            number = value;
        }catch(Exception e){
            SdLog.log(e);
        }
        return;
    }
    /**
     * @return　列車番号
     */
    public String getNumber() {
        return number;
    }

    /**
     * 列車種別を設定します
     * @param value　列車種別(int)
     */
    public void setType(int value) {
        if (value < 0) {
            //列車種別は負の数が許されていません
            type = 0;
            return;
        }
        type = value;
    }

    /**
     * 列車種別を取得します
     * @return 列車種別(整数)
     */
    public int getType(){
        if(type>diaFile.getTypeNum()){
            //DiaFileにある列車種別より大きい値は返すことができない。
            return 0;
        }
        return type;
    }

    /**
     * 指定駅の着時刻を取得します。
     * データは秒単位のintで返ります。
     * 着時刻が存在しないとき発時刻を返します。
     * 発時刻も存在しないときは-1を返します。
     * 何らかのエラーが生じた際は-2を返します。
     * @param station　指定駅番号　null禁止
     * @return　着時刻(秒)
     */

    public int getArriveTime(int station){
        try {
            if ((time[station] & 0x2000000000000000L) == 0) {
                if ((time[station] & 0x1000000000000000L) == 0) {
                    return -1;
                }
                return getDepartureTime(station);
            }
            long result = time[station] & 0x0000ffffff000000L;
            result = result >>> 24;
            return (int) result;
        }catch(Exception e){
            SdLog.log(e);
        }
        return -2;
    }
    /**
     * 指定駅の発時刻を取得します。
     * データは秒単位のintで返ります。
     * 発時刻が存在しないとき着時刻を返します。
     * 着時刻も存在しないときは-1を返します。
     * 何らかのエラーが生じた際は-2を返します。
     * @param station　指定駅番号　null禁止
     * @return　発時刻(秒)
     */
    public int getDepartureTime(int station){
        try {
            if ((time[station] & 0x1000000000000000L) == 0) {
                if ((time[station] & 0x2000000000000000L) == 0) {
                    return -1;
                }
                return getArriveTime(station);
            }
            long result = time[station] & 0x00000000ffffffL;
            return (int) result;
        }catch(Exception e){
            SdLog.log(e);
        }
        return -2;
    }

    /**
     * 指定駅の着時刻を文字列で返します。
     * 指定駅の駅種別に応じて返す文字列も異なりますが。
     * AOdia v0.9以降では時刻表に表示する時刻文字列はTrainTimeViewにて定義していますので
     * 現在は使われていません。
     * @param station 指定駅
     * @param direct 方向
     * @return 着時刻文字列
     */
    public String getArriveTime(int station,int direct){
        switch(getStopType(station)){
            case STOP_TYPE_NOSERVICE:
                return "   : :";
            case STOP_TYPE_NOVIA:
                return "   | |";
            case STOP_TYPE_PASS:
                return "   レ";
        }
        if((time[station]&0x2000000000000000L)==0){
            if((time[station]&0x1000000000000000L)==0) {
                return "null";
            }
            return getDepartureTime(station,direct);
        }
        int second=getArriveTime(station);
        int ss=second%60;
        second=(second-ss)/60;
        int mm=second%60;
        second=(second-ss)/60;
        int hh=second%60;
        hh=hh%24;
        String time="";
        time=String.format("%2d",hh)+String.format("%02d",mm);
        return time;
    }
    /**
     * 指定駅の発時刻を文字列で返します。
     * 指定駅の駅種別に応じて返す文字列も異なりますが。
     * AOdia v0.9以降では時刻表に表示する時刻文字列はTrainTimeViewにて定義していますので
     * 現在は使われていません。
     * @param station 指定駅
     * @param direct 方向
     * @return 発時刻文字列
     */

    public String getDepartureTime(int station,int direct){
        try {
            switch(getStopType(station)){
                case STOP_TYPE_NOSERVICE:
                    return "   : :";
                case STOP_TYPE_NOVIA:
                    return "   | |";
                case STOP_TYPE_PASS:
                    return "   レ";
            }
            if ((time[station] & 0x1000000000000000L) == 0) {
                if ((time[station] & 0x2000000000000000L) == 0) {
                    return "null";
                }
                return getArriveTime(station, direct);
            }
            int second=getDepartureTime(station);
            int ss=second%60;
            second=(second-ss)/60;
            int mm=second%60;
            second=(second-ss)/60;
            int hh=second%60;
            hh=hh%24;
            String time = "";
            time = String.format("%2d", hh) +  String.format("%02d",mm);
            return time;
        }catch(Exception e){
            SdLog.log(e);
        }
        return "null";

    }

    /**
     * 終着駅を返します。
     * directの方向で考えて、発着時刻が存在する最後の駅を返します
     * @param direct　方向
     * @return　駅インデックス
     */
    public int getEndStation(int direct){
        for(int i=(1-direct)*(time.length-1);i*(1-2*direct)>0-direct*time.length;i=i+(2*direct-1)){
            if(timeExist(i)){
                return i;
            }
        }
        return direct*(time.length-1);
    }

    /**
     * 始発駅を返します
     * directの方向で考えて、発着時刻が存在する最後の駅を返します。
     * @param direct　方向
     * @return　駅インデックス
     */
    public int getStartStation(int direct){
        for(int i=direct*(time.length-1);i*(1-2*direct)<(1-direct)*(time.length);i=i+(1-2*direct)){
            if(timeExist(i)){
                return i;
            }
        }
        return (1-direct)*(time.length-1);
    }

    /**
     * この列車の発着時刻を入力します。
     * netgramのJson形式の文字列を発着時刻に変換し、入力していきます。
     * @param timeArray　netgramのJSON形式の時刻
     * @param direct　方向
     */
/*
    public void setTime(JSONArray timeArray, int direct){
        try {
            for (int i = 0 ;i < time.length; i++) {
                int station = (1 - 2 * direct) * i + direct * (time.length - 1);


                if(timeArray.getJSONArray(i).getString(0).length()>0){
                    setStopType(station, timeArray.getJSONArray(i).getString(0));
                }
                if( timeArray.getJSONArray(i).getString(2).length()>0){
                    setArriveTime(station,timeArray.getJSONArray(i).getString(2).replace(":",""));
                }
                if(timeArray.getJSONArray(i).getString(3).length()>0){
                    setDepartTime(station,timeArray.getJSONArray(i).getString(3).replace(":",""));
                }

            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }
*/

    /**
     * 駅扱いをセットする。
     * @param station　駅インデックス
     * @param value　停車駅扱い番号（番号を文字列にしたもの）
     */
    /*
    public void setStopType(int station,String value){
        try{
            setStopType(station,Integer.parseInt(value));
        }catch(Exception e){
            setStopType(station,0);
        }
    }
    */
    /**
     * 駅扱いをセットする。
     * @param station　駅インデックス
     * @param value　停車駅扱い番号
     */

    public void setStopType(int station,int value){
        if(value>16||value<0){
            //error
            return;
        }
        if(value==8||value==9){
            value=STOP_TYPE_STOP;
        }
        long type=(long)value;
        type=type<<56;
        time[station]=time[station]&0xF0FFFFFFFFFFFFFFL;
        time[station]=time[station]|type;
    }

    /**
     * 着時刻をセットする。
     * 0:00,10:34,10:3420,000,1034,103420などの形式に対応。
     * 文字列を秒単位に変換してtime[]にセットします
     * @param station　駅インデックス
     * @param str 着時刻を文字列にしたもの
     */
    public void setArriveTime(int station,String str){
        long result=0;//minutes
        int h;
        int m;
        int s;
        if(str.length()==0){
            return;
        }
        if(str.equals("null")){
            return;
        }
        if(str.indexOf(":")<0) {
            //no ":" char so str is only number
            switch (str.length()) {
                case 3:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(1, 3));
                    result = h * 3600 + m*60;
                    break;
                case 4:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    result = h * 3600 + m*60;
                    break;
                case 5:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(1, 3));
                    s = Integer.parseInt(str.substring(3, 5));
                    result = h * 3600 + m*60+s;
                    break;
                case 6:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    s = Integer.parseInt(str.substring(4, 6));
                    result = h * 3600 + m*60+s;
                    break;
                default:
                    result = -1;

            }
        }else{
            //this str inclues ":" for example 12:17
            switch (str.length()) {
                case 4:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    result = h * 3600 + m*60;
                    break;
                case 5:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(3, 5));
                    result = h * 3600 + m*60;
                    break;
                case 6:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    s = Integer.parseInt(str.substring(4, 6));
                    result = h * 3600 + m*60+s;
                    break;
                case 7:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(3, 5));
                    s = Integer.parseInt(str.substring(5, 7));
                    result = h * 3600 + m*60+s;
                    break;
                default:
                    result = -1;

            }
        }
        if(result>0) {
            time[station] = time[station] & 0xDFFF000000FFFFFFL;
            time[station] = time[station] | 0x2000000000000000L;
            time[station] = time[station] | (result<<24);
        }
    }
    /**
     * 発時刻をセットする。
     * 0:00,10:34,10:3420,000,1034,103420などの形式に対応。
     * 文字列を秒単位に変換してtime[]にセットします
     * @param station　駅インデックス
     * @param str 発時刻を文字列にしたもの
     */

    public void setDepartTime(int station,String str){
        long result=0;//minutes
        int h;
        int m;
        int s;
        if(str.length()==0){
            return;
        }
        if(str.equals("null")){
            return;
        }

        if(str.indexOf(":")<0) {
            //no ":" char so str is only number
            switch (str.length()) {
                case 3:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(1, 3));
                    result = h * 3600 + m*60;
                    break;
                case 4:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    result = h * 3600 + m*60;
                    break;
                case 5:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(1, 3));
                    s = Integer.parseInt(str.substring(3, 5));

                    result = h * 3600 + m*60+s;
                    break;
                case 6:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    s = Integer.parseInt(str.substring(4, 6));

                    result = h * 3600 + m*60+s;
                    break;
                default:
                    result = -1;

            }
        }else{
            //this str inclues ":" for example 12:17
            switch (str.length()) {
                case 4:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    result = h * 3600 + m*60;
                    break;
                case 5:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(3, 5));
                    result = h * 3600 + m*60;
                    break;
                case 6:
                    h = Integer.parseInt(str.substring(0, 1));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(2, 4));
                    s = Integer.parseInt(str.substring(4, 6));
                    result = h * 3600 + m*60+s;
                    break;
                case 7:
                    h = Integer.parseInt(str.substring(0, 2));
                    if (h < 3) {
                        h = h + 24;
                    }
                    m = Integer.parseInt(str.substring(3, 5));
                    s = Integer.parseInt(str.substring(4, 6));
                    result = h * 3600 + m*60+s;
                    break;
                default:
                    result = -1;

            }
        }
        if(result>0) {
            time[station] = time[station] & 0xEFFFFFFFFF000000L;
            time[station] = time[station] | 0x1000000000000000L;
            time[station] = time[station] | (result);
        }
    }
    public int getStopType(int station) {
        if(station<0||station>=time.length){
            return 5;
        }
        long result = time[station] & 0x0F00000000000000L;
        result = result >>> 56;
        if (result < 4) {
            return (int) result;
        } else {
            return 5;
        }
    }
    public int getRequiredTime(int startStation,int endStation){
        if((time[startStation]&0x3000000000000000L)==0||(time[endStation]&0x3000000000000000L)==0){
            return -1;
        }
        try {
            int startDeparture=getDepartureTime(startStation);
            if(startDeparture<0){
                startDeparture=getArriveTime(startStation);
            }
            int endArrive=getArriveTime(endStation);
            if(endArrive<0){
                endArrive=getDepartureTime(endStation);
            }
            if ( startDeparture < endArrive) {
                return endArrive - startDeparture;
            } else {
                return startDeparture - endArrive;
            }
        }catch(Exception e){
            SdLog.log(e);
            return -1;
        }
    }

    /**
     * 着時刻が存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　着時刻存在フラグ
     */
    public boolean arriveExist(int station){
        if(station<0||station>=time.length){
            return false;
        }
            return (time[station] & 0x2000000000000000L) != 0;
    }
    /**
     * 発時刻が存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　発時刻存在フラグ
     */
    public boolean departExist(int station) {
        if(station<0||station>=time.length){
            return false;
        }
        return (time[station] & 0x1000000000000000L) != 0;
    }
    /**
     * 時刻の存在。
     * 発時刻と着時刻のどちらかが存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　時刻存在フラグ
     */

    public boolean timeExist(int station) {
        if(station<0||station>=time.length){
            return false;
        }
        return (time[station] & 0x3000000000000000L) != 0;
    }

    /**
     * 列車名をセットします。
     * @param value　列車名文字列
     */
    public void setName(String value){
        name=value;
    }

    /**
     * 列車名を取得します
     * @return　列車名
     */
    public String getName(){
        return name;
    }

    /**
     * 号数をセットします
     * @param value　号数文字列
     */
    public void setCount(String value){
        if(value.length()>0){
            count=value+"号";
        }
    }

    /**
     * 号数を取得します
     * @return　号数の文字列
     */
    public String getCount(){
        return count;
    }

    /**
     * 備考を取得します
     * @return　備考の内容
     */
    public String getRemark(){return remark;}

    /**
     * 備考をセットします
     * @param value　備考文字列
     */
    public void setRemark(String value){
        remark=value;
    }

    /**
     * 対象駅を通過する列車において、通過時刻を予想してその秒を返します。
     * 対象駅から前方と後方に向けて駅時刻が存在する駅がないかを調べます。
     * DiaFileの最小所要時間を用いて駅間所要時間を推定し、実際の走行時間と駅間所要時間が比例するものと考え、
     * 通過時刻を算出します。なお、算出途中に経由なし運行なしの駅が現れた場合はそれらの駅の駅間所要時間は０とみなします
     * @param station　駅インデックス
     * @return 予測通過時刻
     */
    public static int ARRIVE=1;
    public static int DEPART=0;
    public int getPredictionTime(int station,int AD){
        if(AD==ARRIVE&&arriveExist(station)){
            return getArriveTime(station);
        }
        if(timeExist(station)){
            return getDepartureTime(station);
        }
        if(getStopType(station)==STOP_TYPE_PASS){
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
                Log.d("予測時間","afterTime");
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
        return getPredictionTime(station,DEPART);
    }
    /**
     *日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    public boolean checkDoubleDay(){
        int time=getDepartureTime(getStartStation(0));
        for(int i=getStartStation(0);i<getEndStation(0);i++){
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

    /**
     * trainより前の列車ならtrueを返す
     * 未使用＆未実装
     * @param train　比較対象列車
     * @param station 駅インデックス
     * @return
     */
    public boolean beforeTrain(Train train,int station){
        return true;
    }

}
