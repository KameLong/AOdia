package com.kamelong.aodia.editStation

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.kamelong.OuDia.OuDiaStation
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.diadata.AOdiaStation
import com.kamelong.aodia.diadata.AOdiaStationHistory
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by kame on 2017/12/03.
 */
class EditStationFragment : Fragment(), AOdiaFragmentInterface,CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface {
    override fun onClickCopyButton() {
        stationCopyList=ArrayList<Int>()
        for(i in 0 until stationSelected.size){
            if(stationSelected[i]){
                stationCopyList.add(i)
            }
        }
    }

    override fun onClickPasteButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickInsertButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickAddButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickDeleteButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override lateinit var aodiaActivity: AOdiaActivity

    var fileIndex=0
    lateinit var fragmentContainer:View
    lateinit var stationLinear:LinearLayout

    val stationSelected= ArrayList<Boolean>()
    var stationCopyList= ArrayList<Int>()

    lateinit var stationList:ArrayList<AOdiaStation>


    val backStack=ArrayDeque<AOdiaStationHistory>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        try{
            aodiaActivity =getActivity() as AOdiaActivity
            fragment=this
            val bundle = arguments
            fileIndex = bundle.getInt("fileNum")
            diaFile= aodiaActivity.diaFiles[fileIndex]
            stationList= ArrayList(diaFile.getStationList())
        }catch (e:Exception){
            e.printStackTrace()
            //activity.killFragment(this)
        }
        fragmentContainer = inflater.inflate(R.layout.edit_station_fragment, container, false)
        return fragmentContainer
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stationLinear=fragmentContainer.findViewById(R.id.stationLinear)
        for(i in 0 until diaFile.stationNum){
            stationLinear.addView(EditStaiton(getActivity(),this,diaFile.getStation(i),i))
            stationSelected.add(false)
        }
        fragmentContainer.setOnLongClickListener {
            var copyValue=false
            for(boolean in stationSelected){
                if(boolean){
                    copyValue=true
                }
            }
            val dialog=CopyPasteInsertAddDeleteDialog(aodiaActivity,this,copyValue && (stationCopyList.size!=0))
            true
        }
    }

    fun addStation(index:Int){
        val history=AOdiaStationHistory()
        history.addIndex=index
        backStack.addFirst(history)
        stationList.add(index,OuDiaStation() as AOdiaStation)
        stationSelected.add(index,false)
    }
    fun deleteStaiton(index:Int):Boolean{
        for(station in stationList){
            if(station.branchStation==index||station.loopStation==index){
                return false
            }
        }
        val history=AOdiaStationHistory()
        history.station=stationList[index]
        history.deleteIndex=index
        stationList.remove(stationList[index])
        stationSelected.remove(stationSelected[index])
        return true
    }
    fun openStationEdit(index:Int){
        val frameLayout=fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
        frameLayout.addView(StationEditor(this,index))
        frameLayout.visibility=View.VISIBLE
        val history=AOdiaStationHistory()
        history.changeIndex=index
        history.station=stationList[index]
        backStack.addFirst(history)
    }
    fun closeStationEdit(index:Int,renewFrag:Boolean){
        val frameLayout=fragmentContainer.findViewById<FrameLayout>(R.id.frameLayout)
        if(!renewFrag){
            val history=backStack.first as AOdiaStationHistory
            stationList[history.changeIndex]=history.station!!

        }
        frameLayout.removeAllViews()
        frameLayout.visibility=View.GONE

    }


}