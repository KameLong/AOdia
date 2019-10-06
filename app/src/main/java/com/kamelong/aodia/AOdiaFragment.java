package com.kamelong.aodia;

import androidx.annotation.NonNull;

import com.kamelong.OuDia.LineFile;

public interface AOdiaFragment{
    @NonNull
    String getName();

    /**
     * このFragmentで使われているLineFileを返します。
     * null許容（使用しているLineFileが存在しない場合)
     */
    LineFile getLineFile();
}
