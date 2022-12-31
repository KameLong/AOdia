package com.kamelong.aodia;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.DiagramFragment.DiagramDefaultView;
import com.kamelong.aodia.TimeTable.TimeTableDefaultView;
import com.kamelong.aodia.menu.MenuFragment;
import com.kamelong.tool.SDlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity {
    public MenuFragment menuFragment;
    private AOdia aodiaData=new AOdia(this);
    private boolean saveLoop=true;
    private ActivityResultLauncher openSystemFileLauncher;
    private ActivityResultLauncher saveSystemFileLauncher;
    private LineFile tmpSaveFile=null;
    private String tmpFileName=null;

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
        Intent intent=getIntent();
        SDlog.setActivity(this);
        setContentView(R.layout.activity_main);
        //メニュー設定
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
        openDrawer.setOnClickListener(view -> {
            if(menuLayout.isDrawerOpen(GravityCompat.START)){
                closeMenu();
            }else{
                openMenu();
            }
        });
        menuFragment=new MenuFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu,menuFragment);
        fragmentTransaction.commit();

        //サンプルファイル作成
        createSample();
        //AOdia起動
        aodiaData.openHelp();
        aodiaData.loadTempData();
        System.out.println("intent action:"+intent.getAction());
        if(intent.getAction()!=null&&(intent.getAction().equals(Intent.ACTION_EDIT)||intent.getAction().equals(Intent.ACTION_VIEW))){
            System.out.println();
            try {
                System.out.println("intent data:"+intent.getData());
                Uri uri=intent.getData();
                if(uri.toString().startsWith("content")){
                    aodiaData.openUri(getContentResolver(),uri,null);
                }else{
                    String path=intent.getData().getEncodedPath();
                    System.out.println("intent path1:"+path);
                    if(path.startsWith("/downloads")){
                        path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+path.substring(10);
                    }
                    System.out.println("intent path2:"+path);
                    File file = new File(path);

                    aodiaData.openFile(file);

                }
            } catch (Exception e){
                e.printStackTrace();
                SDlog.toast("ファイルを開くことができません.");
            }
        }
        //メイン画面にあるボタン設定
        findViewById(R.id.backFragment).setOnClickListener(v -> aodiaData.backFragment());
        findViewById(R.id.proceedFragment).setOnClickListener(v -> aodiaData.proceedFragment());
        findViewById(R.id.killFragment).setOnClickListener(v -> aodiaData.killFragment());
        saveLoop=true;
        //自動保存機能開始
        new Thread(() -> {
            while(saveLoop){
                try{
                    Thread.sleep(1000*100);//10秒ごとに保存
                    MainActivity.this.getAOdia().saveData();

                }catch (Exception e){
                    SDlog.log(e);
                }
            }
        }).start();
        //ストレージ権限があるか確認
        storagePermission();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Handler handler=new Handler();
        new Thread(() -> {
            try {
                String url = "http://kamelong.com/aodia/AOdiaInfoVersion.html";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //お知らせ機能
                    InputStream is = con.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    final int version=Integer.parseInt(br.readLine());
                    handler.post(() -> {
                        int savedVersion = pref.getInt("showDialogVersion", -1);
                        if (savedVersion != version) {
//                            Dialog dialog = new InfoDialog(MainActivity.this);
//                            dialog.show();
                            pref.edit().putInt("showDialogVersion", version).apply();
                        }
                    });
                } else {
                    //何もしない
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        //エラーを開発者に通知させる
        if((!pref.getBoolean("send_log",false))
                &&(!pref.getBoolean("send_log_action",false))
        ){
            SendLogPerm perm=new SendLogPerm(this,pref);
            perm.show();
        }
        initApp();
        openSystemFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData  = result.getData();
                        if (resultData  != null) {
                            Uri uri = resultData.getData();
                            aodiaData.openUri(getContentResolver(),uri,null);
                        }
                    }
                });
        saveSystemFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultData  = result.getData();
                        if (resultData  != null) {
                            Uri uri = resultData.getData();
                            if(tmpSaveFile!=null){
                                try{
                                    if(tmpFileName!=null&&tmpFileName.endsWith(".oud2")){
                                        tmpSaveFile.saveToFile(getContentResolver(),uri);

                                    }else{
                                        tmpSaveFile.saveToOuDiaFile(getContentResolver(),uri);
                                    }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                    SDlog.toast("原因不明エラー："+e.getLocalizedMessage());
                                }
                            }else{
                                SDlog.toast("エラー　保存すべきデータが何らかの原因で失われました。処理中に別のアプリを開いた場合に発生する可能性があります。");
                            }

                        }
                    }
                });

        if(savedInstanceState!=null){
            String fragmentHash=savedInstanceState.getString("fragment");
            aodiaData.openFragment(fragmentHash);
        }
    }
    private void initApp(){
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            final String textSize = pref.getString("textsize", "30");
            TimeTableDefaultView.setTextSize(Integer.parseInt(textSize));
            DiagramDefaultView.setTextSize(Integer.parseInt(textSize));

            final String diagramStationWidth = pref.getString("diagramStationWidth", "5");
            DiagramDefaultView.setStationWidth(Integer.parseInt(diagramStationWidth));
            final String timetableStationWidth = pref.getString("timetableStationWidth", "5");
            TimeTableDefaultView.setStationWidth(Integer.parseInt(timetableStationWidth));
        }catch (Exception e){
            SDlog.log(e);
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
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("fragment",aodiaData.getFragmentHash());
    }


    @Override
    public void onStop(){

        saveLoop=false;
        SDlog.log("tempSave onStop");
        aodiaData.saveData();
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
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
    public void OpenSystemFiler(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        openSystemFileLauncher.launch(intent);

    }
    public void SaveSystemFiler(String fileName,LineFile lineFile){
        tmpSaveFile=lineFile;
        tmpFileName=fileName;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        saveSystemFileLauncher.launch(intent);
    }


}
