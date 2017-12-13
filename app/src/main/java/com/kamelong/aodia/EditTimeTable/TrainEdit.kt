package com.kamelong.aodia.EditTimeTable

import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.timeTable.TrainViewGroup

/**
 */
class TrainEdit (val trainEditFragment:TrainTimeEditFragment){
    var train:AOdiaTrain?=null
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
            (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.invalidate()
        }
        if(train!=null)editStationTime=EditStationTime(focusPoint/3,focusPoint%3,train!!)
        println("TrainEditPoint:"+field)
    }

    var editStationTime:EditStationTime?=null

    /**
     * 駅時刻編集関数
     */
    fun stationTimeEdit(value:Int){
        editStationTime?.addNumber(value)
        (trainEditFragment.trainLinear.getChildAt(focusTrain)as TrainViewGroup).trainView.invalidate()
    }

}