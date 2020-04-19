package com.kamelong.OuDia

import java.io.PrintWriter

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */
/**
 * 番線１つを表します。
 * 全てのStationTrackはStationによって管理されます
 */
class StationTrack : Cloneable {
    /**
     * 番線名
     */
    var trackName = ""

    /**
     * 番線略称(共通or下り)
     */
    var trackShortName = ""

    /**
     * 番線略称(上り)
     * この要素は空白も可能です。
     * 空白の場合、m_strTrackRyakusyouが上下両方に対応します。
     */
    var trackShortNameUp = ""

    constructor(name: String, shortName: String) {
        trackName = name
        trackShortName = shortName
        trackShortNameUp = ""
    }

    constructor() {}

    /**
     * oudiaファイルを1行読み込む
     */
    fun setValue(title: String?, value: String) {
        when (title) {
            "TrackName" -> trackName = value
            "TrackRyakusyou" -> trackShortName = value
            "TrackNoboriRyakusyou" -> trackShortNameUp = value
        }
    }

    /**
     * oudia2nd 形式でファイルを保存する
     */
    fun saveToFile(out: PrintWriter) {
        out.println("EkiTrack2.")
        out.println("TrackName=$trackName")
        out.println("TrackRyakusyou=$trackShortName")
        if (trackShortNameUp.length != 0) {
            out.println("TrackNoboriRyakusyou=$trackShortNameUp")
        }
        out.println(".")
    }

    public override fun clone(): StationTrack {
        try {
            return super.clone() as StationTrack
        } catch (e: CloneNotSupportedException) {
            e.printStackTrace()
        }
        return StationTrack()
    } /*
    =================================
    ここまでOuDiaライブラリ共通の処理
    =================================
     */
}