package com.kamelong.aodia;
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
import android.database.sqlite.SQLiteCursor;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kamelong.GTFS.GTFS;
import com.kamelong.JPTI.JPTI;
import com.kamelong.OuDia.OuDiaFile;
import com.kamelong.aodia.AOdiaIO.FileSelectFragment;
import com.kamelong.aodia.AOdiaIO.ProgressDialog;
import com.kamelong.aodia.databaseNewService.EditServiceFragment;
import com.kamelong.aodia.detabase.DBHelper;
import com.kamelong.aodia.detabase.SQLData;
import com.kamelong.aodia.diadata.AOdiaOperation;
import com.kamelong.aodia.diadata.AOdiaService;
import com.kamelong.aodia.diagram.DiagramFragment;
import com.kamelong.aodia.diagram.TrainSelectDiagramFragment;
import com.kamelong.aodia.menu.MenuFragment;
import com.kamelong.aodia.diadata.AOdiaDiaFile;
import com.kamelong.aodia.operation.OperationFragment;
import com.kamelong.aodia.stationInfo.StationInfoFragment;
import com.kamelong.aodia.stationInfo.StationInfoIndexFragment;
import com.kamelong.aodia.timeTable.KLView;
import com.kamelong.aodia.timeTable.SelectTrainTimeTable;
import com.kamelong.aodia.timeTable.TimeTableFragment;
import com.kamelong.aodia.timeTable.TrainSelectListener;
import com.kamelong.aodia.timeTable.TrainTimeEditFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
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
 * アクティビティーはアプリ起動中は破棄されないため、アプリ起動中に失われたくないデータは全てアクティビティーが保持する。
 */
public class AOdiaActivity extends AppCompatActivity {

    private Payment payment;
    public Payment getPayment(){
        return payment;
    }
    public ArrayList<AOdiaService>serviceList=new ArrayList<>();
    public AOdiaService getService(int index){
        if(serviceList.size()==0){
            return null;
        }
        if(index>=0&&index<serviceList.size()){
            return serviceList.get(index);
        }
        return serviceList.get(0);
    }

    public ArrayList<Integer>serviceIndex=new ArrayList<>();

    /**
     * ダイヤデータを保持する。
     * ダイヤファイルをクローズするとArrayListの順番を詰めずに空白にする
     */
    public ArrayList<AOdiaDiaFile> diaFiles=new ArrayList<>();
    /**
     * MenuにおけるdiaFilesの並び順を定義する、数値インデックス。
     */
    public ArrayList<Integer> diaFilesIndex=new ArrayList<Integer>();


    /**
     * 開いているFragmentを保存する
     * fragmentsはFragmentを削除すると順番を詰める
     */
    private ArrayList<AOdiaFragmentInterface> fragments=new ArrayList<>();
    /**
     * 現在開いているFragmentのインデックス
     */
    private int fragmentIndex=-1;

    /**
     * 使用するMenuFragment
     */
    private MenuFragment menuFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //あらかじめ購入処理関係を起動
        payment=new Payment(this);
        SdLog.setActivity(this);
        //MainActivityに用いるContentViewを設定
        setContentView(R.layout.activity_main);
        setting();
        //Drawer初期化
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Button openDrawer=(Button)findViewById(R.id.Button2);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawer.isDrawerOpen(GravityCompat.START)){
                    drawer.openDrawer(GravityCompat.START);
                }else{
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        //メニュー初期化
        menuFragment=new MenuFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.menu,menuFragment);
        fragmentTransaction.commit();
        findViewById(R.id.backFragment).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.proceedFragment).setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.killFragment).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        killFragment(fragmentIndex);
                    }
                }
        );

        createSample();//sample.oudを作成する

        serviceList.add(new AOdiaService());
        AOdiaFragment fragment=new EditServiceFragment();
        Bundle args=new Bundle();
        args.putInt("serviceID",0);
        args.putInt("calendarID",1);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, fragment);
        if(true) {
            return;
        }

        // ファイル関連付けで開くことをできるようにする
        if(Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            String fname[] = String.valueOf(getIntent().getData()).split("//");
            if (fname.length == 2) {
                // 「file:」と「/～.gpx」に分けられたと仮定
                // 受け取ったファイル名をオープンして読み込んで表示する
                try{
                    String filePath= URLDecoder.decode(fname[1], "UTF-8");;
                    //前回のデータが存在するときは、そのファイルを開く
                    System.out.println(filePath);
                    if(filePath.length()>0&&new File(filePath).exists()){
                        if(getStoragePermission()){
                            onFileSelect(new File(filePath));
                            openDiaOrTimeFragment(0,0,0);
                            return;
                        }
                    }
                }catch(Exception e){
                    SdLog.log(e);
                }
            }
        }
        else {
            // 通常起動の時
            //データベースより前回開いたデータを取得

            try{
                SharedPreferences preference=getSharedPreferences("AOdiaPreference",MODE_PRIVATE);
                String filePath=preference.getString("RecentFilePath","");
                int diaNum=preference.getInt("RecentDiaNum",0);
                int direct=preference.getInt("RecentDirect",0);
                //前回のデータが存在するときは、そのファイルを開く
                if(filePath.length()>0&&new File(filePath).exists()){
                    if(getStoragePermission()){
                        onFileSelect(new File(filePath));
//                        openDiaOrTimeFragment(0,diaNum,direct);
                        return;
                    }
                }
            }catch(Exception e){
                SdLog.log(e);
            }
        }
        //もし前回のデータが無ければsample.oudを開く
//        diaFiles.add(new AOdiaDiaFile(this));
// diaFilesIndex.add(0);
        //全開のデータがない場合はsampleを開いたうえでヘルプを開く
        openHelp();
    }

    @Override
    public void onDestroy(){
        payment.close();
        super.onDestroy();
    }

    @Override
    public void onStop(){
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            findViewById(R.id.backFragment).callOnClick();
        }
    }


    /**
     * 設定を押したときの処理
     */
    public void openSetting(){
        SettingFragment preference=new SettingFragment();
        openFragment(preference);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SettingFragment preference=new SettingFragment();
            try {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, preference);
                fragmentTransaction.addToBackStack(null); // 戻るボタンでreplace前に戻る
                fragmentTransaction.commit();
            } catch (Exception e) {
                SdLog.log(e);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (1 == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
                    openDiaOrTimeFragment(0,0, 0);
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
     * ファイル一つが選択された時の処理。
     *
     * @param file
     */
    public void onFileSelect(final File file) {
        final ProgressDialog dialog=new ProgressDialog();
        dialog.show(getFragmentManager(), "test");
        final Handler handler=new Handler();
        if(file.getPath().endsWith(".db")){
            SQLData sq=new SQLData(this,file);
            return;


        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                AOdiaDiaFile diaFile=null;
                final String filePath=file.getPath();
                try {
                    if(filePath.endsWith(".oud")||filePath.endsWith(".oud2")){
                        OuDiaFile oudia=new OuDiaFile(file);
                        JPTI jpti=new JPTI(oudia);
                        diaFile=new AOdiaDiaFile(AOdiaActivity.this,jpti,jpti.getService(0),filePath);
                        diaFile.setFilePath(filePath);
                    }
                    if(filePath.endsWith(".jpti")){
                        JPTI jpti=new JPTI(file,handler,dialog);
                        dialog.dismiss();
                        diaFile=new AOdiaDiaFile(AOdiaActivity.this,jpti,jpti.getService(0),filePath);
                        diaFile.setFilePath(filePath);
                    }
                    if(filePath.endsWith(".zip")){
                        GTFS gtfs=new GTFS(AOdiaActivity.this,file);
                        gtfs.load();
                        JPTI jpti=gtfs.makeJPTI();
                        diaFile=new AOdiaDiaFile(AOdiaActivity.this,jpti,jpti.getService(0),filePath);
                        diaFile.setFilePath(filePath);
                    }
                    dialog.dismiss();
                    if(diaFile==null)return;//diaFileが生成されなければ処理を終了する。
                    final AOdiaDiaFile finalDiaFile=diaFile;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            DBHelper db=new DBHelper(AOdiaActivity.this);
                            db.addHistory(filePath);
                            db.addNewFileToLineData(filePath,finalDiaFile.getDiaNum());
                            diaFiles.add(finalDiaFile);
                            diaFilesIndex.add(0, diaFiles.size() - 1);
                            menuFragment.createMenu();
                            openDiaOrTimeFragment(diaFilesIndex.get(0),0,0);//Fragmentをセットする

                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SdLog.log(e);

                            Toast.makeText(AOdiaActivity.this, "ファイルの読み込みに失敗しました", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();

    }
    /**
     * 複数ファイルが選択されたときの処理
     * @param files
     */
    public void onFileListSelect(File[] files) {
        for(int i=0;i<files.length;i++){
            onFileSelect(files[files.length-1-i]);
        }
    }
    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private void openFragment(AOdiaFragmentInterface fragment){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment.getFragment());
        fragmentTransaction.commit();
        fragmentIndex++;
        fragments.add(fragmentIndex,fragment);
        findViewById(R.id.backFragment).setVisibility(fragmentIndex==0?View.INVISIBLE:View.VISIBLE);
        findViewById(R.id.proceedFragment).setVisibility(fragmentIndex<fragments.size()-1?View.VISIBLE:View.INVISIBLE);


    }
    /**
     * 任意のFragmentをcontainerに開きます。
     * @param fragment
     */
    private void selectFragment(AOdiaFragmentInterface fragment){
        //もしメニューが開いていたら閉じる
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment.getFragment());
        fragmentTransaction.commit();

    }
    /**
     * DiaFileを閉じる
     * DiaFileを閉じるときはリソースの解放とそのDiaFileを使用していたfragmentを閉じる動作が必要
     * @param index
     * @param menuIndex
     */
    public void killDiaFile(int index,int menuIndex){
        for(int i=0;i<fragments.size();i++){
            try{
                AOdiaFragment fragment=(AOdiaFragment)fragments.get(i);
                if(fragment.diaFile==diaFiles.get(index)){
                    killFragment(i);
                    i--;
                }
            }catch (Exception e) {
            }
        }
        diaFiles.set(index,null);
        diaFilesIndex.remove(menuIndex);
        menuFragment.createMenu();

    }

    /**
     * 指定されたIndexのFragmentをkillする
     * @param index
     */
    private void killFragment(int index){
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
                    openHelp();
                }
            } else {
                selectFragment(fragments.get(fragmentIndex));
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    /**
     */
    public void killFragment(Fragment fragment){
        killFragment(fragments.indexOf(fragment));

    }




    /**
     * ファイルダイアログを開く
     */
    public void openFileDialog(){
        if(getStoragePermission()) {
            AOdiaFragment fragment = new FileSelectFragment();
            openFragment(fragment);
        }
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
        comment.setArguments(args);
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
    private void openDiagram(int fileNum, int diaNum){

        AOdiaFragment fragment=new DiagramFragment();
        Bundle args=new Bundle();
        args.putInt("fileNum",fileNum);
        args.putInt("diaN", diaNum);
        fragment.setArguments(args);
        openFragment(fragment);


    }

    public void openDiaOrTimeFragment(int fileNum, int diaNum, int direct) {
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
    private void openLineTimeTable(int fileNum, int diaNum, int direct){
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
    /**
     * 運用表を開く
     */
    public void openOperationFragment(int fileNum,int diaNum,int operationNum){
        try {
            OperationFragment fragment=new OperationFragment();
            Bundle args=new Bundle();
            args.putInt("fileNum",fileNum);
            args.putInt("diaNum", diaNum);
            fragment.setArguments(args);
            openFragment(fragment);
        } catch (Exception e) {
            SdLog.log(e);
        }

    }


    /**
     * diaFileが指定されたとき、そのdiaFileがどのDiaFIleIndexに対応するかを調べる
     * @param diaFile
     * @return
     */
    private int getDiaFileIndexNumByFragment(AOdiaDiaFile diaFile){
        for(int i=0;i<diaFilesIndex.size();i++){
            if(diaFiles.get(diaFilesIndex.get(i))==diaFile){
                return i;
            }
        }
        return 0;

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
                openDiaOrTimeFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 2);
                return true;
            }
            if (strs[0].equals(DBHelper.LINE_TIME_TABLE)) {
                openDiaOrTimeFragment(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
                return true;
            }
            return false;
        }catch(Exception e){
            SdLog.log(e);
            return false;
        }

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
                        intent.setClass(AOdiaActivity.this,AOdiaActivity.class);
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
        intent.setClassName(this,AOdiaActivity.class.getName());
        startActivity(intent);
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
                KLView.setTextSize((int)(textSize/3.0f*scale));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void openSelectTrainTimeTable(int fileNum, int diaNum, int direct, TrainSelectListener listener){
        try {
            SelectTrainTimeTable fragment=new SelectTrainTimeTable();
            Bundle args=new Bundle();
            args.putInt("fileNum",fileNum);
            args.putInt("diaN", diaNum);
            args.putInt("direct", direct);
            fragment.setArguments(args);
            openFragment(fragment);
            fragment.setTrainSelectListener(listener);
        } catch (Exception e) {
            SdLog.log(e);
        }

    }
    public void openSelectDiagram(int fileNum, int diaNum, TrainSelectListener listener,AOdiaOperation operation){
        try {
            TrainSelectDiagramFragment fragment=new TrainSelectDiagramFragment();
            Bundle args=new Bundle();
            args.putInt("fileNum",fileNum);
            args.putInt("diaN", diaNum);
            fragment.setArguments(args);
            openFragment(fragment);
            fragment.setOnTrainSelectListener(listener,operation);
        } catch (Exception e) {
            SdLog.log(e);
        }

    }

    public void saveFile(){
        try {
            final AOdiaDiaFile saveFile = fragments.get(fragments.size() - 1).getDiaFile();
            final Handler handler=new Handler();
            saveFile.saveAOdia();
            System.out.println(fragments.get(fragments.size() - 1));
            final File outFile = new File(saveFile.getFilePath().substring(0, saveFile.getFilePath().lastIndexOf(".")) + ".jpti");
            final ProgressDialog dialog=new ProgressDialog();
            dialog.show(getFragmentManager(), "test");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveFile.getJPTI().makeJSONdata(outFile,handler,dialog);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }).start();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }catch (Exception e){
            SdLog.log(e);
            Toast.makeText(this, "ファイルを保存時にエラーが発生しました。", Toast.LENGTH_LONG).show();


        }


    }

    public void openTrainEdit(){
        AOdiaFragment fragment=new TrainTimeEditFragment();
        openFragment(fragment);
    }


}
