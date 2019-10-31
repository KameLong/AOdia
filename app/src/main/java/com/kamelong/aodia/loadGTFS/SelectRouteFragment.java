package com.kamelong.aodia.loadGTFS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamelong.GTFS.GTFS;
import com.kamelong.GTFS.Route;
import com.kamelong.GTFS2Oudia.GTFS2OuDia;
import com.kamelong.OuDia.LineFile;
import com.kamelong.aodia.AOdia;
import com.kamelong.aodia.AOdiaFragmentCustom;
import com.kamelong.aodia.MainActivity;
import com.kamelong.aodia.R;
import com.kamelong.tool.SDlog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SelectRouteFragment extends AOdiaFragmentCustom {

    String zipFileName=null;
    String tempPath=null;
    GTFS gtfs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            Bundle bundle = getArguments();
            zipFileName= bundle.getString(AOdia.FILE_NAME, "");
            return inflater.inflate(R.layout.open_gtfs, container, false);
        } catch (Exception e) {
            SDlog.log(e);
        }
        return new View(getMainActivity());
    }

    /**
     * ここではtabHostの初期化を行う
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if(!zipFileName.endsWith(".zip")){
                SDlog.toast("このファイルはzipファイルではありません");
                return;
            }
            tempPath=getContext().getCacheDir()+"/GTFS";
            final File directory=new File(tempPath);
            if(!directory.isDirectory()){
                if(directory.exists()){
                    directory.delete();
                }
                directory.mkdir();
            }
            for(File childFile:directory.listFiles()){
                childFile.delete();
            }

            ZipInputStream in = new ZipInputStream(new BufferedInputStream(
                    new FileInputStream(zipFileName)));

            ZipEntry zipEntry;
            //Zip形式圧縮データ解凍
            while((zipEntry = in.getNextEntry()) != null) {

                try {
                    String fileName;

                    fileName = zipEntry.getName();
                    fileName = fileName.split("/")[fileName.split("/").length - 1];
                    System.out.println("aodia:" + fileName);

                    // 解凍したファイルをアプリのファイル領域に書き込む
                    BufferedOutputStream out =
                            new BufferedOutputStream(new FileOutputStream(new File(directory + "/" + fileName)));
                    // エントリの内容を出力
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }

                    in.closeEntry();
                    out.close();
                }catch (Exception e){
                    SDlog.log(e);
                }
            }

            in.close();

        } catch (Exception e) {
            SDlog.log(e);
            SDlog.toast("zipファイルを解凍中に問題が発生しました");
            return;
        }
        try{
            gtfs=new GTFS(tempPath);
        }catch (Exception e){
            SDlog.log(e);
            SDlog.toast("GTFSファイル読み込み中にエラーが発生しました");
            return;
        }
        try{
            ListView routeList=getMainActivity().findViewById(R.id.routeList);
            final RouteViewAdaper adapter = new RouteViewAdaper((MainActivity)getContext(), gtfs);


            routeList.setAdapter(adapter);

            routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
//                        openDirectory(adapter.getItem(position).getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            FloatingActionButton openAsOuDia=getMainActivity().findViewById(R.id.openAsOuDia);
            openAsOuDia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file=new File(getMainActivity().getFilesDir()+"/fromGTFS");
                    if(file.isFile()){
                        SDlog.log(getMainActivity().getFilesDir()+"/fromGTFS"+"にファイルが既に存在するため、GTFSからoudiaに変換できません");
                        return;
                    }
                    if(!file.isDirectory()){
                        file.mkdir();
                    }
                    String lineName="";
                    if(adapter.downRoutes.size()>0){
                        lineName=adapter.downRoutes.get(0).route_name;
                    }else if(adapter.upRoutes.size()>0){
                        lineName=adapter.upRoutes.get(0).route_name;
                    }else{
                        SDlog.toast("路線を選択してください");
                        return;
                    }
                    ArrayList<String> downList=new ArrayList<>();
                    ArrayList<String> upList=new ArrayList<>();
                    for(Route route:adapter.downRoutes){
                        downList.add(route.route_id);
                    }
                    for(Route route:adapter.upRoutes){
                        upList.add(route.route_id);
                    }

                    GTFS2OuDia converter=new GTFS2OuDia(gtfs,file.getPath(),lineName);
                    converter.setRouteID(new ArrayList[]{downList,upList});
                    LineFile lineFile=converter.makeOudiaFile();
                    getAOdia().addLineFile(lineFile);
                }
            });


        }catch (Exception e){
            SDlog.log(e);
            return;
        }


    }

    @NonNull
    @Override
    public String getName() {
        return "";
    }

    @Override
    public LineFile getLineFile() {
        return null;
    }
}
