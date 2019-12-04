package com.kamelong.aodia.KLdatabase

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kamelong.aodia.MainActivity
data class Route(var id:String,var name:String,var color:String){
    lateinit var stationList:ArrayList<String>
}
data class Station(var id:String,var name:String){
}

class KLdetabase(activity:MainActivity): SQLiteOpenHelper(activity, DB_NAME,null, DATABASE_VERSION) {
    companion object{
        const val DB_NAME="KLformat-v1.db"
        const val DATABASE_VERSION=1


        //データベースで使う変数
        const val TABLE_ROUTE="route"
        const val TABLE_STATION="station"
        const val TABLE_ROUTE_STATION_LIST="routeStationList"

        const val ID="id"
        const val ROUTE_NAME="route_name"
        const val ROUTE_COLOR="route_color"

        const val STATION_NAME="station_name"
        const val ROUTE_ID="route_id"
        const val STATION_ID="station_id"
        const val STATION_SEQ="station_sequence"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        //とりあえず何もしない

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //とりあえず何もしない
    }
    fun getStationListFromName(name:String,):ArrayList<Station>


}