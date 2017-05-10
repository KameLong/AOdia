package com.fc2.web.kamelong.aodia.GTFS;

import com.fc2.web.kamelong.aodia.SdLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * GTFS形式のcsvファイルを読み込むときのコンテナ
 * GTFS形式のファイルは最初の行に各列に関するヘッダがある
 * ヘッダから情報を抜き出せるようにしたい
 */
public class GtfsCsvContainer {
    ArrayList<String[]> data=new ArrayList<>();
    String[] headerStr;
    public GtfsCsvContainer(File inputFile){
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            headerStr = br.readLine().split(",",-1);
            String str = br.readLine();
            while(str!=null){
                data.add(str.split(",",-1));
                str=br.readLine();
            }
        }catch(Exception e){
            SdLog.log(e);
        }
    }

    /**
     * 入力のヘッダーはこのファイルでは何番目の列に対応するかを返す。
     * もし、指定のヘッダーが存在しなければ、-1を返す。
     * @param header
     * @return
     */
    public int headerIndex(String header){
        return Arrays.asList(headerStr).indexOf(header);
    }
    /**
     * データのサイズ（行数）を返す
     */
    public int indexNum(){
        return data.size();
    }

    /**
     * ヘッダーとインデックスからデータを取得する
     * @param header
     * @param index
     * @return
     */
    public String fileData(String header,int index){
        try {
            if (index < 0 || index >= indexNum()) {
                return null;
            }
            return data.get(index)[headerIndex(header)];
        }catch (Exception e){
            return null;
        }
    }


}
