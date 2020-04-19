package com.kamelong.OuDia

import com.kamelong.tool.Color
import com.kamelong.tool.SDlog
import java.io.PrintWriter
import java.util.*

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 列車１つを表します
 */
class Train(var lineFile: LineFile, direction: Int) : Cloneable {

    /**
     * この列車の列車方向を示します。
     *
     * コンストラクタで決まります。
     */
    @JvmField
    var direction = DOWN

    /**
     * 列車種別のindex
     */
    @JvmField
    var type = 0

    /**
     * 列車番号
     */
    @JvmField
    var number = ""

    /**
     * 列車名
     */
    @JvmField
    var name = ""

    /**
     * 列車号数
     */
    @JvmField
    var count = ""

    /**
     * 備考
     */
    @JvmField
    var remark = ""

    /**
     * この列車の各駅の時刻。
     * 要素数は、『駅』(DiaFile.stations) の数に等しくなります。
     * 添え字は『駅index』です。
     * 初期状態では、要素数は 0 となります。
     */
    var stationTimes = ArrayList<StationTime?>()

    /**
     * OuDiaファイルの１行を読み込みます
     */
    fun setValue(title: String, value: String) {
        var title = title
        when (title) {
            "Syubetsu" -> type = value.toInt()
            "Ressyabangou" -> number = value
            "Ressyamei" -> name = value
            "Gousuu" -> count = value
            "EkiJikoku" -> setOuDiaTime(value.split(",").dropLastWhile { it.isEmpty() }.toTypedArray())
            "RessyaTrack" -> setOuDiaTrack(value.split(",").dropLastWhile { it.isEmpty() }.toTypedArray())
            "Bikou" -> remark = value
        }
        if (title.startsWith("OperationNumbe")) {
            return
        }
        if (title.startsWith("Operation")) {
            if (title.contains(".")) {
                title = title.substring(9)
                val stations = title.split("\\.").dropLastWhile { it.isEmpty() }.toTypedArray()
                val index = getStationIndex(stations[0].substring(0, stations[0].length - 1).toInt())
                var operationList: ArrayList<StationTimeOperation?>?
                operationList = if (stations[0].substring(stations[0].length - 1) == "B") {
                    stationTimes[index]!!.beforeOperations
                } else {
                    stationTimes[index]!!.afterOperations
                }
                for (i in 1 until stations.size) {
                    val index2 = stations[i].substring(0, stations[i].length - 1).toInt()
                    operationList = if (stations[i].substring(stations[i].length - 1) == "B") {
                        operationList!![index2]!!.beforeOperation
                    } else {
                        operationList!![index2]!!.afterOperation
                    }
                }
                for (s in value.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    operationList!!.add(StationTimeOperation(s))
                }
            } else {
                val index = getStationIndex(title.substring(9, title.length - 1).toInt())
                if (title.substring(title.length - 1) == "B") {
                    for (s in value.split(",").toTypedArray()) {
                        stationTimes[index]!!.beforeOperations.add(StationTimeOperation(s))
                    }
                } else {
                    for (s in value.split(",").toTypedArray()) {
                        stationTimes[index]!!.afterOperations.add(StationTimeOperation(s))
                    }
                }
            }
        }
    }

    /**
     * Ekijikoku行の読み込みを行う
     * @param value
     */
    private fun setOuDiaTime(value: Array<String>) {
        stationTimes = ArrayList()
        for (i in 0 until lineFile.stationNum) {
            stationTimes.add(StationTime(this))
        }
        var i = 0
        while (i < value.size && i < lineFile.stationNum) {
            stationTimes[getStationIndex(i)]!!.setStationTime(value[i])
            if (getStopTrack(getStationIndex(i)) < 0 || getStopTrack(getStationIndex(i)) >= lineFile.getStation(getStationIndex(i)).getTrackNum()) {
                setStopTrack(getStationIndex(i), -1)
            }
            if (getStopType(getStationIndex(i)) < 0 || getStopType(getStationIndex(i)) >= 4) {
                setStopType(getStationIndex(i), 2)
            }
            i++
        }
    }

    /**
     * OuDia2ndの番線行の読み込みを行う。
     * @param value
     */
    private fun setOuDiaTrack(value: Array<String>) {
        var i = 0
        while (i < value.size && i < stationTimes.size) {
            stationTimes[getStationIndex(i)]!!.setTrack(value[i])
            i++
        }
    }

    /**
     * OuDiaSecond形式で保存します
     * @param out
     */
    fun saveToFile(out: PrintWriter) {
        out.println("Ressya.")
        if (direction == 0) {
            out.println("Houkou=Kudari")
        } else {
            out.println("Houkou=Nobori")
        }
        out.println("Syubetsu=$type")
        if (number.length > 0) {
            out.println("Ressyabangou=$number")
        }
        if (name.length > 0) {
            out.println("Ressyamei=$name")
        }
        if (count.length > 0) {
            out.println("Gousuu=$count")
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(true))
        if (remark.length > 0) {
            out.println("Bikou=$remark")
        }
        if (startStation >= 0) {
            if (stationTimes[startStation]!!.beforeOperations.size > 0) {
                saveOperationToFile(out, stationTimes[startStation]!!.beforeOperations, "Operation" + getStationIndex(startStation) + "B")
            }
            if (stationTimes[endStation]!!.afterOperations.size > 0) {
                saveOperationToFile(out, stationTimes[endStation]!!.afterOperations, "Operation" + getStationIndex(endStation) + "A")
            }
        }
        out.println(".")
    }

    private fun saveOperationToFile(out: PrintWriter, target: ArrayList<StationTimeOperation?>?, title: String) {
        if (target!!.size == 0) return
        var result = "$title="
        for (i in target.indices) {
            result += target[i].getOuDiaString() + ","
        }
        out.println(result.substring(0, result.length - 1))
    }

    fun saveToOuDiaFile(out: PrintWriter) {
        out.println("Ressya.")
        if (direction == 0) {
            out.println("Houkou=Kudari")
        } else {
            out.println("Houkou=Nobori")
        }
        out.println("Syubetsu=$type")
        if (number.length > 0) {
            out.println("Ressyabangou=$number")
        }
        if (name.length > 0) {
            out.println("Ressyamei=$name")
        }
        if (count.length > 0) {
            out.println("Gousuu=$count")
        }
        out.println("EkiJikoku=" + getEkijikokuOudia(false))
        if (remark.length > 0) {
            out.println("Bikou=$remark")
        }
        out.println(".")
    }

    /**
     * OuDia形式の駅時刻行を作成します。
     * @param secondFrag trueの時oudia2nd形式に対応します。
     * @return
     */
    private fun getEkijikokuOudia(secondFrag: Boolean): String {
        val result = StringBuilder()
        if (stationTimes.size > lineFile.stationNum) {
            println("駅数オーバーフロー")
            return ""
        }
        for (i in stationTimes.indices) {
            val station = getStationIndex(i)
            result.append(stationTimes[station]!!.getOuDiaString(secondFrag))
            result.append(",")
        }
        return result.toString()
    }

    /**
     * 上り下りの時刻表駅順から、路線駅順を返します。
     * 下りの時は時刻表駅順は路線駅順と同じ
     * 上りの時は時刻表駅順は路線駅順の逆になります。
     */
    fun getStationIndex(index: Int): Int {
        return if (direction == 0) {
            index
        } else {
            lineFile.stationNum - index - 1
        }
    }

    /**
     *
     * @param lineFile 親LineFile
     * @return コピーした列車
     */
    fun clone(lineFile: LineFile): Train {
        return try {
            val result = super.clone() as Train
            result.lineFile = lineFile
            result.stationTimes = ArrayList()
            for (time in stationTimes) {
                val clone = time!!.clone(this)
                clone!!.train = result
                result.stationTimes.add(clone)
            }
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            Train(lineFile, direction)
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
    /**
     * 駅数を返します
     */
    val stationNum: Int
        get() = stationTimes.size

    /**
     * この列車がすべての駅で運行なしの場合、
     * 使用されていないnull列車とします
     */
    fun isnull(): Boolean {
        for (i in 0 until lineFile.stationNum) {
            if (stationTimes[i]!!.stopType.toInt() != 0) return false
        }
        return true
    }

    /**
     * 列車の始発駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    val startStation: Int
        get() {
            if (direction == 0) {
                for (i in stationTimes.indices) {
                    when (getStopType(i)) {
                        1, 2 -> return i
                    }
                }
            } else {
                for (i in stationTimes.indices.reversed()) {
                    when (getStopType(i)) {
                        1, 2 -> return i
                    }
                }
            }
            return -1
        }

    /**
     * 列車の時刻が存在する最初の駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    val timeStartStation: Int
        get() {
            if (direction == 0) {
                for (i in stationTimes.indices) {
                    if (timeExist(i)) return i
                }
            } else {
                for (i in stationTimes.indices.reversed()) {
                    if (timeExist(i)) return i
                }
            }
            return -1
        }

    /**
     * 列車の種着駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    val endStation: Int
        get() {
            if (direction == 1) {
                for (i in stationTimes.indices) {
                    when (getStopType(i)) {
                        1, 2 -> return i
                    }
                }
            } else {
                for (i in stationTimes.indices.reversed()) {
                    when (getStopType(i)) {
                        1, 2 -> return i
                    }
                }
            }
            return -1
        }

    /**
     * 列車の種着駅を返す。もし、全ての駅で運行されていなければ-1を返す
     * @return
     */
    val timeEndStation: Int
        get() {
            if (direction == 1) {
                for (i in stationTimes.indices) {
                    if (timeExist(i)) return i
                }
            } else {
                for (i in stationTimes.indices.reversed()) {
                    if (timeExist(i)) return i
                }
            }
            return -1
        }

    //todo
    val operationNumber: String
        get() =//todo
            ""

    /**
     * 列車の文字色
     */
    val textColor: Color?
        get() = lineFile.trainType[type].textColor

    /**
     * 列車の種別名
     */
    val typeName: String?
        get() = lineFile.trainType[type].name

    /**
     * 列車の種別略称
     */
    val typeShortName: String?
        get() = lineFile.trainType[type].shortName

    /**
     * 指定駅の停車タイプを返します
     * @return int(0-3)
     */
    fun getStopType(stationIndex: Int): Int {
        return if (stationIndex < 0 || stationIndex >= stationNum) {
            StationTime.Companion.STOP_TYPE_NOSERVICE.toInt()
        } else stationTimes[stationIndex]!!.stopType.toInt()
    }

    /**
     * 指定駅の停車タイプを設定します
     */
    fun setStopType(stationIndex: Int, type: Int) {
        stationTimes[stationIndex]!!.stopType = type.toByte()
        if (type == 0) {
            stationTimes[stationIndex]!!.ariTime = -1
            stationTimes[stationIndex]!!.depTime = -1
        }
    }

    /**
     * 指定駅に有効な時刻が存在するか（着時刻、発時刻の片方でもあればよい)
     */
    fun timeExist(stationIndex: Int): Boolean {
        return stationTimes[stationIndex]!!.timeExist()
    }

    /**
     * 指定駅に有効な時刻が存在するか（着時刻、発時刻別)
     * AD=0:dep,AD=1:Ari
     */
    fun timeExist(stationIndex: Int, AD: Int): Boolean {
        return stationTimes[stationIndex]!!.timeExist(AD)
    }

    /**
     * 着時刻取得
     */
    fun getDepTime(station: Int): Int {
        return stationTimes[station]!!.depTime
    }

    /**
     * 着時刻設定
     */
    fun setDepTime(station: Int, value: Int) {
        stationTimes[station]!!.depTime = value
    }

    /**
     * 発時刻取得
     */
    fun getAriTime(station: Int): Int {
        return stationTimes[station]!!.ariTime
    }

    /**
     * 発時刻設定
     */
    fun setAriTime(station: Int, value: Int) {
        stationTimes[station]!!.ariTime = value
    }

    /**
     * 時刻取得(発時刻、着時刻別）
     */
    fun getTime(station: Int, AD: Int): Int {
        return if (AD == 0) {
            getDepTime(station)
        } else {
            getAriTime(station)
        }
    }

    /**
     * 時刻取得(発時刻、着時刻別）
     * useOther=trueの時、該当時刻が存在しないとき、代わりに同一駅の（発時刻、着時刻)を使用する。
     * 両方ともないときは-1
     */
    fun getTime(station: Int, AD: Int, useOther: Boolean): Int {
        return if (useOther) {
            if (timeExist(station, AD)) {
                getTime(station, AD)
            } else getTime(station, (AD + 1) % 2)
        } else getTime(station, AD)
    }

    /**
     * 時刻設定(発時刻、着時刻別）
     */
    fun setTime(station: Int, AD: Int, time: Int) {
        if (AD == 0) {
            setDepTime(station, time)
        } else {
            setAriTime(station, time)
        }
    }

    /**
     * 発着番線取得
     */
    fun getStopTrack(station: Int): Int {
        val result = stationTimes[station]!!.stopTrack.toInt()
        return if (result < 0) {
            lineFile.station[station]!!.stopMain[direction]
        } else result
    }

    /**
     * 発着番線設定
     */
    fun setStopTrack(station: Int, value: Int) {
        stationTimes[station]!!.stopTrack = value.toByte()
    }

    /**
     * 路線外始発駅名
     * 路線外始発駅が存在しない場合はnullが返る
     */
    val outerStartStationName: String?
        get() {
            val startStation = startStation
            if (startStation < 0) {
                return null
            }
            for (operation in stationTimes[startStation]!!.beforeOperations) {
                if (operation!!.operationType == 4 && operation.intData1 >= 0 && operation.intData1 < lineFile.getStation(startStation)!!.outerTerminals.size) {
                    return lineFile.getStation(startStation)!!.getOuterStationTimeTableName(operation.intData1)
                }
            }
            return null
        }

    var outerStartStation: Int
        get() {
            val startStation = startStation
            if (startStation < 0) {
                return -1
            }
            for (operation in stationTimes[startStation]!!.beforeOperations) {
                if (operation!!.operationType == 4) {
                    return operation.intData1
                }
            }
            return -1
        }
        set(outer) {
            val station = startStation
            if (stationTimes[station]!!.beforeOperations.size == 0) {
                stationTimes[station]!!.beforeOperations.add(StationTimeOperation())
            }
            stationTimes[station]!!.beforeOperations[0]!!.operationType = 4
            stationTimes[station]!!.beforeOperations[0]!!.intData1 = outer
        }

    /**
     * 路線外始発駅始発時刻
     */
    var outerStartTime: Int
        get() {
            val startStation = startStation
            if (startStation < 0) {
                return -1
            }
            for (operation in stationTimes[startStation]!!.beforeOperations) {
                if (operation!!.operationType == 4) {
                    return operation.time1
                }
            }
            return -1
        }
        set(time) {
            val station = startStation
            if (station < 0) {
                SDlog.toast("空列車に路線外始発駅を設定する事はできません")
                return
            }
            if (stationTimes[station]!!.beforeOperations.size == 0) {
                stationTimes[station]!!.beforeOperations.add(StationTimeOperation())
            }
            stationTimes[station]!!.beforeOperations[0]!!.operationType = 4
            stationTimes[station]!!.beforeOperations[0]!!.time1 = time
        }

    /**
     * 路線外終着駅名
     * 路線外終着駅が存在しない場合はnullが返る
     */
    val outerEndStationName: String?
        get() {
            val endStation = endStation
            if (endStation < 0) {
                return null
            }
            for (operation in stationTimes[endStation]!!.afterOperations) {
                if (operation!!.operationType == 4 && operation.intData1 >= 0 && operation.intData1 < lineFile.getStation(endStation)!!.outerTerminals.size) {
                    return lineFile.getStation(endStation)!!.getOuterStationTimeTableName(operation.intData1)
                }
            }
            return null
        }

    var outerEndStation: Int
        get() {
            val endStation = endStation
            if (endStation < 0) {
                return -1
            }
            for (operation in stationTimes[endStation]!!.afterOperations) {
                if (operation!!.operationType == 4) {
                    return operation.intData1
                }
            }
            return -1
        }
        set(outer) {
            val station = endStation
            if (stationTimes[station]!!.afterOperations.size == 0) {
                stationTimes[station]!!.afterOperations.add(StationTimeOperation())
            }
            stationTimes[station]!!.afterOperations[0]!!.operationType = 4
            stationTimes[station]!!.afterOperations[0]!!.intData1 = outer
        }

    /**
     * 路線外終着駅時刻
     */
    var outerEndTime: Int
        get() {
            val endStation = endStation
            if (endStation < 0) {
                return -1
            }
            for (operation in stationTimes[endStation]!!.afterOperations) {
                if (operation!!.operationType == 4) {
                    return operation.time1
                }
            }
            return -1
        }
        set(time) {
            val station = endStation
            if (station < 0) {
                SDlog.toast("空列車に路線外始発駅を設定する事はできません")
                return
            }
            if (stationTimes[station]!!.afterOperations.size == 0) {
                stationTimes[station]!!.afterOperations.add(StationTimeOperation())
            }
            stationTimes[station]!!.afterOperations[0]!!.operationType = 4
            stationTimes[station]!!.afterOperations[0]!!.time1 = time
        }

    /**
     * 全駅の時刻をshift秒移動させる
     * @param shift
     */
    fun shiftTime(shift: Int) {
        for (time in stationTimes) {
            time!!.shiftDep(shift)
            time.shiftAri(shift)
        }
    }

    /**
     * 当駅始発にする
     * @param stationIndex
     */
    fun startAtThisStation(stationIndex: Int) {
        if (direction == 0) {
            for (i in 0 until stationIndex) {
                stationTimes[i]!!.reset()
            }
        } else {
            for (i in stationIndex + 1 until stationNum) {
                stationTimes[i]!!.reset()
            }
        }
        setAriTime(stationIndex, -1)
    }

    /**
     * 当駅止めにする
     * @param stationIndex
     */
    fun endAtThisStation(stationIndex: Int) {
        if (direction == 0) {
            for (i in stationIndex + 1 until stationNum) {
                stationTimes[i]!!.reset()
            }
        } else {
            for (i in 0 until stationIndex) {
                stationTimes[i]!!.reset()
            }
        }
        setDepTime(stationIndex, -1)
    }

    /**
     * 列車を結合する
     * 結合駅の出発時刻はotherのものを用いる
     * @param other
     */
    fun conbine(other: Train?) {
        val endStation = endStation
        val startStation = other!!.startStation
        if (direction == 0) {
            for (i in startStation + 1 until stationNum) {
                stationTimes[i] = other.stationTimes[i]!!.clone(this)
            }
        } else {
            for (i in 0 until startStation) {
                stationTimes[i] = other.stationTimes[i]!!.clone(this)
            }
        }
        if (startStation == endStation) {
            setDepTime(endStation, other.getDepTime(endStation))
        } else {
            for (i in startStation + 1 until endStation) {
                setStopType(i, StationTime.Companion.STOP_TYPE_NOVIA.toInt())
            }
        }
    }

    /**
     * 2駅間の所要時間を返す。もし片方の駅に時刻がなければ-1を返す
     */
    fun reqTime(station1: Int, station2: Int): Int {
        return if (timeExist(station1) && timeExist(station2)) {
            if ((1 - direction * 2) * (station2 - station1) > 0) {
                getTime(station2, ARRIVE, true) - getTime(station1, DEPART, true)
            } else {
                getTime(station1, ARRIVE, true) - getTime(station2, DEPART, true)
            }
        } else -1
    }

    /**
     * 列車の通過予想時刻
     * この駅を列車が通過しないと判断したら-1が返る
     */
    fun getPredictionTime(station: Int): Int {
        return getPredictionTime(station, DEPART)
    }

    /**
     * 列車の通過予想時刻
     * AD=1の時、着時刻が存在する場合は着時刻っを優先する
     */
    fun getPredictionTime(station: Int, AD: Int): Int {
        if (getStopType(station) == StationTime.Companion.STOP_TYPE_NOVIA.toInt() || getStopType(station) == StationTime.Companion.STOP_TYPE_NOSERVICE.toInt()) {
            return -1
        }
        if (AD == 1 && timeExist(station, ARRIVE)) {
            return getAriTime(station)
        }
        if (timeExist(station)) {
            return getTime(station, AD, true)
        }
        if (getStopType(station) == StationTime.Companion.STOP_TYPE_PASS.toInt()) {
            //通過時間を予測します
            var afterTime = -1 //後方の時刻あり駅の発車時間
            var beforeTime = -1 //後方の時刻あり駅の発車時間
            var afterMinTime = 0 //後方の時刻あり駅までの最小時間
            var beforeMinTime = 0 //前方の時刻あり駅までの最小時間
            val minstationTime = lineFile.getStationTime()

            //対象駅より先の駅で駅時刻が存在する駅までの最小所要時間
            for (i in station + 1 until lineFile.stationNum) {
                if (getStopType(i) == StationTime.Companion.STOP_TYPE_NOSERVICE.toInt() || getStopType(i) == StationTime.Companion.STOP_TYPE_NOVIA.toInt() || getStopType(i - 1) == StationTime.Companion.STOP_TYPE_NOSERVICE.toInt() || getStopType(i - 1) == StationTime.Companion.STOP_TYPE_NOVIA.toInt()) {
                    continue
                }
                afterMinTime = afterMinTime + minstationTime[i] - minstationTime[i - 1]
                if (timeExist(i)) {
                    afterTime = if (direction == 0) {
                        getTime(i, ARRIVE, true)
                    } else {
                        getTime(i, DEPART, true)
                    }
                    break
                }
            }
            if (afterTime < 0) {
                //対象駅より先の駅で駅時刻が存在する駅がなかった
                return -1
            }
            //対象駅より前方の駅で駅時刻が存在する駅までの最小所要時間と駅時刻
            var startStation = 0
            for (i in station downTo 1) {
                if (getStopType(i) == StationTime.Companion.STOP_TYPE_NOSERVICE.toInt() || getStopType(i) == StationTime.Companion.STOP_TYPE_NOVIA.toInt() || getStopType(i - 1) == StationTime.Companion.STOP_TYPE_NOSERVICE.toInt() || getStopType(i - 1) == StationTime.Companion.STOP_TYPE_NOVIA.toInt()) {
                    continue
                }
                beforeMinTime = beforeMinTime + minstationTime[i] - minstationTime[i - 1]
                if (timeExist(i - 1)) {
                    beforeTime = if (direction == 0) {
                        getTime(i - 1, DEPART, true)
                    } else {
                        getTime(i - 1, ARRIVE, true)
                    }
                    startStation = i - 1
                    break
                }
            }
            return if (beforeTime < 0) {
                -1
            } else getDepTime(startStation) + (afterTime - beforeTime) * beforeMinTime / (afterMinTime + beforeMinTime)
        }
        return -1
    }

    /**
     * 分岐駅で経由なしを用いる場合はbrunch=true
     * index:挿入する駅index
     */
    fun addNewStation(index: Int, brunch: Boolean) {
        //追加する駅時刻
        val time = StationTime(this)
        if (brunch) {
            if (index > 0 && index < stationTimes.size) {
                //挿入駅の前後がNOSERVICEではない時
                if (getStopType(index - 1) != StationTime.Companion.STOP_TYPE_NOSERVICE.toInt() && getStopType(index) != StationTime.Companion.STOP_TYPE_NOSERVICE.toInt()) {
                    time.stopType = StationTime.Companion.STOP_TYPE_NOVIA
                }
            }
        } else {
            if (index > 0 && index < stationTimes.size) {
                if (getStopType(index - 1) == StationTime.Companion.STOP_TYPE_STOP.toInt() || getStopType(index - 1) == StationTime.Companion.STOP_TYPE_PASS.toInt()) {
                    if (getStopType(index) == StationTime.Companion.STOP_TYPE_STOP.toInt() || getStopType(index) == StationTime.Companion.STOP_TYPE_PASS.toInt()) {
                        time.stopType = StationTime.Companion.STOP_TYPE_PASS
                    }
                }
                if (getStopType(index - 1) == StationTime.Companion.STOP_TYPE_NOVIA.toInt()) {
                    if (getStopType(index) == StationTime.Companion.STOP_TYPE_NOVIA.toInt()) {
                        time.stopType = StationTime.Companion.STOP_TYPE_NOVIA
                    }
                }
            }
        }
        stationTimes.add(index, time)
    }

    /**
     * 日付をまたいでいる列車かどうか確認する。
     * 12時間以上さかのぼる際は日付をまたいでいると考えています。
     */
    fun checkDoubleDay(): Boolean {
        var time = getTime(startStation, DEPART, true)
        for (i in startStation until endStation + 1) {
            if (timeExist(i)) {
                if (getTime(i, DEPART, true) - time < -12 * 60 * 60 || getTime(i, DEPART, true) - time > 12 * 60 * 60) {
                    return true
                }
                time = getTime(i, DEPART, true)
            }
        }
        return false
    }

    fun stopOrPass(stationIndex: Int): Boolean {
        return getStopType(stationIndex) == StationTime.Companion.STOP_TYPE_STOP.toInt() || getStopType(stationIndex) == StationTime.Companion.STOP_TYPE_PASS.toInt()
    }

    companion object {
        const val DEPART = 0
        const val ARRIVE = 1

        //下り
        const val DOWN = 0

        //上り
        const val UP = 1
    }

    /**
     * デフォルトコンストラクタ
     * @param lineFile この列車が含まれるDiaFile
     * @param direction　進行方向　上り:1,下り:0
     */
    init {
        this.direction = direction
        stationTimes = ArrayList()
        for (i in 0 until lineFile.stationNum) {
            stationTimes.add(StationTime(this))
        }
    }
}