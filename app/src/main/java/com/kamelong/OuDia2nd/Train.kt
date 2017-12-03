package com.kamelong.OuDia2nd

import com.kamelong.JPTI.Service
import com.kamelong.JPTI.Station
import com.kamelong.JPTI.TrainType
import com.kamelong.JPTI.Trip

import java.util.ArrayList

/**
 * 列車データを格納するクラス。
 * 一つの列車に関するデータはここに格納する
 * Stationクラスには全種類のダイヤ形式で統一できる入力と、出力を書く。
 * それぞれのダイヤ形式に合わせた変換はxxxDiaFileクラスに記述する
 * @author  KameLong
 */
class Train {

    /**
     * 列車の進行方向
     */
    var direct = -1

    /**
     * 列車種別
     */
    var type = 0
        set(value) {
            if (value < 0||value>=diaFile.trainTypeNum) {
                field = 0
                return
            }
            field = value
        }
    /**
     * 列車番号
     */
    var number = ""
        set(value) {
            if (value.isEmpty()) {
                field = ""
                return
            }
            field = value

        }
    /**
     * 列車名
     */
    var name = ""
    /**
     * 運用番号
     */
    var operation=""
    /**
     * 号数
     */
    var count = ""
        set(value) {
            if (value.isNotEmpty()) {
                field = value + "号"
            }
        }
    /**
     * 備考
     */
    var remark = ""
    /**
     * １列車の駅依存の情報を格納する。
     * このデータは駅数分できるため、サイズの大きいオブジェクトはメモリを圧迫します。
     * 省メモリのため文字列などを用いず、すべてlongで表記します。
     * longは64bitなので、各ビットごとに役割を持たせたいます。
     * 先頭より
     * 12bit フラグエリア：どの情報が存在するのかを示したもの（1:存在する,0:存在しない)
     * [free,番線の存在,着時刻の存在,発時刻の存在,free,free,free,free,free,free,free,free]
     * 4bit 駅扱いを記述する。この4bitの値がそのままstopTypeとなる
     * 8bit 番線情報(駅の番線index)
     * 20bit 着時刻（秒単位）
     * 20bit 発時刻（秒単位）
     */
    protected var time= ArrayList<Long>()


    protected var startTime=0
    protected var endTime=0
    /**
     * この列車が所属するDiaFile
     */
    lateinit var diaFile: DiaFile
        private set

    private val stationNum: Int
        get() = time.size

    protected constructor() {}
    /**
     * 列車の生成には所属するDiaFileが必要となります。
     * @param diaFile　呼び出し元のDiaFile
     */
    constructor(diaFile: DiaFile) {
        this.diaFile = diaFile
        try {
            for (i in 0 until diaFile.stationNum) {
                time.add(0L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * OuDia保存用の文字列を作成する
     * @param direct
     * @return
     */
    fun makeTrainText(direct: Int): StringBuilder {
        val result = StringBuilder("Ressya.\r\n")
        if (direct == 0) {
            result.append("Houkou=Kudari\r\n")
        } else {
            result.append("Houkou=Nobori\r\n")
        }
        result.append("Syubetsu=").append(type).append("\r\n")
        if (number.length > 0) {
            result.append("Ressyabangou=").append(number).append("\r\n")
        }
        if (name.length > 0) {
            result.append("Ressyamei=").append(name).append("\r\n")
        }
        if (count.length > 0) {
            result.append("Gousuu=").append(name).append("\r\n")
        }
        if (remark.length > 0) {
            result.append("Bikou=").append(name).append("\r\n")
        }
        result.append("EkiJikoku=")
        for (i in 0 until diaFile.stationNum) {
            var stationIndex = i
            if (direct == 1) {
                stationIndex = diaFile.stationNum - 1 - i
            }
            result.append(makeStationTimeTxt(stationIndex)).append(",")
        }
        result.append("\r\n.\r\n")
        return result
    }

    private fun makeStationTimeTxt(stationIndex: Int): String {
        if (getStopType(stationIndex) == 0) {
            return ""
        }
        var result = "" + getStopType(stationIndex)
        if (!timeExist(stationIndex)) {
            return result
        }
        result += ";"
        if (arriveExist(stationIndex)) {
            result += timeInt2String(getArriveTime(stationIndex)) + "/"
        }
        if (departExist(stationIndex)) {

            result += timeInt2String(getDepartureTime(stationIndex))
        }
        return result
    }

    private fun timeInt2String(time: Int): String {
        val hh = (time / 3600 % 24).toString()
        val mm = String.format("%02d", time / 60 % 60)
        val ss = String.format("%02d", time % 60)
        return if (time % 60 == 0) {
            hh + mm
        } else {
            hh + mm + "-" + ss

        }
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

    fun getArriveTime(station: Int): Int {
        try {
            if (time[station] and 0x2000000000000000L == 0L) {
                return if (time[station] and 0x1000000000000000L == 0L) {
                    -1
                } else getDepartureTime(station)
            }
            var result = time[station] and 0x000000fffff00000L
            result = result.ushr(20)
            return result.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -2
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
    fun getDepartureTime(station: Int): Int {
        try {
            if (time[station] and 0x1000000000000000L == 0L) {
                return if (time[station] and 0x2000000000000000L == 0L) {
                    -1
                } else getArriveTime(station)
            }
            val result = time[station] and 0x000000000fffffL
            return result.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -2
    }

    /**
     * 駅扱いをセットする。
     * @param station　駅インデックス
     * @param value　停車駅扱い番号
     */

    protected fun setStopType(station: Int, value: Int) {
        var value = value.toLong()
        if (value > 16 || value < 0) {
            //error
            return
        }
        if (value == 8.toLong() || value == 9.toLong()) {
            value = STOP_TYPE_STOP.toLong()
        }
        value = value shl 48
        time[station] = time[station] and 0x7FF0FFFFFFFFFFFFL
        time[station] = time[station] or value
    }

    /**
     * 発着番線をセットする
     */
    fun setForm(station: Int, value: Int) {
        var value = value.toLong()
        if (value > 256 || value < 0) {
            //error
            return
        }
        value = value shl 40
        time[station] = time[station] and 0x7FFF00FFFFFFFFFFL
        time[station] = time[station] or value
        time[station] = time[station] or 0x4000000000000000L




    }

    /**
     * 着時刻をセットする。
     * 0:00,10:34,10:3420,000,1034,103420などの形式に対応。
     * 文字列を秒単位に変換してtime[]にセットします
     * @param station　駅インデックス
     * @param str 着時刻を文字列にしたもの
     */
    protected fun setArriveTime(station: Int, str: String) {
        if (station < 0 || station >= time.size) {
            return
        }
        var result: Long = 0//minutes
        var h: Int
        val m: Int
        val s: Int
        if (str.length == 0) {
            return
        }
        if (str == "null") {
            return
        }
        if (str.indexOf(":") < 0) {
            //no ":" char so str is only number
            when (str.length) {
                3 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    result = (h * 3600 + m * 60).toLong()
                }
                4 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    result = (h * 3600 + m * 60).toLong()
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    s = Integer.parseInt(str.substring(3, 5))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                else -> result = -1
            }
        } else {
            //this str inclues ":" for example 12:17
            when (str.length) {
                4 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    result = (h * 3600 + m * 60).toLong()
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    result = (h * 3600 + m * 60).toLong()
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                7 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    s = Integer.parseInt(str.substring(5, 7))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                else -> result = -1
            }
        }
        if (result > 0) {
            time[station] = time[station] and 0x7FFFFF00000FFFFFL
            time[station] = time[station] or 0x2000000000000000L
            time[station] = time[station] or (result shl 20)
        }
    }

    /**
     * 発時刻をセットする。
     * 0:00,10:34,10:3420,000,1034,103420などの形式に対応。
     * 文字列を秒単位に変換してtime[]にセットします
     * @param station　駅インデックス
     * @param str 発時刻を文字列にしたもの
     */

    protected fun setDepartTime(station: Int, str: String) {
        if (station < 0 || station >= time.size) {
            return
        }

        var result: Long = 0//minutes
        var h: Int
        val m: Int
        val s: Int
        if (str.length == 0) {
            return
        }
        if (str == "null") {
            return
        }

        if (str.indexOf(":") < 0) {
            //no ":" char so str is only number
            when (str.length) {
                3 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    result = (h * 3600 + m * 60).toLong()
                }
                4 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    result = (h * 3600 + m * 60).toLong()
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    s = Integer.parseInt(str.substring(3, 5))

                    result = (h * 3600 + m * 60 + s).toLong()
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))

                    result = (h * 3600 + m * 60 + s).toLong()
                }
                else -> result = -1
            }
        } else {
            //this str inclues ":" for example 12:17
            when (str.length) {
                4 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    result = (h * 3600 + m * 60).toLong()
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    result = (h * 3600 + m * 60).toLong()
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                7 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s).toLong()
                }
                else -> result = -1
            }
        }
        if (result > 0) {
            time[station] = time[station] and 0x7FFFFFFFFFF00000L
            time[station] = time[station] or 0x1000000000000000L
            time[station] = time[station] or result
        }
    }

    fun getStopType(station: Int): Int {
        if (station < 0 || station >= time.size) {
            return 5
        }
        var result = time[station] and 0x000F000000000000L
        result = result.ushr(48)
        return if (result < 4) {
            result.toInt()
        } else {
            5
        }
    }

    /**
     * 着時刻が存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　着時刻存在フラグ
     */
    fun arriveExist(station: Int): Boolean {
        return if (station < 0 || station >= time.size) {
            false
        } else time[station] and 0x2000000000000000L != 0L
    }

    /**
     * 発時刻が存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　発時刻存在フラグ
     */
    fun departExist(station: Int): Boolean {
        return if (station < 0 || station >= time.size) {
            false
        } else time[station] and 0x1000000000000000L != 0L
    }

    /**
     * 時刻の存在。
     * 発時刻と着時刻のどちらかが存在する時trueを返す。
     * 駅インデックスが範囲外の時はfalseを返す
     * @param station　駅インデックス
     * @return　時刻存在フラグ
     */

    protected fun timeExist(station: Int): Boolean {
        return if (station < 0 || station >= time.size) {
            false
        } else time[station] and 0x3000000000000000L != 0L
    }


    /**
     * この列車の発着時刻を入力します。
     * oudiaのEkiJikoku形式の文字列を発着時刻に変換し、入力していきます。
     * @param str　oudiaファイル　EkiJikoku=の形式の文字列
     * @param direct　方向
     */
    internal fun setTime(str: String, direct: Int) {
        try {
            val timeString = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in timeString.indices) {
                if (timeString[i].length == 0) {
                    setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Train.STOP_TYPE_NOSERVICE)
                } else {
                    if (!timeString[i].contains(";")) {
                        setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Integer.parseInt(timeString[i]))
                    } else {
                        setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Integer.parseInt(timeString[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]))
                        try {
                            val stationTime = timeString[i].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                            if (!stationTime.contains("/")) {
                                setDepartTime((1 - 2 * direct) * i + direct * (stationNum - 1), stationTime)
                            } else {
                                if (stationTime.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == 2) {
                                    setArriveTime((1 - 2 * direct) * i + direct * (stationNum - 1), stationTime.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                                    setDepartTime((1 - 2 * direct) * i + direct * (stationNum - 1), stationTime.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                                } else {
                                    setArriveTime((1 - 2 * direct) * i + direct * (stationNum - 1), stationTime.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * valueは秒単位の時刻
     * @param station
     * @param value
     */
    private fun setArrivalTime(station: Int, value: Long) {
        if (value > 0 && value < 0xFFFFFF) {
            time[station] = time[station] and 0x7FFFFF00000FFFFFL
            time[station] = time[station] or 0x2000000000000000L
            time[station] = time[station] or (value shl 20)
        } else {
            if (value > 0xFFFFFF) {
                println(value)
            }
        }

    }

    private fun setDepartureTime(station: Int, value: Long) {
        if (value > 0 && value < 0xFFFFFF) {
            time[station] = time[station] and 0x7FFFFFFFFFF00000L
            time[station] = time[station] or 0x1000000000000000L
            time[station] = time[station] or value
        } else {
            if (value > 0xFFFFFF) {
                println(value)
            }
        }
    }


    companion object {
        /**
         * 駅扱いの定数。long timeの9~12bitがstop typeに対応する。
         */
        val STOP_TYPE_STOP = 1
        val STOP_TYPE_PASS = 2
        val STOP_TYPE_NOSERVICE = 0
        val STOP_TYPE_NOVIA = 3
    }


}
