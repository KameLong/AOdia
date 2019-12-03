package com.kamelong.OuDia;

import com.kamelong.tool.SDlog;
import com.kamelong.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleOuDia {
    public String name="";
    public ArrayList<String>stationName=new ArrayList<>();

    public SimpleOuDia(File file)throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String version="";
        try {
            version = br.readLine().split("=", -1)[1];
        }catch (NullPointerException e){
            br.close();
            return;
        }
        try {
            double v = 1.02;
            try {
                v = Double.parseDouble(version.substring(version.indexOf(".") + 1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (version.startsWith("OuDia.") || v < 1.03) {
                loadShiftJis(file);
            } else {
                loadDiaFile(br);
            }
        }catch(Exception e){
            SDlog.log(e);
        }finally {
            br.close();
        }

    }
    private void loadShiftJis(File file)throws Exception{
        BufferedReader br = new ShiftJISBufferedReader(new InputStreamReader(new FileInputStream(file),"Shift-JIS"));
        try {
            String nouse = br.readLine();
            loadDiaFile(br);//version info
        }catch (Exception e){
            throw e;
        }finally {
            br.close();
        }
    }
    private void loadDiaFile(BufferedReader br)throws Exception{
        String line="";
        br.readLine();//Rosen.
        name=br.readLine().split("=",-1)[1];
        line=br.readLine();
        while (line != null) {
            if (line.equals("Eki.")) {
                line = br.readLine();
                stationName.add(line.split("=", -1)[1]);
            }
            if (line.equals("Ressyasyubetsu.")) {
                br.close();
                return;
            }
            line = br.readLine();
        }

    }

}
