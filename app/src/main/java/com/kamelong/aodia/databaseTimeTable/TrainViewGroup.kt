package com.kamelong.aodia.databaseTimeTable

import android.database.Cursor
import android.graphics.Canvas
import android.os.Handler
import android.view.ViewGroup
import com.kamelong.aodia.SdLog
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * AOdiav2.3以降において１つの列車を表示するViewGroup
 * TrainViewの内部には複数のTripView,TrainNameView,TrainTypeView,TrainInfoView,TrainOuterViewを含むことができる
 *
 */
class TrainViewGroup constructor(fragment:TimeTableFragment,blockID:Int,stationView:StationViewGroup,handler:Handler) :ViewGroup(fragment.activity){
    var initFinish=false
    val tripNameViewList=ArrayList<TrainNameViewTrip>()
    val startViewList=ArrayList<StartEndViewTrip>()
    val endViewList=ArrayList<StartEndViewTrip>()
    val tripViewList=ArrayList<ArrayList<TripViewTrip?>>()
    val typeViewList=ArrayList<ArrayList<TypeView?>>()
    val infoViewList=ArrayList<TrainInfoView>()

    val stationView=stationView
    var blockID:Int=0

    var columnNum=1
    val xSize=(KLView.textSize*4.5).toInt()
    var scroll=-1
    companion object {
        var allScroll=0
    }

    init{

            this.blockID=blockID
            var cursor: Cursor?=null
            typeViewList.add(ArrayList())
            for(i in fragment.routeID) {
                typeViewList[0].add(null)
            }
            tripViewList.add(ArrayList())
            for(i in fragment.routeID) {
                tripViewList[0].add(null)
            }
            try{
                for(i in 0 until fragment.routeID.size) {
                    val sql = "select id from trip " +
                            "where block_id=$blockID and route_id=${fragment.routeID[i]} and calendar_id=${fragment.calendarID} and trip_direction=${fragment.direction[i]}"
                    cursor = fragment.database.rawQuery(sql, null)
                    val count = cursor.count
                    cursor.moveToFirst()
                    for (j in 0 until count) {
                        if(tripViewList.size==j){
                            tripViewList.add(ArrayList())
                            for(k in fragment.routeID) {
                                tripViewList[j].add(null)
                            }
                        }
                        tripViewList[j][i]=TripViewTrip(fragment,fragment.routeID[i],cursor.getInt(0),fragment.direction[i])
                    }
                }
            }catch(e:Exception){
                SdLog.log(e)
            }finally {
                cursor?.close()
            }
            for(i in 0 until fragment.routeID.size){
                if(typeViewList[0][i]==null) {

                    typeViewList[0][i]=TypeView(fragment)
                }
                if(tripViewList[0][i]==null){
                    tripViewList[0][i]=TripViewTrip(fragment,fragment.routeID[i],-1,fragment.direction[i])
                }

            }
            tripNameViewList.add(TrainNameViewTrip(fragment,tripViewList[0][0]?.tripID?:1))
            handler.post(Runnable {
                addView(tripNameViewList[0])
                for(i in 0 until fragment.routeID.size){
                    addView(tripViewList[0][i])
                    addView(typeViewList[0][i])
                }
                infoViewList.add(TrainInfoView(fragment))
                addView(infoViewList[0])
                startViewList.add(StartEndViewTrip(fragment,1,true,0))
                addView(startViewList[0])
                endViewList.add(StartEndViewTrip(fragment,2,true,0))
                addView(endViewList[0])
                initFinish=true
                scrollTo()
            })


    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        if(!initFinish)return
        tripNameViewList[0].layout(0,0-scroll,xSize,stationView.tripNameView.ySize-scroll)

        for(i in 0 until tripViewList[0].size){
            tripViewList[0][i]?.layout(0,stationView.tripViewList[i].layoutTop-scroll,xSize,stationView.tripViewList[i].layoutTop+stationView.tripViewList[i].ySize-scroll)
        }
        infoViewList[0].layout(0,stationView.infoView.layoutTop-scroll,xSize,stationView.infoView.layoutTop+stationView.infoView.ySize-scroll)

        if(stationView.startView.layoutTop-scroll>0) {
            startViewList[0].layout(0, stationView.startView.layoutTop - scroll, xSize, stationView.startView.layoutTop + stationView.startView.ySize - scroll)
        }else{
            startViewList[0].layout(0, 0, xSize,stationView.startView.ySize )
        }

        for(i in 0 until tripViewList[0].size){
            if(stationView.typeViewList[i].layoutTop-scroll>stationView.startView.ySize){
                typeViewList[0][i]?.layout(0,stationView.typeViewList[i].layoutTop-scroll,xSize,stationView.typeViewList[i].layoutTop+stationView.typeViewList[i].ySize-scroll)
            }else{
                typeViewList[0][i]?.layout(0,stationView.startView.ySize,xSize,stationView.startView.ySize+stationView.typeViewList[i].ySize)
            }
        }
        if(stationView.endView.layoutTop+stationView.endView.ySize-scroll<height){
            endViewList[0].layout(0,stationView.endView.layoutTop-scroll,xSize,stationView.endView.layoutTop+stationView.endView.ySize-scroll)
        }else{
            endViewList[0].layout(0,height-stationView.endView.ySize,xSize,height)
        }
    }
    fun scrollTo(){
        if(scroll== allScroll)return
        if(!initFinish)return
        scroll= allScroll

        tripNameViewList[0].layout(0,0-scroll,xSize,stationView.tripNameView.ySize-scroll)

        for(i in 0 until tripViewList[0].size){
            tripViewList[0][i]?.layout(0,stationView.tripViewList[i].layoutTop-scroll,xSize,stationView.tripViewList[i].layoutTop+stationView.tripViewList[i].ySize-scroll)
        }
        infoViewList[0].layout(0,stationView.infoView.layoutTop-scroll,xSize,stationView.infoView.layoutTop+stationView.infoView.ySize-scroll)

        if(stationView.startView.layoutTop-scroll>0) {
            startViewList[0].layout(0, stationView.startView.layoutTop - scroll, xSize, stationView.startView.layoutTop + stationView.startView.ySize - scroll)
        }else{
            startViewList[0].layout(0, 0, xSize,stationView.startView.ySize )
        }

        for(i in 0 until tripViewList[0].size){
            if(stationView.typeViewList[i].layoutTop-scroll>stationView.startView.ySize){
                typeViewList[0][i]?.layout(0,stationView.typeViewList[i].layoutTop-scroll,xSize,stationView.typeViewList[i].layoutTop+stationView.typeViewList[i].ySize-scroll)
            }else{
                typeViewList[0][i]?.layout(0,stationView.startView.ySize,xSize,stationView.startView.ySize+stationView.typeViewList[i].ySize)
            }
        }
        if(stationView.endView.layoutTop+stationView.endView.ySize-scroll<height){
            endViewList[0].layout(0,stationView.endView.layoutTop-scroll,xSize,stationView.endView.layoutTop+stationView.endView.ySize-scroll)
        }else{
            endViewList[0].layout(0,height-stationView.endView.ySize,xSize,height)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if(canvas==null)return
        canvas.drawRect(width-0.05f*KLView.textSize,0f,width.toFloat(),height.toFloat(),KLView.blackPaint)
    }
    override fun onMeasure(widthMeasureSpec:Int, heightMeasureSpec:Int) {
        this.setMeasuredDimension(xSize+(KLView.textSize*0.05).toInt(), heightMeasureSpec)
        return
        var height=0
        tripNameViewList[0].measure(xSize,height)
        startViewList[0].measure(xSize,height)
        for(i in 0 until tripViewList[0].size){
            typeViewList[0][i]?.measure(xSize,height)
            tripViewList[0][i]?.measure(xSize,height)
        }
        endViewList[0].measure(xSize,height)
        infoViewList[0].measure(xSize,height)
    }


}