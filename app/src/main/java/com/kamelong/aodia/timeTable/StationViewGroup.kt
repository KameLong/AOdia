package com.kamelong.aodia.timeTable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.ViewGroup
import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 */
class StationViewGroup(context: Context, val diaFile: AOdiaDiaFile, val direct:Int):ViewGroup(context){
    val xSize =(KLView.textSize*5.5).toInt()
    val numberView=TrainNumberView(context,null, xSize)
    val nameView=TrainNameView(context,null, xSize)
    val startView=StartStationView(context,null, xSize)
    val stationView=StationView(context,direct,diaFile, xSize)
    val endView=EndStationView(context,null, xSize)
    val remarkView=RemarkView(context,null, xSize)

    val borderlineWidth=3
    var scroll=0f
    var maxHeight=0;

    init {
        addView(nameView)
        addView(stationView)
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
        stationView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,ysize+stationView.ysize-scroll.toInt())
        ysize+=stationView.ysize
        if(ysize+endView.ysize-scroll.toInt()>height){
            endView.layout(0,height-endView.ysize,width-borderlineWidth,height)
        }else{
            endView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,ysize+endView.ysize-scroll.toInt())

        }
        ysize+=endView.ysize
        remarkView.layout(0,ysize-scroll.toInt(),width-borderlineWidth,ysize+remarkView.ysize-scroll.toInt())
    }
    fun scrollTo( y: Float) {
        scroll=y
    }

    fun scrollBy( y:Float) {
        scroll+=y
        if(maxHeight-scroll<height)scroll=maxHeight-height.toFloat()
        if(scroll<0)scroll=0f
        onLayoutScroll()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if(canvas==null)return
        canvas.drawRect(width-borderlineWidth.toFloat(),0f,width.toFloat(),height.toFloat(), Paint())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var ysize=0
        ysize+=numberView.ysize
        ysize+=nameView.ysize
        ysize+=startView.ysize
        ysize+=stationView.ysize
        ysize+=endView.ysize
        ysize+=remarkView.ysize
        maxHeight=ysize
        this.setMeasuredDimension((KLView.textSize*5.5f).toInt()+borderlineWidth,MeasureSpec.getSize(heightMeasureSpec))

    }

}