package com.kamelong.ColorPicker;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kamelong.aodia.R;
import com.kamelong.tool.Color;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerPopup;
import top.defaults.colorpicker.ColorPickerView;

public class ColorPicker extends LinearLayout {
    public ColorPickerView colorPickerView;
    public SeekBar rSeek;
    public SeekBar gSeek;
    public SeekBar bSeek;
    public EditText editR;
    public EditText editG;
    public EditText editB;
    public Color color;

    public ColorPickerLisetener lisetener=null;
    public void setColorPickerListener(ColorPickerLisetener listener){
        this.lisetener=listener;
    }
    public ColorPicker(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View layout = LayoutInflater.from(context).inflate(R.layout.color_picker, this);
        colorPickerView=findViewById(R.id.view4);
        rSeek=findViewById(R.id.Rseek);
        rSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color.setRed(progress);
                onColorChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        gSeek=findViewById(R.id.Gseek);
        gSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color.setGreen(progress);
                onColorChanged();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        bSeek=findViewById(R.id.Bseek);
        bSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                color.setBlue(progress);
                onColorChanged();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        editR=findViewById(R.id.editR);
        editR.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int r=getColorNumber(editR.getText().toString());
                    color.setRed(r);
                    onColorChanged();

            }
        });
        editG=findViewById(R.id.editG);

        editG.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int r=getColorNumber(editG.getText().toString());
                    color.setGreen(r);
                    onColorChanged();

            }
        });
        editB=findViewById(R.id.editB);

        editB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int r=getColorNumber(editB.getText().toString());
                    color.setBlue(r);
                    onColorChanged();

            }
        });
        colorPickerView.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                ColorPicker.this.color=new Color(color);
                onColorChanged();

            }
        });
    }
    public Color getColor(){
        return  new Color(colorPickerView.getColor());
    }
    public void onColorChanged(){
        if(lisetener!=null){
            lisetener.colorChanged(color);
        }
        if(rSeek.getProgress()!=color.getRed()) {
            rSeek.setProgress(color.getRed());
        }
        if(gSeek.getProgress()!=color.getGreen()) {
            gSeek.setProgress(color.getGreen());
        }
        if(bSeek.getProgress()!=color.getBlue()) {
            bSeek.setProgress(color.getBlue());
        }
        if(colorPickerView.getColor()!=color.getAndroidColor()){
            colorPickerView.setInitialColor(color.getAndroidColor());
        }
        if(getColorNumber(editR.getText().toString())!=color.getRed()){
            editR.setText(String.valueOf(color.getRed()));
        }
        if(getColorNumber(editG.getText().toString())!=color.getGreen()){
            editG.setText(String.valueOf(color.getGreen()));
        }
        if(getColorNumber(editB.getText().toString())!=color.getBlue()){
            editB.setText(String.valueOf(color.getBlue()));
        }
    }
    public int getColorNumber(String s){
        if(s.length()==0){
            return 0;
        }
        int result=Integer.parseInt(s);
        if(result<0){
            return 0;
        }
        if(result>=256){
            return 255;
        }
        return result;
    }
}
