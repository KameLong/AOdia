package com.kamelong.aodia;

import android.Manifest;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kamelong.aodia.menu.MenuFragment;
import com.kamelong.tool.SDlog;
import com.kamelong2.aodia.AOdiaActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity {
    public MenuFragment menuFragment;
    private AOdia aodiaData=new AOdia(this);

    public boolean storagePermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            int reqestCode = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, reqestCode);
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDlog.setActivity(this);
        setContentView(R.layout.activity_main);
        final DrawerLayout menuLayout=findViewById(R.id.drawer_layout);
        menuLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(View drawerView) {
                menuFragment.createMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView) {}
            @Override
            public void onDrawerStateChanged(int newState) { }
        });
        Button openDrawer=findViewById(R.id.Button2);
        openDrawer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(menuLayout.isDrawerOpen(GravityCompat.START)){
                    closeMenu();
                }else{
                    openMenu();
                }
            }
        });
        menuFragment=new MenuFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu,menuFragment);
        fragmentTransaction.commit();
        createSample();
        aodiaData.openHelp();
        aodiaData.loadData();
        //メイン画面にあるボタン
        findViewById(R.id.backFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aodiaData.backFragment();
            }
        });
        findViewById(R.id.proceedFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aodiaData.proceedFragment();
            }
        });
        findViewById(R.id.killFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aodiaData.killFragment();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        Thread.sleep(1000*10);
                        MainActivity.this.getAOdia().saveData();

                    }catch (Exception e){
                    }
                }
            }
        }).start();
        storagePermission();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        final Handler handler=new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String url = "http://kamelong.com/aodia/AOdiaInfoVersion.html";

                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        final int version=Integer.parseInt(br.readLine());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int savedVersion=pref.getInt("showDialogVersion",-1);
                                if(savedVersion!=version){
                                    Dialog dialog=new InfoDialog(MainActivity.this);
                                    dialog.show();
                                    pref.edit().putInt("showDialogVersion",version).apply();
                                }
                            }
                        });
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        if((!pref.getBoolean("send_log",false))
                &&(!pref.getBoolean("send_log_action",false))
        ){
            SendLogPerm perm=new SendLogPerm(this,pref);
            perm.show();
        }
    }
    @NonNull
    public AOdia getAOdia(){
        return aodiaData;
    }

    /*
     *　メニュー関係
     */
    public void openMenu() {
        final DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
        menuFragment.createMenu();
    }
    public void closeMenu(){
        final DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

    }
    /*
     * Fragment関係
     */
    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    public void openFragment(AOdiaFragment fragment){
        //もしメニューが開いていたら閉じる
        closeMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, (Fragment)fragment);
        fragmentTransaction.commit();
    }
    public void killFragment(AOdiaFragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach((Fragment) fragment);
        fragmentTransaction.commit();

    }

    private void createSample(){
        File sample=new File(getExternalFilesDir(null), "sample.oud");
        if(sample.exists()){
            return;
        }
        try{
            InputStream input=getAssets().open("sample.oud");
            OutputStream output=new FileOutputStream(sample);
            byte[] data=new byte[input.available()];
            input.read(data);
            output.write(data);
            input.close();
            output.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(){
        aodiaData.saveData();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            aodiaData.backFragment();
            return false;
        }else{
            return false;
        }
    }
    public void setVisibleProceed(boolean bool){
        if(bool) {
            findViewById(R.id.proceedFragment).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.proceedFragment).setVisibility(View.INVISIBLE);

        }
    }
    public void setVisibleBack(boolean bool){
        if(bool) {
            findViewById(R.id.backFragment).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.backFragment).setVisibility(View.INVISIBLE);

        }
    }


}
