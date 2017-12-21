package com.kamelong.aodia.timeTable

import android.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 * Created by kame on 2017/12/09.
 */
open class TimeTableFragment:Fragment(),AOdiaFragmentInterface{

    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override var aodiaActivity: AOdiaActivity
        get() = activity as AOdiaActivity
    set(value){}

    var fileIndex=0
    var diaIndex=0
    var direction=0
    lateinit var fragmentContainer:LinearLayout
    lateinit var stationView: StationViewGroup
    lateinit var trainLinear:ViewGroup



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val minTime=Array(diaFile.stationNum,{0})
        for(i in 0 until diaFile.stationNum){
            for(d in 0 until diaFile.getDiaNum()){
                if(diaFile.getDiaName(d)=="基準運転時分"){
                    minTime[i]=0
                }
            }
        }



        fragmentContainer=LinearLayout(activity)
        try {
            aodiaActivity = getActivity() as AOdiaActivity
            fragment = this
            val bundle = arguments
            fileIndex = bundle.getInt("fileNum")
            diaIndex = bundle.getInt("diaNum")
            direction = bundle.getInt("direction")
            diaFile = aodiaActivity.diaFiles[fileIndex]
        } catch (e: Exception) {
            e.printStackTrace()
            //activity.killFragment(this)
        }
        fragmentContainer.isFocusableInTouchMode=true
        fragmentContainer.requestFocus()
        fragmentContainer.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                println("key")
                return if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    true
                } else false
            }
        })
        return fragmentContainer
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val gesture = GestureDetector(aodiaActivity,
                object : GestureDetector.SimpleOnGestureListener() {

                    private val flingV = 0f
                    var fling=false

                    override fun onDown(motionEvent: MotionEvent): Boolean {
                        fling = false
                        return true
                    }

                    override fun onDoubleTap(event: MotionEvent): Boolean {
                        /*
                        val x = event.x.toInt()
                        val timeTablex = x + findViewById(R.id.trainTimeLinear).getScrollX() - findViewById(R.id.stationNameLinear).getWidth()
                        var train = timeTablex / (findViewById(R.id.trainNameLinear) as LinearLayout).getChildAt(0).width
                        if (train < 0) {
                            train = 0
                        }
                        if (train >= timeTable.getTrainNum()) {
                            train = timeTable.getTrainNum() - 1
                        }
                        selectTrain(timeTable.getTrain(train))

                        println(train)
                        */
                        return false
                    }

                    override fun onLongPress(motionEvent: MotionEvent) {

                        /*
                        val y = motionEvent.y.toInt()
                        val timeTabley = y + findViewById(R.id.trainTimeLinear).getScrollY() - findViewById(R.id.trainNameLinear).getHeight()
                        val station = ((findViewById(R.id.stationNameLinear) as LinearLayout).getChildAt(0) as StationNameView).getStationFromY(timeTabley)
                        if (station < 0) {
                            return
                        }
                        val dialog = StationInfoDialog(aodiaActivity, this@TimeTableFragmentOld, diaFile, fileNum, diaNumber, direct, station)
                        dialog.show()
                        */

                    }

                    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, vx: Float, vy: Float): Boolean {
                        trainLinear.scrollBy(vx.toInt(),0)
                        stationView.scrollBy(vy)
                        for(i in 0 until trainLinear.childCount){
                            (trainLinear.getChildAt(i) as TrainViewGroup).scrollBy(vy)
                        }
                        return false
                    }

                    override fun onFling(e1: MotionEvent, e2: MotionEvent, v1: Float, v2: Float): Boolean {
                        val flingV = -v1
                        fling = true
                        Thread(Runnable {
                            while (fling) {
                                try {
                                    Thread.sleep(16)
                                    trainLinear.scrollBy((flingV * 16 / 1000f).toInt(), 0)
                                } catch (e: Exception) {
                                    fling = false
                                }
                            }
                        }).start()
                        return false
                    }
                })



        super.onViewCreated(view, savedInstanceState)
        fragmentContainer.orientation=LinearLayout.HORIZONTAL
        stationView= StationViewGroup(activity,diaFile,direction)
        stationView.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT))
        trainLinear=LinearLayout(activity)
        fragmentContainer.setOnTouchListener { v, event -> gesture.onTouchEvent(event) }

        for(i in 0 until diaFile.getTrainNum(diaIndex,direction)){
            trainLinear.addView(TrainViewGroup(activity,diaFile.getTrain(diaIndex,direction,i)))
        }

        fragmentContainer.addView(stationView)
        fragmentContainer.addView(trainLinear)
    }

}