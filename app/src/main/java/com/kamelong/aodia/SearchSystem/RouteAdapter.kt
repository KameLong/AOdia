package com.kamelong.aodia.SearchSystem

import com.kamelong.aodia.KLdatabase.RouteStation
import com.kamelong.aodia.KLdatabase.Station
import java.io.File


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R

class RouteAdapter(private val activity:MainActivity,private val station:Station,private val database:KLdetabase) :BaseAdapter() {
    val routeList:ArrayList<RouteStation> = database.getRouteListFromStation(station)

    val connectList=ArrayList<ConnectLine>()
    init{
        for(route in routeList){
            val startStation=database.getStartStation(route.routeID)
            val endStation=database.getEndStation(route.routeID)
            if(endStation.stationID!=route.stationID){
                val connectLine=ConnectLine()
                connectLine.routeID=route.routeID
                connectLine.directionName=database.getStation(endStation.stationID).name
                connectLine.direction=1
                connectLine.routeName=database.getRoute(route.routeID).name
                connectList.add(connectLine)

            }
            if(startStation.stationID!=route.stationID){
                val connectLine=ConnectLine()
                connectLine.routeID=route.routeID
                connectLine.directionName=database.getStation(startStation.stationID).name
                connectLine.direction=0
                connectLine.routeName=database.getRoute(route.routeID).name
                connectList.add(connectLine)
            }
        }
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view=convertView
        if(view==null) {
            view=(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.search_route_view,parent,false)
        }
        if(view !=null){
            view.findViewById<TextView>(R.id.routeName).setText(connectList[position].routeName)
            view.findViewById<TextView>(R.id.direction).setText(connectList[position].directionName)
            return view
        }
        throw Exception("view is null")
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return connectList.size
    }
    class ConnectLine{
        var routeID:String="";
        var direction:Int=0
        lateinit var file: File;

        var routeName:String=""
        var directionName:String=""
    }
}