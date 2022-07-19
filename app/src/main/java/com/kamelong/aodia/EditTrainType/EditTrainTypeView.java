package com.kamelong.aodia.EditTrainType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.kamelong.ColorPicker.ColorPickerDialog;
import com.kamelong.ColorPicker.ColorPickerListener;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.R;
import com.kamelong.tool.Color;
import com.kamelong.tool.SDlog;

public class EditTrainTypeView extends LinearLayout {
    public boolean checked = false;

    public EditTrainTypeView(final Context context, final TrainType trainType) {
        super(context);
        try {
            LayoutInflater.from(context).inflate(R.layout.traintype_edit_view, this);
            final CheckBox checkBox = findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checked = isChecked);
            final EditText name = findViewById(R.id.nameEdit);
            name.setText(trainType.name);
            name.setOnFocusChangeListener((view, b) -> trainType.name = name.getEditableText().toString());
            final EditText shortName = findViewById(R.id.shortNameEdit);
            shortName.setText(trainType.shortName);
            shortName.setOnFocusChangeListener((view, b) -> trainType.shortName = shortName.getEditableText().toString());

            final Button textColor = findViewById(R.id.textColor);
            textColor.setBackgroundColor(trainType.textColor.getAndroidColor());
            textColor.setOnClickListener(view -> {
                try {
                    new ColorPickerDialog(context, color -> {
                        textColor.setBackgroundColor(color.getAndroidColor());
                        trainType.textColor = color;
                    },trainType.textColor).show();
                } catch (Exception e) {
                    SDlog.log(e);
                    SDlog.toast("色選択時にエラーが発生しました");

                }
            });
            final Button diaColor = findViewById(R.id.diaColor);
            diaColor.setBackgroundColor(trainType.diaColor.getAndroidColor());
            diaColor.setOnClickListener(view -> {
                try {

                    new ColorPickerDialog(context, color -> {
                        diaColor.setBackgroundColor(color.getAndroidColor());
                        trainType.diaColor = color;
                    },trainType.diaColor).show();


                } catch (Exception e) {
                    SDlog.log(e);
                    SDlog.toast("色選択時にエラーが発生しました");
                }
            });
            RadioGroup diaStyle = findViewById(R.id.diaStyleRadio);
            switch (trainType.lineStyle) {
                case 0:
                    diaStyle.check(R.id.radioButton);
                    break;
                case 1:
                    diaStyle.check(R.id.radioButton2);
                    break;
                case 2:
                    diaStyle.check(R.id.radioButton4);
                    break;
                case 3:
                    diaStyle.check(R.id.radioButton3);
                    break;
            }
            diaStyle.setOnCheckedChangeListener((radioGroup, i) -> {
                switch (i) {
                    case R.id.radioButton:
                        trainType.lineStyle = 0;
                        break;
                    case R.id.radioButton2:
                        trainType.lineStyle = 1;
                        break;
                    case R.id.radioButton4:
                        trainType.lineStyle = 2;
                        break;
                    case R.id.radioButton3:
                        trainType.lineStyle = 3;
                        break;

                }
            });
            ((CheckBox) findViewById(R.id.checkBox2)).setChecked(trainType.bold);
            ((CheckBox) findViewById(R.id.checkBox2)).setOnCheckedChangeListener((compoundButton, b) -> trainType.bold = b);
            ((CheckBox) findViewById(R.id.checkBox3)).setChecked(trainType.stopmark);
            ((CheckBox) findViewById(R.id.checkBox3)).setOnCheckedChangeListener((compoundButton, b) -> trainType.stopmark = b);


        } catch (Exception e) {
            SDlog.log(e);
        }
    }
}
