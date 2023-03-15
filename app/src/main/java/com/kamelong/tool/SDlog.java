package com.kamelong.tool;

/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.kamelong.OuDia.LineFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Logを出力させるためのクラス
 */

public class SDlog {
    private static boolean able = false;
    public static Activity activity;
    private static Handler handler;


    public static void setActivity(Activity a) {
        activity = a;
        handler=new Handler();
    }

    public static void toast(String string) {
        if (activity != null) {
            handler.post(() -> Toast.makeText(activity, string, Toast.LENGTH_SHORT).show());
        }
    }
    public static String getNowDate(){
        final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }
    public static void log(Exception e) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
            if(pref.getString("userID","").length()==0){
                pref.edit().putString("userID",UUID.randomUUID().toString()).apply();
            }
            if(pref.getBoolean("send_log",false)) {
                if(pref.getString("userID","").length()==0){
                    pref.edit().putString("userID",UUID.randomUUID().toString()).apply();
                }
                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
               final String logName=activity.getCacheDir()+"/"+packageInfo.versionName+"_"+pref.getString("userID","")+"_"+getNowDate()+".log";

                if (e instanceof LineFileException) {
                    new Thread(() -> {
                        try {
                    LineFile file=((LineFileException)e).errorFile;
                    PrintWriter pw = new PrintWriter(logName+".oud");
                    file.saveToOuDiaFile(pw);
                    Send(logName+".oud");
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
                    }).start();

                }

                new Thread(() -> {
                    try {
                        PrintWriter pw = new PrintWriter(logName);
                        pw.println(packageInfo.versionName);
                        e.printStackTrace(pw);
                        pw.close();
                        Send(logName);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }

                }).start();
            }

        } catch (Exception e2) {
            e.printStackTrace();
        }
        e.printStackTrace();
    }

    public static void log(Object value) {
        System.out.println(value);
    }

    public static void log(Object value1, Object value2) {
        System.out.println("" + value1 + "," + value2);
    }


    public static int Send(String filename){
        try {
            HttpURLConnection con;
            OutputStream op;
            StringBuilder sb = new StringBuilder();
            String bnd = "abcdrghijklmnopqrstuvwxyzabcdefghijklmn";
            int wLength;
            //対象ファイル
            FileInputStream inps = new FileInputStream(filename);

            //送信先
            URL url = new URL("http://www.kamelong.com/aodia/Log/postLog.php");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + bnd);

            sb.append("--");
            sb.append(bnd);
            sb.append("\r\n");

            op = con.getOutputStream();
            op.write(sb.toString().getBytes());

            op.write("Content-Disposition: form-data;".getBytes());
            op.write("name=\"upfile\";".getBytes());
            op.write(("filename=\""+filename+"\"\r\n").getBytes());
            op.write("Content-Type:text/html\r\n".getBytes());
            op.write("\r\n".getBytes());

            byte[] byteData = new byte[128];
            while ((wLength = inps.read(byteData)) != -1) {
                op.write(byteData, 0, wLength);
            }
            op.write("\r\n".getBytes());

            sb.setLength(0);
            sb.append("--");
            sb.append(bnd);
            sb.append("--");

            op.write(sb.toString().getBytes());

            inps.close();
            op.close();
            con.connect();

            InputStream inputStream = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);
            int resChar;

            sb.setLength(0);
            while ((resChar = isr.read()) != -1) {
                sb.append((char) resChar);
            }


            inputStream.close();
            con.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}