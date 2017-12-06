package com.kamelong.aodia.editTrainType

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import com.kamelong.aodia.R
import com.kamelong.aodia.diadata.AOdiaTrainType
import com.azeesoft.lib.colorpicker.ColorPickerDialog



class EditTrainTypeView(context: Context,trainType:AOdiaTrainType): FrameLayout(context) {
    val layout = LayoutInflater.from(context).inflate(R.layout.edit_traintype_view, this)

    init {
        layout.findViewById<EditText>(R.id.nameEdit).setText(trainType.name)
        layout.findViewById<EditText>(R.id.shortNameEdit).setText(trainType.shortName)
        layout.findViewById<Button>(R.id.textColor).setBackgroundColor(trainType.textColor.androidColor)
        layout.findViewById<Button>(R.id.textColor).setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.createColorPickerDialog(context)
            colorPickerDialog.show()
        }
        layout.findViewById<Button>(R.id.diaColor).setBackgroundColor(trainType.diaColor.androidColor)



    }
}