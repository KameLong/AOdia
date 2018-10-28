package com.kamelong.aodia.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.SdLog;
import com.kamelong.aodia.AOdiaIO.SearchFileDialog;

import java.io.File;
import java.util.ArrayList;

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
public class MenuFragment extends android.support.v4.app.Fragment {
    private LinearLayout layout;
    private ArrayList<DiaFile>diaFiles;
    private AOdiaActivity activity;

    // 初期フォルダ
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
        activity=(AOdiaActivity) getActivity();
        diaFiles= activity.diaFiles;
            return inflater.inflate(R.layout.menu, container, false);
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

            Button newFile = new Button(activity);
            newFile.setText("　新規作成");
            newFile.setBackgroundColor(Color.TRANSPARENT);
            newFile.setGravity(Gravity.START);
            //fileOpenLayout.addView(openFile);
//            layout.addView(newFile);
            Button openFileIcon=new Button(activity);
            //openFileIcon.setBackgroundResource(R.drawable.menu_open_file);
            fileOpenLayout.addView(openFileIcon);
            Button openFile = new Button(activity);
            openFile.setText("　ファイルを開く");
            openFile.setBackgroundColor(Color.TRANSPARENT);
            openFile.setGravity(Gravity.START);
            //fileOpenLayout.addView(openFile);
            openFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openFileSelectFragment();
                }
            });
            layout.addView(openFile);
            Button saveFile = new Button(activity);
            saveFile.setText("　ファイルを保存する");
            saveFile.setBackgroundColor(Color.TRANSPARENT);
            saveFile.setGravity(Gravity.START);
            saveFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                   activity.openSaveDialog();
                }
            });

//            layout.addView(saveFile);
            SearchView stationSearch=new SearchView(activity);
            stationSearch.setQueryHint("駅検索");
            stationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String stationName) {
                    SdLog.log("検索");
                    SearchFileDialog.OnFileSelectListener test=new SearchFileDialog.OnFileSelectListener() {
                        @Override
                        public void onFileSelect(File file) {
                            //MainActivityに処理を投げる
//                            activity.onFileSelect(file);
                        }
                        @Override
                        public void onFileListSelect(File[] file) {
                            //nothing to do
                        }
                    };
                    SearchFileDialog searchDialog=new SearchFileDialog(getActivity(),test);
//                    DBHelper db=new DBHelper(activity);
//                    ArrayList<String> fileList=db.searchStationPath(stationName);
//                    searchDialog.show(stationName,fileList);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            //layout.addView(stationSearch);




            Button resetButton=new Button(activity);

            resetButton.setText("　内部データを初期化する");
            resetButton.setBackgroundColor(Color.TRANSPARENT);
            resetButton.setGravity(Gravity.START);
            //fileOpenLayout.addView(openFile);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    activity.resetDetabase();
                }
            });
//            layout.addView(resetButton);

            Button openHelp = new Button(activity);
            openHelp.setText("　v2.4.2のヘルプを開く");
            openHelp.setBackgroundColor(Color.TRANSPARENT);
            openHelp.setGravity(Gravity.START);
            openHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openHelpFragment();
                }
            });
            layout.addView(openHelp);
            for (int i = 0; i < activity.diaFilesIndex.size(); i++) {
                if(diaFiles.get(activity.diaFilesIndex.get(i))==null){
                    continue;
                }
                LineMenu lineMenu=new LineMenu(activity,diaFiles.get(activity.diaFilesIndex.get(i)),i,i);
                layout.addView(lineMenu);
            }
            Button openSetting = new Button(activity);
            openSetting.setText("　設定");
            openSetting.setBackgroundColor(Color.TRANSPARENT);
            openSetting.setGravity(Gravity.START);
            //fileOpenLayout.addView(openFile);
            openSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.openSettingFragment();
                }
            });
            layout.addView(openSetting);

        }catch(Exception e){
            SdLog.log(e);
        }
    }

}
