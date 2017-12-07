package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import com.kamelong.aodia.diadataOld.AOdiaTrain

/**
 * 列車名と列車番号を表示するクラス
 */
class TrainNameView(context: Context, val train:AOdiaTrain, override val xsize: Int):KLView(context) {
    override val ysize: Int
        get() = (textSize*8.25).toInt()

    override fun onDraw(canvas: Canvas?) {
        if(canvas==null)return

    }
}