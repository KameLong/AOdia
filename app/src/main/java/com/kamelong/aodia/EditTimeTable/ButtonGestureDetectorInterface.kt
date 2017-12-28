package com.kamelong.aodia.EditTimeTable

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * 4方向のflingを検知するGestureDetector
 */
abstract class ButtonGestureDetectorInterface :GestureDetector.SimpleOnGestureListener(){
    override fun onDown(motionEvent: MotionEvent): Boolean{ return false }
    override fun onDoubleTap(event: MotionEvent): Boolean{ return false }
    override fun onLongPress(motionEvent: MotionEvent){}
    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, vx: Float, vy: Float):Boolean{ return false }
    override fun onFling(e1: MotionEvent, e2: MotionEvent, vx: Float, vy: Float): Boolean{
        if(vx<vy&&vx<-vy){
            flingLeft()
            return true
        }
        if(vx<vy&&vx>-vy){
            flingDown()
            return true
        }
        if(vx>vy&&vx<-vy){
            flingUp()
            return true
        }
        if(vx>vy&&vx>-vy){
            flingRight()
            return true
        }
        return false
    }
    open fun flingLeft(){
    }
    open fun flingUp(){
    }
    open fun flingDown(){
    }
    open fun flingRight(){
    }

}
