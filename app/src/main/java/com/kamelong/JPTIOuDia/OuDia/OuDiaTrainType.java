package com.kamelong.JPTIOuDia.OuDia;


import com.kamelong.JPTIOuDia.JPTI.TrainType;
import com.kamelong.tool.Color;


/**
 * JPTI-OuDia用のOuDiaTrainType
 */
public class OuDiaTrainType extends com.kamelong.OuDia.OuDiaTrainType {
    public OuDiaTrainType(){
        super();
    }
    public OuDiaTrainType(TrainType trainType){
        super();
        name=trainType.getName();
        if(trainType.getShortName()!=null){
            shortName=trainType.getShortName();

        }
        textColor=trainType.getTextColor();
        if(trainType.getDiaColor()!=null){
            diaColor=getDiaColor();
        }else{
            diaColor=new Color(textColor.getAndroidColor());
        }
        lineStyle=trainType.getDiaStyle();
        boldLine=trainType.getDiaBold();
        showStop=trainType.getShowStop();
        fontNumber=trainType.getFontNumber();
        if(fontNumber<0||fontNumber>6){
            fontNumber=0;
        }


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
    public boolean compare(TrainType another){
        return name.equals(another.getName());
    }
}
