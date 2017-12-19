package com.kamelong.aodia.EditTimeTable

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.kamelong.OuDia2nd.Station
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog
import com.kamelong.aodia.diadata.AOdiaTrain
import com.kamelong.aodia.editStation.CopyPasteInsertAddDeleteDialog


class TrainInfoDialog(context: Context,val train:AOdiaTrain,val listener:EditTrainInfoInterface) : AlertDialog(context) {
    val view = LayoutInflater.from(context).inflate(R.layout.edit_train_name, null)

    init {
        setView(view)
        val trainTypeString=ArrayList<String>()
        for(i in 0 until train.diaFile.trainTypeNum){
            trainTypeString.add(train.diaFile.getTrainType(i).name)
        }
        val trainTypeAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, trainTypeString)
        trainTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        view.findViewById<Spinner>(R.id.trainType).setAdapter(trainTypeAdapter)
        view.findViewById<Spinner>(R.id.trainType).setSelection(train.type)



        view.findViewById<EditText>(R.id.number).setText(train.number)
        view.findViewById<EditText>(R.id.name).setText(train.name)
        view.findViewById<EditText>(R.id.count).setText(train.count)
        view.findViewById<EditText>(R.id.remark).setText(train.remark)
        view.findViewById<RadioGroup>(R.id.startStation).check(
                when(train.startAction){
                    0->R.id.radioButton1
                    1->R.id.radioButton2
                    2->R.id.radioButton3
                    else->R.id.radioButton1
                })
        view.findViewById<RadioGroup>(R.id.startStation).setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.radioButton1->{
                    view.findViewById<LinearLayout>(R.id.startExchange).visibility=GONE
                    view.findViewById<LinearLayout>(R.id.operation).visibility=GONE
                }
                R.id.radioButton2->{
                    view.findViewById<LinearLayout>(R.id.startExchange).visibility= VISIBLE
                    view.findViewById<LinearLayout>(R.id.operation).visibility=GONE


                }
                R.id.radioButton3->{
                    view.findViewById<LinearLayout>(R.id.startExchange).visibility=GONE
                    view.findViewById<LinearLayout>(R.id.operation).visibility= VISIBLE
                }
            }
        }
        when(train.startAction){
            0->{
                view.findViewById<LinearLayout>(R.id.startExchange).visibility=GONE
                view.findViewById<LinearLayout>(R.id.operation).visibility=GONE
            }
            1->{
                view.findViewById<LinearLayout>(R.id.startExchange).visibility= VISIBLE
                view.findViewById<LinearLayout>(R.id.operation).visibility=GONE


            }
            2->{
                view.findViewById<LinearLayout>(R.id.startExchange).visibility=GONE
                view.findViewById<LinearLayout>(R.id.operation).visibility= VISIBLE
            }

        }
        val startExchangeString=ArrayList<String>()
        for(i in 0 until train.diaFile.getStation(train.startStation).stopNum){
            startExchangeString.add(train.diaFile.getStation(train.startStation).getStopName(i))
        }
        val startExchangeAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, startExchangeString)
        startExchangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        view.findViewById<Spinner>(R.id.spinner1).setAdapter(startExchangeAdapter)
        view.findViewById<Spinner>(R.id.spinner1).setSelection(train.startExchangeStop-1)



        view.findViewById<EditText>(R.id.startArrive).setText(time2String(train.startExchangeTimeStart))
        view.findViewById<EditText>(R.id.startDepart).setText(time2String(train.startExchangeTimeEnd))
        view.findViewById<EditText>(R.id.operationName).setText(train.operation)
        view.findViewById<RadioGroup>(R.id.endStation).check(
                when(train.endAction){
                    0->R.id.radioButton4
                    1->R.id.radioButton5
                    2->R.id.radioButton6
                    else->R.id.radioButton4
                })
        view.findViewById<RadioGroup>(R.id.endStation).setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.radioButton4->{
                    view.findViewById<LinearLayout>(R.id.endExchange).visibility=GONE
                }
                R.id.radioButton5->{
                    view.findViewById<LinearLayout>(R.id.endExchange).visibility= VISIBLE
                }
                R.id.radioButton6->{
                    view.findViewById<LinearLayout>(R.id.endExchange).visibility=GONE
                }
            }

        }
        when(train.endAction){
            0->{
                view.findViewById<LinearLayout>(R.id.endExchange).visibility=GONE
            }
            1->{
                view.findViewById<LinearLayout>(R.id.endExchange).visibility= VISIBLE
            }
            2->{
                view.findViewById<LinearLayout>(R.id.endExchange).visibility=GONE
            }

        }
        val endExchangeString=ArrayList<String>()
        for(i in 0 until train.diaFile.getStation(train.endStation).stopNum){
            endExchangeString.add(train.diaFile.getStation(train.endStation).getStopName(i))
        }
        val endExchangeAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, endExchangeString)
        endExchangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        view.findViewById<Spinner>(R.id.spinner2).setAdapter(endExchangeAdapter)
        view.findViewById<Spinner>(R.id.spinner2).setSelection(train.endExchangeStop-1)

        view.findViewById<EditText>(R.id.editText7).setText(time2String(train.endExchangeTimeStart))
        view.findViewById<EditText>(R.id.editText10).setText(time2String(train.endExchangeTimeEnd))

        view.findViewById<Button>(R.id.SubmitButton).setOnClickListener {
            submit()
        }
        view.findViewById<Button>(R.id.CancelButton).setOnClickListener { dismiss() }

    }
    fun time2String(time:Int):String{
        if(time<0)return ""
        val h=time/3600%24
        val m=time/60%60
        val s=time%60
        return h.toString()+String.format("%02d",m)+"-"+String.format("%02d",s)
    }
    fun string2Time(str:String):Int {
        try {
            var value=str
            var result = 0
            if(str[0]=='-'){
                value=str.substring(1)
            }
            val hhmm = value.split('-')[0]
            when (hhmm.length) {
                4 -> {
                    val hh = hhmm.substring(0, 2)
                    val mm = hhmm.substring(2, 4)
                    if (Integer.parseInt(hh) > 24) {
                        return -1
                    }
                    if (Integer.parseInt(mm) >= 60) {
                        return -1
                    }
                    result += 3600 * Integer.parseInt(hh) + 60 * Integer.parseInt(mm)
                }
                3 -> {
                    val hh = hhmm.substring(0, 1)
                    val mm = hhmm.substring(1, 3)
                    if (Integer.parseInt(hh) > 24) {
                        return -1
                    }
                    if (Integer.parseInt(mm) >= 60) {
                        return -1
                    }
                    result += 3600 * Integer.parseInt(hh) + 60 * Integer.parseInt(mm)
                }
                else -> {
                    if (Integer.parseInt(hhmm) > 1440) return -1
                    result += Integer.parseInt(hhmm)
                }
            }
            if (value.contains('-')) {
                val ss = value.split('-')[1]
                if (Integer.parseInt(ss) >= 60) {
                    return -1
                }
                result += Integer.parseInt(ss)
            }
            if(str[0]=='-'){
                result*=-1
            }

            return result
        }catch (e:Exception){
            e.printStackTrace()
            return -1
        }
    }
    fun submit(){
        val oldTrain=train.clone(true)
        listener.oldTrain(oldTrain)
        train.type=view.findViewById<Spinner>(R.id.trainType).selectedItemPosition
        train.number=view.findViewById<EditText>(R.id.number).text.toString()
        train.name=view.findViewById<EditText>(R.id.name).text.toString()
        train.count=("1"+view.findViewById<EditText>(R.id.count).text.toString()).substring(1)
        train.remark=view.findViewById<EditText>(R.id.remark).text.toString()

        train.startAction=
                when(view.findViewById<RadioGroup>(R.id.startStation).checkedRadioButtonId){
                    R.id.radioButton1->0
                    R.id.radioButton2->1
                    R.id.radioButton3->2
                    else->0
                }
        if(train.startAction==1){
            train.startExchangeStop=view.findViewById<Spinner>(R.id.spinner1).selectedItemPosition+1
            val arriveTime=string2Time(view.findViewById<EditText>(R.id.startArrive).text.toString())
            if(arriveTime<0){
                SdLog.toast("始発駅入れ替え発時刻が不適です")
                listener.submitError()
                return
            }
            train.startExchangeTimeStart=arriveTime

            val departTime=string2Time(view.findViewById<EditText>(R.id.startDepart).text.toString())
            if(departTime<0){
                SdLog.toast("始発駅入れ替え着時刻が不適です")
                listener.submitError()
                return
            }
            train.startExchangeTimeEnd=departTime
        }
        if(train.startAction==2){
            train.operation=view.findViewById<EditText>(R.id.operationName).text.toString()
        }

        train.endAction=
                when(view.findViewById<RadioGroup>(R.id.endStation).checkedRadioButtonId){
                    R.id.radioButton4->0
                    R.id.radioButton5->1
                    R.id.radioButton6->2
                    else->0
                }
        if(train.endAction==1){
            train.endExchangeStop=view.findViewById<Spinner>(R.id.spinner2).selectedItemPosition+1
            val arriveTime=string2Time(view.findViewById<EditText>(R.id.editText7).text.toString())
            if(arriveTime<0){
                SdLog.toast("始発駅入れ替え発時刻が不適です")
                listener.submitError()
                return
            }
            train.endExchangeTimeStart=arriveTime

            val departTime=string2Time(view.findViewById<EditText>(R.id.editText10).text.toString())
            if(departTime<0){
                SdLog.toast("始発駅入れ替え着時刻が不適です")
                listener.submitError()
                return
            }
            train.endExchangeTimeEnd=departTime
        }
        listener.submit()
        dismiss()

    }

    interface EditTrainInfoInterface{
        fun oldTrain(train:AOdiaTrain)
        fun submitError()
        fun submit()
    }

}
