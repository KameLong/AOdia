package com.kamelong.aodia.editStation

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragment
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStationHistory
import java.util.*

/**
 * Created by kame on 2017/12/03.
 */
class EditStationFragment : Fragment(), AOdiaFragmentInterface {
    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override lateinit var activity: AOdiaActivity

    var fileIndex=0
    lateinit var fragmentContainer:View
    lateinit var stationLinear:LinearLayout

    val stationSelected= ArrayList<Boolean>()

    val backStack=ArrayDeque<AOdiaStationHistory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        try{
            activity=getActivity() as AOdiaActivity
            fragment=this
            val bundle = arguments
            fileIndex = bundle.getInt("fileNum")
            diaFile=activity.diaFiles[fileIndex]
        }catch (e:Exception){
            e.printStackTrace()
            //activity.killFragment(this)
        }
        fragmentContainer = inflater.inflate(R.layout.edit_station_fragment, container, false)
        return fragmentContainer
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for(station in diaFile.getStationList()){
            stationLinear.addView(EditStaiton(getActivity(),this,station))
            stationSelected.add(false)
        }
    }

    fun stationSelect(view:View,value:Boolean){
        val index=stationLinear.indexOfChild(view)
        stationSelected[index]=value
    }
    fun addStation(index:Int){
        diaFile.addStation(index)
        stationSelected.add(index,false)
        val history=AOdiaStationHistory()
        history.addIndex=index
        backStack.addLast(history)
    }


}