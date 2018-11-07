package com.kamelong.aodia.databaseTimeTable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by kame on 2017/11/25.
 */
class TrainInfoView constructor(fragment:TimeTableFragment): KLView(fragment.activity){
    override val xSize: Int
        get() = (textSize*2.5).toInt()
    override val ySize: Int
        get() = (textSize*8.0).toInt()


    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null)return
        val startX = ((width - KLView.textPaint.textSize) / 2).toInt()
        canvas.drawText("備", startX.toFloat(), KLView.textPaint.textSize * 2.2f, KLView.textPaint)
        canvas.drawText("考", startX.toFloat(), KLView.textPaint.textSize * 3.5f, KLView.textPaint)
    }
}