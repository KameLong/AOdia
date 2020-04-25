package com.kamelong.aodia.EditTrain

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView.OnEditorActionListener
import com.kamelong.OuDia.Train
import com.kamelong.aodia.R
import com.kamelong2.aodia.TimeTable.EditTrain.OnTimeChangeListener

//時刻入力のViewグループ
class EditTimeView : LinearLayout {
    //Viewを閉じたときのイベントリスナー
    private var onCloseEditTimeViewListener: OnCloseEditTimeViewListener? = null
    //時刻を変更するイベントリスナー
    private var timeChangedListener:OnTimeChangeListener?=null

    //時刻変更する列車
    private lateinit var train: Train

    //時刻変更する駅のindex
    private var stationIndex = 0

    //変更するのは到着or発車
    private var AD = Train.DEPART

    constructor(context: Context?) : super(context, null) {}

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.trainedit_edit_time, this)
        (findViewById<View>(R.id.editTime) as EditText).setOnEditorActionListener(
                OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        submitTime()
                        return@OnEditorActionListener true
                    }
                    return@OnEditorActionListener false
                })
        //閉じるボタンが押されたとき
        findViewById<View>(R.id.closeButton).setOnClickListener { submitTime() }
        //時刻編集
        findViewById<View>(R.id.plus10).setOnClickListener { changeTime(train.lineFile.secondShift[0]) }
        findViewById<View>(R.id.plus15).setOnClickListener { changeTime(train.lineFile.secondShift[1]) }
        findViewById<View>(R.id.plus60).setOnClickListener { changeTime(60) }
        findViewById<View>(R.id.plus300).setOnClickListener { changeTime(300) }
        findViewById<View>(R.id.mines10).setOnClickListener { changeTime(-train.lineFile.secondShift[0]) }
        findViewById<View>(R.id.mines15).setOnClickListener { changeTime(-train.lineFile.secondShift[1]) }
        findViewById<View>(R.id.mines60).setOnClickListener { changeTime(-60) }
        findViewById<View>(R.id.mines300).setOnClickListener { changeTime(-300) }
        findViewById<View>(R.id.delete).setOnClickListener {
            train.setTime(stationIndex, AD, -1)
            this@EditTimeView.visibility = View.GONE
            timeChangedListener?.onTimeChanged(stationIndex,AD)
        }
    }

    /**
     * EditTimeViewで編集する時刻を入力します
     */
    fun setValues(train: Train, stationIndex: Int, AD: Int) {
        (findViewById<View>(R.id.plus10) as Button).text = "+" + train.lineFile.secondShift[0] + "秒"
        (findViewById<View>(R.id.plus15) as Button).text = "+" + train.lineFile.secondShift[1] + "秒"
        (findViewById<View>(R.id.mines10) as Button).text = "-" + train.lineFile.secondShift[0] + "秒"
        (findViewById<View>(R.id.mines15) as Button).text = "-" + train.lineFile.secondShift[1] + "秒"
        this.train = train
        this.stationIndex = stationIndex
        this.AD = AD
        if (!train.timeExist(stationIndex, AD)) {
            var time = -1
            if (train.direction == 0) {
                for (i in stationIndex downTo 0) {
                    if (train.timeExist(i, 0)) {
                        time = train.getDepTime(i)
                        break
                    }
                    if (train.timeExist(i, 1)) {
                        time = train.getAriTime(i)
                        break
                    }
                }
                if (time < 0) {
                    for (i in stationIndex until train.stationNum) {
                        if (train.timeExist(i, 1)) {
                            time = train.getAriTime(i)
                            break
                        }
                        if (train.timeExist(i, 0)) {
                            time = train.getDepTime(i)
                            break
                        }
                    }
                }
            } else {
                for (i in stationIndex until train.stationNum) {
                    if (train.timeExist(i, 0)) {
                        time = train.getDepTime(i)
                        break
                    }
                    if (train.timeExist(i, 1)) {
                        time = train.getAriTime(i)
                        break
                    }
                }
                if (time < 0) {
                    for (i in stationIndex downTo 0) {
                        if (train.timeExist(i, 1)) {
                            time = train.getAriTime(i)
                            break
                        }
                        if (train.timeExist(i, 0)) {
                            time = train.getDepTime(i)
                            break
                        }
                    }
                }
            }
            if (time >= 0) {
                train.setTime(stationIndex, AD, time)
                when (train.getStopType(stationIndex)) {
                    0, 3 -> train.setStopType(stationIndex, 1)
                }
                timeChangedListener?.onTimeChanged(stationIndex,AD)
            }
        }
        (findViewById<View>(R.id.editTime) as EditText).setText(timeInt2String(train.getTime(stationIndex, AD)))
    }

    private fun timeInt2String(time: Int): String {
        var time = time
        if (time < 0) return ""
        val ss = time % 60
        time = time / 60
        val mm = time % 60
        time = time / 60
        val hh = time % 24
        return hh.toString() + String.format("%02d", mm) + String.format("%02d", ss)
    }

    /**
     * 指定秒時刻を移動させます
     * @param change
     */
    private fun changeTime(change: Int) {
        if (train.timeExist(stationIndex, AD)) {
            train.setTime(stationIndex, AD, train.getTime(stationIndex, AD) + change)
        }
        if (AD == 1 && (findViewById<View>(R.id.after) as CheckBox).isChecked&&train.timeExist(stationIndex,0)) {
            train.setTime(stationIndex, 0, train.getTime(stationIndex, 0) + change)
        }
        if (AD == 0 && (findViewById<View>(R.id.before) as CheckBox).isChecked&&train.timeExist(stationIndex,1)) {
            train.setTime(stationIndex, 1, train.getTime(stationIndex, 1) + change)
        }
        if (train.direction == 0 && (findViewById<View>(R.id.before) as CheckBox).isChecked || train.direction == 1 && (findViewById<View>(R.id.after) as CheckBox).isChecked) {
            for (i in 0 until stationIndex) {
                if (train.timeExist(i, 0)) {
                    train.setDepTime(i, train.getDepTime(i) + change)
                }
                if (train.timeExist(i, 1)) {
                    train.setAriTime(i, train.getAriTime(i) + change)
                }
            }
        }
        if (train.direction == 0 && (findViewById<View>(R.id.after) as CheckBox).isChecked || train.direction == 1 && (findViewById<View>(R.id.before) as CheckBox).isChecked) {
            for (i in stationIndex + 1 until train.stationNum) {
                if (train.timeExist(i, 0)) {
                    train.setDepTime(i, train.getDepTime(i) + change)
                }
                if (train.timeExist(i, 1)) {
                    train.setAriTime(i, train.getAriTime(i) + change)
                }
            }
        }
        (findViewById<View>(R.id.editTime) as EditText).setText(timeInt2String(train.getTime(stationIndex, AD)))
        timeChangedListener?.onTimeChanged(stationIndex,AD)
    }

    protected fun timeString2Int(time: String): Int {
        var hh = 0
        var mm = 0
        var ss = 0
        when (time.length) {
            3 -> {
                hh = time.substring(0, 1).toInt()
                mm = time.substring(1, 3).toInt()
                return hh * 3600 + mm * 60
            }
            4 -> {
                hh = time.substring(0, 2).toInt()
                mm = time.substring(2, 4).toInt()
                return hh * 3600 + mm * 60
            }
            5 -> {
                hh = time.substring(0, 1).toInt()
                mm = time.substring(1, 3).toInt()
                ss = time.substring(3, 5).toInt()
                return hh * 3600 + mm * 60 + ss
            }
            6 -> {
                hh = time.substring(0, 2).toInt()
                mm = time.substring(2, 4).toInt()
                ss = time.substring(4, 6).toInt()
                return hh * 3600 + mm * 60 + ss
            }
        }
        return -1
    }

    fun setOnTimeChangedLister(listener: OnTimeChangeListener) {
        timeChangedListener = listener
    }
    fun setOnCloseEditTimeViewListener(listener: OnCloseEditTimeViewListener) {
        onCloseEditTimeViewListener = listener
    }

    /**
     * 入力時刻を決定する
     */
    private fun submitTime() {
        val time = timeString2Int((findViewById<View>(R.id.editTime) as EditText).editableText.toString())
        train.setTime(stationIndex, AD, time)
        timeChangedListener?.onTimeChanged(stationIndex,AD)
        this.visibility = View.GONE
        onCloseEditTimeViewListener?.onClosed()
    }

}