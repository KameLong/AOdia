package com.kamelong.aodia.EditTimeTable

import com.kamelong.aodia.timeTable.TrainViewGroup

/**
 */
class TrainEdit (val trainEditFragment:TrainTimeEditFragment){
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
        }
        println("TrainEditTrain:"+field)
    }
    var focusPoint=-1
    set(value){
        field=value
        if(focusTrain>=0) {
            (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.focusPoint = focusPoint
            (trainEditFragment.trainLinear.getChildAt(focusTrain) as TrainViewGroup).trainView.invalidate()
        }
        println("TrainEditPoint:"+field)
    }

}