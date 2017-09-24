package com.kamelong.aodia.menu;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.SdLog;

/**
 * Created by kame on 2017/01/24.
 */
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

/**
 * メニューの各路線内の「上り時刻表」「下り時刻表」「ダイヤグラム」に使われるButton
 * 毎回setOnClickListenerをするのがめんどくさいからfileNumber,diaNumber,fragmetnNumberを使って先に定義してしまった
 */
public class MenuButton extends Button {
    public int fileNumber;
    public int diaNumber;
    public int fragmentNumber;
    public MenuButton(final Context context){
        super(context);
        try {
            this.setBackgroundColor(Color.TRANSPARENT);
            //this.setTextColor(Color.rgb(0,51,204));
            setGravity(Gravity.LEFT);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity) context).setFragment(fileNumber,diaNumber, fragmentNumber);
                }
            });
        }catch(Exception e){
            SdLog.log(e);

        }
    }

}
