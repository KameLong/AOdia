package com.kamelong.aodia.KLdatabase

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.kamelong.aodia.MainActivity
import com.kamelong.tool.SDlog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files.delete
import java.nio.file.Files.exists



class Route(){
    lateinit var id:String
    lateinit var name:String
    lateinit var color:String
    //駅ID一覧
    var stationList:ArrayList<RouteStation> = ArrayList()
    constructor(cursor: Cursor):this(){
        id=cursor.getString(0)
        name=cursor.getString(1)
        color=cursor.getString(2)
    }
    fun equals(other:Route):Boolean{
        return id.equals(other.id)
    }
}
class Station(){
    lateinit var id:String
    lateinit var name:String
    constructor(cursor: Cursor):this(){
        id=cursor.getString(0)
        name=cursor.getString(1)
    }

    override fun equals(other: Any?): Boolean {
        if(other is Station){
            return this.id.equals(other.id)
        }
        return super.equals(other)
    }

}
class RouteStation(){
    lateinit var id:String
    lateinit var routeID:String
    lateinit var stationID:String
    var seq:Int=0
    constructor(cursor: Cursor):this(){
        id=cursor.getString(0)
        routeID=cursor.getString(1)
        stationID=cursor.getString(2)
        seq=cursor.getInt(3)
    }


}

class KLdetabase(private val activity:MainActivity): SQLiteOpenHelper(activity, DB_NAME,null, DATABASE_VERSION) {

    companion object{
        const val DB_NAME="KLformat-v1.db"
        const val DB_FILE_NAME="forJPTI.db"
        const val DATABASE_VERSION=1


        //データベースで使う変数
        const val TABLE_ROUTE="route"
        const val TABLE_STATION="station"
        const val TABLE_ROUTE_STATION_LIST="routeStationList"

        const val ID="id"
        const val ROUTE_NAME="route_name"
        const val ROUTE_COLOR="route_color"

        const val STATION_NAME="name"
        const val ROUTE_ID="route_id"
        const val STATION_ID="station_id"
        const val STATION_SEQ="station_seqence"

    }

    private var databaseExist = true //適切なDBファイルが存在するか
    private val dbPath: File=activity.getDatabasePath(DB_NAME)

    override fun onCreate(db: SQLiteDatabase?) {
        super.onOpen(db);
        databaseExist = false;

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val databasePath = this.dbPath.getAbsolutePath()
        val file = File(databasePath)
        if (file.exists()) {
            file.delete()
        }
        databaseExist = false
    }
    override fun getWritableDatabase(): SQLiteDatabase {
        var database = super.getWritableDatabase()
        if (!databaseExist) {
            try {
                database.close()
                database = copyDatabaseFromAssets()
                databaseExist = true
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return database
    }
    fun getStation(stationID:String):Station{
        var c = writableDatabase.rawQuery("select * from $TABLE_STATION where $ID = ? ", arrayOf(stationID))
        c.moveToFirst()
        return Station(c)
    }
    fun getRoute(routeID:String):Route{
        var c = writableDatabase.rawQuery("select * from $TABLE_ROUTE where $ID = ? ", arrayOf(routeID))
        c.moveToFirst()
        return Route(c)
    }



    fun getStationListFromName(name:String,like:Boolean):ArrayList<Station>{
        val result = java.util.ArrayList<Station>()
        var c = writableDatabase.rawQuery("select * from $TABLE_STATION where $STATION_NAME = ? ", arrayOf(name))
        c.moveToFirst()
        for (i in 0 until c.count) {
            val station=Station(c)
            if (!result.contains(station)) {
                result.add(station)
            }
            c.moveToNext()
        }
        if(like){
            c = writableDatabase.rawQuery("select * from $TABLE_STATION where $STATION_NAME like ?" , arrayOf("%$name%"))
            c.moveToFirst()
            for (i in 0 until c.count) {
                val station=Station(c)
                if (!result.contains(station)) {
                    result.add(station)
                }
                c.moveToNext()
            }
        }
        return result
    }
    fun getStartStation(routeID:String):RouteStation{
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $ROUTE_ID = ? order by $STATION_SEQ", arrayOf(routeID))
            cursor.moveToFirst()
            return RouteStation(cursor)
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        throw Exception("RouteStation don't have routeID=$routeID")

    }
    fun getEndStation(routeID:String):RouteStation{
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $ROUTE_ID = ? order by $STATION_SEQ DESC", arrayOf(routeID))
            cursor.moveToFirst()
            return RouteStation(cursor)
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        throw Exception("RouteStation don't have routeID=$routeID")
    }
    fun getRouteStationNum(routeID:String):Int{
        var result=0;
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $ROUTE_ID = ? order by $STATION_SEQ DESC", arrayOf(routeID))
            cursor.moveToFirst()
            return cursor.getInt(2)
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        throw Exception("RouteStation don't have routeID=$routeID")

    }
    fun getRouteListFromStation(station:Station):ArrayList<RouteStation>{
        val result=ArrayList<RouteStation>()
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $STATION_ID = ?", arrayOf(station.id))
            cursor.moveToFirst()
            for (i in 0 until cursor.count) {
                val routeStation = RouteStation(cursor)
                result.add(routeStation)
                cursor.moveToNext()
            }
            return result
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        return result

    }


    fun getRouteListFromStation(stationList:ArrayList<Station>):ArrayList<RouteStation>{
        val result=ArrayList<RouteStation>()
        var cursor:Cursor?=null
        try {
            for (station in stationList) {
                cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $STATION_ID = ?", arrayOf(station.id))
                cursor.moveToFirst()
                for (i in 0 until cursor.count) {
                    val routeStation = RouteStation(cursor)
                    result.add(routeStation)
                    cursor.moveToNext()
                }
            }
            return result
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        return result

    }

    /**
     * assetsにあるデータベースをdata/data/package/databasesにコピーする
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun copyDatabaseFromAssets(): SQLiteDatabase {
        val inputStream = this.activity.getAssets().open(DB_FILE_NAME)
        val outputStream = FileOutputStream(dbPath)

        val buffer = ByteArray(1024)
        var size: Int
        do{
            size=inputStream.read(buffer)
            if(size<=0){
                break
            }
            outputStream.write(buffer, 0, size)

        }while(true)
        outputStream.flush()
        outputStream.close()
        inputStream.close()

        return super.getWritableDatabase()
    }

    /**
     * 指定routeIDと駅順から対応するstationIDを返す
     */
    public fun findStation(routeID:String,stationIndex:Int):Station{
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select $STATION_ID from $TABLE_ROUTE_STATION_LIST where $ROUTE_ID=? and $STATION_SEQ = ?", arrayOf(routeID,stationIndex.toString()))
            cursor.moveToFirst()
            return Station(cursor)
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        return Station()
    }
    /**
     * stationIDからその駅を通過するroute一覧を返す
     */
    fun findRouteList(stationID:String):ArrayList<RouteStation>{
        val result=ArrayList<RouteStation>()
        var cursor:Cursor?=null
        try {
            cursor = writableDatabase.rawQuery("select * from $TABLE_ROUTE_STATION_LIST where $STATION_ID = ?", arrayOf(stationID))
            cursor.moveToFirst()
            for (i in 0 until cursor.count) {
                val id = cursor.getString(0)
                val name = cursor.getString(1)
                val routeStation = RouteStation(cursor)
                result.add(routeStation)
                cursor.moveToNext()
            }
            return result
        }catch (e:Exception){
            SDlog.log(e)
        }
        finally {
            if(cursor!=null){
                cursor.close()
            }
        }
        return result


    }



}