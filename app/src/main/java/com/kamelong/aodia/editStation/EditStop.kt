package com.kamelong.aodia.editStation

import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.kamelong.aodia.R

/**
 */
class EditStop(val stationEditor: EditStation, index:Int) : FrameLayout(stationEditor.context), CopyPasteInsertAddDeleteDialog.CopyPasteInsertAddDeleteInterface {
    val index=index
    override fun onClickCopyButton() {
    }

    override fun onClickPasteButton() {
    }

    override fun onClickInsertButton() {
        stationEditor.addStop(index)
    }

    override fun onClickAddButton() {
        stationEditor.addStop(index+1)
    }

    override fun onClickDeleteButton() {
        stationEditor.deleteStop(index)
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
        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                stationEditor.setStopSpinnerValue(index,pos)
            }
           override fun onNothingSelected(parent: AdapterView<out Adapter>?) {
            }
        }

        this.setOnLongClickListener {
            val cpiadDialog=com.kamelong.aodia.editStation.CopyPasteInsertAddDeleteDialog(context,this,false,false)
            cpiadDialog.show()
            false
        }
    }

    fun removeSpinnerValue(value:Int){
        if(spinner.selectedItemPosition==value){
            spinner.setSelection(2)
        }
    }

}