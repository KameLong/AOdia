package com.kamelong.aodia.diadata

import android.app.Activity

import com.kamelong.aodia.editStation.AOdiaStationHistory
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
    fun addStation(s:AOdiaStation,index:Int)
    fun addStationRenew(index:Int)
    fun deleteStation(index:Int)
    fun setStation(s: AOdiaStation)
    fun setStationRenew( index:Int,editStopList:ArrayList<Int>)
    fun resetStation()

    fun stationTime(firstStation:Int,endStation:Int):Int{
        var result=100000
            for(d in 0 until getDiaNum()){
                if(getDiaName(d)=="基準運転時分"){
                    result=100000
                    for(t in 0 until getTrainNum(d,0)){
                        val train=getTrain(d,0,t)
                        if(train.existTime(firstStation)&&train.existTime(endStation)){
                            val value=train.getADTime(endStation)-train.getDATime(firstStation)
                            if(result>value){
                                result=value
                            }
                        }
                    }
                    for(t in 0 until getTrainNum(d,1)){
                        val train=getTrain(d,1,t)
                        if(train.existTime(firstStation)&&train.existTime(endStation)){
                            val value=train.getADTime(firstStation)-train.getDATime(endStation)
                            if(result>value){
                                result=value
                            }
                        }
                    }
                    if(result==100000){
                        return 120
                    }
                    return result
                }else{
                    for(t in 0 until getTrainNum(d,0)){
                        val train=getTrain(d,0,t)
                        if(train.existTime(firstStation)&&train.existTime(endStation)){
                            val value=train.getADTime(endStation)-train.getDATime(firstStation)
                            if(result>value){
                                result=value
                            }
                        }
                    }
                    for(t in 0 until getTrainNum(d,1)){
                        val train=getTrain(d,1,t)
                        if(train.existTime(firstStation)&&train.existTime(endStation)){
                            val value=train.getADTime(firstStation)-train.getDATime(endStation)
                            if(result>value){
                                result=value
                            }
                        }
                    }

                }
            }
        if(result==100000){
            return 120
        }
        if(result<30){
            return 30
        }
        return result

    }
    fun getStationTime():ArrayList<Int>{
        val result=ArrayList<Int>()
        result.add(0)
        for(s in 1 until stationNum){
            result.add(result[s-1]+stationTime(s-1,s))
        }
        return result
    }

}
