package com.kamelong.aodia.diadata

/**
 */
interface AOdiaStation {
    var name:String
    var bigStation:Boolean
    var branchStation:Int
    var loopStation:Int

    /**
     * 駅表示スタイルを返す
     * 4bitの整数で表す[列車番号,番線、着時刻、発時刻]
     */
    fun getViewStyle(direct:Int):Int{
        return 0
    }

    /**
     * 駅表示スタイルのうち、発着時刻に関するbit値部分のみ返す
     */
    fun getTimeViewStyle():Int

    /**
     * 駅表示スタイルのうち、発着に関するbit値部分のみ編集する
     */
    fun setTimeViewStyle(value:Int)

    /**
     * 駅表示スタイルのうち、番線表示に関するbit値部分のみ返す
     */
    fun getStopStyle():Int
    /**
     * 駅表示スタイルのうち、番線表示に関するbit値部分のみ編集する
     */
    fun setStopStyle(value:Int)
    /**
     * 駅表示スタイルのうち、ダイヤグラムの番線表示
     */
    fun getStopDiaStyle():Boolean
    /**
     * 駅表示スタイルのうち、ダイヤグラムの番線表示を編集する
     */
    fun setStopDiaStyle(value:Boolean)

    /**
     * このオブジェクトのコピーを作成する
     */
    fun copy(origin:AOdiaStation):AOdiaStation







}