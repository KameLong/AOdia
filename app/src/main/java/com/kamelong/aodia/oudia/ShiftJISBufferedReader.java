package com.kamelong.aodia.oudia;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by kame on 2017/05/15.
 */

public class ShiftJISBufferedReader extends BufferedReader {
    public ShiftJISBufferedReader(@NonNull Reader in) {
        super(in);
    }
    @Override
    public String readLine()throws IOException{
        String str=super.readLine();
        if(str==null||!str.contains("\\"))return str;
        String[] dameMoji={"\\","―","ソ","Ы","Ⅸ","噂","浬","欺","圭","構.","蚕","十","申","曾","箪","貼","能","表","暴","予","禄","兔","喀","媾","彌","拿","杤","歃","濬","畚","秉","綵","臀","藹","觸","軆","鐔","饅","鷭","偆","砡","纊","犾"};
        for (String moji: dameMoji){
            str=str.replace(moji+"\\",moji);
        }
        return str;

    }
}
