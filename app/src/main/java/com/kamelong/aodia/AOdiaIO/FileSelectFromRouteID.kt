package com.kamelong.aodia.AOdiaIO

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdia.ROUTE_ID
import com.kamelong.aodia.AOdiaFragmentCustom
import com.kamelong.aodia.R
import com.kamelong.tool.SDlog
import java.io.File

class FileSelectFromRouteID: AOdiaFragmentCustom() {

    public var routeID:String=""
    lateinit var fragmentContainer:View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        try{
            
            routeID=arguments?.getString(ROUTE_ID,"")?:""
        }catch (e:Exception){
            SDlog.log(e)
        }
        if(routeID.length==0){
            aOdia.killFragment(this)
        }
        fragmentContainer=inflater.inflate(R.layout.fileselector, container, false)
        return fragmentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //端末内の路線ファイル検索
        //OuDiaデータベースのデータを保管するフォルダ
        val directory= File(activity!!.getFilesDir().absolutePath+"/OuDiaDataBase")
        if(!directory.exists()){
            directory.mkdir()
        }
        if(directory.isFile){
            SDlog.toast(directory.absolutePath+"と同名のファイルがあるため、検索を開始できません")
        }
        val files=directory.listFiles()


        for (file in files){
            if(file.absolutePath.startsWith(routeID+"_")){

            }
        }
        //インターネットの路線ファイル検索


    }

    /**
     * routeIDを保存
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ROUTE_ID,routeID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState!=null){
            routeID=savedInstanceState.getString(ROUTE_ID,"")
        }
    }


    override fun getName(): String {
        return ""
    }

    override fun getLineFile(): LineFile? {
        return null;
    }

}