package com.kamelong.aodia.EditTimeTable

import com.kamelong.aodia.diadata.AOdiaTrain
import java.util.*

/**
 * 駅時刻編集クラス
 * pos:着時刻=0,番線=1,発時刻=2
 */

class EditStationTime(val station:Int,val pos:Int,val train:AOdiaTrain) {
    val backStack=ArrayDeque<Long>()
    val forwardStack=ArrayDeque<Long>()
    var time:String=""
    var stopType=0

    val inputDatas=ArrayList<Int>()
    val inputDataSecond=ArrayList<Int>()
    val inputSecondFrag=false

    /**
     * value:停車種別
     */
    fun setStop(value:Int){
        when(value){
            0 or 3->{
                if(train.getStopType(station)!=value){
                    backStack.add(train.getStationTime(station))
                    train.setStopType(station,value)
                    train.setArrivalTime(station,-1)
                    train.setDepartureTime(station,-1)
                }
            }
            1 or 2->{
                if(train.getStopType(station)!=value){
                    backStack.add(train.getStationTime(station))
                    train.setStopType(station,value)
                }
            }
        }
    }
    /**
     * pos:着時刻=0,番線=1,発時刻=2
     * value:数字
     */
    fun addNumber(value:Int){
        inputDatas.add(value)
        reNewValue()
    }
    fun reNewValue(){
        backStack.add(train.getStationTime(station))
        when (pos){
            0 or 2->{
                var result=0
                for(i in 0 until inputDatas.size){
                    if((inputDatas.size-i)%2==0){result*=6}else{result*=10}
                    result+=inputDatas[i]
                }
                result*=6
                if(inputDataSecond.size>0)result+=inputDataSecond[0]
                result*=10
                if(inputDataSecond.size>1)result+=inputDataSecond[1]
                if(inputDatas.size<3){
                    if(pos==2){
                        train.setDepartureTime(station, result % 3600)
                    }else {
                        train.setArrivalTime(station, result % 3600)
                    }
                }else if(inputDatas.size<5){
                    if(pos==2){
                        train.setDepartureTime(station,result%86400)
                    }else {
                        train.setArrivalTime(station, result % 86400)
                    }
                }
            }
            1->{

            }
        }
    }
}