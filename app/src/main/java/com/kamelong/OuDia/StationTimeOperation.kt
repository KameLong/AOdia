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
 * 駅での運用作業を表すクラスです。
 * １つの駅に対して複数のStationTimeOperationを用いることができます。
 * また、連結や解結を行う際の相手列車の運用作業は連結作業のStationTimeOperationに紐付けられます。
 * KameLongはこのクラスの使用方法を理解していないため、
 * OuDia2ndのソースコード（CentDetAfterOperation.hやCentDetBeforeOperation.h）をご覧ください。
 */
class StationTimeOperation() : Cloneable {
    /**
     * 作業種類です。
     * 0:入れ替え
     * 1:増結
     * 2:解結
     * 3:出区
     * 4:路線外始発
     * 5:前列車接続
     *
     *
     */
    var operationType = 0

    /*
     * 停車前運用の場合

     入換:この作業の入換着時刻を、時刻表の表示モードにおける
     当駅の着時刻とみなすか否かを表す値です。
     false(規定値):着時刻として扱いません。
     true:着時刻として扱います。
     なお、前作業内に複数の入換が存在し、二つ以上の入換作業において、
     この値がtrueの場合、前作業コンテナ内で最もIndexが大きいもの(時系列で後ろの物)が有効になります。
     増結:増結編成を、運用番号基準で親編成の前後どちらに接続するかを表します
     (例)親編成が01A、増結編成が03Aの運用番号の場合、
     true:増結編成を親編成よりも前に連結します。例の運用番号は[03A],[01A]となります
     false(規定値):増結編成を親編成よりも後ろに連結します。例の運用番号は[01A],[03A]となります
     解結:未使用
     出区:未使用
     路線外始発:未使用
     前列車接続:種別変更
     この作業が、別列車から種別変更により繋がっている場合、
     この値はtrueになります
     (注)この値がtrueの時は種別変更、ではなく種別変更が決まった場合に、この値がtrueになります
     */
    /*
     * 後運用の場合
     入換:この作業の入換発時刻を、時刻表の表示モードにおける
     当駅の発時刻とみなすか否かを表す値です。
     false(規定値):発時刻として扱いません。
     true:発時刻として扱います。
     なお、後作業内に複数の入換が存在し、二つ以上の入換作業において、
     この値がtrueの場合、後作業コンテナ内で最もIndexが若いもの(時系列で前の物)が有効になります。
     増結:増結編成を、運用番号基準で親編成の前後どちらに接続するかを表します
     (例)親編成が01A、増結編成が03Aの運用番号の場合、
     true:増結編成を親編成よりも前に連結します。例の運用番号は[03A],[01A]となります
     false(規定値):増結編成を親編成よりも後ろに連結します。例の運用番号は[01A],[03A]となります
     解結:未使用
     入区:未使用
     路線外終着:未使用
     次列車接続:後列車接続成功
     この値がtrueの時は、運用探索において、後列車が見つかったことを示します。
     その場合、在線表表示駅において、この作業がある番線の在線横線は表示せず、
     後列車に委任します。
     */
    var boolData1 = false

    /*
     *　停車前運用の場合
     入換:入換前の番線
     増結:未使用
     解結:運用番号上での、解結する編成位置を表します。
     0:後方の任意数の編成を解結します(規定値) 。
     指定数が編成数以上の場合は、編成数-1編成を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。
     1:前方の任意数の編成を解結します。
     指定数が編成数以上の場合、編成数-1編成を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。
     2:前方の任意数の編成"以外"を解結します。
     指定数が編成数以上の場合は、前運番の末尾を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。

     従って、前運用番号が一つの場合は、必ずそれを主編成に充て、解結編成は空白運番となります。
     また、前運用番号が二つの場合は、片方が主編成、もう片方が解結編成となります。
     出区:未使用
     路線外始発:路線外発着駅のIndex
     前列車接続:解結駅Order
     この作業が、別列車の解結編成から繋がっている場合、
     解結が行われた駅の駅Orderが割り当てられます。
     カスタム表示モードにおける↴マークの表示位置になります。
     デフォルト値は-1です。
     */
    /*
     * 停車後運用の場合
     入換:入換前の番線
     増結:未使用
     0:後方の任意数の編成を解結します(規定値) 。
     指定数が編成数以上の場合は、編成数-1編成を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。
     1:前方の任意数の編成を解結します。
     指定数が編成数以上の場合、編成数-1編成を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。
     2:前方の任意数の編成"以外"を解結します。
     指定数が編成数以上の場合は、前運番の末尾を解結します。
     ただし、前運用番号が一つの場合は、前運用番号を主編成に、空白運番を解結編成に充てます。

     従って、前運用番号が一つの場合は、必ずそれを主編成に充て、解結編成は空白運番となります。
     また、前運用番号が二つの場合は、片方が主編成、もう片方が解結編成となります。
     入区:未使用
     路線外終着:路線外終着駅のIndex
     次列車接続:増結駅Order
     この作業が、別列車の増結編成に繋がる場合、
     増結が行われる駅の駅Orderが割り当てられます。
     カスタム表示モードにおける↳マークの表示位置になります。
     デフォルト値は-1です。
     */
    var intData1 = -1

    /*
     * 停車前運用の場合
     入換:未使用
     増結:未使用
     解結:運用番号上での、解結する編成数もしくは残す編成数です。
     m_bBoolData1の値により意味が変わります。
     0:この値の編成数だけ後方から解結します。
     1:この値の編成数だけ前方から解結します。
     2:この値の編成数を除き、後方から解結します。
     (例)元編成が[01A],[03A],[05A],[07A],[09A]の場合
     0,2の場合、解結編成は[07A],[09A]
     1,2の場合、解結編成は[01A],[03A]
     2,2の場合、解結編成は[05A],[07A],[09A]
     規定値は1です。
     出区:未使用
     路線外始発:未使用
     前列車接続:前列車方向(在線表あり駅)
     運用機能により、この作業が別の作業から接続された場合、
     この作業が行われる番線に移動する縦線が、どちらの方向から延びているかを示します。

     -2:この列車の列車方向の起点方から(入換があるので確定)
     -1:この列車の列車方向の起点方から(反転駅設定により変化する)
     0:接続なし,デフォルト値
     1:この列車の列車方向の終点方から(反転駅設定により変化する)
     2:この列車の列車方向の終点方から(入換があるので確定)
     */
    /*
     * 停車後運用の場合
     入換:未使用
     増結:未使用
     解結:運用番号上での、解結する編成数もしくは残す編成数です。
     m_bBoolData1の値により意味が変わります。
     0:この値の編成数だけ後方から解結します。
     1:この値の編成数だけ前方から解結します。
     2:この値の編成数を除き、後方から解結します。
     (例)元編成が[01A],[03A],[05A],[07A],[09A]の場合
     0,2の場合、解結編成は[07A],[09A]
     1,2の場合、解結編成は[01A],[03A]
     2,2の場合、解結編成は[05A],[07A],[09A]
     規定値は1です。
     入区:未使用
     路線外終着:未使用
     次列車接続:列車方向
     運用機能により、この作業から別に作業から接続する場合、
     この作業が行われる番線に移動する縦線が、どちらの方向から延びているかを示します。
     junctionOperationの時に、この値を次作業(BeforeNone)に書き込みます。
     なお、この作業を含む列車と、次作業を含む列車の列車方向が異なる場合は、値が反転します。
     -2:この列車の列車方向の起点方から(入換があるので確定)
     -1:この列車の列車方向の起点方から(反転駅設定により変化する)
     0:解結後に入換無しで接続(デフォルト値)
     1:(この値にはなりません)
     2:この列車の列車方向の終点方から(入換があるので確定)
     */
    var intData2 = -1
    /*
    停車前運用の場合

     入換:列車の入換発時刻
     増結:増結を行う時刻、ダイヤグラムにおける、増結マークの表示位置になるほか、
     増結編成の前作業の作業時刻の原点になります。
     NULL時刻の場合は、列車の着時刻・直前の入換着時刻・
     出区時刻・路線外始発列車の当駅着時刻が、適用されます。
     解結:解結を行う時刻、ダイヤグラムにおける、解結マークの表示位置になるほか、
     解結編成の後作業の作業時刻の原点になります。
     NULL時刻の場合は、列車の発時刻・直後の入換発時刻・
     入区時刻・路線外終着列車の当駅発時刻が、適用されます。
     出区:出区時刻、ダイヤグラムビューにおける丸印の位置を示します。
     路線外始発:路線外始発駅の発車時刻
     前列車接続:起点時刻、前列車からのの接続を探索する際、この時刻を基準とします。
     デフォルト値はNULL時刻です。
     */
    /*
     * 停車後運用の場合
     *
     入換:列車の入換発時刻
     増結:増結を行う時刻、ダイヤグラムにおける、増結マークの表示位置になるほか、
     増結編成の前作業の作業時刻の原点になります。
     NULL時刻の場合は、列車の着時刻・直前の入換着時刻・
     出区時刻・路線外終着列車の当駅着時刻が、適用されます。
     解結:解結を行う時刻、ダイヤグラムにおける、解結マークの表示位置になるほか、
     解結編成の後作業の作業時刻の原点になります。
     NULL時刻の場合は、列車の発時刻・直後の入換発時刻・
     入区時刻・路線外終着列車の当駅発時刻が、適用されます。
     入区:入区時刻、ダイヤグラムビューにおける丸印の位置を示します。
     路線外終着:当駅の発時刻です。
     従来は、CentDedEkijikokuにおける発時刻を用いていましたが、作業側に移動します。
     これは、(データ上の)終着駅における時刻形式を統一させるためです。
     この値が空の時は、当駅の着時刻と同時刻とみなします。
     次列車接続:終点時刻、次列車への接続を探索する際、この時刻を基準とします。
     デフォルト値はNULL時刻です。
     */
    /**
     * 時刻は秒単位で表現し、0:00:00を0とします。
     */
    var time1 = -1
    /*
     *　停車前運用の場合
     *
     入換:列車の入換着時刻
     この値がNULL時刻の場合、入換発時刻と同一とみなします。
     増結:未使用
     解結:未使用
     出区:未使用
     路線外始発:当駅の着時刻です。
     従来は、CentDedEkijikokuにおける着時刻を用いていましたが、作業側に移動します。
     これは、(データ上の)始発駅における時刻形式を統一させるためです。
     この値が空の時は、当駅の発時刻と同時刻とみなします。
     前列車接続:前列車接続時刻(在線表有り駅)
     運用機能により、この作業が別の作業から接続された場合、前の作業の終点時刻がここに入ります。
     デフォルト値はNULL時刻です。

     * 時刻は秒単位で表現し、0:00:00を0とします。

     */
    /*
     *
     * 停車後運用の場合。
     *
     入換:列車の入換着時刻
     この値がNULL時刻の場合、入換発時刻と同一とみなします。
     増結:未使用
     解結:未使用
     入区:未使用
     路線外終着:路線外終着駅の到着時刻
     次列車接続:列車接続時刻(在線表有り駅)
     運用機能により、この作業から別の作業に接続する場合、予めこの作業の接続時刻を求めておき、
     junctionOperationの時に、この値を次作業(BeforeNone)に書き込みます。
     デフォルト値はNULL時刻です。
     */
    /**
     * 時刻は秒単位で表現し、0:00:00を0とします。
     */
    var time2 = -1

    /*
     * 停車前運用の場合
     *
     入換:未使用
     増結:増結後運用番号
     複数編成が連結した状態では、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     出区・路線外始発以外の運用番号は、運用探索によって求まります。
     解結:解結前運用番号
     複数編成が連結した状態では、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     出区・路線外始発以外の運用番号は、運用探索によって求まります。
     入区:運用番号
     複数編成が連結した状態で入区する場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     この作業の運用番号は、運用探索によって求まります。
     路線外終着:運用番号
     複数編成が連結した状態で路線外終着駅に到着する場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     次列車接続:運用番号
     複数編成が連結した状態の場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     この作業の運用番号は、運用探索によって求まります。


     */
    /*
     * 停車後運用の場合。
     *
     入換:未使用
     増結:増結後運用番号
     複数編成が連結した状態では、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     出区・路線外始発以外の運用番号は、運用探索によって求まります。
     解結:解結前運用番号
     複数編成が連結した状態では、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     出区・路線外始発以外の運用番号は、運用探索によって求まります。
     入区:運用番号
     複数編成が連結した状態で入区する場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     この作業の運用番号は、運用探索によって求まります。
     路線外終着:運用番号
     複数編成が連結した状態で路線外終着駅に到着する場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     次列車接続:運用番号
     複数編成が連結した状態の場合、各編成に対して一つづつ運用番号を割り当て、
     それを配列形式で保持します。
     この作業の運用番号は、運用探索によって求まります。
     */
    var operationNumberList = ArrayList<String>()

    /**
     *
     */
    var afterOperation = ArrayList<StationTimeOperation>()

    /**
     *
     */
    var beforeOperation = ArrayList<StationTimeOperation>()

    constructor(value: String) : this() {
        setValue(value)
    }

    /**
     * OuDia2ndV2形式の運用情報を読み込みます
     * @param value
     */
    fun setValue(value: String) {
        val values = value.split("\\$").dropLastWhile { it.isEmpty() }.toTypedArray()
        val value1 = values[0].split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
        val value2: Array<String>
        operationType = value1[0].toInt()
        when (operationType) {
            0 -> {
                //入れかえ
                value2 = values[1].split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
                intData1 = value1[1].toInt()
                time2 = StationTime.Companion.timeStringToInt(value2[0])
                time1 = StationTime.Companion.timeStringToInt(value2[1])
                boolData1 = values[2] == "1"
            }
            1 -> {
                boolData1 = value1[1] == "1"
                time1 = StationTime.Companion.timeStringToInt(values[1])
            }
            2 -> {
                value2 = values[1].split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
                intData1 = value2[0].toInt()
                intData2 = value1[1].toInt()
                time1 = StationTime.Companion.timeStringToInt(value2[1])
            }
            3 -> {
                time1 = StationTime.Companion.timeStringToInt(value1[1])
                operationNumberList = ArrayList()
                if (values.size > 1) {
                    operationNumberList.addAll(Arrays.asList(*values[1].split(";").dropLastWhile { it.isEmpty() }.toTypedArray()))
                }
            }
            4 -> try {
                if (value1.size > 1) {
                    intData1 = value1[1].toInt()
                }
                value2 = values[1].split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
                time1 = StationTime.Companion.timeStringToInt(value2[0])
                time2 = StationTime.Companion.timeStringToInt(value2[1])
                if (values.size > 3) {
                    operationNumberList = ArrayList()
                    operationNumberList.addAll(Arrays.asList(*values[3].split(";").dropLastWhile { it.isEmpty() }.toTypedArray()))
                }
            } catch (e: Exception) {
                SDlog.log(e)
            }
            5 -> {
                time1 = StationTime.Companion.timeStringToInt(value1[1])
                if (values.size > 1) {
                    value2 = values[1].split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
                    boolData1 = value2[0] == "1"
                    operationNumberList = ArrayList()
                    if (value2.size > 1) {
                        operationNumberList.addAll(Arrays.asList(*value2[1].split(";").dropLastWhile { it.isEmpty() }.toTypedArray()))
                    }
                }
            }
        }
    }

    val ouDiaString: String
        get() {
            var result = "" + operationType
            when (operationType) {
                0 -> {
                    result += "/$intData1$"
                    result += StationTime.Companion.timeIntToOuDiaString(time2) + "/" + StationTime.Companion.timeIntToOuDiaString(time1)
                    result += "$"
                    result += if (boolData1) {
                        "1"
                    } else {
                        "0"
                    }
                }
                1 -> {
                    result += "/"
                    result += if (boolData1) {
                        "1"
                    } else {
                        "0"
                    }
                    result += "$"
                    result += StationTime.Companion.timeIntToOuDiaString(time1)
                }
                2 -> {
                    result += "/$intData2$$intData1/"
                    result += StationTime.Companion.timeIntToOuDiaString(time1)
                }
                3 -> {
                    result += "/"
                    result += StationTime.Companion.timeIntToOuDiaString(time1)
                    result += "$"
                    for (s in operationNumberList) {
                        result += "$s;"
                    }
                    result = result.substring(0, result.length - 1)
                }
                4 -> {
                    result += "/"
                    result += intData1
                    result += "$"
                    result += StationTime.Companion.timeIntToOuDiaString(time1) + "/" + StationTime.Companion.timeIntToOuDiaString(time2) + "$"
                    for (s in operationNumberList) {
                        result += "$s;"
                    }
                    result = result.substring(0, result.length - 1)
                }
                5 -> {
                    result += "/"
                    result += StationTime.Companion.timeIntToOuDiaString(time1)
                    result += "$"
                    result += if (boolData1) {
                        "1"
                    } else {
                        "0"
                    }
                    result += "/"
                    for (s in operationNumberList) {
                        result += "$s;"
                    }
                    result = result.substring(0, result.length - 1)
                }
            }
            return result
        }

    public override fun clone(): StationTimeOperation {
        return try {
            val result = super.clone() as StationTimeOperation
            result.afterOperation = ArrayList()
            for (value in afterOperation) {
                result.afterOperation.add(value.clone())
            }
            result.beforeOperation = ArrayList()
            for (value in beforeOperation) {
                result.beforeOperation.add(value.clone())
            }
            //            result.operationNumberList=(ArrayList<String>)operationNumberList.clone();
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            StationTimeOperation()
        }
    }
}