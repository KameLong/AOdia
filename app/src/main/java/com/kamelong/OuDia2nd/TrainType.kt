package com.kamelong.OuDia2nd

import com.kamelong.tool.Color


/**
 */
class TrainType {
    var name = ""
    /**
     * 種別名略称
     */
    var shortName = ""
    set(value) {
        //shift-jis特有の0x5c問題の解決策です
        if (value.length > 2) {
            field = value.substring(0, 2)
        } else if(value.isNotEmpty()){
            field = value
        }
    }
    /**
     * 時刻表文字色
     */
    /**
     * 時刻表文字色をセットする
     * @param color 色を表すint
     */
    var textColor = Color()
    /**
     * ダイヤグラム線色
     */
    /**
     * ダイヤグラム線色をセットする
     * @param color 色を表すint
     */
    var diaColor = Color()
    /**
     * ダイヤグラムを太線で描画するか
     */
    /**
     * 太字にするかどうかを返す
     * @return 太字にするときtrue
     */
    var lineBold = false
    /**
     * ダイヤグラムの線のスタイル
     * LINESTYLE_XXXの定数を用いる
     */
    /**
     * 線スタイルを取得します
     * @return 線スタイルを表すint
     */
    var lineStyle = LINESTYLE_NORMAL

    /**
     * ダイヤグラム上で、停車駅を表示するかどうか
     */
    /**
     * 停車駅表示を取得する
     * @return 停車駅表示を行うとき、trueを返す
     */
    var showStop = false

    var fontNumber = 0

    /**
     * コンストラクタ。
     * 特に情報がない列車種別を作成する。
     * 種別名から文字列
     * 各色はすべて黒
     */
    constructor() {}

    /**
     * oudia保存形式のテキストデータを作成する
     * @return
     */
    fun makeTrainTypeText(): StringBuilder {
        val result = StringBuilder("Ressyasyubetsu.")
        result.append("\r\nSyubetsumei=").append(name)
        result.append("\r\nRyakusyou=").append(shortName)
        result.append("\r\nJikokuhyouMojiColor=").append(textColor.oudiaString)
        result.append("\r\nDiagramSenColor=").append(diaColor.oudiaString)
        when (lineStyle) {
            LINESTYLE_NORMAL -> result.append("\r\nDiagramSenStyle=").append("SenStyle_Jissen")
            LINESTYLE_DASH -> result.append("\r\nDiagramSenStyle=").append("SenStyle_Hasen")
            LINESTYLE_DOT -> result.append("\r\nDiagramSenStyle=").append("SenStyle_Tensen")
            LINESTYLE_CHAIN -> result.append("\r\nDiagramSenStyle=").append("SenStyle_Ittensasen")
        }
        if (lineBold) {
            result.append("\r\nDiagramSenIsBold=1")
        }
        if (showStop) {
            result.append("\r\nStopMarkDrawType=").append("EStopMarkDrawType_DrawOnStop")
        }
        result.append("\r\nJikokuhyouFontIndex=").append(fontNumber)
        result.append("\r\n.\r\n")
        return result

    }

    /**
     * ダイヤグラム線スタイルをセットします。
     * oudiaでは線スタイルは文字列で管理しているので、
     * それぞれの文字列と一致しているかどうかを確認し、lineStyleに数値を代入します。
     * @param value oudiaのSenStyle項目の文字列
     */
    fun setLineStyle(value: String) {
        when (value) {
            "SenStyle_Jissen" -> lineStyle = LINESTYLE_NORMAL
            "SenStyle_Hasen" -> lineStyle = LINESTYLE_DASH
            "SenStyle_Tensen" -> lineStyle = LINESTYLE_DOT
            "SenStyle_Ittensasen" -> lineStyle = LINESTYLE_CHAIN
            "0" -> lineStyle = LINESTYLE_NORMAL
            "1" -> lineStyle = LINESTYLE_DASH
            "2" -> lineStyle = LINESTYLE_DOT
            "3" -> lineStyle = LINESTYLE_CHAIN
        }
    }

    /**
     * ダイヤグラム線を太字にするかをセットします。
     * @param value 太字にするなら正の数　細いままなら0
     */
    fun setLineBold(value: Int) {
        if (value == 0) {
            lineBold = false
        } else {
            lineBold = true
        }
    }

    /**
     * ダイヤグラム線を太字にするかをセットします。
     * oudiaのファイルで対応する項目が"1"か"0"なので、
     * この手法で処理します
     * @param value 太字にするときは"1"
     */
    fun setLineBold(value: String) {
        if (value == "1") {
            lineBold = true
        } else {
            lineBold = false
        }
    }

    /**
     * 停車駅表示のセット
     * @param value oudiaの停車駅表示を示す文字列
     */
    fun setShowStop(value: String) {
        if (value == "EStopMarkDrawType_DrawOnStop") {
            showStop = true
        } else {
            showStop = false
        }
    }

    /**
     * 時刻表文字色をセットする
     * oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     * @param color 色を表す文字列
     */
    internal fun setTextColor(color: String) {
        textColor.setOuDiaColor(color)
    }

    /**
     * ダイヤグラム文字色をセットする
     * oudiaファイルでの色表記は"aabbggrr"の8文字の文字列
     * @param color 色を表す文字列
     */
    internal fun setDiaColor(color: String) {
        diaColor.setOuDiaColor(color)
    }



    companion object {

        val LINESTYLE_NORMAL = 0
        val LINESTYLE_DASH = 1
        val LINESTYLE_DOT = 2
        val LINESTYLE_CHAIN = 3
    }


}
