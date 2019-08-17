package com.kamelong.aodia.EditStation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.Station;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.util.ArrayList;

public class EditStationView extends LinearLayout {
    StationEditInterface stationEditInterface=null;
    public int stationIndex=0;
    public EditStationView(final Context context, final Station station, int sIndex,final ArrayList<Station>editStation){
        super(context);
        this.stationIndex=sIndex;
        try{
            LayoutInflater.from(context).inflate(R.layout.edit_station_view, this);
            ((TextView)findViewById(R.id.stationIndex)).setText(stationIndex+"");
            findViewById(R.id.expandButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.expandButton).setVisibility(GONE);
                    findViewById(R.id.closeButton).setVisibility(VISIBLE);
                    ((LinearLayout)findViewById(R.id.stationLinear)).addView(new EditStationInfoView(context,station,editStation));
                }
            });
            findViewById(R.id.closeButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    findViewById(R.id.expandButton).setVisibility(VISIBLE);
                    findViewById(R.id.closeButton).setVisibility(GONE);
                    ((LinearLayout)findViewById(R.id.stationLinear)).removeViewAt(1);
                }
            });
            findViewById(R.id.addButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(stationEditInterface!=null){
                        stationEditInterface.addNewStation(stationIndex+1);
                    }
                }
            });
            findViewById(R.id.deleteButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(stationEditInterface!=null){
                        stationEditInterface.removeStation(stationIndex);
                    }

                }
            });
            final EditText staitonNameEdit=(EditText)findViewById(R.id.stationName);
            staitonNameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(staitonNameEdit.getEditableText().toString().length()==0){
                        staitonNameEdit.setText(station.name);
                        return;
                    }
                    station.name=staitonNameEdit.getEditableText().toString();
                }
            });
            staitonNameEdit.setText(station.name);


        }catch (Exception e){
            SDlog.log(e);
        }
    }
    public void setStationEditInterface(StationEditInterface listener){
        stationEditInterface=listener;
    }
    public void renewStationIndex(int stationIndex){
        this.stationIndex=stationIndex;
        ((TextView)findViewById(R.id.stationIndex)).setText(stationIndex+"");
    }
    public void closeInfo(){
        if( ((LinearLayout)findViewById(R.id.stationLinear)).getChildCount()==2) {
            findViewById(R.id.expandButton).setVisibility(VISIBLE);
            findViewById(R.id.closeButton).setVisibility(GONE);
            ((LinearLayout) findViewById(R.id.stationLinear)).removeViewAt(1);
        }

    }


}
