package com.kamelong.aodia.databaseTimeTable

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import com.kamelong.aodia.SdLog
import java.security.spec.ECField
import kotlin.concurrent.thread

/**
 * Created by kame on 2017/11/26.
 */
class TrainListViewGroup constructor(fragment:TimeTableFragment,pos:Int) : ViewGroup(fragment.activity) {
    val trainViewList= ArrayList<TrainViewGroup?>()
    val fragment:TimeTableFragment=fragment
    var position=pos
    var maxPosition=false
    var scroll=0f
    val handl= Handler()

    var showMax:Int=0


    fun load(pos:Int):Boolean{
        try {
            for (i in pos until fragment.service.blockID.size) {
                if (trainViewList[i] == null) {
                    trainViewList[i] = TrainViewGroup(fragment, fragment.service.blockID[i], fragment.stationView,handl)
                    handl.post( {  try{addView(trainViewList[i])}catch (e:Exception){} })

                    println(i)
                    return true
                }
            }
            for (i in 0 until pos) {
                if (trainViewList[i] == null) {
                    trainViewList[i] = TrainViewGroup(fragment, fragment.service.blockID[i], fragment.stationView,handl)
                    handl.post( {  try{addView(trainViewList[i])}catch (e:Exception){} })
                    println(i)
                    return true
                }
            }
            return false
        }catch(e:Exception){
            SdLog.log("thread1")
            SdLog.log(e)
        }
        return false
    }
    init{
        for(i in fragment.service.blockID){
            trainViewList.add(null)
        }
        thread{
            try {
                while (true) {
                    handl.post(Runnable { scrollBy(0.001f) })
                    if (!load(position)) {
                        break
                    }
                }
            }catch (e:Exception){
                SdLog.log("thread2")
                SdLog.log(e)
            }
        }
    }
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        scrollBy(0f)
    }
    fun scrollBy(dx:Float){
        if(dx==0f)return
        scroll+=dx
        if(scroll<0){
            scroll=0f
        }
        if(scroll>300+(trainViewList[position]?.xSize?:(KLView.textSize*4.5).toInt())){
            scroll=300+(trainViewList[position]?.xSize?:(KLView.textSize*4.5).toInt()).toFloat()
        }
        var pos=-scroll
        for(i in position until trainViewList.size){
            if(trainViewList[i]!=null){
                trainViewList[i]?.layout(+pos.toInt(),0,trainViewList[i]!!.xSize+pos.toInt(),height)
                pos+=trainViewList[i]!!.xSize
            }else{
                pos+=(KLView.textSize*4.5).toInt()
            }
            if(pos>width+200){
                showMax=i
                break
            }
        }
        while(!maxPosition&&scroll>200+(trainViewList[position]?.xSize?:(KLView.textSize*4.5).toInt())){
            scroll-=trainViewList[position]?.xSize?:(KLView.textSize*4.5).toInt()
            position++
        }
        while(position!=0&&scroll<200){
            position--
            scroll+=trainViewList[position]?.xSize?:(KLView.textSize*4.5).toInt()
        }
        maxPosition=(pos+(trainViewList[showMax]?.xSize?:(KLView.textSize*4.5).toInt())<width)


    }
    fun yScrollTo(yScroll:Int){
        TrainViewGroup.allScroll=yScroll
        for(i in position .. showMax){
            trainViewList[i]?.scrollTo()
        }
    }

}