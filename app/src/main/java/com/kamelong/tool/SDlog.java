package com.kamelong.tool;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

import android.app.Activity;
import android.widget.Toast;

/**
 * Logを出力させるためのクラス
 */

public class SDlog {
    private static boolean able=false;
    private static Activity activity;


    public static void setActivity(Activity a){
        activity=a;
    }
    public static void toast(String string){
        if(activity!=null){
            Toast.makeText(activity, string,Toast.LENGTH_SHORT).show();
        }
    }
/*

    private static PrintWriter getLogFile(){

        //現在日時を取得する
        Calendar c = Calendar.getInstance();
        //フォーマットパターンを指定して表示する
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fileName="/log"+sdf.format(c.getTime())+".txt";
        sdf = new SimpleDateFormat("hh:mm:ss");
        String time=sdf.format(c.getTime());

        File file=new File(Environment.getExternalStorageDirectory().getPath()+"/Android/data/com.kamelong.aodia"+fileName);
        try {
            PrintWriter pw;
            try {
                pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            }catch(Exception e){
                file.createNewFile();
                pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            }
            pw.println("TimeStamp"+time);
            return pw;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
         */

    public static void log(Exception e){
        e.printStackTrace();
        /*
        try {
            Toast.makeText(activity, "エラーが発生しました。\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception w){
        }
        if(!able){
            return;
        }
        try {
            PrintWriter pw=getLogFile();
            e.printStackTrace(pw);
            pw.main_close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        */
    }
    public static void log(Object value) {
        System.out.println(value);
//        Log.d("null",value.toString());
//        if(!able){
//            return;
//        }
//
//        try {
//            PrintWriter pw = getLogFile();
//            pw.println(value);
//            for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
//                pw.println(stack);
//            }
//            pw.main_close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    public static void log(Object value1,Object value2) {
        System.out.println(""+value1+","+value2);
//        Log.d(""+value1,""+value2);
//        if(!able){
//            return;
//        }
//
//        try {
//            PrintWriter pw = getLogFile();
//            pw.println(value1+":"+value2);
//            for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
//                pw.println(stack);
//            }
//            pw.main_close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
