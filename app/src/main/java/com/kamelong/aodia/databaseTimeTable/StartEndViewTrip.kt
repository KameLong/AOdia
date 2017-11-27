package com.kamelong.aodia.databaseTimeTable

import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color

/**
 * Created by kame on 2017/11/26.
 */
class StartEndViewTrip(fragment:TimeTableFragment,tripID:Int,start:Boolean,opeNumber:Int): KLView(fragment.activity) {
    var startFrag=true
    var tripID=1
    var operationNumber=0
    var station=""
    var time=""
    init{
        setBackgroundColor(Color.WHITE)
        startFrag=start
        this.tripID=tripID
        operationNumber=opeNumber

        if(tripID>0){
            var cursor:Cursor?=null
            try {
                    val sql = "select station.station_name,trip.start_time from (trip join stop on trip.start_stop=stop.id) join station on stop.station_id=station.id where trip.id=$tripID ;"
                    cursor = fragment.database.rawQuery(sql, null)
                if(cursor.count!=0){

                cursor.moveToFirst()
                    station=cursor.getString(0)
                    time=cursor.getString(1)
                }

            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                if(cursor!=null){
                    cursor.close()
                }
            }

        }

    }
    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null)return
        canvas.drawLine(0f,blackPaint.strokeWidth/2f, width.toFloat(), blackPaint.strokeWidth/2f, blackPaint)
        drawTextCenter(canvas,if(operationNumber>=0){operationNumber.toString()}else{""},(textSize).toFloat(),textPaint)
        canvas.drawLine(0f,1.2f * textSize, width.toFloat(), 1.2f * textSize, blackPaint)
        drawTextCenter(canvas,station,(textSize*2.25).toFloat(),textPaint)

        drawTextCenter(canvas,time,(textSize*3.25).toFloat(),textPaint)
        canvas.drawLine(0f,3.45f * textSize, width.toFloat(), 3.45f * textSize, blackPaint)

    }

}