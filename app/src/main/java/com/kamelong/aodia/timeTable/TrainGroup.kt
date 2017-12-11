package com.kamelong.aodia.timeTable

import android.content.Context
import android.view.ViewGroup

/**
 */
class TrainGroup(context: Context):ViewGroup(context){
    var scroll=0f
    var xsize=75
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if(childCount==0)return
        //xsize=getChildAt(0).measuredWidth
        onLayoutScroll()
    }

    fun onLayoutScroll(){
        for(i in 0 until childCount){
            getChildAt(i).layout(xsize*i-scroll.toInt(),0,(i+1)*xsize-scroll.toInt(),height)
        }
    }
    override fun scrollBy(dx:Int,dy:Int){
        scrollBy(dx.toFloat(),dy.toFloat())
    }
    fun scrollBy(dx:Float,dy:Float){
        scroll+=dx
        if(scroll>xsize*childCount-width)scroll=xsize*childCount-width.toFloat()
        if(scroll<0)scroll=0f

        for(i in 0 until childCount){
            (getChildAt(i) as TrainViewGroup).scrollBy(dy)
            if(i*xsize>scroll&&(i+1)*xsize<scroll+width){

                println(i)
                (getChildAt(i) as TrainViewGroup).requestLayout()
            }
        }
    }

}