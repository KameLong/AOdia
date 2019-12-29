package com.kamelong.aodia.TimeTable

import android.app.Dialog
import android.widget.CheckBox
import android.widget.LinearLayout
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.EditTrain.OnTrainChangeListener
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R

class TrainTypeFilter(private val activity:MainActivity,private val lineFile: LineFile,private val trainChangeListener: OnTrainChangeListener):Dialog(activity){
    init{
        setContentView(R.layout.timetable_filter_dialog)
        val layout=findViewById<LinearLayout>(R.id.trainTypeList)
        for(trainType in lineFile.trainType){
            val checkBox=CheckBox(activity)
            checkBox.setText(trainType.name)
            checkBox.setTextColor(trainType.textColor.androidColor)
            checkBox.isChecked=trainType.showInTimeTable
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                trainType.showInTimeTable=isChecked
            }
            layout.addView(checkBox)
        }
    }

}