package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import com.kamelong.aodia.diadata.AOdiaTrain

/**
 * Created by kame on 2017/12/09.
 */
class RemarkView (context: Context, val train: AOdiaTrain?, override val xsize: Int) :KLView(context) {
    override val ysize: Int
        get() = (textSize*12.25).toInt()
    override fun onDraw(canvas: Canvas?) {
        if(canvas==null)return
        if(train==null){
            drawTextCenter(canvas,"備", textSize*2.5f, blackPaint)
            drawTextCenter(canvas,"考", textSize*3.5f, blackPaint)

        }else{
            textPaint.color=train.trainType.textColor.androidColor
            val nameList=ArrayList<ArrayList<Char>>()
            var i=0
            var subList=ArrayList<Char>()
            for(c in train.remark.toCharArray()){
                if(i+if(isEng(c)){1}else{2}>24) {
                    nameList.add(subList)
                    i=0
                    subList = ArrayList()
                }
                subList.add(c)
                i+=if(isEng(c)){1}else{2}
            }
            if(subList.isNotEmpty()){
                nameList.add(subList)
            }

            for(line in 0 until nameList.size){
                i=0
                for(char in nameList[line]){
                    if(isHorizontal(char)){
                        canvas.save()
                        canvas.rotate(90f)
                        canvas.drawText(char.toString(),textSize*(1.0f+0.5f*i),-width/2-textSize*(nameList.size/2f-line), textPaint)
                        canvas.restore()

                    }else{
                        canvas.drawText(char.toString(),width/2-textSize*(nameList.size/2f-line),textSize*(1.0f+0.5f*i), textPaint)
                    }
                    i+=if(isEng(char)){1}else{2}
                }
            }
        }
    }
    fun isEng(c:Char):Boolean{
        return c.toInt()<256
    }
    fun isHorizontal(c:Char):Boolean{
        if(c.toInt()<256){
            return true
        }
        val list= arrayOf('「','」','（','）')
        for(char in list){
            if(c==char)return true
        }
        return false
    }
}