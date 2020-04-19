package com.kamelong.OuDia

import com.kamelong.tool.Color
import com.kamelong.tool.SDlog
import java.io.PrintWriter

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 列車種別１種類を表します
 */
class TrainType : Cloneable {
    /**
     * 種別名。
     */
    @JvmField
    var name = "普通"

    /**
     * 略称（種別名の略称）。
     * 規定値は、空文字列。
     */
    @JvmField
    var shortName = ""

    /**
     * 時刻表文字色(ダイヤグラムの列車情報の文字色を兼ねます)
     * 規定値は、黒。
     */
    @JvmField
    var textColor = Color("#000000")

    /**
     * 時刻表ビューで、この列車種別の時刻を表示するための時刻表フォント。
     * 範囲は、 0 以上、 JIKOKUHYOUFONT_COUNT 未満です。
     *
     * - 0：『時刻表ビュー 1』
     * - 1: 『時刻表ビュー 2』
     * - 2: 『時刻表ビュー 3』
     */
    var fontIndex = 0

    /**
     * 時刻表背景色、ダイヤのプロパティにおいて、背景色パターンが種別色の場合に参照されます。
     *
     * 規定値は、白。
     */
    var timeTableBackColor = Color("#FFFFFF")

    /**
     * ダイヤ線色
     */
    @JvmField
    var diaColor = Color("#000000")

    /**
     * 列車線(直線)の線の形状属性。
     */
    @JvmField
    var lineStyle = 0

    /**
     * ダイヤ線が太線かどうか
     */
    @JvmField
    var bold = false

    /**
     * 列車種別毎の、停車駅明示の方法。
     * false:停車駅明示=明示しない
     * true:デフォルト。ダイヤグラムビューで停車駅明示がONの場合は、短時間停車駅に○を描画します。
     */
    @JvmField
    var stopmark = true

    /**
     * 　親種別index
     * -1の時は親種別が存在しません
     */
    var parentIndex = -1

    /**
     * AOdia用
     */
    var showInTimeTable = true
    fun setValue(title: String?, value: String) {
        when (title) {
            "Syubetsumei" -> name = value
            "Ryakusyou" -> shortName = value
            "JikokuhyouMojiColor" -> textColor.setOuDiaColor(value)
            "JikokuhyouFontIndex" -> fontIndex = value.toInt()
            "JikokuhyouBackColor" -> timeTableBackColor.setOuDiaColor(value)
            "DiagramSenColor" -> diaColor.setOuDiaColor(value)
            "DiagramSenStyle" -> when (value) {
                "SenStyle_Jissen" -> lineStyle = 0
                "SenStyle_Hasen" -> lineStyle = 1
                "SenStyle_Tensen" -> lineStyle = 2
                "SenStyle_Ittensasen" -> lineStyle = 3
            }
            "DiagramSenIsBold" -> bold = value == "1"
            "StopMarkDrawType" -> stopmark = value == "EStopMarkDrawType_DrawOnStop"
            "ParentSyubetsuIndex" -> parentIndex = value.toInt()
        }
    }

    fun saveToFile(out: PrintWriter) {
        out.println("Ressyasyubetsu.")
        out.println("Syubetsumei=$name")
        out.println("Ryakusyou=$shortName")
        out.println("JikokuhyouMojiColor=" + textColor.oudiaString)
        out.println("JikokuhyouFontIndex=$fontIndex")
        out.println("JikokuhyouBackColor=" + timeTableBackColor.oudiaString)
        out.println("DiagramSenColor=" + diaColor.oudiaString)
        when (lineStyle) {
            0 -> out.println("DiagramSenStyle=SenStyle_Jissen")
            1 -> out.println("DiagramSenStyle=SenStyle_Hasen")
            2 -> out.println("DiagramSenStyle=SenStyle_Tensen")
            3 -> out.println("DiagramSenStyle=SenStyle_Ittensasen")
        }
        if (bold) {
            out.println("DiagramSenIsBold=1")
        }
        if (stopmark) {
            out.println("StopMarkDrawType=EStopMarkDrawType_DrawOnStop")
        } else {
            out.println("StopMarkDrawType=EStopMarkDrawType_Nothing")
        }
        if (parentIndex >= 0) {
            out.println("ParentSyubetsuIndex=$parentIndex")
        }
        out.println(".")
    }

    fun saveToOuDiaFile(out: PrintWriter) {
        out.println("Ressyasyubetsu.")
        out.println("Syubetsumei=$name")
        out.println("Ryakusyou=$shortName")
        out.println("JikokuhyouMojiColor=" + textColor.oudiaString)
        out.println("JikokuhyouFontIndex=$fontIndex")
        out.println("DiagramSenColor=" + diaColor.oudiaString)
        when (lineStyle) {
            0 -> out.println("DiagramSenStyle=SenStyle_Jissen")
            1 -> out.println("DiagramSenStyle=SenStyle_Hasen")
            2 -> out.println("DiagramSenStyle=SenStyle_Tensen")
            3 -> out.println("DiagramSenStyle=SenStyle_Ittensasen")
        }
        if (bold) {
            out.println("DiagramSenIsBold=1")
        }
        if (stopmark) {
            out.println("StopMarkDrawType=EStopMarkDrawType_DrawOnStop")
        }
        out.println(".")
    }

    public override fun clone(): TrainType {
        return try {
            val result = super.clone() as TrainType
            result.timeTableBackColor = timeTableBackColor.clone()
            result.diaColor = diaColor.clone()
            result.textColor = textColor.clone()
            result
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            TrainType()
        }
    }

    companion object {
        const val LINESTYLE_NORMAL = 0
        const val LINESTYLE_DASH = 1
        const val LINESTYLE_DOT = 2
        const val LINESTYLE_CHAIN = 3
    }

    init {
        name = "種別名未設定"
    }
}