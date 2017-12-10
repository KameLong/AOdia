package com.kamelong.aodia.editStation

import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog
import com.kamelong.aodia.diadata.AOdiaStationHistory
import com.kamelong.tool.downloadView.TableRadioGroup

/**
 * １駅の編集を行う
 * 駅の編集を行う間はdiaFileのstationをコピーしたもの(copyStation)に情報を書き込んでいき、
 * 決定ボタンを押された瞬間にdiaFileのstationに変更を反映する。
 *
 * 番線情報についてはstationHistoryをあらかじめ作成しておき、stationHistoryに書き込めるようにする
 *
 * EditStation作成時、diaFile.stationは古いまま、EditStation.copyStationは編集用
 * EditStation.stationHistoryは古いまま、番線変更情報のみ記載していく
 *
 * 変更確定時にEditStation.copyStationをdiaFile.stationに反映し、stationHistoryはEditStationFragmentに保存
 */
class EditStation(f:EditStationFragment, i:Int, history: AOdiaStationHistory) : FrameLayout(f.aodiaActivity) {
    val index=i
    val fragment=f
    val layout = LayoutInflater.from(fragment.activity).inflate(R.layout.edit_station_dialog, this)
    val station=fragment.stationList[index].clone()
    val stationHistory=history
    lateinit var stopLinear:LinearLayout
    init{


        layout.findViewById<EditText>(R.id.stationNameEditText).setText(station.name)
        layout.findViewById<Button>(R.id.SubmitButton).setOnClickListener {
            station.name=layout.findViewById<EditText>(R.id.stationNameEditText).text.toString()
            fragment.closeStationEdit(true,index,station) }
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
        layout.findViewById<TableRadioGroup>(R.id.showTimeRadio).setOnCheckedChangeListener {
            group, checkedId ->
            station.setTimeViewStyle(when(checkedId){
                R.id.showStop5->Station.SHOW_HATU
                R.id.showStop15->Station.SHOW_HATUTYAKU
                R.id.showStop7->Station.SHOW_KUDARIHATUTYAKU
                R.id.showStop6->Station.SHOW_KUDARITYAKU
                R.id.showStop13->Station.SHOW_NOBORIHATUTYAKU
                R.id.showStop9->Station.SHOW_NOBORITYAKU
                else->Station.SHOW_HATU
            })
            setBranchSpinnerValue()
        }
        layout.findViewById<RadioGroup>(R.id.bigRadio).check(
                if(station.bigStation){
                    R.id.bigStation
                }else{
                    R.id.normalStation
                }
        )
        layout.findViewById<RadioGroup>(R.id.bigRadio).setOnCheckedChangeListener {
            group, i ->
            station.bigStation=(i==1)
        }

        findViewById<LinearLayout>(R.id.stopLinear).setOnLongClickListener {
            val dialog=CopyPasteInsertAddDeleteDialog(context,object:CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface{
                override fun onClickAddButton() {
                    addStop(0)
                }

                override fun onClickCopyButton() {
                }

                override fun onClickDeleteButton() {
                }

                override fun onClickInsertButton() {
                    addStop(0)
                }
                override fun onClickPasteButton() {
                }

            },false,false).show()
            true
        }
        stopLinear=layout.findViewById<LinearLayout>(R.id.stopList)
        for(i in 0 until station.stopNum){
            stopLinear.addView(EditStop(this,i))
        }




        layout.findViewById<CheckBox>(R.id.checkBox6).isChecked=(station.getShowStopStyle() and 0x4) !=0

        layout.findViewById<CheckBox>(R.id.checkBox6).setOnCheckedChangeListener { compoundButton, b ->
            station.setShowStopStyle((station.getShowStopStyle() and 0x40) or if(b){0x04}else{0x00})
        }
        layout.findViewById<CheckBox>(R.id.checkBox4).isChecked=(station.getShowStopStyle() and 0x40) !=0
        layout.findViewById<CheckBox>(R.id.checkBox4).setOnCheckedChangeListener { compoundButton, b ->
            station.setShowStopStyle((station.getShowStopStyle() and 0x04) or if(b){0x40}else{0x00})
        }
        layout.findViewById<CheckBox>(R.id.checkBox5).isChecked=station.getStopDiaStyle()
        layout.findViewById<CheckBox>(R.id.checkBox5).setOnCheckedChangeListener { compoundButton, b ->
            station.setStopDiaStyle(b)
        }
        setBranchSpinnerValue()
    }
    fun setBranchSpinnerValue(){
        try {
            val branchSpinner = layout.findViewById<Spinner>(R.id.branchSpinner)
            var branchSpinnerIndex = 0
            val loopSpinner = layout.findViewById<Spinner>(R.id.loopSpinner)
            var loopSpinnerIndex = 0
            val branchSpinnerList = ArrayList<String>()

            if (station.loopStation != -1 || station.branchStation != -1) {
                enable(false)
            } else {
                enable(true)
            }

            if (station.loopStation != -1) {
                branchSpinnerList.add("環状線駅として設定されています")

            } else if (station.getTimeViewStyle() == Station.SHOW_NOBORITYAKU) {
                branchSpinnerList.add("設定なし")
                for (i in 0 until index - 1) {
                    if (fragment.diaFile.getStation(i).getTimeViewStyle() == Station.SHOW_HATUTYAKU) {
                        branchSpinnerList.add(i.toString() + "." + fragment.diaFile.getStation(i).name)
                        if (station.branchStation == i) {
                            branchSpinnerIndex = branchSpinnerList.size - 1
                        }
                    }
                }
            } else if (station.getTimeViewStyle() == Station.SHOW_KUDARITYAKU) {
                branchSpinnerList.add("設定なし")
                for (i in index + 1 until fragment.diaFile.stationNum) {
                    if (fragment.diaFile.getStation(i).getTimeViewStyle() == Station.SHOW_HATUTYAKU) {
                        branchSpinnerList.add(i.toString() + "." + fragment.diaFile.getStation(i).name)
                        if (station.branchStation == i) {
                            branchSpinnerIndex = branchSpinnerList.size - 1
                        }
                    }
                }
            } else {
                branchSpinnerList.add("下り着時刻、上り着時刻に設定してください")
            }

            val loopSpinnerList = ArrayList<String>()
            if (station.branchStation != -1) {
                loopSpinnerList.add("分岐駅として設定されています")

            } else if (station.getTimeViewStyle() == Station.SHOW_HATUTYAKU) {
                loopSpinnerList.add("設定なし")
                for (i in 0 until index - 1) {
                    if ((fragment.diaFile.getStation(i).getTimeViewStyle() == Station.SHOW_HATUTYAKU) || ((fragment.diaFile.getStation(i).getTimeViewStyle() == Station.SHOW_NOBORITYAKU))) {
                        loopSpinnerList.add(i.toString() + "." + fragment.diaFile.getStation(i).name)
                        if (station.loopStation == i) {
                            loopSpinnerIndex = loopSpinnerList.size - 1
                        }
                    }
                }
            } else {
                loopSpinnerList.add("駅時刻を発着に設定してください")
            }

            val spinnerBranchAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, branchSpinnerList)
            spinnerBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


            branchSpinner.setAdapter(spinnerBranchAdapter)
            if(branchSpinnerIndex!=0) {
                branchSpinner.setSelection(branchSpinnerIndex)
            }
            branchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    try {
                        val value = (view as TextView).text
                        if (!value.contains(".")) {
                            station.branchStation = -1
                            enable(true)

                        } else {
                            try {
                                station.branchStation = value.split(".")[0].toInt()
                                enable(false)

                            } catch (e: Exception) {
                                station.branchStation = -1
                                enable(true)
                            }

                        }
                    }catch (e:Exception){
                        SdLog.log(e)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                }
            }
            val spinnerLoopAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, loopSpinnerList)
            spinnerLoopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            loopSpinner.setAdapter(spinnerLoopAdapter)
            if(loopSpinnerIndex!=0) {
                loopSpinner.setSelection(loopSpinnerIndex)
            }

            loopSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                    try {
                        val value = (view as TextView).text
                        if (!value.contains(".")) {
                            station.loopStation = -1
                            enable(true)

                        } else {
                            try {
                                station.loopStation = value.split(".")[0].toInt()
                                enable(false)
                            } catch (e: Exception) {
                                station.loopStation = -1
                                enable(true)
                            }
                        }
                    }catch (e:Exception){
                        SdLog.log(e)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
                }
            }
        }catch (e:Exception){
            SdLog.log(e)
        }

    }
    fun setStopSpinnerValue(stopIndex:Int,spinnerIndex:Int){
        when(spinnerIndex){
            0->for(i in 0 until stopLinear.childCount){
                if(i!=stopIndex){
                    (stopLinear.getChildAt(i)as EditStop).removeSpinnerValue(0)
                }
            }
            1->for(i in 0 until stopLinear.childCount){
                if(i!=stopIndex){
                    (stopLinear.getChildAt(i)as EditStop).removeSpinnerValue(1)
                }
            }
        }
    }
    fun addStop(index:Int){
        station.addStop(index)
        stationHistory.addStop.add(index)
        stopLinear.removeAllViews()
        for(i in 0 until station.stopNum){
            stopLinear.addView(EditStop(this,i))
        }

    }
    fun deleteStop(index:Int){
        if(index==station.downMain||index==station.upMain){
            SdLog.toast("主要番線は削除できません")
            return
        }
        station.deleteStop(index)
        stationHistory.addStop.add(-index)
        stopLinear.removeAllViews()
        for(i in 0 until station.stopNum){
            stopLinear.addView(EditStop(this,i))
        }


    }
    fun enable(boolean:Boolean){
        findViewById<RadioButton>(R.id.showStop5).isEnabled=boolean
        findViewById<RadioButton>(R.id.showStop15).isEnabled=boolean
        findViewById<RadioButton>(R.id.showStop7).isEnabled=boolean
        findViewById<RadioButton>(R.id.showStop6).isEnabled=boolean
        findViewById<RadioButton>(R.id.showStop13).isEnabled=boolean
        findViewById<RadioButton>(R.id.showStop9).isEnabled=boolean
    }

}