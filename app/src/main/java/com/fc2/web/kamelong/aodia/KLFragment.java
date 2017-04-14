package com.fc2.web.kamelong.aodia;

import android.app.Fragment;

import com.fc2.web.kamelong.aodia.oudia.DiaFile;

/**
 * Created by kame on 2017/02/20.
 */

/**
 * KLFragmentを継承すると
 */
public abstract class KLFragment extends Fragment{
    public DiaFile diaFile;
    public String fragmentName(){
        return "";
    }
}
