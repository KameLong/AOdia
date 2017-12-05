package com.kamelong.aodia.editStation

import android.view.LayoutInflater
import android.widget.*
import com.kamelong.aodia.R
import com.kamelong.aodia.SdLog
import org.w3c.dom.Text

/**
 */
class EditStop(stationEditor: StationEditor,index:Int) : FrameLayout(stationEditor.context), CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface {
    override fun onClickCopyButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickPasteButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickInsertButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickAddButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickDeleteButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val layout= LayoutInflater.from(stationEditor.context).inflate(R.layout.edit_station_stop_view, this)
    lateinit var spinner:Spinner
    init{
        layout.findViewById<EditText>(R.id.textStopName).setText(stationEditor.station.getStopName(index))
        layout.findViewById<EditText>(R.id.textStopShort).setText(stationEditor.station.getShortName(index))

      spinner =layout.findViewById<Spinner>(R.id.mainSropSpnner)
        val spinnerList=ArrayList<String>()
        spinnerList.add("下り主本線")
        spinnerList.add("上り主本線")
        spinnerList.add("副本線/側線")
        val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,spinnerList )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(spinnerAdapter)

        spinner.setSelection(
                if(stationEditor.station.downMain==index){
                    0
                }else if(stationEditor.station.upMain==index){
                    1
                }else{
                    2
                }
        )
        spinner.setOnItemClickListener { adapterView, view, i, l -> stationEditor.setStopSpinnerValue(index,i) }
        this.setOnLongClickListener {
            SdLog.toast("longtap")
            false
        }
    }

    fun removeSpinnerValue(value:Int){
        if(spinner.selectedItemPosition==value){
            spinner.setSelection(2)
        }
    }

}