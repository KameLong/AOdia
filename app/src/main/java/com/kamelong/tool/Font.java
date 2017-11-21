package com.kamelong.tool;

import com.eclipsesource.json.JsonObject;

import org.json.JSONObject;

public class Font {
    public static final Font OUDIA_DEFAULT=new Font("ＭＳ ゴシック",9,false,false);
    /**
     * フォント高さ
     */
    public int height=-1;
    /**
     * フォント名
     */
    public String name=null;
    /**
     * 太字なら１
     */
    public boolean bold=false;
    /**
     * 斜体なら１
     */
    public boolean itaric=false;

    private static final String HEIGHT="height";
    private static final String NAME="facename";
    private static final String BOLD="bold";
    private static final String ITARIC="itaric";

    public Font(){

    }
    public Font(JSONObject json){
        if(json==null){
            return;
        }
        name=json.optString(NAME);
        height=json.optInt(HEIGHT,-1);
        bold=json.optInt(BOLD)==1;
        itaric=json.optInt(ITARIC)==1;
    }
    public Font(JsonObject json){
        if(json==null){
            return;
        }
        name=json.getString(NAME,null);
        height=json.getInt(HEIGHT,9);
        bold=json.getInt(BOLD,0)==1;
        itaric=json.getInt(ITARIC,0)==1;
    }
    private Font(String name, int height, boolean bold, boolean itaric){
        this.name=name;
        this.height=height;
        this.bold=bold;
        this.itaric=itaric;
    }
    public JSONObject makeJSONObject(){
        JSONObject json=new JSONObject();
        try{
            if(height>-1){
                json.put(HEIGHT,height);
            }
            if(name!=null){
                json.put(NAME,name);
            }
            if(bold){
                json.put(BOLD,1);
            }
            if(itaric){
                json.put(ITARIC,1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }
    public JsonObject makeJsonObject(){
        JsonObject json=new JsonObject();
        try{
            if(height>-1){
                json.add(HEIGHT,height);
            }
            if(name!=null){
                json.add(NAME,name);
            }
            if(bold){
                json.add(BOLD,1);
            }
            if(itaric){
                json.add(ITARIC,1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return json;
    }

    /**
     * フォントをOuDia形式のテキストとして出力する
     * @return
     */
    public StringBuilder font2OudiaFontTxt(){
        StringBuilder result=new StringBuilder();
        result.append("PointTextHeight=").append(height);
        if(name!=null){
            result.append(";Facename=").append(name);
        }else{
            result.append(";Facename=").append("ＭＳ ゴシック");
        }
        if(bold){
            result.append(";Bold=1");
        }
        if(itaric){
            result.append("Itaric=1");
        }
        return result;
    }


}
