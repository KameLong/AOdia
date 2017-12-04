package com.kamelong.OuDia2nd


import android.app.Activity
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStation
import com.kamelong.tool.Color
import com.kamelong.tool.Font
import com.kamelong.tool.ShiftJISBufferedReader

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.ArrayList

/**
 * OuDiaSecond形式の1ファイルを取り扱う.
 * 内部に複数のダイヤを格納することができるが、駅リスト、種別リストは一つしか持てない.
 *
 * @author KameLong
 */
class DiaFile(override var activity: Activity, override var menuOpen: Boolean) : AOdiaDiaFile {
    override fun getDiaName(index: Int): String {
        return diaName[index]
    }


    /**
     * 路線名.
     */
    var lineName = ""
    /**
     * ダイヤ名。
     * 複数のダイヤを保持することができるためArrayListを用いる。
     */
    var diaName = ArrayList<String>()
    /**
     * 駅。
     * 複数の駅を保持することができるためArrayListを用いる。
     */
    var station = ArrayList<Station>()
    override val stationNum: Int
        get() = station.size
    /**
     * 種別.
     * 複数の種別を保持することができるためArrayListを用いる。
     */
    var trainType = ArrayList<TrainType>()
    /**
     * Trainは1本の列車に相当する
     * 最初のArrayListはダイヤの数に相当する
     * ArrayListの中に配列があるが、これは上りと下りの２つ(確定)の時刻表があるため、配列を用いている
     * 配列の内部に再びArrayListがあるが、これは各時刻表中の列車の本数分の大きさを持つ
     */
    var train = ArrayList<ArrayList<ArrayList<Train>>>()
    /**
     * コメント。
     * oudiaデータには路線ごとにコメントがついている。
     * ダイヤごとにコメントをつけたい場合はArrayListに拡張しないといけない。
     */
    var comment = ""
    /**
     * ダイヤグラム起点時刻。
     */
    var startTime = 10800
    /**
     * DiagramDgrYZahyouKyoriDefault
     */
    var stationDistanceDefault = 60
    var tableFont = ArrayList<Font>()
    var vfont = Font.OUDIA_DEFAULT
    var stationFont = Font.OUDIA_DEFAULT
    var diaTimeFont = Font.OUDIA_DEFAULT
    var diaTextFont = Font.OUDIA_DEFAULT
    var commnetFont = Font.OUDIA_DEFAULT
    var diaTextColor = Color()
    var backGroundColor = Color()
    var trainColor = Color()
    var axisColor = Color()
    var stationNameLength = 6
    var trainWidth = 5
    private var anySecondIncDec1 = 5
    private var anySecondIncDec2 = 15
    /**
     * FileType
     */
    protected var fileType = ""

    override var filePath = ""

    /**
     * 種別の数を返す。
     */
    val trainTypeNum: Int
        get() = trainType.size
    /**
     * ダイヤ数を返す
     */
    override fun getDiaNum(): Int {
        return train.size
    }
    /**
     * コンストラクタ。
     *
     * @param file 開きたいファイル
     *
     *
     * コンストラクタでは読み込みファイルが与えられるので、そのファイルを読み込む。
     * 読み込む処理はloadDiaに書かれているので適宜呼び出す。
     * oudファイルはShiftJisで書かれているので考慮する必要がある。
     *
     *
     * fileがnullの時はnullPointerExceptionが発生する
     */
    constructor(activity: Activity) : this(activity,true) {}
        constructor(activity: Activity,file: File) : this(activity,true) {
        try {
            val `is` = FileInputStream(file)
            if (file.path.endsWith(".oud2")||file.path.endsWith(".oud")) {
                val filereader = InputStreamReader(`is`, "Shift_JIS")
                val br = ShiftJISBufferedReader(filereader)
                loadDia(br)
            }
        } catch (e: Exception) {
            //ファイル読み込み失敗
            e.printStackTrace()
        }
    }
    private fun oudiaSplit(str:String,index:Int):String{
        try{
            return str.split("=")[index]
        }catch (e:Exception){
            e.printStackTrace()
            return ""
        }
    }

    /**
     * oudファイルを読み込んでオブジェクトを構成する。
     *
     * @param br BufferReader of .oud fille.  forbidden @null
     */
    private fun loadDia(br: BufferedReader) {
        try {
            var line = br.readLine()
            while (line != null) {
                if (line == "Dia.") {
                    line = br.readLine()
                    diaName.add(oudiaSplit(line,1))
                    val trainArray =ArrayList<ArrayList<Train>>()
                    trainArray.add(ArrayList())
                    trainArray.add(ArrayList())

                    while (line != ".") {
                        if (line == "Ressya.") {
                            var direct = 0
                            val t = Train(this)
                            while (line != ".") {

                                if (oudiaSplit(line,0) == "Houkou") {
                                    if (oudiaSplit(line,1) == "Kudari") {
                                        direct = 0
                                    }
                                    if (oudiaSplit(line,1) == "Nobori") {
                                        direct = 1
                                    }
                                    t.direct = direct
                                }

                                if (oudiaSplit(line,0) == "Syubetsu") {
                                    t.type=Integer.parseInt(oudiaSplit(line,1))
                                }
                                if (oudiaSplit(line,0) == "Ressyamei") {
                                    t.name = oudiaSplit(line,1)
                                }
                                if (oudiaSplit(line,0) == "Gousuu") {
                                    t.count=oudiaSplit(line,1)
                                }
                                if (oudiaSplit(line,0) == "Ressyabangou") {
                                    t.number=oudiaSplit(line,1)
                                }
                                if (oudiaSplit(line,0) == "Bikou") {
                                    t.remark = oudiaSplit(line,1)
                                }

                                if (oudiaSplit(line,0) == "EkiJikoku") {
                                    try {
                                        t.setTime(oudiaSplit(line,1), direct)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }

                                line = br.readLine()
                            }
                            if (direct != -1) {
                                trainArray[direct].add(t)
                            }
                        }
                        line = br.readLine()
                        //Diaの終わりは２つの終了行が並んだ時
                        if (line == ".") {
                            line = br.readLine()
                        }
                    }
                    trainArray[0].trimToSize()
                    trainArray[1].trimToSize()
                    train.add(trainArray)
                }
                if (line == "Ressyasyubetsu.") {
                    val mTrainType = TrainType()
                    while (line != ".") {
                        if (oudiaSplit(line,0) == "Syubetsumei") {
                            mTrainType.name = oudiaSplit(line,1)
                        }
                        if (oudiaSplit(line,0) == "Ryakusyou") {
                            mTrainType.shortName=oudiaSplit(line,1)
                        }
                        if (oudiaSplit(line,0) == "JikokuhyouMojiColor") {
                            mTrainType.setTextColor(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "DiagramSenColor") {
                            mTrainType.setDiaColor(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "DiagramSenStyle") {
                            mTrainType.setLineStyle(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "DiagramSenIsBold") {
                            mTrainType.setLineBold(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "StopMarkDrawType") {
                            mTrainType.setShowStop(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "JikokuhyouFontIndex") {
                            mTrainType.fontNumber = Integer.parseInt(oudiaSplit(line,1))
                        }
                        line = br.readLine()
                    }
                    trainType.add(mTrainType)
                }
                if (line == "Eki.") {
                    val mStation = Station()
                    while (line != ".") {
                        if (oudiaSplit(line,0) == "Ekimei") {
                            mStation.name=oudiaSplit(line,1)
                        }
                        if (oudiaSplit(line,0) == "Ekijikokukeisiki") {
                            mStation.setStationTimeShow(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "Ekikibo") {
                            mStation.setSize(oudiaSplit(line,1))
                        }
                        if (oudiaSplit(line,0) == "DownMain") {
                            mStation.downMain = Integer.valueOf(oudiaSplit(line,1)) - 1
                        }
                        if (oudiaSplit(line,0) == "UpMain") {
                            mStation.upMain = Integer.valueOf(oudiaSplit(line,1)) - 1
                        }
                        if (oudiaSplit(line,0) == "BrunchCoreEkiIndex") {
                            mStation.branchStation=Integer.valueOf(oudiaSplit(line,1))

                        }
                        if (line == "EkiTrack2Cont.") {
                            while (line != ".") {
                                if (line == "EkiTrack2.") {
                                    while (line != ".") {
                                        if (oudiaSplit(line,0) == "TrackName") {
                                            mStation.trackName.add(oudiaSplit(line,1))
                                        }
                                        if (oudiaSplit(line,0) == "TrackRyakusyou") {
                                            mStation.trackRyakusyou.add(oudiaSplit(line,1))
                                        }

                                        line = br.readLine()
                                    }
                                }
                                line = br.readLine()
                            }

                        }

                        line = br.readLine()
                    }
                    station.add(mStation)
                }
                if (line == "Rosen.") {
                    line = br.readLine()
                    lineName = oudiaSplit(line,1)
                }
                if (oudiaSplit(line,0) == "Comment") {
                    comment = oudiaSplit(line,1)
                    comment = comment.replace("\\n", "\n")
                }
                if (oudiaSplit(line,0) == "KitenJikoku") {
                    val startTime = oudiaSplit(line,1)
                    if (startTime.length == 4) {
                        this.startTime = Integer.parseInt(startTime.substring(0, 2)) * 3600
                        this.startTime += Integer.parseInt(startTime.substring(2, 4)) * 60
                    }
                    if (startTime.length == 3) {
                        this.startTime = Integer.parseInt(startTime.substring(0, 1)) * 3600
                        this.startTime += Integer.parseInt(startTime.substring(1, 3)) * 60

                    }
                }
                if (oudiaSplit(line,0) == "DiagramDgrYZahyouKyoriDefault") {
                    stationDistanceDefault = Integer.parseInt(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "JikokuhyouFont") {
                    val font = Font()
                    font.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    font.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]
                    font.bold = line.contains("Bold")
                    font.itaric = line.contains("Itaric")
                    tableFont.add(font)
                }
                if (oudiaSplit(line,0) == "JikokuhyouVFont") {
                    vfont = Font()
                    try {
                        vfont.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    vfont.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]

                    vfont.bold = line.contains("Bold")
                    vfont.itaric = line.contains("Itaric")
                }
                if (oudiaSplit(line,0) == "DiaEkimeiFont") {
                    stationFont = Font()
                    stationFont.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    stationFont.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]
                    stationFont.bold = line.contains("Bold")
                    stationFont.itaric = line.contains("Itaric")
                }
                if (oudiaSplit(line,0) == "DiaJikokuFont") {
                    diaTimeFont = Font()
                    diaTimeFont.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    diaTimeFont.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]
                    diaTimeFont.bold = line.contains("Bold")
                    diaTimeFont.itaric = line.contains("Itaric")
                }
                if (oudiaSplit(line,0) == "DiaRessyaFont") {
                    diaTextFont = Font()
                    diaTextFont.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    diaTextFont.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]
                    diaTextFont.bold = line.contains("Bold")
                    diaTextFont.itaric = line.contains("Itaric")
                }
                if (oudiaSplit(line,0) == "CommentFont") {
                    commnetFont = Font()
                    commnetFont.height = Integer.parseInt(line.split("=".toRegex()).toTypedArray()[2].split(";".toRegex()).toTypedArray()[0])
                    commnetFont.name = line.split("=".toRegex()).toTypedArray()[3].split(";".toRegex()).toTypedArray()[0]
                    commnetFont.bold = line.contains("Bold")
                    commnetFont.itaric = line.contains("Itaric")
                }
                if (oudiaSplit(line,0) == "DiaMojiColor") {
                    diaTextColor.setOuDiaColor(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "DiaHaikeiColor") {
                    backGroundColor.setOuDiaColor(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "DiaRessyaColor") {
                    trainColor.setOuDiaColor(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "DiaJikuColor") {
                    axisColor.setOuDiaColor(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "EkimeiLength") {
                    stationNameLength = Integer.parseInt(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "JikokuhyouRessyaWidth") {
                    trainWidth = 60 * Integer.parseInt(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "AnySecondIncDec1") {
                    anySecondIncDec1 = Integer.parseInt(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "AnySecondIncDec2") {
                    anySecondIncDec2 = Integer.parseInt(oudiaSplit(line,1))
                }
                if (oudiaSplit(line,0) == "FileType") {
                    fileType = oudiaSplit(line,1)
                }
                line=br.readLine()

            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

    }

    /**
     * oudiaファイルを書き出す
     *
     * @return
     */
    fun makeOuDiaText(file: File, oudiaSecond: Boolean) {
        val a = StringBuilder()
        try {
            a.append("FileType=")
            a.append(fileType)
            a.append("\r\nRosen.\r\nRosenmei=")
            a.append(lineName)
            a.append("\r\n")
            for (i in 0 until this.stationNum) {
                a.append(station[i].makeStationText(oudiaSecond))
            }
            for (i in 0 until trainTypeNum) {
                a.append(trainType[i].makeTrainTypeText())
            }
            for (dia in diaName.indices) {
                a.append("Dia.\r\nDiaName=")
                a.append(diaName[dia])
                a.append("\r\nKudari.\r\n")
                for (i in 0 until train[dia][0].size) {
                    a.append(train[dia][0][i].makeTrainText(0))
                }
                a.append("\r\n.\r\nNobori.\r\n")
                for (i in 0 until train[dia][1].size) {
                    a.append(train[dia][1][i].makeTrainText(1))

                }
                a.append("\r\n.\r\n.\r\n")
            }
            a.append("KitenJikoku=")
            a.append(startTime / 3600)
            a.append(String.format("%02d", startTime / 60 % 60))
            a.append("\r\nDiagramDgrYZahyouKyoriDefault=").append(stationDistanceDefault)
            a.append("\r\nComment=").append(comment.replace("\n", "\\n"))
            a.append("\r\n.\r\nDispProp.")
            for (i in tableFont.indices) {
                val font = tableFont[i]
                a.append("\r\nJikokuhyouFont=")
                a.append(font.font2OudiaFontTxt())
            }
            a.append("\r\nJikokuhyouVFont=")
            a.append(vfont.font2OudiaFontTxt())
            a.append("\r\nDiaEkimeiFont=")
            a.append(stationFont.font2OudiaFontTxt())
            a.append("\r\nDiaJikokuFont=")
            a.append(diaTimeFont.font2OudiaFontTxt())
            a.append("\r\nDiaRessyaFont=")
            a.append(diaTextFont.font2OudiaFontTxt())
            a.append("\r\nCommentFont=")
            a.append(commnetFont.font2OudiaFontTxt())

            a.append("\r\nDiaMojiColor=")
            a.append(diaTextColor.oudiaString)
            a.append("\r\nDiaHaikeiColor=")
            a.append(backGroundColor.oudiaString)
            a.append("\r\nDiaRessyaColor=")
            a.append(trainColor.oudiaString)
            a.append("\r\nDiaJikuColor=")
            a.append(axisColor.oudiaString)
            a.append("\r\nEkimeiLength=")
            a.append(stationNameLength)
            a.append("\r\nJikokuhyouRessyaWidth=")
            a.append(trainWidth / 60)
            a.append("\r\nAnySecondIncDec1=")
            a.append(anySecondIncDec1)
            a.append("\r\nAnySecondIncDec2=")
            a.append(anySecondIncDec2)
            a.append("\r\n.")
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        try {
            val writer = PrintWriter(BufferedWriter(OutputStreamWriter(FileOutputStream(file), "Shift_JIS")))

            //write contents of StringBuffer to a file
            writer.write(a.toString())
            //close the stream
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getStation(index:Int):Station {
        try {
            return station.get(index)
        }
        catch (e:Exception) {
            e.printStackTrace()
            return Station()
        }
    }
    override fun getStationList():ArrayList<AOdiaStation> {
        try {
            return station as ArrayList<AOdiaStation>
        }
        catch (e:Exception) {
            e.printStackTrace()
            return ArrayList<AOdiaStation>()
        }
    }

    fun getTrainNum(dia:Int, direction:Int):Int {
        return train.get(dia)[direction].size
    }

    fun getTrain(dia:Int, direction:Int, index:Int):Train {
        try
        {
            return train.get(dia)[direction].get(index)
        }
        catch (e:Exception) {
            e.printStackTrace()
            return Train(this)
        }

    }
}
