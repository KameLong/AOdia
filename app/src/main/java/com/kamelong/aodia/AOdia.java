package com.kamelong.aodia;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.kamelong.OuDia.LineFile;
import com.kamelong.OuDia.Station;
import com.kamelong.OuDia.Train;
import com.kamelong.OuDia.TrainType;
import com.kamelong.aodia.AOdiaIO.FileSaveFragment;
import com.kamelong.aodia.AOdiaIO.FileSelectorFragment;
import com.kamelong.aodia.DiagramFragment.DiagramFragment;
import com.kamelong.aodia.EditStation.EditStationFragment;
import com.kamelong.aodia.EditTrainType.EditTrainTypeFragment;
import com.kamelong.aodia.StationTimeTable.StationInfoIndexFragment;
import com.kamelong.aodia.StationTimeTable.StationTimeTableFragment;
import com.kamelong.aodia.TimeTable.TimeTableFragment;
import com.kamelong.aodia.detabase.AOdiaDetabase;
import com.kamelong.aodia.loadGTFS.SelectRouteFragment;
import com.kamelong.tool.SDlog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AOdia内のデータやり取りを行うためのクラスです。
 */
public class AOdia {
    public AOdiaDetabase database;

    public static final String FILE_INDEX="fileIndex";
    public static final String DIA_INDEX="diaIndex";
    public static final String DIRECTION = "direction";
    public static final String TRAIN_INDEX="trainIndex";
    public static final String STATION_INDEX = "stationIndex";

    public static final String FILE_NAME="fileName";

    private MainActivity activity=null;
    /**
     * 開いているOuDiaファイル一覧です。
     */
    private ArrayList<LineFile> lineFiles=new ArrayList<>();

    /**
     * lineFileをメニュー内に表示する順番を記す
     */
    private ArrayList<LineFile>lineFilesIndex=new ArrayList<>();
    /**
     * lineFileの内容を表示するかどうか
     */
    public Map<LineFile,Boolean> lineFileExpand=new HashMap<>();



    /**
     * 開いているFragmentのリストです。
     * index=0は常にHelpFragmentとなります。
     */
    private ArrayList<AOdiaFragment> fragmentList=new ArrayList<>();
    /**
     * fragmentListのうち現在表示しているものです。
     */

    public ArrayList<Train>copyTrain=new ArrayList<>();
    public ArrayList<TrainType> copyTrainType = new ArrayList<>();
    public ArrayList<Station> copyStation = new ArrayList<>();

    public int fragmentIndex=0;

    public AOdia(MainActivity activity){
        this.activity=activity;
        database = new AOdiaDetabase(activity);
    }

    private void openFragment(AOdiaFragment fragment) {
        if (!fragmentList.contains(fragment)) {
            fragmentList.add(fragment);
        }
        fragmentIndex = fragmentList.indexOf(fragment);
        activity.setVisibleProceed(false);
        activity.openFragment(fragment);
    }


    public void makeNewLineFile(){
        File sample=new File(activity.getFilesDir(), "new2.oud");
        if (!sample.exists()) {
            try {
                InputStream input=activity.getAssets().open("new2.oud");
                OutputStream output=new FileOutputStream(sample);
                byte[] data=new byte[input.available()];
                input.read(data);
                output.write(data);
                input.close();
                output.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        openFile(sample, activity.getExternalFilesDir(null) + "/new.oud2");

    }

    /**
     * ファイル選択画面を開きます
     */
    public void openFileSelect(){
        AOdiaFragment fragment=new FileSelectorFragment();
        openFragment(fragment);

    }
    public void openSaveFragment(LineFile lineFile){
        FileSaveFragment fragment=new FileSaveFragment();
        Bundle args=new Bundle();
        args.putInt(FILE_INDEX,lineFiles.indexOf(lineFile));
        fragment.setArguments(args);
        openFragment(fragment);

    }
    public void openFile(File file) {
        openFile(file,null);
    }

    /**
     * ファイルを開く
     */
    public void openFile(File file,String path) {
        if (file.isFile()) {
            if (file.getName().contains(".")) {
                switch (file.getName().substring(file.getName().lastIndexOf(".") + 1)) {
                    case "oud":
                        openOuDiaFile(file,path);
                        return;
                    case "oud2":
                        openOuDiaFile(file,path);
                        return;
                    default:
                        SDlog.log("この形式(" + file.getName().substring(file.getName().lastIndexOf(".")) + ")のファイルは読めません");
                        return;
                }
            }
        }
        SDlog.log("この形式のファイルは読めません");
    }

    /**
     * GTFS形式のファイルを開く
     */
    public void openGTFSfile(File file){

        if (!file.isFile()) {
            return;
        }
        SelectRouteFragment fragment=new SelectRouteFragment();
        Bundle args=new Bundle();
        args.putString(FILE_NAME, file.getPath());
        fragment.setArguments(args);
        openFragment(fragment);

    }
    public void addLineFile(LineFile lineFile){
        lineFiles.add(lineFile);
        lineFilesIndex.add(0,lineFile);
        lineFileExpand.put(lineFile,true);
        openTimeTable(lineFile, 0, 0);
    }

    /**
     * OuDia形式のファイルを開く
     */
    private void openOuDiaFile(File file,String path){
        try {
            LineFile lineFile = new LineFile(file);
            if(path!=null){
                lineFile.filePath=path;

            }
            lineFiles.add(lineFile);
            lineFilesIndex.add(0,lineFile);
            lineFileExpand.put(lineFile,true);
            openTimeTable(lineFile, 0, 0);
        }catch (Exception e){
            SDlog.toast("ファイルを開く際に問題が発生しました。開発者までご連絡ください。\n"+e.toString());
            SDlog.log(e);
        }
    }

    /**
     * 時刻表を開く
     */
    public void openTimeTable(LineFile lineFile, int diaIndex, int direction){
        TimeTableFragment fragment=new TimeTableFragment();
        Bundle args=new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        args.putInt(DIA_INDEX, diaIndex);
        args.putInt(DIRECTION, direction);
        fragment.setArguments(args);
        openFragment(fragment);
    }

    public void openTimeTable(LineFile lineFile, int diaIndex, int direction, int trainIndex) {
        TimeTableFragment fragment = new TimeTableFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        args.putInt(DIA_INDEX, diaIndex);
        args.putInt(DIRECTION, direction);
        args.putInt(TRAIN_INDEX, trainIndex);
        fragment.setArguments(args);
        openFragment(fragment);
    }

    /**
     * ダイヤを開く
     */
    public void openDiagram(LineFile lineFile, int diaIndex){
        DiagramFragment fragment=new DiagramFragment();
        Bundle args=new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        args.putInt(DIA_INDEX, diaIndex);
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openEditStation(LineFile lineFile) {
        EditStationFragment fragment = new EditStationFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        fragment.setArguments(args);
        openFragment(fragment);
    }

    /**
     * 列車種別編集画面
     *
     * @param lineFile
     */
    public void openEditTrainType(LineFile lineFile) {
        EditTrainTypeFragment fragment = new EditTrainTypeFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openStationTimeTableIndex(LineFile lineFile){
        StationInfoIndexFragment fragment = new StationInfoIndexFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        fragment.setArguments(args);
        openFragment(fragment);
    }

    /**
     * 駅時刻表を表示する
     */
    public void openStationTimeTable(LineFile lineFile, int diaIndex, int direction, int stationIndex) {
        StationTimeTableFragment fragment = new StationTimeTableFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        args.putInt(DIA_INDEX, diaIndex);
        args.putInt(DIRECTION, direction);
        args.putInt(STATION_INDEX, stationIndex);
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openComment(LineFile lineFile){
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putInt(FILE_INDEX, lineFiles.indexOf(lineFile));
        fragment.setArguments(args);
        openFragment(fragment);
    }
    public void openHelp(){
        HelpFragment fragment=new HelpFragment();
        openFragment(fragment);
    }

    public void openSetting() {
        SettingFragment fragment = new SettingFragment();
        openFragment(fragment);
    }



    public LineFile getLineFile(int index){
        try {
            return lineFiles.get(index);
        }catch (Exception e){
            return null;
        }
    }
    public ArrayList<LineFile>getLineFileList(){
        return lineFilesIndex;
    }
    public void upDiaFile(LineFile lineFile){
        int index=lineFilesIndex.indexOf(lineFile);
        if(index>0){
            lineFilesIndex.remove(index);
            lineFilesIndex.add(index-1,lineFile);
        }
        activity.openMenu();
    }

    /**
     * 指定LineFileを閉じる
     * 閉じる前にlineFileを使っているFragmentを消す
     * @param lineFile
     */
    public void killLineFile(LineFile lineFile){
        killFragment(lineFile);
        lineFileExpand.remove(lineFile);
        lineFilesIndex.remove(lineFile);
        lineFiles.remove(lineFile);

    }
    /**
     * 該当lineFileを使っているFragmentを消す
     */
    public void killFragment(LineFile lineFile){
        for(int i=0;i<fragmentList.size();i++){
            if(fragmentList.get(i).getLineFile()==lineFile){
                activity.killFragment(fragmentList.get(i));
                fragmentList.remove(i);

                if(fragmentIndex>=i){
                    fragmentIndex--;
                }
                i--;
            }
        }
        if(fragmentIndex<0||fragmentIndex>=fragmentList.size()){
            fragmentIndex=0;
        }
        if(fragmentList.size()==0){
            openHelp();
        }else {
//            activity.openFragment(fragmentList.get(fragmentIndex));
        }
    }
    /**
     * 該当Fragmentを消す
     */
    public void killFragment(AOdiaFragment fragment){
        int i=fragmentList.indexOf(fragment);
        activity.killFragment(fragment);
        if(i>=0) {
            fragmentList.remove(fragment);
            if (fragmentIndex >= i) {
                fragmentIndex--;
            }
        }
        activity.openFragment(fragmentList.get(fragmentIndex));
    }

    public void proceedFragment(){
        fragmentIndex++;
        if(fragmentIndex>=fragmentList.size()){
            fragmentIndex--;
        }
        activity.setVisibleProceed(fragmentIndex!=fragmentList.size()-1);
        activity.setVisibleBack(fragmentIndex!=0);

        activity.openFragment(fragmentList.get(fragmentIndex));

    }
    public void backFragment(){
        fragmentIndex--;
        if(fragmentIndex<0){
            fragmentIndex++;
        }
        activity.setVisibleProceed(fragmentIndex!=fragmentList.size()-1);
        activity.setVisibleBack(fragmentIndex!=0);
        activity.openFragment(fragmentList.get(fragmentIndex));
    }
    public void killFragment(){
        fragmentList.remove(fragmentIndex);
        fragmentIndex--;
        if(fragmentIndex<0){
            fragmentIndex++;
        }
        if(fragmentList.size()==0){
            openHelp();
        }else{
            activity.openFragment(fragmentList.get(fragmentIndex));
        }
    }

    public void loadData(){

        SharedPreferences preferences=activity.getSharedPreferences("files", Context.MODE_PRIVATE);
        String filePath=preferences.getString("tempFilePath","");
        if(filePath.length()>0) {
            try {
                LineFile file = new LineFile(new File(activity.getFilesDir() + "/temp.oud2"));
                file.filePath = filePath;
                lineFiles.add(file);
                lineFilesIndex.add(file);
                lineFileExpand.put(file, true);
                openTimeTable(file, 0, 0);
            }catch (FileNotFoundException e){
                SDlog.toast("一時保存ファイルが見つかりませんでした");

            }catch ( Exception e) {
                SDlog.log(e);
                SDlog.toast("初期ファイルを開けませんでした");
            }
        }
    }
    public void openPayFragment(){
        PayFragment fragment = new PayFragment();
        openFragment(fragment);
    }

    public void saveData(){

        SharedPreferences preferences=activity.getSharedPreferences("files", Context.MODE_PRIVATE);
        if(lineFilesIndex.size()==0) {
            preferences.edit().putString("tempFilePath","").apply();
            return;
        }
        LineFile file=lineFilesIndex.get(0);
        String fileName=activity.getFilesDir() + "/temp2.oud2";

        try {
            file.saveToFile(fileName);
            System.out.println(file.filePath);
            preferences.edit().putString("tempFilePath",file.filePath).apply();
            File oldFile=new File(activity.getFilesDir() + "/temp.oud2");
            File newFile=new File(activity.getFilesDir() + "/temp2.oud2");
            oldFile.delete();
            newFile.renameTo(oldFile);

        }catch (Exception e){
            e.printStackTrace();
            SDlog.toast("バックアップファイルを保存できませんでした");
        }

    }




}

