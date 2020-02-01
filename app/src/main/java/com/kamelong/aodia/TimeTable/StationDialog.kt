package com.kamelong.aodia.TimeTable
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*

import com.kamelong.aodia.AOdia
import com.kamelong.OuDia.LineFile
import com.kamelong.OuDia.Train
import com.kamelong.aodia.AOdiaIO.FileSelectFromRouteID
import com.kamelong.aodia.AOdiaIO.OnFileSelect
import com.kamelong.aodia.EditTrain.OnTrainChangeListener
import com.kamelong.aodia.KLdatabase.KLdetabase
import com.kamelong.aodia.KLdatabase.RouteStation
import com.kamelong.aodia.MainActivity
import com.kamelong.aodia.R
import com.kamelong.aodia.SearchSystem.OnRouteViewClicked
import com.kamelong.aodia.SearchSystem.RouteView
import com.kamelong.aodia.StationTimeTable.OnSortButtonClickListener
import com.kamelong.tool.SDlog
import kotlinx.android.synthetic.main.diagram.*
import java.io.File

/**
 * Created by kame on 2017/01/28.
 */
/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */
class StationDialog(private val activity: MainActivity, internal var lineFile: LineFile, internal var diaIndex: Int, internal var direction: Int, internal var stationIndex: Int,private val timetable:OnTrainChangeListener) : Dialog(activity) {
    internal var aodia: AOdia

    init {
        aodia = activity.aOdia
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.timetable_station_info)
        init()
    }

    private fun init() {
        try {
            val stationNameView = findViewById<TextView>(R.id.stationNameView)
            stationNameView.text = lineFile.station[stationIndex].name + "駅"
            val beforeStationButton = findViewById<Button>(R.id.beforeStationButton)
            if (stationIndex - (1 - 2 * direction) >= 0 && stationIndex - (1 - 2 * direction) < lineFile.stationNum) {
                beforeStationButton.text = "⇦" + lineFile.station[stationIndex - (1 - 2 * direction)].name + "駅"
                beforeStationButton.visibility = View.VISIBLE
            } else {
                beforeStationButton.visibility = View.INVISIBLE
            }
            beforeStationButton.setOnClickListener {
                stationIndex = stationIndex - (1 - 2 * direction)
                this@StationDialog.init()
            }
            val afterStationButton = findViewById<Button>(R.id.afterStationButton)
            if (stationIndex + (1 - 2 * direction) >= 0 && stationIndex + (1 - 2 * direction) < lineFile.stationNum) {
                afterStationButton.text = lineFile.station[stationIndex + (1 - 2 * direction)].name + "駅⇨"
                afterStationButton.visibility = View.VISIBLE
            } else {
                afterStationButton.visibility = View.INVISIBLE
            }
            afterStationButton.setOnClickListener {
                stationIndex = stationIndex + (1 - 2 * direction)
                this@StationDialog.init()
            }
            val downTimetable = findViewById<Button>(R.id.downTimeTableButton)
            downTimetable.setOnClickListener {
                aodia.openStationTimeTable(lineFile, diaIndex, Train.DOWN, stationIndex)
                this@StationDialog.dismiss()
            }
            val upTimetable = findViewById<Button>(R.id.upTimeTableButton)
            upTimetable.setOnClickListener {
                aodia.openStationTimeTable(lineFile, diaIndex, Train.UP, stationIndex)
                this@StationDialog.dismiss()
            }

            if(lineFile.routeID.length!=0){
                if(lineFile.getStation(stationIndex).stationID.length==0){
                    return
                }
                val database=KLdetabase(activity)
//                var routeStationID=0
//                try{
//                    routeStationID=lineFile.routeID.toInt()*100+stationIndex
//                }catch (e:Exception){
//
//                }
//                if(routeStationID==0){
//                    return
//                }
//
//                val routeStation=database.getRouteStation(routeStationID.toString())
                val connectStationsListView=findViewById<LinearLayout>(R.id.connectLines)
                connectStationsListView.removeAllViews()


                val routeList:ArrayList<RouteStation> = database.getRouteListFromStation(database.getStation(lineFile.getStation(stationIndex).stationID))

                val connectList=ArrayList<RouteView.ConnectLine>()
                for(route in routeList){
                    if(route.routeID.length==6){
                    }
                    val startStation=database.getStartStation(route.routeID)
                    val endStation=database.getEndStation(route.routeID)
                    if(endStation.stationID!=route.stationID){
                        val connectLine= RouteView.ConnectLine()
                        connectLine.routeID=route.routeID
                        connectLine.directionName=database.getStation(endStation.stationID).name
                        connectLine.direction=0
                        connectLine.routeName=database.getRoute(route.routeID).name
                        connectLine.seq=route.seq
                        connectList.add(connectLine)

                    }
                    if(startStation.stationID!=route.stationID){
                        val connectLine= RouteView.ConnectLine()
                        connectLine.routeID=route.routeID
                        connectLine.directionName=database.getStation(startStation.stationID).name
                        connectLine.direction=1
                        connectLine.routeName=database.getRoute(route.routeID).name
                        connectLine.seq=route.seq
                        connectList.add(connectLine)
                    }
                }
                //接続路線について
                for(route in connectList){

                    val routeView=RouteView(activity,route,object:OnRouteViewClicked{
                        override fun onRouteTimeTableClicked(routeID: String) {
                            val dialog= FileSelectFromRouteID(activity,route.routeID,object: OnFileSelect {
                                override fun OnFileSelect(filePath: String) {
                                    val openSelect=OpenLineFileSelector(activity,object:OnLineFileOpenSelect{
                                        override fun openAsIncludeLine(direction: Int) {
                                            val lineFile=LineFile(File(filePath))
                                            lineFile.setRouteID(database)
                                            if(route.direction==Train.DOWN){
                                                for(i in 0..(route.seq-1)){
                                                    lineFile.deleteStation(0)
                                                }
                                            }else{
                                                val stationNum=lineFile.stationNum
                                                for(i in (route.seq+1)..(stationNum-1)){
                                                    lineFile.deleteStation(route.seq+1)
                                                }
                                                lineFile.reverse()
                                            }
                                            if(this@StationDialog.direction==Train.DOWN){
                                            }else{
                                                lineFile.reverse()
                                            }
                                            if(direction==Train.DOWN){
                                            }else{
                                                lineFile.reverse()
                                            }


                                            this@StationDialog.lineFile.addLineFile(this@StationDialog.stationIndex,lineFile)
                                            this@StationDialog.timetable.allTrainChange()
                                        }

                                        override fun openAsNewLine() {
                                            activity.aOdia.openFile(File(filePath))
                                        }
                                    })
                                    openSelect.show()

                                    this@StationDialog.dismiss()
                                }})
                            dialog.show()
                        }

                        override fun onStationTimeTableClicked(routeID: String, direction: Int) {
                        }
                    })
                    connectStationsListView.addView(routeView)
                }
            }


        } catch (e: Exception) {
            SDlog.log(e)
        }

    }

    fun setOnSortListener(listener: OnSortButtonClickListener) {
        val sortButton = findViewById<Button>(R.id.sortButton)
        sortButton.visibility = View.VISIBLE

        sortButton.setOnClickListener {
            listener.onSortCicked(stationIndex)
            this@StationDialog.dismiss()
        }

    }
    class ConnectLine{
        var routeID:String="";
        var direction:Int=0
        lateinit var file: File;

        var routeName:String=""
        var directionName:String=""
    }
    class ConnectLinesAdapter(val activity:MainActivity,val routeID:String,val stationIndex:Int) : BaseAdapter() {
        val connectLineList=ArrayList<ConnectLine>()

        val database=KLdetabase(activity)
        //指定路線と方向一覧を取得する



        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return connectLineList.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view=convertView
            if(view==null){
                view=LayoutInflater.from(activity).inflate(R.layout.fileselect_file_view_routeid,parent,false)
            }
            if(view!=null){
                view.findViewById<TextView>(R.id.fileName).setText(connectLineList[position].file.name)
                view.findViewById<TextView>(R.id.directionName).setText(connectLineList[position].directionName)
            }

            return view!!
        }

    }

}


