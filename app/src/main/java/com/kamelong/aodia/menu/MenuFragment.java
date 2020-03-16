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

import com.kamelong.aodia.AOdia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;


public class MenuFragment extends AOdiaFragmentCustom {
    public static final String FRAGMENT_NAME="MenuFragment";

    MainActivity activity;

    AOdia aodia;
    int count=0;

    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            activity=(MainActivity) getActivity();
            aodia=activity.getAOdia();
            return inflater.inflate(R.layout.menu, container, false);
        }catch(Exception e){
            SDlog.log(e);
        }
        return new View(activity);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        try {
            layout=activity.findViewById(R.id.menu_layout);
            createMenu();
        }catch(Exception e){
            SDlog.log(e);
        }
    }
    public void createMenu() {
        try {

            layout.removeAllViews();
            getActivity().findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                }
            });


            LinearLayout fileOpenLayout=new LinearLayout(activity);

            Button newFile = new Button(activity);
            newFile.setText(getString(R.string.newFile));
            newFile.setBackgroundColor(Color.TRANSPARENT);
            newFile.setGravity(Gravity.START);
            newFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().makeNewLineFile();

                }
            });
            layout.addView(newFile);


            SearchView searchView=new SearchView(activity);
            searchView.setQueryHint("駅検索");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    aodia.openSearchFragment(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

//            layout.addView(searchView);

            Button routeMap = new Button(activity);
            routeMap.setText("路線図");
            routeMap.setBackgroundColor(Color.TRANSPARENT);
            routeMap.setGravity(Gravity.START);
            routeMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().openRouteMap();

                }
            });
//            layout.addView(routeMap);




            Button openFileIcon=new Button(activity);
            fileOpenLayout.addView(openFileIcon);
            Button openFile = new Button(activity);
            openFile.setText(getString(R.string.openFile));
            openFile.setBackgroundColor(Color.TRANSPARENT);
            openFile.setGravity(Gravity.START);
            openFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aodia.openFileSelect();

                }
            });
            layout.addView(openFile);

            for (LineFile lineFile : aodia.getLineFileList()) {
                if(lineFile==null){
                    //todo aodia側の処理がおかしい
                    continue;
                }
                LineMenu lineMenu=new LineMenu(activity,lineFile);
                layout.addView(lineMenu);
            }
            Button openSetting = new Button(activity);
            openSetting.setText(getString(R.string.options));
            openSetting.setBackgroundColor(Color.TRANSPARENT);
            openSetting.setGravity(Gravity.START);
            openSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().openSetting();
                }
            });
            layout.addView(openSetting);
            Button openHelp = new Button(activity);
            openHelp.setText(getString(R.string.openHelp));
            openHelp.setBackgroundColor(Color.TRANSPARENT);
            openHelp.setGravity(Gravity.START);
            openHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().openHelp();
                }
            });
            layout.addView(openHelp);
            Button userhelp = new Button(activity);
            userhelp.setText("ユーザーヘルプを開く");
            userhelp.setBackgroundColor(Color.TRANSPARENT);
            userhelp.setGravity(Gravity.START);
            userhelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().openUserHelp();
                }
            });
            layout.addView(userhelp);
            Button openPay = new Button(activity);
            openPay.setText("開発者に寄付する");
            openPay.setBackgroundColor(Color.TRANSPARENT);
            openPay.setGravity(Gravity.START);
            openPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAOdia().openPayFragment();
                }
            });
                layout.addView(openPay);
        }catch(Exception e){
            SDlog.log(e);
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getHash() {
        return FRAGMENT_NAME;
    }

    @Override
    public LineFile getLineFile(){
        return null;
    }

}
