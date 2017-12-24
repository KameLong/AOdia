package com.kamelong.aodia.diadata

/**
 * AOdiaで使用するTrainのInterface
 */
interface AOdiaTrain {
    val diaFile:AOdiaDiaFile

    var name:String
    var number:String
    var count:String
    var operation:String
    var remark:String

    var direction:Int

    var startAction:Int
    var startExchangeStop:Int
    var startExchangeTimeStart:Int
    var startExchangeTimeEnd:Int
    var endAction:Int
    var endExchangeStop:Int
    var endExchangeTimeStart:Int
    var endExchangeTimeEnd:Int

    fun existArriveTime(station:Int):Boolean
    fun existDepartTime(station:Int):Boolean

    fun existTime(station:Int):Boolean{
        return existDepartTime(station)||existDepartTime(station)
    }


    fun getStationTime(station:Int):Long
    fun setStationTime(station:Int,value:Long)

    fun getDepartureTime(station:Int):Int
    fun getArrivalTime(station:Int):Int
    fun getDepartureTime(station:Int, startTime: Int):Int{
        if(getDepartureTime(station)<0)return -1
        return (getDepartureTime(station)+86400-startTime)%86400+startTime
    }
    fun getArrivalTime(station:Int,startTime:Int):Int{
        if(getArrivalTime(station)<0)return -1
        return (getArrivalTime(station)+86400-startTime)%86400+startTime
    }
    fun getADTime(station:Int):Int{
        if(getArrivalTime(station)>=0){
            return getArrivalTime(station)
        }else if(getDepartureTime(station)>=0){
            return getDepartureTime(station)
        }
        return -1
    }
    fun getADTime(station:Int,startTime: Int):Int{
        if(getADTime(station)<0)return -1

        return (getADTime(station)+86400-startTime)%86400+startTime

    }
    fun getDATime(station:Int):Int{
        if(getDepartureTime(station)>=0){
            return getDepartureTime(station)
        }else if(getArrivalTime(station)>=0){
            return getArrivalTime(station)
        }
        return -1
    }
    fun getDATime(station:Int,startTime: Int):Int{
        if(getDATime(station)<0)return -1

        return (getDATime(station)+86400-startTime)%86400+startTime
    }

    fun setDepartureTime(station:Int,value:Int)
    fun setArrivalTime(station:Int,value:Int)





    fun getStopNumber(station:Int):Int
    fun setStopNumber(station:Int,value:Int)

    fun getStopType(station:Int):Int
    fun setStopType(station:Int,value:Int)



    val trainType:AOdiaTrainType
    var type:Int

    val startStation:Int
    val endStation:Int

    fun clone(allCopy:Boolean):AOdiaTrain


    fun predictTime(station:Int,adFrag:Int):Int{
        if(adFrag==0&&existDepartTime(station)){
                return getDepartureTime(station)
        }else if(existArriveTime(station)){
            return getArrivalTime(station)
        }else if(existDepartTime(station)){
            return getDepartureTime(station)
        }
        //前方の駅
        var firstTime=-1
        var firstPredict=-1
        var lastTime=-1
        var lastPredict=-1
        if(diaFile.predictTime.size==0){
            diaFile.renewPredictTime()
        }
        var thisPredict=diaFile.predictTime[station]
        for(i in station-1 downTo 0){
            if(existTime(i)){
                firstPredict=diaFile.predictTime[i]
                firstTime=getDATime(i)
            }
        }
        for(i in station+1 until diaFile.stationNum){
            if(existTime(i)){
                lastPredict=diaFile.predictTime[i]
                lastTime=getDATime(i)
            }
        }
        if(firstPredict==lastPredict){
            return -1
        }
        return (lastTime-firstTime)*(thisPredict-firstPredict)/(lastPredict-firstPredict)+firstTime

    }
    companion object {
        /**
         * 駅扱いの定数。long timeの9~12bitがstop typeに対応する。
         */
        val STOP_TYPE_STOP = 1
        val STOP_TYPE_PASS = 2
        val STOP_TYPE_NOSERVICE = 0
        val STOP_TYPE_NOVIA = 3
    }

}