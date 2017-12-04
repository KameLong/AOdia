package com.kamelong.aodia.diadata

/**
 */
interface AOdiaStation {
    var name:String
    var shortName:String
    var mainStation:Boolean

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



}