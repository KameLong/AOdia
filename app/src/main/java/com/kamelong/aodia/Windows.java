package com.kamelong.aodia;

/**
 * Created by kame on 2017/04/29.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * AOdiaの複数画面の処理などを管轄するクラス
 * 画面設定関係の処理をMainActivityから分離しています
 * @since v1.1.1
 * @author KameLong
 */
class Windows {
    public boolean chooseContainer=false;
    private boolean showMiniTitle=true;
    public boolean showContainer=true;
    private boolean tabletStyle=false;

    private AOdiaActivity activity;

    public Windows(AOdiaActivity mActivity){
        activity=mActivity;
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(activity);
        tabletStyle=spf.getBoolean("fixedMenu",false);


    }

    private View findViewById(int id){
        return activity.findViewById(id);
    }


}
