package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import com.kamelong.aodia.diadata.AOdiaTrain

/**
 * 始発駅作業を表示するView
 */
class StartStationView(context: Context, val train:AOdiaTrain?, override val xsize: Int) :KLView(context) {
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
        }
        canvas.drawLine(0f, blackBPaint.strokeWidth/2f,width.toFloat(), blackBPaint.strokeWidth/2f, blackBPaint)
        canvas.drawLine(0f, height-blackBPaint.strokeWidth/2f,width.toFloat(), height-blackBPaint.strokeWidth/2f, blackBPaint)
    }

}