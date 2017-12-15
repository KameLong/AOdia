package com.kamelong.aodia.editStation

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaStation

/**
 * EditStationFragmentにおいて、１つ１つの駅を簡易表示するためのViewGroup
 * stationが更新されたら再度生成すること
 */
class EditStaitonView(context: Context, fragment:EditStationFragment, station:AOdiaStation, index:Int) : FrameLayout(context) {
    val layout = LayoutInflater.from(context).inflate(R.layout.edit_station_view, this)
    init{
        layout.findViewById<CheckBox>(R.id.checkBox).setOnCheckedChangeListener { button, boolean -> fragment.stationSelected[index]=boolean }
        layout.findViewById<TextView>(R.id.textNumber).text=index.toString()
        layout.findViewById<TextView>(R.id.stationName).text=station.name
        layout.findViewById<TextView>(R.id.textStopType).text=when(station.getTimeViewStyle()){
            Station.SHOW_HATU->"発時刻"
            Station.SHOW_HATUTYAKU->"発着"
            Station.SHOW_KUDARIHATUTYAKU->"下り発着"
            Station.SHOW_KUDARITYAKU->"下り着時刻"
            Station.SHOW_NOBORIHATUTYAKU->"上り発着"
            Station.SHOW_NOBORITYAKU->"上り着時刻"
            else->""
        }
        layout.findViewById<TextView>(R.id.textBranch).text=if(station.branchStation<0){""}else{station.branchStation.toString()}
        layout.findViewById<TextView>(R.id.textLoop).text=if(station.loopStation<0){""}else{station.loopStation.toString()}

        //クリックするとEditStationを開く
        layout.setOnClickListener { fragment.openStationEdit(index) }
        layout.setOnLongClickListener { fragment.fragmentContainer.performLongClick() }
    }

    /**
     * Android固有のselectedをオーバーライド
     * 選択されているときはcheckBoxをONにする
     */
    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        layout.findViewById<CheckBox>(R.id.checkBox).isChecked=selected
    }






}