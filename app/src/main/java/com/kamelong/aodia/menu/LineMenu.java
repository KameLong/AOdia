package com.kamelong.aodia.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.Diagram;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.DiagramEditDialog;
import com.kamelong.aodia.LineFileEditDialog;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;
import com.kamelong.tool.ShiftJISBufferedReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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
public class LineMenu extends LinearLayout{
    private AOdia aodia;
    private MainActivity activity;
    private LinearLayout lineButtonLinear;
    private LinearLayout lineContLinear;



    public LineMenu(final Context context, final LineFile lineFile){
        super(context);
        this.activity=(MainActivity)context;
        this.aodia=activity.getAOdia();
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

                    Intent uploadIntent = ShareCompat.IntentBuilder.from(activity)
                             .setType("application/oud")
                            .getIntent()
                            .setPackage("com.google.android.apps.docs");
                    uploadIntent.putExtra(Intent.EXTRA_SUBJECT,new File(lineFile.filePath).getName());
                    try{
                        lineFile.saveToOuDiaFile(activity.getCacheDir()+"/temp.oud");
                        ShiftJISBufferedReader buf=new ShiftJISBufferedReader(new File(activity.getCacheDir()+"/temp.oud"));
                        String line = buf.readLine();
                        StringBuilder sb = new StringBuilder();
                        while(line != null){
                            sb.append(line).append("\n");
                            line = buf.readLine();
                        }
                        Uri contentUri = FileProvider.getUriForFile(context, "com.kamelong.aodia", new File(activity.getCacheDir()+"/temp.oud"));
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        shareIntent.setType("text/plain");
                        activity.startActivity(uploadIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    System.out.println("test");

//                    aodia.openSaveFragment(lineFile);
                }
            });
            findViewById(R.id.closeButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.killLineFile(lineFile);
                    activity.openMenu();
                }
            });
            findViewById(R.id.upButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.upDiaFile(lineFile);
                }
            });
            if(aodia.getLineFileList().indexOf(lineFile)==0){
                findViewById(R.id.upButton).setVisibility(INVISIBLE);
            }
            findViewById(R.id.hidden).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.lineFileExpand.put(lineFile,false);
                    closeMenu();
                }
            });
            findViewById(R.id.expand).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.lineFileExpand.put(lineFile,true);
                    openMenu();
                }
            });
            if(aodia.lineFileExpand.get(lineFile)){
                openMenu();
            }else{
                closeMenu();
            }
            TextView titleButton =v.findViewById(R.id.TitleView);
            titleButton.setText(lineFile.name);
            titleButton.setBackgroundColor(Color.TRANSPARENT);
            titleButton.setGravity(Gravity.START);
            titleButton.setGravity(Gravity.CENTER_VERTICAL);
            titleButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LineFileEditDialog dialog=new LineFileEditDialog(context,lineFile);
                    dialog.show();
                    ((MainActivity)getContext()).closeMenu();
                }
            });
            float density=getContext().getResources().getDisplayMetrics().density;

            Button station = new Button(context);
            station.setText(activity.getString(R.string.editStation));
            station.setGravity(Gravity.START);
            station.setBackgroundColor(Color.TRANSPARENT);
            station.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams margin1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            margin1.setMargins((int)(10*density), 0, 0, 0);
            station.setLayoutParams(margin1);
            station.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.openEditStation(lineFile);
                }
            });
            lineContLinear.addView(station);
            Button trainType = new Button(context);
            trainType.setText(activity.getString(R.string.editTrainType));
            trainType.setGravity(Gravity.START);
            trainType.setBackgroundColor(Color.TRANSPARENT);
            trainType.setTextColor(Color.BLACK);
            trainType.setLayoutParams(margin1);

            trainType.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.openEditTrainType(lineFile);
                }
            });
            lineContLinear.addView(trainType);
            Button timetable = new Button(context);
            timetable.setText(activity.getString(R.string.stationTimeTable));
            timetable.setGravity(Gravity.START);
            timetable.setBackgroundColor(Color.TRANSPARENT);
            timetable.setTextColor(Color.BLACK);
            timetable.setLayoutParams(margin1);
            timetable.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.openStationTimeTableIndex(lineFile);
                }
            });
            lineContLinear.addView(timetable);

            LinearLayout.LayoutParams margin2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            margin2.setMargins((int)(20*density), 0, 0, 0);

            for (int i = 0; i <lineFile.getDiagramNum(); i++) {

                final int diagramIndex=i;
                Button diaTitle = new Button(context);
                final Diagram diagram=lineFile.getDiagram(i);
                diaTitle.setText(diagram.name);
                diaTitle.setGravity(Gravity.START);
                diaTitle.setGravity(Gravity.CENTER_VERTICAL);
                diaTitle.setBackgroundColor(Color.TRANSPARENT);

                diaTitle.setLayoutParams(margin1);
                diaTitle.setTextColor(Color.GRAY);
                diaTitle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DiagramEditDialog dialog=new DiagramEditDialog(context,lineFile,diagram);
                        dialog.show();
                        ((MainActivity)getContext()).closeMenu();
                    }
                });
                Button downButton = new Button(context);
                downButton.setText(activity.getString(R.string.downwardTimeTable));
                downButton.setGravity(Gravity.START);
                downButton.setGravity(Gravity.CENTER_VERTICAL);
                downButton.setTextColor(Color.BLACK);
                downButton.setBackgroundColor(Color.TRANSPARENT);

                downButton.setLayoutParams(margin2);

                downButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aodia.openTimeTable(lineFile,diagramIndex,0);

                    }
                });
                Button upButton = new Button(context);
                upButton.setText(activity.getString(R.string.upwardTimeTable));
                upButton.setGravity(Gravity.START);
                upButton.setGravity(Gravity.CENTER_VERTICAL);
                upButton.setTextColor(Color.BLACK);

                upButton.setLayoutParams(margin2);
                upButton.setBackgroundColor(Color.TRANSPARENT);


                upButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aodia.openTimeTable(lineFile,diagramIndex,1);

                    }
                });
                Button diagramButton = new Button(context);
                diagramButton.setText(activity.getString(R.string.diagram));
                diagramButton.setGravity(Gravity.START);
                diagramButton.setGravity(Gravity.CENTER_VERTICAL);
                diagramButton.setTextColor(Color.BLACK);
                diagramButton.setLayoutParams(margin2);
                diagramButton.setBackgroundColor(Color.TRANSPARENT);


                diagramButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        aodia.openDiagram(lineFile,diagramIndex);

                    }
                });

                lineContLinear.addView(diaTitle);
                lineContLinear.addView(downButton);
                lineContLinear.addView(upButton);
                lineContLinear.addView(diagramButton);
               // lineContLinear.addView(operationButton);

            }
            Button comment = new Button(context);
            comment.setText(activity.getString(R.string.comment));
            comment.setGravity(Gravity.START);
            comment.setBackgroundColor(Color.TRANSPARENT);
            comment.setTextColor(Color.BLACK);
            comment.setLayoutParams(margin1);
            comment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.openComment(lineFile);
                }
            });
            lineContLinear.addView(comment);
            addView(lineContLinear);
            if(aodia.lineFileExpand.get(lineFile)){
                openMenu();
            }else{
                closeMenu();
            }
        }catch(Exception e){
            SDlog.log(e);
        }
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
