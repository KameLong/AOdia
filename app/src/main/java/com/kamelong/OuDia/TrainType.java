package com.kamelong.OuDia;

import com.kamelong.tool.SDlog;
import com.kamelong.tool.Color;

import java.io.PrintWriter;

public class TrainType implements Cloneable{
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


    /**
     * ダイヤ線色
     */

    public Color diaColor=new Color();
    /**
     列車線(直線)の線の形状属性。
     */
    public int lineStyle=0;
    public static final int LINESTYLE_NORMAL=0;
    public static final int LINESTYLE_DASH=1;
    public static final int LINESTYLE_DOT=2;
    public static final int LINESTYLE_CHAIN=3;
    /**
     * ダイヤ線が太線かどうか
     */
    public boolean bold=false;
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
    public void saveToFile(PrintWriter out) throws Exception {
        out.println("Ressyasyubetsu.");
        out.println("Syubetsumei="+name);
        out.println("Ryakusyou="+shortName);
        out.println("JikokuhyouMojiColor="+textColor.getOudiaString());
        out.println("JikokuhyouFontIndex="+fontIndex);
        out.println("JikokuhyouBackColor="+timeTableBackColor.getOudiaString());
        out.println("DiagramSenColor="+diaColor.getOudiaString());
        switch (lineStyle){
            case 0:
                out.println("DiagramSenStyle=SenStyle_Jissen");
                break;
            case 1:
                out.println("DiagramSenStyle=SenStyle_Hasen");
                break;
            case 2:
                out.println("DiagramSenStyle=SenStyle_Tensen");
                break;
            case 3:
                out.println("DiagramSenStyle=SenStyle_Ittensasen");
                break;
        }
        if (bold){
            out.println("DiagramSenIsBold=1");
        }
        if(stopmark){
            out.println("StopMarkDrawType=EStopMarkDrawType_DrawOnStop");
        }
        if(parentIndex>=0){
            out.println("ParentSyubetsuIndex="+parentIndex);

        }
        out.println(".");
    }
    public void saveToOuDiaFile(PrintWriter out) throws Exception {
        out.println("Ressyasyubetsu.");
        out.println("Syubetsumei="+name);
        out.println("Ryakusyou="+shortName);
        out.println("JikokuhyouMojiColor="+textColor.getOudiaString());
        out.println("JikokuhyouFontIndex="+fontIndex);
        out.println("DiagramSenColor="+diaColor.getOudiaString());
        switch (lineStyle){
            case 0:
                out.println("DiagramSenStyle=SenStyle_Jissen");
                break;
            case 1:
                out.println("DiagramSenStyle=SenStyle_Hasen");
                break;
            case 2:
                out.println("DiagramSenStyle=SenStyle_Tensen");
                break;
            case 3:
                out.println("DiagramSenStyle=SenStyle_Ittensasen");
                break;
        }
        if (bold){
            out.println("DiagramSenIsBold=1");
        }
        if(stopmark){
            out.println("StopMarkDrawType=EStopMarkDrawType_DrawOnStop");
        }
        out.println(".");
    }
    public TrainType clone(){
        try {
            TrainType result = (TrainType) super.clone();
            result.timeTableBackColor = this.timeTableBackColor.clone();
            result.diaColor = this.diaColor.clone();
            result.textColor = this.textColor.clone();
            return result;
        }catch (CloneNotSupportedException e){
            SDlog.log(e);
            return new TrainType();
        }

    }

}
