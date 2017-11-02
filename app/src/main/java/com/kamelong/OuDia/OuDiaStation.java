package com.kamelong.OuDia;

import com.kamelong.JPTI.RouteStation;

/**
 *
 */
public class OuDiaStation {
    /**
     * 駅名。
     */
    protected String name = "";
    /**
     * 発時刻、着時刻の表示非表示を管理する整数。
     * 4bitで記述し
     * 上り着、上り発、下り着、下り発
     * の順でバイナリ記述する。
     * この形を用いることであらゆるパターンの表示を可能とするであろう
     * <p>
     * SHOW_XXXの形の定数はよく使われる発着表示のパターンを定数にしたもの
     */
    protected int timeShow = SHOW_HATU;

    protected static final int SHOW_HATU = 5;
    protected static final int SHOW_HATUTYAKU = 15;
    protected static final int SHOW_KUDARITYAKU = 6;
    protected static final int SHOW_NOBORITYAKU = 9;
    protected static final int SHOW_KUDARIHATUTYAKU = 7;
    protected static final int SHOW_NOBORIHATUTYAKU = 13;


    /**
     * 駅規模。
     */
    protected int size = SIZE_NORMAL;
    private static final int SIZE_NORMAL = 0;
    private static final int SIZE_BIG = 1;

    /**
     * 境界駅を示す。
     * 境界駅の場合1、境界駅ではない場合0が入る。
     */
    protected int border;

    /**
     * 発着表示を取得する際に使う定数
     */
    protected static final int STOP_DEPART = 0;
    protected static final int STOP_ARRIVE = 1;


    public StringBuilder makeStationText(boolean oudiaSecond) {
        StringBuilder result = new StringBuilder("Eki.");
        result.append("\r\nEkimei=").append(name);
        switch (this.timeShow) {
            case SHOW_HATU:
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu");
                break;
            case SHOW_HATUTYAKU:
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsuchaku");
                break;
            case SHOW_KUDARITYAKU:
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_KudariChaku");
                break;
            case SHOW_NOBORITYAKU:
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_NoboriChaku");
                break;
            case SHOW_KUDARIHATUTYAKU:
                if (oudiaSecond) {
                    result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_KudariHatsuchaku");
                } else {
                    result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu");
                }
                break;
            case SHOW_NOBORIHATUTYAKU:
                if (oudiaSecond) {
                    result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_NoboriHatsuchaku");
                } else {
                    result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu");
                }
                break;
        }
        switch (size) {
            case 0:
                result.append("\r\nEkikibo=").append("Ekikibo_Ippan");
                break;
            case 1:
                result.append("\r\nEkikibo=").append("Ekikibo_Syuyou");
                break;
        }

        if (border == 1) {
            result.append("\r\nKyoukaisen=1");
        }
        result.append("\r\n.\r\n");
        return result;

    }

    protected void setName(String value) {
        if (value.length() > 0) {
            name = value;
        }
    }


    /**
     * timeShowをセットする。
     *
     * @param value timeShowを表す整数　0<=value<16
     */
    protected void setTimeShow(int value) {
        if (value > 0 && value < 16) {
            timeShow = value;
            return;
        }
        //error
    }

    /**
     * 境界駅をセットする。
     *
     * @param value 境界駅の場合1、境界駅ではない場合0
     */
    public void setBorder(int value) {
        border = value;
    }

    /**
     * 駅規模を入力する。
     *
     * @param value SIZE_NORMALかSIZE_BIG
     */
    protected void setSize(int value) {
        if (value < 2 && value > 0) {
            size = value;
        }
    }

    /**
     * OuDiaのEkikiboの文字列から駅規模を入力する。
     *
     * @param value OuDiaファイル内のEkikiboの文字列
     */
    void setSize(String value) {
        switch (value) {
            case "Ekikibo_Ippan":
                setSize(OuDiaStation.SIZE_NORMAL);
                break;
            case "Ekikibo_Syuyou":
                setSize(OuDiaStation.SIZE_BIG);
                break;
        }
    }

    /**
     * OuDiaのJikokukeisikiの文字列から時刻表示形式を入力する。
     *
     * @param value OuDiaファイル内のJikokukeisikiの文字列
     */
    void setStationTimeShow(String value) {
        switch (value) {
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
            case "Jikokukeisiki_KudariHatsuchaku":
                setTimeShow(SHOW_KUDARIHATUTYAKU);
                break;
            case "Jikokukeisiki_NoboriHatsuchaku":
                setTimeShow(SHOW_NOBORIHATUTYAKU);
                break;

        }


    }
    public OuDiaStation(){
        super();
    }
    public OuDiaStation(RouteStation routeStation){
        this.name=routeStation.getStation().getName();
        if(routeStation.isBigStation()){
            this.size=1;
        }
        switch (routeStation.getViewStyle()){
            case RouteStation.VIEWSTYLE_HATU:
                timeShow=SHOW_HATU;
                break;
            case RouteStation.VIEWSTYLE_HATUTYAKU:
                timeShow=SHOW_HATUTYAKU;
                break;
            case RouteStation.VIEWSTYLE_KUDARITYAKU:
                timeShow=SHOW_KUDARITYAKU;
                break;
            case RouteStation.VIEWSTYLE_NOBORITYAKU:
                timeShow=SHOW_NOBORITYAKU;
                break;
            case RouteStation.VIEWSTYLE_NOBORIHATUTYAKU:
                timeShow=SHOW_NOBORIHATUTYAKU;
                break;
            case RouteStation.VIEWSTYLE_KUDARIHATUTYAKU:
                timeShow=SHOW_KUDARIHATUTYAKU;
                break;
        }

    }
    public boolean border(){
        return border==1;
    }
    public String getName(){
        return name;
    }
    public boolean getBigStation(){
        return size==1;
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
    public void setBorder(boolean value){
        if(value) {
            this.border = 1;
        }else{
            this.border=0;
        }
    }

}