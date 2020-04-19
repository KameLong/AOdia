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
 * 時刻表中の１つの駅を表します。
 * 実路線構造と関係なく、時刻表上で異なる位置にある駅は別の駅となります。
 * 時刻表中に同一駅が複数現れた場合も、Stationオブジェクトを共通化せず、別々のオブジェクトとしてください。
 */
class Station(var lineFile: LineFile) : Cloneable {
    var stationID = ""

    /**
     * 駅名
     */
    @JvmField
    var name = ""
    var shortName = ""

    /**
     * 着時刻を表示するか
     */
    @JvmField
    var showArrival = booleanArrayOf(false, false)

    /**
     * 発時刻を表示するか
     */
    @JvmField
    var showDeparture = booleanArrayOf(true, true)

    /**
     * 発着番線を表示するか
     */
    @JvmField
    var showtrack = booleanArrayOf(false, false)

    /**
     * ダイヤグラム列車情報表示
     * 0:始発なら表示
     * 1:常に表示
     * 2:表示しない
     */
    var showDiagramInfo = intArrayOf(0, 0)
    var showDiagramTrack = false

    /**
     * 駅規模
     */
    @JvmField
    var bigStation = false

    //ダイヤグラム列車情報表示は未対応
    var tracks = ArrayList<StationTrack?>()

    /**
     * 境界線あり。
     *
     * この属性が true の駅では、時刻表画面の
     * 下り方向の下に、太い境界線を描画します。
     * この属性は、駅ビューと時刻表ビューに適用されます。
     * なおSecond1.02(FileType1.01)からは、分岐駅設定から
     * 境界線位置を特定するため、このパラメータは大きな意味を持ちません。
     * ただし、旧ファイル形式を読み込むとき、一度この値を設定してから、
     * 分岐駅設定の解読を行うため、残しておきます
     */
    var border = false

    /**
     * 主本線
     * 0以上、番線数未満を指定。
     * 列車に番線を設定する場合の、初期値になります。
     * Ver2.0 列車側の主本線指定撤廃により、-1が不要になります
     */
    @JvmField
    var stopMain = intArrayOf(0, 1)

    /**
     * 分岐駅設定の基幹駅駅Index
     * -1:分岐駅設定は無効
     * 0以上:基幹駅のStationIndex
     */
    @JvmField
    var brunchCoreStationIndex = -1

    /**
     * 分岐駅設定が有効な時、通常とは反対向きに合流しているとみなします。
     * 具体的には、下り着時刻駅から下方の駅に対し、分岐駅指定している場合、
     * 通常は[Y]型の路線を意味しますが、この値がtrueの場合、
     * [u]の右が下に伸びた感じの路線形状イメージになります。
     */
    var brunchOpposite = false

    /**
     * 環状線設定の起点駅駅Index
     * -1:環状線設定は無効
     * 0以上:起点駅のStationIndex
     */
    var loopOriginStationIndex = -1

    /**
     * 環状駅設定が有効な時、通常とは反対向きに合流しているとみなします。
     * この値が有効の時は環状線が○ではなく雫型の路線イメージになります。
     */
    var loopOpposite = false

    /**
     * この駅から繋がる路線外始発終着駅名
     * 列車の始発終着駅設定で、「路線外発着」を選択した時に、
     * この中から駅名を選択します。
     *
     */
    @JvmField
    var outerTerminals = ArrayList<OuterTerminal?>()

    /**
     * この駅から次の駅までの距離(秒)
     * デフォルト値は0で、0の場合は、「ダイヤグラムの既定の駅間幅」
     * を用います。
     * 値の範囲は0以上です。
     */
    var nextStationDistance = 0

    /**
     * 作業表示欄設定
     * この変数で指定された数だけ、時刻表のこの駅の欄に、
     * 下り前作業・上り後作業表示欄が設けられます。
     * 0-3の間で設定されます。
     * index=0 終点側
     * index=1 起点側
     */
    var stationOperationNum = intArrayOf(0, 0)

    /**
     * カスタマイズ時刻表ビューでの着時刻表示設定
     *
     * true:この駅で着時刻を表示します
     * false:この駅では着時刻を表示しません。
     */
    @JvmField
    var showArrivalCustom = booleanArrayOf(false, false)

    /**
     * 時刻表ビューでの発時刻表示設定
     *
     * true:この駅で発時刻を表示します
     * false:この駅では発時刻を表示しません。
     */
    @JvmField
    var showDepartureCustom = booleanArrayOf(true, true)

    /**
     * カスタマイズ時刻表ビューでの列車番号表示設定
     *
     * 2:この駅で全列車の列車番号を表示します
     * 1:この駅で種別変更を行った列車のみ列車番号を表示します
     * 0:この駅では列車番号を表示しません。
     */
    var showTrainNumberCustom = intArrayOf(0, 0)

    /**
     * カスタマイズ時刻表ビューでの運用番号表示設定
     *
     * 2:この駅で全列車の運用番号を表示します
     * 1:この駅で種別変更を行った列車のみ運用番号を表示します
     * 0:この駅では列車番号を表示しません。
     */
    var showTrainOperationCustom = intArrayOf(0, 0)

    /**
     * カスタマイズ時刻表ビューでの列車種別表示設定
     *
     * 2:この駅で全列車の列車種別を表示します
     * 1:この駅で種別変更を行った列車のみ列車種別を表示します
     * 0:この駅では列車番号を表示しません。
     */
    var showTrainTypeCustom = intArrayOf(0, 0)

    /**
     * カスタマイズ時刻表ビューでの列車名・号数表示設定
     *
     * 2:この駅で全列車の列車名・号数を表示します
     * 1:この駅で種別変更を行った列車のみ列車名・号数を表示します
     * 0:この駅では列車番号を表示しません。
     */
    var showTrainNameCustom = intArrayOf(0, 0)

    /**
     * 通常時刻表で番線を表示するか
     */
    var omitTrack = false

    /**
     * OuDiaファイル1行の情報を読み取ります
     */
    fun setValue(title: String?, value: String) {
        when (title) {
            "Ekimei" -> name = value
            "stationID" -> stationID = value
            "EkimeiJikokuRyaku" -> shortName = value
            "Ekijikokukeisiki" -> timeTableStyle = value
            "Ekikibo" -> bigStation = value == "Ekikibo_Syuyou"
            "DiagramRessyajouhouHyoujiKudari" -> setShowDiagramInfo(0, value)
            "DiagramRessyajouhouHyoujiNobori" -> setShowDiagramInfo(1, value)
            "DownMain" -> {
                stopMain[0] = value.toInt()
                if (lineFile.version.substring(lineFile.version.indexOf(".") + 1).toDouble() <= 1.06) {
                    stopMain[0]--
                }
            }
            "UpMain" -> {
                stopMain[1] = value.toInt()
                if (lineFile.version.substring(lineFile.version.indexOf(".") + 1).toDouble() <= 1.06) {
                    stopMain[1]--
                }
            }
            "BrunchCoreEkiIndex" -> brunchCoreStationIndex = Integer.valueOf(value)
            "BrunchOpposite" -> brunchOpposite = value == "1"
            "LoopOriginEkiIndex" -> loopOriginStationIndex = Integer.valueOf(value)
            "LoopOpposite" -> loopOpposite = value == "1"
            "JikokuhyouTrackDisplayKudari" -> showtrack[0] = value == "1"
            "JikokuhyouTrackDisplayNobori" -> showtrack[1] = value == "1"
            "DiagramTrackDisplay" -> showDiagramTrack = value == "1"
            "NextEkiDistance" -> nextStationDistance = Integer.valueOf(value)
            "JikokuhyouTrackOmit" -> omitTrack = value == "1"
            "JikokuhyouOperationOrigin" -> stationOperationNum[0] = value.toInt()
            "JikokuhyouOperationTerminal" -> stationOperationNum[1] = value.toInt()
            "JikokuhyouJikokuDisplayKudari" -> {
                showArrivalCustom[0] = value.split(",").toTypedArray()[0] == "1"
                showDepartureCustom[0] = value.split(",").toTypedArray()[1] == "1"
            }
            "JikokuhyouJikokuDisplayNobori" -> {
                showArrivalCustom[1] = value.split(",").toTypedArray()[0] == "1"
                showDepartureCustom[1] = value.split(",").toTypedArray()[1] == "1"
            }
            "JikokuhyouSyubetsuChangeDisplayKudari" -> {
                showTrainNumberCustom[0] = value.split(",").toTypedArray()[0].toInt()
                showTrainOperationCustom[0] = value.split(",").toTypedArray()[1].toInt()
                showTrainTypeCustom[0] = value.split(",").toTypedArray()[2].toInt()
                showTrainNameCustom[0] = value.split(",").toTypedArray()[3].toInt()
            }
            "JikokuhyouSyubetsuChangeDisplayNobori" -> {
                showTrainNumberCustom[1] = value.split(",").toTypedArray()[0].toInt()
                showTrainOperationCustom[1] = value.split(",").toTypedArray()[1].toInt()
                showTrainTypeCustom[1] = value.split(",").toTypedArray()[2].toInt()
                showTrainNameCustom[1] = value.split(",").toTypedArray()[3].toInt()
            }
            "Kyoukaisen" -> border = value == "1"
        }
    }

    /**
     * 発着表示からOuDia2ndファイルのJikokukeisikiを求める
     */
    /**
     * oudiaファイルの文字列形式からtimetableStyleを読み込みます
     * @param str
     */
    private var timeTableStyle: String
        private get() {
            var result = 0
            if (showArrival[1]) {
                result += 8
            }
            if (showDeparture[1]) {
                result += 4
            }
            if (showArrival[0]) {
                result += 2
            }
            if (showDeparture[0]) {
                result += 1
            }
            return when (result) {
                5 -> "Jikokukeisiki_Hatsu"
                15 -> "Jikokukeisiki_Hatsuchaku"
                6 -> "Jikokukeisiki_KudariChaku"
                9 -> "Jikokukeisiki_NoboriChaku"
                13 -> "Jikokukeisiki_NoboriHatsuChaku"
                7 -> "Jikokukeisiki_KudariHatsuChaku"
                else -> "Jikokukeisiki_Hatsu"
            }
        }
        private set(str) {
            when (str) {
                "Jikokukeisiki_Hatsu" -> {
                    showArrival[0] = false
                    showArrival[1] = false
                    showDeparture[0] = true
                    showDeparture[1] = true
                    showArrivalCustom[0] = false
                    showArrivalCustom[1] = false
                    showDepartureCustom[0] = true
                    showDepartureCustom[1] = true
                }
                "Jikokukeisiki_Hatsuchaku" -> {
                    showArrival[0] = true
                    showArrival[1] = true
                    showDeparture[0] = true
                    showDeparture[1] = true
                    showArrivalCustom[0] = true
                    showArrivalCustom[1] = true
                    showDepartureCustom[0] = true
                    showDepartureCustom[1] = true
                }
                "Jikokukeisiki_NoboriChaku" -> {
                    showArrival[0] = false
                    showArrival[1] = true
                    showDeparture[0] = true
                    showDeparture[1] = false
                    showArrivalCustom[0] = false
                    showArrivalCustom[1] = true
                    showDepartureCustom[0] = true
                    showDepartureCustom[1] = false
                }
                "Jikokukeisiki_KudariChaku" -> {
                    showArrival[0] = true
                    showArrival[1] = false
                    showDeparture[0] = false
                    showDeparture[1] = true
                    showArrivalCustom[0] = true
                    showArrivalCustom[1] = false
                    showDepartureCustom[0] = false
                    showDepartureCustom[1] = true
                }
                "Jikokukeisiki_NoboriHatsuChaku" -> {
                    showArrival[0] = false
                    showArrival[1] = true
                    showDeparture[0] = true
                    showDeparture[1] = true
                    showArrivalCustom[0] = false
                    showArrivalCustom[1] = true
                    showDepartureCustom[0] = true
                    showDepartureCustom[1] = true
                }
                "Jikokukeisiki_KudariHatsuChaku" -> {
                    showArrival[0] = true
                    showArrival[1] = true
                    showDeparture[0] = false
                    showDeparture[1] = true
                    showArrivalCustom[0] = true
                    showArrivalCustom[1] = true
                    showDepartureCustom[0] = false
                    showDepartureCustom[1] = true
                }
                else -> {
                    showArrival[0] = false
                    showArrival[1] = false
                    showDeparture[0] = true
                    showDeparture[1] = true
                    showArrivalCustom[0] = false
                    showArrivalCustom[1] = false
                    showDepartureCustom[0] = true
                    showDepartureCustom[1] = true
                }
            }
        }

    /**
     * 発着表示からOuDiaファイルのJikokukeisikiを求める
     */
    private val timeTableStyleOuDia: String
        private get() {
            var result = 0
            if (showArrivalCustom[1]) {
                result += 8
            }
            if (showDepartureCustom[1]) {
                result += 4
            }
            if (showArrivalCustom[0]) {
                result += 2
            }
            if (showDepartureCustom[0]) {
                result += 1
            }
            return when (result) {
                5 -> "Jikokukeisiki_Hatsu"
                15 -> "Jikokukeisiki_Hatsuchaku"
                6 -> "Jikokukeisiki_KudariChaku"
                9 -> "Jikokukeisiki_NoboriChaku"
                else -> "Jikokukeisiki_Hatsu"
            }
        }

    /**
     * oudiaファイルの文字列形式からDiagramRessyajouhouHyoujiを読み込みます
     * @param value
     */
    private fun setShowDiagramInfo(direction: Int, value: String) {
        when (value) {
            "DiagramRessyajouhouHyouji_Anytime" -> showDiagramInfo[direction] = 1
            "DiagramRessyajouhouHyouji_Not" -> showDiagramInfo[direction] = 2
        }
    }

    /**
     * OuDia2ndで廃止されたborder変数ですが、
     * 2ndの形式からborder情報を復元します
     * @return
     */
    fun getBorder(): Boolean {
        if (border) return true
        if (brunchCoreStationIndex != -1 && brunchCoreStationIndex > lineFile.station.indexOf(this)) return true
        val stationIndex = lineFile.station.indexOf(this)
        if (stationIndex < lineFile.stationNum - 1) {
            val b = lineFile.station[stationIndex + 1]!!.brunchCoreStationIndex
            return b >= 0 && b < stationIndex
        }
        return false
    }

    /**
     * @return 番線数
     */
    val trackNum: Int
        get() = tracks.size

    /**
     * @return 番線名
     */
    fun getTrackName(trackIndex: Int): String? {
        return tracks[trackIndex]!!.trackName
    }

    /**
     * @return 番線略称
     */
    fun getTrackShortName(trackIndex: Int): String? {
        if (trackIndex < 0 || trackIndex >= tracks.size) {
            return ""
        }
        val result = tracks[trackIndex]!!.trackShortName
        return if (result == null || result.length == 0) {
            tracks[trackIndex]!!.trackName
        } else result
    }

    /**
     * oudia2nd形式で保存します
     */
    fun saveToFile(out: PrintWriter) {
        out.println("Eki.")
        out.println("Ekimei=$name")
        out.println("stationID=$stationID")
        if (shortName.length != 0) {
            out.println("EkimeiJikokuRyaku=$shortName")
        }
        out.println("Ekijikokukeisiki=$timeTableStyle")
        if (bigStation) {
            out.println("Ekikibo=Ekikibo_Syuyou")
        } else {
            out.println("Ekikibo=Ekikibo_Ippan")
        }
        when (showDiagramInfo[0]) {
            1 -> out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Anytime")
            2 -> out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Not")
        }
        when (showDiagramInfo[1]) {
            1 -> out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Anytime")
            2 -> out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Not")
        }
        out.println("DownMain=" + stopMain[0])
        out.println("UpMain=" + stopMain[1])
        out.println("EkiTrack2Cont.")
        for (i in tracks.indices) {
            tracks[i]!!.saveToFile(out)
        }
        out.println(".")
        for (i in outerTerminals.indices) {
            outerTerminals[i]!!.saveToFile(out)
        }
        if (showDiagramTrack) {
            out.println("DiagramTrackDisplay=1")
        }
        if (brunchCoreStationIndex >= 0) {
            out.println("BrunchCoreEkiIndex=$brunchCoreStationIndex")
        }
        if (brunchOpposite) {
            out.println("BrunchOpposite=1")
        }
        if (loopOriginStationIndex >= 0) {
            out.println("LoopOriginEkiIndex=$loopOriginStationIndex")
        }
        if (loopOpposite) {
            out.println("LoopOpposite=1")
        }
        if (showtrack[0]) {
            out.println("JikokuhyouTrackDisplayKudari=1")
        }
        if (showtrack[1]) {
            out.println("JikokuhyouTrackDisplayNobori=1")
        }
        if (showDiagramTrack) {
            out.println("DiagramTrackDisplay=1")
        }
        if (nextStationDistance > 0) {
            out.println("NextEkiDistance=$nextStationDistance")
        }
        if (omitTrack) {
            out.println("JikokuhyouTrackOmit=1")
        }
        if (stationOperationNum[0] > 0) {
            out.println("JikokuhyouOperationOrigin=" + stationOperationNum[0])
        }
        if (stationOperationNum[1] > 0) {
            out.println("JikokuhyouOperationTerminal=" + stationOperationNum[1])
        }
        out.println("JikokuhyouJikokuDisplayKudari=" + boolean2String(showArrivalCustom[0]) + "," + boolean2String(showDepartureCustom[0]))
        out.println("JikokuhyouJikokuDisplayNobori=" + boolean2String(showArrivalCustom[1]) + "," + boolean2String(showDepartureCustom[1]))
        out.println("JikokuhyouSyubetsuChangeDisplayKudari=" + showTrainNumberCustom[0] + "," + showTrainOperationCustom[0] + "," + showTrainTypeCustom[0] + "," + showTrainNameCustom[0])
        out.println("JikokuhyouSyubetsuChangeDisplayNobori=" + showTrainNumberCustom[1] + "," + showTrainOperationCustom[1] + "," + showTrainTypeCustom[1] + "," + showTrainNameCustom[1])
        out.println(".")
    }

    /**
     * oudia形式で保存します
     */
    fun saveToOuDiaFile(out: PrintWriter) {
        out.println("Eki.")
        out.println("Ekimei=$name")
        out.println("stationID=$stationID")
        out.println("Ekijikokukeisiki=$timeTableStyleOuDia")
        if (bigStation) {
            out.println("Ekikibo=Ekikibo_Syuyou")
        } else {
            out.println("Ekikibo=Ekikibo_Ippan")
        }
        if (getBorder()) {
            out.println("Kyoukaisen=1")
        }
        when (showDiagramInfo[0]) {
            1 -> out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Anytime")
            2 -> out.println("DiagramRessyajouhouHyoujiKudari=DiagramRessyajouhouHyouji_Not")
        }
        when (showDiagramInfo[1]) {
            1 -> out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Anytime")
            2 -> out.println("DiagramRessyajouhouHyoujiNobori=DiagramRessyajouhouHyouji_Not")
        }
        out.println(".")
    }

    /**
     * booleanが真の時に1、偽の時に0を返す
     */
    private fun boolean2String(value: Boolean): String {
        return if (value) {
            "1"
        } else {
            "0"
        }
    }

    /**
     * 駅を複製します
     * @param lineFile 複製した駅の親LineFile
     * @return 複製した駅
     */
    fun clone(lineFile: LineFile): Station {
        return try {
            val result = super.clone() as Station
            result.lineFile = lineFile
            result.showArrival = showArrival.clone()
            result.showArrivalCustom = showArrivalCustom.clone()
            result.showDeparture = showDeparture.clone()
            result.showDepartureCustom = showDepartureCustom.clone()
            result.showDiagramInfo = showDiagramInfo.clone()
            result.showtrack = showtrack.clone()
            result.showTrainNameCustom = showTrainNameCustom.clone()
            result.showTrainNumberCustom = showTrainNumberCustom.clone()
            result.showTrainOperationCustom = showTrainOperationCustom.clone()
            result.showTrainTypeCustom = showTrainTypeCustom.clone()
            result.stationOperationNum = stationOperationNum.clone()
            result.stopMain = stopMain.clone()
            result.outerTerminals = ArrayList()
            for (terminal in outerTerminals) {
                result.outerTerminals.add(terminal!!.clone())
            }
            result.tracks = ArrayList()
            for (track in tracks) {
                result.tracks.add(track!!.clone())
            }
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            Station(lineFile)
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
    /**
     * 着時刻を表示するか
     * Custom時刻表を基準にします
     */
    fun showAriTime(direction: Int): Boolean {
        return showArrivalCustom[direction]
    }

    /**
     * 発着番線を表示するか
     * Custom時刻表を基準にします
     */
    fun showTrack(direction: Int): Boolean {
        return showtrack[direction]
    }

    /**
     * 発時刻を表示するか
     * Custom時刻表を基準にします
     */
    fun showDepTime(direction: Int): Boolean {
        return showDepartureCustom[direction]
    }

    /**
     * 路線外駅名を返します
     * @return
     */
    fun getOuterStationTimeTableName(index: Int): String? {
        return try {
            if (outerTerminals[index]!!.outerTerminalTimeTableName.length != 0) {
                outerTerminals[index]!!.outerTerminalTimeTableName
            } else outerTerminals[index]!!.outerTerminalName
        } catch (e: Exception) {
            SDlog.log(e)
            null
        }
    }

    /**
     * 番線名を入力します
     */
    fun setTrackName(index: Int, value: String?) {
        if (index < 0 || index >= trackNum) {
            return
        }
        tracks[index]!!.trackName = value!!
    }

    /**
     * 番線略称を入力します
     */
    fun setTrackShortName(index: Int, value: String?) {
        if (index < 0 || index >= trackNum) {
            return
        }
        tracks[index]!!.trackShortName = value!!
    }

    /**
     * 発着番線を追加します
     */
    fun addTrack(track: StationTrack?) {
        tracks.add(track)
    }

    /**
     * 発着番線を削除します。
     * 主発着番線に指定されている場合　削除せずfalseを返す
     */
    fun deleteTrack(index: Int): Boolean {
        if (stopMain[Train.Companion.DOWN] == index) {
            return false
        }
        if (stopMain[Train.Companion.UP] == index) {
            return false
        }
        if (stopMain[Train.Companion.DOWN] > index) {
            stopMain[Train.Companion.DOWN]--
        }
        if (stopMain[Train.Companion.UP] > index) {
            stopMain[Train.Companion.UP]--
        }
        val stationIndex = lineFile.station.indexOf(this)
        for (dia in lineFile.diagram) {
            for (train in dia.trains[0]!!) {
                if (train!!.getStopTrack(stationIndex) == index) {
                    train.setStopTrack(stationIndex, -1)
                }
                if (train.getStopTrack(stationIndex) > index) {
                    train.setStopTrack(stationIndex, train.getStopTrack(stationIndex) - 1)
                }
            }
            for (train in dia.trains[1]!!) {
                if (train!!.getStopTrack(stationIndex) == index) {
                    train.setStopTrack(stationIndex, -1)
                }
                if (train.getStopTrack(stationIndex) > index) {
                    train.setStopTrack(stationIndex, train.getStopTrack(stationIndex) - 1)
                }
            }
        }
        tracks.removeAt(index)
        return true
    }

    /**
     * 路線外始終着駅を追加します
     * @param terminal
     */
    fun addOuterTerminal(terminal: OuterTerminal?) {
        outerTerminals.add(terminal)
    }

    /**
     * 路線外始終着駅を削除します
     * 列車が使用している場合　false
     * 削除に成功した場合　true
     */
    fun deleteOuterTerminal(terminal: OuterTerminal?): Boolean {
        val index = outerTerminals.indexOf(terminal)
        return if (index < outerTerminals.size && index >= 0) {
            deleteOuterTerminal(index)
        } else true
    }

    fun deleteOuterTerminal(index: Int): Boolean {
        val stationIndex = lineFile.station.indexOf(this)
        for (dia in lineFile.diagram) {
            for (direction in 0..1) {
                for (train in dia.trains[direction]!!) {
                    val sTime = train!!.stationTimes[stationIndex]
                    for (ope in sTime!!.beforeOperations) {
                        if (ope!!.operationType == 4 && ope.intData1 == index) {
                            return false
                        }
                    }
                    for (ope in sTime.afterOperations) {
                        if (ope!!.operationType == 4 && ope.intData1 == index) {
                            return false
                        }
                    }
                }
            }
        }
        for (dia in lineFile.diagram) {
            for (direction in 0..1) {
                for (train in dia.trains[direction]!!) {
                    val sTime = train!!.stationTimes[stationIndex]
                    for (ope in sTime!!.beforeOperations) {
                        if (ope!!.operationType == 4 && ope.intData1 > index) {
                            ope.intData1--
                        }
                    }
                    for (ope in sTime.afterOperations) {
                        if (ope!!.operationType == 4 && ope.intData1 > index) {
                            ope.intData1--
                        }
                    }
                }
            }
        }
        outerTerminals.removeAt(index)
        return true
    }

    /**
     * 路線が逆転されることに従い、この駅の発着時刻表示情報なども反転されます
     */
    fun reverse() {
        reverse(showArrival)
        reverse(showArrivalCustom)
        reverse(showDeparture)
        reverse(showDepartureCustom)
        reverse(showDiagramInfo)
        reverse(showtrack)
        reverse(showTrainNameCustom)
        reverse(showTrainNumberCustom)
        reverse(showTrainOperationCustom)
        reverse(showTrainTypeCustom)
        reverse(stationOperationNum)
        reverse(stopMain)
        //分岐駅情報も反転対象
        if (brunchCoreStationIndex >= 0) {
            brunchCoreStationIndex = lineFile.stationNum - brunchCoreStationIndex - 1
        }
        if (loopOriginStationIndex >= 0) {
            loopOriginStationIndex = lineFile.stationNum - loopOriginStationIndex - 1
        }
    }

    private fun reverse(task: IntArray) {
        val temp = task[0]
        task[0] = task[1]
        task[1] = temp
    }

    companion object {
        /**
         * オブジェクトを逆転させる
         * @param task
         * @param <T>
        </T> */
        private fun <T> reverse(task: Array<T>) {
            val temp = task[0]
            task[0] = task[1]
            task[1] = temp
        }

        /**
         * boolean  型の配列を逆転させる
         * @param task
         */
        private fun reverse(task: BooleanArray) {
            val temp = task[0]
            task[0] = task[1]
            task[1] = temp
        }
    }

    init {
        name = "新規作成"
    }
}