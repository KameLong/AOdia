package com.kamelong.aodia.diadata

/**
 * AOdiaで使用するTrainのInterface
 */
interface AOdiaTrain {
    var name:String
    var number:String
    var count:String
    var operation:String
    var remark:String

    fun getDepatureTime(station:Int):Int
    fun getArrivalTime(station:Int):Int
    fun getDepatureTime(station:Int,startTime: Int):Int
    fun getArrivalTime(station:Int,startTime:Int):Int

    fun getStopStyle(station:Int)

    val trainType:AOdiaTrainType



}