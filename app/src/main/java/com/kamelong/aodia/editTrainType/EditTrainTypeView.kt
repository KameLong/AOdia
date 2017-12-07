package com.kamelong.aodia.editTrainType

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaTrainType
import com.azeesoft.lib.colorpicker.ColorPickerDialog
import com.kamelong.tool.Color


class EditTrainTypeView(context: Context,trainType:AOdiaTrainType): FrameLayout(context) {
    val layout = LayoutInflater.from(context).inflate(R.layout.edit_traintype_view, this)

    init {
        layout.findViewById<EditText>(R.id.nameEdit).setText(trainType.name)
        layout.findViewById<EditText>(R.id.nameEdit).addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                if(edit.toString().isNotEmpty()){
                    trainType.name=edit.toString()
                }
            }

        })
        layout.findViewById<EditText>(R.id.shortNameEdit).setText(trainType.shortName)
        layout.findViewById<EditText>(R.id.shortNameEdit).addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(edit: Editable?) {
                if(edit.toString().isNotEmpty()){
                    trainType.shortName=edit.toString()
                }
            }

        })
        layout.findViewById<Button>(R.id.textColor).setBackgroundColor(trainType.textColor.androidColor)
        layout.findViewById<Button>(R.id.textColor).setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog(context)
            colorPickerDialog.setLastColor(trainType.textColor.androidColor)
            colorPickerDialog.setOnColorPickedListener { color, hexVal ->
                trainType.textColor=Color(color)
                layout.findViewById<Button>(R.id.textColor).setBackgroundColor(trainType.textColor.androidColor)
            }
            colorPickerDialog.show()
        }
        layout.findViewById<Button>(R.id.diaColor).setBackgroundColor(trainType.diaColor.androidColor)
        layout.findViewById<Button>(R.id.diaColor).setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog(context)
            colorPickerDialog.setLastColor(trainType.diaColor.androidColor)
            colorPickerDialog.setOnColorPickedListener { color, hexVal ->
                trainType.diaColor= Color(color)
                layout.findViewById<Button>(R.id.diaColor).setBackgroundColor(trainType.diaColor.androidColor)
            }
            colorPickerDialog.show()
        }
        layout.findViewById<RadioGroup>(R.id.diaStyleRadio).check(
                when(trainType.lineStyle){
                    0->R.id.radioButton
                    1->R.id.radioButton2
                    2->R.id.radioButton4
                    3->R.id.radioButton3
                    else->R.id.radioButton
                }
        )
        layout.findViewById<RadioGroup>(R.id.diaStyleRadio).setOnCheckedChangeListener { radioGroup, index ->
            trainType.lineStyle=when(index){
                0->0
                1->1
                2->2
                3->3
                else->0
            }
        }
        layout.findViewById<CheckBox>(R.id.checkBox2).isChecked=trainType.lineBold
        layout.findViewById<CheckBox>(R.id.checkBox2).setOnCheckedChangeListener { compoundButton, boolean -> trainType.lineBold=boolean }
        layout.findViewById<CheckBox>(R.id.checkBox3).isChecked=trainType.showStop
        layout.findViewById<CheckBox>(R.id.checkBox3).setOnCheckedChangeListener { compoundButton, boolean -> trainType.showStop=boolean }







    }
}