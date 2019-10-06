package com.kamelong.aodia.EditStation;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Station;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

public class EditStationView extends LinearLayout {
    public int stationIndex = 0;
    public boolean checked=false;

    public EditStationView(final MainActivity context, final Station station, final LineFile lineFile) {
        super(context);
        stationIndex=lineFile.station.indexOf(station);
        try {
            LayoutInflater.from(context).inflate(R.layout.edit_station_view, this);
            final CheckBox checkBox=findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checked=isChecked;
                }
            });
            ((TextView) findViewById(R.id.stationIndex)).setText(stationIndex + "");
            findViewById(R.id.expandButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.expandButton).setVisibility(GONE);
                    findViewById(R.id.closeButton).setVisibility(VISIBLE);
                    ((LinearLayout) findViewById(R.id.stationLinear)).addView(new EditStationInfoView(context, station, lineFile));
                }
            });
            findViewById(R.id.closeButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.expandButton).setVisibility(VISIBLE);
                    findViewById(R.id.closeButton).setVisibility(GONE);
                    ((LinearLayout) findViewById(R.id.stationLinear)).removeViewAt(1);
                }
            });
            final EditText stationNameEdit = findViewById(R.id.stationName);
            stationNameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (stationNameEdit.getEditableText().toString().length() == 0) {
                        stationNameEdit.setText(station.name);
                        return;
                    }
                    station.name = stationNameEdit.getEditableText().toString();
                }
            });
            stationNameEdit.setText(station.name);


        } catch (Exception e) {
            SDlog.log(e);
        }
    }



}
