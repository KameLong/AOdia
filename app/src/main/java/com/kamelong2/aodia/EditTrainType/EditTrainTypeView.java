package com.kamelong2.aodia.EditTrainType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.kamelong2.OuDia.TrainType;
import com.kamelong.aodia.R;
import com.kamelong2.aodia.SDlog;
import com.kamelong2.tool.Color;


import top.defaults.colorpicker.ColorPickerPopup;

public class EditTrainTypeView extends LinearLayout{
    public EditTrainTypeView(final Context context, final TrainType trainType) {
        super(context);
        try {
            LayoutInflater.from(context).inflate(R.layout.old_edit_traintype_view, this);
            final EditText name=findViewById(R.id.nameEdit);
            name.setText(trainType.name);
            name.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    trainType.name=name.getEditableText().toString();
                }
            });
            final EditText shortName=findViewById(R.id.shortNameEdit);
            shortName.setText(trainType.shortName);
            shortName.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    trainType.shortName=shortName.getEditableText().toString();
                }
            });

            final Button textColor=findViewById(R.id.textColor);
            textColor.setBackgroundColor(trainType.textColor.getAndroidColor());
            textColor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new ColorPickerPopup.Builder(getContext())
                            .initialColor(trainType.textColor.getAndroidColor()) // Set initial color
                            .enableBrightness(true) // Enable brightness slider or not
                                .enableAlpha(false) // Enable alpha slider or not
                                .okTitle("PICK")
                            .cancelTitle("Cancel")
                            .showIndicator(true)
                            .showValue(true)
                            .build()
                            .show(textColor, new ColorPickerPopup.ColorPickerObserver() {
                                @Override
                                public void onColorPicked(int color) {
                                    textColor.setBackgroundColor(color);
                                    trainType.textColor=new Color(color);

                                }

                            });



                    }catch (Exception e){
                        SDlog.log(e);
                        SDlog.toast("色選択時にエラーが発生しました");

                    }
                }
            });
            final Button diaColor=findViewById(R.id.diaColor);
            diaColor.setBackgroundColor(trainType.diaColor.getAndroidColor());
            diaColor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new ColorPickerPopup.Builder(getContext())
                            .initialColor(trainType.diaColor.getAndroidColor()) // Set initial color
                            .enableBrightness(true) // Enable brightness slider or not
                                .enableAlpha(false) // Enable alpha slider or not
                                .okTitle("PICK")
                            .cancelTitle("Cancel")
                            .showIndicator(true)
                            .showValue(true)
                            .build()
                            .show(diaColor, new ColorPickerPopup.ColorPickerObserver() {
                                @Override
                                public void onColorPicked(int color) {
                                    diaColor.setBackgroundColor(color);
                                    trainType.diaColor=new Color(color);
                                }

                            });
                    }catch (Exception e){
                        SDlog.log(e);
                        SDlog.toast("色選択時にエラーが発生しました");
                    }
                }
            });
            RadioGroup diaStyle=findViewById(R.id.diaStyleRadio);
            switch (trainType.lineStyle){
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
            diaStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i){
                        case R.id.radioButton:
                            trainType.lineStyle=0;
                            break;
                        case R.id.radioButton2:
                            trainType.lineStyle=1;
                            break;
                        case R.id.radioButton4:
                            trainType.lineStyle=2;
                            break;
                        case R.id.radioButton3:
                            trainType.lineStyle=3;
                            break;

                    }
                }
            });
            ((CheckBox)findViewById(R.id.checkBox2)).setChecked(trainType.bold);
            ((CheckBox)findViewById(R.id.checkBox2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    trainType.bold=b;
                }
            });
            ((CheckBox)findViewById(R.id.checkBox3)).setChecked(trainType.stopmark);
            ((CheckBox)findViewById(R.id.checkBox3)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    trainType.stopmark=b;
                }
            });


        }catch (Exception e){
            SDlog.log(e);
        }
    }
}
