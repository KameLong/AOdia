package com.kamelong.aodia.databaseTimeTable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by kame on 2017/11/25.
 */
class StartEndView constructor(fragment:TimeTableFragment): KLView(fragment.activity){
    var string=""
    init{
        setBackgroundColor(Color.WHITE)
    }
    override val xSize: Int
        get() = (textSize*2.5).toInt()
    override val ySize: Int
        get() = (textSize*3.5).toInt()

    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null)return
        canvas.drawLine(0f,blackPaint.strokeWidth/2f, width.toFloat(), blackPaint.strokeWidth/2f, blackPaint)
        drawTextCenter(canvas,"運用番号",(textSize).toFloat(),textPaint)
        canvas.drawLine(0f,1.2f * textSize, width.toFloat(), 1.2f * textSize, blackPaint)

        drawTextCenter(canvas,string,(textSize*2.7).toFloat(),textPaint)
        canvas.drawLine(0f,3.45f * textSize-1-blackPaint.strokeWidth/2f, width.toFloat(), 3.45f * textSize-1-blackPaint.strokeWidth/2f, blackPaint)

    }
}