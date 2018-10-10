package com.kamelong.OuDia;

import com.kamelong.tool.Color;

import java.io.BufferedReader;

public class TrainType {
    public String name="";
    public String shortName="";
    public Color textColor=new Color();
    public Color diaColor=new Color();
    public boolean bold=false;
    public boolean ityly=false;
    public boolean stopmark=false;
    public int lineStyle=0;
    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;
    public TrainType(BufferedReader br){
        try{
            String line=br.readLine();
            while (!line.equals(".")) {
                String title=line.split("=",-1)[0];
                String value=line.split("=",-1)[1];
                switch (title){
                    case "Syubetsumei":
                        name=value;
                        break;
                    case "Ryakusyou":
                        shortName=value;
                        break;
                    case  "JikokuhyouMojiColor":
                        textColor.setOuDiaColor(value);
                        break;
                    case "JikokuhyouBackColor":
                        break;
                    case "DiagramSenColor":
                        diaColor.setOuDiaColor(value);
                        break;
                    case "DiagramSenStyle":
                        break;
                    case "StopMarkDrawType":
                        stopmark=value.equals("EStopMarkDrawType_DrawOnStop");
                        break;





                }
                line=br.readLine();
            }


            }catch(Exception e){
            e.printStackTrace();
        }
    }

}
