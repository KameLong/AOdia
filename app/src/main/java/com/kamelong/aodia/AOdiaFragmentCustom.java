package com.kamelong.aodia;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public abstract class AOdiaFragmentCustom extends Fragment implements AOdiaFragment {
    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected AOdia getAOdia() {
        return getMainActivity().getAOdia();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            ((TextView) getActivity().findViewById(R.id.titleView)).setText(getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private String getStringR(int resourceID){
        return getMainActivity().getString(resourceID);
    }
}
