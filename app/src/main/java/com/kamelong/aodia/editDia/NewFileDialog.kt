package com.kamelong.aodia.editDia

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NewFileDialog(context:Context):AlertDialog(context){
    val view = LayoutInflater.from(context).inflate(R.layout.new_file_dialog, null)
    init {
        setView(view)
        view.findViewById<Button>(R.id.button14).setOnClickListener {
            val lineName=view.findViewById<EditText>(R.id.editText4).text.toString()
            if(lineName.isEmpty()){
                SdLog.toast("路線名が設定されていません")
            }else{
                val diaFile=DiaFile(context as Activity,lineName)
                diaFile.filePath= (context as Activity ).getExternalFilesDirs(null)[0].path+"/"+lineName
                (context as AOdiaActivity).addDiaFile(diaFile)
                dismiss()
            }
        }
    }

}