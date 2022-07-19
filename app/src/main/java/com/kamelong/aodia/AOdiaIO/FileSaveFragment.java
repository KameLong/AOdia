package com.kamelong.aodia.AOdiaIO;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 端末内にファイルを保存するたのFragment
 */
public class FileSaveFragment extends AOdiaFragmentCustom {
    public static final String FRAGMENT_NAME="FileSaveFragment";
    MainActivity activity;
    LineFile lineFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            activity = (MainActivity) getActivity();
            try {//まずBundleを確認し、fileNum,diaIndex,directを更新する
                Bundle bundle = getArguments();
                int lineIndex = bundle.getInt(AOdia.FILE_INDEX, 0);
                    lineFile = getAOdia().getLineFile(lineIndex);
            } catch (Exception e) {
                SDlog.log(e);
            }
            return inflater.inflate(R.layout.filesave_fragment, container, false);
        } catch (Exception e) {
            SDlog.log(e);
        }
        return new View(activity);
    }

    /**
     * ここではtabHostの初期化を行う
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if(lineFile==null){
            getAOdia().killFragment(this);
            return;
        }
        try {
            TabHost tabHost = activity.findViewById(R.id.tabhost);
            tabHost.setup();
            //現在、端末内に保存するtabしか存在しない
            TabHost.TabSpec spec;
            spec = tabHost.newTabSpec("Tab1")
                    .setIndicator(getString(R.string.FileInTheDevice))
                    .setContent(R.id.tab1);
            tabHost.addTab(spec);
            spec = tabHost.newTabSpec("Tab2")
                    .setIndicator("Google Drive")
                    .setContent(R.id.tab2);
            tabHost.addTab(spec);

            tabHost.setCurrentTab(0);

            try {
                FileSaveToSystem fileSaveFromSystem=getActivity().findViewById(R.id.tab1);
                fileSaveFromSystem.setLineFile(lineFile);
                FileSaveToGoogleDrive fileSaveToGoogleDrive=getActivity().findViewById(R.id.tab2);
                fileSaveToGoogleDrive.setLineFile(lineFile);

            } catch (Exception e) {
                SDlog.log(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        try {
            String name = lineFile.name;
            if (name.length() > 10) {
                name = name.substring(0, 10);
            }
            return name + "\nファイル保存";
        } catch (Exception e) {
            return "ファイル保存";

        }
    }

    @Override
    public String getHash() {
        return FRAGMENT_NAME;
    }

    @Override
    public LineFile getLineFile(){
        return lineFile;
    }

}