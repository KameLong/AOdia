package com.kamelong.aodia.AOdiaIO

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdia.ROUTE_ID
import com.kamelong.aodia.AOdiaFragmentCustom
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import com.kamelong2.aodia.SDlog.activity
import java.io.File


class FileSelectFromRouteID(activity:MainActivity,routeID:String,fileSeleter:OnFileSelect): Dialog(activity) ,OpenDirectory{
    init {
        setContentView(R.layout.fileselect_from_routeid)
        val directory= File(activity!!.getExternalFilesDir(null).absolutePath+"/OuDiaDataBase")
        if(!directory.exists()){
            directory.mkdir()
        }
        if(directory.isFile){
            SDlog.toast(directory.absolutePath+"と同名のファイルがあるため、検索を開始できません")
        }
        val databaseFileAdapter=DataBaseFileAdapter(activity as MainActivity,directory.absolutePath,this, routeID)

        val fileListView = findViewById<ListView>(R.id.fileListInSystem)


        fileListView.setAdapter(databaseFileAdapter)
        fileListView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            try {
                activity.aOdia.openFile(databaseFileAdapter.fileList[position])
//                openDirectory(databaseFileAdapter.getItem(position))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

    }




    override fun openDirectory(path: String?) {
    }




    //インターネットの路線ファイル検索


}


interface OnFileSelect{
    fun OnFileSelect(filePath:String)
}



