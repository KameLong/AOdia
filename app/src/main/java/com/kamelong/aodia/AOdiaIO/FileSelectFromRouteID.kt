package com.kamelong.aodia.AOdiaIO

import android.os.Bundle
import android.view.View
import com.kamelong.OuDia.LineFile
import com.kamelong.aodia.AOdia
import com.kamelong.aodia.AOdiaFragmentCustom

class FileSelectFromRouteID: AOdiaFragmentCustom() {
    private var routeID:String="";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        routeID = bundle!!.getString(AOdia.ROUTE_ID, "")

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getName(): String {
        return ""
    }

    override fun getLineFile(): LineFile? {
        return null;
    }

}