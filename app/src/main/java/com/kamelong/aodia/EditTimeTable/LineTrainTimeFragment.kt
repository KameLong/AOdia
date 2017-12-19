package com.kamelong.aodia.EditTimeTable

import android.app.Fragment
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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
import com.kamelong.aodia.R.id.textView
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ToggleButton
import com.kamelong.OuDia.OuDiaTrain
import com.kamelong.OuDia2nd.DiaFile
import com.kamelong.OuDia2nd.Train
import com.kamelong.aodia.R.id.button0
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.editStation.CopyPasteInsertAddDeleteDialog
import com.kamelong.aodia.timeTable.TrainGroup
import java.util.*


/**
 * 時刻表を編集するためのFrgment
 */
class LineTrainTimeFragment : Fragment(),AOdiaFragmentInterface{
    val trainEdit=TrainEdit(this)

    override var fragment=this as Fragment
    override lateinit var diaFile: AOdiaDiaFile
    lateinit var prefer:SharedPreferences
    override var aodiaActivity: AOdiaActivity
        get() = activity as AOdiaActivity
        set(value){}

    var fileIndex=0
    var diaIndex=0
    var direction=0
    lateinit var fragmentContainer:ViewGroup
    lateinit var stationView: StationViewGroup
    lateinit var trainLinear:LinearLayout

    val trainNum:Int
    get()=diaFile.getTrainNum(diaIndex,direction)

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
        prefer= activity.getSharedPreferences("AOdia-LineTrainTime",MODE_PRIVATE)

        val gesture = GestureDetector(aodiaActivity,
                object : GestureDetector.SimpleOnGestureListener() {

                    private val flingV = 0f
                    var fling=false
                    var flingEnd=false

                    override fun onDown(motionEvent: MotionEvent): Boolean {
                        if(fling){
                            fling=false
                            flingEnd=true
                        }else{
                            flingEnd=false
                        }
                        return true
                    }

                    override fun onSingleTapUp(event: MotionEvent): Boolean {
                        if(flingEnd){
                            flingEnd=false
                            return true
                        }
                        val x = event.x.toInt()
                        val y =event.y.toInt()
                        val timeTablex = x + trainLinear.getScrollX() - stationView.getWidth()
                        if(trainLinear.childCount==0)return false
                        val trainIndex=timeTablex/trainLinear.getChildAt(0).width
                        if(trainIndex<0||trainIndex>=trainLinear.childCount)return false
                        val stationIndex=stationView.getStationIndex(y)
                        if(stationIndex<0){
//                            val dialog=CopyPasteInsertAddDeleteDialog(activity,object :CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface{}).show()

                            val trainInfoInterface=object : TrainInfoDialog.EditTrainInfoInterface {
                                override fun oldTrain(train: AOdiaTrain) {
                                    trainEdit.trainBackUpStack.add(TrainHistory(-1,-1,trainIndex,train))
                                }

                                override fun submitError() {
                                    trainEdit.back()
                                }

                                override fun submit() {
                                    trainEdit.invalidate()
                                }
                            }
                            val dialog=TrainInfoDialog(activity,getTrain(trainIndex),trainInfoInterface).show()
                            trainEdit.focusTrain=trainIndex

                            return true
                        }
                        fragmentContainer.findViewById<LinearLayout>(R.id.edit1).visibility=View.VISIBLE
                        fragmentContainer.findViewById<LinearLayout>(R.id.edit2).visibility=View.VISIBLE
                        (timeTableLayout.layoutParams as MarginLayoutParams).setMargins(0,0,0,getResources().getDimensionPixelSize(R.dimen.margin200))
                        trainEdit.focusTrain=trainIndex
                        trainEdit.focusPoint=stationIndex
                        return false
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
                        (timeTableLayout.layoutParams as MarginLayoutParams).setMargins(0,0,0,getResources().getDimensionPixelSize(R.dimen.margin200))
                        trainEdit.focusTrain=trainIndex
                        trainEdit.focusPoint=stationIndex
                        return false
                    }

                    override fun onLongPress(event: MotionEvent) {
                        println("longpress")

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
                        if(fragmentContainer.findViewById<LinearLayout>(R.id.edit1).visibility==View.VISIBLE){
                            val x = event.x.toInt()
                            val y =event.y.toInt()
                            val timeTablex = x + trainLinear.getScrollX() - stationView.getWidth()
                            if(trainLinear.childCount==0)return
                            val trainIndex=timeTablex/trainLinear.getChildAt(0).width
                            println(trainIndex)

                            if(trainIndex<0||trainIndex>=trainLinear.childCount)return
                            trainEdit.addSelectTrain(trainIndex)

                        }

                    }

                    override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, vx: Float, vy: Float): Boolean {
                        var scrollX=trainLinear.scrollX+vx
                        if(scrollX>trainLinear.getChildAt(0).width*trainLinear.childCount-trainLinear.width+100){
                            scrollX=trainLinear.getChildAt(0).width*trainLinear.childCount-trainLinear.width+100f
                        }
                        if(scrollX<0){
                            scrollX=0f
                        }
                        trainLinear.scrollTo(scrollX.toInt(),0)
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
                                    var scrollX=trainLinear.scrollX+(flingV * 16 / 1000f)
                                    if(scrollX>trainLinear.getChildAt(0).width*trainLinear.childCount-trainLinear.width+100){
                                        scrollX=trainLinear.getChildAt(0).width*trainLinear.childCount-trainLinear.width+100f
                                    }
                                    if(scrollX<0){
                                        scrollX=0f
                                    }
                                    trainLinear.scrollTo(scrollX.toInt(),0)

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
            trainEdit.copyTrain= ArrayList()
            trainEdit.selectedTrain=ArrayList()
            (timeTableLayout.layoutParams as MarginLayoutParams).setMargins(0,0,0,0)
        }
        val moveButton=fragmentContainer.findViewById<Button>(R.id.buttonB)
        val moveButtonG=GestureDetector(aodiaActivity,
                object :ButtonGestureDetectorInterface() {
                    override fun flingDown() {
                        trainEdit.moveDown()
                    }
                    override fun flingUp() {
                        trainEdit.moveUp()
                    }
                    override fun flingLeft() {
                        trainEdit.moveLeft()
                    }
                    override fun flingRight() {
                        trainEdit.moveRight()
                    }
                })
        moveButton.setOnTouchListener { v, event -> moveButtonG.onTouchEvent(event)}
        fragmentContainer.findViewById<Button>(R.id.button0).setOnClickListener {
            editStationTime(0)
        }
        fragmentContainer.findViewById<Button>(R.id.button1).setOnClickListener {
            editStationTime(1)
        }
        fragmentContainer.findViewById<Button>(R.id.button2).setOnClickListener {
            editStationTime(2)
        }
        fragmentContainer.findViewById<Button>(R.id.button3).setOnClickListener {
            editStationTime(3)
        }
        fragmentContainer.findViewById<Button>(R.id.button4).setOnClickListener {
            editStationTime(4)
        }
        fragmentContainer.findViewById<Button>(R.id.button5).setOnClickListener {
            editStationTime(5)
        }
        fragmentContainer.findViewById<Button>(R.id.button6).setOnClickListener {
            editStationTime(6)
        }
        fragmentContainer.findViewById<Button>(R.id.button7).setOnClickListener {
            editStationTime(7)
        }
        fragmentContainer.findViewById<Button>(R.id.button8).setOnClickListener {
            editStationTime(8)
        }
        fragmentContainer.findViewById<Button>(R.id.button9).setOnClickListener {
            editStationTime(9)
        }
        fragmentContainer.findViewById<Button>(R.id.button10).setOnClickListener {
            trainEdit.setStop(2)
            moveNext()
        }
        fragmentContainer.findViewById<Button>(R.id.button11).setOnClickListener {
            trainEdit.setStop(3)
            moveNext()
        }
        fragmentContainer.findViewById<Button>(R.id.buttonE).setOnClickListener {
            trainEdit.setStop(0)
            moveNext()
        }
        fragmentContainer.findViewById<Button>(R.id.buttonEnter).setOnClickListener {
            moveNext()
        }
        val fastButtonG=GestureDetector(aodiaActivity, object :ButtonGestureDetectorInterface() {
            override fun onDown(motionEvent: MotionEvent): Boolean {trainEdit.fast(60);return true}
            override fun flingDown() { trainEdit.moveDown() }
            override fun flingUp() { trainEdit.moveUp() }
            override fun flingLeft() { trainEdit.moveLeft() }
            override fun flingRight() { trainEdit.moveRight() }
        })
        fragmentContainer.findViewById<Button>(R.id.buttonA).setOnTouchListener { v, event -> fastButtonG.onTouchEvent(event)}
        val slowButtonG=GestureDetector(aodiaActivity, object :ButtonGestureDetectorInterface() {
            override fun onDown(motionEvent: MotionEvent): Boolean {trainEdit.slow(60);return true}
            override fun flingDown() { trainEdit.moveDown() }
            override fun flingUp() { trainEdit.moveUp() }
            override fun flingLeft() { trainEdit.moveLeft() }
            override fun flingRight() { trainEdit.moveRight() }
        })
        fragmentContainer.findViewById<Button>(R.id.buttonC).setOnTouchListener { v, event -> slowButtonG.onTouchEvent(event)}

        val trainControlButtonG=GestureDetector(aodiaActivity, object :ButtonGestureDetectorInterface() {
            override fun flingDown() { trainEdit.endThisStation();trainEdit.invalidate() }
            override fun flingUp() { trainEdit.startThisStation();trainEdit.invalidate() }
            override fun flingLeft() {trainEdit.connectTrain();trainEdit.invalidate() }
            override fun flingRight() {trainEdit.splitTrain()  }
        })
        fragmentContainer.findViewById<Button>(R.id.buttonD).setOnTouchListener { v, event -> trainControlButtonG.onTouchEvent(event)}

        fragmentContainer.findViewById<Button>(R.id.buttonF).setOnClickListener{
            trainEdit.editStationTime?.setStop(1)
            moveNext()
        }
        fragmentContainer.findViewById<Button>(R.id.buttonC5).setOnClickListener{
            trainEdit.editStationTime?.addNumber(-1)
            moveNext()
        }
        fragmentContainer.findViewById<Button>(R.id.buttonC6).setOnClickListener{
            trainEdit.forward()
        }
        fragmentContainer.findViewById<Button>(R.id.buttonC2).setOnClickListener{
            trainEdit.back()
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton).setOnCheckedChangeListener {
            compoundButton, b ->
            prefer.edit().putBoolean("editAllStop",b).commit()
            trainEdit.editAllStop=b
            for(i in 0 until trainLinear.childCount){
                (trainLinear.getChildAt(i) as TrainViewGroup).reNewPreference()
            }
            stationView.reNewPreference()
        }
        if(prefer.getBoolean("editAllStop",false)){
            fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton).isChecked=true
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton6).setOnCheckedChangeListener {
            compoundButton, b ->
            prefer.edit().putBoolean("editAllTime",b).commit()
            trainEdit.editAllTime=b
            for(i in 0 until trainLinear.childCount){
                (trainLinear.getChildAt(i) as TrainViewGroup).reNewPreference()
            }
            stationView.reNewPreference()
        }
        if(prefer.getBoolean("editAllTime",false)){
            fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton6).isChecked=true
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton5).setOnCheckedChangeListener {
            compoundButton, b ->

            trainEdit.movingUpFrag=b
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton4).setOnCheckedChangeListener {
            compoundButton, b ->
            trainEdit.movingDownFrag=b
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton3).setOnCheckedChangeListener {
            compoundButton, b ->
            trainEdit.fastInput=b
        }
        fragmentContainer.findViewById<ToggleButton>(R.id.toggleButton2).setOnCheckedChangeListener {
            compoundButton, b ->
            trainEdit.cursolDirectIsRight=b
        }
        fragmentContainer.findViewById<Button>(R.id.buttonB2).setOnClickListener {
            val trainEditListener=object:EditTrainDialog.EditTrainDialogInterface{
                override fun copy(pasteMoveTime:Int) {
                    trainEdit.pasteMoveTime=pasteMoveTime
                    trainEdit.pasteNum=0
                    trainEdit.selectedTrain.sort()
                    for(i in trainEdit.selectedTrain){
                        trainEdit.copyTrain.add(getTrain(i))
                    }

                    trainEdit.clearSelectedTrain()
                }
                override fun paste() {
                    println(trainEdit.pasteMoveTime*trainEdit.pasteNum)
                    val trainList=ArrayList<AOdiaTrain>()
                    trainEdit.pasteNum++
                    for(i in trainEdit.copyTrain){
                        val train=i.clone(true)
                        for(j in 0 until diaFile.stationNum){
                            if(train.existArriveTime(j)){
                                train.setArrivalTime(j,(train.getArrivalTime(j)+trainEdit.pasteMoveTime*trainEdit.pasteNum+86400)%86400)
                            }
                            if(train.existDepartTime(j)){
                                train.setDepartureTime(j,(train.getDepartureTime(j)+trainEdit.pasteMoveTime*trainEdit.pasteNum+86400)%86400)
                            }
                        }
                        trainList.add(train)
                    }
                    for(train in trainList){
                       trainEdit.insertTrain(train)
                    }
                }

                override fun add() {
                    val train=diaFile.getNewTrain(direction)
                    trainEdit.addTrain(train)
                }

                override fun insert() {
                    val train=diaFile.getNewTrain(direction)
                    trainEdit.insertTrain(train)
                }

                override fun delete() {
                    val trainList=ArrayList<AOdiaTrain>()
                    for(i in trainEdit.selectedTrain){
                        trainList.add(getTrain(i))
                    }
                    for(train in trainList){
                        val index=trainEdit.deleteTrain(train)
                        if(index>=0)trainLinear.removeViewAt(index)
                    }
                    trainEdit.clearSelectedTrain()

                }

            }
            EditTrainDialog(activity,trainEditListener,true,true).show()
        }




    }

    fun editStationTime(value:Int){
        trainEdit.stationTimeEdit(value)
    }
    fun getTrain(index:Int):AOdiaTrain{
        return diaFile.getTrain(diaIndex,direction,index)
    }
    fun moveNext(){
        if(true){
            trainEdit.moveDown()
        }
    }

}