package com.kamelong.aodia.newTimeTable

import android.content.Context
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.View
import com.kamelong.JPTI.Route
import com.kamelong.JPTI.Trip
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStation
import com.kamelong.aodia.timeTable.KLView
import java.util.jar.Attributes

/**
 * １つのTripを表示するためのView
 */
class TripView @JvmOverloads constructor(context:Context,attrs:AttributeSet?=null,defStyleAttr:Int=0,val diaFile:AOdiaDiaFile,val trip:Trip,val direct:Int): KLView(context){
    private lateinit var route: Route
    var secondFrag=false
    var showPassFrag=false

    init{
        val spf = PreferenceManager.getDefaultSharedPreferences(context)
        secondFrag = spf.getBoolean("secondSystem", secondFrag)
        showPassFrag = spf.getBoolean("showPass", showPassFrag)
    }
    private fun init(){

    }
    val ySize:Int
    get(){
        var result=0
        result+=route.getStationList(direct).size*textSize
        for(station in route.getStationList(direct)){
            if(station.getViewStyle(direct)==1){
                result+= textSize*7/6
            }
            if(station.getViewStyle(direct)>3){
                result+= textSize*8/6;
            }
        }
        result+=textSize/6
        return result
    }
    val xSize:Int
    get(){
        var lineTextSize = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("lineTimetableWidth", "4"))!! + 1
        if (secondFrag) {
            lineTextSize += 3
        }
        return (textSize* lineTextSize * 0.5f).toInt()
    }
}