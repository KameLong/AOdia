package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.kamelong.OuDia.LineFile;
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
 * 端末内にファイルを保存するためのView
 */
public class FileSaveToSystem extends LinearLayout implements OpenDirectory{
    public String currentDirectoryPath="";
    public LineFile lineFile=null;

    public FileSaveToSystem(Context context) {
        this(context,null);
    }

    /**
     * LineFileが設定されたら、そのLineFileのfilePathに保存できるよう、ディレクトリ移動する
     * @param lineFile 保存するLineFile
     */
    public void setLineFile(final LineFile lineFile){
        if(lineFile==null){
            SDlog.log(new Exception("FileSaveFromSystem.setLineFile(null)"));
            SDlog.toast("エラーの為このファイルは保存できません");
            return;
        }
        this.lineFile=lineFile;
        System.out.println("path:"+lineFile.filePath);
        try {
            if(lineFile.filePath.length()!=0) {
                ((EditText) findViewById(R.id.fileName)).setText(lineFile.filePath.substring(lineFile.filePath.lastIndexOf("/") + 1, lineFile.filePath.lastIndexOf(".")));
                openDirectory(lineFile.filePath.substring(0, lineFile.filePath.lastIndexOf("/")));
            }else{
                ((EditText) findViewById(R.id.fileName)).setText(lineFile.name);
            }
        }catch (Exception e){
            SDlog.log(e);
        }

        final RadioGroup saveStyle=findViewById(R.id.savestyle);
            saveStyle.check(R.id.oud2);
            saveStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(i!=R.id.oud2){
                        new AlertDialog.Builder(getContext())
                                .setTitle("警告")
                                .setMessage("oud形式で保存すると、路線外発着情報など、一部の情報が失われることがあります")
                                .setPositiveButton("OK", (dialog, which) -> {
                                })
                                .show();
                    }
                }
            });


        findViewById(R.id.saveButton).setOnClickListener(v -> {
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("/")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("\\")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains(":")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("*")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("?")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("!")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。(\\/:*?!<>|)");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("<")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains(">")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。");
                return;
            }
            if(((EditText)findViewById(R.id.fileName)).getText().toString().contains("|")){
                SDlog.toast("ファイル名に使用できない文字が含まれています。");
                return;
            }
            String savePath=currentDirectoryPath+"/"+ ((EditText)findViewById(R.id.fileName)).getText();

            if(saveStyle.getCheckedRadioButtonId()==R.id.oud2){
                savePath+=".oud2";
            }else{
                savePath+=".oud";
            }
            if(new File(savePath).isDirectory()){
                SDlog.toast("このファイル名はディレクトリで既に存在しています。ここに保存できません");
                return;
            }
            final String savePath2 = savePath;
            if(new File(savePath).exists()){
                new AlertDialog.Builder(getContext())
                        .setTitle("警告")
                        .setMessage("ファイルを上書きしますか？")
                        .setPositiveButton("OK", (dialog, which) -> {
                            try {
                                if (saveStyle.getCheckedRadioButtonId() == R.id.oud2) {
                                    lineFile.saveToFile(savePath2);
                                } else {
                                    lineFile.saveToOuDiaFile(savePath2);
                                }

                                SDlog.toast("ファイルを保存しました。");

                            } catch (Exception e) {
                                SDlog.log(e);
                                SDlog.toast("ファイルの保存に失敗しました。");
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return;
            }
            try {
                if (saveStyle.getCheckedRadioButtonId()==R.id.oud2) {
                    lineFile.saveToFile(savePath);
                } else {
                    lineFile.saveToOuDiaFile(savePath);
                }

                SDlog.toast("ファイルを保存しました。");
                openDirectory(currentDirectoryPath);

            }catch (Exception e){
                SDlog.log(e);
                SDlog.toast(savePath+"にファイルを保存する事ができませんでした。このフォルダに対するアクセス権限がない可能性があります。");
            }


        });
    }
    public FileSaveToSystem(Context context, AttributeSet attr){
        super(context,attr);
        LayoutInflater.from(context).inflate(R.layout.filesave_terminal, this);
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
        final RadioGroup saveStyle=findViewById(R.id.savestyle);
        saveStyle.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.oud:
                    ((TextView)findViewById(R.id.textView4)).setText(".oud");
                    break;
                case R.id.oud2:
                    ((TextView)findViewById(R.id.textView4)).setText(".oud2");
            }
        });


    }
    /**
     * フォルダ内内ファイルリストを作成する
     * このメソッドを呼び出すとtab内のListViewを更新する。
     * @param directorypath 表示したいディレクトリ
     */
    public void openDirectory(String directorypath){
        try {
            File file=new File(directorypath);
            if (file.isDirectory()) {
                currentDirectoryPath=directorypath;
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
                        ((EditText)findViewById(R.id.fileName)).setText(file.getName().substring(0,file.getName().lastIndexOf(".")));
            } else {
                new MakeNewDirectoryDialog(getContext(),currentDirectoryPath,this).show();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


}
