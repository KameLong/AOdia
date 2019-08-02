package com.kamelong.aodia.AOdiaIO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.OuDia.SimpleOudia;
import com.kamelong.aodia.R;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.SDlog;
import com.kamelong.tool.ShiftJISBufferedReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;

/**
 * ファイル選択のためのFragment
 */

public class FileSelectFragment extends AOdiaFragment {
    private Handler handler=new Handler();
    boolean tab2searchOpen=true;
    public String currentDirectoryPath="";

    public FileSelectFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer=inflater.inflate(R.layout.file_select_fragment, container, false);
        return fragmentContainer;
    }

    /**
     * ここではtabHostの初期化を行う
     * 各tabの初期化は別メソッドを用意すること
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
            tabHost.setup();
            TabHost.TabSpec spec;

            // Tab1
            spec = tabHost.newTabSpec("Tab1")
                    .setIndicator("端末ファイル")
                    .setContent(R.id.tab1);
            tabHost.addTab(spec);

            // Tab2
            spec = tabHost.newTabSpec("Tab2")
                    .setIndicator("OuDia\nデータベース")
                    .setContent(R.id.tab2);
            tabHost.addTab(spec);

            spec = tabHost.newTabSpec("Tab3")
                    .setIndicator("履歴")
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
    public void onStart(){
        super.onStart();
        initTab1();
        initTab2();
        initTab3();


    }

    /**
     * フォルダーViewerタブを初期設定する。
     * ExternalFilesDirsを検索して得られるフォルダリストをもとにルートフォルダリストを作成する。
     */
    private void initTab1(){
        Spinner spinner=(Spinner)findViewById(R.id.spinner);

        final File[] rootFolderList= getAOdiaActivity().getExternalFilesDirs(null);
        final ArrayList<File>rootFolder=new ArrayList<>();

        String[] rootFolderName=new String[rootFolderList.length+1];
        for(int i=0;i<rootFolderList.length;i++){
            rootFolder.add(rootFolderList[i]);
            if(rootFolderList[i]==null){
                rootFolderName[i] = (i + 1) + ":使用不可";
                continue;
            }
            if (Environment.isExternalStorageRemovable(rootFolderList[i])) {
                rootFolderName[i] = (i + 1) + ":SDカード";
            } else {
                rootFolderName[i] = (i + 1) + ":端末フォルダ";
            }
        }
        rootFolder.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        rootFolderName[rootFolderName.length-1] = (rootFolderName.length) + ":ダウンロードフォルダ";

        System.out.println(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS));

        ArrayAdapter<String>adapter=new ArrayAdapter<>(getAOdiaActivity(),android.R.layout.simple_spinner_item,rootFolderName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                if(rootFolder.get(position)!=null) {
                    tab1OpenFile(rootFolder.get(position).getPath());
                }else{
                    SDlog.toast("このフォルダは開けません");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        tab1OpenFile(rootFolderList[0].getPath());//初期設定

        //検索システム
        SearchView searchView=(SearchView) findViewById(R.id.stationSearch);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    File directory = new File(currentDirectoryPath);
                    try {
                        File[] files = directory.listFiles();
                        String[]filePath=new String[files.length];
                        ArrayList<String>[] stationList=new ArrayList[files.length];
                        for(int i=0;i<files.length;i++){
                            if(!files[i].isFile()){
                                continue;
                            }
                            SimpleOudia simpleOudia=new SimpleOudia(files[i]);
                            filePath[i]=files[i].getPath();
                            stationList[i]=simpleOudia.stationName;
                        }
                        getAOdiaActivity().database.addStation(stationList,filePath);
                    }catch (Exception e){
                        SDlog.log(e);
                    }
                }else{
                    tab1OpenFile(currentDirectoryPath);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length()==0){
                    tab1OpenFile(currentDirectoryPath);
                    return false;
                }
                try {
                    final ListView fileListView = (ListView) findViewById(R.id.fileList);
                    ArrayList<String> pathList = getAOdiaActivity().database.searchFileFromStation(s, currentDirectoryPath);
                    String[] pathList2=new String[pathList.size()];
                    for (int i = 0; i < pathList.size(); i++) {
                        pathList2[i]=currentDirectoryPath + "/" + pathList.get(i);
                    }
                    final FileListAdapter adapter = new FileListAdapter(getAOdiaActivity(), pathList2);

                    fileListView.setAdapter(adapter);

                    fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try {
                                tab1OpenFile(adapter.getItem(position).getPath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    SDlog.log(e);
                }

                return false;
            }
        });



    }

    /**
     * フォルダ内内ファイルリストを作成する
     * このメソッドを呼び出すとtab内のListViewを更新する。
     * @param directorypath 表示したいディレクトリ
     */
    private void tab1OpenFile(String directorypath){
        try {
            System.out.println("tab1Open");
            currentDirectoryPath=directorypath;
            File file=new File(directorypath);
            if (file.isDirectory()) {
                final ListView fileListView = (ListView) findViewById(R.id.fileList);
                final FileListAdapter adapter = new FileListAdapter(getAOdiaActivity(), file.getPath());


                fileListView.setAdapter(adapter);
                ((TextView)findViewById(R.id.pathView)).setText(file.getPath());

                fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            tab1OpenFile(adapter.getItem(position).getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (file.exists()) {
                if (file.getName().endsWith(".oud") || file.getName().endsWith(".oud2")) {
                    try{
                        getAOdiaActivity().openFile(file);
                    }catch (Exception e){
                        System.out.println("AOdia専用の処理です");
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getAOdiaActivity(), "この拡張子のファイルは開けません", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getAOdiaActivity(), "このファイルは削除された可能性があります。", Toast.LENGTH_SHORT).show();
            }
        }catch (FilePermException e){
            Toast.makeText(getAOdiaActivity(), "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show();
            getAOdiaActivity().storagePermission();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * OuDiaデータベース検索画面を初期設定する
     */
    private void initTab2(){
        final Button openButton=(Button)findViewById(R.id.openButton);
        final Button closeButton=(Button)findViewById(R.id.closeButton);
        //LinearLayout(R.id.title)をタッチしても検索部分を開閉できるようにする
        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(openButton.getVisibility()==View.VISIBLE){
                    openButton.callOnClick();
                }else{

                    closeButton.callOnClick();
                }
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButton.setVisibility(View.GONE);
                closeButton.setVisibility(View.VISIBLE);
                findViewById(R.id.search).setVisibility(View.VISIBLE);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openButton.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.GONE);
                findViewById(R.id.search).setVisibility(View.GONE);

            }
        });
        Button startSearch=(Button)findViewById(R.id.startSearch);
        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String stationName=((EditText)findViewById(R.id.stationInput)).getText().toString();
                final String keyword=((EditText)findViewById(R.id.keywordInput)).getText().toString();
                final boolean andSearch=((CheckBox)findViewById(R.id.andCheck)).isChecked();
                final String startYear=((EditText)findViewById(R.id.startYear)).getText().toString();
                final String endYear=((EditText)findViewById(R.id.endYear)).getText().toString();

                new AsyncTask<Void,Void,Void>() {
                    @Override
                    public Void doInBackground(Void... params) {
                        try {
                            String url = "https://kamelong.com/OuDiaDataBase/api/apiv1.php";
                            url += "?stationName=" + stationName;
                            url += "&keyword=" + keyword;
                            url += "&startYear=" + startYear;
                            url += "&endYear=" + endYear;
                            if (andSearch) {
                                url += "&andSearch=" + "1";
                            }
                            System.out.println(url);


                            URL con = new URL(url);
                            HttpsURLConnection connection = (HttpsURLConnection) con.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.connect();
                            if (connection.getResponseCode()== HttpURLConnection.HTTP_OK) {
                                // 通信に成功した
                                // テキストを取得する
                                final InputStream in = connection.getInputStream();
                                String encoding = connection.getContentEncoding();
                                if(null == encoding){
                                    encoding = "UTF-8";
                                }
                                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                                final BufferedReader bufReader = new BufferedReader(inReader);
                                String line = bufReader.readLine();
                                final JSONObject json=new JSONObject(line);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(!json.has("lineData")){
                                                json.put("lineData",new JSONArray());
                                            }
                                            tab2Result(json.getJSONArray("lineData"));
                                            ((TextView) findViewById(R.id.statesText)).setText("検索結果は" + json.getJSONArray("lineData").length() + "件です");
                                        }catch (JSONException e){
                                            //
                                            tab2Result(new JSONArray());
                                            ((TextView) findViewById(R.id.statesText)).setText("検索結果は" + 0+ "件です");
                                            e.printStackTrace();
                                        }
                                        ((Button) findViewById(R.id.closeButton)).callOnClick();
                                        return;
                                    }
                                });
                                bufReader.close();
                                inReader.close();
                                in.close();
                            }else{
                                Toast.makeText(getAOdiaActivity(),"検索エラー"+connection.getResponseCode(),Toast.LENGTH_SHORT).show();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        return null;

                    }
                }.execute();

            }
        });

    }

    /** 履歴などを開く画面を作る
     *
     */
    private void initTab3(){
       Button openKeepButton=(Button)findViewById(R.id.OpenKeep);
       openKeepButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               File keep=new File(getContext().getFilesDir()+"/keep.oud2");
               if(keep.exists()){
                   getAOdiaActivity().openFile(keep);
               }else{
                   SDlog.toast("KEEPに保存されているファイルがありません");
               }
           }
       });
        final ListView fileListView = (ListView) findViewById(R.id.HistoryList);
        try {
            final FileListAdapter adapter2 = new FileListAdapter(getAOdiaActivity(), getAOdiaActivity().database.getHistory());


            fileListView.setAdapter(adapter2);

            fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        tab1OpenFile(adapter2.getItem(position).getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            SDlog.log(e);
        }

    }
    private void tab2Result(JSONArray json) {
        ListView listView=(ListView)findViewById(R.id.databaseList);
        try {
            DatabaseListAdapter adapter = new DatabaseListAdapter(getAOdiaActivity(),json);
            listView.setAdapter(adapter);


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * ファイルにアクセスする権利がなかった時に使用するException
     * File.listFiles()を使用するときに発生するNullPointerExceptionはパーミッション不足なので
     * このエラーを返すこと
     */
    private class FilePermException extends Exception{}

    /**
     * ファイル一覧を表示するときのアダプター
     * position=0の部分は親フォルダーへの遷移を担うので別処理にすること
     * 参考URL:http://android.keicode.com/basics/ui-listview.php
     */
    private class FileListAdapter extends BaseAdapter {
        ArrayList<File> fileList = new ArrayList<>();
        LayoutInflater layoutInflater = null;
        Context context = null;

        private int fileValue(File a) {
            if (a.isDirectory()) {
                return 10000;
            } else if (a.getPath().endsWith(".oud") || a.getPath().endsWith(".oud2")) {
                return 0;
            } else {
                return -1;
            }

        }

        public FileListAdapter(Context context, String directoryPath) throws FilePermException {
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            File directory = new File(directoryPath);
            try {
                File[] files = directory.listFiles();
                Comparator<File> comparator = new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if(o1.isDirectory()){
                            if(o2.isDirectory()){
                                return o1.getName().compareTo(o2.getName());
                            }
                            return -1;
                        }
                        if(!o1.getName().endsWith("oud")&&!o1.getName().endsWith("oud2")){
                            if(!o2.isDirectory()&&!o2.getName().endsWith("oud")&&!o2.getName().endsWith("oud2")){
                                return o1.getName().compareTo(o2.getName());
                            }
                            return 1;
                        }
                        if(o2.isDirectory()){
                            return 1;
                        }
                        if(!o2.getName().endsWith("oud")&&!o2.getName().endsWith("oud2")){
                            return -1;
                        }
                        return o1.getName().compareTo(o2.getName());
                    }
                };

                Arrays.sort(files,comparator);
                fileList=new ArrayList<>(Arrays.asList(files));
            } catch (NullPointerException e) {

                throw new FilePermException();
            }
            fileList.add(0, new File(new File(directoryPath).getParent()));
        }
        public FileListAdapter(Context context, String[] filePathList) throws FilePermException {
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            try {
                fileList=new ArrayList<>();
                for(int i=0;i<filePathList.length;i++){
                    fileList.add(new File(filePathList[i]));
                }
            } catch (NullPointerException e) {

                throw new FilePermException();
            }
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public File getItem(int i) {
            return fileList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.file_select_file_list, parent, false);

            ((TextView) convertView.findViewById(R.id.fileName)).setText(fileList.get(position).getName());
            ((TextView) convertView.findViewById(R.id.stationName)).setText(stationName(fileList.get(position)));
            if (fileList.get(position).getName().endsWith(".oud")
                    || fileList.get(position).getName().endsWith(".oud2")
                    ) {
                ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.dia_icon);
                ImageView deleteButton=convertView.findViewById(R.id.deleteButton);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println(fileList.get(position).getPath());
                        new AlertDialog.Builder(getActivity())
                                .setTitle("ファイル削除")
                                .setMessage(fileList.get(position).getName()+"のダイヤデータを削除します")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!fileList.get(position).delete()){
                                            Toast.makeText(getContext(),"ファイルを削除できませんでした",Toast.LENGTH_LONG).show();
                                        }
                                        tab1OpenFile(currentDirectoryPath);

                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
            }else if (position == 0) {
                ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.back_to_up);
                ((TextView) convertView.findViewById(R.id.fileName)).setText("上のフォルダ");
            } else if (fileList.get(position).isDirectory()) {
                ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.folder_icon);
            }


            return convertView;
        }

        private String stationName(File file) {
            if(!file.isFile())return "";
            if(file.getName().endsWith("oud")||file.getName().endsWith("oud2")){

            try {
                SimpleOudia diaFile = new SimpleOudia(file);
                if(diaFile.stationName.size()<2){
                    return "";
                }
                return diaFile.stationName.get(0) + "～" + diaFile.stationName.get(diaFile.stationName.size() - 1);
            }catch (Exception e){
                SDlog.log(e);
            }
            }

            return "";
        }
    }
    private class DatabaseListAdapter extends BaseAdapter{
        ArrayList<JSONObject>jsonArray=new ArrayList<>();
        LayoutInflater layoutInflater = null;
        Context context=null;
        public DatabaseListAdapter(Context context,JSONArray json) throws JSONException {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for(int i=0;i<json.length();i++){
                jsonArray.add(json.getJSONObject(i));
            }

        }

        @Override
        public int getCount() {
            return jsonArray.size();
        }

        @Override
        public Object getItem(int i) {
            jsonArray.get(i);
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.database_list,parent,false);

            try {
                ((TextView) convertView.findViewById(R.id.lineName)).setText(jsonArray.get(position).getString("lineName"));
                ((TextView) convertView.findViewById(R.id.user)).setText(jsonArray.get(position).getString("userName"));
                ((TextView) convertView.findViewById(R.id.type)).setText(jsonArray.get(position).getString("diaYear")+"年");
                Spinner keywordSpinner=convertView.findViewById(R.id.keyword);
                Spinner stationSpinner=convertView.findViewById(R.id.stationName);
                ArrayList<String>keywordList=new ArrayList<>();
                for(int i=0;i<jsonArray.get(position).getJSONArray("keyword").length();i++){
                    keywordList.add(jsonArray.get(position).getJSONArray("keyword").getString(i));
                }
                ArrayList<String>stationNameList=new ArrayList<>();
                for(int i=0;i<jsonArray.get(position).getJSONArray("stationName").length();i++){
                    stationNameList.add(jsonArray.get(position).getJSONArray("stationName").getString(i));
                }
                ArrayAdapter<String>keywordAdapter=new ArrayAdapter<>(getAOdiaActivity(),android.R.layout.simple_spinner_item,keywordList);
                keywordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                keywordSpinner.setAdapter(keywordAdapter);
                ArrayAdapter<String>stationAdapter=new ArrayAdapter<>(getAOdiaActivity(),android.R.layout.simple_spinner_item,stationNameList);
                stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stationSpinner.setAdapter(stationAdapter);
                convertView.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            final String url = "https://kamelong.com/OuDiaDataBase/files/"+jsonArray.get(position).getString("url");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL downloadURL = new URL(url);
                                        HttpURLConnection download = null;
                                        download = (HttpURLConnection) downloadURL.openConnection();
                                        download.connect();
                                        final DataInputStream DATA_INPUT = new DataInputStream(download.getInputStream());
                                        // 書き込み用ストリーム
                                        try {
                                            final FileOutputStream FILE_OUTPUT = new FileOutputStream(getAOdiaActivity().getExternalFilesDir(null).getPath() + "/" + jsonArray.get(position).getString("lineName") + ".oud");
                                            final DataOutputStream DATA_OUT = new DataOutputStream(FILE_OUTPUT);

                                        // 読み込みデータ単位
                                        final byte[] BUFFER = new byte[4096];
                                        // 読み込んだデータを一時的に格納しておく変数
                                        int readByte = 0;

                                        // ファイルを読み込む
                                        while ((readByte = DATA_INPUT.read(BUFFER)) != -1) {
                                            DATA_OUT.write(BUFFER, 0, readByte);
                                        }
                                        // 各ストリームを閉じる
                                        DATA_INPUT.close();
                                        FILE_OUTPUT.close();
                                        DATA_INPUT.close();
                                        download.getInputStream().close();
                                        }catch (Exception e){
                                            SDlog.log(e);
                                            SDlog.toast("原因不明のエラーが発生しました");
                                            return;
                                        }

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    (getAOdiaActivity()).openFile(new File(getAOdiaActivity().getExternalFilesDir(null).getPath() + "/" + jsonArray.get(position).getString("lineName") + ".oud"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }catch (MalformedURLException e) {

                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                            System.out.println(url);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

            }catch(JSONException e){
                e.printStackTrace();
            }

            return convertView;
        }
    }
    private class FileComparator implements Comparator<File> {

        //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
        public int compare(File a, File b) {
            int valueA=0;
            int valueB=0;
            if(a.isDirectory()){
                valueA=10000;
            }else if(a.getPath().endsWith(".oud")||a.getPath().endsWith(".oud2")||a.getPath().endsWith(".jpti")){
                long time=System.currentTimeMillis();
//                DBHelper db=new DBHelper(getAodiaActivity());
//                valueA=db.fileOpenedNum(a.getPath());
                System.out.println(System.currentTimeMillis()-time);
            }else{
                valueA=-1;
            }

            if(b.isDirectory()){
                valueB=10000;
            }else if(b.getPath().endsWith(".oud")||b.getPath().endsWith(".oud2")||b.getPath().endsWith(".jpti")){
                long time=System.currentTimeMillis();
//                DBHelper db=new DBHelper(getAodiaActivity());
//                valueB=db.fileOpenedNum(b.getPath());
                System.out.println(System.currentTimeMillis()-time);
            }else{
                valueB=-1;

            }

            if(valueA==valueB){
                return a.getPath().compareTo(b.getPath());
            }else {
                return  valueB-valueA;
            }
        }

    }
    public String fragmentName(){
        return "ファイル選択";
    }
    public String fragmentHash(){
        return "FileSelect";
    }

}
