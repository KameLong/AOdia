package com.kamelong.OuDia

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
 * １つのダイヤを表します。
 * ダイヤは１つの路線に複数作る事ができますが、同一路線中のダイヤは全て同じ駅順を持ちます。
 */
class Diagram(
        /**
         * 親LineFile
         * このオブジェクトを生成する際には必ずlineFileを設定する必要があります。
         * 別のLineFileにこのオブジェクトをコピーする際には、lineFileを書き換えてください。
         */
        var lineFile: LineFile) : Cloneable {

    /**
     * ダイヤの名称です。
     * （例） "平日ダイヤ" など
     * CentDedRosen に包含される CentDedDia では、
     * この属性は一意でなくてはなりません。
     */
    var name = ""

    /**
     * 時刻表画面における基本背景色のIndexです
     * 単色時、種別色時の空行、縦縞・横縞・市松模様時
     * および、基準運転時分機能有効時に用います。
     * 範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    var mainBackColorIndex = 0

    /**
     * 時刻表画面における補助背景色のIndexです
     * 縦縞・横縞・市松模様時に用います。
     * 範囲は0以上JIKOKUHYOUCOLOR_COUNT未満です。
     */
    var subBackColorIndex = 0

    /**
     * 時刻表画面における背景色パターンのIndexです
     * 0:単色
     * 1:種別色
     * 2:縦縞
     * 3:横縞
     * 4:市松模様
     */
    var timeTableBackPatternIndex = 0

    /**
     * ダイヤにふくまれる列車
     * [0]下り時刻表
     * [1]上り時刻表
     */
    var trains: Array<ArrayList<Train>?> = arrayOfNulls<ArrayList<Train>>(2)

    /**
     * OuDia形式で、データを読み込みます。
     */
    protected fun setValue(title: String?, value: String) {
        when (title) {
            "DiaName" -> name = value
            "MainBackColorIndex" -> mainBackColorIndex = value.toInt()
            "SubBackColorIndex" -> subBackColorIndex = value.toInt()
            "BackPatternIndex" -> timeTableBackPatternIndex = value.toInt()
        }
    }

    /**
     * OuDia2nd形式で出力します
     */
    fun saveToFile(out: PrintWriter) {
        out.println("Dia.")
        out.println("DiaName=$name")
        out.println("MainBackColorIndex=$mainBackColorIndex")
        out.println("SubBackColorIndex=$subBackColorIndex")
        out.println("BackPatternIndex=$timeTableBackPatternIndex")
        out.println("Kudari.")
        for (t in trains[0]!!) {
            t!!.saveToFile(out)
        }
        out.println(".")
        out.println("Nobori.")
        for (t in trains[1]!!) {
            t!!.saveToFile(out)
        }
        out.println(".")
        out.println(".")
    }

    /**
     * OuDia形式で出力します
     */
    fun saveToOuDiaFile(out: PrintWriter) {
        out.println("Dia.")
        out.println("DiaName=$name")
        out.println("Kudari.")
        for (t in trains[0]!!) {
            t!!.saveToOuDiaFile(out)
        }
        out.println(".")
        out.println("Nobori.")
        for (t in trains[1]!!) {
            t!!.saveToOuDiaFile(out)
        }
        out.println(".")
        out.println(".")
    }

    /**
     * Diagramをコピーします。
     * lineFile:コピー先のDiagramが所属するLineFile
     */
    fun clone(lineFile: LineFile): Diagram {
        return try {
            val result = super.clone() as Diagram
            result.trains = arrayOfNulls<ArrayList<Train>?>(2)
            result.trains[0] = ArrayList()
            for (train in trains[0]!!) {
                result.trains[0]!!.add(train!!.clone(lineFile))
            }
            result.trains[1] = ArrayList()
            for (train in trains[1]!!) {
                result.trains[1]!!.add(train!!.clone(lineFile))
            }
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            Diagram(lineFile)
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
    /**
     * 指定方向の列車数を返します
     */
    fun getTrainNum(direction: Int): Int {
        return try {
            trains[direction]!!.size
        } catch (e: IndexOutOfBoundsException) {
            0
        }
    }

    /**
     * 列車を取得します
     * @param direction 方向(0,1)
     * @param index 列車index
     */
    fun getTrain(direction: Int, index: Int): Train? {
        return try {
            trains[direction]!![index]
        } catch (e: IndexOutOfBoundsException) {
            SDlog.log(Exception("Diagram.getTrain($direction,$index)"))
            Train(lineFile, 0)
        }
    }

    /**
     * 指定列車のindexを返します
     */
    fun getTrainIndex(direction: Int, train: Train?): Int {
        return trains[direction]!!.indexOf(train)
    }

    fun getTrainIndex(train: Train): Int {
        return trains[train.direction]!!.indexOf(train)
    }

    /**
     * 指定indexに列車を追加します。
     * index=-1の時は、末尾に追加されます
     * 列車を追加する際は、その列車が持つStationTimeの数とlineFileの駅数が一致する必要があります
     * 一致しない場合はfalseを返し、列車の追加を行いません。
     * 追加に成功するとtrueを返します。
     *
     */
    fun addTrain(direction: Int, index: Int, train: Train): Boolean {
        if (train.stationNum != lineFile.stationNum) {
            SDlog.log("列車の駅数とダイヤの駅数が合いません")
            return false
        }
        if (index >= 0 && index < getTrainNum(direction)) {
            trains[direction]!!.add(index, train)
            train.lineFile = lineFile
            if (train.direction != direction) {
                Collections.reverse(train.stationTimes)
            }
            train.direction = direction
        } else {
            trains[direction]!!.add(train)
        }
        return true
    }

    /**
     * 指定列車を削除します
     * 方向とindexを指定
     */
    fun deleteTrain(direction: Int, index: Int) {
        if (index >= 0 && index < getTrainNum(direction)) {
            trains[direction]!!.removeAt(index)
        }
    }

    /**
     * 指定列車を削除します
     */
    fun deleteTrain(train: Train) {
        trains[train.direction]!!.remove(train)
    }

    /**
     * ダイヤのソートを行う前の処理です。
     * 時刻が存在しない空列車を削除してからソートを行います。
     */
    private fun beforeSort(direction: Int) {
        var i = 0
        while (i < trains[direction]!!.size) {
            if (trains[direction]!![i]!!.isnull()) {
                trains[direction]!!.removeAt(i)
                i--
            }
            i++
        }
    }

    /**
     * 時刻表を並び替える。
     * 並び替えに関しては、基準駅の通過時刻をもとに並び替えた後
     * @param direction     並び替え対象方向
     * @param stationNumber 並び替え基準駅
     */
    fun sortTrain(direction: Int, stationNumber: Int) {
        beforeSort(direction)
        val trainList: Array<Train> = trains[direction]!!.toTypedArray()!!
        val sorter = TimeTableSorter(lineFile, trainList, direction)
        trains[direction] = sorter.sort(stationNumber)
    }

    /**
     * 列車番号ソート
     */
    fun sortNumber(direction: Int) {
        beforeSort(direction)
        val trainList: Array<Train> = trains[direction]!!.toTypedArray()!!
        val sorter = TimeTableSorter(lineFile, trainList, direction)
        trains[direction] = sorter.sortNumber()
    }

    /**
     * 種別ソート
     */
    fun sortType(direction: Int) {
        beforeSort(direction)
        val trainList: Array<Train> = trains[direction]!!.toTypedArray()!!
        val sorter = TimeTableSorter(lineFile, trainList, direction)
        trains[direction] = sorter.sortType()
    }

    /**
     * 列車名ソート
     */
    fun sortName(direction: Int) {
        beforeSort(direction)
        val trainList: Array<Train> = trains[direction]!!.toTypedArray()!!
        val sorter = TimeTableSorter(lineFile, trainList, direction)
        trains[direction] = sorter.sortName()
    }

    /**
     * 備考ソート
     */
    fun sortRemark(direction: Int) {
        beforeSort(direction)
        val trainList: Array<Train> = trains[direction]!!.toTypedArray()!!
        val sorter = TimeTableSorter(lineFile, trainList, direction)
        trains[direction] = sorter.sortRemark()
    }

    /**
     * 列車番号が同一のものを一本化します。
     * ２本の列車を総当たりで調べていき、列車番号、列車種別、終着駅と相手の始発駅が同じになる組み合わせが見つかれば一本化します。
     */
    fun combineByTrainNumber(direction: Int) {
        var i = 0
        while (i < trains[direction]!!.size) {
            val train1 = trains[direction]!![i]
            if (train1!!.isnull()) {
                i++
                continue
            }
            val endStation = train1.endStation
            for (j in trains[direction]!!.indices) {
                val train2 = trains[direction]!![j]
                if (train1.number == train2!!.number && train1.type == train2.type && i != j) {
                    if (lineFile.isSameStation(endStation, train2.startStation)) {
                        train1.conbine(train2)
                        trains[direction]!!.remove(train2)
                        i = if (j > i) {
                            i - 1
                        } else {
                            i - 2
                        }
                        break
                    }
                }
            }
            i++
        }
    }

    companion object {
        const val TIMETABLE_BACKCOLOR_NUM = 4
    }

    /**
     * 推奨コンストラクタ
     * @param diaFile 親ダイヤファイル
     */
    init {
        name = "新しいダイヤ"
        trains[0] = ArrayList()
        trains[1] = ArrayList()
    }
}