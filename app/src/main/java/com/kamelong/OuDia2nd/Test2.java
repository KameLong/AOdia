package com.kamelong.OuDia2nd;

import java.util.ArrayList;

/**
 * Created by kame on 2017/12/16.
 */

public class Test2 {
    public void splitTest(){
        ArrayList<String> strList=new  ArrayList<String>();
        for(int i=0; i<1000000;i++){
            StringBuffer str=new StringBuffer();
            for(int j=0;j<10;j++) {
                str.append((char)(Math.random() * 80 + 35));
            }
            strList.add(str.toString());
//            println(str)
        }
        ArrayList<String> str2=new ArrayList<String>();
        System.out.println("start");
        long time=System.currentTimeMillis();
        for(int i=0;i< 1000000;i++){
            str2.add( strList.get(i).split("a")[0]);
        }
        System.out.println("end"+(System.currentTimeMillis()-time));
        System.out.println(str2.get((int)(Math.random()*10000)));
    }
}
