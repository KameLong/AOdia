package com.fc2.web.kamelong.aodia.menu;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;

import java.util.ArrayList;

/**
 * Created by kame on 2017/01/23.
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
 * メニューを表示するFragment.
 * LinearLayout内にButtonを配置することでメニューを構成する
 *
 *
 */
public class MenuFragment extends Fragment {
    private LinearLayout layout;
    private ArrayList<DiaFile>diaFiles;
    private ArrayList<Integer>diaFilesIndex;
    private MainActivity activity;

    // 初期フォルダ
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
        activity=(MainActivity) getActivity();
        diaFiles=activity.diaFiles;
            diaFilesIndex=activity.diaFilesIndex;
            View v = inflater.inflate(R.layout.menu, container, false);

            return v;
        }catch(Exception e){
            SdLog.log(e);
        }
        return new View(activity);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        try {
            layout=(LinearLayout) activity.findViewById(R.id.menu_layout);
            createMenu();
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    public void createMenu() {
        try {
            layout.removeAllViews();

            LinearLayout fileOpenLayout=new LinearLayout(activity);
            fileOpenLayout.setOrientation(LinearLayout.HORIZONTAL);

            Button openFileIcon=new Button(activity);
            //openFileIcon.setBackgroundResource(R.drawable.menu_open_file);
            fileOpenLayout.addView(openFileIcon);
            Button openFile = new Button(activity);
            openFile.setText("　ファイルを開く");
            openFile.setBackgroundColor(Color.TRANSPARENT);
            openFile.setGravity(Gravity.LEFT);
            //fileOpenLayout.addView(openFile);
            openFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openFileDialog();
                }
            });
            layout.addView(openFile);

            Button connectNetgram = new Button(activity);
            connectNetgram.setText("　ネットグラムにアクセス");
            connectNetgram.setBackgroundColor(Color.TRANSPARENT);
            connectNetgram.setGravity(Gravity.LEFT);
            //fileOpenLayout.addView(openFile);
            connectNetgram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.connectNetgram();
                }
            });
            //layout.addView(connectNetgram);


            Button resetButton=new Button(activity);

            resetButton.setText("　内部データを初期化する");
            resetButton.setBackgroundColor(Color.TRANSPARENT);
            resetButton.setGravity(Gravity.LEFT);
            //fileOpenLayout.addView(openFile);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.resetDetabase();
                }
            });
            layout.addView(resetButton);

            Button openHelp = new Button(activity);
            openHelp.setText("　v1.1.3のヘルプを開く");
            openHelp.setBackgroundColor(Color.TRANSPARENT);
            openHelp.setGravity(Gravity.LEFT);
            openHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openHelp();
                }
            });
            layout.addView(openHelp);
            for (int i = 0; i < diaFilesIndex.size(); i++) {
                LineMenu lineMenu=new LineMenu(activity,diaFiles.get(diaFilesIndex.get(i)),diaFilesIndex.get(i),i);
                layout.addView(lineMenu);
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }

}
