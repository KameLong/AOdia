package com.fc2.web.kamelong.aodia;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.fc2.web.kamelong.aodia.detabase.DBHelper;
import com.fc2.web.kamelong.aodia.diagram.DiagramFragment;
import com.fc2.web.kamelong.aodia.diagram.DiagramSetting;
import com.fc2.web.kamelong.aodia.menu.MenuFragment;
import com.fc2.web.kamelong.aodia.netgram.NetgramActivity;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.file.FileSelectionDialog;
import com.fc2.web.kamelong.aodia.oudia.OuDiaDiaFile;
import com.fc2.web.kamelong.aodia.stationInfo.StationInfoFragment;
import com.fc2.web.kamelong.aodia.stationInfo.StationInfoIndexFragment;
import com.fc2.web.kamelong.aodia.timeTable.KLView;
import com.fc2.web.kamelong.aodia.timeTable.TimeTableFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
 * あと、これは強制というわけではないですが、このソースコードを利用したときは、
 * 作者に一言メールなりで連絡して欲しいなと思ってます。
 * こちらが全く知らないところで使われていたりするのは、ちょっと気分悪いですよね。
 * まあ、強制はできないので、皆さんの良識におまかせします。
 */
/**
 * AOdiaのメインアクティビティー。
 * 起動時に呼ばれるアクティビティー。
 * 表示する各Fragmentページはアクティビティーが管理する。
 *アクティビティーはアプリ起動中は破棄されないため、アプリ起動中に失われたくないデータは全てアクティビティーが保持する。
 */
public class MainActivity extends AppCompatActivity
        implements FileSelectionDialog.OnFileSelectListener {
    public boolean v10=true;
    boolean chooseContainer=false;
    boolean showMiniTitle=true;
    boolean showContainer=true;
    public IInAppBillingService mService;
    public ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

        /**
     * ダイヤデータを保持する。
     * 将来の拡張のため複数のダイヤを保持できるようにしておく。
     *
     * 将来的には複数ダイヤのオープン、クローズも対応できるようにしたい。
     */
    public ArrayList<DiaFile> diaFiles=new ArrayList<DiaFile>();
    public ArrayList<Integer> diaFilesIndex=new ArrayList<Integer>();
    MenuFragment menuFragment;
    public DiagramSetting diagramSetting=new DiagramSetting(this);
    private boolean tabletStyle=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String filePath="";
        int diaNum = 0;
        int direct = 0;
        super.onCreate(savedInstanceState);
        DBHelper db = new DBHelper(this);
        try{
            filePath=db.getRecentFilePath();
            diaNum=db.getRecentDiaNum();
            direct=db.getRecentDirect();
        }catch(Exception e){
            SdLog.log(e);
        }
        if(tabletStyle){
            setContentView(R.layout.activity_main_tablet);
        }else{
            setContentView(R.layout.activity_main);
        }


        final GestureDetector gesture2 = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        changeWindowSize();
                        return true;
                    }
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent motionEvent) {
                    }
                    @Override
                    public void onLongPress(MotionEvent motionEvent) {
                    }
                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                        if(!changeWindowSize)return true;
                        FrameLayout movingFrame=(FrameLayout)findViewById(R.id.movingFrame);
                        ViewGroup.LayoutParams lp = movingFrame.getLayoutParams();
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
                        int leftMargin=mlp.leftMargin-(int)vx;
                        int topMargin=mlp.topMargin-(int)vy;
                        if(leftMargin<0){
                            leftMargin=0;
                        }
                        if(leftMargin>getResources().getDisplayMetrics().widthPixels){
                            leftMargin=getResources().getDisplayMetrics().widthPixels;
                        }
                        if(topMargin<0){
                            topMargin=0;
                        }
                        if(topMargin>getResources().getDisplayMetrics().heightPixels){
                            topMargin=getResources().getDisplayMetrics().heightPixels;
                        }
                        mlp.setMargins(leftMargin,topMargin, mlp.rightMargin, mlp.bottomMargin);
                        //マージンを設定
                        movingFrame.setLayoutParams(mlp);
                        findViewById(R.id.layout1).setLayoutParams(new RelativeLayout.LayoutParams(leftMargin,topMargin));
                        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        spf.edit().putString("width", ""+leftMargin).apply();
                        spf.edit().putString("height", ""+topMargin).apply();

                        return false;
                    }
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                        return false;
                    }
                });
        findViewById(R.id.dragLayuout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture2.onTouchEvent(event);
            }
        });








        setting2();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final GestureDetector gesture = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        if(chooseContainer){

                        }else{
                            if(showMiniTitle){
                                hiddenMiniTitle();
                                showMiniTitle=false;
                            }else{
                                showMiniTitle();
                                showMiniTitle=true;
                            }
                        }
                        return true;
                    }
                    @Override
                    public void onLongPress(MotionEvent motionEvent) {
                        Log.d("test","longPress");
                    }
                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float vx, float vy) {
                        return false;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
                        return false;
                    }
                });
        findViewById(R.id.toolbar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        View.OnClickListener frameTitleClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chooseContainer){

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    KLFragment fragment;
                    fragment=(KLFragment)fragmentManager.findFragmentById(R.id.container);
                    fragmentTransaction.remove(fragment).commit();

                    fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction().remove(fragment).commit();
                    fragmentManager.executePendingTransactions();
                    fragmentManager.beginTransaction().replace(((LinearLayout)view.getParent().getParent()).getChildAt(1).getId(),fragment).commit();
                    hiddenContainer(255);
                    chooseContainer=false;
                    showContainer=false;
                    ((TextView)view).setText(fragment.fragmentName());
                    invalidateOptionsMenu();
                }
            }};
        View.OnClickListener closeButtonClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(((LinearLayout) view.getParent().getParent()).getChildAt(1).getId());
                    if(fragment==null)return;
                    fragmentTransaction.remove(fragment).commit();
                    ((TextView)((LinearLayout) view.getParent()).getChildAt(1)).setText("ここにウインドウを移動できます");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        findViewById(R.id.frameTitle1).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle2).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle3).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.frameTitle4).setOnClickListener(frameTitleClickListener);
        findViewById(R.id.button1).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button2).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button3).setOnClickListener(closeButtonClickListener);
        findViewById(R.id.button4).setOnClickListener(closeButtonClickListener);

        //FroatActionButton has no function
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        try{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            menuFragment = new MenuFragment();
            fragmentTransaction.replace(R.id.menu, menuFragment);
            fragmentTransaction.commit();
        }
        catch(Exception e){
            SdLog.log(e);
        }
        if(!tabletStyle) {
            // drawer
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                public void onDrawerClosed(View view) {
                    menuFragment.createMenu();
                }

                public void onDrawerOpened(View drawerView) {

                }
            };
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }else{

        }
        createSample();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.container, helpFragment);
        fragmentTransaction.commit();
        showContainer(255);

        if (Build.VERSION.SDK_INT < 23) {
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
            }
        }

        if(filePath.length()>0&&new File(filePath).exists()){
            onFileSelect(new File(filePath));
            setFragment(0,diaNum,direct);
        }
        if(diaFiles.size()==0){
            diaFiles.add(new OuDiaDiaFile(this,null));
            diaFilesIndex.add(0);
            setFragment(0,0,0);
        }
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop(){
        String[] filePaths=new String[diaFilesIndex.size()];
        for(int i=0;i<diaFilesIndex.size();i++){
            filePaths[i]=diaFiles.get(diaFilesIndex.get(i)).getFilePath();
        }
        DBHelper db=new DBHelper(this);
        db.memoryFilePaths(filePaths);

        String[] windowData=new String[DBHelper.WINDOW_NUM];
        int[] ids=new int[]{R.id.container,R.id.container1,R.id.container2,R.id.container3,R.id.container4};
        for(int i=0;i<5;i++){
            try {
                if (getFragmentManager().findFragmentById(ids[i]) == null) {
                    windowData[i] = "";
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
                windowData[i]="";
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(HelpFragment.class.getName())) {
                windowData[i]=DBHelper.HELP;
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(CommentFragment.class.getName())) {
                windowData[i]=DBHelper.COMMENT+"-"+getDiaFileIndexNumByFragment(((CommentFragment)getFragmentManager().findFragmentById(ids[i])).diaFile);
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(StationInfoIndexFragment.class.getName())) {
                windowData[i]=DBHelper.STATION_TIME_INDEX+"-"+getDiaFileIndexNumByFragment(((StationInfoIndexFragment)getFragmentManager().findFragmentById(ids[i])).diaFile);
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(StationInfoFragment.class.getName())) {
                StationInfoFragment fragment=((StationInfoFragment)getFragmentManager().findFragmentById(ids[i]));
                windowData[i]=DBHelper.STATION_TIME_TABLE+"-"+getDiaFileIndexNumByFragment(fragment.diaFile)+"-"+fragment.diaNumber+"-"+fragment.direct+"-"+fragment.station;
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(DiagramFragment.class.getName())) {
                DiagramFragment fragment=((DiagramFragment)getFragmentManager().findFragmentById(ids[i]));
                windowData[i]=DBHelper.DIAGRAM+"-"+getDiaFileIndexNumByFragment(fragment.diaFile)+"-"+fragment.diaNumber;
                continue;
            }
            if(getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(TimeTableFragment.class.getName())) {
                TimeTableFragment fragment=((TimeTableFragment)getFragmentManager().findFragmentById(ids[i]));
                windowData[i]=DBHelper.LINE_TIME_TABLE+"-"+getDiaFileIndexNumByFragment(fragment.diaFile)+"-"+fragment.diaNumber+"-"+fragment.direct;
                continue;
            }

        }
        db.saveWindows(windowData);

        super.onStop();
    }
    private int getDiaFileIndexNumByFragment(DiaFile diaFile){
        for(int i=0;i<diaFilesIndex.size();i++){
            if(diaFiles.get(diaFilesIndex.get(i))==diaFile){
                return i;
            }
        }
        return 0;

    }
    /**
     * sample.oudをdataフォルダにコピーする
     */
    private void createSample() {
        File file = new File(getExternalFilesDir(null), "sample.oud");
        try {
            AssetManager assetManager = getAssets();
            ;
            InputStream is = assetManager.open("sample.oud");

            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }
    /**
     * 戻るボタンを押すとMenuDrawerを表示している際、閉じる
     */
    @Override
    public void onBackPressed() {
        if(tabletStyle){
            super.onBackPressed();
        }else{
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * R.menu.mainを右上のメニューボタンを押したときに開くよう設定する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(showContainer){
            menu.findItem(R.id.action_split_window).setIcon(R.drawable.four_display);
            menu.findItem(R.id.action_shrinking).setVisible(true);
            menu.findItem(R.id.action_change_window_size).setVisible(false);
        }else{
            menu.findItem(R.id.action_split_window).setIcon(R.drawable.spread);
            menu.findItem(R.id.action_shrinking).setVisible(false);
            menu.findItem(R.id.action_change_window_size).setVisible(true);
        }
        if(chooseContainer){
            menu.findItem(R.id.action_shrinking).setIcon(R.drawable.spread);
        }else{
            menu.findItem(R.id.action_shrinking).setIcon(R.drawable.shrinking);
        }
        return true;
    }

    /**
     * 設定を押したときの処理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            PreferenceSample preference=new PreferenceSample();
            try {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(tabletStyle){
                    FrameLayout menuFrame=(FrameLayout)findViewById(R.id.menu);
                    menuFrame.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    fragmentTransaction.replace(R.id.menu, preference);
                }else{
                    fragmentTransaction.replace(R.id.container, preference);
                }
                fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
                fragmentTransaction.commit();
                showContainer(255);

            } catch (Exception e) {
                SdLog.log(e);
            }
            return true;
        }
        if(id==R.id.action_change_window_size){
            changeWindowSize();
            return true;
        }
        if(id==R.id.action_shrinking){
            if(chooseContainer) {
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                showContainer(255);
                chooseContainer=false;
            }else{
                try {
                    KLFragment fragment = (KLFragment) getFragmentManager().findFragmentById(R.id.container);
                    fragment.fragmentName();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "このウインドウは分割できません", Toast.LENGTH_SHORT).show();
                    return true;
                }
                findViewById(R.id.backFragments).bringToFront();
                findViewById(R.id.backFragments).setBackgroundColor(Color.argb(0, 255, 255, 255));

                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(200, 200, 200, 200);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);

                showContainer(200);
                showMiniTitle();
                chooseContainer = true;

            }
            invalidateOptionsMenu();
            return true;

        }
        if(id==R.id.action_split_window){
            if(showContainer){
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                hiddenContainer(255);
                showContainer=false;
            }else{
                View frontFragment = findViewById(R.id.container);
                ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
                mlp.setMargins(0, 0, 0, 0);
                //マージンを設定
                frontFragment.setLayoutParams(mlp);
                showContainer(255);
                showContainer=true;
            }
            invalidateOptionsMenu();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (1 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FileSelectionDialog dlg = new FileSelectionDialog(this, this);
                dlg.show(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS));
            } else {
                // 拒否された
                Toast.makeText(this, "拒否された", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void openHelp(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HelpFragment helpFragment = new HelpFragment();
        fragmentTransaction.replace(R.id.container, helpFragment);
        fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
        fragmentTransaction.commit();
        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        showContainer(255);


    }
    public void openFile(){
        DBHelper db = new DBHelper(this);
        File beforeFile=new File(db.getRecentFilePath());
        db.close();
        if (Build.VERSION.SDK_INT < 23) {
            FileSelectionDialog dlg = new FileSelectionDialog(this, this);

            if(beforeFile.exists()) {
                dlg.show(new File(beforeFile.getParent()));
            }else{
                dlg.show(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS));
            }
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                final int REQUEST_CODE = 1;
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, REQUEST_CODE);
            } else {
                FileSelectionDialog dlg = new FileSelectionDialog(this, this);
                if(beforeFile.exists()) {
                    dlg.show(new File(beforeFile.getParent()));
                }else{
                    dlg.show(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS));
                }
            }
        }
    }
    public void connectNetgram(){
        Intent intent=new Intent();
        intent.setClass(this,NetgramActivity.class);
/*        if(dia.netgramUrl.length()>0){
            if(getFragmentManager().findFragmentByTag(MY_TAG_FRAGMENT).equals(timetable[0][0])){
                intent.putExtra("url", dia.netgramUrl+"/2/insert");
            }
            else if(getFragmentManager().findFragmentByTag(MY_TAG_FRAGMENT).equals(timetable[0][1])){
                intent.putExtra("url", dia.netgramUrl+"/1/insert");
            }else {
                intent.putExtra("url", dia.netgramUrl+"/detail");
            }
        }
        */
        startActivity(intent);
    }
    public void openComment(int fileNum){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CommentFragment comment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        comment.setArguments(args);
        fragmentTransaction.replace(R.id.container, comment);
        fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
        fragmentTransaction.commit();
        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        showContainer(255);


    }
    public void openStationTimeTableIndex(int fileNum){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StationInfoIndexFragment fragment=new StationInfoIndexFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
        fragmentTransaction.commit();
        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        showContainer(255);

    }
    public void openStationTimeTable(int fileNum,int diaNum,int direct,int station){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StationInfoFragment fragment=new StationInfoFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        args.putInt("diaN", diaNum);
        args.putInt("direct", direct);
        args.putInt("station",station);
        fragment.setArguments(args);


        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
        fragmentTransaction.commit();

        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        showContainer(255);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    public void resetDetabase(){

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("確認");
        alertDlg.setMessage("アプリ内部データを初期化します（oudファイルは消去されません）");
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDatabase(DBHelper.DETABASE_NAME);
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                    }
                });

        // 表示
        alertDlg.create().show();
    }

    public void onFileSelect(File file) {
        DiaFile dia=null;
        String filePath=file.getPath();
        try {
            if(filePath.endsWith(".oud")){
                dia= new OuDiaDiaFile(this, file);
            }
            if(file.isDirectory()){
                //for netgram
            }
            if(dia==null)return;
            DBHelper db=new DBHelper(this);
            db.addHistory(filePath);

            db.addNewFile(db.getWritableDatabase(),filePath,dia.getDiaNum());
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
            if(spf.getBoolean("item001",false)) {
                diaFiles.add(dia);
                diaFilesIndex.add(0, diaFiles.size() - 1);
            }else{
                if(diaFiles.size()>0){
                    killDiaFile(0,0);
                }
                diaFiles.add(dia);
                diaFilesIndex.add(0, diaFiles.size() - 1);
            }
        } catch (Exception e) {
            SdLog.log(e);
            Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_LONG).show();
        }
        setFragment(diaFilesIndex.get(0),0,0);
    }
    public void onFileListSelect(File[] files) {
        DBHelper db = new DBHelper(this);
        diaFiles.clear();
        diaFilesIndex.clear();
        DiaFile dia=null;
        for(int i=0;i<files.length;i++) {
            String filePath = files[i].getPath();
            try {
                if (filePath.endsWith(".oud")) {
                    dia = new OuDiaDiaFile(this, files[i]);
                }
                if (files[i].isDirectory()) {
                    //for netgram
                }
                if (dia == null) return;
                db.addHistory(filePath);
                db.addNewFile(db.getWritableDatabase(), filePath, dia.getDiaNum());
                diaFiles.add(dia);
                diaFilesIndex.add(diaFiles.size() - 1);
            } catch (Exception e) {
                SdLog.log(e);
                Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_LONG).show();
            }
        }
        String[] windowList=db.readWindows();
        int[] ids=new int[]{R.id.container,R.id.container1,R.id.container2,R.id.container3,R.id.container4};
        for(int i=windowList.length;i>1;i--){
            if(openFragment(windowList[i-1])){
                moveFragment(ids[0],ids[i-1]);
            }
        }
        openFragment(windowList[0]);
    }
    private boolean openFragment(String str){
        try {

            String[] strs = str.split("-");

            if (strs[0].equals(DBHelper.HELP)) {
                openHelp();
                return true;
            }
            if (strs[0].equals(DBHelper.COMMENT)) {
                openComment(Integer.parseInt(strs[1]));
                return true;
            }
            if (strs[0].equals(DBHelper.STATION_TIME_INDEX)) {
                openStationTimeTableIndex(Integer.parseInt(strs[1]));
                return true;
            }
            if (strs[0].equals(DBHelper.STATION_TIME_TABLE)) {
                openStationTimeTable(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]), Integer.parseInt(strs[4]));
                return true;
            }
            if (strs[0].equals(DBHelper.DIAGRAM)) {
                setFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 2);
                return true;
            }
            if (strs[0].equals(DBHelper.LINE_TIME_TABLE)) {
                setFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
                return true;
            }
            return false;
        }catch(Exception e){
            SdLog.log(e);
            return false;
        }

    }
    private void moveFragment(int fromId,int toId){
        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(fromId);
            fragmentTransaction.remove(fragment).commit();
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.executePendingTransactions();
            fragmentManager.beginTransaction().replace(toId, fragment).commit();
        }catch(Exception e){
            SdLog.log(e);
        }

    }
    public void setFragment(int fileNum,int diaNum, int direct) {
        try {
            if(direct<2){


                FragmentManager fragmentManager1 = getFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                Fragment fragment1=new TimeTableFragment();
                Bundle args1=new Bundle();
                args1.putInt("fileNum",fileNum);
                args1.putInt("diaN", diaNum);
                args1.putInt("direct", direct);
                fragment1.setArguments(args1);
                fragmentTransaction1.replace(R.id.container,fragment1);
                fragmentTransaction1.commit();
            }else{
                FragmentManager fragmentManager3 = getFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();

                Fragment fragment3=new DiagramFragment();

                Bundle args3=new Bundle();
                args3.putInt("fileNum",fileNum);
                args3.putInt("diaN", diaNum);
                fragment3.setArguments(args3);
                fragmentTransaction3.replace(R.id.container,fragment3);
                fragmentTransaction3.commit();
            }
            showContainer(255);


            switch(direct){
                case 0:
                    //appTextView.setTitle("下り時刻表");
                    break;
                case 1:
                    //appTextView.setTitle("上り時刻表");
                    break;
                case 2:
                    //appTextView.setTitle("ダイヤグラム");

                    break;


            }


        } catch (Exception e) {
            SdLog.log(e);
        }
        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }
    public void openTrainTime(int fileNum,int diaNum,int direct,int train){
        try {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            TimeTableFragment fragment=new TimeTableFragment();
            Bundle args=new Bundle();
            args.putInt("fileNum",fileNum);
            args.putInt("diaN", diaNum);
            args.putInt("direct", direct);
            args.putInt("trainNum", train);
            fragment.setArguments(args);
            fragmentTransaction.replace(R.id.container,fragment);

            fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
            fragmentTransaction.commit();
            fragment.goTrain(train);
            showContainer(255);
        } catch (Exception e) {
            SdLog.log(e);
        }
        if(!tabletStyle) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                try {
                    SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
                    int value = spf.getInt("textsize", 30);
                    int textSize = value;
                    if (textSize > 0 && textSize < 100) {
                    }
                    KLView.setTextSize(textSize);
                    setFragment(0,0, 0);
                } catch (Exception e) {
                    SdLog.log(e);
                }
                break;
        }
    }
    public void killDiaFile(int index,int menuIndex){
        int[] ids=new int[]{R.id.container,R.id.container1,R.id.container2,R.id.container3,R.id.container4};
        for(int i=0;i<ids.length;i++){
            try{
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(ids[i]);
                System.out.println(fragment.diaFile+","+diaFiles.get(index));
                if(fragment.diaFile==diaFiles.get(index)){
                    System.out.println("remove");
                    fragmentTransaction.remove(fragment).commit();

                }
            }catch(Exception e){

            }
        }
        diaFiles.remove(index);
        diaFilesIndex.remove(menuIndex);
        for(int i=0;i<diaFilesIndex.size();i++){
            if(diaFilesIndex.get(i)>index) {
                diaFilesIndex.set(i, diaFilesIndex.get(i) - 1);
            }
        }


        menuFragment.createMenu();
    }
    public void upDiaFile(int menuIndex){
        if(menuIndex==0)return;
        diaFilesIndex.add(menuIndex-1,diaFilesIndex.get(menuIndex));
        diaFilesIndex.remove(menuIndex+1);
        menuFragment.createMenu();

    }
    public void setting(){
        Intent intent=new Intent();
        intent.setClassName(this,MainActivity.class.getName());
        startActivity(intent);
    }
    public void setting2(){
        // 設定ファイルを閉じたとき
        try {
            final float scale=getResources().getDisplayMetrics().density;
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
            tabletStyle=spf.getBoolean("fixedMenu",false);
            int textSize = Integer.parseInt(spf.getString("textsize2", "30"));
            if (textSize > 0 && textSize < 100) {
                KLView.setTextSize((int)(textSize/3.0f*scale));
            }
            int width=Integer.parseInt(spf.getString("width", "540"));
            int height=Integer.parseInt(spf.getString("height", "960"));
            findViewById(R.id.layout1).setLayoutParams(new RelativeLayout.LayoutParams(width,height));

            FrameLayout movingFrame=(FrameLayout)findViewById(R.id.movingFrame);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)movingFrame.getLayoutParams();
            mlp.setMargins(width,height, mlp.rightMargin, mlp.bottomMargin);
            //マージンを設定
            movingFrame.setLayoutParams(mlp);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private Fragment recreateFragment(Fragment f)
    {
        try {
            Fragment.SavedState savedState = getFragmentManager().saveFragmentInstanceState(f);

            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        }
        catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }
    private void hiddenMiniTitle(){
        findViewById(R.id.titlelayout1).setVisibility(View.GONE);
        findViewById(R.id.titlelayout2).setVisibility(View.GONE);
        findViewById(R.id.titlelayout3).setVisibility(View.GONE);
        findViewById(R.id.titlelayout4).setVisibility(View.GONE);

    }
    private void showMiniTitle(){
        findViewById(R.id.titlelayout1).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout2).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout3).setVisibility(View.VISIBLE);
        findViewById(R.id.titlelayout4).setVisibility(View.VISIBLE);
    }
    private void hiddenContainer(int alfa){
        findViewById(R.id.backFragments).bringToFront();
        findViewById(R.id.backFragments).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container1).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container2).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container3).setBackgroundColor(Color.argb(alfa,255,255,255));
        findViewById(R.id.container4).setBackgroundColor(Color.argb(alfa,255,255,255));
        if(alfa==255) {
            View frontFragment = findViewById(R.id.container);
            ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.setMargins(0, 0, 0, 0);
            frontFragment.setLayoutParams(mlp);
        }
        changeWindowSize=false;
    }
    private void showContainer(int alfa){
        if(alfa==255){
            showContainer=true;
            chooseContainer=false;
            View frontFragment = findViewById(R.id.container);
            ViewGroup.LayoutParams lp = frontFragment.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) lp;
            mlp.setMargins(0, 0, 0, 0);
            frontFragment.setLayoutParams(mlp);
            invalidateOptionsMenu();

        }
        findViewById(R.id.container).bringToFront();
        findViewById(R.id.container).setBackgroundColor(Color.argb(alfa,255,255,255));
        changeWindowSize=false;
    }
    boolean changeWindowSize=false;
    private void changeWindowSize(){
        if(changeWindowSize){
            findViewById(R.id.backFragments).bringToFront();
            changeWindowSize=false;
        }else {
            findViewById(R.id.dragLayuout).bringToFront();
            changeWindowSize=true;
        }
    }

}
