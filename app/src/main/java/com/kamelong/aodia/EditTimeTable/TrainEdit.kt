package com.kamelong.aodia.EditTimeTable

import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.timeTable.TrainViewGroup
import kotlin.concurrent.fixedRateTimer

/**
 */
class TrainEdit (val trainEditFragment:TrainTimeEditFragment){
    var train:AOdiaTrain?=null
    val diaFile: AOdiaDiaFile
    get()=trainEditFragment.diaFile
    var focusTrain=-1
        set(value){
            if(field>=0) {
                (trainEditFragment.trainLinear.getChildAt(field) as TrainViewGroup).trainView.focusPoint = -1
                (trainEditFragment.trainLinear.getChildAt(field)as TrainViewGroup).trainView.invalidate()
            }
            field=value
            if(field>=0) {
                (trainEditFragment.trainLinear.getChildAt(field)as TrainViewGroup).trainView.focusPoint=focusPoint
                (trainEditFragment.trainLinear.getChildAt(field)as TrainViewGroup).trainView.invalidate()
                train=trainEditFragment.getTrain(focusTrain)
            }
            if(train!=null)editStationTime=EditStationTime(focusPoint/3,focusPoint%3,train!!)
            println("TrainEditTrain:"+field)
        }
    var focusPoint=-1
        set(value){
            field=value
            if(focusTrain>=0) {
                (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.focusPoint = focusPoint
                invalidate()
            }
            if(train!=null)editStationTime=EditStationTime(focusPoint/3,focusPoint%3,train!!)
            println("TrainEditPoint:"+field)
        }

    var editStationTime:EditStationTime?=null

    /**
     * 列車描画更新
     */
    fun invalidate(){
        (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.invalidate()
    }

    /**
     * 駅時刻編集関数
     */
    fun stationTimeEdit(value:Int){
        editStationTime?.addNumber(value)
        invalidate()
    }    /**
     * カーソルを下に動かす
     */
    fun moveDown(){
        focusPoint++
        if(focusPoint>diaFile.stationNum*3+2){
            focusPoint=diaFile.stationNum*3+2
            return
        }
            while(true){
                if(focusPoint<0 || focusPoint>=diaFile.stationNum*3)return
                when(focusPoint%3){
                    0->if((diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b10)!=0)return
                    1->if(diaFile.getStation(focusPoint/3).getStopStyle(trainEditFragment.direction))return
                    2->if((diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b1)!=0)return
                }
                focusPoint++
            }

    }
    /**
     * カーソルを上に動かす
     */
    fun moveUp(){
        focusPoint--
        if(focusPoint<-6){
            focusPoint=-6
            return
        }
            while(true){
                if(focusPoint<0 || focusPoint>=diaFile.stationNum*3)return
                when(focusPoint%3){
                    0->if((diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b10)!=0)return
                    1->if(diaFile.getStation(focusPoint/3).getStopStyle(trainEditFragment.direction))return
                    2->if((diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b1)!=0)return
                }
                focusPoint--

        }

    }
    /**
     * カーソルを左に動かす
     */
    fun moveLeft(){
        if(focusTrain<=0){
            return
        }
        focusTrain--


    }
    /**
     * カーソルを右に動かす
     */
    fun moveRight(){
        if(focusTrain>=diaFile.getTrainNum(trainEditFragment.diaIndex,trainEditFragment.direction)-1){
            focusTrain=diaFile.getTrainNum(trainEditFragment.diaIndex,trainEditFragment.direction)-1
            return
        }
        focusTrain++

    }

    /**
     * 指定秒早める
     *
     */
    fun fast(value:Int){
        editStationTime?.moveTime(-value)
        invalidate()
    }
    /**
     * 指定秒遅らせる
     *
     */
    fun slow(value:Int){
        editStationTime?.moveTime(value)
        invalidate()
    }


    /**
     * 当駅止まり
     */
    fun endThisStation(){
        if(trainEditFragment.direction==0){
            train?.setDepartureTime(focusPoint/3,-1)
            for(i in focusPoint/3+1 until diaFile.stationNum){
                train?.setStationTime(i,0)
            }
        }else{
            train?.setDepartureTime(focusPoint/3,-1)
            for(i in 0 until focusPoint/3){
                train?.setStationTime(i,0)
            }

        }

    }
    /**
     * 当駅始発
     */
    fun startThisStation(){
        if(trainEditFragment.direction==0){
            train?.setArrivalTime(focusPoint/3,-1)
            for(i in 0 until focusPoint/3){
                train?.setStationTime(i,0)
            }
        }else{
            train?.setArrivalTime(focusPoint/3,-1)
            for(i in focusPoint/3+1 until diaFile.stationNum){
                train?.setStationTime(i,0)
            }

        }

    }

}