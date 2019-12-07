package com.kamelong.aodia.SearchSystem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.kamelong.aodia.AOdiaIO.FileSelectFromRouteID
import com.kamelong.aodia.AOdiaIO.OnFileSelect
import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.aodia.KLdatabase.RouteStation
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import kotlinx.android.synthetic.main.search_station_view.view.*

class StationAdapter(private val activity:MainActivity,private val searchWord:String) :BaseAdapter() {
    private val database=KLdetabase(activity)
    private val stationList=database.getStationListFromName(searchWord,true)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view=convertView
        if(view==null) {
            view=(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.search_station_view,parent,false)
        }
        if(view !=null){
            view.findViewById<TextView>(R.id.stationName).setText(stationList[position].name)
            val routeListView=view.findViewById<LinearLayout>(R.id.routeList)
            routeListView.removeAllViews()
            val routeList:ArrayList<RouteStation> = database.getRouteListFromStation(stationList[position])

            val connectList=ArrayList<RouteAdapter.ConnectLine>()
            for(route in routeList){
                val startStation=database.getStartStation(route.routeID)
                val endStation=database.getEndStation(route.routeID)
                if(endStation.stationID!=route.stationID){
                    val connectLine= RouteAdapter.ConnectLine()
                    connectLine.routeID=route.routeID
                    connectLine.directionName=database.getStation(endStation.stationID).name
                    connectLine.direction=1
                    connectLine.routeName=database.getRoute(route.routeID).name
                    connectList.add(connectLine)

                }
                if(startStation.stationID!=route.stationID){
                    val connectLine= RouteAdapter.ConnectLine()
                    connectLine.routeID=route.routeID
                    connectLine.directionName=database.getStation(startStation.stationID).name
                    connectLine.direction=0
                    connectLine.routeName=database.getRoute(route.routeID).name
                    connectList.add(connectLine)
                }
            }
            for(route in connectList){
                val routeView=(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.search_route_view,routeListView,false)
                routeView.findViewById<TextView>(R.id.routeName).setText(route.routeName)
                routeView.findViewById<TextView>(R.id.direction).setText(route.directionName+" 方面")
                routeView.findViewById<Button>(R.id.openTimeTable).setOnClickListener{
                    val dialog=FileSelectFromRouteID(activity,route.routeID,object:OnFileSelect{
                        override fun OnFileSelect(filePath: String) {
                        }})
                    dialog.show()
                }
                routeView.findViewById<Button>(R.id.openStationTimeTable).setOnClickListener{
                    val dialog=FileSelectFromRouteID(activity,route.routeID,object:OnFileSelect{
                        override fun OnFileSelect(filePath: String) {
                        }})
                    dialog.show()
                }

                routeListView.addView(routeView)
            }
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
        return stationList.size
    }
}