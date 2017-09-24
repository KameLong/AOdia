package com.kamelong.aodia;

import android.app.Fragment;
import android.view.View;

/**
 * AOdia内のタブとして使われるFragment
 * 画面分割を可能とする
 */

public abstract class TabFragment extends Fragment{

    protected View fragmentContainer=null;

    protected View findViewById(int id){
        return fragmentContainer.findViewById(id);
    }
}
