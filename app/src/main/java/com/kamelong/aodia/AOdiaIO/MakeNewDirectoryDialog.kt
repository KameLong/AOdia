package com.kamelong.aodia.AOdiaIO

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import java.io.File

class MakeNewDirectoryDialog (context: Context,currentDirectory:String,openDirectory: OpenDirectory): Dialog(context) {
    init {
        setContentView(R.layout.fileselector_make_directory)
        val editDirectory=findViewById<EditText>(R.id.directoryName)
        val submitButton=findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            try{
                val file= File(File(currentDirectory).parentFile.absolutePath+"/"+editDirectory.editableText.toString())
                if(file.exists()){
                    SDlog.toast(context.getString(R.string.errorFileExist))
                    return@setOnClickListener
                }
                file.mkdir()
                dismiss()
                openDirectory.openDirectory(file.absolutePath)

            }catch (e:Exception){
e.printStackTrace()
            }
        }

    }

}