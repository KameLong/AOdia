package com.kamelong.aodia.timeTable

import android.app.Fragment
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 * 時刻表を編集するためのFrgment
 */
class TrainTimeEdit: Fragment(),AOdiaFragmentInterface{
    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override var aodiaActivity: AOdiaActivity
        get() = activity as AOdiaActivity
        set(value) {}


}