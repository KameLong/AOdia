package com.kamelong.aodia.AOdiaIO;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

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
 * ファイルオープン時に使うFragment
 */
public class FileSelectorFragment extends AOdiaFragmentCustom {
    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            activity = (MainActivity) getActivity();
            return inflater.inflate(R.layout.fileselector, container, false);
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
        try {
            TabHost tabHost = activity.findViewById(R.id.tabhost);
            tabHost.setup();
            TabHost.TabSpec spec;
            // Tab1
            spec = tabHost.newTabSpec("Tab1")
                    .setIndicator(getString(R.string.FileInTheDevice))
                    .setContent(R.id.tab1);
            tabHost.addTab(spec);

            // Tab2
            spec = tabHost.newTabSpec("Tab2")
                    .setIndicator(getString(R.string.OuDiaDatabase))
                    .setContent(R.id.tab2);
            tabHost.addTab(spec);

            spec = tabHost.newTabSpec("Tab3")
                    .setIndicator(getString(R.string.history))
                    .setContent(R.id.tab3);
            tabHost.addTab(spec);

            tabHost.setCurrentTab(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getName() {
        return "ファイル選択";
    }
    @Override
    public LineFile getLineFile(){
        return null;
    }

}