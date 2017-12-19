package com.kamelong.aodia.EditTimeTable

import android.graphics.Color
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.timeTable.TrainViewGroup
import java.util.*
import kotlin.concurrent.fixedRateTimer

/**
 */
class TrainEdit (val trainEditFragment: LineTrainTimeFragment){
    val trainBackUpStack=ArrayDeque<TrainHistory>()
    val trainBackUpStackFoward=ArrayDeque<TrainHistory>()

    var selectedTrain=mutableListOf<Int>()
    fun addSelectTrain(value:Int){
        if(selectedTrain.contains(value)){
            selectedTrain.remove(value)
            trainEditFragment.trainLinear.getChildAt(value).setBackgroundColor(Color.argb(255,255,255,255))
        }else{
            selectedTrain.add(value)
            trainEditFragment.trainLinear.getChildAt(value).setBackgroundColor(Color.argb(255,230,255,230))
        }
    }
    fun clearSelectedTrain(){
        for(i in selectedTrain){
            trainEditFragment.trainLinear.getChildAt(i).setBackgroundColor(Color.argb(255,255,255,255))
        }
        selectedTrain.clear()
    }
    var copyTrain= mutableListOf<AOdiaTrain>()
    var pasteMoveTime=0//貼り付け移動量
    var pasteNum=0

    var train:AOdiaTrain?=null

    var editAllStop=false
    var editAllTime=false
    var movingUpFrag=false//繰り上げ編集
    var movingDownFrag=false//繰り下げ編集
    var fastInput=false

    var cursolDirectIsRight=false


    val diaFile: AOdiaDiaFile
    get()=trainEditFragment.diaFile
    var focusTrain=-1
        set(value){
            if(editStationTime?.editMode?:false){
                trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,editStationTime!!.copyTrain))
                trainBackUpStackFoward.clear()
            }
            if(field>=0&&field<trainEditFragment.trainLinear.childCount) {
                (trainEditFragment.trainLinear.getChildAt(field) as TrainViewGroup).trainView.focusPoint = -1
                (trainEditFragment.trainLinear.getChildAt(field)as TrainViewGroup).trainView.invalidate()
            }
            field=value
            if(field>=0&&field<trainEditFragment.trainLinear.childCount) {
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
        if(focusTrain>=0&&focusTrain<trainEditFragment.trainLinear.childCount){
            (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).invalidate()
        }
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
                if(focusPoint<0 || focusPoint>=diaFile.stationNum*3){
                    focusPoint=-1
                    return
                }
                when(focusPoint%3){
                    0->if(editAllTime||(diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b10)!=0)return
                    1->if(editAllStop||diaFile.getStation(focusPoint/3).getStopStyle(trainEditFragment.direction))return
                    2->if(editAllTime||(diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b1)!=0)return
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
                    0->if(editAllTime||(diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b10)!=0)return
                    1->if(editAllStop||diaFile.getStation(focusPoint/3).getStopStyle(trainEditFragment.direction))return
                    2->if(editAllTime||(diaFile.getStation(focusPoint/3).getViewStyle(trainEditFragment.direction) and 0b1)!=0)return
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
        if(movingDownFrag){
            for(i in focusPoint/3+1 until diaFile.stationNum){
                if(train?.existArriveTime(i)?:false){
                    train?.setArrivalTime(i,(train!!.getArrivalTime(i)+86400-value)%86400)
                }
                if(train?.existDepartTime(i)?:false){
                    train?.setDepartureTime(i,(train!!.getDepartureTime(i)+86400-value)%86400)


                }
            }
            if(focusPoint%3==0){
                if(train?.existDepartTime(focusPoint/3)?:false){
                    train?.setDepartureTime(focusPoint/3,(train!!.getDepartureTime(focusPoint/3)+86400-value)%86400)
                }
            }
        }
        if(movingUpFrag){
            for(i in focusPoint/3-1 downTo 0){
                if(train?.existArriveTime(i)?:false){
                    train?.setArrivalTime(i,(train!!.getArrivalTime(i)+86400-value)%86400)
                }
                if(train?.existDepartTime(i)?:false){
                    train?.setDepartureTime(i,(train!!.getDepartureTime(i)+86400-value)%86400)
                }
            }
            if(focusPoint%3==2){
                if(train?.existArriveTime(focusPoint/3)?:false){
                    train?.setArrivalTime(focusPoint/3,(train!!.getArrivalTime(focusPoint/3)+86400-value)%86400)
                }
            }
        }

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
        if(movingDownFrag){
            for(i in focusPoint/3+1 until diaFile.stationNum){
                if(train?.existArriveTime(i)?:false){
                    train?.setArrivalTime(i,(train!!.getArrivalTime(i)+86400+value)%86400)
                }
                if(train?.existDepartTime(i)?:false){
                    train?.setDepartureTime(i,(train!!.getDepartureTime(i)+86400+value)%86400)


                }
            }
            if(focusPoint%3==0){
                if(train?.existDepartTime(focusPoint/3)?:false){
                    train?.setDepartureTime(focusPoint/3,(train!!.getDepartureTime(focusPoint/3)+86400+value)%86400)
                }
            }
        }
        if(movingUpFrag){
            for(i in focusPoint/3-1 downTo 0){
                if(train?.existArriveTime(i)?:false){
                    train?.setArrivalTime(i,(train!!.getArrivalTime(i)+86400+value)%86400)
                }
                if(train?.existDepartTime(i)?:false){
                    train?.setDepartureTime(i,(train!!.getDepartureTime(i)+86400+value)%86400)
                }
            }
            if(focusPoint%3==2){
                if(train?.existArriveTime(focusPoint/3)?:false){
                    train?.setArrivalTime(focusPoint/3,(train!!.getArrivalTime(focusPoint/3)+86400+value)%86400)
                }
            }
        }
        invalidate()
    }


    /**
     * 当駅止まり
     */
    fun endThisStation(){
        trainBackUpStack.addLast(TrainHistory(-1,-1,focusTrain,train?.clone(true)))
        trainBackUpStackFoward.clear()
        if(trainEditFragment.direction==0){
            if(train?.existArriveTime(focusPoint/3)?:false) {
                train?.setDepartureTime(focusPoint / 3, -1)
            }
            for(i in focusPoint/3+1 until diaFile.stationNum){
                train?.setStationTime(i,0)
            }

        }else{
            if(train?.existArriveTime(focusPoint/3)?:false) {
                train?.setDepartureTime(focusPoint / 3, -1)
            }
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
        if(trainHistory.changeTrain>=0){
            trainBackUpStackFoward.addLast(TrainHistory(-1,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.changeTrain)))
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).train=trainHistory.train!!
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).invalidate()
            diaFile.setTrain(trainEditFragment.diaIndex,trainEditFragment.direction,trainHistory.changeTrain,trainHistory.train!!)
        }
        if(trainHistory.addTrain>=0){
            trainBackUpStackFoward.addLast(TrainHistory(trainHistory.addTrain,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.addTrain)))
            val index=diaFile.deleteTrain(trainEditFragment.diaIndex, trainEditFragment.direction,trainEditFragment.getTrain(trainHistory.addTrain))
            if(index>=0)trainEditFragment.trainLinear.removeViewAt(index)
            focusTrain=-1
        }
        if(trainHistory.deleteTrain>=0){
            trainBackUpStackFoward.addLast(TrainHistory(-1,trainHistory.deleteTrain,trainHistory.changeTrain,trainHistory.train))
            diaFile.addTrain(trainEditFragment.diaIndex, trainEditFragment.direction,trainHistory.deleteTrain,trainHistory.train!!)
            trainEditFragment.trainLinear.addView(TrainViewGroup(trainEditFragment.activity, trainHistory.train!!),trainHistory.deleteTrain)
            focusTrain++
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
        if(trainHistory.changeTrain>=0){
            trainBackUpStack.addLast(TrainHistory(-1,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.changeTrain)))
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).train=trainHistory.train!!
            (trainEditFragment.trainLinear.getChildAt(trainHistory.changeTrain) as TrainViewGroup).invalidate()
            diaFile.setTrain(trainEditFragment.diaIndex,trainEditFragment.direction,trainHistory.changeTrain,trainHistory.train!!)
        }
        if(trainHistory.addTrain>=0){
            focusTrain=-1
            trainBackUpStack.addLast(TrainHistory(trainHistory.addTrain,-1,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.addTrain)))
            val index=diaFile.deleteTrain(trainEditFragment.diaIndex, trainEditFragment.direction,trainEditFragment.getTrain(trainHistory.addTrain))
            if(index>=0)trainEditFragment.trainLinear.removeViewAt(index)
        }
        if(trainHistory.deleteTrain>=0){
            trainBackUpStack.addLast(TrainHistory(-1,trainHistory.addTrain,trainHistory.changeTrain,trainEditFragment.getTrain(trainHistory.addTrain)))
            diaFile.addTrain(trainEditFragment.diaIndex, trainEditFragment.direction,focusTrain,trainHistory.train!!)
            trainEditFragment.trainLinear.addView(TrainViewGroup(trainEditFragment.activity, trainHistory.train!!),focusTrain)
            focusTrain++
        }
        focusTrain=focusTrain
        focusPoint=focusPoint
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
    /**
     * 列車を追加する
     */
    fun addTrain(train:AOdiaTrain):Int{
        focusTrain++

        val history=TrainHistory(-1,focusTrain,-1,null)
        trainBackUpStack.addLast(history)
        diaFile.addTrain(trainEditFragment.diaIndex, trainEditFragment.direction,focusTrain,train)
        trainEditFragment.trainLinear.addView(TrainViewGroup(trainEditFragment.activity, train),focusTrain)
        return focusTrain
    }
    /**
     * 列車を挿入する
     */
    fun insertTrain(train:AOdiaTrain):Int{
        val history=TrainHistory(-1,focusTrain,-1,null)
        trainBackUpStack.addLast(history)
        diaFile.addTrain(trainEditFragment.diaIndex, trainEditFragment.direction,focusTrain,train)
        trainEditFragment.trainLinear.addView(TrainViewGroup(trainEditFragment.activity, train),focusTrain)
        focusTrain++
        return focusTrain-1
    }
    /**
     * 列車を削除する
     */
    fun deleteTrain(train:AOdiaTrain):Int{
        focusTrain=-1
        val index=diaFile.deleteTrain(trainEditFragment.diaIndex, trainEditFragment.direction,train)
        val history=TrainHistory(index,-1,-1,train)
        trainBackUpStack.addLast(history)

        return index
    }
    fun splitTrain(){
        val train=trainEditFragment.getTrain(focusTrain).clone(true)
        addTrain(train)
        focusTrain--
        endThisStation()
        moveRight()
        startThisStation()

        copyTrain
    }
    fun connectTrain(){
        val train=trainEditFragment.getTrain(focusTrain)
        if(train.endStation!=focusPoint/3){
            return
        }
        for(i in focusTrain until trainEditFragment.trainLinear.childCount){
            val train2=trainEditFragment.getTrain(i);
            if(train2.startStation==focusPoint/3){
                if(train2.type==train.type){
                    train.setDepartureTime(focusPoint/3,train2.getDepartureTime(focusPoint/3))
                    for(j in focusPoint/3 until diaFile.stationNum){
                        train.setArrivalTime(j,train2.getArrivalTime(j))
                        train.setDepartureTime(j,train2.getDepartureTime(j))
                        train.setStopType(j,train2.getStopType(j))
                        train.setStopNumber(j,train2.getStopNumber(j))
                    }
                    trainEditFragment.trainLinear.removeViewAt(deleteTrain(train2))
                    return
                }
            }
        }

    }


}