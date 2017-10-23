package com.kamelong.aodia;

import android.app.Fragment;

import com.kamelong.aodia.diadata.AOdiaDiaFile;

/**
 * Created by kame on 2017/09/28.
 */

public interface AOdiaFragmentInterface {
    /**
     * この画面のタイトルを出力する
     * @return
     */
    public String fragmentName();

    /**
     * この画面を生成するための文字列を出力する
     * @return
     */
    public String fragmentHash();

    /**
     * このinterfaceが実装されているFragmentを返す
     * @return
     */
    public Fragment getFragment();

    /**
     * このinterfaceに所属するDiaFileを返す
     * もしDiaFileに依存しないFragment(設定など)ならnullを返す
     * @return
     */
    public AOdiaDiaFile getDiaFile();
}
