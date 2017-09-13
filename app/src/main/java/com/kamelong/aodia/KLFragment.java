package com.kamelong.aodia;

import android.app.Fragment;

import com.kamelong.aodia.diadata.AOdiaDiaFile;


/**
 * Created by kame on 2017/02/20.
 */

/**
 * KLFragmentを継承すると
 */
public abstract class KLFragment extends Fragment{
    public AOdiaDiaFile diaFile;
    public String fragmentName(){
        return "";
    }
}
