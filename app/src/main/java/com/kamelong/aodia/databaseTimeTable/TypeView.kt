package com.kamelong.aodia.databaseTimeTable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by kame on 2017/11/25.
 */
class TypeView constructor(fragment:TimeTableFragment): KLView(fragment.activity){
    init{
        setBackgroundColor(Color.WHITE)
    }
    override val xSize: Int
        get() = (textSize*2.5).toInt()
    override val ySize: Int
        get(){
            if(visibility== VISIBLE){
                return (textSize*2.25).toInt()
            }
            return 0
        }


    public override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas==null||visibility!= VISIBLE)return
        canvas.drawLine(0f,blackPaint.strokeWidth/2f, width.toFloat(), blackPaint.strokeWidth/2f, blackPaint)
        drawTextCenter(canvas,"列車種別",1.0f* textSize, textPaint)
        drawTextCenter(canvas,"列車番号",2.0f* textSize, textPaint)
        canvas.drawLine(0f,2.25f * textSize-1-blackPaint.strokeWidth/2f, width.toFloat(), 2.25f * textSize-1-blackPaint.strokeWidth/2f, blackPaint)
    }
}