package com.kamelong.aodia.AOdiaIO;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kamelong.OuDia.SimpleOuDia;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
/*
 * Copyright (c) 2019 KameLong
 * contact:kamelong.com
 *
 * This source code is released under GNU GPL ver3.
 */

/**
 * ファイル一覧を表示するときのアダプター
 * position=0の部分は親フォルダーへの遷移を担うので別処理にすること
 * 参考URL:http://android.keicode.com/basics/ui-listview.php
 */
public class FileListAdapter extends BaseAdapter {
    ArrayList<File> fileList = new ArrayList<>();
    LayoutInflater layoutInflater = null;
    MainActivity activity = null;
    OpenDirectory selector;
    String directoryPath;


    public FileListAdapter(Context context,final String directoryPath,final OpenDirectory selector){
        this.selector=selector;
        this.directoryPath=directoryPath;
        this.activity = (MainActivity) context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File directory = new File(directoryPath);
        try {
            File[] files = directory.listFiles();
            Comparator<File> comparator = new Comparator<File>() {
                @Override
                //ファイルの比較方法　ソートに使う
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
            e.printStackTrace();
            throw e;

        }
        //親フォルダに移動する項目を追加
        fileList.add(0, new File(new File(directoryPath).getParent()));
    }
    public FileListAdapter(Context context, ArrayList<String> filePathList){
        this.activity = (MainActivity) context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fileList=new ArrayList<>();
        for(int i=0;i<filePathList.size();i++){
            fileList.add(new File(filePathList.get(i)));
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
        convertView = layoutInflater.inflate(R.layout.fileselector_file_list, parent, false);

        ((TextView) convertView.findViewById(R.id.fileName)).setText(fileList.get(position).getName());
        ((TextView) convertView.findViewById(R.id.stationName)).setText(stationName(fileList.get(position)));
        if (fileList.get(position).getName().endsWith(".oud")|| fileList.get(position).getName().endsWith(".oud2")){
            ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
            fileIcon.setImageResource(R.drawable.fileselector_dia_icon);

            //ファイル削除操作
            ImageView deleteButton=convertView.findViewById(R.id.deleteDiagram);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(fileList.get(position).getPath());
                    new AlertDialog.Builder(activity)
                            .setTitle("ファイル削除")
                            .setMessage(fileList.get(position).getName()+"のダイヤデータを削除します")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!fileList.get(position).delete()){
                                        Toast.makeText(activity,"ファイルを削除できませんでした",Toast.LENGTH_LONG).show();
                                    }
                                    selector.openDirectory(directoryPath);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });
        }else if (position == 0) {
            ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
            fileIcon.setImageResource(R.drawable.back_to_up);
            ((TextView) convertView.findViewById(R.id.fileName)).setText(activity.getString(R.string.UpperFolder));
        } else if (fileList.get(position).isDirectory()) {
            ImageView fileIcon = convertView.findViewById(R.id.fileIcon);
            fileIcon.setImageResource(R.drawable.fileselector_folder_icon);
        }


        return convertView;
    }

    private String stationName(File file) {
        if(!file.isFile())return "";
        if(file.getName().endsWith("oud")||file.getName().endsWith("oud2")){

            try {
                SimpleOuDia diaFile = new SimpleOuDia(file);
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
