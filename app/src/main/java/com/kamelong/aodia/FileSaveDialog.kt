package com.kamelong.aodia

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.aodia.diadata.AOdiaDiaFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileSaveDialog(context: Context,diaFile: AOdiaDiaFile):AlertDialog(context) {
    val view = LayoutInflater.from(context).inflate(R.layout.save_dialog, null)
    init {
        setView(view)
        val date = Date(System.currentTimeMillis())
        val dataStr = SimpleDateFormat("MMddHHmmss").format(date)
        val fileName=diaFile.filePath.split("/")[diaFile.filePath.split("/").size-1].split(".")[0]
        val filePath=diaFile.filePath.substring(0, diaFile.filePath.lastIndexOf("/"))+"/"
        val editText=view.findViewById<EditText>(R.id.editText2)
        editText.setText(fileName+"-"+dataStr+".oud2")
        view.findViewById<Button>(R.id.button13).setOnClickListener {
            view.findViewById<TextView>(R.id.textView19).setText("保存しています")
            diaFile.save(File(filePath+editText.text.toString()))
            this.dismiss()
        }

    }

}