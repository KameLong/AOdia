package com.kamelong.aodia.databaseTimeTable

import android.database.Cursor
import android.graphics.Canvas
import android.preference.PreferenceManager
import com.kamelong.aodia.SdLog

/**
 * Created by kame on 2017/11/25.
 */
class TrainNameViewTrip constructor(fragment:TimeTableFragment,tripID:Int): TrainNameView(fragment){
    lateinit var trainName:String
    init{
        if(tripID>0){
        val sql="select trip_name from trip where id=$tripID"
        var cursor:Cursor?=null
        try{
            cursor=fragment.database.rawQuery(sql,null)
            cursor.moveToFirst()
            trainName=cursor.getString(0)
        }catch (e:Exception){
            SdLog.log(e)
        }finally {
            cursor?.close()
        }
        }else{
            trainName=""
        }
    }
    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        if(canvas==null){
            return
        }
        try {
            val heightSpace = 12
            var value = trainName
            value = value.replace('ー', '｜')
            value = value.replace('（', '(')
            value = value.replace('）', ')')
            value = value.replace('「', '[')
            value = value.replace('」', ']')

            val str = value.toCharArray()
            var lineNum = 1
            var space = heightSpace
            for (i in str.indices) {
                if (space <= 0) {
                    space = heightSpace
                    lineNum++
                }
                if (!(str[i].toInt()<256)) {
                    space--
                }
                space--
            }
            space = heightSpace
            var startX = ((width - lineNum.toFloat() * KLView.textSize.toFloat() * 1.2f) / 2 + (lineNum - 1).toFloat() * KLView.textSize.toFloat() * 1.2f).toInt()
            var startY = (this.height - 8.1f * KLView.textSize).toInt()
            for (i in str.indices) {
                if (space <= 0) {
                    space = heightSpace
                    startX -= (KLView.textSize * 1.2f).toInt()
                    startY = (this.height - 8.1f * KLView.textSize).toInt()
                }
                if (str[i].toInt()<256) {
                    space--
                    canvas.save()
                    canvas.rotate(90f, 0f, 0f)
                    canvas.drawText(str[i].toString(), startY + 2f, (-startX - KLView.textSize * 0.2f), KLView.textPaint)
                    canvas.restore()
                    startY = startY + KLView.textPaint.measureText(str[i].toString()).toInt()
                } else {
                    space = space - 2
                    startY = startY + KLView.textSize
                    canvas.drawText( str[i].toString(), startX.toFloat(), startY.toFloat(), KLView.textPaint)
                }

            }
        } catch (e: Exception) {
            SdLog.log(e)
        }


    }


}

