package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.diadata.AOdiaTrainType

/**
 * 列車種別　列車番号を表示するView
 */
class TrainNumberView(context: Context, val train: AOdiaTrain, override val xsize: Int):KLView(context) {
    override val ysize: Int
        get() = (textSize*2.35).toInt()

    override fun onDraw(canvas: Canvas?) {
        if(canvas==null) return
        textPaint.color=train.trainType.textColor.androidColor
        drawTextCenter(canvas,train.trainType.name, textSize*1.1f, textPaint)
        drawTextCenter(canvas,train.number, textSize*2.1f, textPaint)
        canvas.drawLine(0f,height- blackBPaint.strokeWidth/2,width.toFloat(),height- blackBPaint.strokeWidth/2, blackBPaint)
    }



}