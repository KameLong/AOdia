package com.kamelong.aodia.databaseTimeTable

import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Paint
import com.kamelong.JPTI.Trip

/**
 * Created by kame on 2017/11/25.
 */
class TripViewTrip constructor(fragment: TimeTableFragment,route_id:Int, trip_id:Int, direction:Int):TripView(fragment,route_id,direction){
    val tripID:Int=trip_id
    val stationIDList=ArrayList<Int>()
    lateinit var timeStationIndexList:Array<Int?>
    val pickupTypeList=ArrayList<Int>()
    val arrivalTimeList=ArrayList<String>()
    val departureTimeList=ArrayList<String>()
    override val xSize: Int
        get() = (textSize *2.5).toInt()


    init{
        var cursor: Cursor?=null
        try {
            if(direction==0){
                val sql = "select station_id,view_style from route_station where route_id=$route_id order by station_seq;"
                cursor = fragment.database.rawQuery(sql, null)
            }else{
                val sql = "select station_id,view_style from route_station where route_id=$route_id order by station_seq desc;"
                cursor = fragment.database.rawQuery(sql, null)
            }
            val rowcount = cursor.count
            cursor.moveToFirst()
            for (i in 0 until rowcount) {
                stationIDList.add(cursor.getInt(0))
                var vs=Integer.parseInt(cursor.getString(1))
                if(direction==0){
                    vs=vs%10
                }else{
                    vs=vs/10
                }
                viewStyle.add(vs)
                    showStopNumber.add(false)

                cursor.moveToNext()
            }

            timeStationIndexList=arrayOfNulls(stationIDList.size)
            if(trip_id>0) {
                val sql2 = "select time.*,stop.station_id from time join stop on time.stop_id=stop.id where trip_id=$trip_id"
                cursor = fragment.database.rawQuery(sql2, null)

                val rowcount2 = cursor.count
                cursor.moveToFirst()
                for (i in 0 until rowcount2) {
                    val index = stationIDList.indexOf(cursor.getInt(7))
                    timeStationIndexList[index] = i
                    pickupTypeList.add(cursor.getInt(4))
                    arrivalTimeList.add(cursor.getString(5))
                    departureTimeList.add(cursor.getString(6))
                    cursor.moveToNext()
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            if(cursor!=null){
                cursor.close()
            }
        }
    }




    override fun onDraw(canvas: Canvas?) {
        if(canvas==null)return
        var ypos:Float= textSize.toFloat()
        for(i in 0 until stationIDList.size){
            if(showStopNumber[i]){
                drawTextCenter(canvas,arrivalTime(i),ypos, textPaint)
                canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                ypos+=1.2f* textSize
                drawTextCenter(canvas,"発着番線",ypos, textPaint)
                canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                ypos+=1.2f* textSize
                drawTextCenter(canvas,departureTime(i),ypos, textPaint)
                ypos+= textSize.toFloat()
            }else{
                when(viewStyle[i]){
                    0->{
                        drawTextCenter(canvas,departureTime(i),ypos, textPaint)
                        ypos+= textSize.toFloat()

                    }
                    1->{
                        drawTextCenter(canvas,arrivalTime(i),ypos, textPaint)
                        canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                        ypos+=1.2f* textSize
                        drawTextCenter(canvas,departureTime(i),ypos, textPaint)
                        ypos+= textSize.toFloat()

                    }
                    2->{
                        drawTextCenter(canvas,arrivalTime(i),ypos, textPaint)
                        ypos+= textSize.toFloat()
                    }

                }

            }
        }

    }
    fun arrivalTime(i:Int):String{
        try {
            if(timeStationIndexList[i]==null)return ":  :"
            return arrivalTimeList[timeStationIndexList[i]!!]?:":  :"
        }catch (e:Exception){
            return ":  :"
        }
    }
    fun departureTime(i:Int):String{
        try {
            if(timeStationIndexList[i]==null)return ":  :"
            return departureTimeList[timeStationIndexList[i]!!]?:":  :"
        }catch (e:Exception){
            return ":  :"
        }
    }

}