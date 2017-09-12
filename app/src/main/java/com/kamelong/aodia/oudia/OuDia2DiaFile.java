package com.kamelong.aodia.oudia;

import android.content.Context;

import java.io.File;

/**
 * OuDiaSecondのファイルを読み込むためのクラス
 * ほとんどOuDiaDiaFileと同じだが、OuDiaSecondがこれからも進歩し続けるようなので
 * 別クラスを作って特別対応する。
 * @since v1.2
 * @author kamelong
 */
public class OuDia2DiaFile extends OuDiaDiaFile {
    public OuDia2DiaFile(Context context, File file) {
        super(context, file);
    }
    public OuDia2DiaFile(Context context){
        super(context);
    }
    /**
     * OuDiaのJikokukeisikiの文字列から時刻表示形式を入力する。
     * @param value OuDiaファイル内のJikokukeisikiの文字列
     */
    @Override
    public void setStationTimeShow(Station station,String value){
        switch (value){
            case "Jikokukeisiki_KudariHatsuchaku":
                station.setTimeShow(7);
                break;
            case "Jikokukeisiki_NoboriHatsuchaku":
                station.setTimeShow(13);
                break;
            default:
            super.setStationTimeShow(station,value);
                break;
        }
    }

}
