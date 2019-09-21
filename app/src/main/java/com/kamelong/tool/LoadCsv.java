package com.kamelong.tool;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * Csvファイルを読み込んで必要な情報を出力させるクラス
 */
public class LoadCsv {
    ArrayList<String[]> data=new ArrayList<>();
    Map<String,Integer> headerMap=new HashMap<>();
    public LoadCsv(InputStream inputStream){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String[] header = br.readLine().split(",",-1);
            if(header[0].getBytes()[0]==(byte)0xEF&&header[0].getBytes()[1]==(byte)0xBB&&header[0].getBytes()[2]==(byte)0xBF){
                StringBuilder s=new StringBuilder();
                for(int i=3;i<header[0].getBytes().length;i++){
                    s.append((char)header[0].getBytes()[i]);
                }
                header[0]=s.toString();
            }
            for(int i=0;i<header.length;i++){
                headerMap.put(header[i],i);
            }
            String str=br.readLine();
            while(str!=null){
                data.add(str.split(",",-1));
                str=br.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public String getData(String key,int index){
        try {
            if (index < data.size() && headerMap.containsKey(key)) {
                return data.get(index)[headerMap.get(key)];
            }
        }catch (Exception e){
        }
        return "";
    }
    public int dataNum(){
        return data.size();
    }
}
