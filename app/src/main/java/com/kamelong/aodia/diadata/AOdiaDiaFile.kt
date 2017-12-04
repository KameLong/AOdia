package com.kamelong.aodia.diadata

import android.app.Activity
import android.content.Context

import com.kamelong.JPTI.JPTI
import com.kamelong.JPTI.Operation
import com.kamelong.JPTI.Service
import com.kamelong.aodia.AOdiaActivity
import java.util.ArrayList

/**
 * AOdiaで使用するDiaFile
 * OuDiaの処理をベースにしている。
 */

interface AOdiaDiaFile {
    var activity:Activity
    var filePath:String
    var menuOpen:Boolean
    fun getDiaNum():Int
    fun getDiaName(index:Int):String

    fun getStation(index:Int):AOdiaStation
    val stationNum:Int
    fun getStationList():ArrayList<AOdiaStation>





}
