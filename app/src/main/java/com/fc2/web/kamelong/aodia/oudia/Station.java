package com.fc2.web.kamelong.aodia.oudia;

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
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */

/**
 * @author  KameLong
 *
 * 駅データを格納するクラス。
 * 一つの駅に関するデータはここに格納する
 */
public class Station {
    /**
     * 駅名。
     */
    private String name="";
    /**
     * 発時刻、着時刻の表示非表示を管理する整数。
     * 4bitで記述し
     * 上り着、上り発、下り着、下り着
     * の順でバイナリ記述する。
     * この形を用いることであらゆるパターンの表示を可能とするであろう
     *
     * SHOW_XXXの形の定数はよく使われる発着表示のパターンを定数にしたもの
     */
    private int timeShow=SHOW_HATU;

    static final int SHOW_HATU=5;
    static final int SHOW_HATUTYAKU=15;
    static final int SHOW_KUDARITYAKU=6;
    static final int SHOW_NOBORITYAKU=9;

    /**
     * 駅規模。
     */
    private int size=SIZE_NORMAL;
    static final int SIZE_NORMAL=0;
    static final int SIZE_BIG=1;

    /**
     * 境界駅を示す。
     * 境界駅の場合1、境界駅ではない場合0が入る。
     */
    private int border;

    /**
     * 発着表示を取得する際に使う定数
     */
    public static final int STOP_DEPART=0;
    public static final int STOP_ARRIVE=1;

    public void setName(String value){
        String[] dameMoji={"\\","―","ソ","Ы","Ⅸ","噂","浬","欺","圭","構.","蚕","十","申","曾","箪","貼","能","表","暴","予","禄","兔","喀","媾","彌","拿","杤","歃","濬","畚","秉","綵","臀","藹","觸","軆","鐔","饅","鷭","偆","砡","纊","犾"};
        for (String moji: dameMoji){
            value=value.replace(moji+"\\",moji);
        }
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

    /**
     * OuDiaのEkikiboの文字列から駅規模を入力する。
     * @param value OuDiaファイル内のEkikiboの文字列
     */
    public void setSize(String value){
        switch (value){
            case "Ekikibo_Ippan":
                setSize(0);
                break;
            case "Ekikibo_Syuyou":
                setSize(1);
                break;

            case "0":
                setSize(0);
                break;
            case "1":
                setSize(1);
                break;
        }
    }

    /**
     * OuDiaのJikokukeisikiの文字列から時刻表示形式を入力する。
     * @param value OuDiaファイル内のJikokukeisikiの文字列
     */
    public void setTimeShow(String value){
        switch (value){
            case "Jikokukeisiki_Hatsu":
                setTimeShow(SHOW_HATU);
                break;
            case "Jikokukeisiki_Hatsuchaku":
                setTimeShow(SHOW_HATUTYAKU);
                break;
            case "Jikokukeisiki_NoboriChaku":
                setTimeShow(SHOW_NOBORITYAKU);
                break;
            case "Jikokukeisiki_KudariChaku":
                setTimeShow(SHOW_KUDARITYAKU);
                break;
            case "0":
                setTimeShow(SHOW_HATU);
                break;
            case "1":
                setTimeShow(SHOW_HATUTYAKU);
                break;
            case "3":
                setTimeShow(SHOW_NOBORITYAKU);
                break;
            case "2":
                setTimeShow(SHOW_KUDARITYAKU);
                break;
        }
    }
}
