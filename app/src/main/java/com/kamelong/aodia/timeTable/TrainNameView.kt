package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import com.kamelong.aodia.diadata.AOdiaTrain

/**
 * 列車名と列車番号を表示するクラス
 */
class TrainNameView(context: Context, val train:AOdiaTrain?, override val xsize: Int):KLView(context) {
    override val ysize: Int
        get() = (textSize*8.25).toInt()

    override fun onDraw(canvas: Canvas?) {
        if(canvas==null)return
        if(train==null){
            drawTextCenter(canvas,"列", textSize*2.5f, blackPaint)
            drawTextCenter(canvas,"車", textSize*3.5f, blackPaint)
            drawTextCenter(canvas,"名", textSize*4.5f, blackPaint)
        }else{
            textPaint.color=train.trainType.textColor.androidColor
            val nameList=ArrayList<ArrayList<Char>>()
            var i=0
            var subList=ArrayList<Char>()
            for(c in train.name.toCharArray()){
                if(i+if(isEng(c)){1}else{2}>12) {
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
                        canvas.translate(width/2+textSize*(nameList.size/2f-line),textSize*(1.0f+0.5f*i))
                        canvas.rotate(90f)
                        canvas.drawText(char.toString(),- textSize*0.8f,textSize*0.8f, textPaint)
                        canvas.restore()

                    }else{
                        canvas.drawText(char.toString(),width/2+textSize*(nameList.size/2f-line-1),textSize*(1.0f+0.5f*i), textPaint)
                    }
                    i+=if(isEng(char)){1}else{2}
                }
            }

            if(train.count.isNotEmpty()){
                drawTextCenter(canvas,train.count, textSize*7f, textPaint)
                drawTextCenter(canvas,"号",textSize*8f, textPaint)
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
        val list= arrayOf('「','」','（','）','ー')
        for(char in list){
            if(c==char)return true
        }
        return false
    }
}