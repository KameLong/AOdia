package com.kamelong.OuDia2nd


import com.kamelong.aodia.diadata.AOdiaStation
import java.util.ArrayList

/**
 *
 */
class Station :AOdiaStation{
    override val stopNum: Int
        get() =trackName.size

    override fun getStopName(index: Int):String {
        return trackName[index]
    }

    override fun setStopName(index: Int, value: String) {
        trackName[index]=value
    }

    override fun getShortName(index: Int):String {
        return trackRyakusyou[index]
    }

    override fun setShortName(index: Int, value: String) {
        trackRyakusyou[index]=value
    }

    override fun addStop(index: Int) {
        trackName.add(index,"")
        trackRyakusyou.add(index,"")
    }

    override fun deleteStop(index: Int): Boolean {
        trackName.removeAt(index)
        trackRyakusyou.removeAt(index)
        return true
    }


    /**
     * 駅名。
     */
    override var name = ""
        set(value) {
            if (value.length > 0) {
                field = value
            }
        }
    /**
     * 発時刻、着時刻、時刻表在線、列車番号列車種別の表示非表示を管理する整数。
     * 8bitで記述し
     * 列車種別、上り在線、上り着、上り発、、列車種別、下り在線、下り着、下り発
     * の順でバイナリ記述する。
     * この形を用いることであらゆるパターンの表示を可能とするであろう
     *
     *
     * SHOW_XXXの形の定数はよく使われる発着表示のパターンを定数にしたもの
     */
    var showType = SHOW_HATU
    private set
    override fun getTimeViewStyle(): Int {
        return showType and 0b00110011
    }

    override fun setTimeViewStyle(value: Int) {
        val m_value=value and 0b00110011
        showType = showType and 0b11001100
        showType = showType or m_value
    }

    override fun getStopStyle(): Int {
        return showType and 0b01000100
    }

    override fun setStopStyle(value: Int) {
        val m_value=value and 0b01000100
        showType = showType and 0b10111011
        showType = showType or m_value
    }


    /**
     * 駅規模。
     */
    override var bigStation=false


    /**
     * 分岐起点駅を示す。
     */
    override var branchStation=-1
    /**
     * 環状線起点駅を示す
     */
    override var loopStation=-1



    /**
     * OuDiaSecond
     */
    var trackName = ArrayList<String>()
    var trackRyakusyou = ArrayList<String>()
    override var downMain = 0
    override var upMain = 0
    var showStopDiagram=false
    override fun getStopDiaStyle(): Boolean {
        return showStopDiagram
    }

    override fun setStopDiaStyle(value: Boolean) {
        showStopDiagram=value
    }

    fun makeStationText(oudiaSecond: Boolean): StringBuilder {
        val result = StringBuilder("Eki.")
        result.append("\r\nEkimei=").append(name)
        when (this.showType) {
            SHOW_HATU -> result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu")
            SHOW_HATUTYAKU -> result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsuchaku")
            SHOW_KUDARITYAKU -> result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_KudariChaku")
            SHOW_NOBORITYAKU -> result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_NoboriChaku")
            SHOW_KUDARIHATUTYAKU -> if (oudiaSecond) {
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_KudariHatsuchaku")
            } else {
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu")
            }
            SHOW_NOBORIHATUTYAKU -> if (oudiaSecond) {
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_NoboriHatsuchaku")
            } else {
                result.append("\r\nEkijikokukeisiki=").append("Jikokukeisiki_Hatsu")
            }
        }
        if(bigStation) {
            result.append("\r\nEkikibo=").append("Ekikibo_Syuyou")
        }else{
            result.append("\r\nEkikibo=").append("Ekikibo_Ippan")
        }

        result.append("\r\n.\r\n")
        return result

    }



    /**
     * timeShowをセットする。
     * @param value timeShowを表す整数　
     */
    protected fun setTimeShow(value: Int) {
        val v=value and 0x33
        showType=showType and 0xCC
        showType=showType or v
    }


    /**
     * OuDiaのEkikiboの文字列から駅規模を入力する。
     *
     * @param value OuDiaファイル内のEkikiboの文字列
     */
    internal fun setSize(value: String) {
        when (value) {
            "Ekikibo_Ippan" -> bigStation=false
            "Ekikibo_Syuyou" -> bigStation=true
        }
    }

    /**
     * OuDiaのJikokukeisikiの文字列から時刻表示形式を入力する。
     *
     * @param value OuDiaファイル内のJikokukeisikiの文字列
     */
    internal fun setStationTimeShow(value: String) {
        when (value) {
            "Jikokukeisiki_Hatsu" -> setTimeShow(SHOW_HATU)
            "Jikokukeisiki_Hatsuchaku" -> setTimeShow(SHOW_HATUTYAKU)
            "Jikokukeisiki_NoboriChaku" -> setTimeShow(SHOW_NOBORITYAKU)
            "Jikokukeisiki_KudariChaku" -> setTimeShow(SHOW_KUDARITYAKU)
            "Jikokukeisiki_KudariHatsuchaku" -> setTimeShow(SHOW_KUDARIHATUTYAKU)
            "Jikokukeisiki_NoboriHatsuchaku" -> setTimeShow(SHOW_NOBORIHATUTYAKU)
        }


    }

    /**
     * 発着表示を表す整数を返す。
     * 方向のみ指定し、発着両方の情報を返す。
     * @param direct 取得したい方向（上り(=1)か下り(=0)か）
     * @return 着時刻を表示するとき+2,発時刻を表示するとき+1
     */
    fun getTimeShow(direct: Int): Int {
        when (direct) {
            0 -> return showType % 16
            1 -> return showType / 16 % 16
            else -> return 0
        }
    }

    override fun clone(): AOdiaStation {
        val station=Station()
        station.name=name
        station.showType=showType
        station.bigStation=bigStation
        station.branchStation=branchStation
        station.loopStation=loopStation
        station.trackName=ArrayList(trackName)
        station.trackRyakusyou =ArrayList(trackRyakusyou)
        station.downMain=downMain
        station.upMain=upMain
        station.showStopDiagram=showStopDiagram
        
        return station
    }


    companion object {

        val SHOW_HATU = 0x11
        val SHOW_HATUTYAKU = 0x33
        val SHOW_KUDARITYAKU = 0x12
        val SHOW_NOBORITYAKU = 0x21
        val SHOW_KUDARIHATUTYAKU = 0x13
        val SHOW_NOBORIHATUTYAKU = 0x31
        /**
         * 発着表示を取得する際に使う定数
         */
        protected val STOP_DEPART = 0
        protected val STOP_ARRIVE = 1
    }

}