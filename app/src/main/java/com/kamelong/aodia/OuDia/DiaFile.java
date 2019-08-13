package com.kamelong.aodia.OuDia;

import com.kamelong.OuDiaEdit.DiaFileEdit;

import java.io.File;

public class DiaFile extends DiaFileEdit {
    //AOdia専用オプション
    /**
     * ファイル保存パス
     */
    public String filePath="";
    /**
     * このファイルのメニューを開いているか？
     */
    public boolean menuOpen=true;

    public DiaFile(File file)throws Exception{
        super(file);
        filePath=file.getPath();
    }

}
