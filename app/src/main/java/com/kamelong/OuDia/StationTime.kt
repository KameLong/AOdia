package com.kamelong.OuDia

import com.kamelong.tool.SDlog
import java.util.*

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * １つの駅停車情報を表します。
 * StationTimeはTrainに紐付けられます。
 */
class StationTime(train: Train?) : Cloneable {
    /**
     * 親列車
     */
    var train: Train? = null

    /**
     * 『駅扱』を表します。
     */
    var stopType = STOP_TYPE_NOSERVICE

    /**
     * 着時刻
     * 着時刻が存在しない時は負の数となります
     * 時刻は秒単位で表現し、0:00:00を0とします。
     * 1:00:00は3600、10:08:20は10*3600+8*60+20=36,500となります。
     * なお、内部処理を行う観点から、時刻の起点を3:00:00とします
     */
    private var ariTime = -1

    /**
     * 発時刻
     * 発時刻が存在しない時は負の数となります
     * 時刻は秒単位で表現し、0:00:00を0とします。
     * なお、内部処理を行う観点から、時刻の起点を3:00:00とします
     */
    private var depTime = -1

    /**
     * 番線
     * デフォルト=-1
     */
    var stopTrack: Byte = -1

    /**
     * 前作業一覧
     */
    var beforeOperations = ArrayList<StationTimeOperation?>()

    /**
     * 後作業一覧
     */
    var afterOperations = ArrayList<StationTimeOperation?>()

    /**
     * @param train コピー先の親列車
     * @return
     */
    fun clone(train: Train?): StationTime {
        return try {
            val other = super.clone() as StationTime
            other.beforeOperations = ArrayList()
            other.afterOperations = ArrayList()
            other.train = train
            try {
                for (operation in beforeOperations) {
                    other.beforeOperations.add(operation!!.clone())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (operation in afterOperations) {
                other.afterOperations.add(operation!!.clone())
            }
            other
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            StationTime(train)
        }
    }

    fun setStationTime(value: String) {
        var value = value
        if (value.length == 0) {
            stopType = 0
            return
        }
        if (!value.contains(";")) {
            if (value.contains("$")) {
                stopTrack = value.split("\\$").dropLastWhile { it.isEmpty() }.toTypedArray()[1].toByte()
                stopType = value.split("\\$").dropLastWhile { it.isEmpty() }.toTypedArray()[0].toByte()
                return
            }
            stopType = value.toByte()
            return
        }
        stopType = value.split(";").dropLastWhile { it.isEmpty() }.toTypedArray()[0].toByte()
        value = value.split(";").dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        if (value.contains("$")) {
            stopTrack = value.split("\\$").dropLastWhile { it.isEmpty() }.toTypedArray()[1].toByte()
            value = value.split("\\$").toTypedArray()[0]
        }
        if (value.contains("/")) {
            setAriTime(timeStringToInt(value.split("/").dropLastWhile { it.isEmpty() }.toTypedArray()[0]))
            if (value.split("/").dropLastWhile { it.isEmpty() }.toTypedArray()[1].length != 0) {
                setDepTime(timeStringToInt(value.split("/").dropLastWhile { it.isEmpty() }.toTypedArray()[1]))
            }
        } else {
            setDepTime(timeStringToInt(value))
        }
    }

    /**
     * OuDia2ndV1までの発着番線読み込み
     * 運用機能は無視する
     * @param value
     */
    fun setTrack(value: String) {
        var value = value
        try {
            if (value.length == 0) {
                stopTrack = -1
                return
            }
            if (value.contains(";")) {
                value = value.split(";").toTypedArray()[0]
            }
            stopTrack = (value.toByte() - 1).toByte()
        } catch (e: Exception) {
            stopTrack = -1
            SDlog.log(e)
        }
    }

    fun getOuDiaString(oudia2ndFrag: Boolean): String {
        var value = ""
        if (stopType.toInt() == 0) {
            return value
        }
        value += if (stopType.toInt() == 3) {
            //OuDia2nd対策はしない
            stopType
        } else {
            stopType
        }
        if (ariTime < 0 && depTime < 0) {
            if (oudia2ndFrag) {
                value += "$$stopTrack"
            }
            return value
        }
        value += ";"
        if (timeExist(1)) {
            value += timeIntToOuDiaString(ariTime) + "/"
        }
        if (timeExist(0)) {
            value += timeIntToOuDiaString(depTime)
        }
        if (oudia2ndFrag) {
            value += "$" + getStopTrack()
        }
        return value
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
    /**
     * @param ad Train.DOWN or Train.UP
     * @return 各時刻が存在する場合はtrue、存在しないときはfalseを返します
     */
    fun timeExist(ad: Int): Boolean {
        return if (ad == 0) {
            depTime >= 0
        } else {
            ariTime >= 0
        }
    }

    /**
     * @return 発着時刻のうち片方でも存在する場合はtrue、存在しないときはfalseを返します
     */
    fun timeExist(): Boolean {
        return depTime >= 0 || ariTime >= 0
    }

    /**
     * データを削除し、初期状態にします
     */
    fun reset() {
        depTime = -1
        ariTime = -1
        stopTrack = 0
        stopType = 0
        afterOperations = ArrayList()
        beforeOperations = ArrayList()
    }

    /**
     * 発着番線を返します。
     * trackが-1の時はデフォルトの発着番線を返します。
     */
    fun getStopTrack(): Int {
        return if (stopTrack < 0) {
            train!!.lineFile.station[train!!.getStationIndex(train!!.stationTimes.indexOf(this))]!!.stopMain[train!!.direction]
        } else stopTrack.toInt()
    }

    /**
     * @return 発時刻
     */
    fun getDepTime(): Int {
        return depTime
    }

    /**
     * 発時刻設定。
     * 時刻の正規化を行います
     */
    fun setDepTime(time: Int) {
        var time = time
        if (time < 0) {
            depTime = -1
            return
        }
        if (time < 3 * 3600) {
            time += 24 * 3600
        }
        if (time >= 27 * 3600) {
            time -= 24 * 3600
        }
        depTime = time
    }

    /**
     * @return 着時刻
     */
    fun getAriTime(): Int {
        return ariTime
    }

    /**
     * 着時刻設定、時刻の正規化を行います
     * @param time
     */
    fun setAriTime(time: Int) {
        var time = time
        if (time < 0) {
            ariTime = -1
            return
        }
        if (time < 3 * 3600) {
            time += 24 * 3600
        }
        if (time >= 27 * 3600) {
            time -= 24 * 3600
        }
        ariTime = time
    }

    /**
     * 発時刻をスライドさせます。
     */
    fun shiftDep(shift: Int) {
        if (depTime < 0) {
            return
        }
        depTime += shift
        if (depTime < 3 * 3600) {
            depTime += 24 * 3600
        }
        if (depTime >= 27 * 3600) {
            depTime -= 24 * 3600
        }
    }

    /**
     * 着時刻をスライドさせます。
     * 時刻が存在しないときは、スライドしません。
     */
    fun shiftAri(shift: Int) {
        if (ariTime < 0) {
            return
        }
        ariTime += shift
        if (ariTime < 3 * 3600) {
            ariTime += 24 * 3600
        }
        if (ariTime >= 27 * 3600) {
            ariTime -= 24 * 3600
        }
    }

    companion object {
        const val STOP_TYPE_NOSERVICE: Byte = 0
        const val STOP_TYPE_STOP: Byte = 1
        const val STOP_TYPE_PASS: Byte = 2
        const val STOP_TYPE_NOVIA: Byte = 3

        /**
         * 秒単位の数値で表現された時刻を、文字列に変換します。
         * 秒部分が0の時は秒部分が省略されます。
         * @param time
         * @return
         */
        @JvmStatic
        fun timeIntToOuDiaString(time: Int): String {
            var time = time
            if (time < 0) return ""
            val ss = time % 60
            time = time / 60
            val mm = time % 60
            time = time / 60
            val hh = time % 24
            return if (ss == 0) {
                hh.toString() + String.format("%02d", mm)
            } else hh.toString() + String.format("%02d", mm) + String.format("%02d", ss)
        }

        /**
         * 文字列形式の時刻を秒の数値に変える
         *
         * @param sTime 3桁から6桁の数字で構成された文字列
         */
        @JvmStatic
        fun timeStringToInt(sTime: String): Int {
            return try {
                var hh = 0
                var mm = 0
                var ss = 0
                when (sTime.length) {
                    3 -> {
                        hh = sTime.substring(0, 1).toInt()
                        mm = sTime.substring(1, 3).toInt()
                    }
                    4 -> {
                        hh = sTime.substring(0, 2).toInt()
                        mm = sTime.substring(2, 4).toInt()
                    }
                    5 -> {
                        hh = sTime.substring(0, 1).toInt()
                        mm = sTime.substring(1, 3).toInt()
                        ss = sTime.substring(3, 5).toInt()
                    }
                    6 -> {
                        hh = sTime.substring(0, 2).toInt()
                        mm = sTime.substring(2, 4).toInt()
                        ss = sTime.substring(4, 6).toInt()
                    }
                    else -> return -1
                }
                if (hh > 23 || hh < 0) {
                    return -1
                }
                if (mm > 59 || mm < 0) {
                    return -1
                }
                if (ss > 59 || ss < 0) {
                    -1
                } else 3600 * hh + 60 * mm + ss
            } catch (e: NumberFormatException) {
                SDlog.log(e)
                -1
            }
        }
    }

    init {
        this.train = train
    }
}