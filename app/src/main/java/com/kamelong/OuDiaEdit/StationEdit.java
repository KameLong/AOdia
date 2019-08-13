package com.kamelong.OuDiaEdit;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Station;

public class StationEdit extends Station {
    public StationEdit(DiaFile diaFile) {
        super(diaFile);
    }
    public void setOuterTerminalStationName(int index,String name){
        outerTerminals.get(index).outerTerminalName=name;
    }

}
