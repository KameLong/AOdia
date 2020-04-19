package com.kamelong.OuDia

import com.kamelong2.aodia.SDlog
import java.util.*

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 時刻表並び替え作業を行う作業クラスです。
 * 時刻表並び替えが終了したらこのオブジェクトを破棄してください。
 */
class TimeTableSorter(var lineFile: LineFile, var trainList: Array<Train>, var direction: Int) {
    private val sortBefore: ArrayList<Int>
    private val sortAfter: ArrayList<Int>
    var sorted: BooleanArray
    var loopNum = 0

    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     */
    fun sortNumber(): ArrayList<Train> {
        val sorter = ArrayList<TrainNumberSorter>()
        for (train in trainList) {
            sorter.add(TrainNumberSorter(train))
        }
        Collections.sort(sorter)
        val result = ArrayList<Train>()
        for (sort in sorter) {
            result.add(sort.train)
        }
        return result
    }

    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    fun sortType(): ArrayList<Train> {
        val sorter = ArrayList<TrainTypeSorter>()
        for (train in trainList) {
            sorter.add(TrainTypeSorter(train))
        }
        Collections.sort(sorter)
        val result = ArrayList<Train>()
        for (sort in sorter) {
            result.add(sort.train)
        }
        return result
    }

    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    fun sortName(): ArrayList<Train> {
        val sorter = ArrayList<TrainNameSorter>()
        for (train in trainList) {
            sorter.add(TrainNameSorter(train))
        }
        Collections.sort(sorter)
        val result = ArrayList<Train>()
        for (sort in sorter) {
            result.add(sort.train)
        }
        return result
    }

    /**
     * 列車番号を(数字)+(アルファベットなど)と分離する
     * まずはアルファベットでソートして、その後数字でソートする
     * @return
     */
    fun sortRemark(): ArrayList<Train> {
        val sorter = ArrayList<TrainRemarkSorter>()
        for (train in trainList) {
            sorter.add(TrainRemarkSorter(train))
        }
        Collections.sort(sorter)
        val result = ArrayList<Train>()
        for (sort in sorter) {
            result.add(sort.train)
        }
        return result
    }

    /**
     * 列車時刻でソートします。
     * @param stationIndex ソート基準時刻
     */
    fun sort(stationIndex: Int): ArrayList<Train> {
        var result = ArrayList<Train>()
        try {
            loopNum = 0
            var i = 0
            while (i < sortBefore.size) {
                if (trainList[sortBefore[i]].getPredictionTime(stationIndex) > 0 && !trainList[sortBefore[i]].checkDoubleDay()) {
                    //今からsortAfterに追加する列車の基準駅の時間
                    val baseTime = trainList[sortBefore[i]].getPredictionTime(stationIndex)
                    var j: Int
                    j = sortAfter.size
                    while (j > 0) {
                        if (trainList[sortAfter[j - 1]].getPredictionTime(stationIndex) < baseTime) {
                            break
                        }
                        j--
                    }
                    sortAfter.add(j, sortBefore[i])
                    sortBefore.removeAt(i)
                    i--
                }
                i++
            }
            sorted[stationIndex] = true
            if (direction == Train.Companion.DOWN) {
                sortDown(stationIndex)
            } else {
                sortUp(stationIndex)
            }
            sortAfter.addAll(sortBefore)
            for (i in sortAfter) {
                result.add(trainList[i])
            }
            return result
        } catch (e: Exception) {
            SDlog.log(e)
        }
        result = ArrayList()
        for (i in sortBefore) {
            result.add(trainList[i])
        }
        return result
    }

    /**
     * 路線内を上り方向に探索していきます。
     */
    @Throws(Exception::class)
    fun sortUp(stationIndex: Int) {
        var stationIndex = stationIndex
        loopNum++
        if (loopNum > 50) {
            SDlog.toast("エラーこのダイヤファイルの路線分岐が複雑であるため、列車の並び替え時に無限ループに陥りました。並び替え操作を強制終了します")
            throw Exception("並び替えエラー：" + lineFile.name)
        }
        var skip = true //ソート済みの路線から外れ、別の路線に入る場合skipfragがtrueになる。//ソート済み領域に戻ればskip=false
        while (stationIndex >= 0) {

            //上り方向に探索
            if (sorted[stationIndex]) {
                skip = false
            }
            if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                //この駅が分岐駅設定されている場合
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex > stationIndex) {
                    //下から分岐する場合はソート対象外
                    skip = true
                }
            }
            if (!sorted[stationIndex]) {
                //この駅がまだソートされていないとき
                if (skip) {
                    var subskip = true
                    //この駅がスキップされるとき
                    if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                        //分岐駅設定あり
                        if (sorted[lineFile.getStation(stationIndex)!!.brunchCoreStationIndex]) {
                            //分岐元がソート済み
                            //ソートする
                            subskip = false
                        }
                    }
                    //この駅がどこかの分岐元でその駅がソートされている可能性
                    for (i in 0 until lineFile.stationNum) {
                        if (lineFile.getStation(i)!!.brunchCoreStationIndex == stationIndex && sorted[i]) {
                            subskip = false
                        }
                    }
                    if (subskip) {
                        //スキップする
                        stationIndex--
                        continue
                    }
                }
                //ソートする
                println("sortUp:\t" + stationIndex + "\t(" + lineFile.getStation(stationIndex)!!.name + ")")
                val stations = ArrayList<Int>()
                stations.add(stationIndex)
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                    stations.add(lineFile.getStation(stationIndex)!!.brunchCoreStationIndex)
                }
                for (i in 0 until lineFile.stationNum) {
                    if (lineFile.getStation(i)!!.brunchCoreStationIndex == stationIndex) {
                        stations.add(i)
                    }
                }
                if (direction == Train.Companion.DOWN) {
                    addTrainInSort1(stations)
                } else {
                    addTrainInSort2(stations)
                }
                sorted[stationIndex] = true
                skip = false
            }
            if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                //この駅が分岐駅設定されている場合
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex < stationIndex) {
                    //上へ分岐するときは次の駅からソート対象外
                    skip = true
                }
            }
            stationIndex--
        }

        //ループが終わったのに、既にソート済みの駅が最後に残った場合や最後スキップされていた場合
        for (i in 0 until lineFile.stationNum) {
            if (!sorted[i]) {
                sortDown(0)
                return
            }
        }
    }

    /**
     * 路線内を下り方向に探索していきます。
     */
    @Throws(Exception::class)
    fun sortDown(stationIndex: Int) {
        var stationIndex = stationIndex
        loopNum++
        if (loopNum > 50) {
            SDlog.toast("エラーこのダイヤファイルの路線分岐が複雑であるため、列車の並び替え時に無限ループに陥りました。並び替え操作を強制終了します")
            throw Exception("並び替えエラー：" + lineFile.name)
        }
        var skip = true //ソート済みの路線から外れ、別の路線に入る場合skipfragがtrueになる。//ソート済み領域に戻ればskip=false
        while (stationIndex < lineFile.stationNum) {

            //下り方向に探索
            if (sorted[stationIndex]) {
                skip = false
            }
            if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                //この駅が分岐駅設定されている場合
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex < stationIndex) {
                    //上から分岐する場合はソート対象外
                    skip = true
                }
            }
            if (!sorted[stationIndex]) {
                //この駅がまだソートされていないとき
                if (skip) {
                    var subskip = true
                    //この駅がスキップされるとき
                    if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                        //分岐駅設定あり
                        if (sorted[lineFile.getStation(stationIndex)!!.brunchCoreStationIndex]) {
                            //分岐元がソート済み
                            //ソートする
                            subskip = false
                        }
                    }
                    //この駅がどこかの分岐元でその駅がソートされている可能性
                    for (i in 0 until lineFile.stationNum) {
                        if (lineFile.getStation(i)!!.brunchCoreStationIndex == stationIndex && sorted[i]) {
                            subskip = false
                        }
                    }
                    if (subskip) {
                        //スキップする
                        stationIndex++
                        continue
                    }
                }
                //ソートする
                println("sortDown:\t" + stationIndex + "\t(" + lineFile.getStation(stationIndex)!!.name + ")")
                val stations = ArrayList<Int>()
                stations.add(stationIndex)
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                    stations.add(lineFile.getStation(stationIndex)!!.brunchCoreStationIndex)
                }
                for (i in 0 until lineFile.stationNum) {
                    if (lineFile.getStation(i)!!.brunchCoreStationIndex == stationIndex) {
                        stations.add(i)
                    }
                }
                if (direction == Train.Companion.DOWN) {
                    addTrainInSort2(stations)
                } else {
                    addTrainInSort1(stations)
                }
                sorted[stationIndex] = true
                skip = false
            }
            if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex >= 0) {
                //この駅が分岐駅設定されている場合
                if (lineFile.getStation(stationIndex)!!.brunchCoreStationIndex > stationIndex) {
                    //下へ分岐するときは次の駅からソート対象外
                    skip = true
                }
            }
            stationIndex++
        }
        //ループが終わったのに、既にソート済みの駅が最後に残った場合や最後スキップされていた場合
        for (i in 0 until lineFile.stationNum) {
            if (!sorted[i]) {
                sortUp(lineFile.stationNum - 1)
                break
            }
        }
    }

    /**
     * 列車をsortAfterに時刻前方から挿入する
     * station[0]に停車する列車がソート対象
     * station[1以上]は同一駅
     */
    private fun addTrainInSort1(station: ArrayList<Int>) {
        for (i in sortBefore.size downTo 1) {
            val baseTime = trainList[sortBefore[i - 1]].getTime(station[0], Train.Companion.ARRIVE, true)
            if (baseTime < 0 || trainList[sortBefore[i - 1]].checkDoubleDay()) {
                continue
            }
            var j = 0
            var frag = false
            j = 0
            while (j < sortAfter.size) {
                var sortTime = -1
                for (s in station) {
                    sortTime = Math.max(sortTime, trainList[sortAfter[j]].getPredictionTime(s, Train.Companion.DEPART))
                }
                if (sortTime < 0) {
                    j++
                    continue
                }
                frag = true
                if (sortTime >= baseTime) {
                    break
                }
                j++
            }
            if (frag) {
                sortAfter.add(j, sortBefore[i - 1])
                sortBefore.removeAt(i - 1)
            }
        }
    }

    private fun addTrainInSort2(station: ArrayList<Int>) {
        var i = 0
        while (i < sortBefore.size) {
            val baseTime = trainList[sortBefore[i]].getDepTime(station[0])
            if (baseTime < 0 || trainList[sortBefore[i]].checkDoubleDay()) {
                i++
                continue
            }
            var j = 0
            var frag = false
            j = sortAfter.size - 1
            while (j >= 0) {
                var sortTime = -1
                for (s in station) {
                    sortTime = Math.max(sortTime, trainList[sortAfter[j]].getPredictionTime(s, Train.Companion.ARRIVE))
                }
                if (sortTime < 0) {
                    j--
                    continue
                }
                frag = true
                if (sortTime <= baseTime) {
                    break
                }
                j--
            }
            if (frag) {
                sortAfter.add(j + 1, sortBefore[i])
                sortBefore.removeAt(i)
                i--
            }
            i++
        }
    }

    internal inner class TrainNumberSorter(var train: Train) : Comparable<TrainNumberSorter> {
        var name = ""
        var number = 0
        fun isNumber(c: Char): Boolean {
            return c >= '0' && c <= '9'
        }

        override fun compareTo(o: TrainNumberSorter): Int {
            return if (name == o.name) {
                number - o.number
            } else name.compareTo(o.name)
        }

        init {
            val value = train.number
            for (i in 0 until value!!.length) {
                if (!isNumber(value.toCharArray()[i])) {
                    if (i != 0) {
                        number = value.substring(0, i).toInt()
                    }
                    name = value.substring(i)
                    return
                }
            }
            if (value.length != 0) {
                number = value.toInt()
            }
        }
    }

    internal inner class TrainTypeSorter(var train: Train) : Comparable<TrainTypeSorter> {
        override fun compareTo(o: TrainTypeSorter): Int {
            return train.type - o.train.type
        }

    }

    internal inner class TrainNameSorter(var train: Train) : Comparable<TrainNameSorter> {
        override fun compareTo(o: TrainNameSorter): Int {
            return if (train.name == o.train.name) {
                train.count.compareTo(o.train.count)
            } else train.name.compareTo(o.train.name)
        }

    }

    internal inner class TrainRemarkSorter(var train: Train) : Comparable<TrainRemarkSorter> {
        override fun compareTo(o: TrainRemarkSorter): Int {
            return train.remark.compareTo(o.train.remark)
        }

    }

    /**
     * ソートする時刻表を入力します。
     */
    init {
        sortBefore = ArrayList()
        sortAfter = ArrayList()
        for (i in trainList.indices) {
            sortBefore.add(i)
        }
        sorted = BooleanArray(lineFile.stationNum)
        for (i in sorted.indices) {
            sorted[i] = false
        }
    }
}