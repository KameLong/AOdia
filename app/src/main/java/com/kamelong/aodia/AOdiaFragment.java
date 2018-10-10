package com.kamelong.aodia;

import android.support.v4.app.Fragment;
import android.view.View;

import com.kamelong.OuDia.DiaFile;

public class AOdiaFragment extends Fragment {
    protected View fragmentContainer= null;

    public DiaFile diaFile=null;
    public  View findViewById(int id){
        return fragmentContainer.findViewById(id);
    }
    public AOdiaActivity getAOdiaActivity(){
        return (AOdiaActivity)getActivity();
    }
    public String fragmentName(){
        return "";
    }
    public String fragmentHash() {
        return "help";
    }


}
