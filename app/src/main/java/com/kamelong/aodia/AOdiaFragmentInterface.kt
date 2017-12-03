package com.kamelong.aodia

import android.app.Activity
import android.app.Fragment

import com.kamelong.aodia.diadata.AOdiaDiaFile

/**
 * Created by kame on 2017/09/28.
 */

internal interface AOdiaFragmentInterface {

    /**
     * このinterfaceが実装されているFragment
     * @return
     */
    var fragment: Fragment

    /**
     * このinterfaceに所属するDiaFile
     * @return
     */
    var diaFile: AOdiaDiaFile

    /**
     * このinterfaceが所属するactivity
     */
    var activity:AOdiaActivity

    /**
     * この画面のタイトルを出力する
     * @return
     */
    fun fragmentName(): String{
        return ""
    }

    /**
     * この画面を生成するための文字列を出力する
     * @return
     */
    fun fragmentHash(): String{
        return ""
    }
}
