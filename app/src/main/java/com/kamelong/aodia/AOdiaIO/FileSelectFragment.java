package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.aodia.AOdiaActivity;
import com.kamelong.aodia.R;
import com.kamelong.aodia.AOdiaFragment;
import com.kamelong.aodia.detabase.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * ファイル選択のためのFragment
 */

public class FileSelectFragment extends AOdiaFragment {
    Handler handler=new Handler();
    boolean tab2searchOpen=true;

    public FileSelectFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //Fragmentのレイアウトxmlファイルを指定し、メインのViewをfragmentContainerに代入する（つまり消すな）
        fragmentContainer = inflater.inflate(R.layout.file_select_fragment, container, false);
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

            // Tab3
            /*
            spec = tabHost.newTabSpec("Tab3")
                    .setIndicator("作成中")
                    .setContent(R.id.tab3);
            tabHost.addTab(spec);
            */

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


    }

    /**
     * フォルダーViewerタブを初期設定する。
     * ExternalFilesDirsを検索して得られるフォルダリストをもとにルートフォルダリストを作成する。
     */
    private void initTab1(){
        Spinner spinner=(Spinner)findViewById(R.id.spinner);

        final File[] rootFolderList=getActivity().getExternalFilesDirs(null);
        String[] rootFolderName=new String[rootFolderList.length];
        for(int i=0;i<rootFolderList.length;i++){
            if (Environment.isExternalStorageRemovable(rootFolderList[i])) {
                rootFolderName[i]=(i+1)+":SDカード";
            }else{
                rootFolderName[i]=(i+1)+":端末フォルダ";

            }
        }
        ArrayAdapter<String>adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,rootFolderName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                tab1OpenFile(rootFolderList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        tab1OpenFile(rootFolderList[0]);//初期設定



    }

    /**
     * フォルダ内内ファイルリストを作成する
     * このメソッドを呼び出すとtab内のListViewを更新する。
     * @param file 表示したいフォルダ
     */
    private void tab1OpenFile(File file){
        try {
            if (file.isDirectory()) {
                final ListView fileListView = (ListView) findViewById(R.id.fileList);
                final FileListAdapter adapter = new FileListAdapter(getActivity(), file.getPath());


                fileListView.setAdapter(adapter);
                ((TextView)findViewById(R.id.pathView)).setText(file.getPath());

                fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            tab1OpenFile(adapter.getItem(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (file.exists()) {
                if (file.getName().endsWith(".oud") || file.getName().endsWith(".oud2") || file.getName().endsWith("jpti")) {
                    try{
                        ((AOdiaActivity)getActivity()).onFileSelect(file);
                    }catch (Exception e){
                        System.out.println("AOdia専用の処理です");
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getActivity(), "この拡張子のファイルは開けません", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "このファイルは開けません", Toast.LENGTH_SHORT).show();
            }
        }catch (FilePermException e){
            Toast.makeText(getActivity(), "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show();
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
                                            if(!json.has("data")){
                                                json.put("data",new JSONArray());
                                            }
                                            tab2Result(json.getJSONArray("data"));
                                            ((TextView) findViewById(R.id.statesText)).setText("検索結果は" + json.getJSONArray("data").length() + "件です");
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
                                Toast.makeText(getActivity(),"検索エラー"+connection.getResponseCode(),Toast.LENGTH_SHORT).show();
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
    private void tab2Result(JSONArray json) {
        ListView listView=(ListView)findViewById(R.id.databaseList);
        try {
            DatabaseListAdapter adapter = new DatabaseListAdapter(getActivity(),json);
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
    private class FileListAdapter extends BaseAdapter{
        ArrayList<File> fileList=new ArrayList<>();
        LayoutInflater layoutInflater = null;
        Context context=null;
        private int fileValue(File a){
            if(a.isDirectory()){
                return 10000;
            }else if(a.getPath().endsWith(".oud")||a.getPath().endsWith(".oud2")||a.getPath().endsWith(".jpti")){
                DBHelper db=null;
                try {
                    db = new DBHelper(getActivity());
                    return db.fileOpenedNum(a.getPath());
                }finally {
                    if(db!=null){
                        db.close();
                    }
                }
            }else{
                return -1;
            }

        }
        public FileListAdapter(Context context,String directoryPath) throws FilePermException {
            this.context = context;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            File directory=new File(directoryPath);
            try {
                File[] files=directory.listFiles();
                Arrays.sort(files);
                Map<File,Integer>fileMap=new HashMap<>();
                for(File file:files){
                    fileMap.put(file,fileValue(file));
                }
                List<Map.Entry<File,Integer>> entries =
                        new ArrayList<>(fileMap.entrySet());
                Collections.sort(entries, new Comparator<Map.Entry<File,Integer>>() {

                    @Override
                    public int compare(
                            Map.Entry<File,Integer> entry1, Map.Entry<File,Integer> entry2) {
                        return (entry2.getValue()).compareTo(entry1.getValue());
                    }
                });
                for (Map.Entry<File,Integer> s : entries) {
                    fileList.add(s.getKey());
                }
            }catch (NullPointerException e){

                throw new FilePermException();
            }
            fileList.add(0,new File(new File(directoryPath).getParent()));
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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.file_select_file_list,parent,false);

            ((TextView)convertView.findViewById(R.id.fileName)).setText(fileList.get(position).getName());
            if(position==0){
                ImageView fileIcon=convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.back_to_up);
                ((TextView)convertView.findViewById(R.id.fileName)).setText("上のフォルダ");
            }
            else if(fileList.get(position).isDirectory()){
                ImageView fileIcon=convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.folder_icon);
            }
            else if(fileList.get(position).getName().endsWith(".oud")
                    ||fileList.get(position).getName().endsWith(".oud2")
                    ||fileList.get(position).getName().endsWith(".jpti"))
            {
                ImageView fileIcon=convertView.findViewById(R.id.fileIcon);
                fileIcon.setImageResource(R.drawable.dia_icon);
            }


            return convertView;        }
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
                ArrayAdapter<String>keywordAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,keywordList);
                keywordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                keywordSpinner.setAdapter(keywordAdapter);
                ArrayAdapter<String>stationAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,stationNameList);
                stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stationSpinner.setAdapter(stationAdapter);
                convertView.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            final String url = "https://kamelong.com/OuDiaDataBase/download.cgi?name="+jsonArray.get(position).getString("url")+"&title="+jsonArray.get(position).getString("lineName")+".oud";
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
                                        final FileOutputStream FILE_OUTPUT = new FileOutputStream(getActivity().getExternalFilesDir(null).getPath() + "/"+jsonArray.get(position).getString("lineName")+".oud");
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
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    ((AOdiaActivity) getActivity()).onFileSelect(new File(getActivity().getExternalFilesDir(null).getPath() + "/" + jsonArray.get(position).getString("lineName") + ".oud"));
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
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
    class FileComparator implements Comparator<File> {

        //比較メソッド（データクラスを比較して-1, 0, 1を返すように記述する）
        public int compare(File a, File b) {
            int valueA=0;
            int valueB=0;
            if(a.isDirectory()){
                valueA=10000;
            }else if(a.getPath().endsWith(".oud")||a.getPath().endsWith(".oud2")||a.getPath().endsWith(".jpti")){
                long time=System.currentTimeMillis();
                DBHelper db=new DBHelper(getActivity());
                valueA=db.fileOpenedNum(a.getPath());
                System.out.println(System.currentTimeMillis()-time);
            }else{
                valueA=-1;
            }

            if(b.isDirectory()){
                valueB=10000;
            }else if(b.getPath().endsWith(".oud")||b.getPath().endsWith(".oud2")||b.getPath().endsWith(".jpti")){
                long time=System.currentTimeMillis();
                DBHelper db=new DBHelper(getActivity());
                valueB=db.fileOpenedNum(b.getPath());
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
    @Override
    public String fragmentName(){
            return "ファイル選択";
    }
    @Override
    public String fragmentHash(){
            return "FileSelect";
    }

}
