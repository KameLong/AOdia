package com.kamelong.aodia.diadata;

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

import com.kamelong.JPTIOuDia.OuDia.OuDiaStation;

/**
 *
 * 駅データを格納するクラス。
 * 一つの駅に関するデータはここに格納する
 * Stationクラスには全種類のダイヤ形式で統一できる入力と、出力を書く。
 * それぞれのダイヤ形式に合わせた変換はxxxDiaFileクラスに記述する
 * @author  KameLong
 */
public class AOdiaStation extends OuDiaStation{

    public void setName(String value){
        if(value.length()>0){
            name=value;
        }
    }

    public String getName(){
        return name;
    }

    /**
     * 駅名の略称として、最初の5文字のみ表示する機能を用いる際に使う
     * @return
     */
    public String getShortName(){
        if(name.length()>5){
            return name.substring(0,5);
        }
        return name;
    }

    /**
     * timeShowをセットする。
     * @param value timeShowを表す整数　0<=value<16
     */
    public void setTimeShow(int value){
        if(value>0&&value<16){
            timeShow=value;
            return;
        }
        //error
    }

    /**
     * 境界駅をセットする。
     * @param value 境界駅の場合1、境界駅ではない場合0
     */
    public void setBorder(int value){
        border=value;
    }

    /**
     * 境界駅かどうかを返す。
     * @return 境界駅の場合true,そうではないときfalse
     */
    public boolean border(){
        return border==1;
    }

    /**
     * 発着表示を表す整数を返す。
     * 方向のみ指定し、発着両方の情報を返す。
     * @param direct 取得したい方向（上り(=1)か下り(=0)か）
     * @return 着時刻を表示するとき+2,発時刻を表示するとき+1
     */
    public int getTimeShow(int direct){
        switch(direct){
            case 0:
                return timeShow%4;
            case 1:
                return (timeShow/4)%4;
            default:
                return 0;
        }
    }

    /**
     * 発着表示をするかどうかを返す
     * @param pos 発、着どちらの情報を取得したいか　STOP_ARRIVE,STOP_DEPARTから選択
     * @param direct 取得したい方向（上り(=1)か下り(=0)か）
     * @return 時刻を表示するときはtrueそうでないときはfalse
     */
    public boolean getTimeShow(int pos,int direct){
        if(pos==STOP_ARRIVE){
            switch (direct){
                case 0:
                    return (timeShow&0x00000002)!=0;
                case 1:
                    return (timeShow&0x00000008)!=0;
                default:
                    return false;
            }
        }
        if(pos==STOP_DEPART){
            switch (direct){
                case 0:
                    return (timeShow&0x00000001)!=0;
                case 1:
                    return (timeShow&0x00000004)!=0;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * 駅規模を入力する。
     * @param value SIZE_NORMALかSIZE_BIG
     */
    public void setSize(int value){
        if(value<2&&value>0) {
            size = value;
        }
    }

    /**
     * 駅規模を数値で取得する。
     * @return
     */
    public int getSize(){
        return size;
    }

    /**
     * 駅規模が主要駅かどうかを返す。
     * @return
     */
    public boolean getBigStation(){
        return size!=0;
    }


}
