package com.kamelong.OuDia2nd

import com.kamelong.JPTI.Service
import com.kamelong.JPTI.Station
import com.kamelong.JPTI.TrainType
import com.kamelong.JPTI.Trip
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.diadata.AOdiaTrainType

import java.util.ArrayList

/**
 * 列車データを格納するクラス。
 * 一つの列車に関するデータはここに格納する
 * Stationクラスには全種類のダイヤ形式で統一できる入力と、出力を書く。
 * それぞれのダイヤ形式に合わせた変換はxxxDiaFileクラスに記述する
 * @author  KameLong
 */
class Train :AOdiaTrain{
    override fun existArriveTime(station: Int): Boolean {
        return arriveExist(station)
    }

    override fun existDepartTime(station: Int): Boolean {
        return departExist(station)
    }

    override fun getStationTime(station: Int): Long {
        return time[station]
    }

    override fun setStationTime(station: Int,value:Long) {
        time[station]=value
    }

    override fun setDepartureTime(station: Int, value: Int) {
        setDepartureTime(station,value.toLong())
    }

    override fun setArrivalTime(station: Int, value: Int) {
        setArrivalTime(station,value.toLong())
    }

    override fun getDepartureTime(station: Int, startTime: Int): Int {
        return (getDepartureTime(station)-startTime)%86400+startTime
    }

    override fun getArrivalTime(station: Int, startTime: Int): Int {
        return (getArrivalTime(station)-startTime)%86400+startTime
    }



    override val trainType: AOdiaTrainType
        get() = diaFile.getTrainType(type)

    /**
     * 列車の進行方向
     */
    override var direction = -1
    override val startStation: Int
        get() {
            for(i in if(direction==0){0 until stationNum}else{stationNum-1 downTo 0}){
                if(getStopType(i)== STOP_TYPE_STOP||getStopType(i)== STOP_TYPE_PASS){
                    return i
                }
            }
            return 0
        }
    override var startAction=0
    override var endAction=0
    override var startExchangeStop=0
    override var startExchangeTimeStart=-1
    override var startExchangeTimeEnd=-1
    override var endExchangeStop=0
    override var endExchangeTimeStart=-1
    override var endExchangeTimeEnd=-1

    override val endStation: Int
        get(){
            for(i in if(direction==1){0 until stationNum}else{stationNum-1 downTo 0}){
                if(getStopType(i)== STOP_TYPE_STOP||getStopType(i)== STOP_TYPE_PASS){
                    return i
                }
            }
            return 0

        }

    /**
     * 列車種別
     */
    override var type = 0
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
    override var number = ""
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
    override var name = ""
    /**
     * 運用番号
     */
    override var operation=""
    /**
     * 号数
     */
    override var count = ""
        set(value) {
            if (value.isNotEmpty()) {
                field = value
            }
        }
    /**
     * 備考
     */
    override var remark = ""
    /**
     * １列車の駅依存の情報を格納する。
     * このデータは駅数分できるため、サイズの大きいオブジェクトはメモリを圧迫します。
     * 省メモリのため文字列などを用いず、すべてlongで表記します。
     * longは64bitなので、各ビットごとに役割を持たせたいます。
     * 先頭より
     * 12bit free
     * 4bit 駅扱いを記述する。この4bitの値がそのままstopTypeとなる
     * 8bit 番線情報(駅の番線index)　0はデフォルト番線
     * 20bit 着時刻（秒単位） 最初の１bitは時刻存在フラグ
     * 20bit 発時刻（秒単位）最初の1bitは時刻存在フラグ
     */
    protected var time= ArrayList<Long>()


    protected var startTime=0
    protected var endTime=0
    /**
     * この列車が所属するDiaFile
     */
    override lateinit var diaFile: DiaFile
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
        result.append("\r\nRessyaTrack=")
        for (i in 0 until diaFile.stationNum) {
            var stationIndex = i
            if (direct == 1) {
                stationIndex = diaFile.stationNum - 1 - i
            }
            result.append(makeStopTxt(stationIndex)).append(",")
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
            result += timeInt2String(getArrivalTime(stationIndex)) + "/"
        }
        if (departExist(stationIndex)) {

            result += timeInt2String(getDepartureTime(stationIndex))
        }
        return result
    }
    private fun makeStopTxt(stationIndex:Int):String{
        var result=getStopNumber(stationIndex).toString()
        if(stationIndex==startStation){
            when(startAction){
                0->return result
                1->{
                    result+=";1/"+startExchangeStop+"$"
                    if(startExchangeTimeStart>=0){
                        result+=timeInt2String(startExchangeTimeStart)+"/"
                    }
                    if(startExchangeTimeEnd>=0){
                        result+=timeInt2String(startExchangeTimeEnd)
                    }
                    return result

                }
                2->{
                    result+=";2"
                    if(operation.isNotEmpty()){
                        result+="/"+operation

                    }
                    return result

                }
            }
        }
        if(stationIndex==endStation){
            when(endAction){
                0->return result
                1->{
                    result+=";1/"+endExchangeStop+"$"
                    if(endExchangeTimeStart>=0){
                        result+=timeInt2String(endExchangeTimeStart)+"/"
                    }
                    if(endExchangeTimeEnd>=0){
                        result+=timeInt2String(endExchangeTimeEnd)
                    }
                    return result

                }
                2->{
                    result+=";2"
                    return result
                }
            }
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

    override fun getArrivalTime(station: Int): Int {
        try {
            if (time[station] and 0x000008000000000L == 0L) {
                return -1
            }
            var result = time[station] and 0x0000007ffff00000L
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
    override fun getDepartureTime(station: Int): Int {
        try {
            if (time[station] and 0x000000000080000L == 0L) {
                return -1
            }
            val result = time[station] and 0x00000000007ffffL
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

    override fun setStopType(station: Int, value: Int) {
        var value = value.toLong()
        if (value > 16 || value < 0) {
            //error
            return
        }
        if (value == 8.toLong() || value == 9.toLong()) {
            value = STOP_TYPE_STOP.toLong()
        }
        value = value shl 48
        time[station] = time[station] and 0xFF0FFFFFFFFFFFFL
        time[station] = time[station] or value
    }

    /**
     * 発着番線をセットする
     */
    override fun setStopNumber(station: Int, value: Int) {
        var value = value.toLong()
        if (value >diaFile.station[station].trackName.size || value < 0) {
            //error
            return
        }
        value = value shl 40
        time[station] = time[station] and 0xFFF00FFFFFFFFFFL
        time[station] = time[station] or value
    }
    override fun getStopNumber(station:Int):Int{
        val result = time[station] and 0x0000FF0000000000L
        return (result ushr 40).toInt()

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
            time[station] = time[station] and 0xFFFFF00000FFFFFL
            time[station] = time[station] or 0x000008000000000L
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
            time[station] = time[station] and 0xFFFFFFFFFF00000L
            time[station] = time[station] or 0x000000000080000L
            time[station] = time[station] or result
        }
    }

    override fun getStopType(station: Int): Int {
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
        } else time[station] and 0x000008000000000L != 0L
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
        } else time[station] and 0x000000000080000L != 0L
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
        } else time[station] and 0x000008000080000L != 0L
    }

    fun split(str:String,char:Char,index:Int):String{
        var count=0
        val result=StringBuilder("")
        for(c in str){
            if(c==char){
                count++
            }else{
                if(count==index){
                    result.append(c)
                }
            }
        }
        return result.toString()
    }


    internal fun setStopNumber(str:String,direct:Int) {
        val stopString = str.split(",")
        for (i in stopString.indices) {
            if (stopString[i].isNotEmpty()) {
                setStopNumber((1 - 2 * direct) * i + direct * (stationNum - 1), Integer.parseInt(split(stopString[i],';',0)))
                if((1 - 2 * direct) * i + direct * (stationNum - 1) ==startStation){
                    val stopStr=split(stopString[i],';',1)
                    if(stopStr.isNotEmpty()){
                        startAction=Integer.parseInt(split(stopStr,'/',0))
                        val stopStr2=split(stopStr,'/',1)
                        if(startAction==1){
                            if(stopStr2.isNotEmpty()){
                                startExchangeStop=Integer.parseInt(split(stopStr2,'$',0))
                                if(split(split(stopStr2,'$',1),'/',0).isNotEmpty()) {
                                    startExchangeTimeStart =oudiaTime2Int(split(split(stopStr2, '$', 1), '/', 0))
                                }
                                if(split(stopStr,'/',2).isNotEmpty()) {
                                    startExchangeTimeEnd = oudiaTime2Int(split(stopStr,'/',2))
                                }
                            }
                        }
                        if(startAction==2){
                            operation=stopStr2
                        }
                    }
                }
                if((1 - 2 * direct) * i + direct * (stationNum - 1)==endStation){
                    val stopStr=split(stopString[i],';',1)
                    if(stopStr.isNotEmpty()){
                        endAction=Integer.parseInt(split(stopStr,'/',0))
                        val stopStr2=split(stopStr,'/',1)
                        if(endAction==1){
                            if(stopStr2.isNotEmpty()){
                                endExchangeStop=split(stopStr2,'$',0).toInt()
                                if(split(split(stopStr2,'$',1),'/',0).isNotEmpty()){
                                    endExchangeTimeStart=oudiaTime2Int(split(split(stopStr2,'$',1),'/',0))
                                }
                                if(split(stopStr,'/',2).isNotEmpty()) {
                                    endExchangeTimeEnd = oudiaTime2Int(split(stopStr,'/',2))
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    /**
     * この列車の発着時刻を入力します。
     * oudiaのEkiJikoku形式の文字列を発着時刻に変換し、入力していきます。
     * @param str　oudiaファイル　EkiJikoku=の形式の文字列
     * @param direct　方向
     */
    internal fun setTime(str: String, direct: Int) {
        try {
            val timeString = str.split(",")
            for (i in timeString.indices) {
                if (timeString[i].length == 0) {
                    setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Train.STOP_TYPE_NOSERVICE)
                } else {
                    if (!timeString[i].contains(";")) {
                        setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Integer.parseInt(timeString[i]))
                    } else {
                        setStopType((1 - 2 * direct) * i + direct * (stationNum - 1), Integer.parseInt(split(timeString[i],';',0)))
                        try {
                            val stationTime = split(timeString[i],';',1)
                            if (!stationTime.contains("/")) {
                                setDepartTime((1 - 2 * direct) * i + direct * (stationNum - 1), stationTime)
                            } else {
                                if (stationTime.split("/").size == 2) {
                                    setArriveTime((1 - 2 * direct) * i + direct * (stationNum - 1),split(stationTime,'/',0))
                                    setDepartTime((1 - 2 * direct) * i + direct * (stationNum - 1), split(stationTime,'/',1))
                                } else {
                                    setArriveTime((1 - 2 * direct) * i + direct * (stationNum - 1), split(stationTime,'/',0))
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
        if(value<0){
            time[station] = time[station] and 0xFFFFF00000FFFFFL
        }
        if (value >= 0 && value < 0x7FFFFF) {
            time[station] = time[station] and 0xFFFFF00000FFFFFL
            time[station] = time[station] or 0x000008000000000L
            time[station] = time[station] or (value shl 20)
        } else {
            if (value > 0xFFFFFF) {
                println(value)
            }
        }

    }

    private fun setDepartureTime(station: Int, value: Long) {
        if(value<0){
            time[station] = time[station] and 0xFFFFFFFFFF00000L
        }
        if (value >= 0 && value < 0x7FFFFF) {
            time[station] = time[station] and 0xFFFFFFFFFF00000L
            time[station] = time[station] or 0x000000000080000L
            time[station] = time[station] or value
        } else {
            if (value > 0xFFFFFF) {
                println(value)
            }
        }
    }

    override fun clone(allCopy: Boolean): AOdiaTrain {
        val train=Train(diaFile)
        train.time=ArrayList(time)
        train.number=number
        train.name=name
        train.type=type
        train.operation=operation
        train.count=count
        train.remark=remark
        train.direction=direction
        train.startAction=startAction
        train.startExchangeStop=startExchangeStop
        train.startExchangeTimeStart=startExchangeTimeStart
        train.startExchangeTimeEnd=startExchangeTimeEnd
        train.endAction=endAction
        train.endExchangeStop=endExchangeStop
        train.endExchangeTimeStart=endExchangeTimeStart
        train.endExchangeTimeEnd=endExchangeTimeEnd
        train.operation=operation
        return train
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
    fun oudiaTime2Int(str:String):Int{
        var h=0
        var m=0
        var s=0
        var result=0

        if (str.indexOf(":") < 0) {
            //no ":" char so str is only number
            when (str.length) {
                3 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    result = (h * 3600 + m * 60)
                }
                4 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    result = (h * 3600 + m * 60)
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(1, 3))
                    s = Integer.parseInt(str.substring(3, 5))
                    result = (h * 3600 + m * 60 + s)
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s)
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
                    result = (h * 3600 + m * 60)
                }
                5 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    result = (h * 3600 + m * 60)
                }
                6 -> {
                    h = Integer.parseInt(str.substring(0, 1))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(2, 4))
                    s = Integer.parseInt(str.substring(4, 6))
                    result = (h * 3600 + m * 60 + s)
                }
                7 -> {
                    h = Integer.parseInt(str.substring(0, 2))
                    if (h < 3) {
                        h = h + 24
                    }
                    m = Integer.parseInt(str.substring(3, 5))
                    s = Integer.parseInt(str.substring(5, 7))
                    result = (h * 3600 + m * 60 + s)
                }
                else -> result = -1
            }
        }
        return result

    }
    fun addStation(index:Int){
        if(getStopType(index)==1||getStopType(index)==2){
            if(getStopType(index+1)==1||getStopType(index+1)==2){
                time.add(index,0x002000000000000L)
                return
            }
        }
        time.add(index,0x000000000000000L)
        return

    }
    fun deleteStation(index:Int){
        time.removeAt(index)
    }
    fun deleteStop(station:Int,stop:Int){
        if(getStopNumber(station)==stop){
            setStopNumber(station,0)
        }
        if(getStopNumber(station)>stop){
            setStopNumber(station,getStopNumber(station)-1)
        }
    }
    fun addStop(station:Int,stop:Int){
        if(getStopNumber(station)>=stop){
            setStopNumber(station,getStopNumber(station)+1)
        }

    }


}
