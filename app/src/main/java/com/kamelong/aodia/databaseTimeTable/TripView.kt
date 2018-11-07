package com.kamelong.aodia.databaseTimeTable

import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.security.spec.ECField

/**
 * Created by kame on 2017/11/25.
 */
open class TripView constructor(fragment:TimeTableFragment,tripID:Int,direction:Int): KLView(fragment.activity){
    override val xSize: Int
        get() = (textSize*2.5).toInt()
    override val ySize: Int
        get(){
            var ypos=0.25f* textSize

            for(i in 0 until stationName.size){
                if(showStopNumber[i]){
                    ypos+=1.2f* textSize
                    ypos+=1.2f* textSize
                    ypos+=textSize.toFloat()
                }else{
                    when(viewStyle[i]){
                        0->{
                            ypos+=textSize.toFloat()

                        }
                        1->{
                            ypos+=1.2f* textSize
                            ypos+=textSize.toFloat()
                        }
                        2->{
                            ypos+=textSize.toFloat()
                        }
                    }
                }
            }
            return ypos.toInt()
        }

    val stationName=ArrayList<String>()
    val showStopNumber=ArrayList<Boolean>()
    val viewStyle=ArrayList<Int>()

    init{
        var cursor: Cursor?=null
        try {
            if(direction==0){
                val sql = "select station.station_name,route_station.view_style from route_station join station on station.id=route_station.station_id where route_id=$tripID order by station_seq;"
                cursor = fragment.database.rawQuery(sql, null)
            }else{
                val sql = "select station.station_name,route_station.view_style from route_station join station on station.id=route_station.station_id where route_id=$tripID order by station_seq desc;"
                cursor = fragment.database.rawQuery(sql, null)
            }
            val rowcount = cursor.count
            cursor.moveToFirst()
            for (i in 0 until rowcount) {
                stationName.add(cursor.getString(0))
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

        }catch (e:Exception){
        e.printStackTrace()
        }finally {
            if(cursor!=null){
                cursor.close()
            }
        }
    }




    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null)return
        var ypos:Float= textSize.toFloat()
        for(i in 0 until stationName.size){
            if(showStopNumber[i]){
                drawTextCenter(canvas,stationName[i],ypos, textPaint)
                canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                ypos+=1.2f* textSize
                drawTextCenter(canvas,"発着番線",ypos,textPaint)
                canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                ypos+=1.2f* textSize
                drawTextCenter(canvas,stationName[i],ypos, textPaint)
                ypos+=textSize.toFloat()
            }else{
                when(viewStyle[i]){
                    0->{
                        drawTextCenter(canvas,stationName[i],ypos, textPaint)
                        ypos+=textSize.toFloat()

                    }
                    1->{
                        drawTextCenter(canvas,stationName[i],ypos, textPaint)
                        canvas.drawLine(0f,ypos+0.2f* textSize,width.toFloat(),ypos+0.2f* textSize, blackPaint)
                        ypos+=1.2f* textSize
                        drawTextCenter(canvas,stationName[i],ypos, textPaint)
                        ypos+=textSize.toFloat()

                    }
                    2->{
                        drawTextCenter(canvas,stationName[i],ypos, textPaint)
                        ypos+=textSize.toFloat()
                    }

                }

            }
        }

    }
}