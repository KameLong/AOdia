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

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SDlog;
import com.kamelong.tool.Color;

public class EditTrainTypeView extends LinearLayout{
    public EditTrainTypeView(final Context context, final TrainType trainType) {
        super(context);
        try {
            LayoutInflater.from(context).inflate(R.layout.edit_traintype_view, this);
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
                    ColorPickerDialog dialog=ColorPickerDialog.createColorPickerDialog(context);
                    dialog.setLastColor(trainType.textColor.getAndroidColor());
                    dialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                        @Override
                        public void onColorPicked(int color, String hexVal) {
                            trainType.textColor=new Color(color);
                            textColor.setBackgroundColor(trainType.textColor.getAndroidColor());
                        }
                    });
                    dialog.show();
                }
            });
            final Button diaColor=findViewById(R.id.diaColor);
            diaColor.setBackgroundColor(trainType.diaColor.getAndroidColor());
            diaColor.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorPickerDialog dialog=ColorPickerDialog.createColorPickerDialog(context);
                    dialog.setLastColor(trainType.diaColor.getAndroidColor());
                    dialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                        @Override
                        public void onColorPicked(int color, String hexVal) {
                            trainType.diaColor=new Color(color);
                            diaColor.setBackgroundColor(trainType.diaColor.getAndroidColor());
                        }
                    });
                    dialog.show();
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
