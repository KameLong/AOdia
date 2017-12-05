package com.kamelong.aodia.editStation

import android.app.Fragment
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaStation
import com.kamelong.tool.downloadView.TableRadioGroup

/**
 * １駅の編集を行う
 */
class StationEditor( fragment:EditStationFragment, index:Int) : FrameLayout(fragment.aodiaActivity) {
    val station=fragment.stationList[index].clone()
    val layout = LayoutInflater.from(fragment.activity).inflate(R.layout.edit_station_dialog, this)
    init{
        layout.findViewById<EditText>(R.id.stationNameEditText).setText(station.name)
        layout.findViewById<Button>(R.id.SubmitButton).setOnClickListener { fragment.closeStationEdit(index,true) }
        layout.findViewById<TableRadioGroup>(R.id.showTimeRadio).check(
                when(station.getTimeViewStyle()){
                    Station.SHOW_HATU->R.id.showStop5
                    Station.SHOW_HATUTYAKU->R.id.showStop15
                    Station.SHOW_KUDARIHATUTYAKU->R.id.showStop7
                    Station.SHOW_KUDARITYAKU->R.id.showStop6
                    Station.SHOW_NOBORIHATUTYAKU->R.id.showStop13
                    Station.SHOW_NOBORITYAKU->R.id.showStop9
                    else->R.id.showStop5
                }
        )
        layout.findViewById<RadioGroup>(R.id.bigRadio).check(
                if(station.bigStation){
                    R.id.bigStation
                }else{
                    R.id.normalStation
                }
        )
        val stopLinear=layout.findViewById<LinearLayout>(R.id.stopList)
        for(i in 0 until station.stopNum){
            stopLinear.addView(EditStop(this,i))
        }
        layout.findViewById<CheckBox>(R.id.checkBox6).isChecked=(station.getStopStyle() and 0x8) !=0
        layout.findViewById<CheckBox>(R.id.checkBox4).isChecked=(station.getStopStyle() and 0x80) !=0
        layout.findViewById<CheckBox>(R.id.checkBox5).isChecked=station.getStopDiaStyle()
    }

}