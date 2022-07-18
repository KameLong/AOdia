package com.kamelong2.aodia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kamelong.aodia.R;
import com.kamelong2.OuDia.DiaFile;
import com.kamelong2.OuDia.Train;
import com.kamelong2.aodia.AOdiaIO.FileSelectFragment;
import com.kamelong2.aodia.AOdiaIO.SaveDialog;
import com.kamelong2.aodia.EditStation.EditStationFragment;
import com.kamelong2.aodia.EditTrainType.EditTrainTypeFragment;
import com.kamelong2.aodia.StationTimeTable.StationInfoFragment;
import com.kamelong2.aodia.StationTimeTable.StationInfoIndexFragment;
import com.kamelong2.aodia.TimeTable.TimeTableFragment;
import com.kamelong2.aodia.detabase.AOdiaDetabase;
import com.kamelong2.aodia.diagram.DiagramFragment;
import com.kamelong2.aodia.menu.MenuFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class AOdiaActivity extends FragmentActivity {
    public ArrayList<DiaFile>diaFiles=new ArrayList<>();
    public ArrayList<Integer>diaFilesIndex=new ArrayList<>();

    public ArrayList<AOdiaFragment>fragments=new ArrayList<>();
    public int fragmentIndex=0;

    public  MenuFragment menuFragment=null;

    public DrawerLayout drawerLayout=null;
    public static final String PREFERENCES_NAME="AOdia-Setting";
    public AOdiaDetabase database=new AOdiaDetabase(this);
    public boolean storagePermission(){
        if(Build.VERSION.SDK_INT<23){
            return true;
        }
        int permissionCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
        SDlog.setActivity(this);
        setContentView(R.layout.old_activity_main);
        setting();
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

        Button backButton=findViewById(R.id.backFragment);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentIndex--;
                System.out.println("fragment="+fragmentIndex+",max="+fragments.size());
                if(fragmentIndex<=0){
                    fragmentIndex=0;
                }
                selectFragment(fragments.get(fragmentIndex));
                findViewById(R.id.backFragment).setVisibility(fragmentIndex==0?View.INVISIBLE:View.VISIBLE);
                findViewById(R.id.proceedFragment).setVisibility(fragmentIndex<fragments.size()-1?View.VISIBLE:View.INVISIBLE);

            }
        });
        Button nextButton=findViewById(R.id.proceedFragment);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentIndex++;
                System.out.println("fragment=" + fragmentIndex + ",max=" + fragments.size());
                if (fragmentIndex >= fragments.size()) {
                    fragmentIndex = fragments.size() - 1;
                }
                selectFragment(fragments.get(fragmentIndex));
                findViewById(R.id.backFragment).setVisibility(fragmentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
                findViewById(R.id.proceedFragment).setVisibility(fragmentIndex < fragments.size() - 1 ? View.VISIBLE : View.INVISIBLE);

            }
        });
        Button killButton=findViewById(R.id.killFragment);
        killButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                killFragment(fragmentIndex);
            }
        });

        if(filePath.length()!=0){
            try{

                for(int i=0;i<diaFilesIndex.size();i++){
                    if(diaFiles.get(diaFilesIndex.get(i)).filePath.equals(filePath)){
                        if(direction<2){
                            openTimeTable(0,diaNumber,direction);
                        }else{
                            openDiagram(0,diaNumber);
                        }
                        return;
                    }
                }
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
        super.onDestroy();
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
        if(fragments.size()>1&&fragments.size()>fragmentIndex&&fragments.get(fragmentIndex).fragmentName().equals("駅編集")){
            new AlertDialog.Builder(this)
                    .setTitle("駅編集反映確認")
                    .setMessage("駅編集作業を破棄しますか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            killFragment(fragmentIndex);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SDlog.toast("駅編集完了ボタンを押してください。");

                        }
                    })
                    .show();
            return;
        }
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            fragmentIndex = fragments.size();
            fragments.add(fragment);
            findViewById(R.id.backFragment).setVisibility(fragmentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
            findViewById(R.id.proceedFragment).setVisibility(fragmentIndex < fragments.size() - 1 ? View.VISIBLE : View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"原因不明のエラーが発生しました",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private void selectFragment(AOdiaFragment fragment){
        //もしメニューが開いていたら閉じる
        closeMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();

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
            for(int i=0;i<diaFilesIndex.size();i++){

                if(file.getAbsolutePath().equals(diaFiles.get(diaFilesIndex.get(i)).filePath)){
                    int diaIndex=diaFilesIndex.get(i);
                    diaFilesIndex.remove(i);
                    diaFilesIndex.add(0,diaIndex);
                    openTimeTable(0,0,0);
                    return;
                }
            }
            try {
                DiaFile diaFile = new DiaFile(file);
                diaFiles.add(diaFile);
                diaFilesIndex.add(0,diaFiles.size()-1);
                database.addHistory(diaFile.filePath);
                database.addNewFileToLineData(diaFile.filePath,diaFile.getDiaNum());
                openTimeTable(0,0,0);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this,"エラー：このダイヤファイルを開く事ができませんでした。該当ファイルを、作者メールアドレス(kamelong.dev@gmail.com)に送信していただくと対応いたします。",Toast.LENGTH_LONG).show();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public TimeTableFragment openTimeTable(int menuIndex,int diagramIndex,int direction){
        TimeTableFragment fragment=new TimeTableFragment();
        Bundle args=new Bundle();
        args.putInt("fileIndex",diaFilesIndex.get(menuIndex));
        args.putInt("diagramIndex", diagramIndex);
        args.putInt("direction", direction);
        fragment.setArguments(args);
        openFragment(fragment);
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("finalFilePath",diaFiles.get(diaFilesIndex.get(menuIndex)).filePath);
        editor.putInt("finalDiaNumber",diagramIndex);
        editor.putInt("finalDirection",direction);
        editor.apply();
        return fragment;
    }
    public DiagramFragment openDiagram(int menuIndex, int diagramIndex){
        DiagramFragment fragment=new DiagramFragment();
        Bundle args=new Bundle();
        args.putInt("fileNumber",diaFilesIndex.get(menuIndex));
        args.putInt("diaNumber", diagramIndex);
        fragment.setArguments(args);
        openFragment(fragment);
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("finalFilePath",diaFiles.get(diaFilesIndex.get(menuIndex)).filePath);
        editor.putInt("finalDiaNumber",diagramIndex);
        editor.putInt("finalDirection",2);
        editor.apply();
        return fragment;
    }
    public void openTrainEdit(int diagramIndex,Train train){
        openTrainEdit(diaFiles.indexOf(train.diaFile),diagramIndex,train.direction,train.diaFile.diagram.get(diagramIndex).trains[train.direction].indexOf(train));
    }
    public void openTrainEdit(int fileIndex,int diagramIndex,int direction,int trainIndex){
        TimeTableFragment fragment=new TimeTableFragment();
        Bundle args=new Bundle();
        args.putInt("fileIndex",fileIndex);
        args.putInt("diagramIndex", diagramIndex);
        args.putInt("direction", direction);
        args.putInt("trainEdit", trainIndex);

        fragment.setArguments(args);
        openFragment(fragment);
        SharedPreferences pref =getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("finalFilePath",diaFiles.get(fileIndex).filePath);
        editor.putInt("finalDiaNumber",diagramIndex);
        editor.putInt("finalDirection",direction);
        editor.apply();

    }
    public void openSaveDialog(int menuIndex){
        Dialog dialog=new SaveDialog(this,diaFiles.get(diaFilesIndex.get(menuIndex)));
        dialog.show();
    }
    public void openStationTimeTable(int fileIndex,int diaIndex,int direction,int stationIndex){
        StationInfoFragment fragment=new StationInfoFragment();
        Bundle args=new Bundle();
        args.putInt("fileNum",fileIndex);
        args.putInt("diaN",diaIndex);
        args.putInt("direct",direction);
        args.putInt("station",stationIndex);
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openEditStationFragment(int menuIndex){
        EditStationFragment fragment=new EditStationFragment();
        Bundle args=new Bundle();
        args.putInt("fileIndex",diaFilesIndex.get(menuIndex));
        fragment.setArguments(args);
        openFragment(fragment);

    }
    public void openEditTrainTypeFragment(int menuIndex){
        EditTrainTypeFragment fragment=new EditTrainTypeFragment();
        Bundle args=new Bundle();
        args.putInt("fileIndex",diaFilesIndex.get(menuIndex));
        fragment.setArguments(args);
        openFragment(fragment);

    }

    public void openStationTimeTableIndex(int menuIndex){
        StationInfoIndexFragment fragment=new StationInfoIndexFragment();
        Bundle args=new Bundle();
        args.putInt("fileNum",diaFilesIndex.get(menuIndex));
        fragment.setArguments(args);
        openFragment(fragment);

    }
    public void openCommentFragment(int fileIndex){
        CommentFragment fragment=new CommentFragment();
        Bundle args=new Bundle();
        args.putInt("fileIndex",diaFilesIndex.get(fileIndex));
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openSettingFragment(){

        closeMenu();
        SettingFragment fragment=new SettingFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
    public void killDiaFile(int fileIndex,int menuIndex){
        diaFiles.set(diaFilesIndex.get(fileIndex),null);
        diaFilesIndex.remove(menuIndex);
        for(int i=0;i<fragments.size();i++){
            if(!diaFiles.contains(fragments.get(i).diaFile)){
                getSupportFragmentManager().beginTransaction().remove(fragments.get(i)).commit();
                fragments.remove(i);
                if(i<fragmentIndex){
                    fragmentIndex--;
                }
                i--;
            }
        }

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

    /**
     * 指定されたIndexのFragmentをkillする
     * @param index
     */
    public void killFragment(int index){
        if(index<0){
            return;
        }
        try {
            fragments.remove(index);
            if (fragmentIndex >= index) {
                fragmentIndex--;
            }
            findViewById(R.id.backFragment).setVisibility(fragmentIndex == 0 ? View.INVISIBLE : View.VISIBLE);
            findViewById(R.id.proceedFragment).setVisibility(fragmentIndex < fragments.size() - 1 ? View.VISIBLE : View.INVISIBLE);
            if (fragmentIndex == -1) {
                if (fragments.size() > 0) {
                    fragmentIndex++;
                    selectFragment(fragments.get(0));

                } else {
                    openHelpFragment();
                }
            } else {
                selectFragment(fragments.get(fragmentIndex));
            }
        }catch(Exception e){
            SDlog.log(e);
        }
    }
    public void onCloseSetting(){
        setting();
    }
    /**
     * 設定を反映させる
     */
    private void setting(){
        try {
            final float scale=getResources().getDisplayMetrics().density;
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
            int textSize = Integer.parseInt(spf.getString("textsize2", "30"));
            if (textSize > 0 && textSize < 100) {
                AOdiaDefaultView.setTextSize((int)(textSize/3.0f*scale));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeMenu(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(fragmentIndex>0){
                fragmentIndex--;
                System.out.println("fragment="+fragmentIndex+",max="+fragments.size());
                if(fragmentIndex<=0){
                    fragmentIndex=0;
                }
                selectFragment(fragments.get(fragmentIndex));
                findViewById(R.id.backFragment).setVisibility(fragmentIndex==0?View.INVISIBLE:View.VISIBLE);
                findViewById(R.id.proceedFragment).setVisibility(fragmentIndex<fragments.size()-1?View.VISIBLE:View.INVISIBLE);
                return true;
            }
            return false;
        }else{
            return false;
        }
    }
}
