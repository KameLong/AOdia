package com.kamelong.aodia.editDia

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog
import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 */
class EditDiaDialog(activity: Activity, diaFile: AOdiaDiaFile, diaIndex:Int):AlertDialog(activity) {
    val view = LayoutInflater.from(context).inflate(R.layout.edit_dia, null)
    init {
        setView(view)
        view.findViewById<EditText>(R.id.editText3).setText(diaFile.getDiaName(diaIndex))
        view.findViewById<Button>(R.id.button15).setOnClickListener {
            val diaName=view.findViewById<EditText>(R.id.editText3).text.toString()
            if(diaName.isEmpty()){
                SdLog.toast("ダイヤ名が設定されていません")
            }else{
                diaFile.setDiaName(diaIndex,diaName)
                dismiss()
            }
        }
        view.findViewById<Button>(R.id.button16).setOnClickListener {
            diaFile.addNewDiaFile(diaFile.getDiaName(diaIndex)+"-コピー",diaIndex)
        }
        view.findViewById<Button>(R.id.button16).setOnClickListener {
            diaFile.addNewDiaFile("新規作成",-1)
        }

    }

}