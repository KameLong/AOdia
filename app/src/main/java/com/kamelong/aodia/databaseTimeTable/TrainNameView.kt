package com.kamelong.aodia.databaseTimeTable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.preference.PreferenceManager
import android.view.View

/**
 * 列車名を表示するクラス
 */
open class TrainNameView constructor(fragment:TimeTableFragment): KLView(fragment.activity){
    override val xSize: Int
        get() = (textSize*2.5).toInt()
    override val ySize: Int
        get() = (textSize*8.0).toInt()


    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null){
            return
        }
        val spf = PreferenceManager.getDefaultSharedPreferences(context)
        if (spf.getBoolean("trainName", false)) {
            val startX = ((width - KLView.textPaint.textSize) / 2).toInt()
            canvas.drawText("列", startX.toFloat(), KLView.textPaint.textSize * 1.2f, KLView.textPaint)
            canvas.drawText("車", startX.toFloat(), KLView.textPaint.textSize * 2.5f, KLView.textPaint)
            canvas.drawText("名", startX.toFloat(), KLView.textPaint.textSize * 3.8f, KLView.textPaint)
        }
    }

}