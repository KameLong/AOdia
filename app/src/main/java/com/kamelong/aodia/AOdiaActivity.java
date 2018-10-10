package com.kamelong.aodia;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kamelong.OuDia.DiaFile;
import com.kamelong.aodia.AOdiaIO.FileSelectFragment;
import com.kamelong.aodia.Diagram.DiagramFragment;
import com.kamelong.aodia.TimeTable.TimeTableFragment;
import com.kamelong.aodia.detabase.AOdiaDetabase;
import com.kamelong.aodia.menu.MenuFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AOdiaActivity extends AppCompatActivity {
    public Payment payment=null;
    public ArrayList<DiaFile>diaFiles=new ArrayList<>();
    public ArrayList<Integer>diaFilesIndex=new ArrayList<>();

    public ArrayList<AOdiaFragment>fragments=new ArrayList<>();

    private  MenuFragment menuFragment=null;

    public DrawerLayout drawerLayout=null;
    public static final String PREFERENCES_NAME="AOdia-Setting";
    public AOdiaDetabase database=new AOdiaDetabase(this);
    private boolean storagePermission(){
        if(Build.VERSION.SDK_INT<23){
            return true;
        }
        int permissionCheck=ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            int reqestCode=1;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},reqestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        payment=new Payment(this);
        SdLog.setActivity(this);
        setContentView(R.layout.activity_main);
        //setting();
        drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
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
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        menuFragment=new MenuFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu,menuFragment);
        fragmentTransaction.commit();
        createSample();
        /**
         * 直前に開いていたファイルを復元します。
         */
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        String filePath=pref.getString("finalFilePath","");
        int diaNumber=pref.getInt("finalDiaNumber",0);
        int direction=pref.getInt("finalDirection",0);
        if(filePath.length()!=0){
            try{
                File file=new File(filePath);
                if(file.exists()){
                    openFile(file);
                }else{
                    throw new Exception("file is not Exist");
                }
                if(direction<2){
                    openTimeTable(0,diaNumber,direction);
                }else{
                    openDiagram(0,diaNumber);
                }
            }catch (Exception e){
                openHelpFragment();
            }
        }else{
            openHelpFragment();
        }

    }
    @Override
    public void onDestroy(){
        payment.close();
        super.onDestroy();
    }
    public void onStop(){
        super.onStop();
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


    private void openFragment(AOdiaFragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        TextView titleView=findViewById(R.id.titleView);
        titleView.setText(fragment.fragmentName());


    }

    public void openFileSelectFragment(){
        FileSelectFragment fragment=new FileSelectFragment();
        openFragment(fragment);
    }
    public void openHelpFragment(){
        AOdiaFragment fragment=new HelpFragment();
        openFragment(fragment);
    }

    public void openFile(File file){
        try {
            DiaFile diaFile = new DiaFile(file);
            diaFiles.add(diaFile);
            diaFilesIndex.add(0,diaFiles.size()-1);
            database.addHistory(diaFile.filePath);
            database.addNewFileToLineData(diaFile.filePath,diaFile.getDiaNum());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void openTimeTable(int fileIndex,int diaNum,int direction){
        TimeTableFragment fragment=new TimeTableFragment();
        Bundle args=new Bundle();
        args.putInt("fileNum",diaFilesIndex.get(fileIndex));
        args.putInt("diaNum", diaNum);
        args.putInt("direction", direction);
        fragment.setArguments(args);
        openFragment(fragment);
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("finalFilePath",diaFiles.get(diaFilesIndex.get(fileIndex)).filePath);
        editor.putInt("finalDiaNumber",diaNum);
        editor.putInt("finalDirection",direction);
        editor.apply();
    }
    public void openDiagram(int fileIndex,int diaNum){
        DiagramFragment fragment=new DiagramFragment();
        Bundle args=new Bundle();
        args.putInt("fileNumber",diaFilesIndex.get(fileIndex));
        args.putInt("diaNumber", diaNum);
        fragment.setArguments(args);
        openFragment(fragment);
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("finalFilePath",diaFiles.get(diaFilesIndex.get(fileIndex)).filePath);
        editor.putInt("finalDiaNumber",diaNum);
        editor.putInt("finalDirection",2);
        editor.apply();
    }
    public void killDiaFile(int fileIndex,int menuIndex){
        diaFiles.set(diaFilesIndex.get(fileIndex),null);
        diaFilesIndex.remove(menuIndex);
        menuFragment.createMenu();
    }
    public void upDiaFile(int menuIndex){
        if(menuIndex>0){
            int temp=diaFilesIndex.get(menuIndex-1);
            diaFilesIndex.set(menuIndex-1,diaFilesIndex.get(menuIndex));
            diaFilesIndex.set(menuIndex,temp);
        }
        menuFragment.createMenu();

    }

}
