package com.kamelong.OuDia

import com.kamelong.tool.SDlog
import java.io.PrintWriter

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * １つ路線外発着駅を表します。
 * OuterTerminalは全てStationに管理されます。
 */
class OuterTerminal : Cloneable {
    /**
     * 路線外発着駅名です。
     * 作業設定画面で用います。
     */
    @JvmField
    var outerTerminalName = ""

    /**
     * 路線外発着駅名の時刻表ビューにおける略称です。
     * 空の場合は、OuterTerminalEkimeiをそのまま用います。
     * 文字数制限は当面ありません
     */
    @JvmField
    var outerTerminalTimeTableName = ""

    /**
     * 路線外発着駅名のダイアグラムビューにおける略称です。
     * 空の場合は、OuterTerminalEkimeiの頭文字になります。
     * 文字数制限は当面ありません
     */
    var outerTerminalDiaName = ""

    constructor() {}

    /**
     * 駅名を指定して生成する
     * @param name 路線外駅名
     */
    constructor(name: String) {
        outerTerminalName = name
        outerTerminalTimeTableName = name
        outerTerminalDiaName = if (name.length > 1) {
            name.substring(0, 1)
        } else {
            name
        }
    }

    /**
     * oudiaの1行を読み込む
     */
    fun setValue(title: String?, value: String) {
        when (title) {
            "OuterTerminalEkimei" -> outerTerminalName = value
            "OuterTerminalJikokuRyaku" -> outerTerminalTimeTableName = value
            "OuterTerminalDiaRyaku" -> outerTerminalDiaName = value
        }
    }

    /**
     * oudia2nd　形式でファイルを保存する
     */
    fun saveToFile(out: PrintWriter) {
        out.println("OuterTerminal.")
        out.println("OuterTerminalEkimei=$outerTerminalName")
        out.println("OuterTerminalJikokuRyaku=$outerTerminalTimeTableName")
        out.println("OuterTerminalDiaRyaku=$outerTerminalDiaName")
        out.println(".")
    }

    public override fun clone(): OuterTerminal {
        return try {
            super.clone() as OuterTerminal
        } catch (e: CloneNotSupportedException) {
            SDlog.log(e)
            OuterTerminal()
        }
    }
}