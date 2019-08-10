package com.kamelong.OuDia;

import com.kamelong.aodia.SDlog;
import com.kamelong.tool.Color;

import java.io.BufferedReader;
import java.io.FileWriter;

public class TrainType {
    /**
     種別名。
     規定値は、空文字列。
     */
    public String name="";
    /**
     略称（種別名の略称）。
     規定値は、空文字列。
     */
    public String shortName="";
    /**
     時刻表文字色(ダイヤグラムの列車情報の文字色を兼ねます)
     規定値は、黒。
     */
    public Color textColor=new Color();
    /**
     時刻表ビューで、この列車種別の時刻を表示するための時刻表フォント。
     範囲は、 0 以上、 JIKOKUHYOUFONT_COUNT 未満です。

     - 0：『時刻表ビュー 1』
     - 1: 『時刻表ビュー 2』
     - 2: 『時刻表ビュー 3』
     */
    public int fontIndex=0;
    /**
     時刻表背景色、ダイヤのプロパティにおいて、背景色パターンが種別色の場合に参照されます。

     規定値は、白。
     */
    public Color timeTableBackColor=new Color();



    public Color diaColor=new Color();
    public boolean bold=false;
    public boolean ityly=false;
    /**
     列車線(直線)の線の形状属性。
     */
    public int lineStyle=0;
    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;
    /**
     列車種別毎の、停車駅明示の方法。
     false:停車駅明示=明示しない
     true:デフォルト。ダイヤグラムビューで停車駅明示がONの場合は、短時間停車駅に○を描画します。
     */
    public boolean stopmark=true;

    /**
     *　親種別index
     * -1の時は親種別が存在しません
     */
    public int parentIndex=-1;

    public TrainType(){
        name="新規種別";
    };
    public void setValue(String title,String value){
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
            case "JikokuhyouFontIndex":
                fontIndex=Integer.parseInt(value);
            case "JikokuhyouBackColor":
                timeTableBackColor.setOuDiaColor(value);
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
            case "DiagramSenIsBold":
                bold=value.equals("1");
                break;
            case "StopMarkDrawType":
                stopmark=value.equals("EStopMarkDrawType_DrawOnStop");
                break;
            case "ParentSyubetsuIndex":
                parentIndex=Integer.parseInt(value);
                break;
        }

    }
    public void saveToFile(FileWriter out) throws Exception {
            out.write("Ressyasyubetsu.\r\n");
            out.write("Syubetsumei="+name+"\r\n");
            out.write("Ryakusyou="+shortName+"\r\n");
            out.write("JikokuhyouMojiColor="+textColor.getOudiaString()+"\r\n");
            out.write("JikokuhyouFontIndex="+fontIndex+"\r\n");
            out.write("JikokuhyouBackColor="+timeTableBackColor.getOudiaString()+"\r\n");
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
                out.write("DiagramSenIsBold=1\r\n");
            }
            if(stopmark){
                out.write("StopMarkDrawType=EStopMarkDrawType_DrawOnStop\r\n");
            }
            if(parentIndex>=0){
                out.write("ParentSyubetsuIndex="+parentIndex+"\r\n");

            }
            out.write(".\r\n");
    }

}
