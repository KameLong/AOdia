package com.kamelong.aodia.SearchSystem

import com.kamelong.aodia.KLdatabase.RouteStation
import com.kamelong.aodia.KLdatabase.Station
import java.io.File


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import java.util.jar.Attributes

class RouteView(private val activity:MainActivity, private val connect:ConnectLine,routeViewClicked: OnRouteViewClicked) :LinearLayout(activity) {

    init{
        LayoutInflater.from(context).inflate(R.layout.search_route_view, this)
        this.findViewById<TextView>(R.id.routeName).setText(connect.routeName)
        this.findViewById<TextView>(R.id.direction).setText(connect.directionName)
        this.findViewById<Button>(R.id.openTimeTable).setOnClickListener { routeViewClicked.onRouteTimeTableClicked(connect.routeID) }
        this.findViewById<Button>(R.id.openStationTimeTable).setOnClickListener { routeViewClicked.onStationTimeTableClicked(connect.routeID,connect.direction) }
    }
    class ConnectLine{
        var routeID:String="";
        var direction:Int=0
        lateinit var file: File;

        var routeName:String=""
        var directionName:String=""
        var seq=0
    }
}
interface OnRouteViewClicked{
    fun onRouteTimeTableClicked(routeID:String)
    fun onStationTimeTableClicked(routeID:String,direction:Int)
}