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
    var lineName:String
    var comment:String
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
    var predictTime:ArrayList<Int>
    fun renewPredictTime(){
        for(i in 0 until stationNum){
            predictTime.add(-1)
        }

        predictTime[0]=0
        reNewPredictTimeFoward(1)
    }
    private fun reNewPredictTimeFoward(i:Int){
        if(i<=0||i>=stationNum)return
        if(predictTime[i]>=0)return
        if(getStation(i).branchStation>i){
            predictTime[i]=predictTime[i-1]+stationTime(i-1,i)
            predictTime[getStation(i).branchStation]=predictTime[i]
            reNewPredictTimeFoward(i+1)

        }else if(getStation(i).branchStation>=0){
            predictTime[i]=predictTime[getStation(i).branchStation]
            reNewPredictTimeBack(i-1)
            reNewPredictTimeFoward(i+1)
        }else{
            predictTime[i]=predictTime[i-1]+stationTime(i-1,i)
        }
    }
    private fun reNewPredictTimeBack(i:Int){
        if(i<0||i>=stationNum-1)return
        if(predictTime[i]>=0)return
        if(getStation(i).branchStation>i){
            predictTime[i]=predictTime[getStation(i).branchStation]
            reNewPredictTimeFoward(i+1)
            reNewPredictTimeBack(i-1)
        }else if(getStation(i).branchStation>=0){
            predictTime[i]=predictTime[i+1]-stationTime(i,i+1)
            predictTime[getStation(i).branchStation]=predictTime[i]
            reNewPredictTimeBack(i-1)
        }else{
            predictTime[i]=predictTime[i+1]-stationTime(i,i+1)
            reNewPredictTimeBack(i-1)

        }
    }
    fun getOperationTrains(diaIndex:Int,baseTrain:AOdiaTrain):ArrayList<AOdiaTrain>{
        val result=ArrayList<AOdiaTrain>()
        result.add(baseTrain)
        var nowTrain=baseTrain
        while(true){
            //後方列車リストを作成する
            val endStation=nowTrain.endStation
            val endStop=
                    if(nowTrain.endExchangeStop>0){nowTrain.endExchangeStop}else{nowTrain.getStopNumber(endStation)}
            val endTime=if(nowTrain.endExchangeStop>0&&nowTrain.endExchangeTimeEnd>=0){
                nowTrain.endExchangeTimeEnd
            }else{
                nowTrain.getADTime(endStation)
            }
            var valueTime=200000
            var valueTrain:AOdiaTrain?=null
            for(i in 0 until getTrainNum(diaIndex,0)){
                val train=getTrain(diaIndex,0,i)
                if(train.startStation==endStation){
                    val startStop=if(nowTrain.startExchangeStop>0){nowTrain.startExchangeStop}else{nowTrain.getStopNumber(endStation)}
                    if(startStop==endStop){
                        if(train.startExchangeTimeStart>=0){
                            if(train.startExchangeTimeStart>=endTime&&train.startExchangeTimeStart<valueTime){
                                valueTime=train.startExchangeTimeStart
                                valueTrain=train
                            }
                        }else{
                            if(train.getDATime(train.startStation)>=endTime&&train.getDATime(train.startStation)<valueTime){
                                valueTime=train.getDATime(train.startStation)
                                valueTrain=train
                            }

                        }
                    }
                }
            }
            for(i in 0 until getTrainNum(diaIndex,1)){
                val train=getTrain(diaIndex,1,i)
                if(train.startStation==endStation){
                    val startStop=if(nowTrain.startExchangeStop>0){nowTrain.startExchangeStop}else{nowTrain.getStopNumber(endStation)}
                    if(startStop==endStop){
                        if(train.startExchangeTimeStart>=0){
                            if(train.startExchangeTimeStart>=endTime&&train.startExchangeTimeStart<valueTime){
                                valueTime=train.startExchangeTimeStart
                                valueTrain=train
                            }
                        }
                    }
                }
            }
            if(valueTrain!=null){
                result.add(valueTrain)
            }else{
                break
            }
        }
        while(true){
            //前方列車リストを作成する
            val startStation=nowTrain.startStation
            val startStop=
                    if(nowTrain.startExchangeStop>0){nowTrain.startExchangeStop}else{nowTrain.getStopNumber(startStation)}
            val startTime=if(nowTrain.startExchangeStop>0&&nowTrain.startExchangeTimeStart>=0){
                nowTrain.startExchangeTimeStart
            }else{
                nowTrain.getDATime(startStation)
            }
            var valueTime=0
            var valueTrain:AOdiaTrain?=null
            for(i in 0 until getTrainNum(diaIndex,0)){
                val train=getTrain(diaIndex,0,i)
                if(train.endStation==startStation){
                    val endStop=if(nowTrain.endExchangeStop>0){nowTrain.endExchangeStop}else{nowTrain.getStopNumber(startStation)}
                    if(endStop==startStop){
                        if(train.endExchangeTimeEnd>=0){
                            if(train.endExchangeTimeEnd<=startTime&&train.endExchangeTimeEnd>valueTime){
                                valueTime=train.endExchangeTimeEnd
                                valueTrain=train
                            }
                        }else{
                            if(train.getADTime(train.endStation)<=startTime&&train.getADTime(train.endStation)>valueTime){
                                valueTime=train.getADTime(train.endStation)
                                valueTrain=train
                            }

                        }
                    }
                }
            }
            for(i in 0 until getTrainNum(diaIndex,1)){
                val train=getTrain(diaIndex,1,i)
                if(train.endStation==startStation){
                    val endStop=if(nowTrain.endExchangeStop>0){nowTrain.endExchangeStop}else{nowTrain.getStopNumber(startStation)}
                    if(endStop==startStop){
                        if(train.endExchangeTimeEnd>=0){
                            if(train.endExchangeTimeEnd<=startTime&&train.endExchangeTimeEnd>valueTime){
                                valueTime=train.endExchangeTimeEnd
                                valueTrain=train
                            }
                        }else{
                            if(train.getADTime(train.endStation)<=startTime&&train.getADTime(train.endStation)>valueTime){
                                valueTime=train.getADTime(train.endStation)
                                valueTrain=train
                            }

                        }
                    }
                }
            }
            if(valueTrain!=null){
                result.add(valueTrain)
            }else{
                break
            }

        }
        return result

    }


}
