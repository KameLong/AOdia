package com.kamelong.aodia;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kamelong.aodia.diadata.AOdiaDiaFile;

import static android.content.Context.MODE_PRIVATE;

/**
 * AOdia内のタブとして使われるFragment
 * 画面分割を可能とする
 */

public abstract class AOdiaFragment extends Fragment implements AOdiaFragmentInterface{

    protected View fragmentContainer=null;
    public AOdiaDiaFile diaFile=null;
    public AOdiaActivity activity=null;


        @Override
    public void onStart(){
        super.onStart();
        ((TextView)getActivity().findViewById(R.id.titleView)).setText(fragmentName());


    }
    @Override
    public void onStop(){

        super.onStop();
    }

    protected View findViewById(int id){
        return fragmentContainer.findViewById(id);
    }
    @Override
    public String fragmentName() {
        return "";
    }

    @Override
    public String fragmentHash() {
        return "";
    }
    @Override
    public Fragment getFragment(){
        return this;
    }
    @Override
    public AOdiaDiaFile getDiaFile(){
        return diaFile;
    }
    public AOdiaActivity getAOdiaActivity(){
        return (AOdiaActivity) getActivity();
    }


}
