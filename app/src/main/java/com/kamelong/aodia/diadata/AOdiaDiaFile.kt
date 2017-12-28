package com.kamelong.aodia.diadata

import android.app.Activity

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

    val stationNum:Int
    fun getStation(index:Int):AOdiaStation
    fun getStationList():ArrayList<AOdiaStation>
    fun addStation(s:AOdiaStation,index:Int)
    /**
     * 指定インデックスに駅が挿入されたことを列車に通知します
     */
    fun addStationRenew(index:Int)

    /**
     * 指定インデックスの列車を削除します
     */
    fun deleteStation(index:Int)

    /**
     * 駅リストの末尾に駅を追加します
     */
    fun setStation(s: AOdiaStation)

    /**
     * 駅が変更されたことを列車に通知します
     */
    fun setStationRenew( index:Int,editStopList:ArrayList<Int>)

    /**
     * 駅リストを空にします
     */
    fun resetStation()

    val trainTypeNum:Int
    fun getTrainType(index:Int):AOdiaTrainType
    fun addTrainType(trainType:AOdiaTrainType)

    fun getDiaNum():Int
    fun getDiaName(index:Int):String
    fun setDiaName(index:Int,value:String)
    /**
     * 指定インデックスにダイヤを追加します
     */
    fun addNewDia(index:Int,value:String)

    /**
     * 既存ダイヤをコピーして新しくダイヤを作成します。
     * dName:新ダイヤ名
     * copyIndex:コピー元のダイヤindex
     */
    fun addNewDiaFile(dName:String,copyIndex:Int)



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
     * oudiaSecondファイル形式で保存します
     */
    fun save(outFile:File)

    /**
     * startStationからendStationまでの最小所要時間を計算します
     */
    fun stationTime(startStation:Int, endStation:Int):Int{
        var result=100000
        for(d in 0 until getDiaNum()){
            if(getDiaName(d)=="基準運転時分"){
                result=100000
                for(t in 0 until getTrainNum(d,0)){
                    val train=getTrain(d,0,t)
                    if(train.existTime(startStation)&&train.existTime(endStation)){
                        val value=train.getADTime(endStation)-train.getDATime(startStation)
                        if(result>value){
                            result=value
                        }
                    }
                }
                for(t in 0 until getTrainNum(d,1)){
                    val train=getTrain(d,1,t)
                    if(train.existTime(startStation)&&train.existTime(endStation)){
                        val value=train.getADTime(startStation)-train.getDATime(endStation)
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
                    if(train.existTime(startStation)&&train.existTime(endStation)){
                        val value=train.getADTime(endStation)-train.getDATime(startStation)
                        if(result>value){
                            result=value
                        }
                    }
                }
                for(t in 0 until getTrainNum(d,1)){
                    val train=getTrain(d,1,t)
                    if(train.existTime(startStation)&&train.existTime(endStation)){
                        val value=train.getADTime(startStation)-train.getDATime(endStation)
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

    /**
     * 最小所要時間のリストを返します
     */
    fun getStationTime():ArrayList<Int>{
        val result=ArrayList<Int>()
        result.add(0)
        for(s in 1 until stationNum){
            result.add(result[s-1]+stationTime(s-1,s))
        }
        return result
    }

    /**
     * 路線分岐を考慮した最小所要時間
     */
    var predictTime:ArrayList<Int>
    /**
     * 路線分岐を考慮した最小所要時間を更新する
     */
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

    /**
     * baseTrainの次の運用を求める
     */
    fun getOperationNextTrain(diaIndex: Int,baseTrain: AOdiaTrain):AOdiaTrain?{
        if(baseTrain.endAction==2)return null
        val endStation=baseTrain.endStation
        val endStop=
                if(baseTrain.endAction==1&&baseTrain.endExchangeStop>0){baseTrain.endExchangeStop}else{baseTrain.getActualStopNumber(endStation)}
        val endTime=if(baseTrain.endAction==1&&baseTrain.endExchangeStop>0&&baseTrain.endExchangeTimeEnd>=0){
            baseTrain.endExchangeTimeEnd
        }else{
            baseTrain.getADTime(endStation)
        }
        var valueTime=200000
        var valueTrain:AOdiaTrain?=null
        for(i in 0 until getTrainNum(diaIndex,0)){
            val train=getTrain(diaIndex,0,i)
            if(train==baseTrain)continue
            val startStop=if(train.startAction==1&&train.startExchangeStop>0){train.startExchangeStop}else{train.getActualStopNumber(endStation)}
            if(startStop==endStop){
                if(train.startAction==1&&train.startExchangeTimeStart>=0){
                    if(train.startExchangeTimeStart>=endTime&&train.startExchangeTimeStart<valueTime){
                        valueTime=train.startExchangeTimeStart
                        valueTrain=train
                    }
                }else{
                    if(train.getDATime(endStation)>=0&&train.getDATime(endStation)>=endTime&&train.getDATime(endStation)<valueTime){
                        valueTime=train.getDATime(endStation)
                        valueTrain=train
                        if(baseTrain.number=="M0507A"){
                            println("M0507A")
                            println(valueTrain?.number+","+valueTime+","+endTime)
                        }

                    }

                }
            }
        }
        if(baseTrain.number=="M0507A"){
            println(endStop)
        }
        for(i in 0 until getTrainNum(diaIndex,1)){
            val train=getTrain(diaIndex,1,i)
            if(train==baseTrain)continue
            val startStop=if(train.startAction==1&&train.startExchangeStop>0){train.startExchangeStop}else{train.getActualStopNumber(endStation)}

            if(startStop==endStop){
                if(train.startAction==1&&train.startExchangeTimeStart>=0){
                    if(train.startExchangeTimeStart>=endTime&&train.startExchangeTimeStart<valueTime){
                        valueTime=train.startExchangeTimeStart
                        valueTrain=train
                    }
                }else{
                    if(baseTrain.number=="M0507A"){
                        println(valueTrain?.number+","+valueTime+","+train.getDATime(endStation))
                    }
                    if(train.getDATime(endStation)>=0&&train.getDATime(endStation)>=endTime&&train.getDATime(endStation)<valueTime){
                        valueTime=train.getDATime(endStation)
                        valueTrain=train
                        if(baseTrain.number=="M0507A"){
                            println("M0507A")
                            println(valueTrain?.number+","+valueTime)
                        }

                    }

                }

            }
        }
        if(baseTrain.number=="M0507A"){
            println("M0507A")
            println(valueTrain?.number+","+valueTime)
        }

    if(valueTrain!=null&&valueTrain.startStation==endStation&&valueTrain.startAction!=2){
            return valueTrain
        }
        return null
    }
    /**
     * baseTrainの前の運用を求める
     */
    fun getOperationBeforeTrain(diaIndex: Int,baseTrain: AOdiaTrain):AOdiaTrain?{
        if(baseTrain.startAction==2)return null
        val startStation=baseTrain.startStation
        val startStop=
                if(baseTrain.startAction==1&&baseTrain.startExchangeStop>0){baseTrain.startExchangeStop}else{baseTrain.getActualStopNumber(startStation)}
        val startTime=if(baseTrain.startAction==1&&baseTrain.startExchangeStop>0&&baseTrain.startExchangeTimeStart>=0){
            baseTrain.startExchangeTimeStart
        }else{
            baseTrain.getADTime(startStation)
        }
        var valueTime=0
        var valueTrain:AOdiaTrain?=null
        for(i in 0 until getTrainNum(diaIndex,0)){
            val train=getTrain(diaIndex,0,i)
            if(train==baseTrain)continue
            val endStop=if(train.endAction==1&&train.endExchangeStop>0){train.endExchangeStop}else{train.getActualStopNumber(startStation)}
            if(startStop==endStop){
                if(train.endAction==1&&train.endExchangeTimeEnd>=0){
                    if(train.endExchangeTimeEnd<=startTime&&train.endExchangeTimeEnd>valueTime){
                        valueTime=train.endExchangeTimeEnd
                        valueTrain=train
                    }
                }else{
                    if(train.getDATime(startStation)>=0&&train.getDATime(startStation)<=startTime&&train.getDATime(startStation)>valueTime){
                        valueTime=train.getDATime(startStation)
                        valueTrain=train

                    }

                }
            }
        }
        for(i in 0 until getTrainNum(diaIndex,1)){
            val train=getTrain(diaIndex,1,i)
            if(train==baseTrain)continue
            val endStop=if(train.endAction==1&&train.endExchangeStop>0){train.endExchangeStop}else{train.getActualStopNumber(startStation)}
            if(startStop==endStop){
                if(train.endAction==1&&train.endExchangeTimeEnd>=0){
                    if(train.endExchangeTimeEnd<=startTime&&train.endExchangeTimeEnd>valueTime){
                        valueTime=train.endExchangeTimeEnd
                        valueTrain=train
                    }
                }else{
                    if(train.getDATime(startStation)>=0&&train.getDATime(startStation)<=startTime&&train.getDATime(startStation)>valueTime){
                        valueTime=train.getDATime(startStation)
                        valueTrain=train

                    }

                }
            }
        }
        if(valueTrain!=null&&valueTrain.endStation==startStation&&valueTrain.endAction!=2){
            return valueTrain
        }
        return null
    }
    fun getOperationTrains(diaIndex:Int,baseTrain:AOdiaTrain):ArrayList<AOdiaTrain>{
        val result=ArrayList<AOdiaTrain>()
        result.add(baseTrain)
        var nowTrain:AOdiaTrain?=baseTrain
        while(true){
            nowTrain=getOperationNextTrain(diaIndex,nowTrain!!)
            if(nowTrain==null){
                break;
            }
            result.add(nowTrain)
        }
        nowTrain=baseTrain
        while(true){
            nowTrain=getOperationBeforeTrain(diaIndex,nowTrain!!)
            if(nowTrain==null){
                break;
            }
            result.add(nowTrain)
        }
        return result

    }



}
