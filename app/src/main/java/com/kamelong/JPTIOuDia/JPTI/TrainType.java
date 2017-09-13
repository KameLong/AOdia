package com.kamelong.JPTIOuDia.JPTI;

import com.kamelong.JPTI.JPTIdata;
import com.kamelong.JPTIOuDia.OuDia.OuDiaTrainType;
import com.kamelong.tool.Color;

import org.json.JSONObject;


public class TrainType extends com.kamelong.JPTI.TrainType {
    public TrainType(JPTIdata jpti, com.kamelong.JPTI.Route route) {
        super(jpti, route);
    }


    public TrainType(JPTIdata jpti, Route route, JSONObject json) {
        super(jpti, route, json);
    }
    public TrainType(JPTI jpti,Route route,OuDiaTrainType trainType){
        this(jpti,route);
        name=trainType.getName();
        shortName=trainType.getShortName();
        textColor=trainType.getTextColor();
        diaColor=trainType.getDiaColor();

        diaStyle=trainType.getLineStyle();
        diaBold=trainType.getLineBold();
        showStop=trainType.getShowStop();
        font=trainType.fontNumber;
    }
    public String getName(){
        return name;
    }
    public String getShortName(){
        return shortName;
    }
    public Color getTextColor(){
        return textColor;
    }
    public Color getDiaColor(){
        return diaColor;
    }
    public int getDiaStyle(){
        return diaStyle;
    }
    public boolean getShowStop(){
        return showStop;
    }
    public boolean getDiaBold(){
        return diaBold;
    }
    public int getFontNumber(){
        return font;
    }

}
