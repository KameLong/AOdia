package com.fc2.web.kamelong.aodia.GTMF;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fc2.web.kamelong.aodia.MainActivity;
import com.fc2.web.kamelong.aodia.R;
import com.fc2.web.kamelong.aodia.SdLog;

/**
 * Created by kame on 2017/05/09.
 */

public class GTMFListFragment extends Fragment {
    MainActivity activity;
    ListView stationList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            activity=(MainActivity) getActivity();

            return inflater.inflate(R.layout.gtmf_list, container, false);
        }catch(Exception e){
            SdLog.log(e);
        }
        return new View(activity);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        stationList=(ListView)activity.findViewById(R.id.stationList);
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>();
        stationList.setAdapter(arrayAdapter);

    }

}
