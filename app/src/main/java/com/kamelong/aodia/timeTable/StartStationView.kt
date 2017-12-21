package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.preference.PreferenceManager
import com.kamelong.aodia.diadata.AOdiaTrain

/**
 * 始発駅作業を表示するView
 */
class StartStationView(context: Context, val train:AOdiaTrain?, override val xsize: Int) :KLView(context) {
    var secondsFrag= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("secondSystem",false)
    override val ysize:Int
        get()= (textSize*2.25).toInt()


    init{
        setBackgroundColor(Color.WHITE)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null) return
        if(train==null){
            drawTextCenter(canvas,"始発駅操作",textSize*1.6f, blackPaint)

        }else{
            textPaint.color=train.trainType.textColor.androidColor
            when(train.startAction){
                1->{
                    drawTextCenter(canvas,train.startExchangeStop.toString(),textSize*1.1f, textPaint)
                    if(train.startExchangeTimeEnd>=0){
                        drawTime(canvas, textSize*2.1f,train.startExchangeTimeEnd, textPaint)
                    }else if(train.startExchangeTimeStart>=0){
                        drawTime(canvas, textSize*2.1f,train.startExchangeTimeStart, textPaint)
                    }else{
                    }
                }
                2->drawTextCenter(canvas,"出区",textSize*1.6f, textPaint)
            }


        }
        canvas.drawLine(0f, blackBPaint.strokeWidth/2f,width.toFloat(), blackBPaint.strokeWidth/2f, blackBPaint)
        canvas.drawLine(0f, height-blackBPaint.strokeWidth/2f,width.toFloat(), height-blackBPaint.strokeWidth/2f, blackBPaint)
    }
    fun drawTime(canvas:Canvas,y:Float,time:Int,paint: Paint){
        val hh=(time/3600)%24
        val mm=(time/60)%60
        if(secondsFrag){
            val ss=time%60
            drawTextCenter(canvas, hh.toString() + String.format("%02d", mm)+"-"+String.format("%02d", ss), y, paint)

        }else {
            drawTextCenter(canvas, hh.toString() + String.format("%02d", mm), y, paint)
        }
    }


}