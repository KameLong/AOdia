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
    fun getViewStyle(direct:Int):Int

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
    fun getShowStopStyle():Int

    /**
     * 駅表示スタイルのうち、番線表示を行うときはtrueを返す
     */
    fun getStopStyle(direct:Int):Boolean
    /**
     * 駅表示スタイルのうち、番線表示に関するbit値部分のみ編集する
     */
    fun setShowStopStyle(value:Int)
    /**
     * 駅表示スタイルのうち、ダイヤグラムの番線表示
     */
    fun getStopDiaStyle():Boolean
    /**
     * 駅表示スタイルのうち、ダイヤグラムの番線表示を編集する
     */
    fun setStopDiaStyle(value:Boolean)

    /**
     * 所属番線数
     */
    val stopNum:Int
    /**
     * 番線名
     */
    fun getStopName(index:Int):String
    fun setStopName(index:Int,value:String)
    /**
     * 番線略称
     */
    fun getShortName(index:Int):String
    fun setShortName(index:Int,value:String)
    /**
     * 番線追加
     */
    fun addStop(index:Int)
    /**
     * 番線削除
     */
    fun deleteStop(index:Int):Boolean


    /**
     * 本線を表す
     */
    var downMain :Int
    var upMain :Int
    /**
     * このオブジェクトのコピーを作成する
     */
    fun clone():AOdiaStation

    /**
     * この駅は末端に向かう分岐線の終点である
     * @return 分岐点
     */
    fun branchEnd():Int
    /**
     * この駅は始点に向かう分岐線の始発である
     * @return 分岐点
     */
    fun branchStart():Int

    /**
     * この駅は分岐点である
     * @return 分岐親路線
     */
    fun branchRoot():Int





}