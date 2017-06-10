package com.fc2.web.kamelong.aodia;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.Toast;

import com.fc2.web.kamelong.aodia.GTFS.GTFSListFragment;
import com.fc2.web.kamelong.aodia.detabase.DBHelper;
import com.fc2.web.kamelong.aodia.diagram.DiagramFragment;
import com.fc2.web.kamelong.aodia.menu.MenuFragment;
import com.fc2.web.kamelong.aodia.netgram.NetgramActivity;
import com.fc2.web.kamelong.aodia.oudia.DiaFile;
import com.fc2.web.kamelong.aodia.file.FileSelectionDialog;
import com.fc2.web.kamelong.aodia.oudia.OuDia2DiaFile;
import com.fc2.web.kamelong.aodia.GTFS.GTFSFile;
import com.fc2.web.kamelong.aodia.oudia.OuDiaDiaFile;
import com.fc2.web.kamelong.aodia.stationInfo.StationInfoFragment;
import com.fc2.web.kamelong.aodia.stationInfo.StationInfoIndexFragment;
import com.fc2.web.kamelong.aodia.timeTable.KLView;
import com.fc2.web.kamelong.aodia.timeTable.TimeTableFragment;

import org.json.JSONException;
import org.json.JSONObject;

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
 */
/**
 * AOdiaのメインアクティビティー。
 * 起動時に呼ばれるアクティビティー。
 * 表示する各Fragmentページはアクティビティーが管理する。
 *アクティビティーはアプリ起動中は破棄されないため、アプリ起動中に失われたくないデータは全てアクティビティーが保持する。
 */
public class MainActivity extends AppCompatActivity
        implements FileSelectionDialog.OnFileSelectListener {
    private Payment payment;
    /**
     * ダイヤデータを保持する。
     * ダイヤファイルをクローズするとArrayListの順番を詰めずに空白にする
     */
    public ArrayList<DiaFile> diaFiles=new ArrayList<DiaFile>();
    /**
     * MenuにおけるdiaFilesの並び順を定義する、数値インデックス。
     */
    public ArrayList<Integer> diaFilesIndex=new ArrayList<Integer>();


    MenuFragment menuFragment;
    Windows windows;
    int openId=R.id.container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //あらかじめ購入処理関係を起動
        payment=new Payment(this);
        //画面処理補助クラスを起動
        windows=new Windows(this);

        //MainActivityに用いるContentViewを設定
        //これはタブレットモードかどうかで用いるものが異なる
        if(windows.tabletStyle){
            setContentView(R.layout.activity_main_tablet);
        }else{
            setContentView(R.layout.activity_main);
        }
        setting();
        windows.windowsInit();



        //ツールバーの定義
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        if(!windows.tabletStyle) {
            //tabletスタイルでないときはメニューdrawerを実装する
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
        }

        createSample();//sample.oudを作成する

        //データベースより前回開いたデータを取得
        String filePath;
        int diaNum ;
        int direct = 0;
        DBHelper db = new DBHelper(this);
        try{
            filePath=db.getRecentFilePath();
            diaNum=db.getRecentDiaNum();
            direct=db.getRecentDirect();
            //前回のデータが存在するときは、そのファイルを開く
            if(filePath.length()>0&&new File(filePath).exists()){
                if(getStoragePermission()){
                onFileSelect(new File(filePath));
                setFragment(0,diaNum,direct);
                return;}
            }
        }catch(Exception e){
            SdLog.log(e);
        }
        //もし前回のデータが無ければsample.oudを開く
        diaFiles.add(new OuDiaDiaFile(this,null));
        diaFilesIndex.add(0);
        openHelp();
    }

    @Override
    public void onDestroy(){
        payment.close();
        super.onDestroy();
    }

    @Override
    public void onStop(){
        String[] filePaths = new String[diaFilesIndex.size()];
        for (int i = 0; i < diaFilesIndex.size(); i++) {
            filePaths[i] = diaFiles.get(diaFilesIndex.get(i)).getFilePath();
        }
        DBHelper db = new DBHelper(this);
        db.addFilePaths(filePaths);

        String[] windowData = new String[DBHelper.WINDOW_NUM];

        try {
            int[] ids = new int[]{R.id.container, R.id.container1, R.id.container2, R.id.container3, R.id.container4};
            for (int i = 0; i < 5; i++) {
                try {
                    if (getFragmentManager().findFragmentById(ids[i]) == null) {
                        windowData[i] = "";
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    windowData[i] = "";
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(HelpFragment.class.getName())) {
                    windowData[i] = DBHelper.HELP;
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(CommentFragment.class.getName())) {
                    windowData[i] = DBHelper.COMMENT + "-" + getDiaFileIndexNumByFragment(((CommentFragment) getFragmentManager().findFragmentById(ids[i])).diaFile);
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(StationInfoIndexFragment.class.getName())) {
                    windowData[i] = DBHelper.STATION_TIME_INDEX + "-" + getDiaFileIndexNumByFragment(((StationInfoIndexFragment) getFragmentManager().findFragmentById(ids[i])).diaFile);
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(StationInfoFragment.class.getName())) {
                    StationInfoFragment fragment = ((StationInfoFragment) getFragmentManager().findFragmentById(ids[i]));
                    windowData[i] = DBHelper.STATION_TIME_TABLE + "-" + getDiaFileIndexNumByFragment(fragment.diaFile) + "-" + fragment.diaNumber + "-" + fragment.direct + "-" + fragment.station;
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(DiagramFragment.class.getName())) {
                    DiagramFragment fragment = ((DiagramFragment) getFragmentManager().findFragmentById(ids[i]));
                    windowData[i] = DBHelper.DIAGRAM + "-" + getDiaFileIndexNumByFragment(fragment.diaFile) + "-" + fragment.diaNumber;
                    continue;
                }
                if (getFragmentManager().findFragmentById(ids[i]).getClass().getName().equals(TimeTableFragment.class.getName())) {
                    TimeTableFragment fragment = ((TimeTableFragment) getFragmentManager().findFragmentById(ids[i]));
                    windowData[i] = DBHelper.LINE_TIME_TABLE + "-" + getDiaFileIndexNumByFragment(fragment.diaFile) + "-" + fragment.diaNumber + "-" + fragment.direct;
                    continue;
                }


            }
        }catch(Exception e){
            SdLog.log(e);
        }
        db.saveWindows(windowData);
        super.onStop();
    }

    /**
     * sample.oudをdataフォルダにコピーする
     */
    private void createSample() {
        File file = new File(getExternalFilesDir(null), "sample.oud");
        if(file.exists()){
            //return;
        }
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
     *
     * 理想としては戻るボタンを押すと操作が一つ戻るようにしたいが現状そこまではできていない
     */
    @Override
    public void onBackPressed() {

        if(windows.tabletStyle){
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
        if(windows.showContainer){
            menu.findItem(R.id.action_split_window).setIcon(R.drawable.four_display);
            menu.findItem(R.id.action_shrinking).setVisible(true);
            menu.findItem(R.id.action_change_window_size).setVisible(false);
        }else{
            menu.findItem(R.id.action_split_window).setIcon(R.drawable.spread);
            menu.findItem(R.id.action_shrinking).setVisible(false);
            menu.findItem(R.id.action_change_window_size).setVisible(true);
        }
        if(windows.chooseContainer){
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
            SettingFragment preference=new SettingFragment();
            try {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(windows.tabletStyle){
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
                windows.showContainer(255);

            } catch (Exception e) {
                SdLog.log(e);
            }
            return true;
        }
        if(windows.optionMenu(id)){
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
                Toast.makeText(this, "エラー：ファイルへのアクセスを許可してください", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * リクエストが返ってきたときに行う
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0://設定が終わった時
                try {
                    SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
                    int textSize = spf.getInt("textsize", 30);
                    if (textSize > 0 && textSize < 100) {
                        KLView.setTextSize(textSize);
                    }
                    setFragment(0,0, 0);
                } catch (Exception e) {
                    SdLog.log(e);
                }
                break;

            case 1001://購入操作が終わった時
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String productId = jo.getString("productId");
                        System.out.println(productId);
                        SharedPreferences spf=PreferenceManager.getDefaultSharedPreferences(this);
                        spf.edit().putBoolean("item001",true).commit();
                        onBackPressed();
                    }
                    catch (JSONException e) {

                        e.printStackTrace();
                    }
                } else {
                }
        }
    }
    /**
     * ストレージのパミッションを得ているかを確認する
     * もし取得されていればtrueを返す
     * もし取得されていなければ、取得画面を表示する
     */
    private boolean getStoragePermission(){
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                final int REQUEST_CODE = 1;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        }
    }
    /**
     * ファイルダイアログを開く
     * ストレージのパミッションを確認し、過去に開いたフォルダがあれば、
     * そのフォルダを基準にFileSelectionDialogを開く
     */
    public void openFileDialog(){
        DBHelper db = new DBHelper(this);
        File beforeFile=new File(db.getRecentFilePath());
        db.close();
        if(getStoragePermission()){
            FileSelectionDialog dlg = new FileSelectionDialog(this, this);

            if(beforeFile.exists()) {
                dlg.show(new File(beforeFile.getParent()));
            }else{
                dlg.show(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS));
            }
        }
    }


    /**
     * netgram関係に用いる
     * netgramがV2になれば使用する
     */
    public void connectNetgram(){
        Intent intent=new Intent();
        intent.setClass(this,NetgramActivity.class);
        startActivity(intent);
    }
    /**
     * ヘルプを開く
     */
    public void openHelp(){
        HelpFragment helpFragment = new HelpFragment();
        openFragment(helpFragment);
    }


    /**
     * コメント画面を開く。
     */
    public void openComment(int fileNum){
        CommentFragment comment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        openFragment(comment);
    }

    /**
     * 駅時刻の目次を開く。
     */
    public void openStationTimeTableIndex(int fileNum){
        StationInfoIndexFragment fragment=new StationInfoIndexFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        fragment.setArguments(args);
        openFragment(fragment);
    }

    /**
     * 駅時刻表を開く。
     */
    public void openStationTimeTable(int fileNum,int diaNum,int direct,int station){
        StationInfoFragment fragment=new StationInfoFragment();
        Bundle args = new Bundle();
        args.putInt("fileNum",fileNum);
        args.putInt("diaN", diaNum);
        args.putInt("direct", direct);
        args.putInt("station",station);
        fragment.setArguments(args);
        openFragment(fragment);
    }

    /**
     * ダイヤグラムを開く
     */
    public void openDiagram(int fileNum,int diaNum){
        Fragment fragment=new DiagramFragment();
        Bundle args=new Bundle();
        args.putInt("fileNum",fileNum);
        args.putInt("diaN", diaNum);
        fragment.setArguments(args);
        openFragment(fragment);

    }

    public void setFragment(int fileNum,int diaNum, int direct) {
        try {
            if(direct<2){
                openLineTimeTable(fileNum,diaNum,direct);
            }else{
                openDiagram(fileNum,diaNum);
            }

        } catch (Exception e) {
            SdLog.log(e);
        }
    }
    /**
     * 路線時刻表を開く
     */
    public void openLineTimeTable(int fileNum,int diaNum,int direct){
        openLineTimeTable(fileNum,diaNum,direct,-1);
    }

    /**
     * 路線時刻表を開いたのち
     * 指定列車まで移動する
     */
    public void openLineTimeTable(int fileNum, int diaNum, int direct, int train){
        try {
            TimeTableFragment fragment=new TimeTableFragment();
            Bundle args=new Bundle();
            args.putInt("fileNum",fileNum);
            args.putInt("diaN", diaNum);
            args.putInt("direct", direct);
            fragment.setArguments(args);
            openFragment(fragment);

            if(train!=-1){
                fragment.goTrain(train);

            }
        } catch (Exception e) {
            SdLog.log(e);
        }

    }
    public GTFSFile gtfs;
    public void openGTFSStationList(){
        GTFSListFragment gtfsFragment=new GTFSListFragment();
        openFragment(gtfsFragment);
    }

    /**
     * ファイル一つが選択された時の処理。
     *
     * @param file
     */
    public void onFileSelect(File file) {
        DiaFile diaFile=null;
        String filePath=file.getPath();
        try {
            if(filePath.endsWith(".oud")){
                diaFile= new OuDiaDiaFile(this, file);
            }
            if(filePath.endsWith(".oud2")) {
                diaFile = new OuDia2DiaFile(this, file);
            }
            if(filePath.endsWith(".ZIP")&&(BuildConfig.BUILD_TYPE.equals("beta")||BuildConfig.BUILD_TYPE.equals("debug"))){
                gtfs =new GTFSFile(this,filePath);
                openGTFSStationList();
            }
            if(file.isDirectory()){
                //for netgram
            }
            if(diaFile==null)return;//diaFileが生成されなければ処理を終了する。
            DBHelper db=new DBHelper(this);
            db.addHistory(filePath);
            db.addNewFileToLineData(filePath,diaFile.getDiaNum());
            db.addStation(diaFile.getStationNameList(),diaFile.getFilePath());
            if(payment.buyCheck("item001")) {
                diaFiles.add(diaFile);
                diaFilesIndex.add(0, diaFiles.size() - 1);
            }else{
                if(diaFiles.size()>0){
                    killDiaFile(0,0);
                }
                diaFiles.add(diaFile);
                diaFilesIndex.add(0, diaFiles.size() - 1);
            }
            setFragment(diaFilesIndex.get(0),0,0);//Fragmentをセットする
        } catch (Exception e) {
            SdLog.log(e);
            Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 複数ファイルが選択されたときの処理
     * @param files
     */
    public void onFileListSelect(File[] files) {
        DBHelper db = new DBHelper(this);
        diaFiles.clear();
        diaFilesIndex.clear();
        DiaFile dia=null;
        for(int i=0;i<files.length;i++) {
            String filePath = files[i].getPath();
            try {
                if (filePath.endsWith(".oud")||filePath.endsWith(".oud2")) {
                    dia = new OuDiaDiaFile(this, files[i]);
                }
                if (files[i].isDirectory()) {
                    //for netgram
                }
                if (dia == null) return;
                db.addHistory(filePath);
                db.addNewFileToLineData(filePath, dia.getDiaNum());
                diaFiles.add(dia);
                diaFilesIndex.add(diaFiles.size() - 1);
            } catch (Exception e) {
                SdLog.log(e);
                Toast.makeText(this, "ファイルの読み込みに失敗しました", Toast.LENGTH_LONG).show();
            }
        }
        String[] windowList=db.readWindows();
        int[] ids=new int[]{R.id.container,R.id.container1,R.id.container2,R.id.container3,R.id.container4};
        for(int i=0;i<5;i++){
            openId=ids[i];
            openFragment(windowList[i]);

        }
        openId=ids[0];
    }
    /**
     * diaFileが指定されたとき、そのdiaFileがどのDiaFIleIndexに対応するかを調べる
     * @param diaFile
     * @return
     */
    private int getDiaFileIndexNumByFragment(DiaFile diaFile){
        for(int i=0;i<diaFilesIndex.size();i++){
            if(diaFiles.get(diaFilesIndex.get(i))==diaFile){
                return i;
            }
        }
        return 0;

    }
    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private void openFragment(Fragment fragment){
        if(!windows.tabletStyle) {
            //もしメニューが開いていたら閉じる
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(openId, fragment);
        fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
        fragmentTransaction.commit();
        windows.showContainer(255);
    }

    /**
     * Fragmentを表す文字列からFragmentを生成し、contentViewにセットする。
     * @param str
     * @return
     */
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

    /**
     * fromIDに置かれたFragmentをtoIDに移動させる
     */
    public KLFragment moveFragment(int fromId,int toId){
        try {
            FragmentManager fragmentManager = getFragmentManager();
            KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(fromId);
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().remove(fragment).commit();
            fragmentManager.executePendingTransactions();
            fragmentManager.beginTransaction().replace(toId, fragment).commit();
            windows.hiddenContainer(255);
            return fragment;
        }catch(Exception e){
            SdLog.log(e);
        }
        return null;
    }

    /**
     * DiaFileを閉じる
     * DiaFileを閉じるときはリソースの解放とそのDiaFileを使用していたfragmentを閉じる動作が必要
     * @param index
     * @param menuIndex
     */
    public void killDiaFile(int index,int menuIndex){
        int[] ids=new int[]{R.id.container,R.id.container1,R.id.container2,R.id.container3,R.id.container4};
        for(int i=0;i<ids.length;i++){
            try{
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                KLFragment fragment = (KLFragment) fragmentManager.findFragmentById(ids[i]);
                System.out.println(fragment.diaFile+","+diaFiles.get(index));
                if(fragment.diaFile==diaFiles.get(index)){
                    fragmentTransaction.remove(fragment).commit();
                }
            }catch(Exception e){

            }
        }
        diaFiles.set(index,null);
        diaFilesIndex.remove(menuIndex);
        menuFragment.createMenu();
    }

    /**
     * DiaFileをメニュー上部に移動
     * メニューの並び順はdiaFilesIndexに依存するので。
     * diaFilesIndexのみ変更すればよい
     * @param menuIndex
     */
    public void upDiaFile(int menuIndex){
        if(menuIndex==0)return;
        diaFilesIndex.add(menuIndex-1,diaFilesIndex.get(menuIndex));
        diaFilesIndex.remove(menuIndex+1);
        menuFragment.createMenu();

    }
    /**
     * データベースを初期化する
     */
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

        alertDlg.create().show();
    }


    /**
     * 設定ファイルを閉じたとき
     *
     */
    public void onCloseSetting(){
        Intent intent=new Intent();
        intent.setClassName(this,MainActivity.class.getName());
        startActivity(intent);
    }

    /**
     * 設定を反映させる
     */
    public void setting(){
        try {
            final float scale=getResources().getDisplayMetrics().density;
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
            windows.tabletStyle=spf.getBoolean("fixedMenu",false);
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

}
