package com.kamelong.OuDia;

import com.kamelong.tool.Color;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;

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
    public TrainType(){
        name="新規種別";
    };
    public TrainType(BufferedReader br)throws Exception{
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
                    switch (value){
                        case"SenStyle_Jissen":
                            lineStyle=0;
                            break;
                        case "SenStyle_Hasen":
                            lineStyle=1;
                            break;
                        case "SenStyle_Tensen":
                            lineStyle=2;
                            break;
                        case "SenStyle_Ittensasen":
                            lineStyle=3;
                            break;
                    }
                    break;
                case "StopMarkDrawType":
                    stopmark=value.equals("EStopMarkDrawType_DrawOnStop");
                    break;
                case "DiagramSenIsBold":
                    bold=value.equals("1");

            }
            line=br.readLine();
        }
    }
    public void saveToFile(FileWriter out) throws Exception {
            out.write("Ressyasyubetsu.\r\n");
            out.write("Syubetsumei="+name+"\r\n");
            out.write("Ryakusyou="+shortName+"\r\n");
            out.write("JikokuhyouMojiColor="+textColor.getOudiaString()+"\r\n");
            out.write("DiagramSenColor="+diaColor.getOudiaString()+"\r\n");
            switch (lineStyle){
                case 0:
                    out.write("DiagramSenStyle=SenStyle_Jissen\r\n");
                    break;
                case 1:
                    out.write("DiagramSenStyle=SenStyle_Hasen\r\n");
                    break;
                case 2:
                    out.write("DiagramSenStyle=SenStyle_Tensen\r\n");
                    break;
                case 3:
                    out.write("DiagramSenStyle=SenStyle_Ittensasen\r\n");
                    break;
            }
            if (bold){
                out.write("DiagramSenIsBold=1");
            }
            if(stopmark){
                out.write("StopMarkDrawType=EStopMarkDrawType_DrawOnStop\r\n");
            }
            out.write(".\r\n");
    }

}
