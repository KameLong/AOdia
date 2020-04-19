package com.kamelong.OuDia

import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.tool.Color
import com.kamelong.tool.Font
import com.kamelong.tool.SDlog
import com.kamelong.tool.ShiftJISBufferedReader
import java.io.*
import java.util.*

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 一つの路線ファイルを表す。
 */
class LineFile : Cloneable {
    /**
     * AOdia専用　routeID
     */
    private var routeID = ""

    /**
     * このファイルが保存されていたパス
     */
    var filePath = ""

    /**
     * 路線名
     */
    var name = ""

    /**
     * ダイヤグラム開始時間
     */
    var diagramStartTime = 3600 * 3

    /**
     * ダイヤグラムの既定の駅間幅。
     *
     * 列車設定のない駅間の、ダイヤグラムビュー上での
     * 縦方向の幅を『ダイヤグラムエンティティY座標』単位(秒)で指定します。
     *
     * 既定値は 60 です。
     */
    var stationSpaceDefault = 60

    /**
     * コメント
     */
    var comment = ""

    /**
     * 運用機能の有効無効
     * 路線ファイルのプロパティで変更できます
     * 0:運用機能は無効です。
     * 1:運用機能は簡易モードです。
     * 列車の接続のみを行い、運用番号は設定しません。
     * 2:運用機能は通常モードです
     * 規定値は0です
     */
    var operationStyle = 0

    /**
     * ダイヤの別名です。
     * （例） "北行" など
     */
    var diaNameDefault = arrayOf("下り時刻表", "上り時刻表")

    /**
     * 駅一覧
     */
    @JvmField
    var station = ArrayList<Station?>()

    /**
     * TrainType取得
     */
    /**
     * 列車種別一覧
     */
    @JvmField
    var trainType = ArrayList<TrainType>()

    /**
     * ダイヤ一覧
     */
    @JvmField
    var diagram = ArrayList<Diagram>()

    /**
     * OuDiaバージョン
     */
    @JvmField
    var version = ""
    //DispProp
    /**
     * 時刻表フォント
     */
    var timeTableFont = ArrayList<Font>()

    /**
     * 時刻表Vフォント
     * 縦書きに使う？
     */
    var timeTableVFont = Font.OUDIA_DEFAULT

    /**
     * ダイヤ駅名フォント
     */
    var diaStationNameFont = Font.OUDIA_DEFAULT

    /**
     * ダイヤ時刻フォント
     */
    var diaTimeFont = Font.OUDIA_DEFAULT

    /**
     * ダイヤ列車フォント
     */
    var diaTrainFont = Font.OUDIA_DEFAULT

    /**
     * コメントフォント
     */
    var commentFont = Font.OUDIA_DEFAULT

    /**
     * ダイヤ文字色
     */
    var diaTextColor = Color("#000000")

    /**
     * ダイヤ背景色
     */
    var diaBackColor = Color("#FFFFFF")

    /**
     * ダイヤ列車色
     */
    var diaTrainColor = Color("#000000")

    /**
     * ダイヤ軸色
     */
    var diaAxicsColor = Color("C0C0C0")

    /**
     * 時刻表背景色
     */
    var timeTableBackColor = ArrayList<Color>()

    /**
     * StdOpeTimeLowerColor
     */
    var stdOpeTimeLowerColor = Color()

    /**
     * StdOpeTimeHigherColor
     */
    var stdOpeTimeHigherColor = Color()

    /**
     * StdOpeTimeUndefColor
     */
    var stdOpeTimeUndefColor = Color()

    /**
     * StdOpeTimeIllegalColor
     */
    var stdOpeTimeIllegalColor = Color()

    /**
     * 駅名の長さ
     */
    var stationNameWidth = 6

    /**
     * 時刻用列車幅
     */
    var trainWidth = 5

    /**
     * 秒単位移動量
     * 二つある
     */
    @JvmField
    var secondShift = intArrayOf(10, 15)

    /**
     * 列車名欄表示
     */
    var showTrainName = true

    /**
     * 路線外終着駅を起点側に表示する
     */
    var showOuterTerminalOrigin = false

    /**
     * 路線外終着駅を終点側に表示する
     */
    var showOuterTerminalTerminal = false

    /**
     * 路線外終着駅を表示するか
     */
    var showOuterTerminal = false

    /**
     * デフォルトコンストラクタ
     */
    constructor() {}

    fun getRouteID(): String {
        val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
        if (fileName.contains("-")) {
            val routeID = fileName.split("-").toTypedArray()[0]
            if (routeID.length == 5) {
                try {
                    val a = routeID.toInt()
                    return routeID
                } catch (e: Exception) {
                }
            }
        }
        try {
            val fileNameNumber = fileName.substring(0, fileName.indexOf(".")).toInt()
            if (fileNameNumber > 10000 && fileNameNumber < 100000) {
                return fileNameNumber.toString() + ""
            }
        } catch (e: Exception) {
        }
        return ""
    }

    fun setRouteID(database: KLdetabase) {
        if (routeID.length == 0) {
            routeID = getRouteID()
            if (routeID.length > 0) {
                try {
                    val routeNumber = routeID.toInt()
                    for (i in 0 until stationNum) {
                        getStation(i)!!.stationID = database.getRouteStation((routeNumber * 100 + i).toString() + "").stationID
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    /**
     * ファイルからダイヤを開く
     * @param file　入力ファイル
     * @throws Exception ファイルが読み込めなかった時に返す
     */
    constructor(file: File) {
        filePath = file.path
        val br = BufferedReader(InputStreamReader(FileInputStream(file)))
        version = br.readLine().split("=").dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        var v = 1.02
        try {
            v = version.substring(version.indexOf(".") + 1).toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (version.startsWith("OuDia.") || v < 1.03) {
            //Shift-Jisのファイル
            loadShiftJis(file)
        } else {
            //utf-8のファイル
            loadDiaFile(br)
        }
        checkBorderStation()
        println("読み込み終了")
    }

    /**
     * Shift-JISで書かれたファイルを読み込む
     */
    @Throws(Exception::class)
    private fun loadShiftJis(file: File) {
        val br: BufferedReader = ShiftJISBufferedReader(InputStreamReader(FileInputStream(file), "Shift-JIS"))
        version = br.readLine().split("=").dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        loadDiaFile(br)
    }

    /**
     * ダイヤファイルを読み込む
     * @param br 入力ファイル
     * @throws Exception 読み込み失敗
     */
    @Throws(Exception::class)
    protected fun loadDiaFile(br: BufferedReader) {
        var direction = 0
        station = ArrayList()
        trainType = ArrayList()
        diagram = ArrayList()
        var property = ""
        val propertyStack = Stack<String>()
        var line = br.readLine()
        var tempStation = Station(this)
        var tempTrack = StationTrack()
        var tempOuterTerminal = OuterTerminal()
        var tempType = TrainType()
        var tempDia = Diagram(this)
        var tempTrain = Train(this, 0)
        while (line != null) {
            if (line == ".") {
                //読み込みプロパティ終了
                property = propertyStack.pop()
                line = br.readLine()
                continue
            }
            if (line.endsWith(".")) {
                propertyStack.push(property)
                property = line.substring(0, line.length - 1)
                when (property) {
                    "Eki" -> {
                        tempStation = Station(this)
                        station.add(tempStation)
                    }
                    "EkiTrack2" -> {
                        tempTrack = StationTrack()
                        tempStation.tracks.add(tempTrack)
                    }
                    "OuterTerminal" -> {
                        tempOuterTerminal = OuterTerminal()
                        tempStation.outerTerminals.add(tempOuterTerminal)
                    }
                    "Ressyasyubetsu" -> {
                        tempType = TrainType()
                        trainType.add(tempType)
                    }
                    "Dia" -> {
                        tempDia = Diagram(this)
                        diagram.add(tempDia)
                    }
                    "Kudari" -> direction = 0
                    "Nobori" -> direction = 1
                    "Ressya" -> {
                        tempTrain = Train(this, direction)
                        tempDia.trains[direction].add(tempTrain)
                    }
                }
                line = br.readLine()
                continue
            }
            if (line.contains("=")) {
                val title = line.substring(0, line.indexOf("="))
                val value = line.substring(line.indexOf("=") + 1)
                when (property) {
                    "Eki" -> tempStation.setValue(title, value)
                    "EkiTrack2" -> tempTrack.setValue(title, value)
                    "OuterTerminal" -> tempOuterTerminal.setValue(title, value)
                    "Ressyasyubetsu" -> tempType.setValue(title, value)
                    "Dia" -> tempDia.setValue(title, value)
                    "Ressya" -> tempTrain.setValue(title, value)
                    "Rosen" -> this.setValue(title, value)
                    "DispProp" -> this.setValue(title, value)
                }
            }
            line = br.readLine()
        }
    }

    /**
     * OuDiaファイルを読み込んだ場合は番線情報などが含まれていないため、
     * OuDia2nd形式として保存できるよう、番線情報を付加します。
     */
    fun ConvertOudToOud2nd() {
        for (s in station) {
            if (s!!.tracks.size < 1) {
                s.tracks.add(StationTrack("1番線", "1"))
            }
            if (s.tracks.size < 2) {
                s.tracks.add(StationTrack("2番線", "2"))
            }
        }
    }

    /**
     * OuDia形式の1行を読み込みます。
     * Rosen.とDispProp.に関する情報をここで読み込みます。
     * @param title
     * @param value
     */
    protected fun setValue(title: String?, value: String) {
        when (title) {
            "Rosenmei" -> name = value
            "routeID" -> routeID = value
            "KudariDiaAlias" -> if (value.length != 0) {
                diaNameDefault[0] = value
            }
            "NoboriDiaAlias" -> if (value.length != 0) {
                diaNameDefault[1] = value
            }
            "KitenJikoku" -> diagramStartTime = when (value.length) {
                3 -> 3600 * value.substring(0, 1).toInt() + 60 * value.substring(1, 3).toInt()
                4 -> 3600 * value.substring(0, 2).toInt() + 60 * value.substring(2, 4).toInt()
                else -> 60 * value.toInt()
            }
            "EnableOperation" -> operationStyle = value.toInt()
            "Comment" -> comment = value.replace("\\n", "\n")
            "JikokuhyouFont" -> timeTableFont.add(Font(value))
            "JikokuhyouVFont" -> timeTableVFont = Font(value)
            "DiaEkimeiFont" -> diaStationNameFont = Font(value)
            "DiaJikokuFont" -> diaTimeFont = Font(value)
            "DiaRessyaFont" -> diaTrainFont = Font(value)
            "CommentFont" -> commentFont = Font(value)
            "DiaMojiColor" -> diaTextColor.setOuDiaColor(value)
            "DiaHaikeiColor" -> diaBackColor.setOuDiaColor(value)
            "DiaRessyaColor" -> diaTrainColor.setOuDiaColor(value)
            "DiaJikuColor" -> diaAxicsColor.setOuDiaColor(value)
            "JikokuhyouBackColor" -> {
                val color = Color()
                color.setOuDiaColor(value)
                timeTableBackColor.add(color)
            }
            "StdOpeTimeLowerColor" -> stdOpeTimeLowerColor.setOuDiaColor(value)
            "StdOpeTimeHigherColor" -> stdOpeTimeHigherColor.setOuDiaColor(value)
            "StdOpeTimeUndefColor" -> stdOpeTimeUndefColor.setOuDiaColor(value)
            "StdOpeTimeIllegalColor" -> stdOpeTimeIllegalColor.setOuDiaColor(value)
            "EkimeiLength" -> stationNameWidth = value.toInt()
            "JikokuhyouRessyaWidth" -> trainWidth = value.toInt()
            "AnySecondIncDec1" -> secondShift[0] = value.toInt()
            "AnySecondIncDec2" -> secondShift[1] = value.toInt()
            "DisplayRessyamei" -> showTrainName = value == "1"
            "DisplayOuterTerminalEkimeiOriginSide" -> showOuterTerminalOrigin = value == "1"
            "DisplayOuterTerminalEkimeiTerminalSide" -> showOuterTerminalTerminal = value == "1"
            "DiagramDisplayOuterTerminal" -> showOuterTerminal = value == "1"
        }
    }

    /**
     * OuDiaSecond形式で保存します。
     * 現在はOuDiaSecond.1.07形式で保存します。保存形式は今後のアップデートで変更する可能性があります。
     * @param fileName
     * @throws Exception
     */
    @Throws(Exception::class)
    fun saveToFile(fileName: String) {
        convertToOud2()
        try {
            File(fileName).createNewFile()
        } catch (e: IOException) {
            throw IOException("errorFile:$fileName")
        }
        val fos = FileOutputStream(fileName)
        //BOM付与
        fos.write(0xef)
        fos.write(0xbb)
        fos.write(0xbf)
        val out = PrintWriter(BufferedWriter(OutputStreamWriter(fos)))
        out.println("FileType=OuDiaSecond.1.07")
        out.println("Rosen.")
        out.println("Rosenmei=$name")
        if (routeID.length > 0) {
            out.println("routeID=$routeID")
        }
        if (diaNameDefault[0] != "下り時刻表") {
            out.println("KudariDiaAlias=" + diaNameDefault[0])
        }
        if (diaNameDefault[1] != "上り時刻表") {
            out.println("NoboriDiaAlias=" + diaNameDefault[1])
        }
        for (s in station) {
            s!!.saveToFile(out)
        }
        for (type in trainType) {
            type.saveToFile(out)
        }
        for (dia in diagram) {
            dia.saveToFile(out)
        }
        out.println("KitenJikoku=" + diagramStartTime / 3600 + String.format("%02d", diagramStartTime / 60 % 60))
        out.println("DiagramDgrYZahyouKyoriDefault=$stationSpaceDefault")
        out.println("EnableOperation=$operationStyle")
        out.println("Comment=" + comment.replace("\n", "\\n"))
        out.println(".")
        out.println("DispProp.")
        for (font in timeTableFont) {
            out.println("JikokuhyouFont=" + font.ouDiaString)
        }
        for (i in timeTableFont.size..7) {
            out.println("JikokuhyouFont=" + Font.OUDIA_DEFAULT.ouDiaString)
        }
        out.println("JikokuhyouVFont=" + timeTableVFont.ouDiaString)
        out.println("DiaEkimeiFont=" + diaStationNameFont.ouDiaString)
        out.println("DiaJikokuFont=" + diaTimeFont.ouDiaString)
        out.println("DiaRessyaFont=" + diaTrainFont.ouDiaString)
        out.println("CommentFont=" + commentFont.ouDiaString)
        out.println("DiaMojiColor=" + diaTextColor.oudiaString)
        out.println("DiaHaikeiColor=" + diaBackColor.oudiaString)
        out.println("DiaRessyaColor=" + diaTrainColor.oudiaString)
        out.println("DiaJikuColor=" + diaAxicsColor.oudiaString)
        for (color in timeTableBackColor) {
            out.println("JikokuhyouBackColor=" + color.oudiaString)
        }
        out.println("StdOpeTimeLowerColor=" + stdOpeTimeLowerColor.oudiaString)
        out.println("StdOpeTimeHigherColor=" + stdOpeTimeHigherColor.oudiaString)
        out.println("StdOpeTimeUndefColor=" + stdOpeTimeUndefColor.oudiaString)
        out.println("StdOpeTimeIllegalColor=" + stdOpeTimeIllegalColor.oudiaString)
        out.println("EkimeiLength=$stationNameWidth")
        out.println("JikokuhyouRessyaWidth=$trainWidth")
        out.println("AnySecondIncDec1=" + secondShift[0])
        out.println("AnySecondIncDec2=" + secondShift[1])
        out.println("DisplayRessyamei=" + if (showTrainName) "1" else "0")
        out.println("DisplayOuterTerminalEkimeiOriginSide=" + if (showOuterTerminalOrigin) "1" else "0")
        out.println("DisplayOuterTerminalEkimeiTerminalSide=" + if (showOuterTerminalTerminal) "1" else "0")
        out.println("DiagramDisplayOuterTerminal=" + if (showOuterTerminal) "1" else "0")
        out.println(".")
        out.println("FileTypeAppComment=AOdia V3.0a.0")
        out.close()
    }

    /**
     * OuDia形式で保存します。
     * 現在はOuDia.1.02形式で保存します。保存形式は今後のアップデートで変更する可能性があります。
     * @param fileName
     * @throws Exception
     */
    @Throws(Exception::class)
    fun saveToOuDiaFile(fileName: String?) {
        val out = PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(File(fileName)), "Shift-JIS")))
        out.println("FileType=OuDia.1.02")
        out.println("Rosen.")
        out.println("Rosenmei=$name")
        if (routeID.length > 0) {
            out.println("routeID=$routeID")
        }
        for (s in station) {
            s!!.saveToOuDiaFile(out)
        }
        for (type in trainType) {
            type.saveToOuDiaFile(out)
        }
        for (dia in diagram) {
            dia.saveToOuDiaFile(out)
        }
        out.println("KitenJikoku=" + diagramStartTime / 3600 + String.format("%02d", diagramStartTime / 60 % 60))
        out.println("DiagramDgrYZahyouKyoriDefault=$stationSpaceDefault")
        out.println("Comment=" + comment.replace("\n", "\\n"))
        out.println(".")
        out.println("DispProp.")
        for (font in timeTableFont) {
            out.println("JikokuhyouFont=" + font.ouDiaString)
        }
        for (i in timeTableFont.size..7) {
            out.println("JikokuhyouFont=" + Font.OUDIA_DEFAULT.ouDiaString)
        }
        out.println("JikokuhyouVFont=" + timeTableVFont.ouDiaString)
        out.println("DiaEkimeiFont=" + diaStationNameFont.ouDiaString)
        out.println("DiaJikokuFont=" + diaTimeFont.ouDiaString)
        out.println("DiaRessyaFont=" + diaTrainFont.ouDiaString)
        out.println("CommentFont=" + commentFont.ouDiaString)
        out.println("DiaMojiColor=" + diaTextColor.oudiaString)
        out.println("DiaHaikeiColor=" + diaBackColor.oudiaString)
        out.println("DiaRessyaColor=" + diaTrainColor.oudiaString)
        out.println("DiaJikuColor=" + diaAxicsColor.oudiaString)
        out.println("EkimeiLength=$stationNameWidth")
        out.println("JikokuhyouRessyaWidth=$trainWidth")
        out.println(".")
        out.println("FileTypeAppComment=AOdia V3.0b.1")
        out.close()
    }

    public override fun clone(): LineFile {
        return try {
            val result = super.clone() as LineFile
            result.commentFont = commentFont.clone()
            result.diaAxicsColor = diaAxicsColor.clone()
            result.diaBackColor = diaBackColor.clone()
            result.diaNameDefault = diaNameDefault.clone()
            result.diaStationNameFont = diaStationNameFont.clone()
            result.diaTextColor = diaTextColor.clone()
            result.diaTimeFont = diaTimeFont.clone()
            result.diaTrainColor = diaTrainColor.clone()
            result.secondShift = secondShift.clone()
            result.diaTrainFont = diaTrainFont.clone()
            result.stdOpeTimeHigherColor = stdOpeTimeHigherColor.clone()
            result.stdOpeTimeIllegalColor = stdOpeTimeIllegalColor.clone()
            result.stdOpeTimeLowerColor = stdOpeTimeLowerColor.clone()
            result.stdOpeTimeUndefColor = stdOpeTimeUndefColor.clone()
            result.timeTableVFont = timeTableVFont.clone()
            result.diagram = ArrayList()
            for (d in diagram) {
                val newDia = d.clone(result)
                result.diagram.add(newDia)
            }
            result.station = ArrayList()
            for (s in station) {
                val newS = s!!.clone(result)
                newS.lineFile = result
                result.station.add(newS)
            }
            result.trainType = ArrayList()
            for (t in trainType) {
                result.trainType.add(t.clone())
            }
            result.timeTableBackColor = ArrayList()
            for (c in timeTableBackColor) {
                result.timeTableBackColor.add(c.clone())
            }
            result.timeTableFont = ArrayList()
            for (f in timeTableFont) {
                result.timeTableFont.add(f.clone())
            }
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            LineFile()
        }
    }

    /**
     * この路線ファイルをoud2形式に合わせます。
     * 番線が足りない場合は番線を追加します。
     */
    fun convertToOud2() {
        for (s in station) {
            var i = s!!.tracks.size
            while (i <= s.stopMain[Train.DOWN] || i <= s.stopMain[Train.UP]) {
                val track = StationTrack()
                track.trackName = (i + 1).toString() + "番線"
                track.trackShortName = (i + 1).toString() + ""
                s.tracks.add(track)
                i++
            }
        }
        checkBorderStation()
    }

    /**
     * OuDia形式の境界線をOuDiaSecondのbranchstationに変換します。
     */
    fun checkBorderStation() {
        //oudiaのborderをoudia2ndに合わせる
        for (index in 0 until stationNum) {
            val station = getStation(index)
            if (station!!.border) {
                for (check in index + 1 until stationNum) {
                    if (getStation(check)!!.name == station.name) {
                        station.brunchCoreStationIndex = check
                    }
                }
                if (index != stationNum - 1) {
                    for (check in 0 until index) {
                        if (getStation(check)!!.name == getStation(index + 1)!!.name) {
                            getStation(index + 1)!!.brunchCoreStationIndex = check
                        }
                    }
                }
            }
        }
        for (index in 0 until stationNum) {
            val station = getStation(index)
            station!!.border = false
        }
    }
    /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
    /**
     * lineFileに含まれるダイヤ数
     */
    val diagramNum: Int
        get() = diagram.size

    /**
     * Diagram取得
     */
    fun getDiagram(index: Int): Diagram {
        return diagram[index]
    }

    /**
     * 列車数
     */
    fun getTrainNum(diaIndex: Int, direction: Int): Int {
        return diagram[diaIndex].getTrainNum(direction)
    }

    /**
     * Train取得
     */
    fun getTrain(diaIndex: Int, direction: Int, trainIndex: Int): Train {
        return diagram[diaIndex].getTrain(direction, trainIndex)
    }

    /**
     * TrainType取得
     */
    fun getTrainType(index: Int): TrainType {
        return trainType[index]
    }

    /**
     * ダイヤグラムに含まれる駅数を返します
     * @return
     */
    val stationNum: Int
        get() = station.size

    /**
     * 駅取得
     */
    fun getStation(index: Int): Station? {
        return station[index]
    }

    private var stationTime = ArrayList<Int>()

    /**
     * 最短所要時間のリストを取得します。
     * リストには各駅ごとの起点駅からの累計最短所要時間が入ります。
     * 最短所要時間は毎回計算するのではなく、この関数初回使用時に計算され、２回目以降は初回の結果が返ります。
     * 最短所要時間を更新したい場合はcalcStationTime()を呼び出してください。
     *
     * なお、駅数が変化した場合はcalcStationTimeが呼ばれます
     * @return
     */
    fun getStationTime(): ArrayList<Int> {
        if (stationTime.size != station.size) {
            calcStationTime()
        }
        return stationTime
    }

    /**
     * 最短所要時間を更新します。
     */
    fun calcStationTime() {
        stationTime = ArrayList()
        stationTime.add(0)
        for (dia in diagram) {
            if (dia.name == "基準運転時分") {
                for (i in 1 until stationNum) {
                    var minTime = 100000
                    for (train in dia.trains[0]) {
                        if (train.timeExist(i - 1) && train.timeExist(i)) {
                            var time = train.reqTime(i - 1, i)
                            if (time < 0) continue
                            if (train.getStopType(i - 1) != StationTime.STOP_TYPE_STOP.toInt()) {
                                time += 30
                            }
                            if (train.getStopType(i) != StationTime.STOP_TYPE_STOP.toInt()) {
                                time += 30
                            }
                            if (time < minTime) {
                                minTime = time
                            }
                        }
                    }
                    for (train in dia.trains[1]) {
                        if (train.timeExist(i - 1) && train.timeExist(i)) {
                            var time = train.reqTime(i - 1, i)
                            if (time < 0) continue
                            if (train.getStopType(i - 1) != StationTime.STOP_TYPE_STOP.toInt()) {
                                time += 30
                            }
                            if (train.getStopType(i) != StationTime.STOP_TYPE_STOP.toInt()) {
                                time += 30
                            }
                            if (time < minTime) {
                                minTime = time
                            }
                        }
                    }
                    if (minTime > 90000) {
                        minTime = 180
                    }
                    stationTime.add(stationTime[stationTime.size - 1] + minTime)
                }
                return
            }
        }
        for (i in 1 until stationNum) {
            var minTime = 100000
            for (dia in diagram) {
                for (train in dia.trains[0]) {
                    if (train.timeExist(i - 1) && train.timeExist(i)) {
                        var time = train.reqTime(i - 1, i)
                        if (time < 0) continue
                        if (train.getStopType(i - 1) != StationTime.STOP_TYPE_STOP.toInt()) {
                            time += 30
                        }
                        if (train.getStopType(i) != StationTime.STOP_TYPE_STOP.toInt()) {
                            time += 30
                        }
                        if (time < minTime) {
                            minTime = time
                        }
                    }
                }
                for (train in dia.trains[1]) {
                    if (train.timeExist(i - 1) && train.timeExist(i)) {
                        var time = train.reqTime(i - 1, i)
                        if (time < 0) continue
                        if (train.getStopType(i - 1) != StationTime.STOP_TYPE_STOP.toInt()) {
                            time += 30
                        }
                        if (train.getStopType(i) != StationTime.STOP_TYPE_STOP.toInt()) {
                            time += 30
                        }
                        if (time < minTime) {
                            minTime = time
                        }
                    }
                }
            }
            if (minTime > 90000) {
                minTime = 180
            }
            if (minTime < 30) {
                minTime = 30
            }
            stationTime.add(stationTime[stationTime.size - 1] + minTime)
        }
    }

    /**
     * 新しいTrainTypeを挿入します
     * 全ての列車の列車種別を書き換えます
     */
    fun addTrainType(index: Int, newType: TrainType) {
        var index = index
        if (index < 0 || index > trainType.size) {
            index = trainType.size
        }
        trainType.add(index, newType)
        if (index == trainType.size) return
        for (dia in diagram) {
            for (train in dia.trains[0]) {
                if (train.type >= index) {
                    train.type++
                }
            }
            for (train in dia.trains[1]) {
                if (train.type >= index) {
                    train.type++
                }
            }
        }
    }

    /**
     * 列車種別を削除する
     * return =false:路線内にこの種別を使っているやつがいるから削除できない
     * true:削除成功
     * false:削除に失敗
     *
     */
    fun deleteTrainType(type: TrainType): Boolean {
        val index = trainType.indexOf(type)
        if (index < 0) {
            return false
        }
        for (dia in diagram) {
            for (train in dia.trains[0]) {
                if (train.type == index) {
                    return false
                }
            }
            for (train in dia.trains[1]) {
                if (train.type == index) {
                    return false
                }
            }
        }
        trainType.remove(type)
        for (dia in diagram) {
            for (train in dia.trains[0]) {
                if (train.type >= index) {
                    train.type--
                }
            }
            for (train in dia.trains[1]) {
                if (train.type >= index) {
                    train.type--
                }
            }
        }
        return true
    }

    /**
     * 駅を追加します。
     * 駅が追加されると、このLineFileに所属する列車のstationTimeが更新されます。
     * brunch=trueの時、新規駅を通過する列車は経由なしになります。
     *
     * index:挿入駅index
     */
    fun addStation(index: Int, newStation: Station?, brunch: Boolean) {
        var index = index
        if (index < 0 || index >= stationNum) {
            index = stationNum
        }
        for (s in station) {
            if (s!!.brunchCoreStationIndex >= index) {
                s.brunchCoreStationIndex++
            }
            if (s.loopOriginStationIndex >= index) {
                s.loopOriginStationIndex++
            }
        }
        for (dia in diagram) {
            for (direction in 0..1) {
                for (train in dia.trains[direction]) {
                    train.addNewStation(index, brunch)
                }
            }
        }
        station.add(index, newStation)
    }

    /**
     * 返り値
     * 0:削除成功
     * -1:削除失敗(範囲外)
     * -2:削除失敗(分岐元設定されている)
     */
    fun deleteStation(index: Int): Int {
        if (index < 0 || index >= stationNum) {
            return -1
        }
        for (s in station) {
            if (s!!.brunchCoreStationIndex == index) {
                return -2
            }
            if (s.loopOriginStationIndex == index) {
                s.loopOriginStationIndex = -1
            }
        }
        for (dia in diagram) {
            for (direction in 0..1) {
                for (train in dia.trains[direction]) {
                    train.stationTimes.removeAt(index)
                }
            }
        }
        station.removeAt(index)
        return 0
    }

    /**
     * 路線の切り出しを行います
     * @param userOuterStation trueの時、切り出し範囲外に直通する列車の始発終着駅を路線外始終着駅として登録します
     */
    fun makeSubLine(startStation: Int, endStation: Int, userOuterStation: Boolean) {
        if (userOuterStation) {
            val startStationOuterShift = getStation(startStation)!!.outerTerminals.size
            val endStationOuterShift = getStation(endStation)!!.outerTerminals.size
            for (i in 0 until startStation) {
                getStation(startStation)!!.outerTerminals.add(OuterTerminal(getStation(i)!!.name))
            }
            for (i in endStation + 1 until stationNum) {
                getStation(endStation)!!.outerTerminals.add(OuterTerminal(getStation(i)!!.name))
            }
            for (dia in diagram) {
                for (train in dia.trains[0]) {
                    if (train.startStation < 0) {
                        continue
                    }
                    if (train.endStation < 0) {
                        continue
                    }
                    if (train.startStation < startStation) {
                        val operation = StationTimeOperation()
                        operation.operationType = 4
                        operation.intData1 = train.startStation + startStationOuterShift
                        operation.time1 = train.getTime(train.startStation, Train.DEPART, true)
                        train.stationTimes[startStation].beforeOperations.add(operation)
                    }
                    if (train.endStation > endStation) {
                        val operation = StationTimeOperation()
                        operation.operationType = 4
                        operation.intData1 = train.endStation - endStation + endStationOuterShift - 1
                        operation.time1 = train.getTime(train.endStation, Train.ARRIVE, true)
                        train.stationTimes[endStation].afterOperations.add(operation)
                    }
                }
                for (train in dia.trains[1]) {
                    if (train.startStation < 0) {
                        continue
                    }
                    if (train.endStation < 0) {
                        continue
                    }
                    if (train.endStation < startStation) {
                        val operation = StationTimeOperation()
                        operation.operationType = 4
                        operation.intData1 = train.endStation + startStationOuterShift
                        operation.time1 = train.getTime(train.endStation, Train.ARRIVE, true)
                        train.stationTimes[startStation].afterOperations.add(operation)
                    }
                    if (train.startStation > endStation) {
                        val operation = StationTimeOperation()
                        operation.operationType = 4
                        operation.intData1 = train.startStation - endStation + endStationOuterShift - 1
                        operation.time1 = train.getTime(train.startStation, Train.DEPART, true)
                        train.stationTimes[endStation].beforeOperations.add(operation)
                    }
                }
            }
            run {
                var i = startStationOuterShift
                while (i < getStation(startStation)!!.outerTerminals.size) {
                    if (getStation(startStation)!!.deleteOuterTerminal(i)) {
                        i--
                    }
                    i++
                }
            }
            var i = endStationOuterShift
            while (i < getStation(endStation)!!.outerTerminals.size) {
                if (getStation(endStation)!!.deleteOuterTerminal(i)) {
                    i--
                }
                i++
            }
        }
        for (i in stationNum - 1 downTo endStation + 1) {
            deleteStation(i)
        }
        for (i in startStation - 1 downTo 0) {
            deleteStation(i)
        }
        //時刻表スタイル適正化
        getStation(0)!!.showDepartureCustom[Train.DOWN] = true
        getStation(0)!!.showDepartureCustom[Train.UP] = false
        getStation(0)!!.showArrivalCustom[Train.DOWN] = false
        getStation(0)!!.showArrivalCustom[Train.UP] = true
        getStation(stationNum - 1)!!.showDepartureCustom[Train.DOWN] = false
        getStation(stationNum - 1)!!.showDepartureCustom[Train.UP] = true
        getStation(stationNum - 1)!!.showArrivalCustom[Train.DOWN] = true
        getStation(stationNum - 1)!!.showArrivalCustom[Train.UP] = false
    }

    /**
     * 路線ファイルの組み入れを行います。
     * insertpos組み入れ駅
     *
     * 路線を組み入れる際は、組み入れ駅を２つに分離し、その間に路線を挿入します
     * 組み入れ路線の始発終着駅が組み入れ駅と同じ場合は、２路線の駅を共通化します。
     */
    fun addLineFile(insertPos: Int, other: LineFile) {
        val lineFile = other.clone()
        val stationName = getStation(insertPos)!!.name
        val type = 0
        if (lineFile.getStation(0)!!.name == stationName) {
            for (i in 0 until insertPos) {
                lineFile.addStation(i, getStation(i)!!.clone(lineFile), true)
            }
            for (i in insertPos until stationNum) {
                lineFile.addStation(lineFile.stationNum, getStation(i)!!.clone(lineFile), true)
            }
            for (i in 0 until other.stationNum) {
                addStation(insertPos + i, lineFile.getStation(i + insertPos)!!.clone(this), true)
            }
            //分岐駅の処理
            for (dia in diagram) {
                for (train in dia.trains[0]) {
                    //本線側列車で分岐駅を通るもの
                    if (train.getStopType(insertPos + other.stationNum) == StationTime.STOP_TYPE_STOP.toInt() || train.getStopType(insertPos + other.stationNum) == StationTime.STOP_TYPE_PASS.toInt()) {
                        //本線当駅始発の列車は関係ない
                        if (train.startStation != insertPos + other.stationNum) {
                            train.setStopType(insertPos, train.getStopType(insertPos + other.stationNum))
                            train.setTime(insertPos, Train.ARRIVE, train.getTime(insertPos + other.stationNum, Train.ARRIVE, true))
                            train.setTime(insertPos + other.stationNum, Train.ARRIVE, -1)
                            if (insertPos + other.stationNum + 1 < lineFile.stationNum) {
                                //当駅止まりの列車は経由なしにしない
                                if (train.getStopType(insertPos + other.stationNum + 1) == StationTime.STOP_TYPE_NOSERVICE.toInt()) {
                                    for (i in insertPos + 1 until insertPos + other.stationNum + 1) {
                                        train.setStopType(i, StationTime.STOP_TYPE_NOSERVICE.toInt())
                                    }
                                }
                            }
                        }
                    }
                }
                for (train in dia.trains[1]) {
                    if (train.getStopType(insertPos + other.stationNum) == StationTime.STOP_TYPE_STOP.toInt() || train.getStopType(insertPos + other.stationNum) == StationTime.STOP_TYPE_PASS.toInt()) {
                        //本線当駅終着の列車は関係ない
                        if (train.endStation != insertPos + other.stationNum) {
                            train.setStopType(insertPos, train.getStopType(insertPos + other.stationNum))
                            train.setTime(insertPos, Train.DEPART, train.getTime(insertPos + other.stationNum, Train.DEPART, true))
                            train.setTime(insertPos + other.stationNum, Train.DEPART, -1)
                            if (insertPos + other.stationNum + 1 < lineFile.stationNum) {
                                if (train.getStopType(insertPos + other.stationNum + 1) == StationTime.STOP_TYPE_NOSERVICE.toInt()) {
                                    for (i in insertPos + 1 until insertPos + other.stationNum + 1) {
                                        train.setStopType(i, StationTime.STOP_TYPE_NOSERVICE.toInt())
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (dia in lineFile.diagram) {
                var frag = false
                for (dia2 in diagram) {
                    if (dia.name == dia2.name) {
                        frag = true
                        for (train in dia.trains[0]) {
                            dia2.addTrain(0, -1, train.clone(this))
                        }
                        for (train in dia.trains[1]) {
                            dia2.addTrain(1, -1, train.clone(this))
                        }
                        break
                    }
                }
                if (!frag) {
                    diagram.add(dia.clone(this))
                }
            }
            station[insertPos]!!.showArrivalCustom[Train.DOWN] = true
            station[insertPos]!!.showArrivalCustom[Train.UP] = true
            station[insertPos]!!.showDepartureCustom[Train.DOWN] = true
            station[insertPos]!!.showDepartureCustom[Train.UP] = true
            station[insertPos + other.stationNum]!!.showArrivalCustom[Train.DOWN] = false
            station[insertPos + other.stationNum]!!.showArrivalCustom[Train.UP] = true
            station[insertPos + other.stationNum]!!.showDepartureCustom[Train.DOWN] = true
            station[insertPos + other.stationNum]!!.showDepartureCustom[Train.UP] = false
            station[insertPos + other.stationNum]!!.brunchCoreStationIndex = insertPos
            if (stationNum == insertPos + other.stationNum + 1) {
                deleteStation(insertPos + other.stationNum)
            }
        } else if (lineFile.getStation(lineFile.stationNum - 1)!!.name == stationName) {
            for (i in 0..insertPos) {
                lineFile.addStation(i, getStation(i)!!.clone(lineFile), true)
            }
            for (i in insertPos + 1 until stationNum) {
                lineFile.addStation(lineFile.stationNum, getStation(i)!!.clone(lineFile), true)
            }
            for (i in 0 until other.stationNum) {
                addStation(insertPos + 1 + i, lineFile.getStation(i + insertPos + 1)!!.clone(this), true)
            }
            for (dia in diagram) {
                for (train in dia.trains[0]) {
                    if (train.endStation != insertPos) {
                        train.setStopType(insertPos + other.stationNum, train.getStopType(insertPos))
                        train.setTime(insertPos + other.stationNum, Train.DEPART, train.getTime(insertPos, Train.DEPART, true))
                        train.setTime(insertPos, Train.DEPART, -1)
                    }
                    if (insertPos > 0) {
                        if (train.getStopType(insertPos - 1) == StationTime.STOP_TYPE_NOSERVICE.toInt()) {
                            for (i in insertPos until insertPos + other.stationNum) {
                                train.setStopType(i, StationTime.STOP_TYPE_NOSERVICE.toInt())
                            }
                        }
                    }
                }
                for (train in dia.trains[1]) {
                    if (train.startStation != insertPos) {
                        train.setStopType(insertPos + other.stationNum, train.getStopType(insertPos))
                        train.setTime(insertPos + other.stationNum, Train.ARRIVE, train.getTime(insertPos, Train.ARRIVE, true))
                        train.setTime(insertPos, Train.ARRIVE, -1)
                    }
                    if (insertPos > 0) {
                        if (train.getStopType(insertPos - 1) == StationTime.STOP_TYPE_NOSERVICE.toInt()) {
                            for (i in insertPos until insertPos + other.stationNum) {
                                train.setStopType(i, StationTime.STOP_TYPE_NOSERVICE.toInt())
                            }
                        }
                    }
                }
            }
            for (dia in lineFile.diagram) {
                var frag = false
                for (dia2 in diagram) {
                    if (dia.name == dia2.name) {
                        frag = true
                        for (train in dia.trains[0]) {
                            dia2.addTrain(0, -1, train.clone(this))
                        }
                        for (train in dia.trains[1]) {
                            dia2.addTrain(1, -1, train.clone(this))
                        }
                        break
                    }
                }
                if (!frag) {
                    diagram.add(dia.clone(this))
                }
            }
            station[insertPos]!!.showArrivalCustom[Train.DOWN] = true
            station[insertPos]!!.showArrivalCustom[Train.UP] = false
            station[insertPos]!!.showDepartureCustom[Train.DOWN] = false
            station[insertPos]!!.showDepartureCustom[Train.UP] = true
            station[insertPos + other.stationNum]!!.showArrivalCustom[Train.DOWN] = true
            station[insertPos + other.stationNum]!!.showArrivalCustom[Train.UP] = true
            station[insertPos + other.stationNum]!!.showDepartureCustom[Train.DOWN] = true
            station[insertPos + other.stationNum]!!.showDepartureCustom[Train.UP] = true
            station[insertPos]!!.brunchCoreStationIndex = insertPos + other.stationNum
            if (insertPos == 0) {
                deleteStation(0)
            }
        } else {
            for (i in 0..insertPos) {
                lineFile.addStation(i, getStation(i)!!.clone(lineFile), true)
            }
            for (i in insertPos until stationNum) {
                lineFile.addStation(lineFile.stationNum, getStation(i)!!.clone(lineFile), true)
            }
            addStation(insertPos + 1, getStation(insertPos)!!.clone(this), true)
            for (i in 0 until other.stationNum) {
                addStation(insertPos + 1 + i, lineFile.getStation(i + insertPos + 1)!!.clone(this), true)
            }
            for (dia in diagram) {
                for (train in dia.trains[0]) {
                    train.setStopType(insertPos + other.stationNum + 1, train.getStopType(insertPos))
                    train.setTime(insertPos + other.stationNum + 1, Train.DEPART, train.getTime(insertPos, Train.DEPART, true))
                    train.setTime(insertPos, Train.DEPART, -1)
                }
                for (train in dia.trains[1]) {
                    train.setStopType(insertPos + other.stationNum + 1, train.getStopType(insertPos))
                    train.setTime(insertPos + other.stationNum + 1, Train.ARRIVE, train.getTime(insertPos, Train.ARRIVE, true))
                    train.setTime(insertPos, Train.ARRIVE, -1)
                }
            }
            for (dia in lineFile.diagram) {
                var frag = false
                for (dia2 in diagram) {
                    if (dia.name == dia2.name) {
                        frag = true
                        for (train in dia.trains[0]) {
                            dia2.addTrain(0, -1, train.clone(this))
                        }
                        for (train in dia.trains[1]) {
                            dia2.addTrain(1, -1, train.clone(this))
                        }
                        break
                    }
                }
                if (!frag) {
                    diagram.add(dia.clone(this))
                }
            }
            station[insertPos + other.stationNum + 1]!!.brunchCoreStationIndex = insertPos
            if (insertPos == 0) {
                deleteStation(0)
            }
            if (stationNum == insertPos + other.stationNum + 1) {
                deleteStation(insertPos + other.stationNum)
            }
        }
    }

    /**
     * この路線の駅順を反転させます。
     */
    fun reverse() {
        Collections.reverse(station)
        for (s in station) {
            s!!.reverse()
        }
        for (dia in diagram) {
            for (train in dia.trains[0]) {
                Collections.reverse(train.stationTimes)
                train.direction = 1
            }
            for (train in dia.trains[1]) {
                Collections.reverse(train.stationTimes)
                train.direction = 0
            }
            val temp = dia.trains[0]
            dia.trains[0] = dia.trains[1]
            dia.trains[1] = temp
        }
    }

    /**
     * 列車を時刻順に並び替えます
     */
    fun sortTrain(diaIndex: Int, direction: Int, stationIndex: Int) {
        getDiagram(diaIndex).sortTrain(direction, stationIndex)
    }

    /**
     * ダイヤ名からダイヤを取得します
     * 重複している場合最初のダイヤファイルを返します。
     * 指定ダイヤが存在しない時はnullが返ります。
     */
    fun getDiaFromName(name: String): Diagram? {
        for (dia in diagram) {
            if (dia.name == name) {
                return dia
            }
        }
        return null
    }

    //  2つの駅が同一駅かどうか
    fun isSameStation(station1: Int, station2: Int): Boolean {
        if (station1 == station2) {
            return true
        }
        if (getStation(station1)!!.brunchCoreStationIndex == station2) {
            return true
        }
        if (getStation(station2)!!.brunchCoreStationIndex == station1) {
            return true
        }
        return if (getStation(station2)!!.brunchCoreStationIndex == getStation(station1)!!.brunchCoreStationIndex && getStation(station1)!!.brunchCoreStationIndex >= 0) {
            true
        } else false
    }
}