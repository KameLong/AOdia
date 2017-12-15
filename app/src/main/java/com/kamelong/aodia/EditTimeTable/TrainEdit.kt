package com.kamelong.aodia.EditTimeTable

import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.timeTable.TrainViewGroup
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 */
class TrainEdit (val trainEditFragment:TrainTimeEditFragment){
    val trainBackUpStack=ArrayDeque<TrainHistory>()
    val trainBackUpStackFoward=ArrayDeque<TrainHistory>()
    var train:AOdiaTrain?=null
    val diaFile: AOdiaDiaFile
    get()=trainEditFragment.diaFile
    var focusTrain=-1
        set(value){
            if(editStationTime?.editMode?:false){
                trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,editStationTime!!.copyTrain))
                trainBackUpStackFoward.clear()
            }
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
        }
    var focusPoint=-1
        set(value){
            if(editStationTime?.editMode?:false){
                trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,editStationTime!!.copyTrain))
                trainBackUpStackFoward.clear()
            }
            field=value
            if(focusTrain>=0) {
                (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.focusPoint = focusPoint
                invalidate()
            }
            if(train!=null)editStationTime=EditStationTime(focusPoint/3,focusPoint%3,train!!)
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
            return
        }
        focusTrain++

    }

    /**
     * 指定秒早める
     *
     */
    fun fast(value:Int){
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
        editStationTime?.moveTime(-value)
        invalidate()
    }
    /**
     * 指定秒遅らせる
     *
     */
    fun slow(value:Int){
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
        editStationTime?.moveTime(value)
        invalidate()
    }


    /**
     * 当駅止まり
     */
    fun endThisStation(){
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
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
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
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
    /**
     * 戻る
     */
    fun back(){
        val trainHistory=trainBackUpStack.pollLast()
        if(trainHistory==null)return
        trainBackUpStackFoward.addLast(TrainHistory(-1,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.changeTrain)))
        if(trainHistory.changeTrain>=0){
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).train=trainHistory.train!!
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).invalidate()
            diaFile.setTrain(trainEditFragment.diaIndex,trainEditFragment.direction,trainHistory.changeTrain,trainHistory.train!!)
        }
        focusTrain=focusTrain
        focusPoint=focusPoint
        println("back")
        invalidate()
    }
    /**
     * 進む
     */
    fun forward(){
        val trainHistory=trainBackUpStackFoward.pollLast()
        if(trainHistory==null)return
        trainBackUpStack.addLast(TrainHistory(-1,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.changeTrain)))
        if(trainHistory.changeTrain>=0){
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).train=trainHistory.train!!
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).invalidate()
            diaFile.setTrain(trainEditFragment.diaIndex,trainEditFragment.direction,trainHistory.changeTrain,trainHistory.train!!)
        }
        focusTrain=focusTrain
        focusPoint=focusPoint
        println("forword")
        invalidate()

    }

    /**
     * 駅扱いを編集する
     */
    fun setStop(value:Int){
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
        editStationTime?.setStop(value)
    }

}