package com.kamelong.aodia.menu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.OuDia.Diagram;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

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
 * １つのダイヤファイルについてメニューを構築する。
 * 全てのボタンは１つのLinearLayout内に含まれる
 */
public class LineMenu  extends LinearLayout{
    private LinearLayout lineButtonLinear;
    private LinearLayout lineContLinear;

    private int fileNum;
    private int menuIndex;
//    private View container;
    public LineMenu(final Context context, final DiaFile diaFile, int index, int mMenuIndex){
        this(context);
        fileNum=index;
        menuIndex=mMenuIndex;
        try {
            setOrientation(VERTICAL);
            lineButtonLinear = new LinearLayout(context);
            lineContLinear=new LinearLayout(context);
            lineContLinear.setOrientation(VERTICAL);
            View v= ((Activity)getContext()).getLayoutInflater().inflate(R.layout.menu_line_buttons, lineButtonLinear);
            addView(lineButtonLinear);
            findViewById(R.id.saveButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity)getContext()).openSaveDialog(menuIndex);
                }
            });
            findViewById(R.id.closeButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity)getContext()).killDiaFile(fileNum,menuIndex);
                }
            });
            findViewById(R.id.upButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity)getContext()).upDiaFile(menuIndex);
                }
            });
            if(menuIndex==0){
                findViewById(R.id.upButton).setVisibility(INVISIBLE);
            }
            findViewById(R.id.hidden).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    diaFile.menuOpen=false;
                    closeMenu();
                }
            });
            findViewById(R.id.expand).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                   diaFile.menuOpen=true;
                    openMenu();

                }
            });
            TextView titleButton = (TextView)v.findViewById(R.id.TitleView);
            titleButton.setText(diaFile.name);
            titleButton.setBackgroundColor(Color.TRANSPARENT);
            titleButton.setGravity(Gravity.LEFT);
            titleButton.setGravity(Gravity.CENTER_VERTICAL);
            titleButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditLineDialog dialog=new EditLineDialog((AOdiaActivity)context,diaFile);
                    dialog.show();
                    ((AOdiaActivity) context).closeMenu();

                }
            });
            Button station = new Button(context);
            station.setText("　駅編集");
            station.setGravity(Gravity.LEFT);
            station.setBackgroundColor(Color.TRANSPARENT);
            station.setTextColor(Color.BLACK);
            station.setClickable(true);
            station.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity) context).openEditStationFragment(fileNum);
                }
            });
            lineContLinear.addView(station);
            Button trainType = new Button(context);
            trainType.setText("　種別編集");
            trainType.setGravity(Gravity.LEFT);
            trainType.setBackgroundColor(Color.TRANSPARENT);
            trainType.setTextColor(Color.BLACK);
            trainType.setClickable(true);
            trainType.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity) context).openEditTrainTypeFragment(fileNum);
                }
            });
            lineContLinear.addView(trainType);
            Button timetable = new Button(context);
            timetable.setText("　駅時刻表一覧");
            timetable.setGravity(Gravity.LEFT);
            timetable.setBackgroundColor(Color.TRANSPARENT);
            timetable.setTextColor(Color.BLACK);
            timetable.setClickable(true);
            timetable.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity) context).openStationTimeTableIndex(fileNum);
                }
            });
            lineContLinear.addView(timetable);


            for (int i = 0; i < diaFile.getDiaNum(); i++) {
                Button diaTitle = new Button(context);
                Diagram diagram=diaFile.diagram.get(i);
                diaTitle.setText("　"+diagram.name);
                diaTitle.setGravity(Gravity.LEFT);
                diaTitle.setGravity(Gravity.CENTER_VERTICAL);

                diaTitle.setBackgroundColor(Color.TRANSPARENT);
                diaTitle.setTextColor(Color.GRAY);
                final int j=i;
                diaTitle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DiagramEditDialog dialog=new DiagramEditDialog((AOdiaActivity)context,diaFile,j);
                        dialog.show();
                        ((AOdiaActivity) context).closeMenu();
                    }
                });
                MenuButton downButton = new MenuButton(context);
                downButton.setText("　　下り時刻表");
                downButton.fileNumber=fileNum;
                downButton.diaNumber = i;
                downButton.fragmentNumber = 0;
                MenuButton upButton = new MenuButton(context);
                upButton.setText("　　上り時刻表");
                upButton.fileNumber=fileNum;
                upButton.diaNumber = i;
                upButton.fragmentNumber = 1;

                MenuButton diagramButton = new MenuButton(context);
                diagramButton.setText("　　ダイヤグラム");
                diagramButton.fileNumber=fileNum;
                diagramButton.diaNumber = i;
                diagramButton.fragmentNumber = 2;


                MenuButton operationButton = new MenuButton(context);
                operationButton.setText("　　運用");
                operationButton.fileNumber=fileNum;
                operationButton.diaNumber = i;
                operationButton.fragmentNumber = 3;


                lineContLinear.addView(diaTitle);
                lineContLinear.addView(downButton);
                lineContLinear.addView(upButton);
                lineContLinear.addView(diagramButton);
               // lineContLinear.addView(operationButton);

            }
            Button comment = new Button(context);
            comment.setText("　コメント");
            comment.setGravity(Gravity.LEFT);
            comment.setBackgroundColor(Color.TRANSPARENT);
            comment.setTextColor(Color.BLACK);
            comment.setClickable(true);
            comment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AOdiaActivity) context).openCommentFragment(fileNum);
                }
            });
            lineContLinear.addView(comment);
            addView(lineContLinear);
            if(diaFile.menuOpen){
                openMenu();
            }else{
                closeMenu();
            }
        }catch(Exception e){
            SDlog.log(e);
        }
    }
    public LineMenu(Context context){
        super(context);
    }
    private void closeMenu(){
        findViewById(R.id.hidden).setVisibility(GONE);
        findViewById(R.id.expand).setVisibility(VISIBLE);
        lineContLinear.setVisibility(GONE);

    }
    private void openMenu(){
        findViewById(R.id.hidden).setVisibility(VISIBLE);
        findViewById(R.id.expand).setVisibility(GONE);
        lineContLinear.setVisibility(VISIBLE);


    }

}
