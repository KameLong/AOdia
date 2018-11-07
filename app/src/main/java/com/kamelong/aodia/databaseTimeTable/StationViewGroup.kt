package com.kamelong.aodia.databaseTimeTable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.ViewGroup

/**
 * AOdiav2.3以降において１つの列車を表示するViewGroup
 * TrainViewの内部には複数のTripView,TrainNameView,TrainTypeView,TrainInfoView,TrainOuterViewを含むことができる
 *
 */
class StationViewGroup constructor(fragment:TimeTableFragment) : ViewGroup(fragment.activity){

    val tripNameView=TrainNameView(fragment)
    val startView=StartEndView(fragment)
    val endView=StartEndView(fragment)
    val tripViewList=ArrayList<TripView>()
    val typeViewList=ArrayList<TypeView>()
    val infoView=TrainInfoView(fragment)

    val xSize=(KLView.textSize*5).toInt()
    var scroll=0
    var maxScroll:Int=0
    init {
        addView(tripNameView)
        addView(infoView)
        for(i in 0 until fragment.routeID.size){
            tripViewList.add(TripView(fragment,fragment.routeID[i],fragment.direction[i]))
            typeViewList.add(TypeView(fragment))
            addView(tripViewList[i])
            addView(typeViewList[i])
        }
        addView(startView)
        startView.string="始発"

        addView(endView)
        endView.string="終着"

    }
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        tripNameView.layout(0,0,xSize,tripNameView.ySize)
        startView.layout(0,startView.layoutTop,xSize,startView.layoutTop+startView.ySize)

        for(i in 0 until tripViewList.size){
            typeViewList[i].layout(0,typeViewList[i].layoutTop,xSize,typeViewList[i].layoutTop+typeViewList[i].ySize)
            tripViewList[i].layout(0,tripViewList[i].layoutTop,xSize,tripViewList[i].layoutTop+tripViewList[i].ySize)
        }
        endView.layout(0,endView.layoutTop,xSize,endView.layoutTop+endView.ySize)
        infoView.layout(0,infoView.layoutTop,xSize,infoView.layoutTop+infoView.ySize)
    }

    override fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec:Int) {
        var height=0
        tripNameView.measure(xSize,height)
        height+=tripNameView.ySize
        startView.measure(xSize,height)
        height+=startView.ySize
        for(i in 0 until tripViewList.size){
            typeViewList[i].measure(xSize,height)
            height+=typeViewList[i].ySize
            tripViewList[i].measure(xSize,height)
            height+=tripViewList[i].ySize

        }
        endView.measure(xSize,height)
        height+=endView.ySize
        infoView.measure(xSize,height)
        height+=infoView.ySize
        maxScroll=height-MeasureSpec.getSize(heightMeasureSpec)
        if(maxScroll<0){
            maxScroll=0
        }
        this.setMeasuredDimension(xSize+(KLView.textSize*0.1).toInt(), heightMeasureSpec)
    }

    fun scrollTo(value:Int){
        if(value==scroll)return
        scroll=value

        tripNameView.layout(0,0-scroll,xSize,tripNameView.ySize-scroll)

        for(i in 0 until tripViewList.size){
            tripViewList[i].layout(0,tripViewList[i].layoutTop-scroll,xSize,tripViewList[i].layoutTop+tripViewList[i].ySize-scroll)
        }
        infoView.layout(0,infoView.layoutTop-scroll,xSize,infoView.layoutTop+infoView.ySize-scroll)

        if(startView.layoutTop-scroll>0) {
            startView.layout(0, startView.layoutTop - scroll, xSize, startView.layoutTop + startView.ySize - scroll)
        }else{
            startView.layout(0, 0, xSize,startView.ySize )
        }

        for(i in 0 until tripViewList.size){
            if(typeViewList[i].layoutTop-scroll>startView.ySize){
                typeViewList[i].layout(0,typeViewList[i].layoutTop-scroll,xSize,typeViewList[i].layoutTop+typeViewList[i].ySize-scroll)
            }else{
                typeViewList[i].layout(0,startView.ySize,xSize,startView.ySize+typeViewList[i].ySize)
            }
        }
        if(endView.layoutTop+endView.ySize-scroll<height){
            endView.layout(0,endView.layoutTop-scroll,xSize,endView.layoutTop+endView.ySize-scroll)
        }else{
            endView.layout(0,height-endView.ySize,xSize,height)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if(canvas==null)return
        canvas.drawRect(width-KLView.textSize*0.1f,0f,width.toFloat(),height.toFloat(),KLView.blackPaint);
    }



}