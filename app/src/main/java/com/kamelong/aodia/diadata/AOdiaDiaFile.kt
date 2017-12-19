package com.kamelong.aodia.diadata

import android.app.Activity
import android.content.Context

import com.kamelong.JPTI.JPTI
import com.kamelong.JPTI.Operation
import com.kamelong.JPTI.Service
import com.kamelong.aodia.AOdiaActivity
import java.io.File
import java.util.ArrayList

/**
 * AOdiaで使用するDiaFile
 * OuDiaの処理をベースにしている。
 */

interface AOdiaDiaFile {
    var activity:Activity
    var filePath:String
    var menuOpen:Boolean
    fun getDiaNum():Int
    fun getDiaName(index:Int):String
    fun setDiaName(index:Int,value:String)
    fun addNewDia(index:Int,value:String)

    fun getStation(index:Int):AOdiaStation
    val stationNum:Int
    fun getStationList():ArrayList<AOdiaStation>

    val trainTypeNum:Int
    fun getTrainType(index:Int):AOdiaTrainType
    fun addTrainType(trainType:AOdiaTrainType)

    fun getTrainNum(diaIndex:Int,direct:Int):Int
    fun getTrain(diaIndex:Int,direct:Int,trainIndex:Int):AOdiaTrain
    /**
     * 新しい列車を作ります
     */
    fun getNewTrain(direction:Int):AOdiaTrain

    /**
     * 新しい列車を挿入します
     */
    fun addTrain(diaIndex: Int,direct: Int,index:Int,train:AOdiaTrain)
    /**
     * 列車を削除します
     * return 削除した列車のIndex
     */
    fun deleteTrain(diaIndex: Int,direct: Int,train:AOdiaTrain):Int


    fun setTrain(diaNum:Int,direction:Int,trainNum:Int,train:AOdiaTrain)


    /**
     * ファイル形式で保存します
     */
    fun save(outFile:File)

}
