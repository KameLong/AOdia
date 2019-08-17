package com.kamelong.tool;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/*
 *     This file is part of AOdia.

AOdia is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 When you want to know about GNU, see <http://www.gnu.org/licenses/>.
 */
/*
 * AOdiaはGNUに従う、オープンソースのフリーソフトです。
 * ソースコートの再利用、改変し、公開することは自由ですが、
 * 公開した場合はそのアプリにもGNUライセンスとしてください。
 *
 */
public class SDlog {
    private static boolean able=false;
    public static Activity activity;


    public static void setActivity(Activity a){
        activity=a;
    }
    public static void toast(String string){
        if(activity!=null){
            Toast.makeText(activity, string,Toast.LENGTH_SHORT).show();
        }
    }
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
    public static void log(Exception e){
        e.printStackTrace();
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
            pw.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    public static void log(Object value) {
        Log.d("null",value.toString());
        if(!able){
            return;
        }

        try {
            PrintWriter pw = getLogFile();
            pw.println(value);
            for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
                pw.println(stack);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void log(Object value1,Object value2) {
        Log.d(""+value1,""+value2);
        if(!able){
            return;
        }

        try {
            PrintWriter pw = getLogFile();
            pw.println(value1+":"+value2);
            for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
                pw.println(stack);
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
