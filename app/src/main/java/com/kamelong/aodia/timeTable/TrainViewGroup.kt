package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import com.kamelong.JPTI.Station
import com.kamelong.aodia.diadata.AOdiaTrain

class TrainViewGroup(context: Context, val train: AOdiaTrain): ViewGroup(context){
    val diaFile=train.diaFile

    val xSize=(KLView.textSize*2.5).toInt()
    val numberView=TrainNumberView(context,train,xSize)
    val nameView=TrainNameView(context,train,xSize)
    val startView=StartStationView(context,train,xSize)
    val trainView =TrainView(context,train,xSize)
    val endView=EndStationView(context,train,xSize)
    val remarkView=RemarkView(context,train,xSize)

    var scroll=0f
    val borderlineWidth=2
    var maxHeight=0

    init {
        addView(nameView)
        addView(trainView)
        addView(remarkView)
        addView(startView)
        addView(endView)
        addView(numberView)
    }
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        onLayoutScroll()
    }

    fun onLayoutScroll(){
        var ysize=0
        numberView.layout(0,ysize,width-borderlineWidth,ysize+numberView.ysize)
        ysize+=numberView.ysize
        nameView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,ysize+nameView.ysize-scroll.toInt())
        ysize+=nameView.ysize
        if(ysize-scroll<numberView.ysize){
            startView.layout(0, numberView.ysize, width - borderlineWidth, numberView.ysize+ startView.ysize)
        }else {
            startView.layout(0, ysize - scroll.toInt(), width - borderlineWidth, ysize + startView.ysize - scroll.toInt())
        }
        ysize+=startView.ysize
        trainView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,if(ysize+trainView.ysize-scroll>height){height}else{ysize+trainView.ysize-scroll.toInt()})
        ysize+=trainView.ysize
        if(ysize+endView.ysize-scroll.toInt()>height){
            endView.layout(0,height-endView.ysize,width-borderlineWidth,height)
        }else{
            endView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,ysize+endView.ysize-scroll.toInt())

        }
        ysize+=endView.ysize
        remarkView.layout(0,if(ysize-scroll>height){height}else{ysize-scroll.toInt()},width-borderlineWidth,if(ysize+remarkView.ysize-scroll>height){height}else{ysize+remarkView.ysize-scroll.toInt()})
    }
    fun scrollTo( y: Float) {
        scroll=y
    }

    fun scrollBy( y:Float) {

        scroll+=y
        if(maxHeight-scroll<height)scroll=maxHeight-height.toFloat()
        if(scroll<0)scroll=0f
    }




    override fun dispatchDraw(canvas: Canvas?) {

        requestLayout()
        super.dispatchDraw(canvas)
        if(canvas==null)return
        canvas.drawRect(width-2f,0f,width.toFloat(),height.toFloat(), Paint())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var ysize=0
        ysize+=numberView.ysize
        ysize+=nameView.ysize
        ysize+=startView.ysize
        ysize+= trainView.ysize
        ysize+=endView.ysize
        ysize+=remarkView.ysize
        maxHeight=ysize
        this.setMeasuredDimension(xSize +borderlineWidth,MeasureSpec.getSize (heightMeasureSpec ))

    }
}