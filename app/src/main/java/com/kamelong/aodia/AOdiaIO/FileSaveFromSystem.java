package com.kamelong.aodia.AOdiaIO;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.kamelong.aodia.AOdiaData.LineFile;
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
public class FileSaveFromSystem extends LinearLayout implements OpenDirectory{
    public String currentDirectoryPath="";
    public LineFile lineFile=null;

    public FileSaveFromSystem(Context context) {
        this(context,null);
    }

    /**
     * LineFileが設定されたら、そのLineFileのfilePathに保存できるよう、ディレクトリ移動する
     * @param lineFile 保存するLineFile
     */
    public void setLineFile(final LineFile lineFile){
        this.lineFile=lineFile;
        System.out.println("path:"+lineFile.filePath);
        ((EditText)findViewById(R.id.fileName)).setText(lineFile.filePath.substring(lineFile.filePath.lastIndexOf("/")+1,lineFile.filePath.lastIndexOf(".")));

        final RadioGroup saveStyle=findViewById(R.id.savestyle);
        if(lineFile.filePath.endsWith("oud2")){
            saveStyle.check(R.id.oud2);
        }else{
            saveStyle.check(R.id.oud);
        }

        openDirectory(lineFile.filePath.substring(0,lineFile.filePath.lastIndexOf("/")));
        findViewById(R.id.saveButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String savePath=currentDirectoryPath+"/"+ ((EditText)findViewById(R.id.fileName)).getText();

                if(saveStyle.getCheckedRadioButtonId()==R.id.oud2){
                    savePath+=".oud2";
                }else{
                    savePath+=".oud";
                }
                if(new File(savePath).isDirectory()){
                    SDlog.toast("このファイル名はディレクトリです。保存できません");
                    return;
                }
                final String savePath2 = savePath;
                if(new File(savePath).exists()){
                    new AlertDialog.Builder(getContext())
                            .setTitle("警告")
                            .setMessage("ファイルを上書きしますか？")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (saveStyle.getCheckedRadioButtonId()==R.id.oud2) {
                                            lineFile.saveToFile(savePath2);
                                        } else {
                                            lineFile.saveToOuDiaFile(savePath2);
                                        }

                                        SDlog.toast("ファイルを保存しました。");
                                        openDirectory(currentDirectoryPath);

                                    } catch (Exception e) {
                                        SDlog.log(e);
                                        SDlog.toast("ファイルの保存に失敗しました。");
                                    }
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
                    SDlog.toast("ファイルの保存に失敗しました。");
                }


            }
        });
    }
    public FileSaveFromSystem(Context context, AttributeSet attr){
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
        saveStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.oud:
                        ((TextView)findViewById(R.id.textView4)).setText(".oud");
                        break;
                    case R.id.oud2:
                        ((TextView)findViewById(R.id.textView4)).setText(".oud2");
                }
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
            currentDirectoryPath=directorypath;
            File file=new File(directorypath);
            if (file.isDirectory()) {
                final ListView fileListView =  findViewById(R.id.fileList);
                final FileListAdapter adapter = new FileListAdapter(getContext(), file.getPath(),this);


                fileListView.setAdapter(adapter);
                ((TextView)findViewById(R.id.pathView)).setText(file.getPath());

                fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            openDirectory(adapter.getItem(position).getPath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (file.exists()) {
                        ((EditText)findViewById(R.id.fileName)).setText(file.getName().substring(0,file.getName().lastIndexOf(".")));
            } else {
                Toast.makeText(getContext(),"このファイルは削除された可能性があります。", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "このフォルダにアクセスする権限がありません", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


}
