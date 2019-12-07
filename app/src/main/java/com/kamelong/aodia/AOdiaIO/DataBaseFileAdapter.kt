package com.kamelong.aodia.AOdiaIO

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kamelong.OuDia.SimpleOuDia
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class DataBaseFileAdapter(val activity:MainActivity,val directoryPath:String,val selector:OpenDirectory,var routeID:String) : BaseAdapter() {
    var fileList:ArrayList<File>
    lateinit var layoutInflater:LayoutInflater
    init{
        this.layoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val directory = File(directoryPath)
        fileList= ArrayList()
        try {
            val files = directory.listFiles()
//            val comparator = Comparator<File> { o1, o2 ->
//                //ファイルの比較方法　ソートに使う
//                if (o1.isDirectory) {
//                    return@Comparator if (o2.isDirectory) {
//                        o1.name.compareTo(o2.name)
//                    } else -1
//                }
//                if (!o1.name.endsWith("oud") && !o1.name.endsWith("oud2")) {
//                    return@Comparator if (!o2.isDirectory && !o2.name.endsWith("oud") && !o2.name.endsWith("oud2")) {
//                        o1.name.compareTo(o2.name)
//                    } else 1
//                }
//                if (o2.isDirectory) {
//                    return@Comparator 1
//                }
//                if (!o2.name.endsWith("oud") && !o2.name.endsWith("oud2")) {
//                    -1
//                } else o1.name.compareTo(o2.name)
//            }
//
//            Arrays.sort(files, comparator)

            if(routeID.length==6){
                routeID=routeID.substring(0,5)
            }
            for(file in files){
                if(file.name.startsWith(routeID)){
                    fileList.add(file)
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
            throw e
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = layoutInflater.inflate(R.layout.fileselect_file_view_routeid, parent, false)
        val file=fileList[position]
        val diaFile = SimpleOuDia(file)
        view.findViewById<TextView>(R.id.lineName).setText(diaFile.name)
        val fileNameSplit=file.name.split(".")[0].split("-");
        val routeID=fileNameSplit[0];
        val date=fileNameSplit[1];

        val term=fileNameSplit[2];

        view.findViewById<TextView>(R.id.routeID).setText(routeID)
        view.findViewById<TextView>(R.id.time).setText(date)
        view.findViewById<TextView>(R.id.team).setText(term)
        return view

    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()

    }

    override fun getCount(): Int {
        return fileList.size
    }
}