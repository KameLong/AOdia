package com.kamelong.aodia.editStation

import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.diadata.AOdiaStation

/**
 * Created by kame on 2017/12/21.
 */
class AOdiaStationEdit(val station:AOdiaStation){
    var editStopList=ArrayList<Int>()

    fun clone():AOdiaStationEdit{
        val result=AOdiaStationEdit(station.clone())
        result.editStopList=ArrayList(editStopList)
        return result
    }
}