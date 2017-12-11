package com.kamelong.aodia.EditTimeTable

import android.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import com.kamelong.aodia.AOdiaActivity
import com.kamelong.aodia.AOdiaFragmentInterface
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaDiaFile
import com.kamelong.aodia.timeTable.StationViewGroup
import com.kamelong.aodia.timeTable.TrainViewGroup

/**
 * 時刻表を編集するためのFrgment
 */
class TrainTimeEditFragment : Fragment(),AOdiaFragmentInterface{
    val trainEdit=TrainEdit(this)

    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    override var aodiaActivity: AOdiaActivity
        get() = activity as AOdiaActivity
        set(value){}

    var fileIndex=0
    var diaIndex=0
    var direction=0
    lateinit var fragmentContainer:ViewGroup
    lateinit var stationView: StationViewGroup
    lateinit var trainLinear:LinearLayout

    lateinit var timeTableLayout:LinearLayout


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        fragmentContainer=inflater.inflate(R.layout.train_time_edit_fragment, container, false) as ViewGroup
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
                        val x = event.x.toInt()
                        val y =event.y.toInt()
                        val timeTablex = x + trainLinear.getScrollX() - stationView.getWidth()
                        if(trainLinear.childCount==0)return false
                        val trainIndex=timeTablex/trainLinear.getChildAt(0).width
                        if(trainIndex<0||trainIndex>=trainLinear.childCount)return false
                        val stationIndex=stationView.getStationIndex(y)
                        fragmentContainer.findViewById<LinearLayout>(R.id.edit1).visibility=View.VISIBLE
                        fragmentContainer.findViewById<LinearLayout>(R.id.edit2).visibility=View.VISIBLE
                        trainEdit.focusTrain=trainIndex
                        trainEdit.focusPoint=stationIndex
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
                                    e.printStackTrace()

                                    fling = false
                                }
                            }
                        }).start()
                        return false
                    }
                })



        super.onViewCreated(view, savedInstanceState)
        timeTableLayout=fragmentContainer.findViewById(R.id.timetable)

        stationView= StationViewGroup(activity, diaFile, direction)
        stationView.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT))
        trainLinear=LinearLayout(activity)
        trainLinear.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT))

        timeTableLayout.setOnTouchListener { v, event -> gesture.onTouchEvent(event) }

        for(i in 0 until diaFile.getTrainNum(diaIndex,direction)){
            val t= TrainViewGroup(activity, diaFile.getTrain(diaIndex, direction, i))
            trainLinear.addView(t)
        }

        timeTableLayout.addView(stationView)
        timeTableLayout.addView(trainLinear)
        initEditButton()
    }
    fun initEditButton(){
        val backButton=fragmentContainer.findViewById<Button>(R.id.buttonN)
        backButton.setOnClickListener {
            fragmentContainer.findViewById<LinearLayout>(R.id.edit1).visibility=View.GONE
            fragmentContainer.findViewById<LinearLayout>(R.id.edit2).visibility=View.GONE
        }
        val moveButton=fragmentContainer.findViewById<Button>(R.id.buttonB)
        val moveButtonG=GestureDetector(aodiaActivity,
                object :ButtonGestureDetectorInterface() {
                    override fun flingDown() {
                        trainEdit.focusPoint+=3
                    }
                    override fun flingUp() {
                        trainEdit.focusPoint-=3
                    }
                    override fun flingLeft() {
                        trainEdit.focusTrain--
                    }
                    override fun flingRight() {
                        trainEdit.focusTrain++
                    }
                })
        moveButton.setOnTouchListener { v, event -> moveButtonG.onTouchEvent(event)}
    }

}