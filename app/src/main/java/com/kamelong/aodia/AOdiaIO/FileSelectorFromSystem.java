package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.OuDia.SimpleOuDia;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.io.File;
import java.util.ArrayList;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * 端末内からファイルを開くときに使うView
 */
public class FileSelectorFromSystem extends LinearLayout implements OpenDirectory{
    public String currentDirectoryPath="";

    public FileSelectorFromSystem(Context context) {
        this(context,null);
    }

    public FileSelectorFromSystem(final Context context, AttributeSet attr) {
        super(context,attr);
        LayoutInflater.from(context).inflate(R.layout.fileselector_terminal, this);
        //ルートディレクトリ選択スピナー
        Spinner spinner=findViewById(R.id.spinner);
        //AOdia内部ストレージの数＋ダウンロードフォルダ
        final File[] rootFolderList= getContext().getExternalFilesDirs(null);
        final ArrayList<File> rootFolder=new ArrayList<>();
        String[] rootFolderName=new String[rootFolderList.length+1];
        for(int i=0;i<rootFolderList.length;i++){
            rootFolder.add(rootFolderList[i]);
            if(rootFolderList[i]==null){
                rootFolderName[i] = (i + 1) + ":使用不可";
                continue;
            }
            if (Environment.isExternalStorageRemovable(rootFolderList[i])) {
                rootFolderName[i] = (i + 1) + ":"+getContext().getString(R.string.SDcard);
            } else {
                rootFolderName[i] = (i + 1) + ":"+getContext().getString(R.string.inDevice);
            }
        }
        rootFolder.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        rootFolderName[rootFolderName.length-1] = (rootFolderName.length) + ":ダウンロードフォルダ";
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,rootFolderName);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(rootFolder.get(position)!=null) {
                    openDirectory(rootFolder.get(position).getPath());
                }else{
                    SDlog.toast("このフォルダは開けません");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        openDirectory(rootFolderList[0].getPath());//初期設定

        //検索システム実装
        SearchView searchView=findViewById(R.id.stationSearch);
        searchView.setOnQueryTextFocusChangeListener((view, b) -> {
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
                        SimpleOuDia simpleOudia=new SimpleOuDia(files[i]);
                        filePath[i]=files[i].getPath();
                        stationList[i]=simpleOudia.stationName;
                    }
                    ((MainActivity) context).getAOdia().database.addStation(stationList, filePath);
                    //データベースに駅名を登録する
                }catch (Exception e){
                    SDlog.log(new Exception("FileSelectorFromSystem onFocusChange:directory="+directory.getPath()));
                }
            }else{
                openDirectory(currentDirectoryPath);
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
                    openDirectory(currentDirectoryPath);
                    return false;
                }
                try {
                    final ListView fileListView = findViewById(R.id.fileList);
                    ArrayList<String> pathList = ((MainActivity) context).getAOdia().database.searchFileFromStation(s, currentDirectoryPath);
                    for (int i = 0; i < pathList.size(); i++) {
                        pathList.set(i,currentDirectoryPath + "/" + pathList.get(i));
                    }
                    final FileListAdapter adapter = new FileListAdapter(getContext(), pathList);

                    fileListView.setAdapter(adapter);

                    fileListView.setOnItemClickListener((parent, view, position, id) -> {
                        try {
                            openDirectory(adapter.getItem(position).getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
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
    @Override
    public void openDirectory(String directorypath){
        try {
            currentDirectoryPath=directorypath;
            File file=new File(directorypath);
            if (file.isDirectory()) {
                final ListView fileListView =  findViewById(R.id.fileList);
                final FileListAdapter adapter = new FileListAdapter(getContext(), file.getPath(),this);


                fileListView.setAdapter(adapter);
                ((TextView)findViewById(R.id.pathView)).setText(file.getPath());

                fileListView.setOnItemClickListener((parent, view, position, id) -> {
                    try {
                        openDirectory(adapter.getItem(position).getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (file.exists()) {
                if (file.getName().endsWith(".oud") || file.getName().endsWith(".oud2")) {
                    try {
                        ((MainActivity) getContext()).getAOdia().openFile(file);
//                        getAOdiaActivity().openFile(file);
                    } catch (Exception e) {
                        System.out.println("AOdia専用の処理です");
                        e.printStackTrace();
                    }
                    return;
                }
                if (file.getName().endsWith(".zip")) {

                    ((MainActivity)getContext()).getAOdia().openGTFSfile(file);
                    return;
                }

                Toast.makeText(getContext(), "この拡張子のファイルは開けません", Toast.LENGTH_SHORT).show();
            } else {
                new MakeNewDirectoryDialog(getContext(),currentDirectoryPath,this).show();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


}
