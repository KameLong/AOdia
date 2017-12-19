package com.kamelong.aodia.EditTimeTable

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaTrain

/**
 * Created by kame on 2017/12/18.
 */
class EditTrainDialog(context: Context, val listener: EditTrainDialogInterface,selectedFrag:Boolean,copyExistFrag:Boolean) : AlertDialog(context) {
    val view = LayoutInflater.from(context).inflate(R.layout.train_acrtion_dialog, null)
    init{
        setView(view)
            view.findViewById<Button>(R.id.copy).isEnabled=selectedFrag
            view.findViewById<Button>(R.id.delete).isEnabled=selectedFrag
        view.findViewById<Button>(R.id.paste).isEnabled=copyExistFrag
        view.findViewById<Button>(R.id.copy).setOnClickListener {
            listener.copy(string2Time(view.findViewById<EditText>(R.id.editText).text.toString()))
            dismiss()
        }
        view.findViewById<Button>(R.id.paste).setOnClickListener {
            listener.paste()
            dismiss()
        }
        view.findViewById<Button>(R.id.delete).setOnClickListener {
            listener.delete()
            dismiss()
        }
        view.findViewById<Button>(R.id.add).setOnClickListener {
            listener.add()
            dismiss()
        }
        view.findViewById<Button>(R.id.insert).setOnClickListener {
            listener.insert()
            dismiss()
        }
    }
    fun string2Time(value:String):Int {
        try {
            var result = 0
            val hhmm = value.split('-')[0]
            when (hhmm.length) {
                4 -> {
                    val hh = hhmm.substring(0, 2)
                    val mm = hhmm.substring(2, 4)
                    if (Integer.parseInt(hh) > 24) {
                        return -1
                    }
                    if (Integer.parseInt(mm) >= 60) {
                        return -1
                    }
                    result += 3600 * Integer.parseInt(hh) + 60 * Integer.parseInt(mm)
                }
                3 -> {
                    val hh = hhmm.substring(0, 1)
                    val mm = hhmm.substring(1, 3)
                    if (Integer.parseInt(hh) > 24) {
                        return -1
                    }
                    if (Integer.parseInt(mm) >= 60) {
                        return -1
                    }
                    result += 3600 * Integer.parseInt(hh) + 60 * Integer.parseInt(mm)
                }
                else -> {
                    if (Integer.parseInt(hhmm) > 1440) return -1
                    result += Integer.parseInt(hhmm)*60
                }
            }
            if (value.contains('-')) {
                val ss = value.split('-')[1]
                if (Integer.parseInt(ss) >= 60) {
                    return -1
                }
                result += Integer.parseInt(ss)
            }
            return result
        }catch (e:Exception){
            e.printStackTrace()
            return -1
        }
    }
    interface EditTrainDialogInterface{
        fun copy(pasteMove:Int)
        fun paste()
        fun add()
        fun insert()
        fun delete()
    }
}
